package com.simplestocks.trade.analytics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import static org.junit.Assert.fail;
import org.junit.Test;

import com.simplestocks.trade.Trade;
import com.simplestocks.trade.TradeType;

import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

public class TradeAnalyticsTest {

	private TradeAnalytics service;
	private PublishSubject<Trade> obs;
	private TestSubscriber<StockPriceChangeEvent> sub;
	private TestScheduler scheduler;
	
	@Before
	public void setUp(){
		obs = PublishSubject.create();
		sub = new TestSubscriber<StockPriceChangeEvent>();
		scheduler = new TestScheduler();
		service = new TradeAnalytics(obs, scheduler);
		service.getStockFeed().subscribe(sub);
		service.useSlidingTimeWindow(3L, 1L, TimeUnit.SECONDS);
	}
	
	@Test
	public void givenASeriesOfTradeEventsCalculateNewStockPrice() throws InterruptedException{
		
		
		obs.onNext(new Trade("AAA", 100, 105D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 10, 106D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 150, 110D, TradeType.BUY, new Date()));
		
		scheduler.advanceTimeBy(3L, TimeUnit.SECONDS);
		
		
		sub.assertValue(new StockPriceChangeEvent("AAA", 107.92307692307692));	
	}
	
	@Test
	public void givenASeriesOfMixedTradeEventsCalculateNewStockPrice() throws InterruptedException{
		
		
		obs.onNext(new Trade("AAA", 100, 105D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("BBB", 50, 220D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 10, 106D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("BBB", 85, 210D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("BBB", 110, 225D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 150, 110D, TradeType.BUY, new Date()));
		
		scheduler.advanceTimeBy(3L, TimeUnit.SECONDS);
		sub.assertValueCount(2);
		sub.assertValues(new StockPriceChangeEvent("AAA", 107.92307692307692), new StockPriceChangeEvent("BBB", 218.77551020408163));
	}
	
	@Test 
	public void givenASeriesOfEventsThatExceedsAWindowExpectTwoOutputs(){
		
		
		obs.onNext(new Trade("AAA", 100, 100D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 50, 100D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 10, 100D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 85, 100D, TradeType.BUY, new Date()));
		
		scheduler.advanceTimeBy(1L, TimeUnit.SECONDS);
		
		//Expect no output as first window not complete
		sub.assertValueCount(0);
		
		obs.onNext(new Trade("BBB", 110, 50D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 150, 100D, TradeType.BUY, new Date()));
		
		scheduler.advanceTimeBy(1L, TimeUnit.SECONDS);
		
		//Expect no output as first window not complete
		sub.assertValueCount(0);
				
		obs.onNext(new Trade("CCC", 150, 110D, TradeType.BUY, new Date()));
		
		scheduler.advanceTimeBy(1L, TimeUnit.SECONDS);
		
		// first window complete expect event for AAA, BBB and CCC
		sub.assertValueCount(3);
		
		assertStockChangeEventFor("AAA", sub);
		assertStockChangeEventFor("BBB", sub);
		assertStockChangeEventFor("CCC", sub);
		sub.getOnCompletedEvents().clear();
		
		scheduler.advanceTimeBy(1L, TimeUnit.SECONDS);
		
		sub.assertValueCount(6);
		assertStockChangeEventFor("AAA", sub);
		assertStockChangeEventFor("BBB", sub);
		assertStockChangeEventFor("CCC", sub);
		
		
		scheduler.advanceTimeBy(1L, TimeUnit.SECONDS);
		
		sub.assertValueCount(7);
		
		assertStockChangeEventFor("CCC", sub);

	}
	
	private void assertStockChangeEventFor(String stockSymbol, TestSubscriber<StockPriceChangeEvent> sub){
		for(StockPriceChangeEvent event:sub.getOnNextEvents()){
			if(event.getSymbol().equals(stockSymbol)){
				return;
			}
		}
		fail("No stock change events for " + stockSymbol + " observed");
	}
}
