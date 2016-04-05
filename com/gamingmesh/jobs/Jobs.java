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
import com.gamingmesh.jobs.Gui.GuiManager;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.config.BossBarManager;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.config.ExploreManager;
import com.gamingmesh.jobs.config.GeneralConfigManager;
import com.gamingmesh.jobs.config.LanguageManager;
import com.gamingmesh.jobs.config.NameTranslatorManager;
import com.gamingmesh.jobs.config.RestrictedAreaManager;
import com.gamingmesh.jobs.config.RestrictedBlockManager;
import com.gamingmesh.jobs.config.ScboardManager;
import com.gamingmesh.jobs.config.ScheduleManager;
import com.gamingmesh.jobs.config.ShopManager;
import com.gamingmesh.jobs.config.TitleManager;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.economy.Economy;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.listeners.MythicMobsListener;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.JobsClassLoader;
import com.gamingmesh.jobs.stuff.Loging;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;

import net.coreprotect.CoreProtectAPI;

public class Jobs {
    public static Jobs instance = new Jobs();
//    public static JobsPlugin plugin = new JobsPlugin();
    private static PlayerManager pManager = new PlayerManager();
    private static JobsCommands cManager = null;
    private static Language lManager = null;
    private static LanguageManager lmManager = null;
    private static SignUtil signManager = null;
    private static ScboardManager scboardManager = null;
    private static ScheduleManager scheduleManager = null;
    private static NameTranslatorManager NameTranslatorManager = null;
    private static GuiManager GUIManager = null;
    private static ExploreManager exploreManager = null;
    private static TitleManager titleManager = null;
    private static RestrictedBlockManager RBManager = null;
    private static RestrictedAreaManager RAManager = null;
    private static BossBarManager BBManager;
    private static ShopManager shopManager;

    private static MythicMobsListener MythicManager;

    private static ConfigManager configManager;
    private static GeneralConfigManager GconfigManager;

    private static Logger pLogger;
    private static File dataFolder;
    private static JobsClassLoader classLoader = new JobsClassLoader(instance);
    private static JobsDAO dao = null;
    private static List<Job> jobs = null;
    private static Job noneJob = null;
    private static WeakHashMap<Job, Integer> usedSlots = new WeakHashMap<Job, Integer>();
    public static WeakHashMap<String, Boolean> actionbartoggle = new WeakHashMap<String, Boolean>();
    public static WeakHashMap<String, Boolean> BossBartoggle = new WeakHashMap<String, Boolean>();
//	public static WeakHashMap<String, Double> GlobalBoost = new WeakHashMap<String, Double>();
    private static BufferedEconomy economy;
    private static PermissionHandler permissionHandler;

    public static BufferedPaymentThread paymentThread = null;
    private static DatabaseSaveThread saveTask = null;

    public final static HashMap<String, PaymentData> paymentLimit = new HashMap<String, PaymentData>();
    public final static HashMap<String, PaymentData> ExpLimit = new HashMap<String, PaymentData>();
    public final static HashMap<String, PaymentData> PointLimit = new HashMap<String, PaymentData>();

    private static NMS nms;

    private static ActionBar actionbar;

    private static CoreProtectAPI CPAPI = null;

    public static CoreProtectAPI getCoreProtectApi() {
	return CPAPI;
    }

    public static void setCoreProtectApi(CoreProtectAPI capi) {
	CPAPI = capi;
    }

    public static void setMythicManager(JobsPlugin plugin) {
	MythicManager = new MythicMobsListener(plugin);
    }

    public static MythicMobsListener getMythicManager() {
	return MythicManager;
    }

    public static void setShopManager(JobsPlugin plugin) {
	shopManager = new ShopManager(plugin);
    }

    public static ShopManager getShopManager() {
	return shopManager;
    }

    public static void setConfigManager(JobsPlugin plugin) {
	configManager = new ConfigManager(plugin);
    }

    public static ConfigManager getConfigManager() {
	return configManager;
    }

    public static void setGCManager(JobsPlugin plugin) {
	GconfigManager = new GeneralConfigManager(plugin);
    }

    public static GeneralConfigManager getGCManager() {
	return GconfigManager;
    }

    public static void setActionBar(ActionBar bar) {
	actionbar = bar;
    }

    public static ActionBar getActionBar() {
	return actionbar;
    }

    public static void setNms(NMS newNms) {
	nms = newNms;
    }

    public static NMS getNms() {
	return nms;
    }

    /**
     * Returns player manager
     * @return the player manager
     */
    public static PlayerManager getPlayerManager() {
	return pManager;
    }

    public static void setRestrictedBlockManager(JobsPlugin plugin) {
	RBManager = new RestrictedBlockManager(plugin);
    }

    public static RestrictedBlockManager getRestrictedBlockManager() {
	return RBManager;
    }

    public static void setRestrictedAreaManager(JobsPlugin plugin) {
	RAManager = new RestrictedAreaManager(plugin);
    }

    public static RestrictedAreaManager getRestrictedAreaManager() {
	return RAManager;
    }

    public static void setTitleManager(JobsPlugin plugin) {
	titleManager = new TitleManager(plugin);
    }

    public static TitleManager gettitleManager() {
	return titleManager;
    }

    public static void setBBManager(JobsPlugin plugin) {
	BBManager = new BossBarManager(plugin);
    }

    public static BossBarManager getBBManager() {
	return BBManager;
    }

    public static WeakHashMap<String, Boolean> getActionbarToggleList() {
	return actionbartoggle;
    }

    public static WeakHashMap<String, Boolean> getBossBarToggleList() {
	return BossBartoggle;
    }

    /**
     * Returns schedule manager
     * @return the schedule manager
     */
    public static ScheduleManager getScheduleManager() {
	return scheduleManager;
    }

    public static void setScheduleManager(JobsPlugin plugin) {
	scheduleManager = new ScheduleManager(plugin);
    }

    public static NameTranslatorManager getNameTranslatorManager() {
	return NameTranslatorManager;
    }

    public static void setNameTranslatorManager(JobsPlugin plugin) {
	NameTranslatorManager = new NameTranslatorManager(plugin);
    }

    public static GuiManager getGUIManager() {
	return GUIManager;
    }

    public static void setGUIManager(JobsPlugin plugin) {
	GUIManager = new GuiManager();
    }

    public static JobsCommands getCommandManager() {
	return cManager;
    }

    public static void setCommandManager(JobsPlugin plugin) {
	cManager = new JobsCommands(plugin);
    }

    public static ExploreManager getExplore() {
	return exploreManager;
    }

    public static void setExplore() {
	exploreManager = new ExploreManager();
    }

    /**
     * Returns scoreboard manager
     * @return the scoreboard manager
     */
    public static ScboardManager getScboard() {
	return scboardManager;
    }

    public static void setScboard(JobsPlugin plugin) {
	scboardManager = new ScboardManager(plugin);
    }

    /**
     * Returns sign manager
     * @return the sign manager
     */
    public static SignUtil getSignUtil() {
	return signManager;
    }

    public static void setSignUtil(JobsPlugin plugin) {
	signManager = new SignUtil(plugin);
    }

    /**
     * Returns language manager
     * @return the language manager
     */
    public static Language getLanguage() {
	return lManager;
    }

    public static void setLanguage(JobsPlugin plugin) {
	lManager = new Language(plugin);
    }

    public static LanguageManager getLanguageManager() {
	return lmManager;
    }

    public static void setLanguageManager(JobsPlugin plugin) {
	lmManager = new LanguageManager(plugin);
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

	Jobs.getGCManager().reload();
	Jobs.getLanguage().reload(Jobs.getGCManager().getLocale());
	Jobs.getConfigManager().reload();
	usedSlots.clear();
	for (Job job : jobs) {
	    usedSlots.put(job, getJobsDAO().getSlotsTaken(job));
	}
	pManager.reload();
	permissionHandler.registerPermissions();

	// set the system to auto save
	if (Jobs.getGCManager().getSavePeriod() > 0) {
	    saveTask = new DatabaseSaveThread(Jobs.getGCManager().getSavePeriod());
	    saveTask.start();
	}

	// schedule payouts to buffered payments
	paymentThread = new BufferedPaymentThread(Jobs.getGCManager().getEconomyBatchDelay());
	paymentThread.start();

	Jobs.getJobsDAO().loadPlayerData();

	// Schedule
	Jobs.getScheduleManager().load();
	if (Jobs.getGCManager().useGlobalBoostScheduler)
	    Jobs.getScheduleManager().scheduler();
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
	if (Jobs.getGCManager().storageMethod.equals("mysql"))
	    Jobs.getGCManager().startSqlite();
	else
	    Jobs.getGCManager().startMysql();
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

    public static boolean isUnderMoneyLimit(OfflinePlayer player, Double amount) {

	if (player == null)
	    return true;

	if (amount == 0)
	    return true;

	String playername = player.getName();

	if (!Jobs.getGCManager().MoneyLimitUse)
	    return true;

	if (!paymentLimit.containsKey(playername)) {
	    PaymentData data = new PaymentData(System.currentTimeMillis(), amount, 0.0, 0.0, 0L, false);
	    //data.AddNewAmount(amount);
	    paymentLimit.put(playername, data);
	} else {
	    PaymentData data = paymentLimit.get(playername);
	    JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(player);
	    if (data.IsReachedMoneyLimit(Jobs.getGCManager().MoneyTimeLimit, JPlayer.getMoneyLimit())) {
		if (player.isOnline() && !data.Informed && !data.isReseted()) {
		    ((Player) player).sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reachedlimit"));
		    ((Player) player).sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reachedlimit2"));
		    data.Setinformed();
		}
		if (data.IsAnnounceTime(Jobs.getGCManager().MoneyAnnouncmentDelay) && player.isOnline()) {
		    String message = Jobs.getLanguage().getMessage("command.limit.output.lefttime", "%hour%", data.GetLeftHour(Jobs
			.getGCManager().MoneyTimeLimit));
		    message = message.replace("%min%", String.valueOf(data.GetLeftMin(Jobs.getGCManager().MoneyTimeLimit)));
		    message = message.replace("%sec%", String.valueOf(data.GetLeftsec(Jobs.getGCManager().MoneyTimeLimit)));
		    Jobs.getActionBar().send(((Player) player), ChatColor.RED + message);
		}
		if (data.isReseted())
		    data.setReseted(false);
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

	if (!Jobs.getGCManager().ExpLimitUse)
	    return true;

	if (!ExpLimit.containsKey(playername)) {
	    PaymentData data = new PaymentData(System.currentTimeMillis(), 0.0, 0.0, amount, 0L, false);
	    //data.AddNewAmount(amount);
	    ExpLimit.put(playername, data);
	} else {
	    PaymentData data = ExpLimit.get(playername);
	    JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(player);
	    if (data.IsReachedExpLimit(Jobs.getGCManager().ExpTimeLimit, JPlayer.getExpLimit())) {
		if (player.isOnline() && !data.Informed && !data.isReseted()) {
		    ((Player) player).sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reachedExplimit"));
		    ((Player) player).sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reachedExplimit2"));
		    data.Setinformed();
		}
		if (data.IsAnnounceTime(Jobs.getGCManager().ExpAnnouncmentDelay) && player.isOnline()) {
		    String message = Jobs.getLanguage().getMessage("command.limit.output.lefttime", "%hour%", data.GetLeftHour(Jobs.getGCManager().ExpTimeLimit));
		    message = message.replace("%min%", String.valueOf(data.GetLeftMin(Jobs.getGCManager().ExpTimeLimit)));
		    message = message.replace("%sec%", String.valueOf(data.GetLeftsec(Jobs.getGCManager().ExpTimeLimit)));
		    Jobs.getActionBar().send(((Player) player), ChatColor.RED + message);
		}
		if (data.isReseted())
		    data.setReseted(false);
		return false;
	    } else
		data.AddExpAmount(amount);
	    ExpLimit.put(playername, data);
	}
	return true;
    }

    public static boolean isUnderPointLimit(final OfflinePlayer player, Double amount) {
	if (player == null)
	    return false;

	if (amount == 0)
	    return true;

	String playername = player.getName();

	if (!Jobs.getGCManager().PointLimitUse)
	    return true;

	if (!PointLimit.containsKey(playername)) {
	    PaymentData data = new PaymentData(System.currentTimeMillis(), 0.0, amount, 0.0, 0L, false);
	    //data.AddNewAmount(amount);
	    PointLimit.put(playername, data);
	} else {
	    PaymentData data = PointLimit.get(playername);
	    JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(player);
	    if (data.IsReachedPointLimit(Jobs.getGCManager().PointTimeLimit, JPlayer.getPointLimit())) {
		if (player.isOnline() && !data.Informed && !data.isReseted()) {
		    ((Player) player).sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reachedPointlimit"));
		    ((Player) player).sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reachedPointlimit2"));
		    data.Setinformed();
		}
		if (data.IsAnnounceTime(Jobs.getGCManager().PointAnnouncmentDelay) && player.isOnline()) {
		    String message = Jobs.getLanguage().getMessage("command.limit.output.lefttime", "%hour%", data.GetLeftHour(Jobs.getGCManager().PointTimeLimit));
		    message = message.replace("%min%", String.valueOf(data.GetLeftMin(Jobs.getGCManager().PointTimeLimit)));
		    message = message.replace("%sec%", String.valueOf(data.GetLeftsec(Jobs.getGCManager().PointTimeLimit)));
		    Jobs.getActionBar().send(((Player) player), ChatColor.RED + message);
		}
		if (data.isReseted())
		    data.setReseted(false);
		return false;
	    } else
		data.AddPoints(amount);
	    PointLimit.put(playername, data);
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
    public static void action(JobsPlayer jPlayer, ActionInfo info, double multiplier) {

	if (jPlayer == null)
	    return;

	List<JobProgression> progression = jPlayer.getJobProgression();
	int numjobs = progression.size();
	// no job

	if (numjobs == 0) {
	    Job jobNone = Jobs.getNoneJob();
	    Player dude = Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID());

	    if (jobNone != null) {
		JobInfo jobinfo = jobNone.getJobInfo(info, 1);

		if (jobinfo == null)
		    return;

		Double income = jobinfo.getIncome(1, numjobs);
		Double points = jobinfo.getPoints(1, numjobs);

		if (income != 0D || points != 0D) {

		    BoostMultiplier FinalBoost = Jobs.getPlayerManager().getFinalBonus(Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID()), Jobs.getNoneJob());

		    // Calculate income

		    Double amount = 0D;
		    if (income != 0D) {
			amount = income + (income * FinalBoost.getMoneyBoost() / 100);
			if (Jobs.getGCManager().useMinimumOveralPayment && income > 0) {
			    double maxLimit = income * Jobs.getGCManager().MinimumOveralPaymentLimit;
			    if (amount < maxLimit) {
				amount = maxLimit;
			    }
			}
		    }

		    // Calculate points

		    Double pointAmount = 0D;
		    if (points != 0D) {
			pointAmount = points + (points * FinalBoost.getPointsBoost() / 100);
			if (Jobs.getGCManager().useMinimumOveralPoints && points > 0) {
			    double maxLimit = points * Jobs.getGCManager().MinimumOveralPaymentLimit;
			    if (pointAmount < maxLimit) {
				pointAmount = maxLimit;
			    }
			}
		    }

		    if (!isUnderMoneyLimit(dude, amount)) {
			amount = 0D;
			if (Jobs.getGCManager().MoneyStopPoint)
			    pointAmount = 0D;
		    }

		    if (!isUnderPointLimit(dude, pointAmount)) {
			pointAmount = 0D;
			if (Jobs.getGCManager().PointStopMoney)
			    amount = 0D;
		    }
		    if (amount == 0D && pointAmount == 0D)
			return;

		    if (pointAmount != 0D)
			jPlayer.setSaved(false);

		    Jobs.getEconomy().pay(jPlayer, amount, pointAmount, 0.0);

		    if (Jobs.getGCManager().LoggingUse)
			Loging.recordToLog(jPlayer, info, amount, 0);
		}
	    }
	} else {

	    for (JobProgression prog : progression) {

		int level = prog.getLevel();
		JobInfo jobinfo = prog.getJob().getJobInfo(info, level);

		if (jobinfo == null)
		    continue;

		Double income = jobinfo.getIncome(level, numjobs);
		Double points = jobinfo.getPoints(level, numjobs);

		if (income == 0D && points == 0D)
		    continue;

		Double exp = jobinfo.getExperience(level, numjobs);

		if (Jobs.getGCManager().addXpPlayer()) {
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

		BoostMultiplier FinalBoost = Jobs.getPlayerManager().getFinalBonus(Bukkit.getServer().getPlayer(jPlayer.getPlayerUUID()), prog.getJob());

		if (multiplier != 0.0)
		    FinalBoost = new BoostMultiplier(FinalBoost.getMoneyBoost() + multiplier,
			FinalBoost.getPointsBoost() + multiplier,
			FinalBoost.getExpBoost() + multiplier);

		OfflinePlayer dude = jPlayer.getPlayer();

		// Calculate income

		Double amount = 0D;
		if (income != 0D) {
		    amount = income + (income * FinalBoost.getMoneyBoost() / 100);
		    if (Jobs.getGCManager().useMinimumOveralPayment && income > 0) {
			double maxLimit = income * Jobs.getGCManager().MinimumOveralPaymentLimit;
			if (amount < maxLimit) {
			    amount = maxLimit;
			}
		    }
		}

		// Calculate points

		Double pointAmount = 0D;
		if (points != 0D) {
		    pointAmount = points + (points * FinalBoost.getPointsBoost() / 100);
		    if (Jobs.getGCManager().useMinimumOveralPoints && points > 0) {
			double maxLimit = points * Jobs.getGCManager().MinimumOveralPaymentLimit;
			if (pointAmount < maxLimit) {
			    pointAmount = maxLimit;
			}
		    }
		}

		// Calculate exp
		double expAmount = exp + (exp * FinalBoost.getExpBoost() / 100);

		if (Jobs.getGCManager().useMinimumOveralPayment && exp > 0) {
		    double maxLimit = exp * Jobs.getGCManager().MinimumOveralPaymentLimit;
		    if (exp < maxLimit) {
			exp = maxLimit;
		    }
		}

		if (!isUnderMoneyLimit(dude, amount)) {
		    amount = 0D;
		    if (Jobs.getGCManager().MoneyStopExp)
			expAmount = 0D;
		    if (Jobs.getGCManager().MoneyStopPoint)
			pointAmount = 0D;
		}

		if (!isUnderExpLimit(dude, expAmount)) {
		    expAmount = 0D;
		    if (Jobs.getGCManager().ExpStopMoney)
			amount = 0D;
		    if (Jobs.getGCManager().ExpStopPoint)
			pointAmount = 0D;
		}

		if (!isUnderPointLimit(dude, pointAmount)) {
		    pointAmount = 0D;
		    if (Jobs.getGCManager().PointStopMoney)
			amount = 0D;
		    if (Jobs.getGCManager().PointStopExp)
			expAmount = 0D;
		}

		if ((amount == 0D || pointAmount == 0D) && expAmount == 0D)
		    continue;

		if (Jobs.getGCManager().BossBarEnabled && Jobs.getGCManager().BossBarShowOnEachAction)
		    Jobs.getBBManager().ShowJobProgression(jPlayer, prog);
		else if (Jobs.getGCManager().BossBarEnabled && !Jobs.getGCManager().BossBarShowOnEachAction)
		    jPlayer.getUpdateBossBarFor().add(prog.getJob().getName());

		Jobs.getEconomy().pay(jPlayer, amount, pointAmount, expAmount);
		int oldLevel = prog.getLevel();

		if (Jobs.getGCManager().LoggingUse)
		    Loging.recordToLog(jPlayer, info, amount, expAmount);

		// JobsPayment event
		JobsExpGainEvent JobsExpGainEvent = new JobsExpGainEvent(jPlayer.getPlayer(), prog.getJob(), expAmount);
		Bukkit.getServer().getPluginManager().callEvent(JobsExpGainEvent);
		// If event is canceled, don't do anything
		if (JobsExpGainEvent.isCancelled())
		    continue;
		if (prog.addExperience(expAmount))
		    Jobs.getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
	    }
	}
    }
}
