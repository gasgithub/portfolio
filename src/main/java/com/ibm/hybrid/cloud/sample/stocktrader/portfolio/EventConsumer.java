package com.ibm.hybrid.cloud.sample.stocktrader.portfolio;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.dao.PurchaseTransactionDao;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.event.BaseEvent;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.event.LoyaltyChangeEvent;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseState;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseTransaction;

@ApplicationScoped
public class EventConsumer {
	
	private static Logger logger = Logger.getLogger(EventConsumer.class.getName());
	private Jsonb jsonb = JsonbBuilder.create();
	
	private @Inject @ConfigProperty(name = "NOTIFICATION_ENABLED", defaultValue = "true") boolean NOTIFICATION_ENABLED;

	@Inject
	private PortfolioService portfolioService;
	
	@Inject
	private PurchaseTransactionDao purchaseTransactionDao;
	
	
	@Incoming("stock-channel")
	public void receive(String eventAsString) {
		try {
			logger.info("Received: " + eventAsString);
			
			// parse BaseEvent
			BaseEvent event = null;
			event = jsonb.fromJson(eventAsString, BaseEvent.class);
			
			String type = event.getType();
			logger.info("type: " + type);
			
			if(type.equals(BaseEvent.TYPE_LOYALTY_CHANGED)) {
				// handle 
				// finalize transaction
				logger.info("Received: " + type + " Notification enabled: " + NOTIFICATION_ENABLED);
				LoyaltyChangeEvent loyaltyChangeEvent = jsonb.fromJson(eventAsString, LoyaltyChangeEvent.class);
				if(NOTIFICATION_ENABLED) {
					// change transaction status
					portfolioService.updatePurchaseTransaction(event.getOwner(), PurchaseState.LOYALTY_CHANGE_PENDING);
				}
				else {
					// update loyalty
					portfolioService.updateLoyalty(loyaltyChangeEvent.getOwner(), loyaltyChangeEvent.getNewLoyalty());
					// finalize transaction
					portfolioService.onAcceptTransactions(event.getOwner());
				}
				
			}
			else if(type.equals(BaseEvent.TYPE_LOYALTY_NOT_CHANGED)) {
				// handle 
				// finalize transaction
				logger.info("handle1: " + type);
				portfolioService.onAcceptTransactions(event.getOwner());
			}
			else if(type.equals(BaseEvent.TYPE_LOYALTY_CHANGE_NOTIFIED)) {
				// handle 
				// change status
				logger.info("handle: " + type);
			}
			else if(type.equals(BaseEvent.TYPE_LOYALTY_CHANGED_FAILED)) {
				// handle 
				// rollback
				logger.info("handle: " + type);
			}
			else if(type.equals(BaseEvent.TYPE_PURCHASE)) {
				// handle 
				// rollback
				logger.info("ignore: " + type);
			}
			else {
				logger.warning("Unknown event: " + type);
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
			throw t; // should disable ack...
		 }
	}


}
