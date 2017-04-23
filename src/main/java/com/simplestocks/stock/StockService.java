package com.simplestocks.stock;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Service responsible for maintenance of the set
 * of stocks trading on the market. 
 * 
 * Provides methods to get a stock, to update its last
 * dividend and lists to a feed of changes to stock prices.
 * 
 * This class is also responsible for calculation of the market
 * GBCE index as geometric mean of all trading stock prices.
 * 
 * This class provides two feeds to which interested parties may
 * subscribe. The stock feed represents changes to any stock traded
 * on the market. The market feed represents changes to the overall
 * GBCE market index.
 * 
 *
 */
public class StockService {

	private Map<String, Stock> stocks = Collections.synchronizedMap(new HashMap<String, Stock>());
	private PublishSubject<Stock> stockFeed;
	private PublishSubject<Double> marketFeed;

	private volatile Double gbceMarketIndex;
	private static final Logger LOG = LoggerFactory.getLogger(StockService.class);

	public StockService() {
		stockFeed = PublishSubject.create();
		marketFeed = PublishSubject.create();
	}

	public Observable<Stock> getStockFeed() {
		return stockFeed.observeOn(Schedulers.computation());
	}

	public Observable<Double> getMarketFeed() {
		return marketFeed.observeOn(Schedulers.computation());
	}

	public void registerCommonStock(String symbol, Double lastDividend) {
		Stock stock = new CommonStock(symbol, lastDividend);
		stocks.put(stock.getSymbol(), stock);
	}

	public void registerPreferredStock(String symbol, Double parValue, Double fixedDividend) {
		LOG.info("registerPreferredStock " + symbol + " parValue " + fixedDividend);
		Stock stock = new PreferredStock(symbol, parValue, fixedDividend);
		stocks.put(stock.getSymbol(), stock);
	}

	public Set<String> getStockSymbols() {
		return new HashSet<String>(stocks.keySet());
	}

	public Stock getStock(String symbol) {
		LOG.info("getStock " + symbol);
		return stocks.get(symbol);
	}

	public void updateStockPrice(String symbol, Double price) {
		LOG.info("updateStockPrice " + symbol + " " + price);
		if (!stocks.containsKey(symbol)) {
			throw new IllegalArgumentException("Invalid stock symbol");
		}

		Stock stock = stocks.get(symbol);
		Stock update = stock.cloneWithNewPrice(price);
		stocks.replace(update.getSymbol(), update);
		recalculateGBCEIndex();
		synchronized (stockFeed) {
			stockFeed.onNext(stock);
		}
	}

	public void updateLastDividend(String symbol, Double lastDividend) {
		LOG.info("updateLastDividend " + symbol + " " + lastDividend);
		Stock stock = stocks.get(symbol);
		Stock updated = stock.cloneWithNewLastDividend(lastDividend);
		stocks.replace(updated.getSymbol(), updated);

		synchronized (stockFeed) {
			stockFeed.onNext(stock);
		}
	}

	public Double getGBCEIndex() {
		return gbceMarketIndex;
	}

	public void recalculateGBCEIndex() {
		Double priceProduct = 1D;
		int count = 0;
		HashMap<String, Stock> stockCopy = new HashMap<String, Stock>(stocks);
		for (Stock stock : stockCopy.values()) {
			if (stock.getStockPrice() != null && stock.getStockPrice() > 0) {
				priceProduct = priceProduct * stock.getStockPrice();
				count++;
			}
		}
		this.gbceMarketIndex = Math.pow(priceProduct, 1D / count);
		LOG.info("Recalculated GBCE market index " + gbceMarketIndex);
		synchronized (marketFeed) {
			marketFeed.onNext(gbceMarketIndex);
		}
	}

}
