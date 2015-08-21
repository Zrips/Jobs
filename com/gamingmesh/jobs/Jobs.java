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

package com.gamingmesh.jobs;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.economy.Economy;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.JobsClassLoader;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;

public class Jobs {
	public static Jobs instance = new Jobs();
	private static PlayerManager pManager = new PlayerManager();

	private static Logger pLogger;
	private static File dataFolder;
	private static JobsClassLoader classLoader = new JobsClassLoader(instance);
	private static JobsDAO dao = null;
	private static List<Job> jobs = null;
	private static Job noneJob = null;
	private static WeakHashMap<Job, Integer> usedSlots = new WeakHashMap<Job, Integer>();
	public static WeakHashMap<String, Boolean> actionbartoggle = new WeakHashMap<String, Boolean>();
//	public static WeakHashMap<String, Double> GlobalBoost = new WeakHashMap<String, Double>();
	private static BufferedEconomy economy;
	private static PermissionHandler permissionHandler;

	public static BufferedPaymentThread paymentThread = null;
	private static DatabaseSaveThread saveTask = null;

	public final static HashMap<String, PaymentData> paymentLimit = new HashMap<String, PaymentData>();
	public final static HashMap<String, PaymentData> ExpLimit = new HashMap<String, PaymentData>();

	private Jobs() {
	}

	/**
	 * Returns player manager
	 * @return the player manager
	 */
	public static PlayerManager getPlayerManager() {
		return pManager;
	}

	/**
	 * Sets the plugin logger
	 */
	public static void setPluginLogger(Logger logger) {
		pLogger = logger;
	}

	/**
	 * Retrieves the plugin logger
	 * @return the plugin logger
	 */
	public static Logger getPluginLogger() {
		return pLogger;
	}

	/**
	 * Sets the data folder
	 * @param dir - the data folder
	 */
	public static void setDataFolder(File dir) {
		dataFolder = dir;
	}

	/**
	 * Retrieves the data folder
	 * @return data folder
	 */
	public static File getDataFolder() {
		return dataFolder;
	}

	/**
	 * Sets the Data Access Object
	 * @param dao - the DAO
	 */
	public static void setDAO(JobsDAO value) {
		dao = value;
	}

	/**
	 * Get the Data Access Object
	 * @return the DAO
	 */
	public static JobsDAO getJobsDAO() {
		return dao;
	}

	/**
	 * Sets the list of jobs
	 * @param jobs - list of jobs
	 */
	public static void setJobs(List<Job> list) {
		jobs = list;
	}

	/**
	 * Retrieves the list of active jobs
	 * @return list of jobs
	 */
	public static List<Job> getJobs() {
		return Collections.unmodifiableList(jobs);
	}

	/**
	 * Sets the none job
	 * @param noneJob - the none job
	 */
	public static void setNoneJob(Job job) {
		noneJob = job;
	}

	/**
	 * Retrieves the "none" job
	 * @return the none job
	 */
	public static Job getNoneJob() {
		return noneJob;
	}

	/**
	 * Function to return the job information that matches the jobName given
	 * @param jobName - the ame of the job given
	 * @return the job that matches the name
	 */
	public static Job getJob(String jobName) {
		for (Job job : jobs) {
			if (job.getName().equalsIgnoreCase(jobName))
				return job;
		}
		return null;
	}

	/**
	 * Executes startup
	 * @throws IOException 
	 */
	public static void startup() throws IOException {
		reload();
		// add all online players
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			Jobs.getPlayerManager().playerJoin(online);
		}
	}

	/**
	 * Reloads all data
	 * @throws IOException 
	 */
	public static void reload() throws IOException {
		if (saveTask != null) {
			saveTask.shutdown();
			saveTask = null;
		}

		if (paymentThread != null) {
			paymentThread.shutdown();
			paymentThread = null;
		}

		if (dao != null) {
			dao.closeConnections();
		}

		ConfigManager.getJobsConfiguration().reload();
		Language.reload(ConfigManager.getJobsConfiguration().getLocale());
		ConfigManager.getJobConfig().reload();
		usedSlots.clear();
		for (Job job : jobs) {
			usedSlots.put(job, getJobsDAO().getSlotsTaken(job));
		}
		pManager.reload();
		permissionHandler.registerPermissions();

		// set the system to auto save
		if (ConfigManager.getJobsConfiguration().getSavePeriod() > 0) {
			saveTask = new DatabaseSaveThread(ConfigManager.getJobsConfiguration().getSavePeriod());
			saveTask.start();
		}

		// schedule payouts to buffered payments
		paymentThread = new BufferedPaymentThread(ConfigManager.getJobsConfiguration().getEconomyBatchDelay());
		paymentThread.start();
		
		ConfigManager.getJobsConfiguration().loadScheduler();
	}

	/**
	 * Executes clean shutdown
	 */
	public static void shutdown() {
		if (saveTask != null)
			saveTask.shutdown();

		if (paymentThread != null)
			paymentThread.shutdown();

		pManager.saveAll();

		if (dao != null) {
			dao.closeConnections();
		}
	}

	/**
	 * Executes close connections
	 */
	public static void ChangeDatabase() {
		if (dao != null) {
			dao.closeConnections();
		}
		if (ConfigManager.getJobsConfiguration().storageMethod.equals("mysql"))
			ConfigManager.getJobsConfiguration().startSqlite();
		else
			ConfigManager.getJobsConfiguration().startMysql();
		pManager.reload();
	}

	/**
	 * Function to get the number of slots used on the server for this job
	 * @param job - the job
	 * @return the number of slots
	 */
	public static int getUsedSlots(Job job) {
		return usedSlots.get(job);
	}

	/**
	 * Function to increase the number of used slots for a job
	 * @param job - the job someone is taking
	 */
	public static void takeSlot(Job job) {
		usedSlots.put(job, usedSlots.get(job) + 1);
	}

	/**
	 * Function to decrease the number of used slots for a job
	 * @param job - the job someone is leaving
	 */
	public static void leaveSlot(Job job) {
		usedSlots.put(job, usedSlots.get(job) - 1);
	}

	/**
	 * Returns the jobs classloader
	 * @return the classloader
	 */
	public static JobsClassLoader getJobsClassloader() {
		return classLoader;
	}

	/**
	 * Sets the permission handler
	 * @param h - the permission handler
	 */
	public static void setPermissionHandler(PermissionHandler h) {
		permissionHandler = h;
	}

	/**
	 * Gets the permission handler
	 * @return the permission handler
	 */
	public static PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	/**
	 * Sets the economy handler
	 * @param eco - the economy handler
	 */
	public static void setEconomy(JobsPlugin plugin, Economy eco) {
		economy = new BufferedEconomy(plugin, eco);
	}

	/**
	 * Gets the economy handler
	 * @return the economy handler
	 */
	public static BufferedEconomy getEconomy() {
		return economy;
	}

	public static boolean isUnderLimit(OfflinePlayer player, Double amount) {

		if (player == null)
			return false;

		String playername = player.getName();

		if (!ConfigManager.getJobsConfiguration().EconomyLimitUse)
			return true;

		if (!paymentLimit.containsKey(playername)) {
			PaymentData data = new PaymentData(System.currentTimeMillis(), amount, 0L, false);
			//data.AddNewAmount(amount);
			paymentLimit.put(playername, data);
		} else {
			PaymentData data = paymentLimit.get(playername);
			if (data.IsReachedLimit(ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit, ConfigManager.getJobsConfiguration().EconomyLimitMoneyLimit)) {
				if (player.isOnline() && !data.Informed) {
					((Player) player).sendMessage(Language.getMessage("command.limit.output.reachedlimit"));
					((Player) player).sendMessage(Language.getMessage("command.limit.output.reachedlimit2"));
					data.Setinformed();
				}
				if (data.IsAnnounceTime(ConfigManager.getJobsConfiguration().EconomyLimitAnnouncmentDelay) && player.isOnline()) {
					String message = Language.getMessage("command.limit.output.lefttime").replace("%hour%", String.valueOf(data.GetLeftHour(ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit)));
					message = message.replace("%min%", String.valueOf(data.GetLeftMin(ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit)));
					message = message.replace("%sec%", String.valueOf(data.GetLeftsec(ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit)));
					ActionBar.send(((Player) player), ChatColor.RED + message);
				}
				return false;
			} else
				data.AddAmount(amount);
			paymentLimit.put(playername, data);
		}
		return true;
	}

	public static boolean isUnderExpLimit(final OfflinePlayer player, Double amount) {
		if (player == null)
			return false;

		String playername = player.getName();

		if (!ConfigManager.getJobsConfiguration().EconomyExpLimitUse)
			return true;

		if (!ExpLimit.containsKey(playername)) {
			PaymentData data = new PaymentData();
			data.AddNewAmount(amount);
			ExpLimit.put(playername, data);
		} else {

			final PaymentData data = ExpLimit.get(playername);
			if (data.IsReachedLimit(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit, ConfigManager.getJobsConfiguration().EconomyExpLimit)) {
				if (player.isOnline() && !data.Informed) {
					((Player) player).sendMessage(Language.getMessage("command.limit.output.reachedExplimit"));
					((Player) player).sendMessage(Language.getMessage("command.limit.output.reachedExplimit2"));
					data.Setinformed();
				}
				Bukkit.getScheduler().runTaskAsynchronously(JobsPlugin.instance, new Runnable() {
					@Override
					public void run() {
						if (data.IsAnnounceTime(ConfigManager.getJobsConfiguration().EconomyLimitAnnouncmentExpDelay) && player.isOnline()) {
							String message = Language.getMessage("command.limit.output.lefttime").replace("%hour%", String.valueOf(data.GetLeftHour(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit)));
							message = message.replace("%min%", String.valueOf(data.GetLeftMin(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit)));
							message = message.replace("%sec%", String.valueOf(data.GetLeftsec(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit)));
							ActionBar.send(((Player) player), ChatColor.RED + message);
						}
					}
				});
				return false;
			} else
				data.AddAmount(amount);
			ExpLimit.put(playername, data);
		}
		return true;
	}

	/**
	 * Performed an action
	 * 
	 * Give correct experience and income
	 * @param jPlayer - the player
	 * @param action - the action
	 * @param multiplier - the payment/xp multiplier
	 */
	@SuppressWarnings("deprecation")
	public static void action(JobsPlayer jPlayer, ActionInfo info, double multiplier, ItemStack item, ItemStack[] armor) {

		if (jPlayer == null)
			return;

		List<JobProgression> progression = jPlayer.getJobProgression();
		int numjobs = progression.size();
		// no job

		if (numjobs == 0) {
			Job jobNone = Jobs.getNoneJob();
			Player dude = Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID());

			if (jobNone != null) {
				Double income = jobNone.getIncome(info, 1, numjobs);
				if (income != null) {

					
					Double amount = income + ((income * multiplier) - income) + ((income * 1.0) - income) + ((income * Jobs.getNoneJob().getMoneyBoost()) - income);
					
					if (ConfigManager.getJobsConfiguration().useDynamicPayment) {
						double moneyBonus = (income * (jobNone.getBonus() / 100));
						amount += moneyBonus;
					}
					
					if (!isUnderLimit(dude, amount))
						return;

					Jobs.getEconomy().pay(jPlayer, amount, 0.0);

				}
			}
		} else {

			for (JobProgression prog : progression) {

				int level = prog.getLevel();
				Double income = prog.getJob().getIncome(info, level, numjobs);

				if (income != null) {
					Double exp = prog.getJob().getExperience(info, level, numjobs);

					if (ConfigManager.getJobsConfiguration().addXpPlayer()) {
						Player player = Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID());
						if (player != null) {
							/*
							 * Minecraft experience is calculated in whole numbers only.
							 * Calculate the fraction of an experience point and perform a dice roll.
							 * That way jobs that give fractions of experience points will slowly give
							 * experience in the aggregate
							 */
							int expInt = exp.intValue();
							double remainder = exp.doubleValue() - expInt;
							if (Math.abs(remainder) > Math.random()) {
								if (exp.doubleValue() < 0) {
									expInt--;
								} else {
									expInt++;
								}
							}
							player.giveExp(expInt);
						}
					}

					// Item boost check
					Double itemMoneyBoost = 0.0;
					Double itemExpBoost = 0.0;
					if (item != null)
						if (item.hasItemMeta()) {
							ItemMeta meta = item.getItemMeta();
							if (meta.hasDisplayName() && meta.hasLore())
								for (JobItems oneItem : prog.getJob().getItems()) {
									if (oneItem.getId() != item.getTypeId())
										continue;
									if (!ChatColor.translateAlternateColorCodes('&', oneItem.getName()).equalsIgnoreCase(meta.getDisplayName()))
										continue;
									if (!oneItem.getLore().equals(meta.getLore()))
										continue;
									itemMoneyBoost = ((income * oneItem.getMoneyBoost()) - income);
									itemExpBoost = ((exp * oneItem.getExpBoost()) - exp);
									break;
								}
						}

					// Armor boost check
					Double armorMoneyBoost = 0.0;
					Double armorExpBoost = 0.0;
					if (armor != null)
						for (ItemStack OneArmor : armor) {
							if (OneArmor == null)
								continue;
							if (!OneArmor.hasItemMeta())
								continue;
							ItemMeta meta = OneArmor.getItemMeta();
							if (!meta.hasDisplayName() || !meta.hasLore())
								continue;
							for (JobItems oneItem : prog.getJob().getItems()) {
								if (oneItem.getId() != OneArmor.getTypeId())
									continue;
								if (!ChatColor.translateAlternateColorCodes('&', oneItem.getName()).equalsIgnoreCase(meta.getDisplayName()))
									continue;
								if (!oneItem.getLore().equals(meta.getLore()))
									continue;
								armorMoneyBoost += ((income * oneItem.getMoneyBoost()) - income);
								armorExpBoost += ((exp * oneItem.getExpBoost()) - exp);
								break;

							}

						}

					OfflinePlayer dude = jPlayer.getPlayer();

					// Calculate income
					Double amount = income + ((income * multiplier) - income) + ((income * prog.getJob().getMoneyBoost()) - income) + ((income * prog.getMoneyBoost()) - income) + itemMoneyBoost + armorMoneyBoost;
					
					if (ConfigManager.getJobsConfiguration().useDynamicPayment) {					
						double moneyBonus = (income * (prog.getJob().getBonus() / 100));
						amount += moneyBonus;
					}
					
					// Calculate exp
					double expAmount = exp + ((exp * multiplier) - exp) + ((exp * prog.getJob().getExpBoost()) - exp) + ((exp * prog.getExpBoost()) - exp) + itemExpBoost + armorExpBoost;
					
					if (ConfigManager.getJobsConfiguration().useDynamicPayment) {					
						double expBonus = (exp * (prog.getJob().getBonus() / 100));
						expAmount += expBonus;
					}
					
					if (!isUnderLimit(dude, amount)) {
						amount = 0.0000000001;
						if (ConfigManager.getJobsConfiguration().EconomyExpStop)
							expAmount = 0.0;
					}

					if (!isUnderExpLimit(dude, expAmount)) {
						expAmount = 0.0;
						if (ConfigManager.getJobsConfiguration().EconomyMoneyStop)
							expAmount = 0.0000000001;
					}

					if (amount == 0.0000000001 && expAmount == 0.0)
						continue;

					Jobs.getEconomy().pay(jPlayer, amount, expAmount);
					int oldLevel = prog.getLevel();

					if (prog.addExperience(expAmount))
						Jobs.getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);

				}
			}
		}
	}
}
