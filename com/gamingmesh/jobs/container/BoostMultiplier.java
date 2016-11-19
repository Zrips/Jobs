package com.gamingmesh.jobs.container;

import java.util.HashMap;

public class BoostMultiplier {

    HashMap<BoostType, Double> map = new HashMap<BoostType, Double>();

    public BoostMultiplier() {
	for (BoostType one : BoostType.values()) {
	    map.put(one, 0D);
	}
    }

    public BoostMultiplier add(BoostType type, double amount) {
	map.put(type, amount);
	return this;
    }

    public BoostMultiplier add(double amount) {
	for (BoostType one : BoostType.values()) {
	    map.put(one, amount);
	}
	return this;
    }

    public double get(BoostType type) {
	if (!map.containsKey(type))
	    return 0D;
	return this.map.get(type);
    }

    public void add(BoostMultiplier armorboost) {
	for (BoostType one : BoostType.values()) {
	    double r = armorboost.get(one);
	    map.put(one, get(one) + r);
	}
    }
}
