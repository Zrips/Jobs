package com.gamingmesh.jobs.container;

import java.util.HashMap;

import com.gamingmesh.jobs.stuff.TimeManage;

public final class Log {
    private String action;
    private int day;
    private HashMap<String, LogAmounts> amountMap = new HashMap<String, LogAmounts>();

    public Log(String action) {
	this.action = action;
	setDate();
    }

    public String getActionType() {
	return this.action;
    }

    public void add(String item, double money, double exp) {
	if (!this.amountMap.containsKey(item)) {
	    LogAmounts LAmount = new LogAmounts(item);
	    LAmount.addCount();
	    LAmount.addMoney(money);
	    LAmount.addExp(exp);
	    this.amountMap.put(item, LAmount);
	} else {
	    LogAmounts LAmount = this.amountMap.get(item);
	    LAmount.addCount();
	    LAmount.addMoney(money);
	    LAmount.addExp(exp);
	    this.amountMap.put(item, LAmount);
	}
    }

    public void add(String item, int count, double money, double exp) {
	if (!this.amountMap.containsKey(item)) {
	    LogAmounts LAmount = new LogAmounts(item);
	    LAmount.setCount(count);
	    LAmount.setNewEntry(false);
	    LAmount.addMoney(money);
	    LAmount.addExp(exp);
	    this.amountMap.put(item, LAmount);
	} else {
	    LogAmounts LAmount = this.amountMap.get(item);
	    LAmount.setCount(count);
	    LAmount.setNewEntry(false);
	    LAmount.addMoney(money);
	    LAmount.addExp(exp);
	    this.amountMap.put(item, LAmount);
	}
    }

    public void setDate() {
	this.day = TimeManage.timeInInt();
    }

    public int getDate() {
	return this.day;
    }

    public HashMap<String, LogAmounts> getAmountList() {
	return this.amountMap;
    }

    public int getCount(String item) {
	if (this.amountMap.containsKey(item))
	    return this.amountMap.get(item).getCount();
	return 0;
    }

    public double getMoney(String item) {
	if (this.amountMap.containsKey(item))
	    return this.amountMap.get(item).getMoney();
	return 0;
    }

    public double getExp(String item) {
	if (this.amountMap.containsKey(item))
	    return this.amountMap.get(item).getExp();
	return 0;
    }
}