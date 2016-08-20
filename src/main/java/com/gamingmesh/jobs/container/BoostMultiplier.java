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

	public double getMoneyBoost() {
		return this.money;
	}

	public double getPointsBoost() {
		return this.points;
	}

	public double getExpBoost() {
		return this.exp;
	}
}
