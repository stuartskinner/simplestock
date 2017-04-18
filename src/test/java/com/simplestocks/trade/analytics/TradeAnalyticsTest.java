package com.simplestocks.trade.analytics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.simplestocks.trade.Trade;
import com.simplestocks.trade.TradeRepository;
import com.simplestocks.trade.TradeService;
import com.simplestocks.trade.TradeType;
import com.simplestocks.trade.analytics.StockPriceChangeEvent;
import com.simplestocks.trade.analytics.TradeAnalytics;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;
import rx.subjects.TestSubject;

public class TradeAnalyticsTest {

	private TradeAnalytics service;
	private PublishSubject<Trade> obs;
	private TestSubscriber<StockPriceChangeEvent> sub;
		
	@Before
	public void setUp(){
		obs = PublishSubject.create();
		sub = new TestSubscriber<StockPriceChangeEvent>();
		service = new TradeAnalytics(obs);
		service.getStockFeed().subscribe(sub);
	}
	
	@Test
	public void givenASeriesOfTradeEventsCalculateNewStockPrice() throws InterruptedException{
		service.useFixedSizeWindow(3);
		
		obs.onNext(new Trade("AAA", 100, 105D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 10, 106D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 150, 110D, TradeType.BUY, new Date()));
		
		sub.assertValue(new StockPriceChangeEvent("AAA", 107.92307692307692));	
	}
	
	@Test
	public void givenASeriesOfMixedTradeEventsCalculateNewStockPrice() throws InterruptedException{
		service.useFixedSizeWindow(6);
		
		obs.onNext(new Trade("AAA", 100, 105D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("BBB", 50, 220D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 10, 106D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("BBB", 85, 210D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("BBB", 110, 225D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 150, 110D, TradeType.BUY, new Date()));
		
		sub.assertValueCount(2);
		sub.assertValues(new StockPriceChangeEvent("AAA", 107.92307692307692), new StockPriceChangeEvent("BBB", 218.77551020408163));
	}
	
	@Test 
	public void givenASeriesOfEventsThatExceedsAWindowExpectTwoOutputs(){
		service.useFixedSizeWindow(3);
		
		obs.onNext(new Trade("AAA", 100, 105D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 50, 220D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 10, 106D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 85, 210D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 110, 225D, TradeType.BUY, new Date()));
		obs.onNext(new Trade("AAA", 150, 110D, TradeType.BUY, new Date()));
		
		sub.assertValueCount(2);
		sub.assertValues(new StockPriceChangeEvent("AAA", 141.0), new StockPriceChangeEvent("AAA", 171.30434782608697));
	}
}
