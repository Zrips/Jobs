package com.gamingmesh.jobs.container;

import java.util.Calendar;
import java.util.Date;
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

    public BoostMultiplier add(CurrencyType type, double amount, int hour, int minute, int second) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(new Date());

	cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hour);
	cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minute);
	cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + second);

	time = cal.getTimeInMillis();
	return add(type, amount);
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

	return map.get(type);
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
