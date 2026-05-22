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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Gui.GuiManager;
import com.gamingmesh.jobs.Placeholders.Placeholder;
import com.gamingmesh.jobs.Placeholders.PlaceholderAPIHook;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsInstancePaymentEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.config.BlockProtectionManager;
import com.gamingmesh.jobs.config.BossBarManager;
import com.gamingmesh.jobs.config.ChunkExplorationManager;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.config.ExploitProtectionManager;
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
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.container.CurrencyLimit;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.FastPayment;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.JobsWorld;
import com.gamingmesh.jobs.container.LoadStatus;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestProgression;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
import com.gamingmesh.jobs.dao.JobsClassLoader;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.dao.JobsDAOData;
import com.gamingmesh.jobs.dao.JobsManager;
import com.gamingmesh.jobs.economy.BufferedEconomy;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.gamingmesh.jobs.economy.Economy;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.hooks.JobsHook;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.listeners.JobsDefaultFishPaymentListener;
import com.gamingmesh.jobs.listeners.JobsListener;
import com.gamingmesh.jobs.listeners.JobsPayment1_14Listener;
import com.gamingmesh.jobs.listeners.JobsPayment1_16Listener;
import com.gamingmesh.jobs.listeners.JobsPayment1_17Listener;
import com.gamingmesh.jobs.listeners.JobsPayment1_20Listener;
import com.gamingmesh.jobs.listeners.JobsPayment1_9Listener;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.gamingmesh.jobs.listeners.JobsPaymentVisualizationListener;
import com.gamingmesh.jobs.listeners.PistonProtectionListener;
import com.gamingmesh.jobs.listeners.PlayerSignEdit1_20Listeners;
import com.gamingmesh.jobs.selection.SelectionManager;
import com.gamingmesh.jobs.stuff.Loging;
import com.gamingmesh.jobs.stuff.TabComplete;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;
import com.gamingmesh.jobs.stuff.Util;
import com.gamingmesh.jobs.stuff.VersionChecker;
import com.gamingmesh.jobs.stuff.complement.Complement;
import com.gamingmesh.jobs.stuff.complement.Complement1;
import com.gamingmesh.jobs.stuff.complement.JobsChatEvent;
import com.gamingmesh.jobs.tasks.BufferedPaymentThread;
import com.gamingmesh.jobs.tasks.DatabaseSaveThread;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.milkbowl.vault.permission.Permission;

public final class Jobs extends JavaPlugin {

	private static PlayerManager pManager;
	private static JobsCommands cManager;
	private static Language lManager;
	private static LanguageManager lmManager;
	private static SignUtil signManager;
	private static ScheduleManager scheduleManager;
	private static NameTranslatorManager nameTranslatorManager;
	@Deprecated
	private static ExploreManager exploreManager;
	private static ChunkExplorationManager chunkExplorationManager;
	private static TitleManager titleManager;
	private static RestrictedBlockManager rbManager;
	private static RestrictedAreaManager raManager;
	private static BossBarManager bbManager;
	private static ShopManager shopManager;
	private static Loging loging;
	@Deprecated
	private static BlockProtectionManager bpManager;
	private static ExploitProtectionManager exploitManager;
	private static JobsManager dbManager;
	private static ConfigManager configManager;
	private static GeneralConfigManager gConfigManager;

	private static BufferedEconomy economy;

	private static Permission vaultPermission;
	private static PermissionHandler permissionHandler;
	private static PermissionManager permissionManager;

	private static JobsClassLoader classLoader;

	private final HashMap<CMIMaterial, BlockOwnerShip> blockOwnerShipsMaterial = new HashMap<>();
	private final HashMap<BlockTypes, BlockOwnerShip> blockOwnerShipsBlockType = new HashMap<>();

	private boolean kyoriSupported = false;

	private Complement complement;
	private GuiManager guiManager;

	private static JobsDAO dao;

	private static List<Job> jobs = new ArrayList<Job>();
	private static HashMap<String, Job> jobsByName = new HashMap<>();
	private static HashMap<Integer, Job> jobsByID = new HashMap<>();

	private static Job noneJob;
	private static Map<Job, Integer> usedSlots = new WeakHashMap<>();

	public static BufferedPaymentThread paymentThread;
	private static DatabaseSaveThread saveTask;

	public static LoadStatus status = LoadStatus.Good;

	private static boolean hasLimitedItems = false;

	public static boolean fullyLoaded = false;

	private static final int MAX_ENTRIES = 20;
	public static final LinkedHashMap<UUID, FastPayment> FASTPAYMENT = new LinkedHashMap<UUID, FastPayment>(MAX_ENTRIES + 1, .75F, false) {
		protected boolean removeEldestEntry(Map.Entry<UUID, FastPayment> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	protected static VersionChecker versionCheckManager;
	protected static SelectionManager smanager;

	public Complement getComplement() {
		return complement;
	}

	public boolean isKyoriSupported() {
		return kyoriSupported;
	}

	public static JobsClassLoader getJobsClassloader() {
		if (classLoader == null)
			classLoader = new JobsClassLoader(Jobs.getInstance());
		return classLoader;
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
	 * Returns the block owner ship for specific {@link CMIMaterial} type. If the
	 * addNew parameter is enabled, it will cache a new owner ship for specific
	 * {@link CMIMaterial} type.
	 * 
	 * @param type   {@link CMIMaterial}
	 * @param addNew whenever to add a new owner ship
	 * @return {@link BlockOwnerShip}, otherwise {@link Optional#empty()}
	 */
	public Optional<BlockOwnerShip> getBlockOwnerShip(CMIMaterial type, boolean addNew) {
		BlockOwnerShip b = blockOwnerShipsMaterial.get(type);

		if (addNew && b == null) {
			b = new BlockOwnerShip(type);
			blockOwnerShipsMaterial.put(type, b);
			blockOwnerShipsBlockType.put(b.getType(), b);
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
		BlockOwnerShip b = blockOwnerShipsBlockType.get(type);
		if (b != null)
			return Optional.ofNullable(b);

		return Optional.empty();
	}

	public void removeBlockOwnerShip(Block block) {
		BlockOwnerShip ship = blockOwnerShipsMaterial.get(CMIMaterial.get(block));
		if (ship != null)
			ship.remove(block);
	}

	/**
	 * @return a set of block owner ships.
	 */
	public HashMap<CMIMaterial, BlockOwnerShip> getBlockOwnerShips() {
		return blockOwnerShipsMaterial;
	}

	private Placeholder placeholder;
	private boolean placeholderAPIEnabled = false;

	public Placeholder getPlaceholderAPIManager() {
		if (placeholder == null)
			placeholder = new Placeholder(this);
		return placeholder;
	}

	private boolean setupPlaceHolderAPI() {
		Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
		if (papi == null || !papi.isEnabled())
			return false;

		try {
			if (Integer.parseInt(papi.getDescription().getVersion().replaceAll("[^\\d]", "")) >= 2100 && new PlaceholderAPIHook(this).register()) {
				CMIMessages.consoleMessage("&6PlaceholderAPI &ehooked.");
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

	@Deprecated
	public static BlockProtectionManager getBpManager() {
		if (bpManager == null)
			bpManager = new BlockProtectionManager();
		return bpManager;
	}

	public static ExploitProtectionManager getExploitManager() {
		if (exploitManager == null)
			exploitManager = new ExploitProtectionManager();
		return exploitManager;
	}

	public static JobsManager getDBManager() {
		if (dbManager == null)
			dbManager = new JobsManager(getInstance());
		return dbManager;
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
		return getGeneralConfigManager();
	}

	public static GeneralConfigManager getGeneralConfigManager() {
		if (gConfigManager == null)
			gConfigManager = new GeneralConfigManager();
		return gConfigManager;
	}

	/**
	 * @return {@link PlayerManager}
	 */
	public static PlayerManager getPlayerManager() {
		if (pManager == null)
			pManager = new PlayerManager(getInstance());
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
	 * 
	 * @return the schedule manager
	 */
	public static ScheduleManager getScheduleManager() {
		if (scheduleManager == null)
			scheduleManager = new ScheduleManager(getInstance());
		return scheduleManager;
	}

	public static NameTranslatorManager getNameTranslatorManager() {
		if (nameTranslatorManager == null)
			nameTranslatorManager = new NameTranslatorManager();
		return nameTranslatorManager;
	}

	public GuiManager getGUIManager() {
		if (guiManager == null)
			guiManager = new GuiManager(this);
		return guiManager;
	}

	public static JobsCommands getCommandManager() {
		if (cManager == null)
			cManager = new JobsCommands(getInstance());
		return cManager;
	}

	@Deprecated
	public static ExploreManager getExplore() {
		return getExploreManager();
	}

	@Deprecated
	public static ExploreManager getExploreManager() {
		if (exploreManager == null)
			exploreManager = new ExploreManager();
		return exploreManager;
	}

	public static ChunkExplorationManager getChunkExplorationManager() {
		if (chunkExplorationManager == null)
			chunkExplorationManager = new ChunkExplorationManager();
		return chunkExplorationManager;
	}

	/**
	 * @return returns this class object instance
	 */
	public static Jobs getInstance() {
		return JavaPlugin.getPlugin(Jobs.class);
	}

	/**
	 * Returns sign manager
	 * 
	 * @return the sign manager
	 */
	public static SignUtil getSignUtil() {
		if (signManager == null)
			signManager = new SignUtil(getInstance());
		return signManager;
	}

	/**
	 * Returns language manager
	 * 
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
	 * 
	 * @return the plugin logger
	 */
	public static Logger getPluginLogger() {
		return getInstance().getLogger();
	}

	public static File getFolder() {
		File folder = getInstance().getDataFolder();
		folder.mkdirs();
		return folder;
	}

	/**
	 * Sets the Data Access Object
	 * 
	 * @param dao - the DAO
	 */
	public static void setDAO(JobsDAO dao) {
		Jobs.dao = dao;
	}

	/**
	 * Get the Data Access Object
	 * 
	 * @return the DAO
	 */
	public static JobsDAO getJobsDAO() {
		return dao;
	}

	/**
	 * Sets the list of jobs
	 * 
	 * @param jobs - list of jobs
	 */
	public static void setJobs(List<Job> jobs) {
		Jobs.jobs = jobs;

		jobsByName.clear();

		for (Job job : jobs) {
			jobsByName.put(job.getName().toLowerCase(), job);
			jobsByName.put(job.getJobFullName().toLowerCase(), job);
		}

		fillJobsByID();
	}

	private static void fillJobsByID() {
		jobsByID.clear();
		for (Job job : jobs) {
			if (job.getId() != 0)
				jobsByID.put(job.getId(), job);
			if (job.getLegacyId() != 0)
				jobsByID.put(job.getLegacyId(), job);
		}
	}

	/**
	 * Returns the list of available jobs.
	 * 
	 * @return an unmodifiable list of jobs
	 */
	public static List<Job> getJobs() {
		return Collections.unmodifiableList(jobs);
	}

	/**
	 * Sets the none job
	 * 
	 * @param noneJob - the none job
	 */
	public static void setNoneJob(Job noneJob) {
		Jobs.noneJob = noneJob;
	}

	/**
	 * Retrieves the "none" job
	 * 
	 * @return the none job
	 */
	public static Job getNoneJob() {
		return noneJob;
	}

	/**
	 * Function to return the job information that matches the jobName given
	 * 
	 * @param jobName - the ame of the job given
	 * @return the job that matches the name
	 */
	public static Job getJob(String jobName) {
		if (jobName == null)
			return null;
		return jobsByName.get(jobName.toLowerCase());
	}

	/**
	 * Returns a job by identifier.
	 * 
	 * @param id the id of job
	 * @return {@link Job}
	 */
	public static Job getJob(int id) {
		if (jobsByID.isEmpty())
			fillJobsByID();
		return jobsByID.get(id);
	}

	public boolean isPlaceholderAPIEnabled() {
		return placeholderAPIEnabled;
	}

	private static void startup() {
		reload(true);

		// This goes in sync to avoid issues while loading data
		loadAllPlayersData();
		for (Player online : Bukkit.getServer().getOnlinePlayers())
			getPlayerManager().playerJoin(online);
	}

	public static void loadAllPlayersData() {
		long time = System.currentTimeMillis();
		HashMap<UUID, PlayerInfo> temp = new HashMap<>(getPlayerManager().getPlayersInfoUUIDMap());
		Map<Integer, List<JobsDAOData>> playersJobs = dao.getAllJobs();
		Map<Integer, PlayerPoints> playersPoints = dao.getAllPoints();
		Map<Integer, Map<String, Log>> playersLogs = dao.getAllLogs();
		Map<Integer, ArchivedJobs> playersArchives = dao.getAllArchivedJobs();
		Map<Integer, PaymentData> playersLimits = dao.loadPlayerLimits();
		for (Iterator<PlayerInfo> it = temp.values().iterator(); it.hasNext();) {
			PlayerInfo one = it.next();
			int id = one.getID();
			JobsPlayer jPlayer = getPlayerManager().getJobsPlayerOffline(one, playersJobs.get(id), playersPoints.get(id), playersLogs.get(id), playersArchives.get(id), playersLimits.get(id));
			if (jPlayer != null)
				getPlayerManager().addPlayerToCache(jPlayer);
		}
		if (!getPlayerManager().getPlayersCache().isEmpty())
			CMIMessages.consoleMessage("&ePreloaded &6" + getPlayerManager().getPlayersCache().size() + " &eplayers data in &6" + ((int) ((System.currentTimeMillis() - time) / 1000.0D * 100.0D) / 100.0D));
	}

	public static void convertDatabase() {
		try {
			List<Convert> archivelist = dao.convertDatabase();

			getDBManager().switchDataBase();
			getPlayerManager().reload();

			dao.truncateAllTables();

			for (Job one : Jobs.getJobs()) {
				dao.recordNewJobName(one, one.getId());
			}
			for (JobsWorld one : Util.getJobsWorlds().values()) {
				dao.recordNewWorld(one.getName(), one.getId());
			}

			getPlayerManager().convertChacheOfPlayers(true);

			dao.continueConvertions(archivelist);
			getPlayerManager().clearMaps();
			getPlayerManager().clearCache();

			if (Jobs.getGeneralConfigManager().ExploreSaveIntoDatabase)
				dao.saveExplore();
//    Do we really need to convert Block protection?
//    Jobs.getJobsDAO().saveBlockProtection();
		} catch (SQLException e) {
			e.printStackTrace();
			CMIMessages.consoleMessage("&cCan't write data to data base, please send error log to dev's.");
			return;
		}

		reload();
		loadAllPlayersData();
	}

	/**
	 * Checks if the given {@link JobsPlayer} have the given {@link ActionType} in
	 * one of jobs.
	 * 
	 * @param jPlayer {@link JobsPlayer}
	 * @param type    {@link ActionType}
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
	 * 
	 * @param job - the job
	 * @return the number of slots
	 */
	public static int getUsedSlots(Job job) {
		return usedSlots.getOrDefault(job, 0);
	}

	/**
	 * Function to increase the number of used slots for a job
	 * 
	 * @param job - the job someone is taking
	 */
	public static void takeSlot(Job job) {
		Integer used = usedSlots.get(job);
		if (used != null)
			usedSlots.put(job, used + 1);
	}

	/**
	 * Function to decrease the number of used slots for a job
	 * 
	 * @param job - the job someone is leaving
	 */
	public static void leaveSlot(Job job) {
		Integer used = usedSlots.get(job);
		if (used != null)
			usedSlots.put(job, used - 1);
	}

	/**
	 * Gets the permission handler
	 * 
	 * @return the permission handler
	 */
	public static PermissionHandler getPermissionHandler() {
		if (permissionHandler == null)
			permissionHandler = new PermissionHandler(getInstance());
		return permissionHandler;
	}

	public static PermissionManager getPermissionManager() {
		if (permissionManager == null)
			permissionManager = new PermissionManager();
		return permissionManager;
	}

	/**
	 * Sets the economy handler
	 * 
	 * @param eco - the economy handler
	 */
	public static void setEconomy(Economy eco) {
		economy = new BufferedEconomy(getInstance(), eco);
	}

	public static void setVaultPermission(Permission permission) {
		vaultPermission = permission;
	}

	/**
	 * Gets the economy handler
	 * 
	 * @return the economy handler
	 */
	public static BufferedEconomy getEconomy() {
		return economy;
	}

	public static Permission getVaultPermission() {
		return vaultPermission;
	}

	/**
	 * Gets the version check manager
	 * 
	 * @return the version check manager
	 */
	public static VersionChecker getVersionCheckManager() {
		if (versionCheckManager == null)
			versionCheckManager = new VersionChecker(getInstance());

		return versionCheckManager;
	}

	private final static String prefix = "&6------------- &2Jobs &6-------------";
	private final static String suffix = "&6------------------------------------";

	@Override
	public void onEnable() {
		CMIMessages.consoleMessage(prefix);

		try {
			Class.forName("net.kyori.adventure.text.Component");
			ItemMeta.class.getDeclaredMethod("displayName");
			kyoriSupported = true;
		} catch (NoSuchMethodException | ClassNotFoundException e) {
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

			if (status.equals(LoadStatus.MYSQLFailure) || status.equals(LoadStatus.SQLITEFailure)) {
				CMIMessages.consoleMessage("&cCould not connect to " + (status.equals(LoadStatus.MYSQLFailure) ? "MySQL" : "SqLite") + "!");
				CMIMessages.consoleMessage("&cPlugin will be disabled");
				this.onDisable();
				this.setEnabled(false);
				return;
			}

			if (getGCManager().SignsEnabled) {
				new YmlMaker(getFolder(), "Signs.yml").saveDefaultConfig();
			}

			JobsHook.loadHooks();
			registerListeners();

			complement = new Complement1();

			if (HookVault.isVaultEnable()) {
				// register economy
				CMIScheduler.runTask(Jobs.getInstance(), () -> new HookEconomyTask(net.milkbowl.vault.economy.Economy.class));

				// register permission from vault
				CMIScheduler.runTask(Jobs.getInstance(), () -> new HookPermissionTask(Permission.class));
			}

			dao.loadBlockProtection();
			if (!getGCManager().useNewExploration)
				getExploreManager().load();
			getCommandManager().fillCommands();
			getDBManager().getDB().triggerTableIdUpdate();

			CMIMessages.consoleMessage("&ePlugin has been enabled successfully.");
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("There was some issues when starting plugin. Please contact dev about this. Plugin will be disabled.");
			setEnabled(false);
		}
		fullyLoaded = true;
		CMIMessages.consoleMessage(suffix);
	}

	private static void registerListeners() {

		CMIMessages.consoleMessage("&eRegistering listeners...");

		PluginManager pm = getInstance().getServer().getPluginManager();

		if (getGCManager().useCustomFishingOnly) {
			JobsHook.CustomFishing.registerListener();
		} else {
			pm.registerEvents(new JobsDefaultFishPaymentListener(), getInstance());
		}

		pm.registerEvents(new JobsListener(getInstance()), getInstance());
		pm.registerEvents(new JobsPaymentListener(getInstance()), getInstance());

		pm.registerEvents(new JobsPaymentVisualizationListener(getInstance()), getInstance());

		if (Version.isCurrentEqualOrHigher(Version.v1_9_R1))
			pm.registerEvents(new JobsPayment1_9Listener(), getInstance());

		if (Version.isCurrentEqualOrHigher(Version.v1_14_R1))
			pm.registerEvents(new JobsPayment1_14Listener(), getInstance());

		if (Version.isCurrentEqualOrHigher(Version.v1_16_R3))
			pm.registerEvents(new JobsPayment1_16Listener(), getInstance());

		if (Version.isCurrentEqualOrHigher(Version.v1_17_R1))
			pm.registerEvents(new JobsPayment1_17Listener(), getInstance());

		if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
			pm.registerEvents(new PlayerSignEdit1_20Listeners(), getInstance());
			pm.registerEvents(new JobsPayment1_20Listener(), getInstance());
		}

		if (getGCManager().useBlockProtection) {
			pm.registerEvents(new PistonProtectionListener(), getInstance());
		}

		pm.registerEvents(new JobsChatEvent(getInstance()), getInstance());

		JobsHook.PyroFishingPro.registerListener();
		JobsHook.mcMMO.registerListener();
		JobsHook.MythicMobs.registerListener();

		CMIMessages.consoleMessage("&eListeners registered successfully");
	}

	public static void reload() {
		reload(false);
	}

	public static void reload(boolean startup) {
		// unregister all registered listeners by this plugin and register again
		if (!startup) {
			HandlerList.unregisterAll(getInstance());
			registerListeners();
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

		hasLimitedItems = Jobs.getJobs().stream().anyMatch(job -> !job.getLimitedItems().isEmpty());

		getDBManager().getDB().loadAllJobsWorlds();
		getDBManager().getDB().loadAllJobsNames();

		if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
			getInstance().getBlockOwnerShip(CMIMaterial.LEGACY_BREWING_STAND).ifPresent(BlockOwnerShip::load);
			getInstance().getBlockOwnerShip(CMIMaterial.LEGACY_BURNING_FURNACE).ifPresent(BlockOwnerShip::load);
		} else {
			getInstance().getBlockOwnerShip(CMIMaterial.FURNACE).ifPresent(BlockOwnerShip::load);
			getInstance().getBlockOwnerShip(CMIMaterial.BREWING_STAND).ifPresent(BlockOwnerShip::load);
		}
		if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
			getInstance().getBlockOwnerShip(CMIMaterial.BLAST_FURNACE).ifPresent(BlockOwnerShip::load);
			getInstance().getBlockOwnerShip(CMIMaterial.SMOKER).ifPresent(BlockOwnerShip::load);
		}

		ToggleBarHandling.init();
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

		// Load active boosts from file
		try {
			BoostManager.loadBoosts();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// Schedule
		if (getGCManager().enableSchedule) {
			try {
				getScheduleManager().load();
				getScheduleManager().start();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else
			getScheduleManager().cancel();
	}

	@Override
	public void onDisable() {

		CMIMessages.consoleMessage(prefix);
		HandlerList.unregisterAll(this);

		if (dao != null && Jobs.getGeneralConfigManager().ExploreSaveIntoDatabase)
			dao.saveExplore();

		// Save active boosts before shutdown
		try {
			BoostManager.saveBoosts();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		BlockOwnerShip.onDisable();

		if (saveTask != null)
			saveTask.shutdown();

		if (paymentThread != null)
			paymentThread.shutdown();

		if (pManager != null) {
			pManager.removePlayerAdditions();
			CMIMessages.consoleMessage("&eCleared boss bar cache");
			pManager.saveAll();
			CMIMessages.consoleMessage("&eSaved player data");
		}

		if (dao != null) {
			dao.closeConnections();
			CMIMessages.consoleMessage("&eClosed database connection");
		}

		CMIMessages.consoleMessage(suffix);
	}

	private static void checkDailyQuests(JobsPlayer jPlayer, Job job, ActionInfo info) {
		if (!Jobs.getGCManager().DailyQuestsEnabled) {
			return;
		}
		if (!job.getQuests().isEmpty()) {
			for (QuestProgression one : jPlayer.getQuestProgressions(job, info.getType())) {
				one.processQuest(jPlayer, info);
			}
		}
	}

	/**
	 * Perform an action for the given {@link JobsPlayer} with the given action
	 * info.
	 * 
	 * @param jPlayer {@link JobsPlayer}
	 * @param info    {@link ActionInfo}
	 * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
	 */
	public static void action(JobsPlayer jPlayer, ActionInfo info) {
		action(jPlayer, info, null, null, null);
	}

	/**
	 * Perform an action for the given {@link JobsPlayer} with the given action info
	 * and block.
	 * 
	 * @param jPlayer {@link JobsPlayer}
	 * @param info    {@link ActionInfo}
	 * @param block   {@link Block}
	 * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
	 */
	public static void action(JobsPlayer jPlayer, ActionInfo info, Block block) {
		action(jPlayer, info, block, null, null);
	}

	/**
	 * Perform an action for the given {@link JobsPlayer} with the given action info
	 * and entity.
	 * 
	 * @param jPlayer {@link JobsPlayer}
	 * @param info    {@link ActionInfo}
	 * @param ent     {@link Entity}
	 * @see #action(JobsPlayer, ActionInfo, Block, Entity, LivingEntity)
	 */
	public static void action(JobsPlayer jPlayer, ActionInfo info, Entity ent) {
		action(jPlayer, info, null, ent, null);
	}

	/**
	 * Perform an action for the given {@link JobsPlayer} with the given action
	 * info, entity and living entity.
	 * 
	 * @param jPlayer {@link JobsPlayer}
	 * @param info    {@link ActionInfo}
	 * @param ent     {@link Entity}
	 * @param victim  {@link LivingEntity}
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
	 * If the player does not have any job progression cached into memory, the
	 * player only retrieve the "noneJob" by default. This means that there will be
	 * no any extra income calculations and the player does no get the full income
	 * from jobs, but the half of it.<br>
	 * In other cases if player have at least 1 job cached, they will get the full
	 * income with the extra calculated multiplications including bonuses and
	 * limits.
	 * <p>
	 * 
	 * <b>This usually not be called in your code, to avoid misbehaviour working
	 * ability.</b>
	 * 
	 * @param jPlayer {@link JobsPlayer}
	 * @param info    {@link ActionInfo}
	 * @param ent     {@link Entity}
	 * @param victim  {@link LivingEntity}
	 * @param block   {@link Block}
	 */
	public static void action(JobsPlayer jPlayer, ActionInfo info, Block block, Entity ent, LivingEntity victim) {
		if (jPlayer == null)
			return;

		List<JobProgression> progression = jPlayer.getJobProgression();
		int numjobs = progression.size();

		if (!Jobs.getGCManager().useBlockProtectionBlockTracker && !Jobs.getExploitManager().isProtectionValidAddIfNotExists(jPlayer, info, block, true))
			return;

		// no job
		if (numjobs == 0) {
			if (noneJob == null || noneJob.isWorldBlackListed(block, ent, victim))
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

			JobsPrePaymentEvent jobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), noneJob, income, 0, pointAmount, block, ent, victim, info);
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
					double percentLoss = 100 / ((1 / gConfigManager.maxPaymentCurveFactor * percentOver * percentOver) + 1);

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

			if (info.getType() == ActionType.BREAK && block != null) {
				if (getGCManager().useNewBlockProtection)
					getExploitManager().remove(block);
				else
					getBpManager().remove(block);
			}

			if (pointAmount != 0D) {
				jPlayer.setSaved(false);
			}

			Map<CurrencyType, Double> payments = new HashMap<>();
			if (income != 0D)
				payments.put(CurrencyType.MONEY, income);
			if (pointAmount != 0D)
				payments.put(CurrencyType.POINTS, pointAmount);

			// FinalPayment event
			CMIScheduler.runTaskAsynchronously(getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new JobsInstancePaymentEvent(jPlayer.getPlayer(), payments)));
			payOut(jPlayer, payments);

			if (gConfigManager.LoggingUse) {
				Map<CurrencyType, Double> amounts = new HashMap<>();
				amounts.put(CurrencyType.MONEY, income);
				getLoging().recordToLog(jPlayer, info, amounts);
			}

		} else {
			List<Job> expiredJobs = new ArrayList<>();
			for (JobProgression prog : progression) {
				if (prog.getJob().isWorldBlackListed(block, ent, victim))
					continue;

				if (jPlayer.isLeftTimeEnded(prog.getJob())) {
					expiredJobs.add(prog.getJob());
				}

				JobInfo jobinfo = prog.getJob().getJobInfo(info, prog.getLevel());

				checkDailyQuests(jPlayer, prog.getJob(), info);

				if (jobinfo == null || (gConfigManager.disablePaymentIfMaxLevelReached && prog.getLevel() >= prog.getJob().getMaxLevel())) {
					continue;
				}

				double income = jobinfo.getIncome(prog.getLevel(), numjobs, jPlayer.maxJobsEquation);
				double pointAmount = jobinfo.getPoints(prog.getLevel(), numjobs, jPlayer.maxJobsEquation);
				double expAmount = jobinfo.getExperience(prog.getLevel(), numjobs, jPlayer.maxJobsEquation);

				if (income == 0D && pointAmount == 0D && expAmount == 0D)
					continue;

				if (gConfigManager.addXpPlayer()) {
					Player player = jPlayer.getPlayer();
					if (player != null) {
						/*
						 * Minecraft experience is calculated in whole numbers only. Calculate the
						 * fraction of an experience point and perform a dice roll. That way jobs that
						 * give fractions of experience points will slowly give experience in the
						 * aggregate
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

				JobsPrePaymentEvent jobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), prog.getJob(), CurrencyType.generate(income, expAmount, pointAmount), block, ent, victim, info);

				Bukkit.getServer().getPluginManager().callEvent(jobsPrePaymentEvent);
				// If event is canceled, don't do anything
				if (jobsPrePaymentEvent.isCancelled()) {
					income = 0D;
					pointAmount = 0D;
					expAmount = 0D;
				} else {
					income = jobsPrePaymentEvent.getAmount();
					pointAmount = jobsPrePaymentEvent.getPoints();
					expAmount = jobsPrePaymentEvent.getExp();
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
				JobsExpGainEvent jobsExpGainEvent = new JobsExpGainEvent(jPlayer.getPlayer(), prog.getJob(), expAmount, block, ent, victim, info);
				Bukkit.getServer().getPluginManager().callEvent(jobsExpGainEvent);
				// If event is canceled, don't do anything
				expAmount = jobsExpGainEvent.isCancelled() ? 0D : jobsExpGainEvent.getExp();

				Map<CurrencyType, Double> payments = new HashMap<>();
				if (income != 0D)
					payments.put(CurrencyType.MONEY, income);
				if (pointAmount != 0D)
					payments.put(CurrencyType.POINTS, pointAmount);
				if (expAmount != 0D)
					payments.put(CurrencyType.EXP, expAmount);

				FASTPAYMENT.put(jPlayer.getUniqueId(), new FastPayment(jPlayer, info, new BufferedPayment(jPlayer.getPlayer(), payments), prog.getJob()));

				// FinalPayment event
				CMIScheduler.runTaskAsynchronously(getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new JobsInstancePaymentEvent(jPlayer.getPlayer(), payments)));

				payOut(jPlayer, payments);

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

			// need to update bp
			if (block != null && !Jobs.getGCManager().useBlockProtectionBlockTracker) {
				BlockProtection bp = null;
				if (Jobs.getGCManager().useNewBlockProtection) {
					getExploitManager().setPaid(block, true);
				} else
					bp = getBpManager().getBp(block.getLocation());
				if (bp != null)
					bp.setPaid(true);
			}

			expiredJobs.forEach(j -> getPlayerManager().leaveJob(jPlayer, j));
		}
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

	public static void perform(JobsPlayer jPlayer, ActionInfo info, BufferedPayment payment, Job job, Block block, Entity ent, LivingEntity victim) {

		// Need to clone
		payment = new BufferedPayment(jPlayer.getPlayer(), payment.getPayment());

		double expPayment = payment.get(CurrencyType.EXP);

		JobsPrePaymentEvent jobsPrePaymentEvent = new JobsPrePaymentEvent(jPlayer.getPlayer(), job, payment.getPayment(), block, ent, victim, info);
		Bukkit.getServer().getPluginManager().callEvent(jobsPrePaymentEvent);
		// If event is canceled, don't do anything
		if (jobsPrePaymentEvent.isCancelled())
			return;

		payment.set(CurrencyType.MONEY, jobsPrePaymentEvent.getAmount());
		payment.set(CurrencyType.POINTS, jobsPrePaymentEvent.getPoints());

		JobsExpGainEvent jobsExpGainEvent = new JobsExpGainEvent(payment.getOfflinePlayer(), job, expPayment, block, ent, victim, info);
		Bukkit.getServer().getPluginManager().callEvent(jobsExpGainEvent);
		// If event is canceled, don't do anything
		if (jobsExpGainEvent.isCancelled())
			return;

		checkDailyQuests(jPlayer, job, info);

		payment.set(CurrencyType.EXP, jobsExpGainEvent.getExp());

		boolean limited = true;
		for (CurrencyType one : CurrencyType.values()) {
			if (jPlayer.isUnderLimit(one, payment.get(one))) {
				limited = false;
				break;
			}
		}

		if (limited)
			return;

		payOut(jPlayer, payment);

		JobProgression prog = jPlayer.getJobProgression(job);
		int oldLevel = prog.getLevel();

		if (gConfigManager.LoggingUse) {
			getLoging().recordToLog(jPlayer, info, payment.getPayment());
		}

		if (prog.addExperience(expPayment))
			getPlayerManager().performLevelUp(jPlayer, prog.getJob(), oldLevel);
	}

	private static void payOut(JobsPlayer jPlayer, BufferedPayment payment) {
		if (getEconomy() == null || payment == null)
			return;

		payOut(jPlayer, payment.getPayment());
	}

	private static void payOut(JobsPlayer jPlayer, Map<CurrencyType, Double> payments) {
		if (getEconomy() == null || payments == null)
			return;

		getEconomy().pay(jPlayer, payments);
	}

	public static SelectionManager getSelectionManager() {
		return smanager;
	}

	public static boolean hasPermission(Object sender, String perm, boolean rawEnable) {
		if (!(sender instanceof Player) || ((Player) sender).hasPermission(perm))
			return true;

		if (!rawEnable) {
			CMIMessages.sendMessage(sender, LC.info_NoPermission);
			return false;
		}
		new RawMessage().addText(LC.info_NoPermission.getLocale()).addHover("&2" + perm).show((Player) sender);
		return false;

	}

	public static boolean hasLimitedItems() {
		return hasLimitedItems;
	}
}
