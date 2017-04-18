package com.simplestocks.trade.analytics;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Triple;

import com.simplestocks.trade.Trade;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class TradeAnalytics {

	private Observable<Trade> tradeStream;
	private PublishSubject<StockPriceChangeEvent> stockChangeFeed;

	public TradeAnalytics(Observable<Trade> tradeStream) {
		this.tradeStream = tradeStream;
		this.stockChangeFeed = PublishSubject.create();
	}

	
	public void processTradeEventWindow(Observable<Trade> tradeFeed,
			PublishSubject<StockPriceChangeEvent> publishFeed) {
		tradeFeed.groupBy(t -> t.getStockSymbol()).subscribe(
				o -> o.map(t -> new TradeCollector(t.getStockSymbol(), t.getQuantity(), t.getTotalTradePrice()))
						.reduce(new TradeCollector(),
								(o1, o2) -> new TradeCollector(o2.getSymbol(), o1.getTotalQuantity() + o2.getTotalQuantity(),
										o1.getTotalPrice() + o2.getTotalPrice()))
						.subscribe(r -> publishFeed.onNext(new StockPriceChangeEvent(r.getSymbol(), r.getTotalPrice() / r.getTotalQuantity()))));
	}

	public void useSlidingTimeWindow(Long window, Long interval, TimeUnit unit) {
		tradeStream.window(window, interval, unit).subscribe(obs -> processTradeEventWindow(obs, stockChangeFeed));
	}
	
	public void useFixedSizeWindow(Integer windowSize){
		tradeStream.window(windowSize).subscribe(obs -> processTradeEventWindow(obs, stockChangeFeed));
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
