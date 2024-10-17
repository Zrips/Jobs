package com.gamingmesh.jobs.economy;

import java.util.Calendar;
import java.util.Date;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyLimit;
import com.gamingmesh.jobs.container.CurrencyType;

public class LimitsData {
    private CurrencyType type = null;
    private double amount = 0D;
    private long paymentsTime = 0L;
    private long resetsAt = 0L;
    private boolean reseted = false;

    @Deprecated
    public LimitsData(CurrencyType type, long paymentsTime) {
        init(type, paymentsTime, 0D);
    }

    @Deprecated
    public LimitsData(CurrencyType type, long paymentsTime, double payment) {
        init(type, paymentsTime, payment);
    }

    public LimitsData(CurrencyType type, double payment) {
        init(type, System.currentTimeMillis(), payment);
    }

    private void init(CurrencyType type, long paymentsTime, double payment) {
        this.type = type;
        this.paymentsTime = paymentsTime;
        CurrencyLimit limit = Jobs.getGCManager().getLimit(type);
        if (limit.getResetsAt() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            resetsAt = limit.getResetsAt().toMili();
        } else
            resetsAt = (limit.getTimeLimit() * 100L) + System.currentTimeMillis();
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

    private static long day = (1000L * 60L * 60L * 24L);

    public void setPaymentsTime(long paymentsTime) {
        this.paymentsTime = paymentsTime;
        // If first payment time is over 24 hours, reset the amount
        if (this.paymentsTime + day < resetsAt) {
            setAmount(0);
            setReseted(true);
        }
    }

    public long getResetsAt() {
        return resetsAt;
    }

    public void setResetsAt(long resetsAt) {
        this.resetsAt = resetsAt;
    }

}
