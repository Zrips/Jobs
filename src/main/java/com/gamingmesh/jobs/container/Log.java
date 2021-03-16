package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map;

import com.gamingmesh.jobs.stuff.TimeManage;

public final class Log {

    private String action;
    private int day;
    private Map<String, LogAmounts> amountMap = new HashMap<>();

    public Log(String action) {
	this.action = action;
	setDate();
    }

    public String getActionType() {
	return action;
    }

    public void add(String item, Map<CurrencyType, Double> amounts) {
	LogAmounts logAmount = amountMap.getOrDefault(item, new LogAmounts(item));
	logAmount.addCount();
	logAmount.add(amounts);
	amountMap.put(item, logAmount);
    }

    public void add(String item, int count, Map<CurrencyType, Double> amounts) {
	LogAmounts logAmount = amountMap.getOrDefault(item, new LogAmounts(item));
	logAmount.setCount(count);
	logAmount.add(amounts);
	logAmount.setNewEntry(false);
	amountMap.put(item, logAmount);
    }

    public void setDate() {
	this.day = TimeManage.timeInInt();
    }

    public int getDate() {
	return day;
    }

    public Map<String, LogAmounts> getAmountList() {
	return amountMap;
    }

    public int getCount(String item) {
	return amountMap.containsKey(item) ? amountMap.get(item).getCount() : 0;
    }

    public double get(String item, CurrencyType type) {
	return amountMap.containsKey(item) ? amountMap.get(item).get(type) : 0;
    }
}
