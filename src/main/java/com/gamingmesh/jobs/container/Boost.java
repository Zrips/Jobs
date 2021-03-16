package com.gamingmesh.jobs.container;

import java.util.HashMap;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PlayerManager.BoostOf;

public class Boost {

    private java.util.Map<BoostOf, BoostMultiplier> map = new HashMap<>();

    public Boost() {
	for (BoostOf one : BoostOf.values()) {
	    map.put(one, new BoostMultiplier());
	}
    }

    public void add(BoostOf boostoff, BoostMultiplier multiplier) {
	map.put(boostoff, multiplier);
    }

    public BoostMultiplier get(BoostOf boostOf) {
	return map.getOrDefault(boostOf, new BoostMultiplier());
    }

    public double get(BoostOf boostOf, CurrencyType type) {
	return get(boostOf, type, false);
    }

    public double get(BoostOf boostOf, CurrencyType type, boolean percent) {
	if (!map.containsKey(boostOf))
	    return 0D;

	double r = map.get(boostOf).get(type);
	if (r < -1)
	    r = -1;

	return percent ? (int) (r * 100) : r;
    }

    public double getFinal(CurrencyType type) {
	return getFinal(type, false, false);
    }

    public double getFinalAmount(CurrencyType type, double income) {
	double f = income;

	if (income > 0 || income < 0 && Jobs.getGCManager().applyToNegativeIncome)
	    f = income + income * getFinal(type, false, false);

	if (income > 0 && f < 0 || income < 0 && f > 0)
	    f = 0;

	return f;
    }

    public double getFinal(CurrencyType type, boolean percent, boolean excludeExtra) {
	double r = 0D;

	for (BoostOf one : BoostOf.values()) {
	    if (!map.containsKey(one))
		continue;

	    if (excludeExtra && (one == BoostOf.NearSpawner || one == BoostOf.PetPay))
		continue;

	    BoostMultiplier bm = map.get(one);
	    if (bm.isValid(type))
		r += bm.get(type);
	}

	if (r < -1)
	    r = -1;

	return percent ? (int) (r * 100) : r;
    }
}
