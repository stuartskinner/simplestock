package com.simplestocks;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplestocks.stock.StockService;
import com.simplestocks.trade.Trade;
import com.simplestocks.trade.TradeRepository;
import com.simplestocks.trade.TradeService;
import com.simplestocks.trade.TradeType;
import com.simplestocks.trade.analytics.TradeAnalytics;

import rx.schedulers.TestScheduler;

public class SimpleStocks {

	private TradeService tradeService;
	private StockService stockService;
	private TradeAnalytics analytics;
	private static final Logger LOG = LoggerFactory.getLogger(SimpleStocks.class);
	
	public SimpleStocks(){
		tradeService = new TradeService(new TradeRepository());
		analytics = new TradeAnalytics(tradeService.getTradeFeed(), 15, 1, TimeUnit.MINUTES);
		stockService = new StockService();
		stockService.getStockFeed().subscribe(s ->
				LOG.info("STOCK PRICE UPDATE " + s));
		stockService.getMarketFeed().subscribe(s ->
			LOG.info("GBCE MARKET INDEX UPDATE " + s));

		analytics.connect();
		analytics.getStockFeed().subscribe(e -> stockService.updateStockPrice(e.getSymbol(), e.getNewStockPrice()));
	}
	
	public void registerStocks(){
		stockService.registerCommonStock("TEA", 0D);
		stockService.registerCommonStock("POP", 8D);
		stockService.registerCommonStock("ALE", 23D);
		stockService.registerPreferredStock("GIN", 0.02D, 100D);
		stockService.registerCommonStock("JOE", 13D);
	}
	
	
	public StockService getStockService(){
		return stockService;
	}
	
	public TradeService getTradeService(){
		return tradeService;
	}
	
	public static void main(String args[]) throws Exception{
		SimpleStocks stocks = new SimpleStocks();
		stocks.registerStocks();
		while(true){
			stocks.getTradeService().trade(generateRandomTrade(stocks.getStockService()));
			Thread.sleep(100);
		}
	}
	
	
	private static Trade generateRandomTrade(StockService stockService) {
		Random random = new Random();
		
		String[] stocks = stockService.getStockSymbols().toArray(new String[0]);
		int ran = random.nextInt(stocks.length);
		String symbol = stocks[ran];
		
		double tickerPrice = stockService.getStock(symbol).getStockPrice();
		
		double tradePrice;
		if(tickerPrice == 0D){
			tradePrice = random.nextDouble() * 1000;
		}else{
			tradePrice = random.nextDouble() * 1.5 * tickerPrice;
		}
		
		int quantity = random.nextInt(10000) + 1;
		
		Trade t = new Trade(symbol, quantity, tradePrice, TradeType.BUY, new Date());
		return t;
	}
}
