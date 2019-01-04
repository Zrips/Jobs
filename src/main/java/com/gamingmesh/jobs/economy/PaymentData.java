package com.gamingmesh.jobs.economy;

import java.util.HashMap;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;

public class PaymentData {

    private Long lastAnnouced = 0L;
    private HashMap<CurrencyType, Double> payments = new HashMap<>();
    private HashMap<CurrencyType, Long> paymentsTimes = new HashMap<>();
    private boolean Informed = false;
    private boolean Reseted = false;

    public PaymentData(Long time, Double Payment, Double Points, Double Exp, Long lastAnnouced, boolean Informed) {
	paymentsTimes.put(CurrencyType.EXP, time);
	paymentsTimes.put(CurrencyType.MONEY, time);
	paymentsTimes.put(CurrencyType.POINTS, time);
	payments.put(CurrencyType.EXP, Exp);
	payments.put(CurrencyType.MONEY, Payment);
	payments.put(CurrencyType.POINTS, Points);
	this.lastAnnouced = lastAnnouced;
	this.Informed = Informed;
    }

    public PaymentData(CurrencyType type, Double amount) {
	paymentsTimes.put(type, System.currentTimeMillis());
	payments.put(type, amount);
	this.lastAnnouced = 0L;
	this.Informed = false;
    }

    public PaymentData() {
	resetLimits();
    }

    public Long GetTime(CurrencyType type) {
	return paymentsTimes.get(type);
    }

    public void setReseted(boolean Reseted) {
	this.Reseted = Reseted;
    }

    public boolean isReseted() {
	return Reseted;
    }

    public Double GetAmount(CurrencyType type) {
	if (!payments.containsKey(type))
	    return 0D;
	return payments.get(type);
    }

    public Double GetAmountBylimit(CurrencyType type, int limit) {
	if (GetAmount(type) > limit)
	    return (double) limit;
	return (int) (GetAmount(type) * 100) / 100.0;
    }

    public Long GetLastAnnounced() {
	return lastAnnouced;
    }

    public boolean IsAnnounceTime(int t) {
	if (this.lastAnnouced + (t * 1000) > System.currentTimeMillis())
	    return false;
	SetAnnouncmentTime();
	return true;
    }

    public void SetAnnouncmentTime() {
	this.lastAnnouced = System.currentTimeMillis();
    }

    public void AddNewAmount(CurrencyType type, Double Payment) {
	AddNewAmount( type,  Payment, null);
    }

    public void AddNewAmount(CurrencyType type, Double Payment, Long time) {
	paymentsTimes.put(type, time == null ? System.currentTimeMillis() : time);
	payments.put(type, Payment);
    }

    public void setInformed() {
	this.Informed = true;
    }

    public void setNotInformed() {
	this.Informed = false;
    }

    public void AddAmount(CurrencyType type, Double Payment) {
	payments.put(type, payments.get(type) + Payment);
    }

    public long GetLeftTime(CurrencyType type) {
	long left = 0;
	if (this.GetTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) > System.currentTimeMillis())
	    left = (this.GetTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) - System.currentTimeMillis());
	return left;
    }

    public boolean IsOverLimit(CurrencyType type, int limit) {
	if (this.payments.get(type) < limit)
	    return false;
	return true;
    }

    public double percentOverLimit(CurrencyType type, int limit) {
	return ((this.payments.get(type) / limit) - 1) * 100;
    }

    public boolean IsOverTimeLimit(CurrencyType type) {
	if (this.GetTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) > System.currentTimeMillis())
	    return false;
	if (this.Informed)
	    this.Informed = false;
	resetLimits();
	return true;
    }

    public void resetLimits() {
	for (CurrencyType type : CurrencyType.values()) {
	    AddNewAmount(type, 0D);
	}
	this.Reseted = true;
    }

    public boolean IsReachedLimit(CurrencyType type, int money) {
	if (IsOverTimeLimit(type))
	    return true;
	if (IsOverLimit(type, money))
	    return true;
	return false;
    }

    public boolean isInformed() {
	return Informed;
    }

    public void setInformed(boolean Informed) {
	this.Informed = Informed;
    }
}
