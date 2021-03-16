package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class LogAmounts {

    private String username;
    private String action;

    private String item;
    private int count = 0;

    private Map<CurrencyType, Double> amounts = new HashMap<>();

    private boolean newEntry = true;

    public LogAmounts(String item) {
	this.item = item;
    }

    public boolean isNewEntry() {
	return newEntry;
    }

    public void setNewEntry(boolean newEntry) {
	this.newEntry = newEntry;
    }

    public String getItemName() {
	return item;
    }

    public void add(Map<CurrencyType, Double> amounts) {
	for (Entry<CurrencyType, Double> one : amounts.entrySet()) {
	    add(one.getKey(), one.getValue());
	}
    }

    public void add(CurrencyType type, Double amount) {
	if (amount == null)
	    return;

	amounts.put(type, amounts.getOrDefault(type, 0D) + amount);
    }

    public double get(CurrencyType type) {
	return ((int) (amounts.getOrDefault(type, 0D) * 100D)) / 100D;
    }

    public void addCount() {
	this.count++;
    }

    public int getCount() {
	return count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

    public void setAction(String action) {
	this.action = action;
    }

    public String getAction() {
	return action;
    }

}
