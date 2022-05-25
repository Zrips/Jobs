package com.gamingmesh.jobs.container;

import java.util.HashMap;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.PlayerManager.BoostOf;

import net.Zrips.CMILib.Logs.CMIDebug;

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
	BoostMultiplier bm = map.get(boostOf);
	if (bm == null)
	    return 0D;

	double r = bm.get(type);
	if (r < -1)
	    r = -1;

	return percent ? (int) (r * 100) : r;
    }

    public double getFinal(CurrencyType type) {
	return getFinal(type, false, false);
    }

    public double getFinalAmount(CurrencyType type, double income) {
	double f = income;

	if (income > 0 || (income < 0 && Jobs.getGCManager().applyToNegativeIncome))
	    f = income + income * getFinal(type, false, false);

	if (income > 0 && f < 0 || income < 0 && f > 0)
	    f = 0;

	return f;
    }

    public double getFinal(CurrencyType type, boolean percent, boolean excludeExtra) {
	double r = 0D;

	for (BoostOf one : BoostOf.values()) {
	    BoostMultiplier bm = map.get(one);
	    if (bm == null)
		continue;

	    if (one == BoostOf.NearSpawner || one == BoostOf.PetPay)
		continue;

	    if (bm.isValid(type))
		r += bm.get(type);
	}

	if (!excludeExtra) {
	    if (Jobs.getGCManager().multiplyBoostedExtraValues) {
		BoostMultiplier bm = map.get(BoostOf.NearSpawner);
		if (bm != null && bm.isValid(type) && bm.get(type) != 0) {
		    r = (r + 1) * (bm.get(type) + 1);
		    r -= 1;
		}

		bm = map.get(BoostOf.PetPay);
		if (bm != null && bm.isValid(type) && bm.get(type) != 0) {
		    r = (r + 1) * (bm.get(type) + 1);
		    r -= 1;
		}
	    } else {
		BoostMultiplier bm = map.get(BoostOf.NearSpawner);
		if (bm != null && bm.isValid(type)) {
		    r += bm.get(type);
		}
		bm = map.get(BoostOf.PetPay);
		if (bm != null && bm.isValid(type)) {
		    r += bm.get(type);
		}
	    }
	}

	if (r < -1)
	    r = -1;

	return percent ? (int) (r * 100) : r;
    }
}
