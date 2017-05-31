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
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.tasks.BufferedPaymentTask;
import com.gamingmesh.jobs.util.ActionBar;
import com.gamingmesh.jobs.util.ChatColor;

public class BufferedEconomy {
	private JobsPlugin plugin;
	private Economy economy;
	private LinkedBlockingQueue<BufferedPayment> payments = new LinkedBlockingQueue<BufferedPayment>();
	private final Map<UUID, BufferedPayment> paymentCache = Collections.synchronizedMap(new HashMap<UUID, BufferedPayment>());

	public BufferedEconomy(JobsPlugin plugin, Economy economy) {
		this.plugin = plugin;
		this.economy = economy;
	}

	/**
	 * Add payment to player's payment buffer
	 * @param player - player to be paid
	 * @param amount - amount to be paid
	 */
	public void pay(JobsPlayer player, double amount) {
		if (amount == 0)
			return;
		@SuppressWarnings("deprecation")
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUserName());
		pay(new BufferedPayment(offlinePlayer, amount));
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
	public void payAll() {
		if (payments.isEmpty())
			return;

		synchronized (paymentCache) {
			// combine all payments using paymentCache
			while (!payments.isEmpty()) {
				BufferedPayment payment = payments.remove();
				if (paymentCache.containsKey(payment.getOfflinePlayer().getUniqueId())) {
					BufferedPayment existing = paymentCache.get(payment.getOfflinePlayer().getUniqueId());
					existing.setAmount(existing.getAmount() + payment.getAmount());
				} else {
					paymentCache.put(payment.getOfflinePlayer().getUniqueId(), payment);
				}
			}
			// Schedule all payments
			int i = 0;
			for (BufferedPayment payment : paymentCache.values()) {
				i++;
				if (ConfigManager.getJobsConfiguration().isEconomyAsync()) {
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new BufferedPaymentTask(this, economy, payment), i);
				} else {
					Bukkit.getScheduler().runTaskLater(plugin, new BufferedPaymentTask(this, economy, payment), i);
					if (Jobs.actionbartoggle.containsKey(payment.getOfflinePlayer().getPlayer().getName()))
						if (Jobs.actionbartoggle.get(payment.getOfflinePlayer().getPlayer().getName())){
							String Message = Language.getMessage("command.toggle.output.paid");
							Message = Message.replace("[amount]", String.valueOf((((int) (payment.getAmount() * 100)) / 100.0)));
							ActionBar.send(payment.getOfflinePlayer().getPlayer(), ChatColor.GREEN + Message);
						}
				}
			}
			// empty payment cache
			paymentCache.clear();
		}
	}
}
