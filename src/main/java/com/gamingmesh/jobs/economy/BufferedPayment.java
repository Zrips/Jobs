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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import com.gamingmesh.jobs.container.CurrencyType;

public class BufferedPayment {

    private OfflinePlayer offlinePlayer;
    private final Map<CurrencyType, Double> payments = new HashMap<>();

    @Deprecated
    public BufferedPayment(OfflinePlayer offlinePlayer, double amount, double points, double exp) {
	this.offlinePlayer = offlinePlayer;
	this.payments.put(CurrencyType.MONEY, amount);
	this.payments.put(CurrencyType.EXP, exp);
	this.payments.put(CurrencyType.POINTS, points);
    }

    public BufferedPayment(OfflinePlayer offlinePlayer, Map<CurrencyType, Double> payments) {
	this.offlinePlayer = offlinePlayer;
	// This can contain only one value instead of all posible ones
	this.payments.putAll(payments);
    }

    public OfflinePlayer getOfflinePlayer() {
	return offlinePlayer;
    }

    @Deprecated
    public Double getAmount() {	
	Double amount = this.payments.get(CurrencyType.MONEY);
	return amount == null ? 0 : amount;
    }

    @Deprecated
    public double getPoints() {
	Double amount = this.payments.get(CurrencyType.POINTS);
	return amount == null ? 0 : amount;
    }

    @Deprecated
    public double getExp() {
	Double amount = this.payments.get(CurrencyType.EXP);
	return amount == null ? 0 : amount;
    }

    @Deprecated
    public void setAmount(double amount) {
	this.payments.put(CurrencyType.MONEY, amount);
    }

    @Deprecated
    public void setPoints(double points) {
	this.payments.put(CurrencyType.POINTS, points);
    }

    @Deprecated
    public void setExp(double exp) {
	this.payments.put(CurrencyType.EXP, exp);
    }

    public Double get(CurrencyType type) {
	return payments.getOrDefault(type, 0d);
    }

    public Double set(CurrencyType type, double amount) {
	return this.payments.put(type, amount);
    }

    public boolean containsPayment() {
	for (Double one : payments.values()) {
	    if (one != 0D)
		return true;
	}
	return false;
    }

    public Map<CurrencyType, Double> getPayment() {
	return payments;
    }
}
