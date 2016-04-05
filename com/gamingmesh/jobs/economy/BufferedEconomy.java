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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.tasks.BufferedPaymentTask;

public class BufferedEconomy {
    private JobsPlugin plugin;
    private Economy economy;
    private LinkedBlockingQueue<BufferedPayment> payments = new LinkedBlockingQueue<BufferedPayment>();
    private final Map<UUID, BufferedPayment> paymentCache = Collections.synchronizedMap(new HashMap<UUID, BufferedPayment>());

    private OfflinePlayer ServerAccount = null;
    private OfflinePlayer ServerTaxesAccount = null;

    PaymentData PaymentData = new PaymentData();

    public BufferedEconomy(JobsPlugin plugin, Economy economy) {
	this.plugin = plugin;
	this.economy = economy;
    }

    /**
     * Add payment to player's payment buffer
     * @param player - player to be paid
     * @param amount - amount to be paid
     */
    public void pay(JobsPlayer player, double amount, double points, double exp) {
	pay(new BufferedPayment(player.getPlayer(), amount, points, exp));
    }

    /**
     * Add payment to player's payment buffer
     * @param payment - payment to be paid
     */
    public void pay(BufferedPayment payment) {
	payments.add(payment);
    }

    public String format(double money) {
	return economy.format(money);
    }

    /**
     * Payout all players the amount they are going to be paid
     */
    @SuppressWarnings("deprecation")
    public void payAll() {
	if (payments.isEmpty())
	    return;

	synchronized (paymentCache) {

	    Double TotalAmount = 0.0;
	    Double TotalPoints = 0.0;
	    Double TaxesAmount = 0.0;
	    Double TaxesPoints = 0.0;

	    // combine all payments using paymentCache
	    while (!payments.isEmpty()) {
		BufferedPayment payment = payments.remove();
		TotalAmount += payment.getAmount();
		TotalPoints += payment.getPoints();

		if (Jobs.getGCManager().UseTaxes) {
		    TaxesAmount += payment.getAmount() * (Jobs.getGCManager().TaxesAmount / 100.0);
		    TaxesPoints += payment.getPoints() * (Jobs.getGCManager().TaxesAmount / 100.0);
		}

		UUID uuid = payment.getOfflinePlayer().getUniqueId();
		if (paymentCache.containsKey(uuid)) {
		    BufferedPayment existing = paymentCache.get(uuid);

		    double money = payment.getAmount();
		    double points = payment.getPoints();
		    double exp = payment.getExp();

		    if (Jobs.getGCManager().TakeFromPlayersPayment && Jobs.getGCManager().UseTaxes) {
			money = money - (money * (Jobs.getGCManager().TaxesAmount / 100.0));
			points = points - (points * (Jobs.getGCManager().TaxesAmount / 100.0));
		    }

		    existing.setAmount(existing.getAmount() + money);
		    existing.setPoints(existing.getPoints() + points);
		    existing.setExp(existing.getExp() + exp);
		} else {

		    double money = payment.getAmount();
		    double points = payment.getPoints();

		    if (Jobs.getGCManager().TakeFromPlayersPayment && Jobs.getGCManager().UseTaxes) {
			payment.setAmount(money - (money * (Jobs.getGCManager().TaxesAmount / 100.0)));
			payment.setPoints(points - (points * (Jobs.getGCManager().TaxesAmount / 100.0)));
		    }

		    paymentCache.put(uuid, payment);
		}
	    }

	    boolean hasMoney = false;
	    String ServerAccountname = Jobs.getGCManager().ServerAcountName;
	    String ServerTaxesAccountname = Jobs.getGCManager().ServertaxesAcountName;
	    if (this.ServerAccount == null)
		this.ServerAccount = Bukkit.getOfflinePlayer(ServerAccountname);

	    if (this.ServerTaxesAccount == null)
		this.ServerTaxesAccount = Bukkit.getOfflinePlayer(ServerAccountname);

	    if (Jobs.getGCManager().UseTaxes && Jobs.getGCManager().TransferToServerAccount && ServerTaxesAccount != null) {

		economy.depositPlayer(ServerTaxesAccount, TaxesAmount);

		if (ServerTaxesAccount.isOnline()) {
		    if (!Jobs.getActionbarToggleList().containsKey(ServerTaxesAccountname) && Jobs.getGCManager().ActionBarsMessageByDefault)
			Jobs.getActionbarToggleList().put(ServerTaxesAccountname, true);
		    if (Jobs.getActionbarToggleList().containsKey(ServerTaxesAccountname) && Jobs.getActionbarToggleList().get(ServerTaxesAccountname)) {
			Jobs.getActionBar().send(Bukkit.getPlayer(ServerAccountname), Jobs.getLanguage().getMessage("message.taxes", "[amount]", (int) (TotalAmount * 100)
			    / 100.0));
		    }
		}
	    }

	    if (Jobs.getGCManager().UseServerAccount) {
		if (economy.hasMoney(ServerAccountname, TotalAmount)) {
		    hasMoney = true;
		    economy.withdrawPlayer(ServerAccountname, TotalAmount);
		}
	    }

	    // Schedule all payments
	    int i = 0;
	    for (BufferedPayment payment : paymentCache.values()) {
		i++;

		// JobsPayment event
		JobsPaymentEvent JobsPaymentEvent = new JobsPaymentEvent(payment.getOfflinePlayer(), payment.getAmount(), payment.getPoints());
		Bukkit.getServer().getPluginManager().callEvent(JobsPaymentEvent);
		// If event is canceled, dont do anything
		if (JobsPaymentEvent.isCancelled())
		    continue;

		if (Jobs.getGCManager().UseServerAccount) {
		    if (!hasMoney) {
			Jobs.getActionBar().send(payment.getOfflinePlayer().getPlayer(), ChatColor.RED + Jobs.getLanguage().getMessage("economy.error.nomoney"));
			continue;
		    } else {
			if (Jobs.getGCManager().isEconomyAsync())
			    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new BufferedPaymentTask(this, economy, payment), i);
			else
			    Bukkit.getScheduler().runTaskLater(plugin, new BufferedPaymentTask(this, economy, payment), i);
		    }
		} else {
		    if (Jobs.getGCManager().isEconomyAsync())
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new BufferedPaymentTask(this, economy, payment), i);
		    else

			Bukkit.getScheduler().runTaskLater(plugin, new BufferedPaymentTask(this, economy, payment), i);
		}
		try {
		    // Action bar stuff
		    Jobs.getActionBar().ShowActionBar(payment);
		    if (payment.getOfflinePlayer().isOnline()) {
			JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(payment.getOfflinePlayer().getName());
			Jobs.getBBManager().ShowJobProgression(jPlayer);
		    }
		} catch (Exception e) {
		}
	    }
	    // empty payment cache
	    paymentCache.clear();
	}

    }
}
