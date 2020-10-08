package com.gamingmesh.jobs.economy;

import java.util.HashMap;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;

public class PaymentData {

    private Long lastAnnouced = 0L;

    private final HashMap<CurrencyType, Double> payments = new HashMap<>();
    private final HashMap<CurrencyType, Long> paymentsTimes = new HashMap<>();

    private boolean informed = false;
    private boolean reseted = false;

    public PaymentData(Long time, Double Payment, Double Points, Double Exp, Long lastAnnouced, boolean Informed) {
	paymentsTimes.put(CurrencyType.EXP, time);
	paymentsTimes.put(CurrencyType.MONEY, time);
	paymentsTimes.put(CurrencyType.POINTS, time);
	payments.put(CurrencyType.EXP, Exp);
	payments.put(CurrencyType.MONEY, Payment);
	payments.put(CurrencyType.POINTS, Points);

	this.lastAnnouced = lastAnnouced;
	this.informed = Informed;
    }

    public PaymentData(CurrencyType type, Double amount) {
	paymentsTimes.put(type, System.currentTimeMillis());
	payments.put(type, amount);

	this.lastAnnouced = 0L;
	this.informed = false;
    }

    public PaymentData() {
	resetLimits();
    }

    public Long getTime(CurrencyType type) {
	return paymentsTimes.get(type);
    }

    public void setReseted(boolean reseted) {
	this.reseted = reseted;
    }

    public boolean isReseted() {
	return reseted;
    }

    public Double getAmount(CurrencyType type) {
	return !payments.containsKey(type) ? 0D : (int) (payments.get(type) * 100) / 100D;
    }

    public Double getAmountBylimit(CurrencyType type, int limit) {
	return getAmount(type) > limit ? (double) limit : (int) (getAmount(type) * 100) / 100D;
    }

    public Long getLastAnnounced() {
	return lastAnnouced;
    }

    public boolean isAnnounceTime(int t) {
	if (lastAnnouced + (t * 1000) > System.currentTimeMillis())
	    return false;

	setAnnouncementTime();
	return true;
    }

    public void setAnnouncementTime() {
	this.lastAnnouced = System.currentTimeMillis();
    }

    public void addNewAmount(CurrencyType type, Double Payment) {
	addNewAmount(type, Payment, null);
    }

    public void addNewAmount(CurrencyType type, Double Payment, Long time) {
	paymentsTimes.put(type, time == null ? System.currentTimeMillis() : time);
	payments.put(type, Payment);
    }

    public void addAmount(CurrencyType type, Double Payment) {
	payments.put(type, payments.get(type) + Payment);
    }

    public long getLeftTime(CurrencyType type) {
	long left = 0;
	if (getTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) > System.currentTimeMillis())
	    left = (getTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) - System.currentTimeMillis());
	return left;
    }

    public boolean isOverLimit(CurrencyType type, int limit) {
	if (payments.get(type) < limit)
	    return false;
	return true;
    }

    public double percentOverLimit(CurrencyType type, int limit) {
	return ((payments.get(type) / limit) - 1) * 100;
    }

    public boolean isOverTimeLimit(CurrencyType type) {
	if (getTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) > System.currentTimeMillis())
	    return false;
	if (informed)
	    informed = false;
	resetLimits();
	return true;
    }

    public void resetLimits() {
	for (CurrencyType type : CurrencyType.values()) {
	    addNewAmount(type, 0D);
	}
	reseted = true;
    }

    public boolean isReachedLimit(CurrencyType type, int money) {
	return isOverTimeLimit(type) || isOverLimit(type, money);
    }

    public boolean isInformed() {
	return informed;
    }

    public void setInformed(boolean informed) {
	this.informed = informed;
    }
}
