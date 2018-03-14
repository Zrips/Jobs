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

package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.container.CurrencyLimit;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.LocaleReader;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.VersionChecker.Version;

public class GeneralConfigManager {
    private Jobs plugin;
    public List<Integer> BroadcastingLevelUpLevels = new ArrayList<Integer>();
    protected Locale locale;
    protected int savePeriod;
    protected boolean economyAsync;
    protected boolean isBroadcastingSkillups;
    protected boolean isBroadcastingLevelups;
    protected boolean payInCreative;
    protected boolean payExploringWhenFlying;
    protected boolean addXpPlayer;
    protected boolean hideJobsWithoutPermission;
    protected int maxJobs;
    protected boolean payNearSpawner;
    protected boolean modifyChat;
    public String modifyChatPrefix;
    public String modifyChatSuffix;
    public String modifyChatSeparator;
    protected int economyBatchDelay;
    protected boolean saveOnDisconnect;
    protected boolean MultiServerCompatability;
//    public boolean LocalOfflinePlayersData;
    public boolean MythicMobsEnabled;
    public boolean LoggingUse;
    public boolean PaymentMethodsMoney;
    public boolean PaymentMethodsPoints;
    public boolean PaymentMethodsExp;
    private HashMap<CurrencyType, Double> generalMulti = new HashMap<CurrencyType, Double>();
    public int getSelectionTooldID;

    private int ResetTimeHour;
    private int ResetTimeMinute;

    // Limits
    public HashMap<CurrencyType, CurrencyLimit> currencyLimitUse = new HashMap<CurrencyType, CurrencyLimit>();

    public boolean PayForRenaming, PayForEachCraft, SignsEnabled,
	SignsColorizeJobName, ShowToplistInScoreboard, useGlobalTimer, useCoreProtect, BlockPlaceUse,
	EnableAnounceMessage, useSilkTouchProtection, UseCustomNames,
	UseJobsBrowse, PreventSlimeSplit, PreventMagmaCubeSplit, PreventHopperFillUps, PreventBrewingStandFillUps,
	BrowseUseNewLook;
    public int globalblocktimer, CowMilkingTimer,
	CoreProtectInterval, BlockPlaceInterval, InfoUpdateInterval;
    public Double TreeFellerMultiplier, gigaDrillMultiplier, superBreakerMultiplier;
    public String localeString = "EN";

    private boolean FurnacesReassign, BrewingStandsReassign;
    private int FurnacesMaxDefault, BrewingStandsMaxDefault, BrowseAmountToShow;

    public boolean useBlockProtection;
    public int BlockProtectionDays;

    public boolean useMinimumOveralPayment;
    public boolean useMinimumOveralPoints;
    public boolean useBreederFinder = false;
    private boolean useTnTFinder = false;
    public boolean CancelCowMilking;
    public boolean fixAtMaxLevel, ToggleActionBar, TitleChangeChat, TitleChangeActionBar, LevelChangeChat,
	LevelChangeActionBar, SoundLevelupUse, SoundTitleChangeUse, UseServerAccount, EmptyServerAcountChat,
	EmptyServerAcountActionBar, ActionBarsMessageByDefault, ShowTotalWorkers, ShowPenaltyBonus, useDynamicPayment,
	JobsGUIOpenOnBrowse, JobsGUIShowChatBrowse, JobsGUISwitcheButtons, JobsGUIOpenOnJoin;

    private int JobsGUIRows, JobsGUIBackButton,
	JobsGUIStartPosition,
	JobsGUIGroupAmount,
	JobsGUISkipAmount;

    private String DecimalPlacesMoney, DecimalPlacesExp, DecimalPlacesPoints;

    public ItemStack guiBackButton;
    public ItemStack guiFiller;

    public Integer levelLossPercentageFromMax, levelLossPercentage, SoundLevelupVolume, SoundLevelupPitch, SoundTitleChangeVolume,
	SoundTitleChangePitch, ToplistInScoreboardInterval;
    public double MinimumOveralPaymentLimit;
    public double MinimumOveralPointsLimit;

    public boolean MonsterDamageUse = false;
    public double MonsterDamagePercentage;

    public double DynamicPaymentMaxPenalty;
    public double DynamicPaymentMaxBonus;
    public double TaxesAmount;
    public String SoundLevelupSound, SoundTitleChangeSound, ServerAcountName, ServertaxesAcountName;
    public ArrayList<String> keys;
    public boolean hideJobsInfoWithoutPermission;
    public boolean UseTaxes;
    public boolean TransferToServerAccount;
    public boolean TakeFromPlayersPayment;

    public int AutoJobJoinDelay;
    public boolean AutoJobJoinUse;

    public boolean AllowDelevel;

    //BossBar
    public boolean BossBarEnabled;
    public boolean BossBarShowOnEachAction;
    public int BossBarTimer;
    public boolean BossBarsMessageByDefault;

    public Parser DynamicPaymentEquation;

    public boolean DisabledWorldsUse;
    public List<String> DisabledWorldsList = new ArrayList<String>();

    public List<Schedule> BoostSchedule = new ArrayList<Schedule>();

    public HashMap<String, List<String>> commandArgs = new HashMap<String, List<String>>();

    public boolean DBCleaningJobsUse;
    public int DBCleaningJobsLvl;
    public boolean DBCleaningUsersUse;
    public int DBCleaningUsersDays;

    private boolean ShowNewVersion;

    public HashMap<String, List<String>> getCommandArgs() {
	return commandArgs;
    }

    public CurrencyLimit getLimit(CurrencyType type) {
	return currencyLimitUse.get(type);
    }

    public GeneralConfigManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public void setBreederFinder(boolean state) {
	this.useBreederFinder = state;
    }

    public boolean isUseBreederFinder() {
	return this.useBreederFinder;
    }

    public void setTntFinder(boolean state) {
	this.useTnTFinder = state;
    }

    public boolean isUseTntFinder() {
	return this.useTnTFinder;
    }

    /**
     * Get how often in minutes to save job information
     * @return how often in minutes to save job information
     */
    public synchronized int getSavePeriod() {
	return savePeriod;
    }

    /**
     * Should we use asynchronous economy calls
     * @return true - use async
     * @return false - use sync
     */
    public synchronized boolean isEconomyAsync() {
	return economyAsync;
    }

    /**
     * Function that tells if the system is set to broadcast on skill up
     * @return true - broadcast on skill up
     * @return false - do not broadcast on skill up
     */
    public synchronized boolean isBroadcastingSkillups() {
	return isBroadcastingSkillups;
    }

    /**
     * Function that tells if the system is set to broadcast on level up
     * @return true - broadcast on level up
     * @return false - do not broadcast on level up
     */
    public synchronized boolean isBroadcastingLevelups() {
	return isBroadcastingLevelups;
    }

    /**
     * Function that tells if the player should be paid while in creative
     * @return true - pay in creative
     * @return false - do not pay in creative
     */
    public synchronized boolean payInCreative() {
	return payInCreative;
    }

    /**
     * Function that tells if the player should be paid while exploring and flying
     * @return true - pay
     * @return false - do not
     */
    public synchronized boolean payExploringWhenFlying() {
	return payExploringWhenFlying;
    }

    public synchronized boolean addXpPlayer() {
	return addXpPlayer;
    }

    /**
     * Function to check if jobs should be hidden to players that lack permission to join the job
     * @return
     */
    public synchronized boolean getHideJobsWithoutPermission() {
	return hideJobsWithoutPermission;
    }

    /**
     * Function to return the maximum number of jobs a player can join
     * @return
     */
    public synchronized int getMaxJobs() {
	return maxJobs;
    }

    /**
     * Function to check if you get paid near a spawner is enabled
     * @return true - you get paid
     * @return false - you don't get paid
     */
    public synchronized boolean payNearSpawner() {
	return payNearSpawner;
    }

    public synchronized boolean getModifyChat() {
	return modifyChat;
    }

    public String getModifyChatPrefix() {
	return modifyChatPrefix;
    }

    public String getModifyChatSuffix() {
	return modifyChatSuffix;
    }

    public String getModifyChatSeparator() {
	return modifyChatSeparator;
    }

    public synchronized int getEconomyBatchDelay() {
	return economyBatchDelay;
    }

    public synchronized boolean saveOnDisconnect() {
	return saveOnDisconnect;
    }

    public synchronized boolean MultiServerCompatability() {
	return MultiServerCompatability;
    }

    public synchronized Locale getLocale() {
	return locale;
    }

    public boolean canPerformActionInWorld(Entity ent) {
	if (ent == null)
	    return true;
	if (ent.getWorld() == null)
	    return true;
	return canPerformActionInWorld(ent.getWorld());
    }

    public boolean canPerformActionInWorld(Player player) {
	if (player == null)
	    return true;
	return canPerformActionInWorld(player.getWorld());
    }

    public boolean canPerformActionInWorld(World world) {
	if (world == null)
	    return true;
	if (!this.DisabledWorldsUse)
	    return true;
	return canPerformActionInWorld(world.getName());
    }

    public boolean canPerformActionInWorld(String world) {
	if (world == null)
	    return true;
	if (!this.DisabledWorldsUse)
	    return true;
	if (this.DisabledWorldsList.isEmpty())
	    return true;
	if (this.DisabledWorldsList.contains(world))
	    return false;
	return true;
    }

    public synchronized void reload() {
	// general settings
	loadGeneralSettings();
	Jobs.getJobsDAO().cleanJobs();
	Jobs.getJobsDAO().cleanUsers();
	// Load locale
	Jobs.setLanguageManager(this.plugin);
	Jobs.getLanguageManager().load();
	// title settings
	Jobs.setTitleManager(this.plugin);
	Jobs.gettitleManager().load();
	// restricted areas
	Jobs.setRestrictedAreaManager(this.plugin);
	Jobs.getRestrictedAreaManager().load();
	// restricted blocks
	Jobs.setRestrictedBlockManager(this.plugin);
	Jobs.getRestrictedBlockManager().load();
	// Item/Block/mobs name list
	Jobs.setNameTranslatorManager(this.plugin);
	Jobs.getNameTranslatorManager().load();
	// signs information
	Jobs.setSignUtil(this.plugin);
	Jobs.getSignUtil().LoadSigns();
	// Schedule
	Jobs.setScheduleManager(this.plugin);
	// Shop
	Jobs.setShopManager(this.plugin);
	Jobs.getShopManager().load();
    }

    /**
     * Method to load the general configuration
     * 
     * loads from Jobs/generalConfig.yml
     */
    @SuppressWarnings("deprecation")
    private synchronized void loadGeneralSettings() {
	File f = new File(plugin.getDataFolder(), "generalConfig.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	CommentedYamlConfiguration write = new CommentedYamlConfiguration();
	LocaleReader c = new LocaleReader(conf, write);

	StringBuilder header = new StringBuilder();
	header.append("General configuration.");
	header.append(System.getProperty("line.separator"));
	header.append("  The general configuration for the jobs plugin mostly includes how often the plugin");
	header.append(System.getProperty("line.separator"));
	header.append("saves user data (when the user is in the game), the storage method, whether");
	header.append(System.getProperty("line.separator"));
	header.append("to broadcast a message to the server when a user goes up a skill level.");
	header.append(System.getProperty("line.separator"));
	header.append("  It also allows admins to set the maximum number of jobs a player can have at");
	header.append(System.getProperty("line.separator"));
	header.append("any one time.");
	header.append(System.getProperty("line.separator"));

	c.getC().options().copyDefaults(true);

	c.getW().options().header(header.toString());

	c.getW().addComment("locale-language", "Default language.", "Example: en, ru", "File in locale folder with same name should exist. Example: messages_ru.yml");
	localeString = c.get("locale-language", "en");
	try {
	    int i = localeString.indexOf('_');
	    if (i == -1) {
		locale = new Locale(localeString);
	    } else {
		locale = new Locale(localeString.substring(0, i), localeString.substring(i + 1));
	    }
	} catch (IllegalArgumentException e) {
	    locale = Locale.getDefault();
	    Jobs.getPluginLogger().warning("Invalid locale \"" + localeString + "\" defaulting to " + locale.getLanguage());
	}

	Jobs.getDBManager().start(c);

//	c.getW().addComment("storage-method", "storage method, can be MySQL, sqlite");
//	storageMethod = c.get("storage-method", "sqlite");
//	if (storageMethod.equalsIgnoreCase("mysql")) {
//	    startMysql();
//	} else if (storageMethod.equalsIgnoreCase("sqlite")) {
//	    startSqlite();
//	} else {
//	    Jobs.getPluginLogger().warning("Invalid storage method!  Changing method to sqlite!");
//	    c.getC().set("storage-method", "sqlite");
//	    startSqlite();
//	}
//
//	c.getW().addComment("mysql-username", "Requires Mysql.");
//	c.get("mysql-username", "root");
//	c.get("mysql-password", "");
//	c.get("mysql-hostname", "localhost:3306");
//	c.get("mysql-database", "minecraft");
//	c.get("mysql-table-prefix", "jobs_");

	c.getW().addComment("save-period", "How often in minutes you want it to save.  This must be a non-zero number");
	c.get("save-period", 10);
	if (c.getC().getInt("save-period") <= 0) {
	    Jobs.getPluginLogger().severe("Save period must be greater than 0!  Defaulting to 10 minutes!");
	    c.getC().set("save-period", 10);
	}
	savePeriod = c.getC().getInt("save-period");

	c.getW().addComment("save-on-disconnect", "Should player data be saved on disconnect?",
	    "Player data is always periodically auto-saved and autosaved during a clean shutdown.",
	    "Only enable this if you have a multi-server setup, or have a really good reason for enabling this.", "Turning this on will decrease database performance.");
	saveOnDisconnect = c.get("save-on-disconnect", false);

	c.getW().addComment("selectionTool", "Tool used when selecting bounds for restricted area");
	getSelectionTooldID = c.get("selectionTool", 294);
	if (Material.getMaterial(Jobs.getGCManager().getSelectionTooldID) == null)
	    getSelectionTooldID = 294;

	c.getW().addComment("MultiServerCompatability", "Enable if you are using one data base for multiple servers across bungee network",
	    "This will force to load players data every time he is logging in to have most up to date data instead of having preloaded data",
	    "This will enable automaticaly save-on-disconnect feature");
	MultiServerCompatability = c.get("MultiServerCompatability", false);
	if (MultiServerCompatability)
	    saveOnDisconnect = true;

	c.getW().addComment("Optimizations.NewVersion",
	    "When set to true staff will be informed about new Jobs plugin version", "You need to have jobs.versioncheck permission node");
	ShowNewVersion = c.get("Optimizations.NewVersion", true);

	c.getW().addComment("Optimizations.DecimalPlaces.Money",
	    "Decimal places to be shown");
	DecimalPlacesMoney = "%." + c.get("Optimizations.DecimalPlaces.Money", 2) + "f";
	DecimalPlacesExp = "%." + c.get("Optimizations.DecimalPlaces.Exp", 2) + "f";
	DecimalPlacesPoints = "%." + c.get("Optimizations.DecimalPlaces.Points", 2) + "f";

	c.getW().addComment("Optimizations.DBCleaning.Jobs.Use",
	    "Warning!!! before enabling this feature, please make data base backup, just in case there will be some issues with data base cleaning",
	    "When set to true, jobs data base will be cleaned on each startup to avoid having not used jobs",
	    "keep in mind that this will only clean actual jobs, but not recorded players");
	DBCleaningJobsUse = c.get("Optimizations.DBCleaning.Jobs.Use", false);
	c.getW().addComment("Optimizations.DBCleaning.Jobs.Level", "Any one who has jobs level equal or less then set, hies job will be removed from data base");
	DBCleaningJobsLvl = c.get("Optimizations.DBCleaning.Jobs.Level", 1);

	c.getW().addComment("Optimizations.DBCleaning.Users.Use",
	    "Warning!!! before enabling this feature, please make data base backup, just in case there will be some issues with data base cleaning",
	    "When set to true, data base will be cleaned on each startup from user data to avoid having old player data");
	DBCleaningUsersUse = c.get("Optimizations.DBCleaning.Users.Use", false);
	c.getW().addComment("Optimizations.DBCleaning.Users.Days", "Any one who not playied for defined amount of days, will be removed from data base");
	DBCleaningUsersDays = c.get("Optimizations.DBCleaning.Users.Days", 60);

	c.getW().addComment("Optimizations.AutoJobJoin.Use", "Use or not auto join jobs feature",
	    "If you are not using auto join feature, keep it disabled");
	AutoJobJoinUse = c.get("Optimizations.AutoJobJoin.Use", false);
	c.getW().addComment("Optimizations.AutoJobJoin.Delay", "Delay in seconds to perform auto join job if used after player joins server",
	    "If you using offline server, try to keep it slightly more than your login plugin gives time to enter password",
	    "For player to auto join job add permission node jobs.autojoin.[jobname]",
	    "Op players are ignored");
	AutoJobJoinDelay = c.get("Optimizations.AutoJobJoin.Delay", 15);

	c.getW().addComment("Optimizations.AllowDelevel", "When set to true players who gets negavite experience can delevel job up to level 1",
	    "ATTENTION! Set it to true only if you certain that commands performed on levelup will not cause issues if player start level and delevel in a row.");
	AllowDelevel = c.get("Optimizations.AllowDelevel", false);

//	c.getW().addComment("Optimizations.UseLocalOfflinePlayersData", "With this set to true, offline player data will be taken from local player data files",
//	    "This will eliminate small lag spikes when request is being send to mojangs servers for offline players data",
//	    "Theroticali this should work without issues, but if you havving some, just disable",
//	    "But then you can feal some small (100-200ms) lag spikes while performings some jobs commands");
//	LocalOfflinePlayersData = c.get("Optimizations.UseLocalOfflinePlayersData", true);

	c.getW().addComment("Optimizations.DisabledWorlds.Use", "By setting this to true, Jobs plugin will be disabled in given worlds",
	    "Only commands can be performed from disabled worlds with jobs.disabledworld.commands permission node");
	DisabledWorldsUse = c.get("Optimizations.DisabledWorlds.Use", false);
	DisabledWorldsList = c.getStringList("Optimizations.DisabledWorlds.List", Arrays.asList(Bukkit.getWorlds().get(0).getName()));

//	c.getW().addComment("Optimizations.Purge.Use", "By setting this to true, Jobs plugin will clean data base on startup from all jobs with level 1 and at 0 exp");
//	PurgeUse = c.get("Optimizations.Purge.Use", false);

	c.getW().addComment("Logging.Use", "With this set to true all players jobs actions will be logged to database for easy to see statistics",
	    "This is still in development and in feature it will expand");
	LoggingUse = c.get("Logging.Use", false);

	c.getW().addComment("broadcast.on-skill-up.use", "Do all players get a message when somone goes up a skill level?");
	isBroadcastingSkillups = c.get("broadcast.on-skill-up.use", false);

	c.getW().addComment("broadcast.on-level-up.use", "Do all players get a message when somone goes up a level?");
	isBroadcastingLevelups = c.get("broadcast.on-level-up.use", false);
	c.getW().addComment("broadcast.on-level-up.levels", "For what levels you want to broadcast message? Keep it at 0 if you want for all of them");
	BroadcastingLevelUpLevels = c.getIntList("broadcast.on-level-up.levels", Arrays.asList(0));

	c.getW().addComment("DailyQuests.ResetTime", "Defines time in 24hour format when we want to give out new daily quests",
	    "Any daily quests given before reset will be invalid and new ones will be given out");
	ResetTimeHour = c.get("DailyQuests.ResetTime.Hour", 4);
	ResetTimeMinute = c.get("DailyQuests.ResetTime.Minute", 0);

	c.getW().addComment("max-jobs", "Maximum number of jobs a player can join.", "Use 0 for no maximum", "Keep in mind that jobs.max.[amount] will bypass this setting");
	maxJobs = c.get("max-jobs", 3);

	c.getW().addComment("hide-jobs-without-permission", "Hide jobs from player if they lack the permission to join the job");
	hideJobsWithoutPermission = c.get("hide-jobs-without-permission", false);

	c.getW().addComment("hide-jobsinfo-without-permission", "Hide jobs info from player if they lack the permission to join the job");
	hideJobsInfoWithoutPermission = c.get("hide-jobsinfo-without-permission", false);

	c.getW().addComment("enable-pay-near-spawner",
	    "Option to allow payment to be made when killing mobs from a spawner.",
	    "Use jobs.nearspawner.[amount] to define multiplayer. Example jobs.nearspawner.-0.5 will pay half of payment, jobs.nearspawner.-1 will not pay at all");
	payNearSpawner = c.get("enable-pay-near-spawner", false);

	c.getW().addComment("enable-pay-creative", "Option to allow payment to be made in creative mode");
	payInCreative = c.get("enable-pay-creative", false);

	c.getW().addComment("enable-pay-for-exploring-when-flying", "Option to allow payment to be made for exploring when player flyies");
	payExploringWhenFlying = c.get("enable-pay-for-exploring-when-flying", false);

	c.getW().addComment("add-xp-player", "Adds the Jobs xp recieved to the player's Minecraft XP bar");
	addXpPlayer = c.get("add-xp-player", false);

	c.getW().addComment("modify-chat",
	    "Modifys chat to add chat titles.  If you're using a chat manager, you may add the tag {jobs} to your chat format and disable this.");
	modifyChat = c.get("modify-chat", true);

	modifyChatPrefix = c.get("modify-chat-prefix", "&c[", true);
	modifyChatSuffix = c.get("modify-chat-suffix", "&c]&r", true);
	modifyChatSeparator = c.get("modify-chat-separator", " ", true);

	c.getW().addComment("UseCustomNames", "Do you want to use custom item/block/mob/enchant/color names",
	    "With this set to true names like Stone:1 will be translated to Granite", "Name list is in TranslatableWords.yml file");
	UseCustomNames = c.get("UseCustomNames", true);

	c.getW().addComment("economy-batch-delay", "Changes how often, in seconds, players are paid out.  Default is 5 seconds.",
	    "Setting this too low may cause tick lag.  Increase this to improve economy performance (at the cost of delays in payment)");
	economyBatchDelay = c.get("economy-batch-delay", 5);

	c.getW().addComment("economy-async", "Enable async economy calls.", "Disable this if you have issues with payments or your plugin is not thread safe.");
	economyAsync = c.get("economy-async", true);

	c.getW().addComment("Economy.PaymentMethods",
	    "By disabling one of thies, players no longer will get particular payment.",
	    "Usefull for removing particular payment method without editing whole jobConfig file");
	PaymentMethodsMoney = c.get("Economy.PaymentMethods.Money", true);
	PaymentMethodsPoints = c.get("Economy.PaymentMethods.Points", true);
	PaymentMethodsExp = c.get("Economy.PaymentMethods.Exp", true);

	c.getW().addComment("Economy.GeneralMulti",
	    "Can be used to change payment amounts for all jobs and all actions if you want to readjust them",
	    "Amounts are in percentage, above 0 will increase payments",
	    "Amount belove 0 will decrease payments",
	    "If action pays negative amount, then value above 0 will increase that negative value",
	    "So is placing diamond ore takes from you 10 bucks, then by setting 50 for money income, you will be charged 15 bucks for placing it",
	    "If you are getting paid 10 for placing wood, then same value of 50 for money income, will result you getting 15 bucks",
	    "This only effects base income value");
	for (CurrencyType one : CurrencyType.values()) {
	    generalMulti.put(one, c.get("Economy.GeneralMulti." + one.name(), 0D) / 100D);
	}

	c.getW().addComment("Economy.MinimumOveralPayment.use",
	    "Determines minimum payment. In example if player uses McMMO treefeller and earns only 20%, but at same time he gets 25% penalty from dynamic payment. He can 'get' negative amount of money",
	    "This will limit it to particular percentage", "Works only when original payment is above 0");
	useMinimumOveralPayment = c.get("Economy.MinimumOveralPayment.use", true);
	MinimumOveralPaymentLimit = c.get("Economy.MinimumOveralPayment.limit", 0.1);
	c.getW().addComment("Economy.MinimumOveralPoints.use",
	    "Determines minimum payment. In example if player uses McMMO treefeller and earns only 20%, but at same time he gets 25% penalty from dynamic payment. He can 'get' negative amount of money",
	    "This will limit it to particular percentage", "Works only when original payment is above 0");
	useMinimumOveralPoints = c.get("Economy.MinimumOveralPoints.use", true);
	MinimumOveralPointsLimit = c.get("Economy.MinimumOveralPoints.limit", 0.1);

	c.getW().addComment("Economy.DynamicPayment.use", "Do you want to use dinamic payment dependent on how many players already working for jobs",
	    "This can help automaticaly lift up payments for not so popular jobs and lower for most popular ones");
	useDynamicPayment = c.get("Economy.DynamicPayment.use", false);

	String maxExpEquationInput = c.get("Economy.DynamicPayment.equation", "((totalworkers / totaljobs) - jobstotalplayers)/10.0");
	try {
	    DynamicPaymentEquation = new Parser(maxExpEquationInput);
	    // test equation
	    DynamicPaymentEquation.setVariable("totalworkers", 100);
	    DynamicPaymentEquation.setVariable("totaljobs", 10);
	    DynamicPaymentEquation.setVariable("jobstotalplayers", 10);
	    DynamicPaymentEquation.getValue();
	} catch (Exception e) {
	    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Dynamic payment equation has an invalid property. Disabling feature!");
	    useDynamicPayment = false;
	}

	DynamicPaymentMaxPenalty = c.get("Economy.DynamicPayment.MaxPenalty", 25.0);
	DynamicPaymentMaxBonus = c.get("Economy.DynamicPayment.MaxBonus", 100.0);

	c.getW().addComment("Economy.UseServerAcount", "Server economy acount", "With this enabled, players will get money from defined user (server account)",
	    "If this acount dont have enough money to pay for players for, player will get message");
	UseServerAccount = c.get("Economy.UseServerAcount", false);
	c.getW().addComment("Economy.AcountName", "Username should be with Correct capitalization");
	ServerAcountName = c.get("Economy.AcountName", "Server");
	c.getW().addComment("Economy.Taxes.use", "Do you want to use taxes feature for jobs payment");
	UseTaxes = c.get("Economy.Taxes.use", false);
	c.getW().addComment("Economy.Taxes.AccountName", "Username should be with Correct capitalization, it can be same as settup in server account before");
	ServertaxesAcountName = c.get("Economy.Taxes.AccountName", "Server");
	c.getW().addComment("Economy.Taxes.Amount", "Amount in percentage");
	TaxesAmount = c.get("Economy.Taxes.Amount", 15.0);
	c.getW().addComment("Economy.Taxes.TransferToServerAccount", "Do you want to transfer taxes to server account");
	TransferToServerAccount = c.get("Economy.Taxes.TransferToServerAccount", true);
	c.getW().addComment("Economy.Taxes.TakeFromPlayersPayment",
	    "With this true, taxes will be taken from players payment and he will get less money than its shown in jobs info",
	    "When its false player will get full payment and server account will get taxes amount to hes account");
	TakeFromPlayersPayment = c.get("Economy.Taxes.TakeFromPlayersPayment", false);

	// Money limit
	CurrencyLimit limit = new CurrencyLimit();
	c.getW().addComment("Economy.Limit.Money", "Money gain limit", "With this enabled, players will be limited how much they can make in defined time",
	    "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setEnabled(c.get("Economy.Limit.Money.Use", false));
	List<CurrencyType> list = new ArrayList<CurrencyType>();
	c.getW().addComment("Economy.Limit.Money.StopWithExp", "Do you want to stop money gain when exp limit reached?");
	if (c.get("Economy.Limit.Money.StopWithExp", false))
	    list.add(CurrencyType.EXP);
	c.getW().addComment("Economy.Limit.Money.StopWithPoint", "Do you want to stop money gain when point limit reached?");
	if (c.get("Economy.Limit.Money.StopWithPoint", false))
	    list.add(CurrencyType.POINTS);
	limit.setStopWith(list);
	c.getW().addComment("Economy.Limit.Money.MoneyLimit",
	    "Equation to calculate max limit. Option to use totallevel to include players total amount levels of current jobs",
	    "You can always use simple number to set money limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String MoneyLimit = c.get("Economy.Limit.Money.MoneyLimit", "500+500*(totallevel/100)");
	try {
	    Parser Equation = new Parser(MoneyLimit);
	    Equation.setVariable("totallevel", 1);
	    Equation.getValue();
	    limit.setMaxEquation(Equation);
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("MoneyLimit has an invalid value. Disabling money limit!");
	    limit.setEnabled(false);
	}
	c.getW().addComment("Economy.Limit.Money.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setTimeLimit(c.get("Economy.Limit.Money.TimeLimit", 3600));
	c.getW().addComment("Economy.Limit.Money.AnnouncmentDelay", "Delay between announcements about reached money limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	limit.setAnnouncmentDelay(c.get("Economy.Limit.Money.AnnouncmentDelay", 30));
	currencyLimitUse.put(CurrencyType.MONEY, limit);

	// Point limit
	limit = new CurrencyLimit();
	list = new ArrayList<CurrencyType>();
	c.getW().addComment("Economy.Limit.Point", "Point gain limit", "With this enabled, players will be limited how much they can make in defined time");
	limit.setEnabled(c.get("Economy.Limit.Point.Use", false));
	c.getW().addComment("Economy.Limit.Point.StopWithExp", "Do you want to stop Point gain when exp limit reached?");
	if (c.get("Economy.Limit.Point.StopWithExp", false))
	    list.add(CurrencyType.EXP);
	c.getW().addComment("Economy.Limit.Point.StopWithMoney", "Do you want to stop Point gain when money limit reached?");
	if (c.get("Economy.Limit.Point.StopWithMoney", false))
	    list.add(CurrencyType.MONEY);
	limit.setStopWith(list);
	c.getW().addComment("Economy.Limit.Point.Limit",
	    "Equation to calculate max limit. Option to use totallevel to include players total amount levels of current jobs",
	    "You can always use simple number to set limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String PointLimit = c.get("Economy.Limit.Point.Limit", "500+500*(totallevel/100)");
	try {
	    Parser Equation = new Parser(PointLimit);
	    Equation.setVariable("totallevel", 1);
	    Equation.getValue();
	    limit.setMaxEquation(Equation);
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("PointLimit has an invalid value. Disabling money limit!");
	    limit.setEnabled(false);
	}
	c.getW().addComment("Economy.Limit.Point.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setTimeLimit(c.get("Economy.Limit.Point.TimeLimit", 3600));
	c.getW().addComment("Economy.Limit.Point.AnnouncmentDelay", "Delay between announcements about reached limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	limit.setAnnouncmentDelay(c.get("Economy.Limit.Point.AnnouncmentDelay", 30));
	currencyLimitUse.put(CurrencyType.POINTS, limit);

	// Exp limit
	limit = new CurrencyLimit();
	list = new ArrayList<CurrencyType>();
	c.getW().addComment("Economy.Limit.Exp", "Exp gain limit", "With this enabled, players will be limited how much they can get in defined time",
	    "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setEnabled(c.get("Economy.Limit.Exp.Use", false));
	c.getW().addComment("Economy.Limit.Exp.StopWithMoney", "Do you want to stop exp gain when money limit reached?");
	if (c.get("Economy.Limit.Exp.StopWithMoney", false))
	    list.add(CurrencyType.MONEY);
	c.getW().addComment("Economy.Limit.Exp.StopWithPoint", "Do you want to stop exp gain when point limit reached?");
	if (c.get("Economy.Limit.Exp.StopWithPoint", false))
	    list.add(CurrencyType.POINTS);
	limit.setStopWith(list);
	c.getW().addComment("Economy.Limit.Exp.Limit", "Equation to calculate max money limit. Option to use totallevel to include players total amount of current jobs",
	    "You can always use simple number to set exp limit",
	    "Default equation is: 5000+5000*(totallevel/100), this will add 1% from 5000 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 6850 limit");
	String expLimit = c.get("Economy.Limit.Exp.Limit", "5000+5000*(totallevel/100)");
	try {
	    Parser Equation = new Parser(expLimit);
	    Equation.setVariable("totallevel", 1);
	    Equation.getValue();
	    limit.setMaxEquation(Equation);
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("ExpLimit has an invalid value. Disabling money limit!");
	    limit.setEnabled(false);
	}
	c.getW().addComment("Economy.Limit.Exp.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setTimeLimit(c.get("Economy.Limit.Exp.TimeLimit", 3600));
	c.getW().addComment("Economy.Limit.Exp.AnnouncmentDelay", "Delay between announcements about reached Exp limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	limit.setAnnouncmentDelay(c.get("Economy.Limit.Exp.AnnouncmentDelay", 30));
	currencyLimitUse.put(CurrencyType.EXP, limit);

	c.getW().addComment("Economy.Repair.PayForRenaming", "Do you want to give money for only renaming items in anvil",
	    "Players will get full pay as they would for remairing two items when they only renaming one",
	    "This is not big issue, but if you want to disable it, you can");
	PayForRenaming = c.get("Economy.Repair.PayForRenaming", true);

	c.getW().addComment("Economy.Crafting.PayForEachCraft",
	    "With this true, player will get money for all crafted items instead of each crafting action (like with old payment mechanic)",
	    "By default its false, as you can make ALOT of money if prices kept from old payment mechanics");
	PayForEachCraft = c.get("Economy.Crafting.PayForEachCraft", false);

	c.getW().addComment("Economy.MilkingCow.CancelMilking", "With this true, when timer is still going, cow milking event will be canceled",
	    "With this false, player will get bucket of milk, but still no payment");
	CancelCowMilking = c.get("Economy.MilkingCow.CancelMilking", false);
	c.getW().addComment("Economy.MilkingCow.Timer",
	    "How ofter player can milk cows in seconds. Keep in mind that by default player can milk cow indefinetly and as often as he wants",
	    "Set to 0 if you want to disable timer");
	CowMilkingTimer = c.get("Economy.MilkingCow.Timer", 30) * 1000;

	c.getW().addComment("ExploitProtections.Furnaces.Reassign",
	    "When enabled, players interacted furnaces will be saved into file and will be reassigned after restart to keep giving out money",
	    "Players will no longer need to click on furnace to get paid from it after server restart");
	FurnacesReassign = c.get("ExploitProtections.Furnaces.Reassign", true);
	c.getW().addComment("ExploitProtections.Furnaces.MaxDefaultAvailable",
	    "Defines max avaible furnaces each player can have to get paid from",
	    "This can be ovveriden with jobs.maxfurnaces.[amount] permission node");
	FurnacesMaxDefault = c.get("ExploitProtections.Furnaces.MaxDefaultAvailable", 20);

	c.getW().addComment("ExploitProtections.BrewingStands.Reassign",
	    "When enabled, players interacted brewing stands will be saved into file and will be reassigned after restart to keep giving out money",
	    "Players will no longer need to click on brewing stand to get paid from it after server restart");
	BrewingStandsReassign = c.get("ExploitProtections.BrewingStands.Reassign", true);
	c.getW().addComment("ExploitProtections.BrewingStands.MaxDefaultAvailable",
	    "Defines max avaible brewing stands each player can have to get paid from",
	    "Set to 0 if you want to disable this limitation",
	    "This can be ovveriden with jobs.maxbrewingstands.[amount] permission node");
	BrewingStandsMaxDefault = c.get("ExploitProtections.BrewingStands.MaxDefaultAvailable", 20);

	c.getW().addComment("ExploitProtections.General.PlaceAndBreakProtection",
	    "Enable blocks protection, like ore, from exploiting by placing and destroying same block again and again.",
	    "Modify restrictedBlocks.yml for blocks you want to protect");
	useBlockProtection = c.get("ExploitProtections.General.PlaceAndBreakProtection", true);

	c.getW().addComment("ExploitProtections.General.KeepDataFor",
	    "For how long in days to keep block protection data in data base", "This will clean block data which ones have -1 as cooldown value",
	    "Data base cleannup will be performed on each server startup");
	BlockProtectionDays = c.get("ExploitProtections.General.KeepDataFor", 14);

	c.getW().addComment("ExploitProtections.General.GlobalBlockTimer", "All blocks will be protected X sec after player places it on ground.");
	useGlobalTimer = c.get("ExploitProtections.General.GlobalBlockTimer.use", true);
	globalblocktimer = c.get("ExploitProtections.General.GlobalBlockTimer.timer", 3);

	c.getW().addComment("ExploitProtections.General.SilkTouchProtection", "Enable silk touch protection.",
	    "With this enabled players wont get paid for breaked blocks from restrictedblocks list with silk touch tool.");
	useSilkTouchProtection = c.get("ExploitProtections.General.SilkTouchProtection", false);

	c.getW().addComment("ExploitProtections.General.MonsterDamage.Use", "This section controls how much damage player should do to monster for player to get paid",
	    "This prevents from killing monsters in one hit when they suffer in example fall damage");
	MonsterDamageUse = c.get("ExploitProtections.General.MonsterDamage.Use", false);
	MonsterDamagePercentage = c.get("ExploitProtections.General.MonsterDamage.Percentage", 60);

	c.getW().addComment("ExploitProtections.McMMO", "McMMO abilities");
	c.getW().addComment("ExploitProtections.McMMO.TreeFellerMultiplier", "Players will get part of money from cutting trees with treefeller ability enabled.",
	    "0.2 means 20% of original price");
	TreeFellerMultiplier = c.get("ExploitProtections.McMMO.TreeFellerMultiplier", 0.2);
	c.getW().addComment("ExploitProtections.McMMO.gigaDrillMultiplier", "Players will get part of money from braking blocks with gigaDrill ability enabled.",
	    "0.2 means 20% of original price");
	gigaDrillMultiplier = c.get("ExploitProtections.McMMO.gigaDrillMultiplier", 0.2);
	c.getW().addComment("ExploitProtections.McMMO.superBreakerMultiplier", "Players will get part of money from braking blocks with super breaker ability enabled.",
	    "0.2 means 20% of original price");
	superBreakerMultiplier = c.get("ExploitProtections.McMMO.superBreakerMultiplier", 0.2);

	c.getW().addComment("ExploitProtections.MythicMobs", "MythicMobs plugin support", "Disable if you having issues with it or using old version");
	MythicMobsEnabled = c.get("ExploitProtections.MythicMobs.enabled", true);

	c.getW().addComment("ExploitProtections.Spawner.PreventSlimeSplit", "Prevent slime spliting when they are from spawner",
	    "Protects agains exploiting as new splited slimes is treated as naturaly spawned and not from spawner");
	PreventSlimeSplit = c.get("ExploitProtections.Spawner.PreventSlimeSplit", true);
	c.getW().addComment("ExploitProtections.Spawner.PreventMagmaCubeSplit", "Prevent magmacube spliting when they are from spawner");
	PreventMagmaCubeSplit = c.get("ExploitProtections.Spawner.PreventMagmaCubeSplit", true);

	c.getW().addComment("ExploitProtections.Smelt.PreventHopperFillUps", "Prevent payments when hoppers moving items into furnace", "Player will not get paid, but items will be smellted");
	PreventHopperFillUps = c.get("ExploitProtections.Smelt.PreventHopperFillUps", true);
	c.getW().addComment("ExploitProtections.Smelt.PreventMagmaCubeSplit", "Prevent payments when hoppers moving items into brewing stands",
	    "Player will not get paid, but items will be brewd as they supose too");
	PreventBrewingStandFillUps = c.get("ExploitProtections.Brew.PreventBrewingStandFillUps", true);

	c.getW().addComment("use-breeder-finder", "Breeder finder.",
	    "If you are not using breeding payment, you can disable this to save little resources. Really little.");
	useBreederFinder = c.get("use-breeder-finder", true);

	c.getW().addComment("old-job",
	    "Old job save", "Players can leave job and return later with some level loss during that",
	    "You can fix players level if hes job level is at max level");
	levelLossPercentage = c.get("old-job.level-loss-percentage", 30);
	fixAtMaxLevel = c.get("old-job.fix-at-max-level", true);
	c.getW().addComment("old-job.level-loss-from-max-level",
	    "Percentage to loose when leaving job at max level",
	    "Only works when fix-at-max-level is set to false");
	levelLossPercentageFromMax = c.get("old-job.level-loss-from-max-level", levelLossPercentage);

	c.getW().addComment("ActionBars.Messages.EnabledByDefault", "When this set to true player will see action bar messages by default");
	ActionBarsMessageByDefault = c.get("ActionBars.Messages.EnabledByDefault", true);

	c.getW().addComment("BossBar.Enabled", "Enables BossBar feature", "Works only from 1.9 mc version");
	BossBarEnabled = c.get("BossBar.Enabled", true);

	if (Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_9_R1)) {
	    BossBarEnabled = false;
	    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Jobs] Your server version don't support BossBar. This feature will be disabled");
	}

	c.getW().addComment("BossBar.Messages.EnabledByDefault", "When this set to true player will see Bossbar messages by default");
	BossBarsMessageByDefault = c.get("BossBar.Messages.EnabledByDefault", true);

	c.getW().addComment("BossBar.ShowOnEachAction", "If enabled boss bar will update after each action",
	    "If disabled, BossBar will update only on each payment. This can save some server resources");
	BossBarShowOnEachAction = c.get("BossBar.ShowOnEachAction", false);
	c.getW().addComment("BossBar.Timer", "How long in sec to show BossBar for player",
	    "If you have disabled ShowOnEachAction, then keep this number higher than payment interval for better experience");
	BossBarTimer = c.get("BossBar.Timer", economyBatchDelay + 1);

	c.getW().addComment("ShowActionBars", "You can enable/disable message shown for players in action bar");
	TitleChangeActionBar = c.get("ShowActionBars.OnTitleChange", true);
	LevelChangeActionBar = c.get("ShowActionBars.OnLevelChange", true);
	EmptyServerAcountActionBar = c.get("ShowActionBars.OnEmptyServerAcount", true);

	c.getW().addComment("ShowChatMessage", "Chat messages", "You can enable/disable message shown for players in chat");
	TitleChangeChat = c.get("ShowChatMessage.OnTitleChange", true);
	LevelChangeChat = c.get("ShowChatMessage.OnLevelChange", true);
	EmptyServerAcountChat = c.get("ShowChatMessage.OnEmptyServerAcount", true);

	c.getW().addComment("Sounds", "Sounds", "Extra sounds on some events",
	    "All sounds can be found in https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
	SoundLevelupUse = c.get("Sounds.LevelUp.use", true);
	SoundLevelupSound = c.get("Sounds.LevelUp.sound", "ENTITY_PLAYER_LEVELUP");
	SoundLevelupVolume = c.get("Sounds.LevelUp.volume", 1);
	SoundLevelupPitch = c.get("Sounds.LevelUp.pitch", 3);
	SoundTitleChangeUse = c.get("Sounds.TitleChange.use", true);
	SoundTitleChangeSound = c.get("Sounds.TitleChange.sound", "ENTITY_PLAYER_LEVELUP");
	SoundTitleChangeVolume = c.get("Sounds.TitleChange.volume", 1);
	SoundTitleChangePitch = c.get("Sounds.TitleChange.pitch", 3);

	c.getW().addComment("Signs", "You can disable this to save SMALL amount of server resources");
	SignsEnabled = c.get("Signs.Enable", true);
	SignsColorizeJobName = c.get("Signs.Colors.ColorizeJobName", true);
	c.getW().addComment("Signs.InfoUpdateInterval",
	    "This is interval in sec in which signs will be updated. This is not continues update, signs are updated only on levelup, job leave, job join or similar action.");
	c.getW().addComment("Signs.InfoUpdateInterval",
	    "This is update for same job signs, to avoid huge lag if you have bunch of same type signs. Keep it from 1 to as many sec you want");
	InfoUpdateInterval = c.get("Signs.InfoUpdateInterval", 5);

	c.getW().addComment("Scoreboard.ShowToplist", "This will enables to show top list in scoreboard instead of chat");
	ShowToplistInScoreboard = c.get("Scoreboard.ShowToplist", true);
	c.getW().addComment("Scoreboard.interval", "For how long to show scoreboard");
	ToplistInScoreboardInterval = c.get("Scoreboard.interval", 10);

	c.getW().addComment("JobsBrowse.ShowTotalWorkers", "Do you want to show total amount of workers for job in jobs browse window");
	ShowTotalWorkers = c.get("JobsBrowse.ShowTotalWorkers", true);
	c.getW().addComment("JobsBrowse.ShowPenaltyBonus", "Do you want to show penalty and bonus in jobs browse window. Only works if this feature is enabled");
	ShowPenaltyBonus = c.get("JobsBrowse.ShowPenaltyBonus", true);

	c.getW().addComment("JobsBrowse.UseNewLook", "Defines if you want to use new /jobs browse look or old one");
	BrowseUseNewLook = c.get("JobsBrowse.UseNewLook", true);
	c.getW().addComment("JobsBrowse.AmountToShow", "Defines amount of jobs to be shown in one page for /jobs browse");
	BrowseAmountToShow = c.get("JobsBrowse.AmountToShow", 5);

	c.getW().addComment("JobsGUI.OpenOnBrowse", "Do you want to show GUI when performing /jobs browse command");
	JobsGUIOpenOnBrowse = c.get("JobsGUI.OpenOnBrowse", true);
	c.getW().addComment("JobsGUI.ShowChatBrowse", "Do you want to show chat information when performing /jobs browse command");
	JobsGUIShowChatBrowse = c.get("JobsGUI.ShowChatBrowse", true);
	c.getW().addComment("JobsGUI.SwitcheButtons", "With true left mouse button will join job and right will show more info",
	    "With false left mouse button will show more info, rigth will join job", "Dont forget to adjust locale file");
	JobsGUISwitcheButtons = c.get("JobsGUI.SwitcheButtons", false);
	c.getW().addComment("JobsGUI.Rows", "Defines size in rows of GUI");
	JobsGUIRows = c.get("JobsGUI.Rows", 5);
	c.getW().addComment("JobsGUI.BackButtonSlot", "Defines back button slot in GUI");
	JobsGUIBackButton = c.get("JobsGUI.BackButtonSlot", 37);
	c.getW().addComment("JobsGUI.StartPosition", "Defines start position in gui from which job icons will be shown");
	JobsGUIStartPosition = c.get("JobsGUI.StartPosition", 11);
	c.getW().addComment("JobsGUI.GroupAmount", "Defines By how many jobs we need to group up");
	JobsGUIGroupAmount = c.get("JobsGUI.GroupAmount", 7);
	c.getW().addComment("JobsGUI.SkipAmount", "Defines By how many slot we need to skip after group");
	JobsGUISkipAmount = c.get("JobsGUI.SkipAmount", 2);

	Material tmat = Material.getMaterial(c.get("JobsGUI.BackButton.Material", "JACK_O_LANTERN"));
	guiBackButton = new ItemStack(tmat == null ? Material.JACK_O_LANTERN : tmat, 1, (byte) c.get("JobsGUI.BackButton.Data", 0));
	tmat = Material.getMaterial(c.get("JobsGUI.Filler.Material", "STAINED_GLASS_PANE"));
	guiFiller = new ItemStack(tmat == null ? Material.STAINED_GLASS_PANE : tmat, 1, (byte) c.get("JobsGUI.Filler.Data", 15));

//	c.getW().addComment("Schedule.Boost.Enable", "Do you want to enable scheduler for global boost");
//	useGlobalBoostScheduler = c.get("Schedule.Boost.Enable", false);

	try {
	    c.getW().save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public int getSelectionTooldID() {
	return getSelectionTooldID;
    }

    public boolean isShowNewVersion() {
	return ShowNewVersion;
    }

    public int getResetTimeHour() {
	return ResetTimeHour;
    }

    public void setResetTimeHour(int resetTimeHour) {
	ResetTimeHour = resetTimeHour;
    }

    public int getResetTimeMinute() {
	return ResetTimeMinute;
    }

    public void setResetTimeMinute(int resetTimeMinute) {
	ResetTimeMinute = resetTimeMinute;
    }

    public boolean isFurnacesReassign() {
	return FurnacesReassign;
    }

    public boolean isBrewingStandsReassign() {
	return BrewingStandsReassign;
    }

    public int getFurnacesMaxDefault() {
	return FurnacesMaxDefault;
    }

    public int getBrewingStandsMaxDefault() {
	return BrewingStandsMaxDefault;
    }

    public int getBrowseAmountToShow() {
	return BrowseAmountToShow;
    }

    public String getDecimalPlacesMoney() {
	return DecimalPlacesMoney;
    }

    public String getDecimalPlacesExp() {
	return DecimalPlacesExp;
    }

    public String getDecimalPlacesPoints() {
	return DecimalPlacesPoints;
    }

    public int getJobsGUIRows() {
	if (JobsGUIRows < 1)
	    JobsGUIRows = 1;
	return JobsGUIRows;
    }

    public int getJobsGUIBackButton() {
	if (JobsGUIBackButton < 1)
	    JobsGUIBackButton = 1;
	if (JobsGUIBackButton > JobsGUIRows * 9)
	    JobsGUIBackButton = JobsGUIRows * 9;
	return JobsGUIBackButton - 1;
    }

    public int getJobsGUIStartPosition() {
	if (JobsGUIBackButton < 1)
	    JobsGUIBackButton = 1;
	return JobsGUIStartPosition - 1;
    }

    public int getJobsGUIGroupAmount() {
	return JobsGUIGroupAmount;
    }

    public int getJobsGUISkipAmount() {
	return JobsGUISkipAmount;
    }

    public Double getGeneralMulti(CurrencyType type) {
	return generalMulti.get(type);
    }

}
