package com.gamingmesh.jobs.container;

import java.util.HashMap;

public class BoostMultiplier {

    private final HashMap<CurrencyType, Double> map = new HashMap<>();

    private Long time = 0L;

    @Override
    public BoostMultiplier clone() {
	BoostMultiplier boost = new BoostMultiplier();
	for (CurrencyType type : CurrencyType.values()) {
	    boost.add(type, map.get(type));
	}
	return boost;
    }

    public BoostMultiplier() {
	for (CurrencyType one : CurrencyType.values()) {
	    map.put(one, 0D);
	}
    }

    public BoostMultiplier add(CurrencyType type, double amount) {
	map.put(type, amount);
	return this;
    }

    public BoostMultiplier add(CurrencyType type, double amount, long time) {
	this.time = time;
	return add(type, amount);
    }

    public BoostMultiplier add(double amount) {
	for (CurrencyType one : CurrencyType.values()) {
	    map.put(one, amount);
	}

	return this;
    }

    public double get(CurrencyType type) {
	return map.getOrDefault(type, 0D);
    }

    public Long getTime() {
	return time;
    }

    public void setTime(Long time) {
	this.time = time;
    }

    public boolean isValid() {
	if (time == 0L) {
	    return true;
	}

	return time > System.currentTimeMillis();
    }

    public void add(BoostMultiplier armorboost) {
	for (CurrencyType one : CurrencyType.values()) {
	    double r = armorboost.get(one);
	    map.put(one, get(one) + r);
	}
    }
}
