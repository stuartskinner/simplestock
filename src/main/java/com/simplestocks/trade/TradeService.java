package com.simplestocks.trade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * TradeService is responsible for initial capture
 * of trades into the system, their validation, persistence
 * amd notification of the trade to interested parties.s
 *
 */
public class TradeService {

	private TradeRepository repository;
	private PublishSubject<Trade> tradeFeed;
	private static final Logger LOG = LoggerFactory.getLogger(TradeService.class);
	
	
	public TradeService(TradeRepository repository){
		tradeFeed = PublishSubject.create();
		this.repository = repository;
	}
	
	
	public void trade(Trade trade) {
		LOG.info("trade " + trade);
		try{
			validateTrade(trade);
			repository.saveTrade(trade);
			synchronized(tradeFeed){
				tradeFeed.onNext(trade);
			}
			LOG.info("trade success " + trade);
		}catch(IllegalArgumentException e){
			LOG.error("trade failed validation " + e.getMessage() + " " + trade);
			throw e;
		}
	}

	public Observable<Trade> getTradeFeed(){
		return tradeFeed.observeOn(Schedulers.computation());
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
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}
		if(trade.getTradePrice() == null){
			throw new IllegalArgumentException("Price must be non null");
		}
		if(trade.getTradePrice() <= 0){
			throw new IllegalArgumentException("Price must be greater than zero");
		}
		if(trade.getTradeType() == null){
			throw new IllegalArgumentException("Type must be non null");
		}
		if(trade.getTimestamp() == null){
			throw new IllegalArgumentException("Timestamp must be non null");
		}
	}
}
