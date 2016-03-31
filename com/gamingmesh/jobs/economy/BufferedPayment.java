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

package com.gamingmesh.jobs.economy;

import org.bukkit.OfflinePlayer;

public class BufferedPayment {
    private OfflinePlayer offlinePlayer;
    private double amount = 0.0;
    private double points = 0.0;
    private double exp = 0.0;

    public BufferedPayment(OfflinePlayer offlinePlayer, double amount, double points, double exp) {
	this.offlinePlayer = offlinePlayer;
	this.amount = amount;
	this.points = points;
	this.exp = exp;
    }

    public OfflinePlayer getOfflinePlayer() {
	return offlinePlayer;
    }

    public double getAmount() {
	return amount;
    }
    
    public double getPoints() {
	return points;
    }

    public double getExp() {
	return exp;
    }

    public void setAmount(double amount) {
	this.amount = amount;
    }
    
    public void setPoints(double amount) {
	this.points = amount;
    }
    
    public void setExp(double exp) {
	this.exp = exp;
    }
}
