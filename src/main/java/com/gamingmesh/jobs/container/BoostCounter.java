package com.gamingmesh.jobs.container;

public class BoostCounter {
    CurrencyType type;
    double boost;
    Long calculatedon;

    public BoostCounter(CurrencyType type, double boost, Long calculatedon) {
	this.type = type;
	this.boost = boost;
	this.calculatedon = calculatedon;
    }

    public CurrencyType getType() {
	return this.type;
    }

    public long getTime() {
	return this.calculatedon;
    }

    public double getBoost() {
	return this.boost;
    }

    public void setTime(long time) {
	this.calculatedon = time;
    }

    public void setBoost(double boost) {
	this.boost = boost;
    }
}
