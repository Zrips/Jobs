package com.gamingmesh.jobs.container;

public class BoostMultiplier {
    double money;
    double points;
    double exp;

    public BoostMultiplier(double money, double points, double exp) {
	this.money = money;
	this.points = points;
	this.exp = exp;
    }

    public double getMoney() {
	return this.money;
    }

    public double getPoints() {
	return this.points;
    }

    public double getExp() {
	return this.exp;
    }
}
