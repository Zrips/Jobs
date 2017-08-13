/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.container;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.resources.jfep.Parser;

public class JobInfo {
    private ActionType actionType;
    private int id;
    private String meta;
    private String name;
    private double baseIncome, baseXp, basePoints;
    private Parser moneyEquation, xpEquation, pointsEquation;
    private int fromLevel = 0;
    private int untilLevel = Integer.MAX_VALUE;
    
    private String configPath = "";

    public JobInfo(ActionType actionType, int id, String meta, String name, double baseIncome, Parser moneyEquation, double baseXp, Parser xpEquation,
	Parser pointsEquation, double basePoints, int fromLevel, int untilLevel, String configPath) {
	this.actionType = actionType;
	this.id = id;
	this.meta = meta;
	this.name = name;
	this.baseIncome = baseIncome;
	this.moneyEquation = moneyEquation;
	this.pointsEquation = pointsEquation;
	this.basePoints = basePoints;
	this.baseXp = baseXp;
	this.xpEquation = xpEquation;
	this.fromLevel = fromLevel;
	this.untilLevel = untilLevel;
	this.configPath = configPath;

    }

    public int getFromLevel() {
	return this.fromLevel;
    }

    public int getUntilLevel() {
	return this.untilLevel;
    }

    public boolean isInLevelRange(int level) {
	return level >= fromLevel && (level <= untilLevel || untilLevel == -1);
    }

    public String getName() {
	return this.name;
    }

    public int getId() {
	return this.id;
    }

    public ActionType getActionType() {
	return this.actionType;
    }

    public String getMeta() {
	return this.meta;
    }

    public double getBaseIncome() {
	return this.baseIncome;
    }

    public double getBaseXp() {
	return this.baseXp;
    }

    public double getBasePoints() {
	return this.basePoints;
    }

    public double getIncome(double level, double numjobs) {
	if (baseIncome == 0 || !Jobs.getGCManager().PaymentMethodsMoney)
	    return 0;
	moneyEquation.setVariable("joblevel", level);
	moneyEquation.setVariable("numjobs", numjobs);
	moneyEquation.setVariable("baseincome", baseIncome);
	return moneyEquation.getValue();
    }

    public double getExperience(double level, double numjobs) {
	if (baseXp == 0 || !Jobs.getGCManager().PaymentMethodsExp)
	    return 0;
	xpEquation.setVariable("joblevel", level);
	xpEquation.setVariable("numjobs", numjobs);
	xpEquation.setVariable("baseexperience", baseXp);
	return xpEquation.getValue();
    }

    public double getPoints(double level, double numjobs) {
	if (basePoints == 0 || !Jobs.getGCManager().PaymentMethodsPoints)
	    return 0;
	pointsEquation.setVariable("joblevel", level);
	pointsEquation.setVariable("numjobs", numjobs);
	pointsEquation.setVariable("basepoints", basePoints);
	return pointsEquation.getValue();
    }

    public String getConfigPath() {
	return configPath;
    }

    public void setConfigPath(String configPath) {
	this.configPath = configPath;
    }

    public void setBaseIncome(double baseIncome) {
	this.baseIncome = baseIncome;
    }

    public void setBaseXp(double baseXp) {
	this.baseXp = baseXp;
    }

    public void setBasePoints(double basePoints) {
	this.basePoints = basePoints;
    }
}
