package com.ibm.hybrid.cloud.sample.stocktrader.portfolio;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.dao.PurchaseTransactionDao;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseState;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseTransaction;

@Stateless
public class TransactionTimeoutChecker {
	private static Logger logger = Logger.getLogger(TransactionTimeoutChecker.class.getName());
	
	@Inject
	private PurchaseTransactionDao purchaseTransactionDAO;
	
	@Schedule(hour = "*", minute = "*/1", persistent = false )
	public void checkPendingTransactions() {
		logger.info("Check pending transactions");
		List<PurchaseTransaction> transInProgress =  purchaseTransactionDAO.findByTimeoutInProgress();
		if(transInProgress == null || transInProgress.isEmpty()) {
			logger.info("No pending trans to timeout");
		}
		else {
			logger.info("Timeouting " + transInProgress.size() + " transactions.");
			for (PurchaseTransaction purchaseTransaction : transInProgress) {
				purchaseTransaction.setState(PurchaseState.PURCHASE_FAILED);
				purchaseTransactionDAO.updatePurchaseEvent(purchaseTransaction);
			}
		}
	}

}
