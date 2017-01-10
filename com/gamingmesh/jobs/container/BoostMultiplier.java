package com.gamingmesh.jobs.container;

import java.util.HashMap;

public class BoostMultiplier {

    HashMap<CurrencyType, Double> map = new HashMap<CurrencyType, Double>();

    public BoostMultiplier() {
	for (CurrencyType one : CurrencyType.values()) {
	    map.put(one, 0D);
	}
    }

    public BoostMultiplier add(CurrencyType type, double amount) {
	map.put(type, amount);
	return this;
    }

    public BoostMultiplier add(double amount) {
	for (CurrencyType one : CurrencyType.values()) {
	    map.put(one, amount);
	}
	return this;
    }

    public double get(CurrencyType type) {
	if (!map.containsKey(type))
	    return 0D;
	return this.map.get(type);
    }

    public void add(BoostMultiplier armorboost) {
	for (CurrencyType one : CurrencyType.values()) {
	    double r = armorboost.get(one);
	    map.put(one, get(one) + r);
	}
    }
}
