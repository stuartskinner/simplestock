package com.simplestocks;

import java.util.concurrent.TimeUnit;

import com.simplestocks.stock.StockService;
import com.simplestocks.trade.TradeRepository;
import com.simplestocks.trade.TradeService;
import com.simplestocks.trade.analytics.TradeAnalytics;

public class SimpleStocks {

	public SimpleStocks(){
		
	}
	
	
	public static void main(String args[]) {
//		TradeService tradeService = new TradeService(new TradeRepository());
//		TradeAnalytics analytics = new TradeAnalytics(tradeService.getTradeFeed());
//		StockService stockService = new StockService();
//
//		analytics.useSlidingTimeWindow(15L, 1L, TimeUnit.MINUTES);
//
//		analytics.getStockFeed().subscribe(e -> stockService.updateStockPrice(e.getSymbol(), e.getNewStockPrice()));
//
//		stockService.getStockFeed().subscribe(s -> System.out.println("Stock price change " + s));
		
		// TradeSimulator simulator = new TradeSimulator(tradeService,
		// stockService);
		// simulator.start();
	}

}
