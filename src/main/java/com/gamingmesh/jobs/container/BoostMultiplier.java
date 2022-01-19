package com.gamingmesh.jobs.container;

import java.util.HashMap;

public class BoostMultiplier implements Cloneable {

    private final java.util.Map<CurrencyType, Double> map = new HashMap<>();
    private final java.util.Map<CurrencyType, Long> timers = new HashMap<>();

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
	timers.put(type, time);
	return add(type, amount);
    }

    public BoostMultiplier add(double amount) {
	if (amount != 0) {
	    for (CurrencyType one : CurrencyType.values()) {
		map.put(one, amount);
	    }
	}
	return this;
    }

    public double get(CurrencyType type) {
	if (!isValid(type))
	    return 0D;
	return map.getOrDefault(type, 0D);
    }

    public Long getTime(CurrencyType type) {
	return timers.get(type);
    }

    public boolean isValid(CurrencyType type) {
	Long time = getTime(type);
	if (time == null)
	    return true;

	if (time < System.currentTimeMillis()) {
	    map.remove(type);
	    timers.remove(type);
	    return false;
	}

	return true;
    }

    public void add(BoostMultiplier armorboost) {
	for (CurrencyType one : CurrencyType.values()) {
	    map.put(one, get(one) + armorboost.get(one));
	}
    }
}
