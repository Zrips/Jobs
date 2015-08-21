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
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.ActionBar;
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
	public void pay(JobsPlayer player, double amount, double exp) {
		if (amount == 0)
			return;
		pay(new BufferedPayment(player.getPlayer(), amount, exp));
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
			Double TaxesAmount = 0.0;

			// combine all payments using paymentCache
			while (!payments.isEmpty()) {
				BufferedPayment payment = payments.remove();
				TotalAmount += payment.getAmount();

				if (ConfigManager.getJobsConfiguration().UseTaxes) {
					TaxesAmount += payment.getAmount() * (ConfigManager.getJobsConfiguration().TaxesAmount / 100.0);
				}

				UUID uuid = payment.getOfflinePlayer().getUniqueId();
				if (paymentCache.containsKey(uuid)) {
					BufferedPayment existing = paymentCache.get(uuid);

					double money = payment.getAmount();
					double exp = payment.getExp();

					if (ConfigManager.getJobsConfiguration().TakeFromPlayersPayment) {
						money = money - (money * (ConfigManager.getJobsConfiguration().TaxesAmount / 100.0));
					}

					existing.setAmount(existing.getAmount() + money);
					existing.setExp(existing.getExp() + exp);
				} else {
					
					double money = payment.getAmount();

					if (ConfigManager.getJobsConfiguration().TakeFromPlayersPayment) {
						payment.setAmount(money - (money * (ConfigManager.getJobsConfiguration().TaxesAmount / 100.0)));
					}
					
					paymentCache.put(uuid, payment);
				}
			}

			boolean hasMoney = false;
			String ServerAccountname = ConfigManager.getJobsConfiguration().ServerAcountName;
			String ServerTaxesAccountname = ConfigManager.getJobsConfiguration().ServertaxesAcountName;
			if (this.ServerAccount == null)
				this.ServerAccount = Bukkit.getOfflinePlayer(ServerAccountname);
			
			if (this.ServerTaxesAccount == null)
				this.ServerTaxesAccount = Bukkit.getOfflinePlayer(ServerAccountname);

			if (ConfigManager.getJobsConfiguration().UseTaxes && ConfigManager.getJobsConfiguration().TransferToServerAccount && ServerTaxesAccount != null) {

				economy.depositPlayer(ServerTaxesAccount, TaxesAmount);

				if (ServerTaxesAccount.isOnline()) {
					if (!Jobs.actionbartoggle.containsKey(ServerTaxesAccountname) && ConfigManager.getJobsConfiguration().JobsToggleEnabled)
						Jobs.actionbartoggle.put(ServerTaxesAccountname, true);
					if (Jobs.actionbartoggle.containsKey(ServerTaxesAccountname) && Jobs.actionbartoggle.get(ServerTaxesAccountname)) {
						ActionBar.send((Player) ServerTaxesAccount, Language.getMessage("message.taxes").replace("[amount]", String.valueOf((int) (TotalAmount * 100) / 100.0)));
					}
				}
			}

			if (ConfigManager.getJobsConfiguration().UseServerAccount) {
				if (economy.hasMoney(ServerAccountname, TotalAmount)) {
					hasMoney = true;
					economy.withdrawPlayer(ServerAccountname, TotalAmount);
				}
			}

			// Schedule all payments
			int i = 0;
			for (BufferedPayment payment : paymentCache.values()) {
				i++;

				// JobsJoin event
				JobsPaymentEvent JobsPaymentEvent = new JobsPaymentEvent(payment.getOfflinePlayer(), payment.getAmount());
				Bukkit.getServer().getPluginManager().callEvent(JobsPaymentEvent);
				// If event is canceled, dont do anything
				if (JobsPaymentEvent.isCancelled())
					continue;

				if (ConfigManager.getJobsConfiguration().isEconomyAsync()) {
					if (ConfigManager.getJobsConfiguration().UseServerAccount) {
						if (!hasMoney) {
							ActionBar.send(payment.getOfflinePlayer().getPlayer(), ChatColor.RED + Language.getMessage("economy.error.nomoney"));
							continue;
						} else
							Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new BufferedPaymentTask(this, economy, payment), i);
					} else
						Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new BufferedPaymentTask(this, economy, payment), i);

					// Action bar stuff
					ActionBar.ShowActionBar(payment);
				} else {
					if (ConfigManager.getJobsConfiguration().UseServerAccount) {
						if (!hasMoney) {
							ActionBar.send(payment.getOfflinePlayer().getPlayer(), ChatColor.RED + Language.getMessage("economy.error.nomoney"));
							continue;
						} else
							Bukkit.getScheduler().runTaskLater(plugin, new BufferedPaymentTask(this, economy, payment), i);
					} else
						Bukkit.getScheduler().runTaskLater(plugin, new BufferedPaymentTask(this, economy, payment), i);

					// Action bar stuff
					ActionBar.ShowActionBar(payment);
				}
			}
			// empty payment cache
			paymentCache.clear();
		}

	}
}
