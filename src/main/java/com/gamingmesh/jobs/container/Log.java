package com.gamingmesh.jobs.container;

import java.util.HashMap;

import com.gamingmesh.jobs.stuff.TimeManage;

public final class Log {
    private String action;
    private int day;
    private HashMap<String, LogAmounts> amountMap = new HashMap<>();

    public Log(String action) {
	this.action = action;
	setDate();
    }

    public String getActionType() {
	return action;
    }

    public void add(String item, HashMap<CurrencyType, Double> amounts) {
	LogAmounts LAmount = amountMap.getOrDefault(item, new LogAmounts(item));
	LAmount.addCount();
	LAmount.add(amounts);
	this.amountMap.put(item, LAmount);
    }

    public void add(String item, int count, HashMap<CurrencyType, Double> amounts) {
	LogAmounts LAmount = amountMap.getOrDefault(item, new LogAmounts(item));
	LAmount.setCount(count);
	LAmount.add(amounts);
	LAmount.setNewEntry(false);
	this.amountMap.put(item, LAmount);
    }

    public void setDate() {
	this.day = TimeManage.timeInInt();
    }

    public int getDate() {
	return day;
    }

    public HashMap<String, LogAmounts> getAmountList() {
	return amountMap;
    }

    public int getCount(String item) {
	if (this.amountMap.containsKey(item))
	    return this.amountMap.get(item).getCount();
	return 0;
    }

    public double get(String item, CurrencyType type) {
	if (this.amountMap.containsKey(item))
	    return this.amountMap.get(item).get(type);
	return 0;
    }
}
