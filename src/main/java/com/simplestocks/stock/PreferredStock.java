package com.simplestocks.stock;

public class PreferredStock extends Stock{

	private Double fixedDividend;
	private Double parValue;

	public PreferredStock(String symbol, Double parValue, Double fixedDividend){
		super(symbol, StockType.PREFERRED);
		this.parValue = parValue;
		this.fixedDividend = fixedDividend;
	}

	@Override
	public Double getDividend() {
		return fixedDividend * parValue;
	}

	@Override
	public String toString() {
		return "PreferredStock [fixedDividend=" + fixedDividend + ", parValue=" + parValue + ", getDividend()="
				+ getDividend() + ", getSymbol()=" + getSymbol() + ", getStockPrice()=" + getStockPrice()
				+ ", getPToERatio()=" + getPToERatio() + ", getDividendYield()=" + getDividendYield() + ", getType()="
				+ getType() + "]";
	}
	
	
}
