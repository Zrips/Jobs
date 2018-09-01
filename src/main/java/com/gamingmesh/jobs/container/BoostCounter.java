package com.gamingmesh.jobs.container;

public class BoostCounter {
    private CurrencyType type;
    private double boost;
    private Long calculatedon;

    public BoostCounter(CurrencyType type, double boost, Long calculatedon) {
	this.type = type;
	this.boost = boost;
	this.calculatedon = calculatedon;
    }

    public CurrencyType getType() {
	return type;
    }

    public long getTime() {
	return calculatedon;
    }

    public double getBoost() {
	return boost;
    }

    public void setTime(long calculatedon) {
	this.calculatedon = calculatedon;
    }

    public void setBoost(double boost) {
	this.boost = boost;
    }
}
