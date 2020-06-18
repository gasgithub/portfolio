package com.ibm.hybrid.cloud.sample.stocktrader.portfolio.json;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table
@NamedQuery(name = "PurchaseTransaction.findByOwnerInProgress", 
		query = "select pe from PurchaseTransaction pe where pe.owner = :owner and pe.state in :pendingStates")
		
public class PurchaseTransaction { 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	private PurchaseState state;
	
	private String owner;
	
	private String symbol;
	
	private int shares;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PurchaseState getState() {
		return state;
	}

	public void setState(PurchaseState state) {
		this.state = state;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}
	
	
}
