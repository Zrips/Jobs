package com.gamingmesh.jobs.container;

import java.util.HashMap;

import com.gamingmesh.jobs.PlayerManager.BoostOf;

public class Boost {

    HashMap<BoostOf, BoostMultiplier> map = new HashMap<BoostOf, BoostMultiplier>();

    public Boost() {
	for (BoostOf one : BoostOf.values()) {
	    map.put(one, new BoostMultiplier());
	}
    }

    public void add(BoostOf boostoff, BoostMultiplier BM) {
	map.put(boostoff, BM);
    }

    public BoostMultiplier get(BoostOf boostoff) {
	if (!map.containsKey(boostoff))
	    return new BoostMultiplier();
	return map.get(boostoff);
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
	double f = income + ((income > 0D ? income : -income) * getFinal(BT, false, false));
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
	    r += map.get(one).get(BT);
	}
	if (r < -1)
	    r = -1;
	if (percent)
	    return (int) (r * 100);
	return r;
    }
}
