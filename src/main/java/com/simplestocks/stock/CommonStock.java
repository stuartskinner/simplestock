package com.simplestocks.stock;

/**
 * Common stock specialisation of a stock. A common
 * stock is characterized by 
 * 
 * a dividend equal to the value of the last paid dividend.
 * 
 *
 */
public class CommonStock extends Stock{
	
	public CommonStock(String symbol, Double lastDividend){
		super(symbol, StockType.COMMON, lastDividend);
	}


	public CommonStock(Stock source, Double newStockPrice, Double newDividend) {
		super(source, newStockPrice, newDividend);
	}

	public CommonStock(Stock source, Double newStockPrice) {
		super(source, newStockPrice);
	}

	@Override
	public Double getDividend() {
		return getLastDividend();
	}
	
	@Override
	public Stock cloneWithNewPrice(Double newStockPrice) {
		return new CommonStock(this, newStockPrice);
	}

	@Override
	public Stock cloneWithNewLastDividend(Double newLastDividend) {
		return new CommonStock(this, getStockPrice(), newLastDividend);
	}	

	@Override
	public String toString() {
		return "CommonStock [lastDividend=" + getLastDividend() + ", getSymbol()=" + getSymbol() + ", getStockPrice()="
				+ getStockPrice() + ", getPToERatio()=" + getPToERatio() + ", getDividendYield()=" + getDividendYield()
				+ ", getType()=" + getType() + "]";
	}


	
	
}
