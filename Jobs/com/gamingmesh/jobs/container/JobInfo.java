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

import com.gamingmesh.jobs.resources.jfep.Parser;

public class JobInfo {
    private String name;
    private double baseIncome, baseXp;
    private Parser moneyEquation, xpEquation;
    public JobInfo(String name, double baseIncome, Parser moneyEquation, double baseXp, Parser xpEquation) {
        this.name = name;
        this.baseIncome = baseIncome;
        this.moneyEquation = moneyEquation;
        this.baseXp = baseXp;
        this.xpEquation = xpEquation;
    }
    
    public String getName() {
        return name;
    }
    
    public double getBaseIncome() {
        return baseIncome;
    }
    
    public double getBaseXp() {
        return baseXp;
    }
    
    public double getIncome(int level, int numjobs) {
        moneyEquation.setVariable("joblevel", level);
        moneyEquation.setVariable("numjobs", numjobs);
        moneyEquation.setVariable("baseincome", baseIncome);
        return moneyEquation.getValue();
    }
    
    public double getExperience(int level, int numjobs) {
        xpEquation.setVariable("joblevel", level);
        xpEquation.setVariable("numjobs", numjobs);
        xpEquation.setVariable("baseexperience", baseXp);
        return xpEquation.getValue();
    }
}
