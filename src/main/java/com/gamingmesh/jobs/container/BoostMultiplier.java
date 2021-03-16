package com.gamingmesh.jobs.container;

import java.util.HashMap;

public class BoostMultiplier implements Cloneable {

    private final java.util.Map<CurrencyType, Double> map = new HashMap<>();

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
	isValid(type); // Call without check to make sure map cache is removed
	return map.getOrDefault(type, 0D);
    }

    public Long getTime() {
	return time;
    }

    public boolean isValid(CurrencyType type) {
	boolean valid = time > System.currentTimeMillis();
	if (time != 0L && !valid) {
	    map.remove(type);
	    time = 0L;
	}

	return time == 0L || valid;
    }

    public void add(BoostMultiplier armorboost) {
	for (CurrencyType one : CurrencyType.values()) {
	    map.put(one, get(one) + armorboost.get(one));
	}
    }
}
