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

import com.gamingmesh.jobs.CMILib.RawMessage;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.CMILib.ActionBarManager;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.CMIReflections;
import com.gamingmesh.jobs.CMILib.VersionChecker;
import com.gamingmesh.jobs.Gui.GuiManager;
import com.gamingmesh.jobs.Placeholders.PlaceholderAPIHook;
import com.gamingmesh.jobs.Placeholders.Placeholder;
import com.gamingmesh.jobs.hooks.HookManager;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.config.*;
import com.gamingmesh.jobs.container.*;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
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
import com.gamingmesh.jobs.stuff.complement.JobsChatEvent;
import com.gamingmesh.jobs.stuff.complement.Complement;
import com.gamingmesh.jobs.stuff.complement.Complement1;
import com.gamingmesh.jobs.stuff.complement.Complement2;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Jobs extends JavaPlugin {

    private static PlayerManager pManager;
    private static JobsCommands cManager;
    private static Language lManager;
    private static LanguageManager lmManager;
    private static SignUtil signManager;
    private static ScheduleManager scheduleManager;
    private static NameTranslatorManager nameTranslatorManager;
    private static ExploreManager exploreManager;
    private static TitleManager titleManager;
    private static RestrictedBlockManager rbManager;
    private static RestrictedAreaManager raManager;
    private static BossBarManager bbManager;
    private static ShopManager shopManager;
    private static Loging loging;
    private static BlockProtectionManager bpManager;
    private static JobsManager dbManager;
    private static ConfigManager configManager;
    private static GeneralConfigManager gConfigManager;
    private static CMIReflections reflections;
    private static BufferedEconomy economy;
    private static PermissionHandler permissionHandler;
    private static PermissionManager permissionManager;

    private final Set<BlockOwnerShip> blockOwnerShips = new HashSet<>();

    private boolean kyoriSupported = false;

    private CMIScoreboardManager cmiScoreboardManager;
    private Complement complement;
    private GuiManager guiManager;

    private static JobsDAO dao;
    private static List<Job> jobs;
    private static Job noneJob;
    private static Map<Job, Integer> usedSlots = new WeakHashMap<>();

    public static BufferedPaymentThread paymentThread;
    private static DatabaseSaveThread saveTask;

    public static final Map<UUID, FastPayment> FASTPAYMENT = new HashMap<>();

    private static NMS nms;

    protected static VersionChecker versionCheckManager;
    protected static SelectionManager smanager;

    private static PointsData pointsDatabase;

    public Complement getComplement() {
	return complement;
    }

    public boolean isKyoriSupported() {
	return kyoriSupported;
    }

	/**
     * Returns the block owner ship for specific {@link CMIMaterial} type.
     * 
     * @param type {@link CMIMaterial}
     * @see #getBlockOwnerShip(CMIMaterial, boolean)
     * @return {@link BlockOwnerShip}, otherwise {@link Optional#empty()}
     */
    public Optional<BlockOwnerShip> getBlockOwnerShip(CMIMaterial type) {
	return getBlockOwnerShip(type, true);
    }

    /**
     * Returns the block owner ship for specific {@link CMIMaterial} type.
     * If the addNew parameter is enabled, it will cache a new owner ship for specific
     * {@link CMIMaterial} type.
     * 
     * @param type {@link CMIMaterial}
     * @param addNew whenever to add a new owner ship
     * @return {@link BlockOwnerShip}, otherwise {@link Optional#empty()}
     */
    public Optional<BlockOwnerShip> getBlockOwnerShip(CMIMaterial type, boolean addNew) {
	BlockOwnerShip b = null;
	for (BlockOwnerShip ship : blockOwnerShips) {
	    if (ship.getMaterial() == type) {
		b = ship;
		break;
	    }
	}

	if (addNew && b == null) {
	    blockOwnerShips.add(b = new BlockOwnerShip(type));
	}

	return Optional.ofNullable(b);
    }

    /**
     * Returns the block owner ship for specific {@link BlockTypes} type.
     * 
     * @param type {@link BlockTypes}
     * @return {@link BlockOwnerShip}, otherwise {@link Optional#empty()}
     */
    public Optional<BlockOwnerShip> getBlockOwnerShip(BlockTypes type) {
	for (BlockOwnerShip ship : blockOwnerShips) {
	    if (ship.getType() == type) {
		return Optional.ofNullable(ship);
	    }
	}

	return Optional.empty();
    }

    /**
     * @return a set of block owner ships.
     */
    public Set<BlockOwnerShip> getBlockOwnerShips() {
	return blockOwnerShips;
    }

    private Placeholder placeholder;
    private boolean placeholderAPIEnabled = false;

    public Placeholder getPlaceholderAPIManager() {
	if (placeholder == null)
	    placeholder = new Placeholder(this);
	return placeholder;
    }

    private boolean setupPlaceHolderAPI() {
	org.bukkit.plugin.Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
	if (papi == null || !papi.isEnabled())
	    return false;

	try {
	    if (Integer.parseInt(papi
		.getDescription().getVersion().replaceAll("[^\\d]", "")) >= 2100 && new PlaceholderAPIHook(this).register()) {
		consoleMsg("&e[Jobs] PlaceholderAPI hooked.");
	    }
	} catch (NumberFormatException ex) {
	    return false;
	}

	return true;
    }

    public static Loging getLoging() {
	if (loging == null)
	    loging = new Loging();
	return loging;
    }

    public static BlockProtectionManager getBpManager() {
	if (bpManager == null)
	    bpManager = new BlockProtectionManager();
	return bpManager;
    }

    public static CMIReflections getReflections() {
	if (reflections == null)
	    reflections = new CMIReflections();
	return reflections;
    }

    public static JobsManager getDBManager() {
	if (dbManager == null)
	    dbManager = new JobsManager(instance);
	return dbManager;
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
	if (gConfigManager == null)
	    gConfigManager = new GeneralConfigManager();
	return gConfigManager;
    }

    public static NMS getNms() {
	return nms;
    }

    /**
     * @return {@link PlayerManager}
     */
    public static PlayerManager getPlayerManager() {
	if (pManager == null)
	    pManager = new PlayerManager(instance);
	return pManager;
    }

    public static RestrictedBlockManager getRestrictedBlockManager() {
	if (rbManager == null) {
	    rbManager = new RestrictedBlockManager();
	}

	return rbManager;
    }

    public static RestrictedAreaManager getRestrictedAreaManager() {
	if (raManager == null) {
	    raManager = new RestrictedAreaManager();
	}

	return raManager;
    }

    /**
     * @return {@link TitleManager}
     */
    public static TitleManager getTitleManager() {
	if (titleManager == null) {
	    titleManager = new TitleManager();
	}

	return titleManager;
    }

    public static BossBarManager getBBManager() {
	return bbManager;
    }

    /**
     * Returns schedule manager
     * @return the schedule manager
     */
    public static ScheduleManager getScheduleManager() {
	if (scheduleManager == null) {
	    scheduleManager = new ScheduleManager(instance);
	}

	return scheduleManager;
    }

    public static NameTranslatorManager getNameTranslatorManager() {
	if (nameTranslatorManager == null) {
	    nameTranslatorManager = new NameTranslatorManager();
	}

	return nameTranslatorManager;
    }

    public GuiManager getGUIManager() {
	if (guiManager == null)
	    guiManager = new GuiManager(this);
	return guiManager;
    }

    public static JobsCommands getCommandManager() {
	if (cManager == null) {
	    cManager = new JobsCommands(instance);
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
	if (cmiScoreboardManager == null)
	    cmiScoreboardManager = new CMIScoreboardManager();

	return cmiScoreboardManager;
    }

    // TODO Get rid of this entirely from project
    // There are better implementations than this
    protected static Jobs instance;

    /**
     * This shouldn't be used.
     * @return returns this class object instance
     */
    @Deprecated
    public static Jobs getInstance() {
	return instance;
    }

    /**
     * Returns sign manager
     * @return the sign manager
     */
    public static SignUtil getSignUtil() {
	if (signManager == null) {
	    signManager = new SignUtil(instance);
	}

	return signManager;
    }

    /**
     * Returns language manager
     * @return the language manager
     */
    public static Language getLanguage() {
	if (lManager == null)
	    lManager = new Language();
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
	File folder = instance.getDataFolder();
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
	    if (job.getJobFullName().equalsIgnoreCase(jobName))
		return job;
	}
	return null;
    }

    /**
     * Returns a job by identifier.
     * 
     * @param id the id of job
     * @return {@link Job}
     */
    public static Job getJob(int id) {
	for (Job job : jobs) {
	    if (job.getId() == id) {
		return job;
	    }
	}

	return null;
    }

    public boolean isPlaceholderAPIEnabled() {
	return placeholderAPIEnabled;
    }

    private void startup() {
	reload(true);

	CompletableFuture<Void> pd = loadAllPlayersData();

	// attempt to add all online players to cache
	pd.thenAccept(e -> getServer().getOnlinePlayers().forEach(getPlayerManager()::playerJoin));
    }

    public static CompletableFuture<Void> loadAllPlayersData() {
	return CompletableFuture.supplyAsync(() -> {
	    long time = System.currentTimeMillis();
	    // Cloning to avoid issues
	    Map<UUID, PlayerInfo> temp = new HashMap<>(getPlayerManager().getPlayersInfoUUIDMap());
	    Map<Integer, List<JobsDAOData>> playersJobs = dao.getAllJobs();
	    Map<Integer, PlayerPoints> playersPoints = dao.getAllPoints();
	    Map<Integer, Map<String, Log>> playersLogs = dao.getAllLogs();
	    Map<Integer, ArchivedJobs> playersArchives = dao.getAllArchivedJobs();
	    Map<Integer, PaymentData> playersLimits = dao.loadPlayerLimits();

	    for (Iterator<PlayerInfo> it = temp.values().iterator(); it.hasNext();) {
		PlayerInfo one = it.next();
		int id = one.getID();
		JobsPlayer jPlayer = getPlayerManager().getJobsPlayerOffline(
		    one,
		    playersJobs.get(id),
		    playersPoints.get(id),
		    playersLogs.get(id),
		    playersArchives.get(id),
		    playersLimits.get(id));
		if (jPlayer != null)
		    getPlayerManager().addPlayerToCache(jPlayer);
	    }

	    return time;
	}).thenAccept(t -> {
	    if (!getPlayerManager().getPlayersCache().isEmpty())
		consoleMsg("&e[Jobs] Preloaded " + getPlayerManager().getPlayersCache().size() + " players data in " +
		    ((int) (((System.currentTimeMillis() - t) / 1000d) * 100) / 100D));
	});
    }

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
     * Checks if the given {@link JobsPlayer} have the given {@link ActionType} in one of jobs.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param type {@link ActionType}
     * @return true if the player have the given action
     */
    public static boolean isPlayerHaveAction(JobsPlayer jPlayer, ActionType type) {
	if (jPlayer == null || type == null)
	    return false;

	boolean found = false;

	for (JobProgression prog : jPlayer.getJobProgression()) {
	    for (JobInfo info : prog.getJob().getJobInfo(type)) {
		if (info.getActionType() == type) {
		    found = true;
		    break;
		}
	    }

	    if (!found) {
		for (Quest q : prog.getJob().getQuests()) {
		    if (q != null && q.hasAction(type)) {
			found = true;
			break;
		    }
		}
	    }

	    if (found) {
		break;
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
	Integer used = usedSlots.get(job);
	if (used != null)
	    usedSlots.put(job, used + 1);
    }

    /**
     * Function to decrease the number of used slots for a job
     * @param job - the job someone is leaving
     */
    public static void leaveSlot(Job job) {
	Integer used = usedSlots.get(job);
	if (used != null)
	    usedSlots.put(job, used - 1);
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

    /**
     * Sets the economy handler
     * @param eco - the economy handler
     */
    public static void setEconomy(Economy eco) {
	economy = new BufferedEconomy(instance, eco);
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

	try {
	    Class.forName("net.kyori.adventure.text.Component");
	    kyoriSupported = true;
	} catch (ClassNotFoundException e) {
	}

	placeholderAPIEnabled = setupPlaceHolderAPI();

	try {
	    new YmlMaker(getFolder(), "shopItems.yml").saveDefaultConfig();
	    new YmlMaker(getFolder(), "restrictedBlocks.yml").saveDefaultConfig();

	    bbManager = new BossBarManager(this);

	    Optional.ofNullable(getCommand("jobs")).ifPresent(j -> {
		j.setExecutor(getCommandManager());
		j.setTabCompleter(new TabComplete());
	    });

	    startup();

	    if (getGCManager().SignsEnabled) {
		new YmlMaker(getFolder(), "Signs.yml").saveDefaultConfig();
	    }

	    // register the listeners
	    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
		getServer().getPluginManager().registerEvents(new com.gamingmesh.jobs.listeners.Listener1_9(), instance);
	    }

	    getServer().getPluginManager().registerEvents(new JobsListener(this), this);
	    getServer().getPluginManager().registerEvents(new JobsPaymentListener(this), this);
	    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
		getServer().getPluginManager().registerEvents(new JobsPayment14Listener(), this);
	    }

	    HookManager.loadHooks();

	    if (getGCManager().useBlockProtection) {
		getServer().getPluginManager().registerEvents(new PistonProtectionListener(), this);
	    }

	    if (Version.isCurrentEqualOrHigher(Version.v1_16_R3) && kyoriSupported) {
		complement = new Complement2();
		//getServer().getPluginManager().registerEvents(new KyoriChatEvent(this), this);
	    } else {
		complement = new Complement1();
	    }
	    getServer().getPluginManager().registerEvents(new JobsChatEvent(this), this);

	    // register economy
	    getServer().getScheduler().runTask(this, new HookEconomyTask(this));

	    dao.loadBlockProtection();
	    getExplore().load();
	    getCommandManager().fillCommands();
	    getDBManager().getDB().triggerTableIdUpdate();

	    consoleMsg("&e[Jobs] Plugin has been enabled successfully.");
	} catch (Throwable e) {
	    e.printStackTrace();
	    System.out.println("There was some issues when starting plugin. Please contact dev about this. Plugin will be disabled.");
	    setEnabled(false);
	}
    }

    public static void reload() {
	reload(false);
    }

    public static void reload(boolean startup) {
	// unregister all registered listeners by this plugin and register again
	if (!startup) {
	    org.bukkit.plugin.PluginManager pm = instance.getServer().getPluginManager();

	    HandlerList.unregisterAll(instance);

	    com.gamingmesh.jobs.CMIGUI.GUIManager.registerListener();

	    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
		pm.registerEvents(new com.gamingmesh.jobs.listeners.Listener1_9(), instance);
	    }

	    pm.registerEvents(new JobsListener(instance), instance);
	    pm.registerEvents(new JobsPaymentListener(instance), instance);
	    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
		pm.registerEvents(new JobsPayment14Listener(), instance);
	    }

	    if (getGCManager().useBlockProtection) {
		pm.registerEvents(new PistonProtectionListener(), instance);
	    }

	    if (HookManager.getMcMMOManager().CheckmcMMO()) {
		HookManager.setMcMMOlistener();
	    }
	    if (HookManager.checkMythicMobs()) {
		HookManager.getMythicManager().registerListener();
	    }
	}

	if (saveTask != null) {
	    saveTask.shutdown();
	}

	if (paymentThread != null) {
	    paymentThread.shutdown();
	}

	smanager = new SelectionManager();

	getGCManager().reload();
	getLanguage().reload();
	getConfigManager().reload();

	getDBManager().getDB().loadAllJobsWorlds();
	getDBManager().getDB().loadAllJobsNames();

	if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
	    instance.getBlockOwnerShip(CMIMaterial.LEGACY_BREWING_STAND).ifPresent(BlockOwnerShip::load);
	    instance.getBlockOwnerShip(CMIMaterial.LEGACY_BURNING_FURNACE).ifPresent(BlockOwnerShip::load);
	} else {
	    instance.getBlockOwnerShip(CMIMaterial.FURNACE).ifPresent(BlockOwnerShip::load);
	    instance.getBlockOwnerShip(CMIMaterial.BREWING_STAND).ifPresent(BlockOwnerShip::load);
	}
	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
	    instance.getBlockOwnerShip(CMIMaterial.BLAST_FURNACE).ifPresent(BlockOwnerShip::load);
	    instance.getBlockOwnerShip(CMIMaterial.SMOKER).ifPresent(BlockOwnerShip::load);
	}

	ToggleBarHandling.load();
	usedSlots.clear();
	for (Job job : jobs) {
	    usedSlots.put(job, dao.getSlotsTaken(job));
	}
	getPlayerManager().reload();
	getPermissionHandler().registerPermissions();

	// set the system to auto save
	saveTask = new DatabaseSaveThread(getGCManager().getSavePeriod());
	saveTask.start();

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

	HandlerList.unregisterAll(instance);

	if (dao != null) {
	    dao.saveExplore();
	}

	blockOwnerShips.forEach(BlockOwnerShip::save);
	ToggleBarHandling.save();

	if (saveTask != null)
	    saveTask.shutdown();

	if (paymentThread != null)
	    paymentThread.shutdown();

	if (pManager != null) {
	    pManager.removePlayerAdditions();
	    pManager.saveAll();
	}

	if (dao != null) {
	    dao.closeConnections();
	}

	instance = null;
	consoleMsg("&e[Jobs] &2Plugin has been disabled successfully.");
    }

    private static void checkDailyQuests(JobsPlayer jPlayer, Job job, ActionInfo info) {
	if (!job.getQuests().isEmpty()) {
	    for (QuestProgression one : jPlayer.getQuestProgressions(job, info.getType())) {
		one.processQuest(jPlayer, info);
	    }
	}
    }

    /**
     * Perform an action for the given {@link JobsPlayer} with the given action info.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param info {@link ActionInfo}
     * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
     */
    public static void action(JobsPlayer jPlayer, ActionInfo info) {
	action(jPlayer, info, null, null, null);
    }

    /**
     * Perform an action for the given {@link JobsPlayer} with the given action info and block.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param info {@link ActionInfo}
     * @param block {@link Block}
     * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
     */
    public static void action(JobsPlayer jPlayer, ActionInfo info, Block block) {
	action(jPlayer, info, block, null, null);
    }

    /**
     * Perform an action for the given {@link JobsPlayer} with the given action info and entity.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param info {@link ActionInfo}
     * @param ent {@link Entity}
     * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
     */
    public static void action(JobsPlayer jPlayer, ActionInfo info, Entity ent) {
	action(jPlayer, info, null, ent, null);
    }

    /**
     * Perform an action for the given {@link JobsPlayer} with the given action info,
     * entity and living entity.
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param info {@link ActionInfo}
     * @param ent {@link Entity}
     * @param victim {@link LivingEntity}
     * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
     */
    public static void action(JobsPlayer jPlayer, ActionInfo info, Entity ent, LivingEntity victim) {
	action(jPlayer, info, null, ent, victim);
    }

    /**
     * Perform an action for the given {@link JobsPlayer} with the parameters.
     * <p>
     * The process:
     * <p>
     * If the player does not have any job progression cached into memory, the player
     * only retrieve the "noneJob" by default. This means that there will be no any
     * extra income calculations and the player does no get the full income from jobs,
     * but the half of it.<br>
     * In other cases if player have at least 1 job cached, they will get the full income
     * with the extra calculated multiplications including bonuses and limits.
     * <p>
     * 
     * <b>This usually not be called in your code, to avoid misbehaviour working ability.</b>
     * 
     * @param jPlayer {@link JobsPlayer}
     * @param info {@link ActionInfo}
     * @param ent {@link Entity}
     * @param victim {@link LivingEntity}
     * @param block {@link Block}
     * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
     */
    public static void action(JobsPlayer jPlayer, ActionInfo info, Block block, Entity ent, LivingEntity victim) {
	if (jPlayer == null)
	    return;

	List<JobProgression> progression = jPlayer.getJobProgression();
	int numjobs = progression.size();

	if (!isBpOk(jPlayer, info, block, true))
	    return;

	// no job
	if (numjobs == 0) {
	    if (noneJob == null || noneJob.isWorldBlackListed(block) || noneJob.isWorldBlackListed(block, ent) || noneJob.isWorldBlackListed(victim))
		return;

	    JobInfo jobinfo = noneJob.getJobInfo(info, 1);

	    checkDailyQuests(jPlayer, noneJob, info);

	    if (jobinfo == null)
		return;

	    double income = jobinfo.getIncome(1, numjobs, jPlayer.maxJobsEquation);
	    double pointAmount = jobinfo.getPoints(1, numjobs, jPlayer.maxJobsEquation);

	    if (income == 0D && pointAmount == 0D)
		return;

	    Boost boost = getPlayerManager().getFinalBonus(jPlayer, noneJob);

	    JobsPrePaymentEvent jobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), noneJob, income,
		pointAmount, block, ent, victim, info);
	    Bukkit.getServer().getPluginManager().callEvent(jobsPrePaymentEvent);
	    // If event is canceled, don't do anything
	    if (jobsPrePaymentEvent.isCancelled()) {
		income = 0D;
		pointAmount = 0D;
	    } else {
		income = jobsPrePaymentEvent.getAmount();
		pointAmount = jobsPrePaymentEvent.getPoints();
	    }

	    // Calculate income
	    if (income != 0D) {
		income = boost.getFinalAmount(CurrencyType.MONEY, income);

		if (gConfigManager.useMinimumOveralPayment && income > 0) {
		    double maxLimit = income * gConfigManager.MinimumOveralPaymentLimit;

		    if (income < maxLimit)
			income = maxLimit;
		}
	    }

	    // Calculate points
	    if (pointAmount != 0D) {
		pointAmount = boost.getFinalAmount(CurrencyType.POINTS, pointAmount);

		if (gConfigManager.useMinimumOveralPoints && pointAmount > 0) {
		    double maxLimit = pointAmount * gConfigManager.MinimumOveralPointsLimit;

		    if (pointAmount < maxLimit)
			pointAmount = maxLimit;
		}
	    }

	    if (!jPlayer.isUnderLimit(CurrencyType.MONEY, income)) {
		if (gConfigManager.useMaxPaymentCurve) {
		    double percentOver = jPlayer.percentOverLimit(CurrencyType.MONEY);
		    float factor = gConfigManager.maxPaymentCurveFactor;
		    double percentLoss = 100 / ((1 / factor * percentOver * percentOver) + 1);
		    income = income - (income * percentLoss / 100);
		} else
		    income = 0D;
		if (gConfigManager.getLimit(CurrencyType.MONEY).getStopWith().contains(CurrencyType.POINTS))
		    pointAmount = 0D;
	    }

	    if (!jPlayer.isUnderLimit(CurrencyType.POINTS, pointAmount)) {
		pointAmount = 0D;
		if (gConfigManager.getLimit(CurrencyType.POINTS).getStopWith().contains(CurrencyType.MONEY))
		    income = 0D;
	    }

	    if (income == 0D && pointAmount == 0D)
		return;

	    if (info.getType() == ActionType.BREAK && block != null)
		getBpManager().remove(block);

	    if (pointAmount != 0D)
		jPlayer.setSaved(false);

	    Map<CurrencyType, Double> payments = new HashMap<>();
	    if (income != 0D)
		payments.put(CurrencyType.MONEY, income);
	    if (pointAmount != 0D)
		payments.put(CurrencyType.POINTS, pointAmount);

	    economy.pay(jPlayer, payments);

	    if (gConfigManager.LoggingUse) {
		Map<CurrencyType, Double> amounts = new HashMap<>();
		amounts.put(CurrencyType.MONEY, income);
		getLoging().recordToLog(jPlayer, info, amounts);
	    }

	} else {
	    FASTPAYMENT.clear();

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

		if (jobinfo == null || (gConfigManager.disablePaymentIfMaxLevelReached && prog.getLevel() >= prog.getJob().getMaxLevel())) {
		    continue;
		}

		double income = jobinfo.getIncome(level, numjobs, jPlayer.maxJobsEquation);
		double pointAmount = jobinfo.getPoints(level, numjobs, jPlayer.maxJobsEquation);
		double expAmount = jobinfo.getExperience(level, numjobs, jPlayer.maxJobsEquation);

		if (income == 0D && pointAmount == 0D && expAmount == 0D)
		    continue;

		if (gConfigManager.addXpPlayer()) {
		    Player player = jPlayer.getPlayer();
		    if (player != null) {
			/*
			 * Minecraft experience is calculated in whole numbers only.
			 * Calculate the fraction of an experience point and perform a dice roll.
			 * That way jobs that give fractions of experience points will slowly give
			 * experience in the aggregate
			 */
			int expInt = (int) expAmount;
			double remainder = expAmount - expInt;
			if (Math.abs(remainder) > Math.random()) {
			    if (expAmount < 0)
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

		JobsPrePaymentEvent jobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), prog.getJob(), income,
		    pointAmount, block, ent, victim, info);

		Bukkit.getServer().getPluginManager().callEvent(jobsPrePaymentEvent);
		// If event is canceled, don't do anything
		if (jobsPrePaymentEvent.isCancelled()) {
		    income = 0D;
		    pointAmount = 0D;
		} else {
		    income = jobsPrePaymentEvent.getAmount();
		    pointAmount = jobsPrePaymentEvent.getPoints();
		}

		// Calculate income
		if (income != 0D) {
		    income = boost.getFinalAmount(CurrencyType.MONEY, income);

		    if (gConfigManager.useMinimumOveralPayment && income > 0) {
			double maxLimit = income * gConfigManager.MinimumOveralPaymentLimit;

			if (income < maxLimit)
			    income = maxLimit;
		    }
		}

		// Calculate points
		if (pointAmount != 0D) {
		    pointAmount = boost.getFinalAmount(CurrencyType.POINTS, pointAmount);

		    if (gConfigManager.useMinimumOveralPoints && pointAmount > 0) {
			double maxLimit = pointAmount * gConfigManager.MinimumOveralPointsLimit;

			if (pointAmount < maxLimit)
			    pointAmount = maxLimit;
		    }
		}

		// Calculate exp
		if (expAmount != 0D) {
		    expAmount = boost.getFinalAmount(CurrencyType.EXP, expAmount);

		    if (gConfigManager.useMinimumOveralExp && expAmount > 0) {
			double maxLimit = expAmount * gConfigManager.minimumOveralExpLimit;

			if (expAmount < maxLimit)
			    expAmount = maxLimit;
		    }
		}

		if (!jPlayer.isUnderLimit(CurrencyType.MONEY, income)) {
		    income = 0D;

		    CurrencyLimit cLimit = gConfigManager.getLimit(CurrencyType.MONEY);

		    if (cLimit.getStopWith().contains(CurrencyType.EXP))
			expAmount = 0D;

		    if (cLimit.getStopWith().contains(CurrencyType.POINTS))
			pointAmount = 0D;
		}

		if (!jPlayer.isUnderLimit(CurrencyType.EXP, expAmount)) {
		    expAmount = 0D;

		    CurrencyLimit cLimit = gConfigManager.getLimit(CurrencyType.EXP);

		    if (cLimit.getStopWith().contains(CurrencyType.MONEY))
			income = 0D;

		    if (cLimit.getStopWith().contains(CurrencyType.POINTS))
			pointAmount = 0D;
		}

		if (!jPlayer.isUnderLimit(CurrencyType.POINTS, pointAmount)) {
		    pointAmount = 0D;

		    CurrencyLimit cLimit = gConfigManager.getLimit(CurrencyType.POINTS);

		    if (cLimit.getStopWith().contains(CurrencyType.MONEY))
			income = 0D;

		    if (cLimit.getStopWith().contains(CurrencyType.EXP))
			expAmount = 0D;
		}

		if (income == 0D && pointAmount == 0D && expAmount == 0D)
		    continue;

		// JobsPayment event
		JobsExpGainEvent jobsExpGainEvent = new JobsExpGainEvent(jPlayer.getPlayer(), prog.getJob(), expAmount,
		    block, ent, victim, info);
		Bukkit.getServer().getPluginManager().callEvent(jobsExpGainEvent);
		// If event is canceled, don't do anything
		expAmount = jobsExpGainEvent.isCancelled() ? 0D : jobsExpGainEvent.getExp();

		try {
		    if (expAmount != 0D && gConfigManager.BossBarEnabled)
			if (gConfigManager.BossBarShowOnEachAction)
			    bbManager.ShowJobProgression(jPlayer, prog, expAmount);
			else
			    jPlayer.getUpdateBossBarFor().add(prog.getJob().getName());
		} catch (Throwable e) {
		    e.printStackTrace();
		    consoleMsg("&c[Jobs] Some issues with boss bar feature accured, try disabling it to avoid it.");
		}

		Map<CurrencyType, Double> payments = new HashMap<>();
		if (income != 0D)
		    payments.put(CurrencyType.MONEY, income);
		if (pointAmount != 0D)
		    payments.put(CurrencyType.POINTS, pointAmount);
		if (expAmount != 0D)
		    payments.put(CurrencyType.EXP, expAmount);

		FASTPAYMENT.put(jPlayer.getUniqueId(), new FastPayment(jPlayer, info, new BufferedPayment(jPlayer.getPlayer(), payments), prog
		    .getJob()));

		economy.pay(jPlayer, payments);
		int oldLevel = prog.getLevel();

		if (gConfigManager.LoggingUse) {
		    Map<CurrencyType, Double> amounts = new HashMap<>();
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
	}
    }

    private static boolean isBpOk(JobsPlayer player, ActionInfo info, Block block, boolean inform) {
	if (block == null || !gConfigManager.useBlockProtection)
	    return true;

	if (info.getType() == ActionType.BREAK) {
	    if (block.hasMetadata("JobsExploit")) {
		//player.sendMessage("This block is protected using Rukes' system!");
		return false;
	    }

	    BlockProtection bp = getBpManager().getBp(block.getLocation());
	    if (bp != null) {
		long time = bp.getTime();
		Integer cd = getBpManager().getBlockDelayTime(block);

		if (time == -1L) {
		    getBpManager().remove(block);
		    return false;
		}

		if (time < System.currentTimeMillis() && bp.getAction() != DBAction.DELETE) {
		    getBpManager().remove(block);
		    return true;
		}

		if ((time > System.currentTimeMillis() || bp.isPaid()) && bp.getAction() != DBAction.DELETE) {
		    int sec = Math.round((time - System.currentTimeMillis()) / 1000L);
		    if (inform && player.canGetPaid(info)) {
			ActionBarManager.send(player.getPlayer(), lManager.getMessage("message.blocktimer", "[time]", sec));
		    }

		    return false;
		}

		getBpManager().add(block, cd);

		if ((cd == null || cd == 0) && gConfigManager.useGlobalTimer) {
		    getBpManager().add(block, gConfigManager.globalblocktimer);
		}

	    } else if (gConfigManager.useGlobalTimer) {
		getBpManager().add(block, gConfigManager.globalblocktimer);
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

		    if ((time > System.currentTimeMillis() || bp.isPaid()) && bp.getAction() != DBAction.DELETE) {
			int sec = Math.round((time - System.currentTimeMillis()) / 1000L);
			if (inform && player.canGetPaid(info)) {
			    ActionBarManager.send(player.getPlayer(), lManager.getMessage("message.blocktimer", "[time]", sec));
			}

			getBpManager().add(block, cd);
			return false;
		    }

		    // Lets add protection in any case
		    getBpManager().add(block, cd);
		} else if (bp.isPaid() && bp.getTime() == -1L && cd != null && cd == -1) {
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
	return (expToLevel(player.getLevel()) + Math.round(deltaLevelToExp(player.getLevel()) * player.getExp()));
    }

    // total xp calculation based by lvl
    private static int expToLevel(int level) {
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
	JobsExpGainEvent jobsExpGainEvent = new JobsExpGainEvent(payment.getOfflinePlayer(), job, payment.get(CurrencyType.EXP));
	Bukkit.getServer().getPluginManager().callEvent(jobsExpGainEvent);
	// If event is canceled, don't do anything
	if (jobsExpGainEvent.isCancelled())
	    return;

	boolean limited = true;
	for (CurrencyType one : CurrencyType.values()) {
	    if (jPlayer.isUnderLimit(one, payment.get(one))) {
		limited = false;
		break;
	    }
	}

	if (limited)
	    return;

	economy.pay(jPlayer, payment.getPayment());

	JobProgression prog = jPlayer.getJobProgression(job);
	int oldLevel = prog.getLevel();

	if (gConfigManager.LoggingUse) {
	    getLoging().recordToLog(jPlayer, info, payment.getPayment());
	}

	if (prog.addExperience(payment.get(CurrencyType.EXP)))
	    getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
    }

    public static void consoleMsg(String msg) {
	if (msg != null) {
	    Bukkit.getServer().getConsoleSender().sendMessage(CMIChatColor.translate(msg));
	}
    }

    public static SelectionManager getSelectionManager() {
	return smanager;
    }

    public static boolean hasPermission(Object sender, String perm, boolean rawEnable) {
	if (!(sender instanceof Player) || ((Player) sender).hasPermission(perm))
	    return true;

	if (!rawEnable) {
	    ((Player) sender).sendMessage(lManager.getMessage("general.error.permission"));
	    return false;
	}
	new RawMessage().addText(lManager.getMessage("general.error.permission")).addHover("&2" + perm).show((Player) sender);
	return false;

    }

    public void showPagination(CommandSender sender, PageInfo pi, String cmd) {
	showPagination(sender, pi.getTotalPages(), pi.getCurrentPage(), pi.getTotalEntries(), cmd, null);
    }

    public void showPagination(CommandSender sender, PageInfo pi, String cmd, String pagePref) {
	showPagination(sender, pi.getTotalPages(), pi.getCurrentPage(), pi.getTotalEntries(), cmd, pagePref);
    }

    public void showPagination(CommandSender sender, int pageCount, int currentPage, int totalEntries, String cmd, String pagePref) {
	if (!(sender instanceof Player))
	    return;

	if (!cmd.startsWith("/"))
	    cmd = "/" + cmd;

	if (pageCount == 1)
	    return;

	String pagePrefix = pagePref == null ? "" : pagePref;

	int nextPage = currentPage + 1;
	nextPage = currentPage < pageCount ? nextPage : currentPage;

	int prevpage = currentPage - 1;
	prevpage = currentPage > 1 ? prevpage : currentPage;

	RawMessage rm = new RawMessage()
	    .addText((currentPage > 1 ? lManager.getMessage("command.help.output.prevPage") : lManager.getMessage("command.help.output.prevPageOff")))
	    .addHover(currentPage > 1 ? "<<<" : ">|")
	    .addCommand(currentPage > 1 ? cmd + " " + pagePrefix + prevpage : cmd + " " + pagePrefix + pageCount);

	rm.addText(lManager.getMessage("command.help.output.pageCount", "[current]", currentPage, "[total]", pageCount))
	    .addHover(lManager.getMessage("command.help.output.pageCountHover", "[totalEntries]", totalEntries));

	rm.addText(pageCount > currentPage ? lManager.getMessage("command.help.output.nextPage") : lManager.getMessage("command.help.output.nextPageOff"))
	    .addHover(pageCount > currentPage ? ">>>" : "|<")
	    .addCommand(pageCount > currentPage ? cmd + " " + pagePrefix + nextPage : cmd + " " + pagePrefix + 1);

	if (pageCount != 0)
	    rm.show(sender);
    }
}
