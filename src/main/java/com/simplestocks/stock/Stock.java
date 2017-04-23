package com.simplestocks.stock;

public abstract class Stock {

	private String symbol;
	private StockType type;
	private volatile Double stockPrice = 0D;

	public Stock(String symbol, StockType type) {
		this.symbol = symbol;
		this.type = type;
	}
	
	public String getSymbol() {
		return this.symbol;
	}

	public Double getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(Double stockPrice) {
		this.stockPrice = stockPrice;
	}

	public abstract Double getDividend();

	public Double getPToERatio() {
		return getStockPrice() / getDividend();
	}

	public Double getDividendYield() {
		return getDividend() / getStockPrice();
	}

	public StockType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stock other = (Stock) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Stock [symbol=" + symbol + ", type=" + type + ", stockPrice=" + stockPrice + ", getDividend()="
				+ getDividend() + ", getPToERatio()=" + getPToERatio() + ", getDividendYield()=" + getDividendYield()
				+ "]";
	}
	
	

}
