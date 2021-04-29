package com.gamingmesh.jobs.economy;

import java.util.HashMap;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;

public class PaymentData {

    private Long lastAnnouced = 0L;

    private final java.util.Map<CurrencyType, LimitsData> payments = new HashMap<>();

    private boolean informed = false;

    public PaymentData(Long time, Double Payment, Double Points, Double Exp, Long lastAnnouced, boolean Informed) {
	payments.put(CurrencyType.EXP, new LimitsData(CurrencyType.EXP, time, Exp));
	payments.put(CurrencyType.MONEY, new LimitsData(CurrencyType.MONEY, time, Payment));
	payments.put(CurrencyType.POINTS, new LimitsData(CurrencyType.POINTS, time, Points));
	this.lastAnnouced = lastAnnouced;
	this.informed = Informed;
    }

    public PaymentData(CurrencyType type, Double amount) {
	for (CurrencyType one : CurrencyType.values()) {
	    if (one != type)
		payments.put(one, new LimitsData(one, System.currentTimeMillis(), 0D));
	}
	payments.put(type, new LimitsData(type, System.currentTimeMillis(), amount));
    }

    public PaymentData() {
	resetLimits();
    }

    public Long getTime(CurrencyType type) {
	return payments.get(type).getPaymentsTime();
    }

    public void setReseted(CurrencyType type, boolean reseted) {
	payments.get(type).setReseted(reseted);
    }

    public boolean isReseted(CurrencyType type) {
	return payments.get(type).isReseted();
    }

    public double getAmount(CurrencyType type) {
	if (type == null)
	    return 0D;

	LimitsData data = payments.get(type);
	return data == null ? 0D : (int) (data.getAmount() * 100) / 100D;
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
	payments.put(type, new LimitsData(type, time == null ? System.currentTimeMillis() : time, Payment));
    }

    public void addAmount(CurrencyType type, double payment) {
	payments.get(type).addAmount(payment);
    }

    public long getLeftTime(CurrencyType type) {
	long left = getTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000);
	return left > System.currentTimeMillis() ? left - System.currentTimeMillis() : 0L;
    }

    public boolean isOverLimit(CurrencyType type, int limit) {
	return payments.get(type).getAmount() >= limit;
    }

    public double percentOverLimit(CurrencyType type, int limit) {
	return ((payments.get(type).getAmount() / limit) - 1) * 100;
    }

    public boolean isOverTimeLimit(CurrencyType type) {
	if (getTime(type) + (Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000) > System.currentTimeMillis()) {
	    return false;
	}
	if (informed)
	    informed = false;
	resetLimits();
	return true;
    }

    public void resetLimits() {
	for (CurrencyType type : CurrencyType.values()) {
	    addNewAmount(type, 0D);
	    setReseted(type, true);
	}
    }

    public void resetLimits(CurrencyType type) {
	addNewAmount(type, 0D);
	setReseted(type, true);
    }

    public boolean isReachedLimit(CurrencyType type, int money) {
	isOverTimeLimit(type);
	return isOverLimit(type, money);
    }

    public boolean isInformed() {
	return informed;
    }

    public void setInformed(boolean informed) {
	this.informed = informed;
    }
}
