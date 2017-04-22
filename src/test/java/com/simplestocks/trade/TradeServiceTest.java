package com.simplestocks.trade;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.simplestocks.trade.Trade;
import com.simplestocks.trade.TradeRepository;
import com.simplestocks.trade.TradeService;
import com.simplestocks.trade.TradeType;

import rx.observers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class TradeServiceTest {
	
	private TradeService tradeService;
	
	@Mock
	private TradeRepository tradeRepository;
	
	@Before
	public void setUp(){
		tradeService = new TradeService(tradeRepository);
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANullTradeAnErrorShouldBeRaised(){
		tradeService.trade(null);
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANullSymbolAnErrorShouldBeRaised(){
		tradeService.trade(new Trade(null, 100, 105D, TradeType.BUY, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANullAmountAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", null, 105D, TradeType.BUY, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANegativeAmountAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", -5, 105D, TradeType.BUY, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenAZeroAmountAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", 0, 105D, TradeType.BUY, new Date()));
	}

	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANullPriceAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", 10, null, TradeType.BUY, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANegativePriceAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", 100, -100D, TradeType.BUY, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenAZeroPriceAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", 100, 0D, TradeType.BUY, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANullTypeAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", 100, 100D, null, new Date()));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void givenANullTimestampAnErrorShouldBeRaised(){
		tradeService.trade(new Trade("AAA", 100, 100D, TradeType.BUY, null));
	}
	
	@Test
	public void givenAnInvalidSymbolAnErrorShouldBeRaised(){
		
	}
	
	@Test
	public void givenAValidTradeListenersShouldBeNotified(){
		Trade trade = new Trade("AAA", 100, 100D, TradeType.BUY, new Date());
		TestSubscriber<Trade> sub = new TestSubscriber<Trade>();
		tradeService.getTradeFeed().subscribe(sub);
		tradeService.trade(trade);
		await().timeout(500, TimeUnit.MILLISECONDS).until(sub::getValueCount, equalTo(1));
		sub.assertValue(trade);
	}
	
	@Test
	public void givenAValidTradeTheTradeShouldBePersisted(){
		Trade trade = new Trade("AAA", 100, 100D, TradeType.BUY, new Date());
		tradeService.trade(trade);
		verify(tradeRepository).saveTrade(trade);
	}
}
