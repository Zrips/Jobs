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
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.container.LocaleReader;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.dao.JobsDAOMySQL;
import com.gamingmesh.jobs.dao.JobsDAOSQLite;
import com.gamingmesh.jobs.stuff.ChatColor;

public class GeneralConfigManager {
    private JobsPlugin plugin;
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
    public boolean LocalOfflinePlayersData;
    public boolean MythicMobsEnabled;
    public boolean LoggingUse;

    // Money limit
    public boolean MoneyLimitUse;
    public boolean MoneyStopPoint;
    public boolean MoneyStopExp;
    public int MoneyTimeLimit;
    public int MoneyAnnouncmentDelay;

    // Point limit
    public boolean PointLimitUse;
    public boolean PointStopExp;
    public boolean PointStopMoney;
    public int PointTimeLimit;
    public int PointAnnouncmentDelay;

    // Exp limit
    public boolean ExpLimitUse;
    public boolean ExpStopPoint;
    public boolean ExpStopMoney;
    public int ExpTimeLimit;
    public int ExpAnnouncmentDelay;

    public boolean PayForRenaming, PayForEachCraft, SignsEnabled,
	SignsColorizeJobName, ShowToplistInScoreboard, useGlobalTimer, useCoreProtect, BlockPlaceUse,
	EnableAnounceMessage, useBlockPiston, useSilkTouchProtection, UseCustomNames,
	UseJobsBrowse, PreventSlimeSplit, PreventMagmaCubeSplit, WaterBlockBreake;
    public int globalblocktimer, CowMilkingTimer,
	CoreProtectInterval, BlockPlaceInterval, InfoUpdateInterval;
    public Double payNearSpawnerMultiplier, VIPpayNearSpawnerMultiplier, TreeFellerMultiplier, gigaDrillMultiplier, superBreakerMultiplier, PetPay, VipPetPay;
    public String localeString = "EN";
    public boolean useBlockProtection;
    public boolean useBlockTimer;
    public boolean useMinimumOveralPayment;
    public boolean useMinimumOveralPoints;
    public boolean useBreederFinder = false;
    private boolean useTnTFinder = false;
    public boolean CancelCowMilking;
    public boolean fixAtMaxLevel, ToggleActionBar, TitleChangeChat, TitleChangeActionBar, LevelChangeChat,
	LevelChangeActionBar, SoundLevelupUse, SoundTitleChangeUse, UseServerAccount, EmptyServerAcountChat,
	EmptyServerAcountActionBar, ActionBarsMessageByDefault, ShowTotalWorkers, ShowPenaltyBonus, useDynamicPayment,
	useGlobalBoostScheduler, JobsGUIOpenOnBrowse, JobsGUIShowChatBrowse, JobsGUISwitcheButtons, JobsGUIOpenOnJoin;
    public Integer levelLossPercentage, SoundLevelupVolume, SoundLevelupPitch, SoundTitleChangeVolume,
	SoundTitleChangePitch, ToplistInScoreboardInterval;
    public double BoostExp;
    public double MinimumOveralPaymentLimit;
    public double MinimumOveralPointsLimit;
    public double BoostMoney;
    public double BoostPoints;
    public double DynamicPaymentMaxPenalty;
    public double DynamicPaymentMaxBonus;
    public double TaxesAmount;
    public String SoundLevelupSound, SoundTitleChangeSound, ServerAcountName, ServertaxesAcountName;
    public ArrayList<String> keys;
    public String storageMethod;
    public boolean hideJobsInfoWithoutPermission;
    public boolean UseTaxes;
    public boolean TransferToServerAccount;
    public boolean TakeFromPlayersPayment;

    //BossBar
    public boolean BossBarEnabled;
    public boolean BossBarShowOnEachAction;
    public int BossBarTimer;
    public boolean BossBarsMessageByDefault;

    public Parser DynamicPaymentEquation;
    public Parser maxMoneyEquation;
    public Parser maxExpEquation;
    public Parser maxPointEquation;

    public boolean DisabledWorldsUse;
    public List<String> DisabledWorldsList = new ArrayList<String>();

    public List<Schedule> BoostSchedule = new ArrayList<Schedule>();

    public HashMap<String, List<String>> commandArgs = new HashMap<String, List<String>>();

    public HashMap<String, List<String>> getCommandArgs() {
	return commandArgs;
    }

    public GeneralConfigManager(JobsPlugin plugin) {
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

    public synchronized Locale getLocale() {
	return locale;
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
	// Load locale
	Jobs.setLanguageManager(plugin);
	Jobs.getLanguageManager().load();
	// title settings
	Jobs.setTitleManager(plugin);
	Jobs.gettitleManager().load();
	// restricted areas
	Jobs.setRestrictedAreaManager(plugin);
	Jobs.getRestrictedAreaManager().load();
	// restricted blocks
	Jobs.setRestrictedBlockManager(plugin);
	Jobs.getRestrictedBlockManager().load();
	// Item/Block/mobs name list
	Jobs.setNameTranslatorManager(plugin);
	Jobs.getNameTranslatorManager().load();
	// signs information
	Jobs.setSignUtil(plugin);
	Jobs.getSignUtil().LoadSigns();
	// Schedule
	Jobs.setScheduleManager(plugin);
	Jobs.getScheduleManager().load();
	// Shop
	Jobs.setShopManager(plugin);
	Jobs.getShopManager().load();
    }

    /**
     * Method to load the general configuration
     * 
     * loads from Jobs/generalConfig.yml
     */
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

	c.getW().addComment("storage-method", "storage method, can be MySQL, sqlite");
	storageMethod = c.get("storage-method", "sqlite");
	if (storageMethod.equalsIgnoreCase("mysql")) {
	    startMysql();
	} else if (storageMethod.equalsIgnoreCase("sqlite")) {
	    startSqlite();
	} else {
	    Jobs.getPluginLogger().warning("Invalid storage method!  Changing method to sqlite!");
	    c.getC().set("storage-method", "sqlite");
	    Jobs.setDAO(JobsDAOSQLite.initialize());
	}

	c.getW().addComment("mysql-username", "Requires Mysql.");
	c.get("mysql-username", "root");
	c.get("mysql-password", "");
	c.get("mysql-hostname", "localhost:3306");
	c.get("mysql-database", "minecraft");
	c.get("mysql-table-prefix", "jobs_");

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

	c.getW().addComment("Optimizations.UseLocalOfflinePlayersData", "With this set to true, offline player data will be taken from local player data files",
	    "This will eliminate small lag spikes when request is being send to mojangs servers for offline players data",
	    "Theroticali this should work without issues, but if you havving some, just disable",
	    "But then you can feal some small (100-200ms) lag spikes while performings some jobs commands");
	LocalOfflinePlayersData = c.get("Optimizations.UseLocalOfflinePlayersData", true);

	c.getW().addComment("Optimizations.DisabledWorlds.Use", "By setting this to true, Jobs plugin will be disabled in given worlds",
	    "Only commands can be performed from disabled worlds with jobs.disabledworld.commands permission node");
	DisabledWorldsUse = c.get("Optimizations.DisabledWorlds.Use", false);
	DisabledWorldsList = c.getStringList("Optimizations.DisabledWorlds.List", Arrays.asList(Bukkit.getWorlds().get(0).getName()));

	c.getW().addComment("Logging.Use", "With this set to true all players jobs actions will be logged to database for easy to see statistics",
	    "This is still in development and in feature it will expand");
	LoggingUse = c.get("Logging.Use", false);

	c.getW().addComment("broadcast.on-skill-up.use", "Do all players get a message when somone goes up a skill level?");
	isBroadcastingSkillups = c.get("broadcast.on-skill-up.use", false);

	c.getW().addComment("broadcast.on-level-up.use", "Do all players get a message when somone goes up a level?");
	isBroadcastingLevelups = c.get("broadcast.on-level-up.use", false);
	c.getW().addComment("broadcast.on-level-up.levels", "For what levels you want to broadcast message? Keep it at 0 if you want for all of them");
	BroadcastingLevelUpLevels = c.getIntList("broadcast.on-level-up.levels", Arrays.asList(0));

	c.getW().addComment("max-jobs", "Maximum number of jobs a player can join.", "Use 0 for no maximum");
	maxJobs = c.get("max-jobs", 3);

	c.getW().addComment("hide-jobs-without-permission", "Hide jobs from player if they lack the permission to join the job");
	hideJobsWithoutPermission = c.get("hide-jobs-without-permission", false);

	c.getW().addComment("hide-jobsinfo-without-permission", "Hide jobs info from player if they lack the permission to join the job");
	hideJobsInfoWithoutPermission = c.get("hide-jobsinfo-without-permission", false);

	c.getW().addComment("enable-pay-near-spawner", "Option to allow payment to be made when killing mobs from a spawner");
	payNearSpawner = c.get("enable-pay-near-spawner", false);

	c.getW().addComment("pay-near-spawner-multiplier", "enable-pay-near-spawner should be enabled for this to work",
	    "0.5 means that players will get only 50% exp/money from monsters spawned from spawner");
	payNearSpawnerMultiplier = c.get("pay-near-spawner-multiplier", 1.0);

	c.getW().addComment("VIP-pay-near-spawner-multiplier", "VIP multiplier to pay for monsters from spawners, this will ignore global multiplier",
	    "Use jobs.vipspawner permission node for this to be enabled");
	VIPpayNearSpawnerMultiplier = c.get("VIP-pay-near-spawner-multiplier", 1.0);

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
	modifyChatSuffix = c.get("modify-chat-suffix", "&c]", true);
	modifyChatSeparator = c.get("modify-chat-separator", " ", true);

	c.getW().addComment("UseCustomNames", "Do you want to use custom item/block/mob/enchant/color names",
	    "With this set to true names like Stone:1 will be translated to Granite", "Name list is in TranslatableWords.yml file");
	UseCustomNames = c.get("UseCustomNames", true);

	c.getW().addComment("economy-batch-delay", "Changes how often, in seconds, players are paid out.  Default is 5 seconds.",
	    "Setting this too low may cause tick lag.  Increase this to improve economy performance (at the cost of delays in payment)");
	economyBatchDelay = c.get("economy-batch-delay", 5);

	c.getW().addComment("economy-async", "Enable async economy calls.", "Disable this if you have issues with payments or your plugin is not thread safe.");
	economyAsync = c.get("economy-async", true);

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
	c.getW().addComment("Economy.Limit.Money", "Money gain limit", "With this enabled, players will be limited how much they can make in defined time",
	    "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	MoneyLimitUse = c.get("Economy.Limit.Money.Use", false);
	c.getW().addComment("Economy.Limit.Money.StopWithExp", "Do you want to stop money gain when exp limit reached?");
	MoneyStopExp = c.get("Economy.Limit.Money.StopWithExp", false);
	c.getW().addComment("Economy.Limit.Money.StopWithPoint", "Do you want to stop money gain when point limit reached?");
	MoneyStopPoint = c.get("Economy.Limit.Money.StopWithPoint", false);

	c.getW().addComment("Economy.Limit.Money.MoneyLimit",
	    "Equation to calculate max limit. Option to use totallevel to include players total amount levels of current jobs",
	    "You can always use simple number to set money limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String MoneyLimit = c.get("Economy.Limit.Money.MoneyLimit", "500+500*(totallevel/100)");
	try {
	    maxMoneyEquation = new Parser(MoneyLimit);
	    maxMoneyEquation.setVariable("totallevel", 1);
	    maxMoneyEquation.getValue();
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("MoneyLimit has an invalid value. Disabling money limit!");
	    MoneyLimitUse = false;
	}

	c.getW().addComment("Economy.Limit.Money.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	MoneyTimeLimit = c.get("Economy.Limit.Money.TimeLimit", 3600);
	c.getW().addComment("Economy.Limit.Money.AnnouncmentDelay", "Delay between announcements about reached money limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	MoneyAnnouncmentDelay = c.get("Economy.Limit.Money.AnnouncmentDelay", 30);

	// Point limit
	c.getW().addComment("Economy.Limit.Point", "Point gain limit", "With this enabled, players will be limited how much they can make in defined time");
	PointLimitUse = c.get("Economy.Limit.Point.Use", false);
	c.getW().addComment("Economy.Limit.Point.StopWithExp", "Do you want to stop Point gain when exp limit reached?");
	PointStopExp = c.get("Economy.Limit.Point.StopWithExp", false);
	c.getW().addComment("Economy.Limit.Point.StopWithMoney", "Do you want to stop Point gain when money limit reached?");
	PointStopMoney = c.get("Economy.Limit.Point.StopWithMoney", false);

	c.getW().addComment("Economy.Limit.Point.Limit",
	    "Equation to calculate max limit. Option to use totallevel to include players total amount levels of current jobs",
	    "You can always use simple number to set limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String PointLimit = c.get("Economy.Limit.Point.Limit", "500+500*(totallevel/100)");
	try {
	    maxPointEquation = new Parser(PointLimit);
	    maxPointEquation.setVariable("totallevel", 1);
	    maxPointEquation.getValue();
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("PointLimit has an invalid value. Disabling money limit!");
	    PointLimitUse = false;
	}

	c.getW().addComment("Economy.Limit.Point.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	PointTimeLimit = c.get("Economy.Limit.Point.TimeLimit", 3600);
	c.getW().addComment("Economy.Limit.Point.AnnouncmentDelay", "Delay between announcements about reached limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	PointAnnouncmentDelay = c.get("Economy.Limit.Point.AnnouncmentDelay", 30);

	// Exp limit
	c.getW().addComment("Economy.Limit.Exp", "Exp gain limit", "With this enabled, players will be limited how much they can get in defined time",
	    "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	ExpLimitUse = c.get("Economy.Limit.Exp.Use", false);
	c.getW().addComment("Economy.Limit.Exp.StopWithMoney", "Do you want to stop exp gain when money limit reached?");
	ExpStopMoney = c.get("Economy.Limit.Exp.StopWithMoney", false);
	c.getW().addComment("Economy.Limit.Exp.StopWithPoint", "Do you want to stop exp gain when point limit reached?");
	ExpStopPoint = c.get("Economy.Limit.Exp.StopWithPoint", false);

	c.getW().addComment("Economy.Limit.Exp.Limit", "Equation to calculate max money limit. Option to use totallevel to include players total amount of current jobs",
	    "You can always use simple number to set exp limit",
	    "Default equation is: 5000+5000*(totallevel/100), this will add 1% from 5000 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 6850 limit");
	String expLimit = c.get("Economy.Limit.Exp.Limit", "5000+5000*(totallevel/100)");
	try {
	    maxExpEquation = new Parser(expLimit);
	    maxExpEquation.setVariable("totallevel", 1);
	    maxExpEquation.getValue();
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("ExpLimit has an invalid value. Disabling money limit!");
	    ExpLimitUse = false;
	}

	c.getW().addComment("Economy.Limit.Exp.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	ExpTimeLimit = c.get("Economy.Limit.Exp.TimeLimit", 3600);
	c.getW().addComment("Economy.Limit.Exp.AnnouncmentDelay", "Delay between announcements about reached Exp limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	ExpAnnouncmentDelay = c.get("Economy.Limit.Exp.AnnouncmentDelay", 30);

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

	c.getW().addComment("ExploitProtections.Coreprotect.Use",
	    "Requires to have CoreProtect plugin and there should be block place/break logging enabled in core protect config file.",
	    "This will prevent players from abusing by placing and breaking blocks again and again", "This will work even after server restart");
	useCoreProtect = c.get("ExploitProtections.Coreprotect.Use", false);
	c.getW().addComment("ExploitProtections.Coreprotect.TimeLimit", "Time limit in minutes to protect blocks from repeating place/breake action.",
	    "10080 equals to 7 days, keep it in reasonable time range");
	CoreProtectInterval = c.get("ExploitProtections.Coreprotect.TimeLimit", 604800);

	c.getW().addComment("ExploitProtections.Coreprotect.BlockPlace.Use", "Do you want to use block place interval protection");
	BlockPlaceUse = c.get("ExploitProtections.Coreprotect.BlockPlace.Use", true);
	EnableAnounceMessage = c.get("ExploitProtections.Coreprotect.BlockPlace.EnableAnounceMessage", true);
	c.getW().addComment("ExploitProtections.Coreprotect.BlockPlace.Interval", "Time interval in seconds in how fast you can place block in same place.",
	    "Keep it on low interval, 3-5 sec will be enough to prevent fast block place in same place and dont annoy peps",
	    "Edit block list in restrictedBlocks.yml under PlacedBlockTimer");
	BlockPlaceInterval = c.get("ExploitProtections.Coreprotect.BlockPlace.Interval", 2);

	c.getW().addComment("ExploitProtections.General.PlaceAndBreakProtection",
	    "Enable blocks protection, like ore, from exploiting by placing and destroying same block again and again.", "This works only until server restart",
	    "Modify restrictedBlocks.yml for blocks you want to protect");
	useBlockProtection = c.get("ExploitProtections.General.PlaceAndBreakProtection", true);

	c.getW().addComment("ExploitProtections.General.SilkTouchProtection", "Enable silk touch protection.",
	    "With this enabled players wont get paid for breaked blocks from restrictedblocks list with silk touch tool.");
	useSilkTouchProtection = c.get("ExploitProtections.General.SilkTouchProtection", false);

	c.getW().addComment("ExploitProtections.General.StopPistonBlockMove", "Enable piston moving blocks from restrictedblocks list.",
	    "If piston moves block then it will be like new block and BlockPlaceAndBreakProtection wont work properly",
	    "If you using core protect and its being logging piston block moving, then you can disable this");
	useBlockPiston = c.get("ExploitProtections.General.StopPistonBlockMove", true);

	c.getW().addComment("ExploitProtections.General.BlocksTimer", "Enable blocks timer protection.",
	    "Only enable if you want to protect block from beying broken to fast, useful for vegetables.", "Modify restrictedBlocks.yml for blocks you want to protect");
	useBlockTimer = c.get("ExploitProtections.General.BlocksTimer", true);

	c.getW().addComment("ExploitProtections.General.GlobalBlockTimer", "All blocks will be protected X sec after player places it on ground.");
	useGlobalTimer = c.get("ExploitProtections.General.GlobalBlockTimer.use", false);
	globalblocktimer = c.get("ExploitProtections.General.GlobalBlockTimer.timer", 30);

	c.getW().addComment("ExploitProtections.General.PetPay", "Do you want to pay when players pet kills monster/player", "Can be exploited with mob farms",
	    "0.2 means 20% of original reward", "Optionaly you can give jobs.petpay permission node for specific players/ranks to get paid by VipPetPay multiplier");
	PetPay = c.get("ExploitProtections.General.PetPay", 0.1);
	VipPetPay = c.get("ExploitProtections.General.VipPetPay", 1.0);

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

	c.getW().addComment("ExploitProtections.WaterBlockBreake",
	    "Prevent water braking placed blocks. Protection resets with server restart or after plants grows to next stage with bone powder or naturally",
	    "For strange reason works only 5 of 10 times, but this is completely enough to prevent exploiting");
	WaterBlockBreake = c.get("ExploitProtections.WaterBlockBreake", true);

	c.getW().addComment("use-breeder-finder", "Breeder finder.",
	    "If you are not using breeding payment, you can disable this to save little resources. Really little.");
	useBreederFinder = c.get("use-breeder-finder", true);

	c.getW().addComment("boost", "Money exp boost with special permision.",
	    "You will need to add special permision for groups or players to have money/exp/points boost.",
	    "Use: jobs.boost.[jobname].money or jobs.boost.[jobname].exp or jobs.boost.[jobname].points or jobs.boost.[jobname].all for all of them with specific jobs name.",
	    "Use: jobs.boost.all.money or jobs.boost.all.exp or jobs.boost.all.points or jobs.boost.all.all to get boost for all jobs",
	    "1.25 means that player will get 25% more than others, you can set less than 1 to get less from anothers");
	BoostExp = c.get("boost.exp", 1.00);
	BoostMoney = c.get("boost.money", 1.00);
	BoostPoints = c.get("boost.points", 1.00);

	c.getW().addComment("old-job", "Old job save", "Players can leave job and return later with some level loss during that",
	    "You can fix players level if hes job level is at max level");
	levelLossPercentage = c.get("old-job.level-loss-percentage", 30);
	fixAtMaxLevel = c.get("old-job.fix-at-max-level", true);

	c.getW().addComment("ActionBars.Messages.EnabledByDefault", "When this set to true player will see action bar messages by default");
	ActionBarsMessageByDefault = c.get("ActionBars.Messages.EnabledByDefault", true);

	c.getW().addComment("BossBar.Enabled", "Enables BossBar feature", "Works only from 1.9 mc version");
	BossBarEnabled = c.get("BossBar.Enabled", true);

	if (Jobs.getActionBar().getVersion() < 1900) {
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

	c.getW().addComment("JobsGUI.OpenOnBrowse", "Do you want to show GUI when performing /jobs browse command");
	JobsGUIOpenOnBrowse = c.get("JobsGUI.OpenOnBrowse", true);
	c.getW().addComment("JobsGUI.ShowChatBrowse", "Do you want to show chat information when performing /jobs browse command");
	JobsGUIShowChatBrowse = c.get("JobsGUI.ShowChatBrowse", true);
	c.getW().addComment("JobsGUI.SwitcheButtons", "With true left mouse button will join job and right will show more info",
	    "With false left mouse button will show more info, rigth will join job", "Dont forget to adjust locale file");
	JobsGUISwitcheButtons = c.get("JobsGUI.SwitcheButtons", false);
	c.getW().addComment("JobsBrowse.ShowPenaltyBonus", "Do you want to show GUI when performing /jobs join command");
	JobsGUIOpenOnJoin = c.get("JobsGUI.OpenOnJoin", true);

	c.getW().addComment("Schedule.Boost.Enable", "Do you want to enable scheduler for global boost");
	useGlobalBoostScheduler = c.get("Schedule.Boost.Enable", false);

	//		writer.addComment("Gui.UseJobsBrowse", "Do you want to use jobs browse gui instead of chat text");
	//		UseJobsBrowse = c.get("Gui.UseJobsBrowse", true);

	// Write back config
	try {
	    c.getW().save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public synchronized void startMysql() {
	File f = new File(plugin.getDataFolder(), "generalConfig.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	String legacyUrl = config.getString("mysql-url");
	if (legacyUrl != null) {
	    String jdbcString = "jdbc:mysql://";
	    if (legacyUrl.toLowerCase().startsWith(jdbcString)) {
		legacyUrl = legacyUrl.substring(jdbcString.length());
		String[] parts = legacyUrl.split("/");
		if (parts.length >= 2) {
		    config.set("mysql-hostname", parts[0]);
		    config.set("mysql-database", parts[1]);
		}
	    }
	}
	String username = config.getString("mysql-username");
	if (username == null) {
	    Jobs.getPluginLogger().severe("mysql-username property invalid or missing");
	}
	String password = config.getString("mysql-password");
	String hostname = config.getString("mysql-hostname");
	String database = config.getString("mysql-database");
	String prefix = config.getString("mysql-table-prefix");
	if (plugin.isEnabled())
	    Jobs.setDAO(JobsDAOMySQL.initialize(hostname, database, username, password, prefix));
    }

    public synchronized void startSqlite() {
	Jobs.setDAO(JobsDAOSQLite.initialize());
    }
}
