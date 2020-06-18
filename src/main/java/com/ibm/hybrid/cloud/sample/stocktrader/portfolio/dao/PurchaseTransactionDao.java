package com.ibm.hybrid.cloud.sample.stocktrader.portfolio.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseTransaction;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseState;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.Stock;

public class PurchaseTransactionDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;
    
    public PurchaseTransaction findByOwnerInProgress(String owner) {
    	List<PurchaseState> states = new ArrayList<PurchaseState>();
    	states.add(PurchaseState.PURCHASE_PENDING);
    	states.add(PurchaseState.LOYALTY_CHANGE_PENDING);
        List<PurchaseTransaction> resultList = em.createNamedQuery("PurchaseTransaction.findByOwnerInProgress", PurchaseTransaction.class)
            .setParameter("owner", owner)
            .setParameter("pendingStates", states)
            .getResultList();
        if(resultList == null || resultList.isEmpty()) {
        	return null;
        }
        return resultList.get(0);
    }

	public void createPurchaseEvent(PurchaseTransaction pt) {
		em.persist(pt);
		em.flush();
	}
	
    public void updatePurchaseEvent(PurchaseTransaction pt) {
        em.merge(pt);
        em.flush();
    }
}
