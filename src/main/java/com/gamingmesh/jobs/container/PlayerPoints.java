package com.gamingmesh.jobs.container;

public class PlayerPoints {

	private double current = 0D;
	private double total = 0D;
	private boolean newEntry = false;

    public PlayerPoints() {
	newEntry = true;
    }

    public PlayerPoints(double points, double total) {
	if (points == 0D && total == 0D)
	    newEntry = true;
	this.current = points;
	this.total = total;
    }

    public void addPoints(double points) {
	current += points;
	total += points;
    }

    public void setPoints(double points) {
	current = points;
	total = points;
    }

    public void takePoints(double points) {
	current -= points;
    }

    public boolean havePoints(double points) {
	return current >= points;
    }

    public double getCurrentPoints() {
	return current;
    }

    public double getTotalPoints() {
	return total;
    }

    public boolean isNewEntry() {
	return newEntry;
    }

    public void setNewEntry(boolean newEntry) {
	this.newEntry = newEntry;
    }
}
