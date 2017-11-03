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
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.CmiItems.ItemManager;
import com.gamingmesh.jobs.Gui.GuiManager;
import com.gamingmesh.jobs.MyPet.MyPetManager;
import com.gamingmesh.jobs.MythicMobs.MythicMobInterface;
import com.gamingmesh.jobs.MythicMobs.MythicMobs2;
import com.gamingmesh.jobs.MythicMobs.MythicMobs4;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.WorldGuard.WorldGuardManager;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.config.BlockProtectionManager;
import com.gamingmesh.jobs.config.BossBarManager;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.config.ExploreManager;
import com.gamingmesh.jobs.config.GeneralConfigManager;
import com.gamingmesh.jobs.config.LanguageManager;
import com.gamingmesh.jobs.config.NameTranslatorManager;
import com.gamingmesh.jobs.config.RestrictedAreaManager;
import com.gamingmesh.jobs.config.RestrictedBlockManager;
import com.gamingmesh.jobs.config.ScheduleManager;
import com.gamingmesh.jobs.config.ShopManager;
import com.gamingmesh.jobs.config.TitleManager;
import com.gamingmesh.jobs.config.YmlMaker;
import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.ArchivedJobs;
import com.gamingmesh.jobs.container.BlockProtection;
import com.gamingmesh.jobs.container.Boost;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.DBAction;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.dao.*;
import com.gamingmesh.jobs.container.FastPayment;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.gamingmesh.jobs.economy.Economy;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.listeners.JobsListener;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.gamingmesh.jobs.listeners.McMMOlistener;
import com.gamingmesh.jobs.listeners.PistonProtectionListener;
import com.gamingmesh.jobs.selection.SelectionManager;
import com.gamingmesh.jobs.stuff.ActionBar;
import com.gamingmesh.jobs.stuff.Loging;
import com.gamingmesh.jobs.stuff.RawMessage;
import com.gamingmesh.jobs.stuff.TabComplete;
import com.gamingmesh.jobs.stuff.VersionChecker;
import com.gamingmesh.jobs.stuff.CMIScoreboardManager;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;

public class Jobs extends JavaPlugin {
//    public static Jobs plugin = new Jobs();
    private static String version = "";
    private static PlayerManager pManager = null;
    private static JobsCommands cManager = null;
    private static Language lManager = null;
    private static LanguageManager lmManager = null;
    private static SignUtil signManager = null;
    private CMIScoreboardManager CMIScoreboardManager = null;
    private static ScheduleManager scheduleManager = null;
    private static NameTranslatorManager NameTranslatorManager = null;
    private static GuiManager GUIManager = null;
    private static ExploreManager exploreManager = null;
    private static TitleManager titleManager = null;
    private static RestrictedBlockManager RBManager = null;
    private static RestrictedAreaManager RAManager = null;
    private static BossBarManager BBManager;
    private static ShopManager shopManager;
    private static Loging loging;
    private static BlockProtectionManager BpManager = null;

    private static JobsManager DBManager = null;

    private static PistonProtectionListener PistonProtectionListener = null;
    private static McMMOlistener McMMOlistener = null;

    private static MythicMobInterface MythicManager;
    private static MyPetManager myPetManager;
    private static WorldGuardManager worldGuardManager;

    private static ConfigManager configManager;
    private static GeneralConfigManager GconfigManager;

    private static Logger pLogger;
    private static File dataFolder;
    private static JobsClassLoader classLoader;
    private static JobsDAO dao = null;
    private static List<Job> jobs = null;
    private static Job noneJob = null;
    private static WeakHashMap<Job, Integer> usedSlots = new WeakHashMap<Job, Integer>();
    public static WeakHashMap<String, Boolean> actionbartoggle = new WeakHashMap<String, Boolean>();
    public static WeakHashMap<String, Boolean> BossBartoggle = new WeakHashMap<String, Boolean>();
//	public static WeakHashMap<String, Double> GlobalBoost = new WeakHashMap<String, Double>();
    private static BufferedEconomy economy;
    private static PermissionHandler permissionHandler;
    private static PermissionManager permissionManager;

    private static ItemManager itemManager;

    public static BufferedPaymentThread paymentThread = null;
    private static DatabaseSaveThread saveTask = null;

    public static HashMap<String, FastPayment> FastPayment = new HashMap<String, FastPayment>();

    private static NMS nms;

    private static ActionBar actionbar;
    private static boolean running = false;

    protected static VersionChecker versionCheckManager;

    protected static SelectionManager smanager;

    public void setMcMMOlistener() {
	McMMOlistener = new McMMOlistener(this);
    }

    public static McMMOlistener getMcMMOlistener() {
	return McMMOlistener;
    }

    public void setPistonProtectionListener() {
	PistonProtectionListener = new PistonProtectionListener(this);
    }

    public static PistonProtectionListener getPistonProtectionListener() {
	return PistonProtectionListener;
    }

    public void setMyPetManager() {
	myPetManager = new MyPetManager();
    }

    public static MyPetManager getMyPetManager() {
	return myPetManager;
    }

    public static WorldGuardManager getWorldGuardManager() {
	return worldGuardManager;
    }

    public void setMythicManager() {
	try {
	    Class.forName("net.elseland.xikage.MythicMobs.API.MythicMobsAPI");
	    MythicManager = new MythicMobs2(this);
	} catch (ClassNotFoundException e) {
	    try {
		Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper");
		MythicManager = new MythicMobs4(this);
	    } catch (ClassNotFoundException ex) {
	    }
	}
	if (MythicManager != null)
	    consoleMsg("&e[Jobs] MythicMobs detected.");
    }

    private boolean setWorldGuard() {
	Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	if (plugin != null) {
	    worldGuardManager = new WorldGuardManager(this);
	    consoleMsg("&e[Jobs] WorldGuard detected.");
	    return true;
	}
	return false;
    }

    public static MythicMobInterface getMythicManager() {
	return MythicManager;
    }

    public void setLoging() {
	loging = new Loging();
    }

    public static Loging getLoging() {
	return loging;
    }

    public void setBpManager() {
	BpManager = new BlockProtectionManager();
    }

    public static BlockProtectionManager getBpManager() {
	return BpManager;
    }

    public static JobsManager getDBManager() {
	if (DBManager == null)
	    DBManager = new JobsManager(instance);
	return DBManager;
    }

    public static void setShopManager(Jobs plugin) {
	shopManager = new ShopManager(plugin);
    }

    public static ShopManager getShopManager() {
	return shopManager;
    }

    public void setConfigManager() {
	configManager = new ConfigManager(this);
    }

    public static ConfigManager getConfigManager() {
	return configManager;
    }

    public void setGCManager() {
	GconfigManager = new GeneralConfigManager(this);
    }

    public static GeneralConfigManager getGCManager() {
	return GconfigManager;
    }

    public void setActionBar() {
	actionbar = new ActionBar();
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

    public void setPlayerManager() {
	pManager = new PlayerManager(this);
    }

    public static void setRestrictedBlockManager(Jobs plugin) {
	RBManager = new RestrictedBlockManager(plugin);
    }

    public static RestrictedBlockManager getRestrictedBlockManager() {
	return RBManager;
    }

    public static void setRestrictedAreaManager(Jobs plugin) {
	RAManager = new RestrictedAreaManager(plugin);
    }

    public static RestrictedAreaManager getRestrictedAreaManager() {
	return RAManager;
    }

    public static void setTitleManager(Jobs plugin) {
	titleManager = new TitleManager(plugin);
    }

    public static TitleManager gettitleManager() {
	return titleManager;
    }

    public void setBBManager() {
	BBManager = new BossBarManager(this);
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

    public static void setScheduleManager(Jobs plugin) {
	scheduleManager = new ScheduleManager(plugin);
    }

    public static NameTranslatorManager getNameTranslatorManager() {
	return NameTranslatorManager;
    }

    public static void setNameTranslatorManager(Jobs plugin) {
	NameTranslatorManager = new NameTranslatorManager(plugin);
    }

    public static GuiManager getGUIManager() {
	return GUIManager;
    }

    public void setGUIManager() {
	GUIManager = new GuiManager();
    }

    public static JobsCommands getCommandManager() {
	return cManager;
    }

    public void setCommandManager() {
	cManager = new JobsCommands(this);
    }

    public static ExploreManager getExplore() {
	return exploreManager;
    }

    public void setExplore() {
	exploreManager = new ExploreManager();
    }

    /**
     * Returns scoreboard manager
     * @return the scoreboard manager
     */
    public CMIScoreboardManager getCMIScoreboardManager() {
	if (CMIScoreboardManager == null)
	    CMIScoreboardManager = new CMIScoreboardManager(this);
	return CMIScoreboardManager;
    }

    protected static Jobs instance;

    public static Jobs getInstance() {
	return instance;
    }

    /**
     * Returns sign manager
     * @return the sign manager
     */
    public static SignUtil getSignUtil() {
	return signManager;
    }

    public static void setSignUtil(Jobs plugin) {
	signManager = new SignUtil(plugin);
    }

    /**
     * Returns language manager
     * @return the language manager
     */
    public static Language getLanguage() {
	return lManager;
    }

    public void setLanguage() {
	lManager = new Language(this);
    }

    public static LanguageManager getLanguageManager() {
	return lmManager;
    }

    public static void setLanguageManager(Jobs plugin) {
	lmManager = new LanguageManager(plugin);
    }

    /**
     * Sets the plugin logger
     */
    public void setPluginLogger(Logger logger) {
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
    public void setDataFolder(File dir) {
	dataFolder = dir;
    }

    /**
     * Retrieves the data folder
     * @return data folder
     */
    public static File getFolder() {
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
    public void startup() {
	instance = this;
	try {
	    reload();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	loadAllPlayersData();
	// add all online players
	for (Player online : Bukkit.getServer().getOnlinePlayers()) {
	    Jobs.getPlayerManager().playerJoin(online);
	}
    }

    public static void loadAllPlayersData() {
	long time = System.currentTimeMillis();
	// Cloning to avoid issues
	HashMap<UUID, PlayerInfo> temp = new HashMap<UUID, PlayerInfo>(Jobs.getPlayerManager().getPlayersInfoUUIDMap());
	HashMap<Integer, List<JobsDAOData>> playersJobs = Jobs.getJobsDAO().getAllJobs();
	HashMap<Integer, PlayerPoints> playersPoints = Jobs.getJobsDAO().getAllPoints();
	HashMap<Integer, HashMap<String, Log>> playersLogs = Jobs.getJobsDAO().getAllLogs();
	HashMap<Integer, ArchivedJobs> playersArchives = Jobs.getJobsDAO().getAllArchivedJobs();
	HashMap<Integer, PaymentData> playersLimits = Jobs.getJobsDAO().loadPlayerLimits();
	Iterator<Entry<UUID, PlayerInfo>> it = temp.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<UUID, PlayerInfo> one = it.next();
	    try {
		int id = one.getValue().getID();
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(
		    one.getValue(),
		    playersJobs.get(id),
		    playersPoints.get(id),
		    playersLogs.get(id),
		    playersArchives.get(id),
		    playersLimits.get(id));
		if (jPlayer == null)
		    continue;
		Jobs.getPlayerManager().addPlayerToCache(jPlayer);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	dao.getMap().clear();
	Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Jobs] Preloaded " + Jobs.getPlayerManager().getPlayersCache().size() + " players data in " + ((int) (((System.currentTimeMillis() - time)
	    / 1000d) * 100) / 100D));
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
	smanager = new SelectionManager();
	if (dao != null) {
	    dao.closeConnections();
	}

	GconfigManager.reload();
	lManager.reload();
	Jobs.getConfigManager().reload();
	usedSlots.clear();
	for (Job job : jobs) {
	    usedSlots.put(job, getJobsDAO().getSlotsTaken(job));
	}
	pManager.reload();
	permissionHandler.registerPermissions();

	// set the system to auto save
	if (GconfigManager.getSavePeriod() > 0) {
	    saveTask = new DatabaseSaveThread(GconfigManager.getSavePeriod());
	    saveTask.start();
	}

	// schedule payouts to buffered payments
	paymentThread = new BufferedPaymentThread(GconfigManager.getEconomyBatchDelay());
	paymentThread.start();

	Jobs.getJobsDAO().loadPlayerData();

	// Schedule
	Jobs.getScheduleManager().load();
	if (GconfigManager.useGlobalBoostScheduler)
	    Jobs.getScheduleManager().start();

	permissionManager = new PermissionManager();
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
	getDBManager().switchDataBase();
	pManager.reload();
    }

    /**
     * Function to get the number of slots used on the server for this job
     * @param job - the job
     * @return the number of slots
     */
    public static int getUsedSlots(Job job) {
	if (usedSlots.containsKey(job))
	    return usedSlots.get(job);
	return 0;
    }

    /**
     * Function to increase the number of used slots for a job
     * @param job - the job someone is taking
     */
    public static void takeSlot(Job job) {
	if (usedSlots.containsKey(job))
	    usedSlots.put(job, usedSlots.get(job) + 1);
    }

    /**
     * Function to decrease the number of used slots for a job
     * @param job - the job someone is leaving
     */
    public static void leaveSlot(Job job) {
	if (usedSlots.containsKey(job))
	    usedSlots.put(job, usedSlots.get(job) - 1);
    }

    /**
     * Returns the jobs classloader
     * @return the classloader
     */
    public static JobsClassLoader getJobsClassloader() {
	return classLoader;
    }

    public void setJobsClassloader() {
	classLoader = new JobsClassLoader(this);
    }

    /**
     * Sets the permission handler
     * @param h - the permission handler
     */
    public void setPermissionHandler(PermissionHandler h) {
	permissionHandler = h;
    }

    /**
     * Gets the permission handler
     * @return the permission handler
     */
    public static PermissionHandler getPermissionHandler() {
	return permissionHandler;
    }

    public static PermissionManager getPermissionManager() {
	return permissionManager;
    }

    public static ItemManager getItemManager() {
	return itemManager;
    }

    /**
     * Sets the economy handler
     * @param eco - the economy handler
     */
    public static void setEconomy(Jobs plugin, Economy eco) {
	economy = new BufferedEconomy(plugin, eco);
    }

    /**
     * Gets the economy handler
     * @return the economy handler
     */
    public static BufferedEconomy getEconomy() {
	return economy;
    }

    /**
     * Gets the version check manager
     * @return the version check manager
     */
    public static VersionChecker getVersionCheckManager() {
	return versionCheckManager;
    }

    @Override
    public void onEnable() {
	running = true;
	this.setEnabled(true);

	versionCheckManager = new VersionChecker(this);
	version = versionCheckManager.getVersion().getShortVersion();

	itemManager = new ItemManager(this);

	try {
	    Class<?> nmsClass;
	    nmsClass = Class.forName("com.gamingmesh.jobs.nmsUtil." + version);
	    if (NMS.class.isAssignableFrom(nmsClass)) {
		setNms((NMS) nmsClass.getConstructor().newInstance());
	    } else {
		System.out.println("Something went wrong, please note down version and contact author v:" + version);
		this.setEnabled(false);
	    }
	} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
	    | SecurityException e) {
	    System.out.println("Your server version is not compatible with this plugins version! Plugin will be disabled: " + version);
	    this.setEnabled(false);
	    e.printStackTrace();
	    return;
	}

	try {

	    setActionBar();
	    YmlMaker jobConfig = new YmlMaker(this, "jobConfig.yml");
	    jobConfig.saveDefaultConfig();

	    YmlMaker jobSigns = new YmlMaker(this, "Signs.yml");
	    jobSigns.saveDefaultConfig();

	    YmlMaker jobSchedule = new YmlMaker(this, "schedule.yml");
	    jobSchedule.saveDefaultConfig();

	    YmlMaker jobShopItems = new YmlMaker(this, "shopItems.yml");
	    jobShopItems.saveDefaultConfig();

	    setPermissionHandler(new PermissionHandler(this));
	    setJobsClassloader();
	    setPlayerManager();
	    setLanguage();
	    setGUIManager();
	    setExplore();
	    setBBManager();
	    setPluginLogger(getLogger());
	    setDataFolder(getDataFolder());
	    setLoging();
	    setGCManager();
	    setConfigManager();
	    setCommandManager();
	    setBpManager();

	    getCommand("jobs").setExecutor(cManager);
	    this.getCommand("jobs").setTabCompleter(new TabComplete());

	    startup();

	    // register the listeners
	    getServer().getPluginManager().registerEvents(new JobsListener(this), this);
	    getServer().getPluginManager().registerEvents(new JobsPaymentListener(this), this);

	    setMcMMOlistener();
	    if (McMMOlistener.CheckmcMMO()) {
		getServer().getPluginManager().registerEvents(McMMOlistener, this);
	    }

	    setMyPetManager();
	    setWorldGuard();

	    setMythicManager();
	    if (MythicManager != null && MythicManager.Check() && GconfigManager.MythicMobsEnabled) {
		MythicManager.registerListener();
	    }

	    setPistonProtectionListener();
	    if (GconfigManager.useBlockProtection) {
		getServer().getPluginManager().registerEvents(PistonProtectionListener, this);
	    }

	    // register economy
	    Bukkit.getScheduler().runTask(this, new HookEconomyTask(this));

	    // all loaded properly.

	    dao.loadBlockProtection();
	    getExplore().load();

	    String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] Plugin has been enabled succesfully.");
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    console.sendMessage(message);
	    lManager.reload();

	    cManager.fillCommands();
	} catch (Exception e) {
	    System.out.println("There was some issues when starting plugin. Please contact dev about this. Plugin will be disabled.");
	    e.printStackTrace();
	}
    }

    @Override
    public void onDisable() {
	running = false;
	GUIManager.CloseInventories();
	shopManager.CloseInventories();
	dao.saveExplore();
	dao.saveBlockProtection();
	Jobs.shutdown();
	String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] &2Plugin has been disabled succesfully.");
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	console.sendMessage(message);
	this.setEnabled(false);
    }

    /**
     * Performed an action
     * 
     * Give correct experience and income
     * @param jPlayer - the player
     * @param action - the action
     * @param multiplier - the payment/xp multiplier
     */

    public static void action(JobsPlayer jPlayer, ActionInfo info) {
	action(jPlayer, info, null, null, null);
    }

    public static void action(JobsPlayer jPlayer, ActionInfo info, Block block) {
	action(jPlayer, info, block, null, null);
    }

    public static void action(JobsPlayer jPlayer, ActionInfo info, Entity ent) {
	action(jPlayer, info, null, ent, null);
    }

    public static void action(JobsPlayer jPlayer, ActionInfo info, Entity ent, LivingEntity victim) {
	action(jPlayer, info, null, ent, victim);
    }

    public static void action(JobsPlayer jPlayer, ActionInfo info, Block block, Entity ent, LivingEntity victim) {

	if (jPlayer == null)
	    return;

	List<JobProgression> progression = jPlayer.getJobProgression();
	int numjobs = progression.size();
	// no job

	if (!isBpOk(jPlayer, info, block, true))
	    return;

	if (numjobs == 0) {

	    if (noneJob == null)
		return;
	    JobInfo jobinfo = noneJob.getJobInfo(info, 1);

	    if (jobinfo == null)
		return;

	    Double income = jobinfo.getIncome(1, numjobs);
	    Double pointAmount = jobinfo.getPoints(1, numjobs);

	    if (income == 0D && pointAmount == 0D)
		return;

	    Boost boost = pManager.getFinalBonus(jPlayer, Jobs.getNoneJob());

	    // Calculate income

	    if (income != 0D) {
		income = income + (income * boost.getFinal(CurrencyType.MONEY));
		if (GconfigManager.useMinimumOveralPayment && income > 0) {
		    double maxLimit = income * GconfigManager.MinimumOveralPaymentLimit;
		    if (income < maxLimit) {
			income = maxLimit;
		    }
		}
	    }

	    // Calculate points

	    if (pointAmount != 0D) {
		pointAmount = pointAmount + (pointAmount * boost.getFinal(CurrencyType.POINTS));
		if (GconfigManager.useMinimumOveralPoints && pointAmount > 0) {
		    double maxLimit = pointAmount * GconfigManager.MinimumOveralPaymentLimit;
		    if (pointAmount < maxLimit) {
			pointAmount = maxLimit;
		    }
		}
	    }

	    if (!jPlayer.isUnderLimit(CurrencyType.MONEY, income)) {
		income = 0D;
		if (GconfigManager.getLimit(CurrencyType.MONEY).getStopWith().contains(CurrencyType.POINTS))
		    pointAmount = 0D;
	    }

	    if (!jPlayer.isUnderLimit(CurrencyType.POINTS, pointAmount)) {
		pointAmount = 0D;
		if (GconfigManager.getLimit(CurrencyType.POINTS).getStopWith().contains(CurrencyType.MONEY))
		    income = 0D;
	    }

	    if (income == 0D && pointAmount == 0D)
		return;

	    if (info.getType() == ActionType.BREAK && block != null)
		Jobs.getBpManager().remove(block);

	    if (pointAmount != 0D)
		jPlayer.setSaved(false);

	    Jobs.getEconomy().pay(jPlayer, income, pointAmount, 0.0);

	    if (GconfigManager.LoggingUse) {
		HashMap<CurrencyType, Double> amounts = new HashMap<CurrencyType, Double>();
		amounts.put(CurrencyType.MONEY, income);
		loging.recordToLog(jPlayer, info, amounts);
	    }

	} else {
	    for (JobProgression prog : progression) {
		int level = prog.getLevel();
		JobInfo jobinfo = prog.getJob().getJobInfo(info, level);

		if (jobinfo == null)
		    continue;
		Double income = jobinfo.getIncome(level, numjobs);
		Double pointAmount = jobinfo.getPoints(level, numjobs);
		Double expAmount = jobinfo.getExperience(level, numjobs);

		if (income == 0D && pointAmount == 0D && expAmount == 0D)
		    continue;

		if (GconfigManager.addXpPlayer()) {
		    Player player = jPlayer.getPlayer();
		    if (player != null) {
			/*
			 * Minecraft experience is calculated in whole numbers only.
			 * Calculate the fraction of an experience point and perform a dice roll.
			 * That way jobs that give fractions of experience points will slowly give
			 * experience in the aggregate
			 */
			int expInt = expAmount.intValue();
			double remainder = expAmount.doubleValue() - expInt;
			if (Math.abs(remainder) > Math.random()) {
			    if (expAmount.doubleValue() < 0) {
				expInt--;
			    } else {
				expInt++;
			    }
			}

			if (expInt < 0 && getPlayerExperience(player) < -expInt) {
			    player.setLevel(0);
			    player.setTotalExperience(0);
			    player.setExp(0);
			} else
			    player.giveExp(expInt);
		    }
		}
		Boost boost = Jobs.getPlayerManager().getFinalBonus(jPlayer, prog.getJob(), ent, victim);

		// Calculate income
		if (income != 0D) {
		    income = boost.getFinalAmount(CurrencyType.MONEY, income);
		    if (GconfigManager.useMinimumOveralPayment && income > 0) {
			double maxLimit = income * GconfigManager.MinimumOveralPaymentLimit;
			if (income < maxLimit) {
			    income = maxLimit;
			}
		    }
		}

		// Calculate points
		if (pointAmount != 0D) {
		    pointAmount = boost.getFinalAmount(CurrencyType.POINTS, pointAmount);
		    if (GconfigManager.useMinimumOveralPoints && pointAmount > 0) {
			double maxLimit = pointAmount * GconfigManager.MinimumOveralPaymentLimit;
			if (pointAmount < maxLimit) {
			    pointAmount = maxLimit;
			}
		    }
		}

		// Calculate exp
		expAmount = boost.getFinalAmount(CurrencyType.EXP, expAmount);

		if (GconfigManager.useMinimumOveralPayment && expAmount > 0) {
		    double maxLimit = expAmount * GconfigManager.MinimumOveralPaymentLimit;
		    if (expAmount < maxLimit) {
			expAmount = maxLimit;
		    }
		}

		if (!jPlayer.isUnderLimit(CurrencyType.MONEY, income)) {
		    income = 0D;
		    if (GconfigManager.getLimit(CurrencyType.MONEY).getStopWith().contains(CurrencyType.EXP))
			expAmount = 0D;
		    if (GconfigManager.getLimit(CurrencyType.MONEY).getStopWith().contains(CurrencyType.POINTS))
			pointAmount = 0D;
		}

		if (!jPlayer.isUnderLimit(CurrencyType.EXP, expAmount)) {
		    expAmount = 0D;
		    if (GconfigManager.getLimit(CurrencyType.EXP).getStopWith().contains(CurrencyType.MONEY))
			income = 0D;
		    if (GconfigManager.getLimit(CurrencyType.EXP).getStopWith().contains(CurrencyType.POINTS))
			pointAmount = 0D;
		}

		if (!jPlayer.isUnderLimit(CurrencyType.POINTS, pointAmount)) {
		    pointAmount = 0D;
		    if (GconfigManager.getLimit(CurrencyType.POINTS).getStopWith().contains(CurrencyType.MONEY))
			income = 0D;
		    if (GconfigManager.getLimit(CurrencyType.POINTS).getStopWith().contains(CurrencyType.EXP))
			expAmount = 0D;
		}

		if (income == 0D && pointAmount == 0D && expAmount == 0D)
		    continue;

		try {
		    if (expAmount != 0D && GconfigManager.BossBarEnabled)
			if (GconfigManager.BossBarShowOnEachAction) {
			    Jobs.getBBManager().ShowJobProgression(jPlayer, prog);
			} else
			    jPlayer.getUpdateBossBarFor().add(prog.getJob().getName());
		} catch (Exception e) {
		    Bukkit.getConsoleSender().sendMessage("[Jobs] Some issues with boss bar feature accured, try disabling it to avoid it.");
		}

		// JobsPayment event
		JobsExpGainEvent JobsExpGainEvent = new JobsExpGainEvent(jPlayer.getPlayer(), prog.getJob(), expAmount);
		Bukkit.getServer().getPluginManager().callEvent(JobsExpGainEvent);
		// If event is canceled, don't do anything
		if (JobsExpGainEvent.isCancelled())
		    expAmount = 0D;
		else
		    expAmount = JobsExpGainEvent.getExp();

		FastPayment.clear();
		FastPayment.put(jPlayer.getUserName(), new FastPayment(jPlayer, info, new BufferedPayment(jPlayer.getPlayer(), income, pointAmount, expAmount), prog
		    .getJob()));

		economy.pay(jPlayer, income, pointAmount, expAmount);
		int oldLevel = prog.getLevel();

		if (GconfigManager.LoggingUse) {
		    HashMap<CurrencyType, Double> amounts = new HashMap<CurrencyType, Double>();
		    amounts.put(CurrencyType.MONEY, income);
		    amounts.put(CurrencyType.EXP, expAmount);
		    amounts.put(CurrencyType.POINTS, pointAmount);
		    loging.recordToLog(jPlayer, info, amounts);
		}

		if (prog.addExperience(expAmount))
		    pManager.performLevelUp(jPlayer, prog.getJob(), oldLevel);
	    }

	    //need to update bp
	    if (block != null) {
		BlockProtection bp = BpManager.getBp(block.getLocation());
		if (bp != null)
		    bp.setPaid(true);
	    }
	}
    }

    private static boolean isBpOk(JobsPlayer player, ActionInfo info, Block block, boolean inform) {
	if (block == null || !getGCManager().useBlockProtection)
	    return true;

	if (info.getType() == ActionType.BREAK) {
	    if (block.hasMetadata("JobsExploit")) {
		//player.sendMessage("This block is protected using Rukes' system!");
		return false;
	    }
	    BlockProtection bp = getBpManager().getBp(block.getLocation());
	    if (bp != null) {
		Long time = bp.getTime();
		Integer cd = getBpManager().getBlockDelayTime(block);

		if (time == -1L) {
		    getBpManager().add(block, cd);
		    return false;
		}
		if ((time < System.currentTimeMillis()) && (bp.getAction() != DBAction.DELETE)) {
		    getBpManager().remove(block);
		    return true;
		}
		if (time > System.currentTimeMillis() || bp.isPaid() && bp.getAction() != DBAction.DELETE) {
		    int sec = Math.round((time - System.currentTimeMillis()) / 1000L);
		    if (inform) {
			if (player.canGetPaid(info))
			    getActionBar().send(player.getPlayer(), getLanguage().getMessage("message.blocktimer", "[time]", sec));
		    }
		    return false;
		}
		getBpManager().add(block, cd);
		if ((cd == null || cd == 0) && getGCManager().useGlobalTimer) {
		    getBpManager().add(block, getGCManager().globalblocktimer);
		}
	    } else if (getGCManager().useGlobalTimer) {
		getBpManager().add(block, getGCManager().globalblocktimer);
	    }
	} else if (info.getType() == ActionType.PLACE) {
	    BlockProtection bp = getBpManager().getBp(block.getLocation());
	    if (bp != null) {
		Long time = bp.getTime();
		Integer cd = getBpManager().getBlockDelayTime(block);

		if (time != -1L) {
		    if (time < System.currentTimeMillis() && bp.getAction() != DBAction.DELETE) {
			getBpManager().add(block, cd);
			return true;
		    }
		    if (time > System.currentTimeMillis() || bp.isPaid() && bp.getAction() != DBAction.DELETE) {
			int sec = Math.round((time - System.currentTimeMillis()) / 1000L);
			if (inform) {
			    if (player.canGetPaid(info))
				getActionBar().send(player.getPlayer(), getLanguage().getMessage("message.blocktimer", "[time]", sec));
			}
			getBpManager().add(block, cd);
			return false;
		    }
		} else if (bp.isPaid().booleanValue() && bp.getTime() == -1L && cd != null && cd == -1) {
		    getBpManager().add(block, cd);
		    return false;
		} else
		    getBpManager().add(block, cd);
	    } else {
		getBpManager().add(block, getBpManager().getBlockDelayTime(block));
	    }
	}

	return true;
    }

    private static int getPlayerExperience(Player player) {
	int bukkitExp = (ExpToLevel(player.getLevel()) + Math.round(deltaLevelToExp(player.getLevel()) * player.getExp()));
	return bukkitExp;
    }

    // total xp calculation based by lvl
    private static int ExpToLevel(int level) {
	if (version.equals("1_7")) {
	    if (level <= 16) {
		return 17 * level;
	    } else if (level <= 31) {
		return ((3 * level * level) / 2) - ((59 * level) / 2) + 360;
	    } else {
		return ((7 * level * level) / 2) - ((303 * level) / 2) + 2220;
	    }
	}
	if (level <= 16) {
	    return (level * level) + (6 * level);
	} else if (level <= 31) {
	    return (int) ((2.5 * level * level) - (40.5 * level) + 360);
	} else {
	    return (int) ((4.5 * level * level) - (162.5 * level) + 2220);
	}
    }

    // xp calculation for one current lvl
    private static int deltaLevelToExp(int level) {
	if (version.equals("1_7")) {
	    if (level <= 16) {
		return 17;
	    } else if (level <= 31) {
		return 3 * level - 31;
	    } else {
		return 7 * level - 155;
	    }
	}
	if (level <= 16) {
	    return 2 * level + 7;
	} else if (level <= 31) {
	    return 5 * level - 38;
	} else {
	    return 9 * level - 158;
	}
    }

    public static void perform(JobsPlayer jPlayer, ActionInfo info, BufferedPayment payment, Job job) {
	// JobsPayment event
	JobsExpGainEvent JobsExpGainEvent = new JobsExpGainEvent(payment.getOfflinePlayer(), job, payment.getExp());
	Bukkit.getServer().getPluginManager().callEvent(JobsExpGainEvent);
	// If event is canceled, don't do anything
	if (JobsExpGainEvent.isCancelled())
	    return;

	if (!jPlayer.isUnderLimit(CurrencyType.MONEY, payment.getAmount()))
	    return;
	if (!jPlayer.isUnderLimit(CurrencyType.EXP, payment.getExp()))
	    return;
	if (!jPlayer.isUnderLimit(CurrencyType.POINTS, payment.getPoints()))
	    return;

	economy.pay(jPlayer, payment.getAmount(), payment.getPoints(), payment.getExp());

	JobProgression prog = jPlayer.getJobProgression(job);

	int oldLevel = prog.getLevel();

	if (GconfigManager.LoggingUse) {
	    HashMap<CurrencyType, Double> amounts = new HashMap<CurrencyType, Double>();
	    amounts.put(CurrencyType.MONEY, payment.getAmount());
	    amounts.put(CurrencyType.EXP, payment.getExp());
	    amounts.put(CurrencyType.POINTS, payment.getPoints());
	    loging.recordToLog(jPlayer, info, amounts);
	}

	if (prog.addExperience(payment.getExp()))
	    pManager.performLevelUp(jPlayer, prog.getJob(), oldLevel);
    }

    public static void consoleMsg(String msg) {
	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static SelectionManager getSelectionManager() {
	return smanager;
    }

    public static boolean hasPermission(Object sender, String perm, boolean inform) {
	if (sender instanceof Player) {
	    if (((Player) sender).hasPermission(perm))
		return true;
	    if (!inform)
		return false;
	    RawMessage rm = new RawMessage();
	    rm.add(Jobs.getLanguage().getMessage("general.error.permission"), "&2" + perm);
	    rm.show((Player) sender);
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    Jobs.sendMessage(console, Jobs.getLanguage().getMessage("general.error.permission"));
	    return false;
	}
	return true;
    }

    public static void sendMessage(Object sender, String msg) {
	if (sender instanceof Player) {
	    ((Player) sender).sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	} else
	    ((CommandSender) sender).sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void ShowPagination(CommandSender sender, int pageCount, int CurrentPage, String cmd) {
	if (!(sender instanceof Player))
	    return;
	if (!cmd.startsWith("/"))
	    cmd = "/" + cmd;
//	String separator = Jobs.getLanguage().getMessage("command.help.output.fliperSimbols");

	if (pageCount == 1)
	    return;

	int NextPage = CurrentPage + 1;
	NextPage = CurrentPage < pageCount ? NextPage : CurrentPage;
	int Prevpage = CurrentPage - 1;
	Prevpage = CurrentPage > 1 ? Prevpage : CurrentPage;

	RawMessage rm = new RawMessage();
	rm.add((CurrentPage > 1 ? Jobs.getLanguage().getMessage("command.help.output.prev") : Jobs.getLanguage().getMessage("command.help.output.prevOff")), CurrentPage > 1 ? "<<<" : null, CurrentPage > 1
	    ? cmd + " " + Prevpage : null);
	rm.add(pageCount > CurrentPage ? Jobs.getLanguage().getMessage("command.help.output.next") : Jobs.getLanguage().getMessage("command.help.output.nextOff"), pageCount > CurrentPage ? ">>>" : null,
	    pageCount > CurrentPage ? cmd + " " + NextPage : null);
	if (pageCount != 0)
	    rm.show(sender);
    }
}
