package com.gamingmesh.jobs.container;

public class PlayerPoints {

    double current = 0D;
    double total = 0D;
    boolean newEntry = false;

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
	this.current += points;
	this.total += points;
    }
    
    public void setPoints(double points) {
	this.current = points;
	this.total = points;
    }
    
    public void takePoints(double points) {
	this.current -= points;
    }

    public boolean havePoints(double points) {
	return this.current >= points;
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
    
    public void setNewEntry(boolean state) {
	newEntry = state;
    }
}
