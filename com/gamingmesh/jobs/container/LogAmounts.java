package com.gamingmesh.jobs.container;

public final class LogAmounts {

    private String username;
    private String action;

    private String item;
    private int count = 0;
    private double money = 0.0;
    private double exp = 0.0;

    private boolean newEntry = true;

    public LogAmounts(String item) {
	this.item = item;
    }

    public boolean isNewEntry() {
	return this.newEntry;
    }

    public void setNewEntry(boolean state) {
	this.newEntry = state;
    }

    public String getItemName() {
	return this.item;
    }

    public void addMoney(Double amount) {
	this.money += amount;
    }

    public double getMoney() {
	return (int) (this.money * 100) / 100.0;
    }

    public void addExp(Double amount) {
	this.exp += amount;
    }

    public double getExp() {
	return (int) (this.exp * 100) / 100.0;
    }

    public void addCount() {
	this.count++;
    }

    public int getCount() {
	return this.count;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getUsername() {
	return this.username;
    }

    public void setAction(String action) {
	this.action = action;
    }

    public String getAction() {
	return this.action;
    }

}