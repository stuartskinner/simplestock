package com.simplestocks.stock;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplestocks.trade.TradeService;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class StockService {

	private Map<String, Stock> stocks = Collections.synchronizedMap(new HashMap<String, Stock>());
	private PublishSubject<Stock> stockFeed;
	private PublishSubject<Double> marketFeed;
	
	private volatile Double gbceMarketIndex;
	private static final Logger LOG = LoggerFactory.getLogger(TradeService.class);
	
	public StockService(){
		stockFeed = PublishSubject.create();
		marketFeed = PublishSubject.create();
	}
	
	public Observable<Stock> getStockFeed(){
		return stockFeed.observeOn(Schedulers.computation());	
	}
	
	public Observable<Double> getMarketFeed(){
		return marketFeed.observeOn(Schedulers.computation());	
	}
	
	public void registerCommonStock(String symbol, Double lastDividend){
		Stock stock = new CommonStock(symbol, lastDividend);
		stocks.put(stock.getSymbol(), stock);
	}
	
	public void registerPreferredStock(String symbol, Double parValue, Double fixedDividend){
		LOG.info("registerPreferredStock " + symbol + " parValue " + fixedDividend);
		Stock stock = new PreferredStock(symbol, parValue, fixedDividend);
		stocks.put(stock.getSymbol(), stock);
	}
	
	public Set<String> getStockSymbols(){
		return new HashSet<String>(stocks.keySet());
	}
	
	public Stock getStock(String symbol){
		LOG.info("getStock " + symbol);
		return stocks.get(symbol);
	}
	
	public void updateStockPrice(String symbol, Double price){
		LOG.info("updateStockPrice " + symbol + " " + price);
		if(!stocks.containsKey(symbol)){
			throw new IllegalArgumentException("Invalid stock symbol");
		}
		
		Stock stock = stocks.get(symbol); 
		stock.setStockPrice(price);
		recalculateGBCEIndex();
		synchronized (stockFeed) {
			stockFeed.onNext(stock);
		}
	}
	
	public void updateLastDividend(String symbol, Double lastDividend){
		LOG.info("updateLastDividend " + symbol + " " + lastDividend);
		Stock stock = stocks.get(symbol);
		if(stock instanceof CommonStock){
			((CommonStock)stock).setLastDividend(lastDividend);
			synchronized(stockFeed){
				stockFeed.onNext(stock);
			}
		}else{
			throw new IllegalArgumentException("Stock is not of type COMMON");
		}
	}
	
	public Double getGBCEIndex() {
		return gbceMarketIndex;
	}
	
	public void recalculateGBCEIndex(){
		Double priceProduct = 1D;
		int count = 0;
		HashMap<String,Stock> stockCopy = new HashMap<String,Stock>(stocks);
		for(Stock stock: stockCopy.values()){
			if(stock.getStockPrice() != null && stock.getStockPrice() > 0){
				priceProduct = priceProduct * stock.getStockPrice();
				count++;
			}
		}
		this.gbceMarketIndex = Math.pow(priceProduct, 1D/count);
		LOG.info("Recalculated GBCE market index " + gbceMarketIndex);
		synchronized(marketFeed){
			marketFeed.onNext(gbceMarketIndex);
		}
	}

}
