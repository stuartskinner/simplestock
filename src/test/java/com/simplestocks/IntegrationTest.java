package com.simplestocks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import com.simplestocks.stock.Stock;
import com.simplestocks.stock.StockService;
import com.simplestocks.trade.Trade;
import com.simplestocks.trade.TradeRepository;
import com.simplestocks.trade.TradeService;
import com.simplestocks.trade.TradeType;
import com.simplestocks.trade.analytics.TradeAnalytics;

import rx.Scheduler;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

public class IntegrationTest {

	private TradeService tradeService;
	private StockService stockService;
	private TradeAnalytics analytics;
	private TestScheduler scheduler;
	
	@Before
	public void setUp(){
		scheduler = Schedulers.test();
		tradeService = new TradeService(new TradeRepository());
		analytics = new TradeAnalytics(tradeService.getTradeFeed(), 3, 1, TimeUnit.SECONDS, scheduler);
		stockService = new StockService();
		analytics.connect();
		analytics.getStockFeed().subscribe(e -> stockService.updateStockPrice(e.getSymbol(), e.getNewStockPrice()));
		
		stockService.getStockFeed().subscribe(s -> System.out.println("Stock price change " + s));
		stockService.registerCommonStock("TEA", 0D);
		stockService.registerCommonStock("POP", 8D);
		stockService.registerCommonStock("ALE", 23D);
		stockService.registerPreferredStock("GIN", 0.02D, 100D);
		stockService.registerCommonStock("JOE", 13D);
		
	}
	
	@Test
	public void givenSomeTradeDataValidateSystem() throws InterruptedException{
		TestSubscriber<Stock> stockSub = new TestSubscriber<>();
		TestSubscriber<Double> marketSub = new TestSubscriber<>();
		TestSubscriber<Trade> tradeSub = new TestSubscriber<>();
		stockService.getStockFeed().subscribe(stockSub);
		stockService.getMarketFeed().subscribe(marketSub);
		tradeService.getTradeFeed().subscribe(tradeSub);
		
		tradeService.trade(new Trade("TEA", 50, 102D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("POP", 55, 105D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("ALE", 456, 210D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("TEA", 486, 97D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("ALE", 345, 200D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("GIN", 333, 450D, TradeType.SELL, new Date()));
		tradeService.trade(new Trade("POP", 444, 78D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("JOE", 767, 100D, TradeType.BUY, new Date()));
		tradeService.trade(new Trade("JOE", 1000, 120D, TradeType.SELL, new Date()));
		tradeService.trade(new Trade("GIN", 100, 100D, TradeType.BUY, new Date()));
		
		await().timeout(500, TimeUnit.MILLISECONDS).until(tradeSub::getValueCount, equalTo(10));
		
		scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
		
		await().timeout(500, TimeUnit.MILLISECONDS).until(stockSub::getValueCount, equalTo(5));
		
		assertEquals(97.46641791044776D, stockService.getStock("TEA").getStockPrice(), 0D);
		assertEquals(80.97595190380761D, stockService.getStock("POP").getStockPrice(), 0D);
		assertEquals(369.1685912240185D, stockService.getStock("GIN").getStockPrice(), 0D);
		assertEquals(205.69288389513108D, stockService.getStock("ALE").getStockPrice(), 0D);
		assertEquals(111.31861912846632D, stockService.getStock("JOE").getStockPrice(), 0D);
		
		await().timeout(500, TimeUnit.MILLISECONDS).until(marketSub::getValueCount, equalTo(5));
		
		assertEquals(146.16539945993313D, stockService.getGBCEIndex(), 0D);
	}
}
