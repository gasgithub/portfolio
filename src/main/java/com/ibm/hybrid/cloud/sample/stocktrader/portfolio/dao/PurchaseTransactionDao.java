package com.ibm.hybrid.cloud.sample.stocktrader.portfolio.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseState;
import com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json.PurchaseTransaction;

public class PurchaseTransactionDao {
	private @Inject @ConfigProperty(name = "FAILED_TIMEOUT", defaultValue = "3") int TIMEOUT;

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

    public List<PurchaseTransaction> findByTimeoutInProgress() {
    	LocalDateTime now = LocalDateTime.now();
    	now = now.minusMinutes(TIMEOUT);
    	List<PurchaseState> states = new ArrayList<PurchaseState>();
    	states.add(PurchaseState.PURCHASE_PENDING);
    	states.add(PurchaseState.LOYALTY_CHANGE_PENDING);
        List<PurchaseTransaction> resultList = em.createNamedQuery("PurchaseTransaction.findByTimeoutInProgress", PurchaseTransaction.class)
            .setParameter("pendingStates", states)
            .setParameter("dateToCheck", Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .getResultList();
        if(resultList == null || resultList.isEmpty()) {
        	return null;
        }
        return resultList;
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
