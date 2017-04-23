package com.simplestocks.stock;

public class CommonStock extends Stock{

	private volatile Double lastDividend;
	

	public CommonStock(String symbol, Double lastDividend){
		super(symbol, StockType.COMMON);
		this.lastDividend = lastDividend;
	}

	@Override
	public Double getDividend() {
		return lastDividend;
	}
	
	public Double getLastDividend() {
		return lastDividend;
	}

	public void setLastDividend(Double lastDividend) {
		this.lastDividend = lastDividend;
	}

	@Override
	public String toString() {
		return "CommonStock [lastDividend=" + lastDividend + ", getSymbol()=" + getSymbol() + ", getStockPrice()="
				+ getStockPrice() + ", getPToERatio()=" + getPToERatio() + ", getDividendYield()=" + getDividendYield()
				+ ", getType()=" + getType() + "]";
	}
	
	
}
