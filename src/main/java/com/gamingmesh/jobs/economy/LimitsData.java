package com.gamingmesh.jobs.economy;

import com.gamingmesh.jobs.container.CurrencyType;

public class LimitsData {
    private CurrencyType type = null;
    private double amount = 0D;
    private long paymentsTime = 0L;
    private boolean reseted = false;

    public LimitsData(CurrencyType type, long paymentsTime) {
	this(type, paymentsTime, 0D);
    }

    public LimitsData(CurrencyType type, long paymentsTime, double payment) {
	this.type = type;
	this.paymentsTime = paymentsTime;
	this.amount = payment;
    }

    public boolean isReseted() {
	return reseted;
    }

    public void setReseted(boolean reseted) {
	this.reseted = reseted;
    }

    public CurrencyType getType() {
	return type;
    }

    public void setType(CurrencyType type) {
	this.type = type;
    }

    public double getAmount() {
	return this.amount;
    }

    public double addAmount(double amount) {
	return this.amount += amount;
    }

    public void setAmount(double payment) {
	this.amount = payment;
    }

    public long getPaymentsTime() {
	return paymentsTime;
    }

    public void setPaymentsTime(long paymentsTime) {
	this.paymentsTime = paymentsTime;
    }

}
