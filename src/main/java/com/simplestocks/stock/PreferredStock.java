package com.simplestocks.stock;

/**
 * Specialization of stock representing a preferred stock
 * 
 * A preferred stock essentially has characteristics more
 * akin to a bond ie a fixed dividend.
 * 
 * The dividend of a preferred stock is calculated as
 * fixedDividend * parValue
 *
 */
public class PreferredStock extends Stock{

	private Double fixedDividend;
	private Double parValue;

	public PreferredStock(String symbol, Double parValue, Double fixedDividend){
		super(symbol, StockType.PREFERRED);
		this.parValue = parValue;
		this.fixedDividend = fixedDividend;
	}
	
	public PreferredStock(PreferredStock source, Double newStockPrice){
		super(source, newStockPrice);
		this.parValue = source.parValue;
		this.fixedDividend = source.fixedDividend;
	}
	
	
	@Override
	public Stock cloneWithNewPrice(Double newStockPrice) {
		return new PreferredStock(this, newStockPrice);
	}

	@Override
	public Stock cloneWithNewLastDividend(Double newLastDividend) {
		// np new last dividend for preferred stock not a mutating operation
		// so just returb this
		return this;
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
