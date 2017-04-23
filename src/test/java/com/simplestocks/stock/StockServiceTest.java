package com.simplestocks.stock;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import rx.observers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceTest {

	private StockService stockService;
			

	@Before
	public void setUp() {
		stockService = new StockService();
	}

	@Test
	public void givenAStockIsRegisteredItShouldBeFetchable() {
		stockService.registerCommonStock("AAA", 200D);
		assertNotNull("Stock does not exist", stockService.getStock("AAA"));
	}

	@Test
	public void givenAnInvalidStockShouldReturnNull() {
		assertNull("Stock should not exist", stockService.getStock("AAA"));
	}

	@Test
	public void givenAnUpdateToPriceItShouldBeReflected() {
		stockService.registerCommonStock("AAA", 200D);
		stockService.updateStockPrice("AAA", 207D);
		assertNotNull("Stock does not exist", stockService.getStock("AAA"));
		assertEquals("Stock price update failed", 207D, stockService.getStock("AAA").getStockPrice(), 0D);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void givenUpdateToInvalidStockErrorShouldBeRaised() {
		stockService.registerCommonStock("AAA", 200D);
		stockService.updateStockPrice("AAB", 207D);
	}

	@Test
	public void givenUpdateToLastDividendItShouldBeRefletected() {
		stockService.registerCommonStock("AAA", 200D);
		stockService.updateLastDividend("AAA", 140.44D);
		assertNotNull("Stock does not exist", stockService.getStock("AAA"));
		assertEquals("Last dividend update failed", 140.44D,
				((CommonStock) stockService.getStock("AAA")).getLastDividend(), 0D);
	}

	@Test
	public void givenUpdateToStockPriceItShouldBeNotified() {
		TestSubscriber<Stock> sub = new TestSubscriber<Stock>();
		stockService.getStockFeed().subscribe(sub);
		stockService.registerCommonStock("AAA", 200D);
		stockService.updateStockPrice("AAA", 140.44D);
		await().timeout(500, TimeUnit.MILLISECONDS).until(sub::getValueCount, equalTo(1));
		sub.assertNoErrors();
		sub.assertValue(stockService.getStock("AAA"));
	}

	@Test
	public void givenUpdateToLastDividendItShouldBeNotified() {
		TestSubscriber<Stock> sub = new TestSubscriber<Stock>();
		stockService.getStockFeed().subscribe(sub);
		stockService.registerCommonStock("AAA", 200D);
		stockService.updateLastDividend("AAA", 20D);
		await().timeout(500, TimeUnit.MILLISECONDS).until(sub::getValueCount, equalTo(1));
		sub.assertNoErrors();
		sub.assertValue(stockService.getStock("AAA"));
	}
	
	@Test
	public void givenACommonShareCalculateDividend() {
		stockService.registerCommonStock("AAA", 14D);
		Stock stock = stockService.getStock("AAA");
		assertEquals("Common stock dividend incorrect", 14D, stock.getDividend(), 0D);
	}

	@Test
	public void givenAPreferredShareCalculateDividend() {
		stockService.registerPreferredStock("AAB", 200D, 0.03D);
		Stock stock = stockService.getStock("AAB");
		assertEquals("Prefered stock dividend incorrect", 6D, stock.getDividend(), 0D);
	}

	@Test
	public void givenAPreferredShareCalculatePtoERatio() {
		stockService.registerPreferredStock("AAB", 200D, 0.1D);
		stockService.updateStockPrice("AAB", 200D);
		Stock stock = stockService.getStock("AAB");
		assertEquals("Prefered stock p to e incorrect", 10D, stock.getPToERatio(), 0D);

	}

	@Test
	public void givenACommonShareCalculatePtoERatio() {
		stockService.registerCommonStock("AAA", 20D);
		stockService.updateStockPrice("AAA", 400D);
		Stock stock = stockService.getStock("AAA");
		assertEquals("Stock price not updated", 400D, stock.getStockPrice(), 0D);
		assertEquals("Common stock p to e incorrect", 20D, stock.getPToERatio(), 0D);
	}

	@Test
	public void givenACommonStockCalculateDividendYield() {
		stockService.registerCommonStock("AAA", 25D);
		stockService.updateStockPrice("AAA", 250D);
		Stock stock = stockService.getStock("AAA");
		assertEquals("Common stock dividend yield incorrect", 0.1D, stock.getDividendYield(), 0D);
	}

	@Test
	public void givenASetOfTradingStocksCalculateTheGBCEIndex() {
		stockService.registerCommonStock("AAA", 25D);
		stockService.updateStockPrice("AAA", 250D);
		stockService.registerCommonStock("BBB", 25D);
		stockService.updateStockPrice("BBB", 30D);
		stockService.registerCommonStock("CCC", 25D);
		stockService.updateStockPrice("CCC", 1300D);
		stockService.registerCommonStock("DDD", 25D);
		stockService.updateStockPrice("DDD", 100D);
		stockService.registerCommonStock("EEE", 25D);
		stockService.updateStockPrice("EEE", 200D);

		assertEquals("GBCE index is incorrect", 181.13689640194661D, stockService.getGBCEIndex(), 0D);
	}
}
