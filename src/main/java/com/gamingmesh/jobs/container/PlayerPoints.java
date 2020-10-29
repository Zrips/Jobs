package com.gamingmesh.jobs.container;

public class PlayerPoints {

    private double current = 0D;
    private double total = 0D;
    private int dbId = 0;

    public PlayerPoints() {
    }

    public PlayerPoints(double points, double total) {
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

    public void setTotalPoints(double total) {
	this.total = total;
    }

    public int getDbId() {
	return dbId;
    }

    public void setDbId(int dbId) {
	this.dbId = dbId;
    }
}
