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

package com.gamingmesh.jobs.tasks;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.gamingmesh.jobs.economy.Economy;

public class BufferedPaymentTask implements Runnable {

    private BufferedEconomy bufferedEconomy;
    private Economy economy;
    private BufferedPayment payment;

    public BufferedPaymentTask(BufferedEconomy bufferedEconomy, Economy economy, BufferedPayment payment) {
	this.bufferedEconomy = bufferedEconomy;
	this.economy = economy;
	this.payment = payment;
    }

    @Override
    public void run() {
	double money = payment.get(CurrencyType.MONEY);
	if (money > 0) {
	    if (Jobs.getGCManager().isEconomyAsync()) {
		org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(bufferedEconomy.getPlugin(), () -> economy.depositPlayer(payment.getOfflinePlayer(), money));
	    } else {
		economy.depositPlayer(payment.getOfflinePlayer(), money);
	    }
	} else if (!economy.withdrawPlayer(payment.getOfflinePlayer(), -money)) {
	    bufferedEconomy.pay(payment);
	}

	double points = payment.get(CurrencyType.POINTS);
	if (points != 0D)
	    Jobs.getPlayerManager().getJobsPlayer(payment.getOfflinePlayer().getUniqueId()).getPointsData().addPoints(points);
    }
}