package com.simplestocks.trade.analytics;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplestocks.trade.Trade;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class TradeAnalytics {

	private Observable<Trade> tradeStream;
	private PublishSubject<StockPriceChangeEvent> stockChangeFeed;
	private Scheduler scheduler;
	private TimeUnit units;
	private int slide;
	private int window;
	private static final Logger LOG = LoggerFactory.getLogger(TradeAnalytics.class);

	public TradeAnalytics(Observable<Trade> tradeStream, int window, int slide, TimeUnit units) {
		this(tradeStream, window, slide, units, Schedulers.computation());
	}

	public TradeAnalytics(Observable<Trade> tradeStream, int window, int slide, TimeUnit units, Scheduler scheduler) {
		this.tradeStream = tradeStream;
		this.stockChangeFeed = PublishSubject.create();
		this.window = window;
		this.slide = slide;
		this.units = units;
		this.scheduler = scheduler;
	}

	public void processTradeEventWindow(Observable<Trade> tradeFeed) {
		tradeFeed.groupBy(t -> t.getStockSymbol()).subscribe(
				o -> o.map(t -> new TradeCollector(t.getStockSymbol(), t.getQuantity(), t.getTotalTradePrice()))
						.reduce(new TradeCollector(), (o1, o2) -> new TradeCollector(o2.getSymbol(),
								o1.getTotalQuantity() + o2.getTotalQuantity(), o1.getTotalPrice() + o2.getTotalPrice()))
						.subscribe(r -> stockChangeFeed.onNext(
								new StockPriceChangeEvent(r.getSymbol(), r.getTotalPrice() / r.getTotalQuantity()))));
	}

	public void connect() {
		tradeStream.window(window, slide, units, scheduler).subscribe(obs -> processTradeEventWindow(obs));
	}

	public Observable<StockPriceChangeEvent> getStockFeed() {
		return stockChangeFeed;
	}

	static class TradeCollector {
		private String symbol;
		private Integer quantity = 0;
		private Double price = 0D;

		public TradeCollector() {
		}

		public TradeCollector(String symbol, Integer totalQuantity, Double totalPrice) {
			super();
			this.symbol = symbol;
			this.quantity = totalQuantity;
			this.price = totalPrice;
		}

		public String getSymbol() {
			return symbol;
		}

		public Integer getTotalQuantity() {
			return quantity;
		}

		public Double getTotalPrice() {
			return price;
		}

	}
}
