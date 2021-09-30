package com.gamingmesh.jobs.container;

import java.time.Instant;

public class BoostCounter {
    private final CurrencyType type;
    private double boost;
    private Instant instant;

    public BoostCounter(CurrencyType type, double boost, Instant instant) {
        this.type = type;
        this.boost = boost;
        this.instant = instant;
    }

    public CurrencyType getType() {
	return type;
    }

    public Instant getTime() {
	return instant;
    }

    public double getBoost() {
	return boost;
    }

    public void setTime(Instant instant) {
	this.instant = instant;
    }

    public void setBoost(double boost) {
	this.boost = boost;
    }
}
