package com.gamingmesh.jobs.container;

import java.util.HashMap;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PlayerManager.BoostOf;

public class Boost {

    private HashMap<BoostOf, BoostMultiplier> map = new HashMap<>();

    public Boost() {
	for (BoostOf one : BoostOf.values()) {
	    map.put(one, new BoostMultiplier());
	}
    }

    public void add(BoostOf boostoff, BoostMultiplier BM) {
	map.put(boostoff, BM);
    }

    public BoostMultiplier get(BoostOf boostoff) {
	return map.getOrDefault(boostoff, new BoostMultiplier());
    }

    public double get(BoostOf boostoff, CurrencyType BT) {
	return get(boostoff, BT, false);
    }

    public double get(BoostOf boostoff, CurrencyType BT, boolean percent) {
	if (!map.containsKey(boostoff))
	    return 0D;
	double r = map.get(boostoff).get(BT);
	if (r < -1)
	    r = -1;
	if (percent)
	    return (int) (r * 100);
	return r;
    }

    public double getFinal(CurrencyType BT) {
	return getFinal(BT, false, false);
    }

    public double getFinalAmount(CurrencyType BT, double income) {
	double f = income;

	if (income > 0 || income < 0 && Jobs.getGCManager().applyToNegativeIncome)
	    f = income + ((income > 0D ? income : -income) * getFinal(BT, false, false));

	if (income > 0 && f < 0 || income < 0 && f > 0)
	    f = 0;

	return f;
    }

    public double getFinal(CurrencyType BT, boolean percent, boolean excludeExtra) {
	double r = 0D;

	for (BoostOf one : BoostOf.values()) {
	    if (!map.containsKey(one))
		continue;

	    if (excludeExtra && (one == BoostOf.NearSpawner || one == BoostOf.PetPay))
		continue;

	    if (!map.get(one).isValid(BT))
		continue;

	    r += map.get(one).get(BT);
	}

	if (r < -1)
	    r = -1;

	return percent ? (int) (r * 100) : r;
    }
}
