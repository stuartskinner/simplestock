package com.simplestocks.trade;

import java.util.Date;

public class Trade {
	private String stockSymbol;
	private Integer quantity;
	private Double tradePrice;
	private TradeType tradeType;
	private Date timestamp;
	
	public Trade(String stockSymbol, Integer quantity, Double tradePrice, TradeType tradeType, Date timestamp) {
		super();
		this.stockSymbol = stockSymbol;
		this.quantity = quantity;
		this.tradePrice = tradePrice;
		this.tradeType = tradeType;
		this.timestamp = timestamp;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getTradePrice() {
		return tradePrice;
	}
	
	public Double getTotalTradePrice(){
		return getTradePrice() * getQuantity();
	}

	public TradeType getTradeType() {
		return tradeType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "Trade [stockSymbol=" + stockSymbol + ", quantity=" + quantity + ", tradePrice=" + tradePrice
				+ ", tradeType=" + tradeType + ", timestamp=" + timestamp + "]";
	}
	
	
	
	
}
