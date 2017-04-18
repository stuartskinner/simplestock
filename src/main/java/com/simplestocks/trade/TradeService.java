package com.simplestocks.trade;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class TradeService {

	private TradeRepository repository;
	private PublishSubject<Trade> tradeFeed;

	public TradeService(TradeRepository repository){
		tradeFeed = PublishSubject.create();
		this.repository = repository;
	}
	
	
	public void trade(Trade trade) {
		validateTrade(trade);
		repository.saveTrade(trade);
		System.out.println("Punlishing trade " + trade);
		tradeFeed.onNext(trade);
	}

	public Observable<Trade> getTradeFeed(){
		return tradeFeed;
	}
	
	private void validateTrade(Trade trade) {
		if(trade == null){
			throw new IllegalArgumentException("Trade must be non null");
		}
		if(trade.getStockSymbol() == null){
			throw new IllegalArgumentException("Stock symbol must be non null");
		}
		if(trade.getQuantity() == null){
			throw new IllegalArgumentException("Quantity must be non null");
		}
		if(trade.getQuantity() <= 0){
			throw new IllegalArgumentException("Quantity must be greater than zerp");
		}
		if(trade.getTradePrice() == null){
			throw new IllegalArgumentException("Price must be non null");
		}
		if(trade.getTradePrice() <= 0){
			throw new IllegalArgumentException("Price must be greater than zerp");
		}
		if(trade.getTradeType() == null){
			throw new IllegalArgumentException("Type must be non null");
		}
		if(trade.getTimestamp() == null){
			throw new IllegalArgumentException("Timestamp must be non null");
		}
	}


}
