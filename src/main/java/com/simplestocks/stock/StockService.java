package com.simplestocks.stock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

public class StockService {

	private Map<String, Stock> stocks = Collections.synchronizedMap(new HashMap<String, Stock>());
	private PublishSubject<Stock> stockFeed;
	private Double gbceMarketIndex;
	
	public StockService(){
		stockFeed = PublishSubject.create();
	}
	
	public Observable<Stock> getStockFeed(){
		return stockFeed;	
	}
	
	public void registerCommonStock(String symbol, Double lastDividend){
		Stock stock = new CommonStock(symbol, lastDividend);
		stocks.put(stock.getSymbol(), stock);
	}
	
	public void registerPreferredStock(String symbol, Double parValue, Double fixedDividend){
		Stock stock = new PreferredStock(symbol, parValue, fixedDividend);
		stocks.put(stock.getSymbol(), stock);
	}
	
	public Stock getStock(String symbol){
		return stocks.get(symbol);
	}
	
	public void updateStockPrice(String symbol, Double price){
		if(!stocks.containsKey(symbol)){
			throw new IllegalArgumentException("Invalid stock symbol");
		}
		Stock stock = stocks.get(symbol); 
		stock.setStockPrice(price);
		recalculateGBCEIndex();
		stockFeed.onNext(stock);
	}
	
	public void updateLastDividend(String symbol, Double lastDividend){
		Stock stock = stocks.get(symbol);
		if(stock instanceof CommonStock){
			((CommonStock)stock).setLastDividend(lastDividend);
			stockFeed.onNext(stock);
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
		for(Stock stock: stocks.values()){
			if(stock.getStockPrice() != null && stock.getStockPrice() > 0){
				priceProduct = priceProduct * stock.getStockPrice();
				count++;
			}
		}
		this.gbceMarketIndex = Math.pow(priceProduct, 1D/count);
	}

}
