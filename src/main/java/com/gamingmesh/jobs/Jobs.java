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

import com.gamingmesh.jobs.CMILib.ActionBarTitleMessages;
import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.CMILib.Reflections;
import com.gamingmesh.jobs.CMILib.VersionChecker;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;
import com.gamingmesh.jobs.Gui.GuiManager;
import com.gamingmesh.jobs.Placeholders.NewPlaceholderAPIHook;
import com.gamingmesh.jobs.Placeholders.Placeholder;
import com.gamingmesh.jobs.Placeholders.PlaceholderAPIHook;
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.config.*;
import com.gamingmesh.jobs.container.*;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.dao.JobsDAOData;
import com.gamingmesh.jobs.dao.JobsManager;
import com.gamingmesh.jobs.economy.*;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.listeners.JobsListener;
import com.gamingmesh.jobs.listeners.JobsPayment14Listener;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.gamingmesh.jobs.listeners.PistonProtectionListener;
import com.gamingmesh.jobs.selection.SelectionManager;
import com.gamingmesh.jobs.stuff.*;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class Jobs extends JavaPlugin {

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
    private static BossBarManager BBManager = null;
    private static ShopManager shopManager = null;
    private static Loging loging = null;
    private static BlockProtectionManager BpManager = null;
    private static JobsManager DBManager = null;

    private static PistonProtectionListener PistonProtectionListener = null;

    private static ConfigManager configManager = null;
    private static GeneralConfigManager GconfigManager = null;

    private static Reflections reflections = null;

    private static JobsDAO dao = null;
    private static List<Job> jobs = null;
    private static Job noneJob = null;
    private static WeakHashMap<Job, Integer> usedSlots = new WeakHashMap<>();
    private static HashMap<Integer, Job> jobsIds = new HashMap<>();
//	public static WeakHashMap<String, Double> GlobalBoost = new WeakHashMap<String, Double>();

    private static BufferedEconomy economy = null;
    private static PermissionHandler permissionHandler = null;
    private static PermissionManager permissionManager = null;

//    private static ItemManager itemManager;

    public static BufferedPaymentThread paymentThread = null;
    private static DatabaseSaveThread saveTask = null;

    public static HashMap<UUID, FastPayment> FastPayment = new HashMap<>();

    private static NMS nms = null;

    private static ActionBarTitleMessages actionbar = null;

    protected static VersionChecker versionCheckManager = null;

    protected static SelectionManager smanager = null;

    private static PointsData pointsDatabase = null;

    public static PistonProtectionListener getPistonProtectionListener() {
	if (PistonProtectionListener == null)
	    PistonProtectionListener = new PistonProtectionListener();
	return PistonProtectionListener;
    }

    private Placeholder Placeholder;
    private boolean PlaceholderAPIEnabled = false;

    public Placeholder getPlaceholderAPIManager() {
	if (Placeholder == null)
	    Placeholder = new Placeholder(this);
	return Placeholder;
    }

    @SuppressWarnings("deprecation")
    private boolean setupPlaceHolderAPI() {
	if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
	    return false;

	try {
	    if (Integer.parseInt(getServer().getPluginManager().getPlugin("PlaceholderAPI")
		.getDescription().getVersion().replace(".", "")) >= Integer.parseInt("2100")) {
		if (new NewPlaceholderAPIHook(this).register())
		    consoleMsg("&e[Jobs] PlaceholderAPI hooked.");
	    } else {
		if (new PlaceholderAPIHook(this).hook())
		    consoleMsg("&e[Jobs] PlaceholderAPI hooked. This is a deprecated version of PlaceholderAPI. Please update "
			+ "to the latest version.");
	    }
	} catch (NumberFormatException e) { // when using a dev build
	    if (new NewPlaceholderAPIHook(this).register()) {
		consoleMsg("&e[Jobs] PlaceholderAPI hooked.");
	    }
	}

	return true;
    }

    public static Loging getLoging() {
	if (loging == null)
	    loging = new Loging();
	return loging;
    }

    public static BlockProtectionManager getBpManager() {
	if (BpManager == null)
	    BpManager = new BlockProtectionManager();
	return BpManager;
    }

    public static Reflections getReflections() {
	if (reflections == null)
	    reflections = new Reflections();
	return reflections;
    }

    public static JobsManager getDBManager() {
	if (DBManager == null)
	    DBManager = new JobsManager(instance);
	return DBManager;
    }

    /**
     * Gets the PointsData
     * @deprecated Use {@link JobsPlayer#getPointsData()}
     * @return {@link PointsData}
     */
    @Deprecated
    public static PointsData getPointsData() {
	if (pointsDatabase == null)
	    pointsDatabase = new PointsData();

	return pointsDatabase;
    }

    public static ShopManager getShopManager() {
	if (shopManager == null) {
	    shopManager = new ShopManager();
	}
	return shopManager;
    }

    public static ConfigManager getConfigManager() {
	if (configManager == null)
	    configManager = new ConfigManager();
	return configManager;
    }

    public static GeneralConfigManager getGCManager() {
	if (GconfigManager == null)
	    GconfigManager = new GeneralConfigManager();
	return GconfigManager;
    }

    public static ActionBarTitleMessages getActionBar() {
	if (actionbar == null)
	    actionbar = new ActionBarTitleMessages();
	return actionbar;
    }

    public static NMS getNms() {
	return nms;
    }

    /**
     * Returns player manager
     * @return the player manager
     */
    public static PlayerManager getPlayerManager() {
	if (pManager == null)
	    pManager = new PlayerManager();
	return pManager;
    }

    public static RestrictedBlockManager getRestrictedBlockManager() {
	if (RBManager == null) {
	    RBManager = new RestrictedBlockManager();
	}

	return RBManager;
    }

    public static RestrictedAreaManager getRestrictedAreaManager() {
	if (RAManager == null) {
	    RAManager = new RestrictedAreaManager();
	}

	return RAManager;
    }

    public static TitleManager gettitleManager() {
	if (titleManager == null) {
	    titleManager = new TitleManager();
	}

	return titleManager;
    }

    private void setBBManager() {
	BBManager = new BossBarManager(this);
    }

    public static BossBarManager getBBManager() {
	return BBManager;
    }

    /**
     * Returns schedule manager
     * @return the schedule manager
     */
    public static ScheduleManager getScheduleManager() {
	if (scheduleManager == null) {
	    scheduleManager = new ScheduleManager(getInstance());
	}

	return scheduleManager;
    }

    public static NameTranslatorManager getNameTranslatorManager() {
	if (NameTranslatorManager == null) {
	    NameTranslatorManager = new NameTranslatorManager();
	}

	return NameTranslatorManager;
    }

    public static GuiManager getGUIManager() {
	if (GUIManager == null)
	    GUIManager = new GuiManager();
	return GUIManager;
    }

    public static JobsCommands getCommandManager() {
	if (cManager == null) {
	    cManager = new JobsCommands(getInstance());
	}
	return cManager;
    }

    public static ExploreManager getExplore() {
	if (exploreManager == null)
	    exploreManager = new ExploreManager();
	return exploreManager;
    }

    /**
     * Returns scoreboard manager
     * @return the scoreboard manager
     */
    public CMIScoreboardManager getCMIScoreboardManager() {
	if (CMIScoreboardManager == null)
	    CMIScoreboardManager = new CMIScoreboardManager();

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
	if (signManager == null) {
	    signManager = new SignUtil();
	}

	return signManager;
    }

    /**
     * Returns language manager
     * @return the language manager
     */
    public static Language getLanguage() {
	if (lManager == null)
	    lManager = new Language(instance);
	return lManager;
    }

    public static LanguageManager getLanguageManager() {
	if (lmManager == null) {
	    lmManager = new LanguageManager();
	}

	return lmManager;
    }

    /**
     * Retrieves the plugin logger
     * @return the plugin logger
     */
    public static Logger getPluginLogger() {
	return instance.getLogger();
    }

    public static File getFolder() {
	File folder = getInstance().getDataFolder();
	if (!folder.exists())
	    folder.mkdirs();
	return folder;
    }

    /**
     * Sets the Data Access Object
     * @param dao - the DAO
     */
    public static void setDAO(JobsDAO dao) {
	Jobs.dao = dao;
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
    public static void setJobs(List<Job> jobs) {
	Jobs.jobs = jobs;
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
    public static void setNoneJob(Job noneJob) {
	Jobs.noneJob = noneJob;
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

    public static Job getJob(int id) {
	return jobsIds.get(id);
    }

    public boolean isPlaceholderAPIEnabled() {
	return PlaceholderAPIEnabled;
    }

    public static HashMap<Integer, Job> getJobsIds() {
	return jobsIds;
    }

    /**
     * Executes startup
     * @throws IOException 
     */
    public void startup() {
	reload(true);
	loadAllPlayersData();
	// add all online players
	for (Player online : Bukkit.getServer().getOnlinePlayers()) {
	    getPlayerManager().playerJoin(online);
	}
    }

    public static void loadAllPlayersData() {
	long time = System.currentTimeMillis();
	// Cloning to avoid issues
	HashMap<UUID, PlayerInfo> temp = new HashMap<>(getPlayerManager().getPlayersInfoUUIDMap());
	HashMap<Integer, List<JobsDAOData>> playersJobs = dao.getAllJobs();
	HashMap<Integer, PlayerPoints> playersPoints = dao.getAllPoints();
	HashMap<Integer, HashMap<String, Log>> playersLogs = dao.getAllLogs();
	HashMap<Integer, ArchivedJobs> playersArchives = dao.getAllArchivedJobs();
	HashMap<Integer, PaymentData> playersLimits = dao.loadPlayerLimits();
	for (Iterator<Entry<UUID, PlayerInfo>> it = temp.entrySet().iterator(); it.hasNext();) {
	    Entry<UUID, PlayerInfo> one = it.next();
	    try {
		int id = one.getValue().getID();
		JobsPlayer jPlayer = getPlayerManager().getJobsPlayerOffline(
		    one.getValue(),
		    playersJobs.get(id),
		    playersPoints.get(id),
		    playersLogs.get(id),
		    playersArchives.get(id),
		    playersLimits.get(id));
		if (jPlayer == null)
		    continue;
		getPlayerManager().addPlayerToCache(jPlayer);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	dao.getMap().clear();
	if (getPlayerManager().getPlayersCache().size() != 0)
	    consoleMsg("&e[Jobs] Preloaded " + getPlayerManager().getPlayersCache().size() + " players data in " +
		((int) (((System.currentTimeMillis() - time) / 1000d) * 100) / 100D));
    }

    /**
     * Executes clean shutdown
     */
    public static void shutdown() {
	if (saveTask != null)
	    saveTask.shutdown();

	if (paymentThread != null)
	    paymentThread.shutdown();

	getPlayerManager().removePlayerAdditions();
	getPlayerManager().saveAll();

	if (dao != null) {
	    dao.closeConnections();
	}

	HandlerList.unregisterAll(instance);
    }

    /**
     * Executes close connections
     */
    public static void convertDatabase() {
	try {
	    List<Convert> archivelist = dao.convertDatabase();

	    getDBManager().switchDataBase();
	    getPlayerManager().reload();

	    dao.truncateAllTables();
	    getPlayerManager().convertChacheOfPlayers(true);

	    dao.continueConvertions(archivelist);
	    getPlayerManager().clearMaps();
	    getPlayerManager().clearCache();

	    dao.saveExplore();
//    Do we really need to convert Block protection?
//    Jobs.getJobsDAO().saveBlockProtection();
	} catch (SQLException e) {
	    e.printStackTrace();
	    Jobs.consoleMsg("&cCan't write data to data base, please send error log to dev's.");
	    return;
	}

	reload();
	loadAllPlayersData();
    }

    /**
     * Checks if player have the given {@link ActionType} in jobs.
     * @param jPlayer {@link JobsPlayer}
     * @param type {@link ActionType}
     * @return true if the player have the given action
     */
    public static boolean isPlayerHaveAction(JobsPlayer jPlayer, ActionType type) {
	if (jPlayer == null || type == null)
	    return false;

	boolean found = false;

	t: for (JobProgression prog : jPlayer.getJobProgression()) {
	    for (JobInfo info : jPlayer.getJobProgression(prog.getJob()).getJob().getJobInfo(type)) {
		if (info.getActionType() == type) {
		    found = true;
		    break t;
		}
	    }

	    for (Quest q : prog.getJob().getQuests()) {
		if (q != null && q.hasAction(type)) {
		    found = true;
		    break t;
		}
	    }
	}

	return found;
    }

    /**
     * Function to get the number of slots used on the server for this job
     * @param job - the job
     * @return the number of slots
     */
    public static int getUsedSlots(Job job) {
	return usedSlots.getOrDefault(job, 0);
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
     * Gets the permission handler
     * @return the permission handler
     */
    public static PermissionHandler getPermissionHandler() {
	if (permissionHandler == null)
	    permissionHandler = new PermissionHandler(instance);
	return permissionHandler;
    }

    public static PermissionManager getPermissionManager() {
	if (permissionManager == null)
	    permissionManager = new PermissionManager();
	return permissionManager;
    }

//    public static ItemManager getItemManager() {
//	return itemManager;
//    }

    /**
     * Sets the economy handler
     * @param eco - the economy handler
     */
    public static void setEconomy(Economy eco) {
	economy = new BufferedEconomy(getInstance(), eco);
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
	if (versionCheckManager == null)
	    versionCheckManager = new VersionChecker(instance);

	return versionCheckManager;
    }

    @Override
    public void onEnable() {
	instance = this;

//	itemManager = new ItemManager(this);

	try {
	    Class<?> nmsClass = Class.forName("com.gamingmesh.jobs.nmsUtil." + Version.getCurrent().getShortVersion());
	    if (NMS.class.isAssignableFrom(nmsClass)) {
		nms = (NMS) nmsClass.getConstructor().newInstance();
	    } else {
		System.out.println("Something went wrong, please note down version and contact author, version: " + Version.getCurrent().toString());
		setEnabled(false);
		return;
	    }
	} catch (Exception e) {
	    System.out.println("Your server version is not compatible with this plugins version! Plugin will be disabled: " + Version.getCurrent().toString());
	    setEnabled(false);
	    e.printStackTrace();
	    return;
	}

	if (setupPlaceHolderAPI()) {
	    consoleMsg("&ePlaceholderAPI was found - Enabling capabilities.");
	    PlaceholderAPIEnabled = true;
	}

	try {
	    YmlMaker jobConfig = new YmlMaker(this, "jobConfig.yml");
	    jobConfig.saveDefaultConfig();

	    YmlMaker jobShopItems = new YmlMaker(this, "shopItems.yml");
	    jobShopItems.saveDefaultConfig();

	    YmlMaker restrictedBlocks = new YmlMaker(this, "restrictedBlocks.yml");
	    restrictedBlocks.saveDefaultConfig();

	    setBBManager();

	    getCommand("jobs").setExecutor(getCommandManager());
	    getCommand("jobs").setTabCompleter(new TabComplete());

	    startup();

	    if (getGCManager().SignsEnabled) {
		YmlMaker jobSigns = new YmlMaker(this, "Signs.yml");
		jobSigns.saveDefaultConfig();
	    }

	    // register the listeners
	    getServer().getPluginManager().registerEvents(new JobsListener(this), this);
	    getServer().getPluginManager().registerEvents(new JobsPaymentListener(this), this);
	    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
		getServer().getPluginManager().registerEvents(new JobsPayment14Listener(), this);
	    }

	    HookManager.loadHooks();

	    if (getGCManager().useBlockProtection) {
		getServer().getPluginManager().registerEvents(getPistonProtectionListener(), this);
	    }

	    // register economy
	    Bukkit.getScheduler().runTask(this, new HookEconomyTask(this));

	    dao.loadBlockProtection();
	    getExplore().load();

	    consoleMsg("&e[Jobs] Plugin has been enabled successfully.");

	    getCommandManager().fillCommands();

	    getDBManager().getDB().triggerTableIdUpdate();
	} catch (Throwable e) {
	    e.printStackTrace();
	    System.out.println("There was some issues when starting plugin. Please contact dev about this. Plugin will be disabled.");
	    setEnabled(false);
	}
    }

    public static void reload() {
	reload(false);
    }

    /**
     * Reloads all data
     */
    public static void reload(boolean startup) {
	// unregister all registered listeners by this plugin and register again
	if (!startup) {
	    org.bukkit.plugin.PluginManager pm = getInstance().getServer().getPluginManager();
	    HandlerList.unregisterAll(instance);
	    com.gamingmesh.jobs.CMIGUI.GUIManager.registerListener();
	    pm.registerEvents(new JobsListener(instance), instance);
	    pm.registerEvents(new JobsPaymentListener(instance), instance);
	    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
		pm.registerEvents(new JobsPayment14Listener(), instance);
	    }

	    if (getGCManager().useBlockProtection)
		pm.registerEvents(getPistonProtectionListener(), instance);

	    if (HookManager.getMcMMOManager().CheckmcMMO()) {
		HookManager.setMcMMOlistener();
	    }
	    if (HookManager.checkMythicMobs()) {
		HookManager.getMythicManager().registerListener();
	    }
	}

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

	getGCManager().reload();
	getLanguage().reload();
	getConfigManager().reload();

	getDBManager().getDB().loadAllJobsWorlds();
	getDBManager().getDB().loadAllJobsNames();

	FurnaceBrewingHandling.load();
	ToggleBarHandling.load();
	usedSlots.clear();
	for (Job job : jobs) {
	    usedSlots.put(job, dao.getSlotsTaken(job));
	}
	getPlayerManager().reload();
	getPermissionHandler().registerPermissions();

	// set the system to auto save
	if (getGCManager().getSavePeriod() > 0) {
	    saveTask = new DatabaseSaveThread(getGCManager().getSavePeriod());
	    saveTask.start();
	}

	// schedule payouts to buffered payments
	paymentThread = new BufferedPaymentThread(getGCManager().getEconomyBatchDelay());
	paymentThread.start();

	dao.loadPlayerData();

	// Schedule
	if (getGCManager().enableSchedule) {
	    getScheduleManager().load();
	    getScheduleManager().start();
	} else
	    getScheduleManager().cancel();

    }

    @Override
    public void onDisable() {
	if (instance == null)
	    return;

//	GUIManager.CloseInventories();
//	shopManager.CloseInventories();
	dao.saveExplore();

	getBpManager().saveCache();
	FurnaceBrewingHandling.save();
	ToggleBarHandling.save();

	shutdown();
	instance = null;
	consoleMsg("&e[Jobs] &2Plugin has been disabled successfully.");
	setEnabled(false);
    }

    private static void checkDailyQuests(JobsPlayer jPlayer, Job job, ActionInfo info) {
	if (!job.getQuests().isEmpty()) {
	    List<QuestProgression> q = jPlayer.getQuestProgressions(job, info.getType());
	    for (QuestProgression one : q) {
		if (one != null) {
		    one.processQuest(jPlayer, info);
		}
	    }
	}
    }

    /**
     * Performed an action
     * 
     * Give correct experience and income
     * @param jPlayer - the player
     * @param info - the action
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

	if (!isBpOk(jPlayer, info, block, true))
	    return;

	// no job
	if (numjobs == 0) {
	    if (noneJob == null)
		return;

	    if (noneJob.isWorldBlackListed(block) || noneJob.isWorldBlackListed(block, ent) || noneJob.isWorldBlackListed(victim))
		return;

	    JobInfo jobinfo = noneJob.getJobInfo(info, 1);

	    checkDailyQuests(jPlayer, noneJob, info);

	    if (jobinfo == null)
		return;

	    Double income = jobinfo.getIncome(1, numjobs);
	    Double pointAmount = jobinfo.getPoints(1, numjobs);

	    if (income == 0D && pointAmount == 0D)
		return;

	    Boost boost = getPlayerManager().getFinalBonus(jPlayer, noneJob);

	    JobsPrePaymentEvent JobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), noneJob, income,
		pointAmount, block, ent, victim, info);
	    Bukkit.getServer().getPluginManager().callEvent(JobsPrePaymentEvent);
	    // If event is canceled, don't do anything
	    if (JobsPrePaymentEvent.isCancelled()) {
		income = 0D;
		pointAmount = 0D;
	    } else {
		income = JobsPrePaymentEvent.getAmount();
		pointAmount = JobsPrePaymentEvent.getPoints();
	    }

	    // Calculate income

	    if (income != 0D) {
		income = boost.getFinalAmount(CurrencyType.MONEY, income);
		if (GconfigManager.useMinimumOveralPayment && income > 0) {
		    double maxLimit = income * GconfigManager.MinimumOveralPaymentLimit;
		    if (income < maxLimit)
			income = maxLimit;
		}
	    }

	    // Calculate points

	    if (pointAmount != 0D) {
		pointAmount = boost.getFinalAmount(CurrencyType.POINTS, pointAmount);
		if (GconfigManager.useMinimumOveralPoints && pointAmount > 0) {
		    double maxLimit = pointAmount * GconfigManager.MinimumOveralPaymentLimit;
		    if (pointAmount < maxLimit)
			pointAmount = maxLimit;
		}
	    }
	    if (!jPlayer.isUnderLimit(CurrencyType.MONEY, income)) {
		if (GconfigManager.useMaxPaymentCurve) {
		    double percentOver = jPlayer.percentOverLimit(CurrencyType.MONEY);
		    float factor = GconfigManager.maxPaymentCurveFactor;
		    double percentLoss = 100 / ((1 / factor * percentOver * percentOver) + 1);
		    income = income - (income * percentLoss / 100);
		} else
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
		getBpManager().remove(block);

	    if (pointAmount != 0D)
		jPlayer.setSaved(false);

	    HashMap<CurrencyType, Double> payments = new HashMap<CurrencyType, Double>();
	    if (income != 0D)
		payments.put(CurrencyType.MONEY, income);
	    if (pointAmount != 0D)
		payments.put(CurrencyType.POINTS, pointAmount);

	    economy.pay(jPlayer, payments);

	    if (GconfigManager.LoggingUse) {
		HashMap<CurrencyType, Double> amounts = new HashMap<>();
		amounts.put(CurrencyType.MONEY, income);
		getLoging().recordToLog(jPlayer, info, amounts);
	    }

	} else {
	    FastPayment.clear();

	    List<Job> expiredJobs = new ArrayList<>();
	    for (JobProgression prog : progression) {
		if (prog.getJob().isWorldBlackListed(block) || prog.getJob().isWorldBlackListed(block, ent)
		    || prog.getJob().isWorldBlackListed(victim))
		    continue;

		if (jPlayer.isLeftTimeEnded(prog.getJob())) {
		    expiredJobs.add(prog.getJob());
		}

		int level = prog.getLevel();

		JobInfo jobinfo = prog.getJob().getJobInfo(info, level);

		checkDailyQuests(jPlayer, prog.getJob(), info);

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
			    if (expAmount.doubleValue() < 0)
				expInt--;
			    else
				expInt++;
			}

			if (expInt < 0 && getPlayerExperience(player) < -expInt) {
			    player.setLevel(0);
			    player.setTotalExperience(0);
			    player.setExp(0);
			} else
			    player.giveExp(expInt);
		    }
		}

		Boost boost = getPlayerManager().getFinalBonus(jPlayer, prog.getJob(), ent, victim);

		JobsPrePaymentEvent JobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), prog.getJob(), income,
		    pointAmount, block, ent, victim, info);

		Bukkit.getServer().getPluginManager().callEvent(JobsPrePaymentEvent);
		// If event is canceled, don't do anything
		if (JobsPrePaymentEvent.isCancelled()) {
		    income = 0D;
		    pointAmount = 0D;
		} else {
		    income = JobsPrePaymentEvent.getAmount();
		    pointAmount = JobsPrePaymentEvent.getPoints();
		}

		// Calculate income
		if (income != 0D) {
		    income = boost.getFinalAmount(CurrencyType.MONEY, income);
		    if (GconfigManager.useMinimumOveralPayment && income > 0) {
			double maxLimit = income * GconfigManager.MinimumOveralPaymentLimit;
			if (income < maxLimit)
			    income = maxLimit;
		    }
		}

		// Calculate points
		if (pointAmount != 0D) {
		    pointAmount = boost.getFinalAmount(CurrencyType.POINTS, pointAmount);
		    if (GconfigManager.useMinimumOveralPoints && pointAmount > 0) {
			double maxLimit = pointAmount * GconfigManager.MinimumOveralPaymentLimit;
			if (pointAmount < maxLimit)
			    pointAmount = maxLimit;
		    }
		}

		// Calculate exp
		expAmount = boost.getFinalAmount(CurrencyType.EXP, expAmount);

		if (GconfigManager.useMinimumOveralPayment && expAmount > 0) {
		    double maxLimit = expAmount * GconfigManager.MinimumOveralPaymentLimit;
		    if (expAmount < maxLimit)
			expAmount = maxLimit;
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

		// JobsPayment event
		JobsExpGainEvent JobsExpGainEvent = new JobsExpGainEvent(jPlayer.getPlayer(), prog.getJob(), expAmount);
		Bukkit.getServer().getPluginManager().callEvent(JobsExpGainEvent);
		// If event is canceled, don't do anything
		if (JobsExpGainEvent.isCancelled())
		    expAmount = 0D;
		else
		    expAmount = JobsExpGainEvent.getExp();

		try {
		    if (expAmount != 0D && GconfigManager.BossBarEnabled)
			if (GconfigManager.BossBarShowOnEachAction)
			    BBManager.ShowJobProgression(jPlayer, prog, expAmount);
			else
			    jPlayer.getUpdateBossBarFor().add(prog.getJob().getName());
		} catch (Throwable e) {
		    consoleMsg("&c[Jobs] Some issues with boss bar feature accured, try disabling it to avoid it.");
		}

		HashMap<CurrencyType, Double> payments = new HashMap<CurrencyType, Double>();
		if (income != 0D)
		    payments.put(CurrencyType.MONEY, income);
		if (pointAmount != 0D)
		    payments.put(CurrencyType.POINTS, pointAmount);
		if (expAmount != 0D)
		    payments.put(CurrencyType.EXP, expAmount);

		FastPayment.put(jPlayer.getUniqueId(), new FastPayment(jPlayer, info, new BufferedPayment(jPlayer.getPlayer(), payments), prog
		    .getJob()));

		economy.pay(jPlayer, payments);
		int oldLevel = prog.getLevel();

		if (GconfigManager.LoggingUse) {
		    HashMap<CurrencyType, Double> amounts = new HashMap<>();
		    amounts.put(CurrencyType.MONEY, income);
		    amounts.put(CurrencyType.EXP, expAmount);
		    amounts.put(CurrencyType.POINTS, pointAmount);
		    getLoging().recordToLog(jPlayer, info, amounts);
		}

		if (prog.addExperience(expAmount))
		    getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
	    }

	    //need to update bp
	    if (block != null) {
		BlockProtection bp = getBpManager().getBp(block.getLocation());
		if (bp != null)
		    bp.setPaid(true);
	    }

	    expiredJobs.forEach(j -> getPlayerManager().leaveJob(jPlayer, j));
	    expiredJobs.clear();
	}
    }

    private static boolean isBpOk(JobsPlayer player, ActionInfo info, Block block, boolean inform) {
	if (block == null || !GconfigManager.useBlockProtection)
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
		    getBpManager().remove(block);
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
			    getActionBar().send(player.getPlayer(), lManager.getMessage("message.blocktimer", "[time]", sec));
		    }
		    return false;
		}
		getBpManager().add(block, cd);
		if ((cd == null || cd == 0) && GconfigManager.useGlobalTimer) {
		    getBpManager().add(block, GconfigManager.globalblocktimer);
		}
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
				getActionBar().send(player.getPlayer(), lManager.getMessage("message.blocktimer", "[time]", sec));
			}
			getBpManager().add(block, cd);
			return false;
		    }
		} else if (bp.isPaid().booleanValue() && bp.getTime() == -1L && cd != null && cd == -1) {
		    getBpManager().add(block, cd);
		    return false;
		} else
		    getBpManager().add(block, cd);
	    } else
		getBpManager().add(block, getBpManager().getBlockDelayTime(block));
	}

	return true;
    }

    private static int getPlayerExperience(Player player) {
	int bukkitExp = (ExpToLevel(player.getLevel()) + Math.round(deltaLevelToExp(player.getLevel()) * player.getExp()));
	return bukkitExp;
    }

    // total xp calculation based by lvl
    private static int ExpToLevel(int level) {
	if (Version.isCurrentEqualOrLower(Version.v1_7_R4)) {
	    if (level <= 16)
		return 17 * level;
	    else if (level <= 31)
		return ((3 * level * level) / 2) - ((59 * level) / 2) + 360;
	    else
		return ((7 * level * level) / 2) - ((303 * level) / 2) + 2220;
	}
	if (level <= 16)
	    return (level * level) + (6 * level);
	else if (level <= 31)
	    return (int) ((2.5 * level * level) - (40.5 * level) + 360);
	else
	    return (int) ((4.5 * level * level) - (162.5 * level) + 2220);
    }

    // xp calculation for one current lvl
    private static int deltaLevelToExp(int level) {
	if (Version.isCurrentEqualOrLower(Version.v1_7_R4)) {
	    if (level <= 16)
		return 17;
	    else if (level <= 31)
		return 3 * level - 31;
	    else
		return 7 * level - 155;
	}
	if (level <= 16)
	    return 2 * level + 7;
	else if (level <= 31)
	    return 5 * level - 38;
	else
	    return 9 * level - 158;
    }

    public static void perform(JobsPlayer jPlayer, ActionInfo info, BufferedPayment payment, Job job) {
	// JobsPayment event
	JobsExpGainEvent JobsExpGainEvent = new JobsExpGainEvent(payment.getOfflinePlayer(), job, payment.get(CurrencyType.EXP));
	Bukkit.getServer().getPluginManager().callEvent(JobsExpGainEvent);
	// If event is canceled, don't do anything
	if (JobsExpGainEvent.isCancelled())
	    return;

	for (CurrencyType one : CurrencyType.values()) {
	    if (!jPlayer.isUnderLimit(one, payment.get(one)))
		return;
	}

	economy.pay(jPlayer, payment.getPayment());

	JobProgression prog = jPlayer.getJobProgression(job);

	int oldLevel = prog.getLevel();

	if (GconfigManager.LoggingUse) {
	    getLoging().recordToLog(jPlayer, info, payment.getPayment());
	}

	if (prog.addExperience(payment.get(CurrencyType.EXP)))
	    getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
    }

    public static void consoleMsg(String msg) {
	if (msg != null) {
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
    }

    public static SelectionManager getSelectionManager() {
	return smanager;
    }

    public static boolean hasPermission(Object sender, String perm, boolean rawEnable) {
	if (!(sender instanceof Player))
	    return true;

	if (((Player) sender).hasPermission(perm))
	    return true;
	if (!rawEnable) {
	    ((Player) sender).sendMessage(lManager.getMessage("general.error.permission"));
	    return false;
	}
	RawMessage rm = new RawMessage();
	rm.add(lManager.getMessage("general.error.permission"), "&2" + perm);
	rm.show((Player) sender);
	return false;

    }

    public void ShowPagination(CommandSender sender, PageInfo pi, String cmd) {
	ShowPagination(sender, pi.getTotalPages(), pi.getCurrentPage(), pi.getTotalEntries(), cmd, null);
    }

    public void ShowPagination(CommandSender sender, PageInfo pi, String cmd, String pagePref) {
	ShowPagination(sender, pi.getTotalPages(), pi.getCurrentPage(), pi.getTotalEntries(), cmd, pagePref);
    }

    public void ShowPagination(CommandSender sender, int pageCount, int CurrentPage, int totalEntries, String cmd, String pagePref) {
	if (!(sender instanceof Player))
	    return;
	if (!cmd.startsWith("/"))
	    cmd = "/" + cmd;
	if (pageCount == 1)
	    return;
	String pagePrefix = pagePref == null ? "" : pagePref;
	int NextPage = CurrentPage + 1;
	NextPage = CurrentPage < pageCount ? NextPage : CurrentPage;
	int Prevpage = CurrentPage - 1;
	Prevpage = CurrentPage > 1 ? Prevpage : CurrentPage;

	RawMessage rm = new RawMessage();

	rm.add((CurrentPage > 1 ? lManager.getMessage("command.help.output.prevPage") : lManager.getMessage("command.help.output.prevPageOff")),
	    CurrentPage > 1 ? "<<<" : ">|",
	    CurrentPage > 1 ? cmd + " " + pagePrefix + Prevpage : cmd + " " + pagePrefix + pageCount);
	rm.add(lManager.getMessage("command.help.output.pageCount", "[current]", CurrentPage, "[total]", pageCount), lManager.getMessage("command.help.output.pageCountHover", "[totalEntries]",
	    totalEntries));
	rm.add(pageCount > CurrentPage ? lManager.getMessage("command.help.output.nextPage") : lManager.getMessage("command.help.output.nextPageOff"),
	    pageCount > CurrentPage ? ">>>" : "|<",
	    pageCount > CurrentPage ? cmd + " " + pagePrefix + NextPage : cmd + " " + pagePrefix + 1);
	if (pageCount != 0)
	    rm.show(sender);
    }
}
