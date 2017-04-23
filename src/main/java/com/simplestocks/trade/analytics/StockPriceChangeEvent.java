package com.simplestocks.trade.analytics;

/**
 * £vent class representing a change in a stocks price.
 *
 */
public class StockPriceChangeEvent {
	private String symbol;
	private Double newStockPrice;
	
	public StockPriceChangeEvent(String symbol, Double newStockPrice) {
		super();
		this.symbol = symbol;
		this.newStockPrice = newStockPrice;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public Double getNewStockPrice() {
		return newStockPrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((newStockPrice == null) ? 0 : newStockPrice.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		StockPriceChangeEvent other = (StockPriceChangeEvent) obj;
		if (newStockPrice == null) {
			if (other.newStockPrice != null)
				return false;
		} else if (!newStockPrice.equals(other.newStockPrice))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StockPriceChangeEvent [symbol=" + symbol + ", newStockPrice=" + newStockPrice + "]";
	}
	
	
}
