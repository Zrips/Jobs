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
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.container.NameList;
import com.gamingmesh.jobs.container.RestrictedArea;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.container.Title;
import com.gamingmesh.jobs.dao.JobsDAOMySQL;
import com.gamingmesh.jobs.dao.JobsDAOSQLite;
import com.gamingmesh.jobs.stuff.ChatColor;

public class JobsConfiguration {
    private JobsPlugin plugin;
    protected List<Title> titles = new ArrayList<Title>();
    protected ArrayList<RestrictedArea> restrictedAreas = new ArrayList<RestrictedArea>();
    public ArrayList<String> restrictedBlocks = new ArrayList<String>();
    public ArrayList<String> restrictedBlocksTimer = new ArrayList<String>();
    public ArrayList<Integer> restrictedPlaceBlocksTimer = new ArrayList<Integer>();
    public ArrayList<NameList> ListOfNames = new ArrayList<NameList>();
    public ArrayList<NameList> ListOfEntities = new ArrayList<NameList>();
    public ArrayList<NameList> ListOfEnchants = new ArrayList<NameList>();
    public ArrayList<NameList> ListOfColors = new ArrayList<NameList>();
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
    public boolean LoggingUse;
    public boolean EconomyLimitUse, EconomyExpLimitUse, PayForRenaming, PayForEachCraft, SignsEnabled,
	SignsColorizeJobName, ShowToplistInScoreboard, useGlobalTimer, useCoreProtect, BlockPlaceUse,
	EnableAnounceMessage, useBlockPiston, useSilkTouchProtection, UseCustomNames, EconomyMoneyStop,
	EconomyExpStop, UseJobsBrowse, PreventSlimeSplit, PreventMagmaCubeSplit, WaterBlockBreake;
    public int EconomyLimitTimeLimit, EconomyExpTimeLimit;
    public int EconomyLimitAnnouncmentDelay, EconomyLimitAnnouncmentExpDelay, globalblocktimer, CowMilkingTimer,
	CoreProtectInterval, BlockPlaceInterval, InfoUpdateInterval;
    public Double payNearSpawnerMultiplier, VIPpayNearSpawnerMultiplier, TreeFellerMultiplier, gigaDrillMultiplier, superBreakerMultiplier, PetPay, VipPetPay;
    public String localeString;
    public boolean useBlockProtection;
    public boolean useBlockTimer;
    public boolean useMinimumOveralPayment;
    public boolean useBreederFinder = false;
    private boolean useTnTFinder = false;
    public boolean CancelCowMilking;
    public boolean fixAtMaxLevel, ToggleActionBar, TitleChangeChat, TitleChangeActionBar, LevelChangeChat,
	LevelChangeActionBar, SoundLevelupUse, SoundTitleChangeUse, UseServerAccount, EmptyServerAcountChat,
	EmptyServerAcountActionBar, JobsToggleEnabled, ShowTotalWorkers, ShowPenaltyBonus, useDynamicPayment,
	useGlobalBoostScheduler, JobsGUIOpenOnBrowse, JobsGUIShowChatBrowse, JobsGUISwitcheButtons, JobsGUIOpenOnJoin;
    public Integer levelLossPercentage, SoundLevelupVolume, SoundLevelupPitch, SoundTitleChangeVolume,
	SoundTitleChangePitch, ToplistInScoreboardInterval;
    public double BoostExp;
    public double MinimumOveralPaymentLimit;
    public double BoostMoney;
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

    public Parser DynamicPaymentEquation;
    public Parser maxMoneyEquation;
    public Parser maxExpEquation;

    public List<Schedule> BoostSchedule = new ArrayList<Schedule>();

    public JobsConfiguration(JobsPlugin plugin) {
	super();
	this.plugin = plugin;
    }

    public String Colors(String text) {
	return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    public String GetConfigString(String path, String text, CommentedYamlConfiguration writer, YamlConfiguration conf, Boolean colorize) {
	conf.addDefault(path, text);
	text = conf.getString(path);
	if (colorize)
	    text = Colors(text);
	copySetting(conf, writer, path);
	return text;
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

    /**
     * Function to return the title for a given level
     * @return the correct title
     * @return null if no title matches
     */
    public Title getTitleForLevel(int level, String jobName) {
	Title title = null;
	for (Title t : titles) {
	    if (title == null) {
		if (t.getLevelReq() <= level) {
		    title = t;
		}
	    } else {
		if (t.getLevelReq() <= level && t.getLevelReq() > title.getLevelReq()) {
		    title = t;
		}
	    }
	}
	return title;
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

    /**
     * Gets the area multiplier for the player
     * @param player
     * @return - the multiplier
     */
    public synchronized double getRestrictedMultiplier(Player player) {
	for (RestrictedArea area : restrictedAreas) {
	    if (area.inRestrictedArea(player))
		return area.getMultiplier();
	}
	return 1.0;
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

    public synchronized void reload() {
	// general settings
	loadGeneralSettings();
	// Load locale
	loadLanguage();
	// title settings
	loadTitleSettings();
	// restricted areas
	loadRestrictedAreaSettings();
	// restricted blocks
	loadRestrictedBlocks();
	// Item/Block/mobs name list
	loadItemList();
	// signs information
	Jobs.getSignUtil().LoadSigns();

//		loadScheduler();
    }

    /**
     * Method to load the general configuration
     * 
     * loads from Jobs/generalConfig.yml
     */
    private synchronized void loadGeneralSettings() {
	File f = new File(plugin.getDataFolder(), "generalConfig.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

	CommentedYamlConfiguration writer = new CommentedYamlConfiguration();
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

	config.options().copyDefaults(true);

	writer.options().header(header.toString());

	writer.addComment("locale-language", "Default language.", "Example: en, ru", "File in locale folder with same name should exist. Example: messages_ru.yml");
	localeString = getString("locale-language", "en", config, writer);
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

	writer.addComment("storage-method", "storage method, can be MySQL, sqlite");
	storageMethod = getString("storage-method", "sqlite", config, writer);
	if (storageMethod.equalsIgnoreCase("mysql")) {
	    startMysql();
	    //		} else if (storageMethod.equalsIgnoreCase("h2")) {
	    //			File h2jar = new File(plugin.getDataFolder(), "h2.jar");
	    //			Jobs.getPluginLogger().warning("H2 database no longer supported!  Converting to SQLite.");
	    //			if (!h2jar.exists()) {
	    //				Jobs.getPluginLogger().info("H2 library not found, downloading...");
	    //				try {
	    //					FileDownloader.downloadFile(new URL("http://dev.bukkit.org/media/files/692/88/h2-1.3.171.jar"), h2jar);
	    //				} catch (MalformedURLException e) {
	    //					e.printStackTrace();
	    //				} catch (IOException e) {
	    //					Jobs.getPluginLogger().severe("Could not download database library!");
	    //				}
	    //			}
	    //			if (plugin.isEnabled()) {
	    //				try {
	    //					Jobs.getJobsClassloader().addFile(h2jar);
	    //				} catch (IOException e) {
	    //					Jobs.getPluginLogger().severe("Could not load database library!");
	    //				}
	    //				if (plugin.isEnabled()) {
	    //					try {
	    //						JobsDAOH2.convertToSQLite();
	    //						Jobs.setDAO(JobsDAOSQLite.initialize());
	    //						config.set("storage-method", "sqlite");
	    //					} catch (SQLException e) {
	    //						Jobs.getPluginLogger().severe("Error when converting from H2 to SQLite!");
	    //						e.printStackTrace();
	    //					}
	    //				}
	    //			}
	} else if (storageMethod.equalsIgnoreCase("sqlite")) {
	    startSqlite();
	} else {
	    Jobs.getPluginLogger().warning("Invalid storage method!  Changing method to sqlite!");
	    config.set("storage-method", "sqlite");
	    Jobs.setDAO(JobsDAOSQLite.initialize());
	}

	writer.addComment("mysql-username", "Requires Mysql.");
	getString("mysql-username", "root", config, writer);
	getString("mysql-password", "", config, writer);
	getString("mysql-hostname", "localhost:3306", config, writer);
	getString("mysql-database", "minecraft", config, writer);
	getString("mysql-table-prefix", "jobs_", config, writer);

	writer.addComment("save-period", "How often in minutes you want it to save.  This must be a non-zero number");
	getInt("save-period", 10, config, writer);
	if (config.getInt("save-period") <= 0) {
	    Jobs.getPluginLogger().severe("Save period must be greater than 0!  Defaulting to 10 minutes!");
	    config.set("save-period", 10);
	}
	savePeriod = config.getInt("save-period");

	writer.addComment("save-on-disconnect", "Should player data be saved on disconnect?",
	    "Player data is always periodically auto-saved and autosaved during a clean shutdown.",
	    "Only enable this if you have a multi-server setup, or have a really good reason for enabling this.", "Turning this on will decrease database performance.");
	saveOnDisconnect = getBoolean("save-on-disconnect", false, config, writer);

	writer.addComment("Optimizations.UseLocalOfflinePlayersData", "With this set to true, offline player data will be taken from local player data files",
	    "This will eliminate small lag spikes when request is being send to mojangs servers for offline players data",
	    "Theroticali this should work without issues, but if you havving some, just disable",
	    "But then you can feal some small (100-200ms) lag spikes while performings some jobs commands");
	LocalOfflinePlayersData = getBoolean("Optimizations.UseLocalOfflinePlayersData", true, config, writer);

	writer.addComment("Logging.Use", "With this set to true all players jobs actions will be logged to database for easy to see statistics",
	    "This is still in development and in feature it will expand");
	LoggingUse = getBoolean("Logging.Use", false, config, writer);

	writer.addComment("broadcast.on-skill-up.use", "Do all players get a message when somone goes up a skill level?");
	isBroadcastingSkillups = getBoolean("broadcast.on-skill-up.use", false, config, writer);

	writer.addComment("broadcast.on-level-up.use", "Do all players get a message when somone goes up a level?");
	isBroadcastingLevelups = getBoolean("broadcast.on-level-up.use", false, config, writer);
	writer.addComment("broadcast.on-level-up.levels", "For what levels you want to broadcast message? Keep it at 0 if you want for all of them");
	BroadcastingLevelUpLevels = getIntArray("broadcast.on-level-up.levels", Arrays.asList(0), config, writer);

	writer.addComment("max-jobs", "Maximum number of jobs a player can join.", "Use 0 for no maximum");
	maxJobs = getInt("max-jobs", 3, config, writer);

	writer.addComment("hide-jobs-without-permission", "Hide jobs from player if they lack the permission to join the job");
	hideJobsWithoutPermission = getBoolean("hide-jobs-without-permission", false, config, writer);

	writer.addComment("hide-jobsinfo-without-permission", "Hide jobs info from player if they lack the permission to join the job");
	hideJobsInfoWithoutPermission = getBoolean("hide-jobsinfo-without-permission", false, config, writer);

	writer.addComment("enable-pay-near-spawner", "Option to allow payment to be made when killing mobs from a spawner");
	payNearSpawner = getBoolean("enable-pay-near-spawner", false, config, writer);

	writer.addComment("pay-near-spawner-multiplier", "enable-pay-near-spawner should be enabled for this to work",
	    "0.5 means that players will get only 50% exp/money from monsters spawned from spawner");
	payNearSpawnerMultiplier = getDouble("pay-near-spawner-multiplier", 1.0, config, writer);

	writer.addComment("VIP-pay-near-spawner-multiplier", "VIP multiplier to pay for monsters from spawners, this will ignore global multiplier",
	    "Use jobs.vipspawner permission node for this to be enabled");
	VIPpayNearSpawnerMultiplier = getDouble("VIP-pay-near-spawner-multiplier", 1.0, config, writer);

	writer.addComment("enable-pay-creative", "Option to allow payment to be made in creative mode");
	payInCreative = getBoolean("enable-pay-creative", false, config, writer);

	writer.addComment("enable-pay-for-exploring-when-flying", "Option to allow payment to be made for exploring when player flyies");
	payExploringWhenFlying = getBoolean("enable-pay-for-exploring-when-flying", false, config, writer);

	writer.addComment("add-xp-player", "Adds the Jobs xp recieved to the player's Minecraft XP bar");
	addXpPlayer = getBoolean("add-xp-player", false, config, writer);

	writer.addComment("modify-chat",
	    "Modifys chat to add chat titles.  If you're using a chat manager, you may add the tag {jobs} to your chat format and disable this.");
	modifyChat = getBoolean("modify-chat", true, config, writer);

	modifyChatPrefix = getString("modify-chat-prefix", "&c[", config, writer, true);
	modifyChatSuffix = getString("modify-chat-suffix", "&c]", config, writer, true);
	modifyChatSeparator = getString("modify-chat-seperator", " ", config, writer, true);

	writer.addComment("UseCustomNames", "Do you want to use custom item/block/mob/enchant/color names",
	    "With this set to true names like Stone:1 will be translated to Granite", "Name list is in ItemList.yml file");
	UseCustomNames = getBoolean("UseCustomNames", true, config, writer);

	writer.addComment("economy-batch-delay", "Changes how often, in seconds, players are paid out.  Default is 5 seconds.",
	    "Setting this too low may cause tick lag.  Increase this to improve economy performance (at the cost of delays in payment)");
	economyBatchDelay = getInt("economy-batch-delay", 5, config, writer);

	writer.addComment("economy-async", "Enable async economy calls.", "Disable this if you have issues with payments or your plugin is not thread safe.");
	economyAsync = getBoolean("economy-async", true, config, writer);

	writer.addComment("Economy.MinimumOveralPayment.use",
	    "Determines minimum payment. In example if player uses McMMO treefeller and earns only 20%, but at same time he gets 25% penalty from dynamic payment. He can 'get' negative amount of money",
	    "This will limit it to particular percentage", "Works only when original payment is above 0");
	useMinimumOveralPayment = getBoolean("Economy.MinimumOveralPayment.use", true, config, writer);
	MinimumOveralPaymentLimit = getDouble("Economy.MinimumOveralPayment.limit", 0.1, config, writer);

	writer.addComment("Economy.DynamicPayment.use", "Do you want to use dinamic payment dependent on how many players already working for jobs",
	    "This can help automaticaly lift up payments for not so popular jobs and lower for most popular ones");
	useDynamicPayment = getBoolean("Economy.DynamicPayment.use", false, config, writer);

	String maxExpEquationInput = getString("Economy.DynamicPayment.equation", "((totalworkers / totaljobs) - jobstotalplayers)/10.0", config, writer);
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

	DynamicPaymentMaxPenalty = getDouble("Economy.DynamicPayment.MaxPenalty", 25.0, config, writer);
	DynamicPaymentMaxBonus = getDouble("Economy.DynamicPayment.MaxBonus", 100.0, config, writer);

	writer.addComment("Economy.EnabledJobsToglle", "Do you want to enable jobs toggle by default");
	JobsToggleEnabled = getBoolean("Economy.EnabledJobsToglle", true, config, writer);

	writer.addComment("Economy.UseServerAcount", "Server economy acount", "With this enabled, players will get money from defined user (server account)",
	    "If this acount dont have enough money to pay for players for, player will get message");
	UseServerAccount = getBoolean("Economy.UseServerAcount", false, config, writer);
	writer.addComment("Economy.AcountName", "Username should be with Correct capitalization");
	ServerAcountName = getString("Economy.AcountName", "Server", config, writer);
	writer.addComment("Economy.Taxes.use", "Do you want to use taxes feature for jobs payment");
	UseTaxes = getBoolean("Economy.Taxes.use", false, config, writer);
	writer.addComment("Economy.Taxes.AccountName", "Username should be with Correct capitalization, it can be same as settup in server account before");
	ServertaxesAcountName = getString("Economy.Taxes.AccountName", "Server", config, writer);
	writer.addComment("Economy.Taxes.Amount", "Amount in percentage");
	TaxesAmount = getDouble("Economy.Taxes.Amount", 15.0, config, writer);
	writer.addComment("Economy.Taxes.TransferToServerAccount", "Do you want to transfer taxes to server account");
	TransferToServerAccount = getBoolean("Economy.Taxes.TransferToServerAccount", true, config, writer);
	writer.addComment("Economy.Taxes.TakeFromPlayersPayment",
	    "With this true, taxes will be taken from players payment and he will get less money than its shown in jobs info",
	    "When its false player will get full payment and server account will get taxes amount to hes account");
	TakeFromPlayersPayment = getBoolean("Economy.Taxes.TakeFromPlayersPayment", false, config, writer);

	writer.addComment("Economy.Limit.Money", "Money gain limit", "With this enabled, players will be limited how much they can make in defined time",
	    "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	EconomyLimitUse = getBoolean("Economy.Limit.Money.Use", false, config, writer);
	writer.addComment("Economy.Limit.Money.StopWithExp", "Do you want to stop money gain when exp limit reached?");
	EconomyMoneyStop = getBoolean("Economy.Limit.Money.StopWithExp", false, config, writer);

	writer.addComment("Economy.Limit.Money.MoneyLimit",
	    "Equation to calculate max money limit. Option to use totallevel to include players total amount levels of current jobs",
	    "You can always use simple number to set money limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String MoneyLimit = getString("Economy.Limit.Money.MoneyLimit", "500+500*(totallevel/100)", config, writer);
	try {
	    maxMoneyEquation = new Parser(MoneyLimit);
	    maxMoneyEquation.setVariable("totallevel", 1);
	    maxMoneyEquation.getValue();
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("MoneyLimit has an invalid value. Disabling money limit!");
	    EconomyLimitUse = false;
	}

	writer.addComment("Economy.Limit.Money.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	EconomyLimitTimeLimit = getInt("Economy.Limit.Money.TimeLimit", 3600, config, writer);
	writer.addComment("Economy.Limit.Money.AnnouncmentDelay", "Delay between announcements about reached money limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	EconomyLimitAnnouncmentDelay = getInt("Economy.Limit.Money.AnnouncmentDelay", 30, config, writer);

	writer.addComment("Economy.Limit.Exp", "Exp gain limit", "With this enabled, players will be limited how much they can get in defined time",
	    "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	EconomyExpLimitUse = getBoolean("Economy.Limit.Exp.Use", false, config, writer);
	writer.addComment("Economy.Limit.Exp.StopWithMoney", "Do you want to stop exp gain when money limit reached?");
	EconomyExpStop = getBoolean("Economy.Limit.Exp.StopWithMoney", false, config, writer);

	writer.addComment("Economy.Limit.Exp.Limit", "Equation to calculate max money limit. Option to use totallevel to include players total amount of current jobs",
	    "You can always use simple number to set exp limit",
	    "Default equation is: 5000+5000*(totallevel/100), this will add 1% from 5000 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 6850 limit");
	String expLimit = getString("Economy.Limit.Exp.Limit", "5000+5000*(totallevel/100)", config, writer);
	try {
	    maxExpEquation = new Parser(expLimit);
	    maxExpEquation.setVariable("totallevel", 1);
	    maxExpEquation.getValue();
	} catch (Exception e) {
	    Jobs.getPluginLogger().warning("ExpLimit has an invalid value. Disabling money limit!");
	    EconomyExpLimitUse = false;
	}

	writer.addComment("Economy.Limit.Exp.TimeLimit", "Time in seconds: 60 = 1min, 3600 = 1 hour, 86400 = 24 hours");
	EconomyExpTimeLimit = getInt("Economy.Limit.Exp.TimeLimit", 3600, config, writer);
	writer.addComment("Economy.Limit.Exp.AnnouncmentDelay", "Delay between announcements about reached Exp limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	EconomyLimitAnnouncmentExpDelay = getInt("Economy.Limit.Exp.AnnouncmentDelay", 30, config, writer);

	writer.addComment("Economy.Repair.PayForRenaming", "Do you want to give money for only renaming items in anvil",
	    "Players will get full pay as they would for remairing two items when they only renaming one",
	    "This is not big issue, but if you want to disable it, you can");
	PayForRenaming = getBoolean("Economy.Repair.PayForRenaming", true, config, writer);

	writer.addComment("Economy.Crafting.PayForEachCraft",
	    "With this true, player will get money for all crafted items instead of each crafting action (like with old payment mechanic)",
	    "By default its false, as you can make ALOT of money if prices kept from old payment mechanics");
	PayForEachCraft = getBoolean("Economy.Crafting.PayForEachCraft", false, config, writer);

	writer.addComment("Economy.MilkingCow.CancelMilking", "With this true, when timer is still going, cow milking event will be canceled",
	    "With this false, player will get bucket of milk, but still no payment");
	CancelCowMilking = getBoolean("Economy.MilkingCow.CancelMilking", false, config, writer);
	writer.addComment("Economy.MilkingCow.Timer",
	    "How ofter player can milk cows in seconds. Keep in mind that by default player can milk cow indefinetly and as often as he wants",
	    "Set to 0 if you want to disable timer");
	CowMilkingTimer = getInt("Economy.MilkingCow.Timer", 30, config, writer) * 1000;

	writer.addComment("ExploitProtections.Coreprotect.Use",
	    "Requires to have CoreProtect plugin and there should be block place/break logging enabled in core protect config file.",
	    "This will prevent players from abusing by placing and breaking blocks again and again", "This will work even after server restart");
	useCoreProtect = getBoolean("ExploitProtections.Coreprotect.Use", false, config, writer);
	writer.addComment("ExploitProtections.Coreprotect.TimeLimit", "Time limit in minutes to protect blocks from repeating place/breake action.",
	    "10080 equals to 7 days, keep it in reasonable time range");
	CoreProtectInterval = getInt("ExploitProtections.Coreprotect.TimeLimit", 604800, config, writer);

	writer.addComment("ExploitProtections.Coreprotect.BlockPlace.Use", "Do you want to use block place interval protection");
	BlockPlaceUse = getBoolean("ExploitProtections.Coreprotect.BlockPlace.Use", true, config, writer);
	EnableAnounceMessage = getBoolean("ExploitProtections.Coreprotect.BlockPlace.EnableAnounceMessage", true, config, writer);
	writer.addComment("ExploitProtections.Coreprotect.BlockPlace.Interval", "Time interval in seconds in how fast you can place block in same place.",
	    "Keep it on low interval, 3-5 sec will be enough to prevent fast block place in same place and dont annoy peps",
	    "Edit block list in restrictedBlocks.yml under PlacedBlockTimer");
	BlockPlaceInterval = getInt("ExploitProtections.Coreprotect.BlockPlace.Interval", 2, config, writer);

	writer.addComment("ExploitProtections.General.PlaceAndBreakProtection",
	    "Enable blocks protection, like ore, from exploiting by placing and destroying same block again and again.", "This works only until server restart",
	    "Modify restrictedBlocks.yml for blocks you want to protect");
	useBlockProtection = getBoolean("ExploitProtections.General.PlaceAndBreakProtection", true, config, writer);

	writer.addComment("ExploitProtections.General.SilkTouchProtection", "Enable silk touch protection.",
	    "With this enabled players wont get paid for breaked blocks from restrictedblocks list with silk touch tool.");
	useSilkTouchProtection = getBoolean("ExploitProtections.General.SilkTouchProtection", false, config, writer);

	writer.addComment("ExploitProtections.General.StopPistonBlockMove", "Enable piston moving blocks from restrictedblocks list.",
	    "If piston moves block then it will be like new block and BlockPlaceAndBreakProtection wont work properly",
	    "If you using core protect and its being logging piston block moving, then you can disable this");
	useBlockPiston = getBoolean("ExploitProtections.General.StopPistonBlockMove", true, config, writer);

	writer.addComment("ExploitProtections.General.BlocksTimer", "Enable blocks timer protection.",
	    "Only enable if you want to protect block from beying broken to fast, useful for vegetables.", "Modify restrictedBlocks.yml for blocks you want to protect");
	useBlockTimer = getBoolean("ExploitProtections.General.BlocksTimer", true, config, writer);

	writer.addComment("ExploitProtections.General.GlobalBlockTimer", "All blocks will be protected X sec after player places it on ground.");
	useGlobalTimer = getBoolean("ExploitProtections.General.GlobalBlockTimer.use", false, config, writer);
	globalblocktimer = getInt("ExploitProtections.General.GlobalBlockTimer.timer", 30, config, writer);

	writer.addComment("ExploitProtections.General.PetPay", "Do you want to pay when players pet kills monster/player", "Can be exploited with mob farms",
	    "0.2 means 20% of original reward", "Optionaly you can give jobs.petpay permission node for specific players/ranks to get paid by VipPetPay multiplier");
	PetPay = getDouble("ExploitProtections.General.PetPay", 0.1, config, writer);
	VipPetPay = getDouble("ExploitProtections.General.VipPetPay", 1.0, config, writer);

	writer.addComment("ExploitProtections.McMMO", "McMMO abilities");
	writer.addComment("ExploitProtections.McMMO.TreeFellerMultiplier", "Players will get part of money from cutting trees with treefeller ability enabled.",
	    "0.2 means 20% of original price");
	TreeFellerMultiplier = getDouble("ExploitProtections.McMMO.TreeFellerMultiplier", 0.2, config, writer);
	writer.addComment("ExploitProtections.McMMO.gigaDrillMultiplier", "Players will get part of money from braking blocks with gigaDrill ability enabled.",
	    "0.2 means 20% of original price");
	gigaDrillMultiplier = getDouble("ExploitProtections.McMMO.gigaDrillMultiplier", 0.2, config, writer);
	writer.addComment("ExploitProtections.McMMO.superBreakerMultiplier", "Players will get part of money from braking blocks with super breaker ability enabled.",
	    "0.2 means 20% of original price");
	superBreakerMultiplier = getDouble("ExploitProtections.McMMO.superBreakerMultiplier", 0.2, config, writer);

	writer.addComment("ExploitProtections.Spawner.PreventSlimeSplit", "Prevent slime spliting when they are from spawner",
	    "Protects agains exploiting as new splited slimes is treated as naturaly spawned and not from spawner");
	PreventSlimeSplit = getBoolean("ExploitProtections.Spawner.PreventSlimeSplit", true, config, writer);
	writer.addComment("ExploitProtections.Spawner.PreventMagmaCubeSplit", "Prevent magmacube spliting when they are from spawner");
	PreventMagmaCubeSplit = getBoolean("ExploitProtections.Spawner.PreventMagmaCubeSplit", true, config, writer);

	writer.addComment("ExploitProtections.WaterBlockBreake",
	    "Prevent water braking placed blocks. Protection resets with server restart or after plants grows to next stage with bone powder or naturally",
	    "For strange reason works only 5 of 10 times, but this is completely enough to prevent exploiting");
	WaterBlockBreake = getBoolean("ExploitProtections.WaterBlockBreake", true, config, writer);

	writer.addComment("use-breeder-finder", "Breeder finder.",
	    "If you are not using breeding payment, you can disable this to save little resources. Really little.");
	useBreederFinder = getBoolean("use-breeder-finder", true, config, writer);

	writer.addComment("boost", "Money exp boost with special permision.", "You will need to add special permision for groups or players to have money/exp boost.",
	    "Use: jobs.boost.[jobname].money or jobs.boost.[jobname].exp or jobs.boost.[jobname].both for both of them with specific jobs name.",
	    "Use: jobs.boost.all.money or jobs.boost.all.exp or jobs.boost.all.both to get boost for all jobs",
	    "1.25 means that player will get 25% more than others, you can set less than 1 to get less from anothers");
	BoostExp = getDouble("boost.exp", 1.25, config, writer);
	BoostMoney = getDouble("boost.money", 1.25, config, writer);

	writer.addComment("old-job", "Old job save", "Players can leave job and return later with some level loss during that",
	    "You can fix players level if hes job level is at max level");
	levelLossPercentage = getInt("old-job.level-loss-percentage", 30, config, writer);
	fixAtMaxLevel = getBoolean("old-job.fix-at-max-level", true, config, writer);

	writer.addComment("ActionBars", "Action bars", "You can enable/disable togglebale message by player with /jobs toggle");
	ToggleActionBar = getBoolean("ActionBars.Toggle", true, config, writer);

	writer.addComment("ShowActionBars", "You can enable/disable message shown for players in action bar");
	TitleChangeActionBar = getBoolean("ShowActionBars.OnTitleChange", true, config, writer);
	LevelChangeActionBar = getBoolean("ShowActionBars.OnLevelChange", true, config, writer);
	EmptyServerAcountActionBar = getBoolean("ShowActionBars.OnEmptyServerAcount", true, config, writer);

	writer.addComment("ShowChatMessage", "Chat messages", "You can enable/disable message shown for players in chat");
	TitleChangeChat = getBoolean("ShowChatMessage.OnTitleChange", true, config, writer);
	LevelChangeChat = getBoolean("ShowChatMessage.OnLevelChange", true, config, writer);
	EmptyServerAcountChat = getBoolean("ShowChatMessage.OnEmptyServerAcount", true, config, writer);

	writer.addComment("Sounds", "Sounds", "Extra sounds on some events", "All sounds can be found in https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
	SoundLevelupUse = getBoolean("Sounds.LevelUp.use", true, config, writer);
	SoundLevelupSound = getString("Sounds.LevelUp.sound", "LEVEL_UP", config, writer);
	SoundLevelupVolume = getInt("Sounds.LevelUp.volume", 1, config, writer);
	SoundLevelupPitch = getInt("Sounds.LevelUp.pitch", 3, config, writer);
	SoundTitleChangeUse = getBoolean("Sounds.TitleChange.use", true, config, writer);
	SoundTitleChangeSound = getString("Sounds.TitleChange.sound", "LEVEL_UP", config, writer);
	SoundTitleChangeVolume = getInt("Sounds.TitleChange.volume", 1, config, writer);
	SoundTitleChangePitch = getInt("Sounds.TitleChange.pitch", 3, config, writer);

	writer.addComment("Signs", "You can disable this to save SMALL amount of server resources");
	SignsEnabled = getBoolean("Signs.Enable", true, config, writer);
	SignsColorizeJobName = getBoolean("Signs.Colors.ColorizeJobName", true, config, writer);
	writer.addComment("Signs.InfoUpdateInterval",
	    "This is interval in sec in which signs will be updated. This is not continues update, signs are updated only on levelup, job leave, job join or similar action.");
	writer.addComment("Signs.InfoUpdateInterval",
	    "This is update for same job signs, to avoid huge lag if you have bunch of same type signs. Keep it from 1 to as many sec you want");
	InfoUpdateInterval = getInt("Signs.InfoUpdateInterval", 5, config, writer);

	writer.addComment("Scoreboard.ShowToplist", "This will enables to show top list in scoreboard instead of chat");
	ShowToplistInScoreboard = getBoolean("Scoreboard.ShowToplist", true, config, writer);
	writer.addComment("Scoreboard.interval", "For how long to show scoreboard");
	ToplistInScoreboardInterval = getInt("Scoreboard.interval", 10, config, writer);

	writer.addComment("JobsBrowse.ShowTotalWorkers", "Do you want to show total amount of workers for job in jobs browse window");
	ShowTotalWorkers = getBoolean("JobsBrowse.ShowTotalWorkers", true, config, writer);
	writer.addComment("JobsBrowse.ShowPenaltyBonus", "Do you want to show penalty and bonus in jobs browse window. Only works if this feature is enabled");
	ShowPenaltyBonus = getBoolean("JobsBrowse.ShowPenaltyBonus", true, config, writer);

	writer.addComment("JobsGUI.OpenOnBrowse", "Do you want to show GUI when performing /jobs browse command");
	JobsGUIOpenOnBrowse = getBoolean("JobsGUI.OpenOnBrowse", true, config, writer);
	writer.addComment("JobsGUI.ShowChatBrowse", "Do you want to show chat information when performing /jobs browse command");
	JobsGUIShowChatBrowse = getBoolean("JobsGUI.ShowChatBrowse", true, config, writer);
	writer.addComment("JobsGUI.SwitcheButtons", "With true left mouse button will join job and right will show more info",
	    "With false left mouse button will show more info, rigth will join job", "Dont forget to adjust locale file");
	JobsGUISwitcheButtons = getBoolean("JobsGUI.SwitcheButtons", false, config, writer);
	writer.addComment("JobsBrowse.ShowPenaltyBonus", "Do you want to show GUI when performing /jobs join command");
	JobsGUIOpenOnJoin = getBoolean("JobsGUI.OpenOnJoin", true, config, writer);

	writer.addComment("Schedule.Boost.Enable", "Do you want to enable scheduler for global boost");
	useGlobalBoostScheduler = getBoolean("Schedule.Boost.Enable", false, config, writer);

	//		writer.addComment("Gui.UseJobsBrowse", "Do you want to use jobs browse gui instead of chat text");
	//		UseJobsBrowse = getBoolean("Gui.UseJobsBrowse", true, config, writer);

	// Write back config
	try {
	    writer.save(f);
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

    public synchronized void copySetting(Configuration reader, Configuration writer, String path) {
	writer.set(path, reader.get(path));
    }

    /**
     * Method to load the title configuration
     * 
     * loads from Jobs/titleConfig.yml
     */
    private synchronized void loadTitleSettings() {
	this.titles.clear();
	File f = new File(plugin.getDataFolder(), "titleConfig.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	StringBuilder header = new StringBuilder().append("Title configuration").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"))
	    .append("Stores the titles people gain at certain levels.").append(System.getProperty("line.separator")).append(
		"Each title requres to have a name, short name (used when the player has more than").append(System.getProperty("line.separator")).append(
		    "1 job) the colour of the title and the level requrirement to attain the title.").append(System.getProperty("line.separator")).append(System
			.getProperty("line.separator")).append("It is recommended but not required to have a title at level 0.").append(System.getProperty(
			    "line.separator")).append(System.getProperty("line.separator")).append("Titles are completely optional.").append(System.getProperty(
				"line.separator")).append("Posible variable are {level} to add current jobs level.").append(System.getProperty("line.separator")).append(
				    System.getProperty("line.separator")).append("Titles:").append(System.getProperty("line.separator")).append("  Apprentice:").append(
					System.getProperty("line.separator")).append("    Name: Apprentice").append(System.getProperty("line.separator")).append(
					    "    ShortName: A").append(System.getProperty("line.separator")).append("    ChatColour: WHITE").append(System.getProperty(
						"line.separator")).append("    levelReq: 0").append(System.getProperty("line.separator")).append("  Novice:").append(
						    System.getProperty("line.separator")).append("    Name: Novice").append(System.getProperty("line.separator")).append(
							"    ShortName: N").append(System.getProperty("line.separator")).append("    ChatColour: GRAY").append(System
							    .getProperty("line.separator")).append("    levelReq: 30").append(System.getProperty("line.separator"))
	    .append("  Journeyman:").append(System.getProperty("line.separator")).append("    Name: Journeyman").append(System.getProperty("line.separator")).append(
		"    ShortName: J").append(System.getProperty("line.separator")).append("    ChatColour: GOLD").append(System.getProperty("line.separator")).append(
		    "    levelReq: 60").append(System.getProperty("line.separator")).append("  Master:").append(System.getProperty("line.separator")).append(
			"    Name: Master").append(System.getProperty("line.separator")).append("    ShortName: '{level} M'").append(System.getProperty("line.separator"))
	    .append("    ChatColour: BLACK").append(System.getProperty("line.separator")).append("    levelReq: 90").append(System.getProperty("line.separator")).append(
		System.getProperty("line.separator"));
	conf.options().header(header.toString());
	conf.options().copyDefaults(true);
	conf.options().indent(2);

	ConfigurationSection titleSection = conf.getConfigurationSection("Titles");
	if (titleSection == null) {
	    titleSection = conf.createSection("Titles");
	}
	for (String titleKey : titleSection.getKeys(false)) {
	    String jobName = null;
	    String titleName = titleSection.getString(titleKey + ".Name");
	    String titleShortName = titleSection.getString(titleKey + ".ShortName");
	    ChatColor titleColor = ChatColor.matchColor(titleSection.getString(titleKey + ".ChatColour", ""));
	    int levelReq = titleSection.getInt(titleKey + ".levelReq", -1);

	    if (titleSection.isString(titleKey + ".JobName")) {
		jobName = titleSection.getString(titleKey + ".JobName");
	    }

	    if (titleName == null) {
		Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid Name property. Skipping!");
		continue;
	    }

	    if (titleShortName == null) {
		Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid ShortName property. Skipping!");
		continue;
	    }
	    if (titleColor == null) {
		Jobs.getPluginLogger().severe("Title " + titleKey + "has an invalid ChatColour property. Skipping!");
		continue;
	    }
	    if (levelReq <= -1) {
		Jobs.getPluginLogger().severe("Title " + titleKey + " has an invalid levelReq property. Skipping!");
		continue;
	    }

	    this.titles.add(new Title(titleName, titleShortName, titleColor, levelReq, jobName));
	}

	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Method to load the restricted areas configuration
     * 
     * loads from Jobs/restrictedAreas.yml
     */
    private synchronized void loadRestrictedAreaSettings() {
	this.restrictedAreas.clear();
	File f = new File(plugin.getDataFolder(), "restrictedAreas.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
	conf.options().indent(2);
	conf.options().copyDefaults(true);
	StringBuilder header = new StringBuilder();

	header.append("Restricted area configuration");
	header.append(System.getProperty("line.separator")).append(System.getProperty("line.separator")).append(
	    "Configures restricted areas where you cannot get experience or money").append(System.getProperty("line.separator")).append("when performing a job.").append(
		System.getProperty("line.separator")).append(System.getProperty("line.separator")).append("The multiplier changes the experience/money gains in an area.")
	    .append(System.getProperty("line.separator")).append("A multiplier of 0.0 means no money or xp, while 0.5 means you will get half the normal money/exp")
	    .append(System.getProperty("line.separator")).append(System.getProperty("line.separator")).append("restrictedareas:").append(System.getProperty(
		"line.separator")).append("  area1:").append(System.getProperty("line.separator")).append("    world: 'world'").append(System.getProperty(
		    "line.separator")).append("    multiplier: 0.0").append(System.getProperty("line.separator")).append("    point1:").append(System.getProperty(
			"line.separator")).append("      x: 125").append(System.getProperty("line.separator")).append("      y: 0").append(System.getProperty(
			    "line.separator")).append("      z: 125").append(System.getProperty("line.separator")).append("    point2:").append(System.getProperty(
				"line.separator")).append("      x: 150").append(System.getProperty("line.separator")).append("      y: 100").append(System.getProperty(
				    "line.separator")).append("      z: 150").append(System.getProperty("line.separator")).append("  area2:").append(System.getProperty(
					"line.separator")).append("    world: 'world_nether'").append(System.getProperty("line.separator")).append("    multiplier: 0.0")
	    .append(System.getProperty("line.separator")).append("    point1:").append(System.getProperty("line.separator")).append("      x: -100").append(System
		.getProperty("line.separator")).append("      y: 0").append(System.getProperty("line.separator")).append("      z: -100").append(System.getProperty(
		    "line.separator")).append("    point2:").append(System.getProperty("line.separator")).append("      x: -150").append(System.getProperty(
			"line.separator")).append("      y: 100").append(System.getProperty("line.separator")).append("      z: -150");
	conf.options().header(header.toString());
	ConfigurationSection areaSection = conf.getConfigurationSection("restrictedareas");
	if (areaSection != null) {
	    for (String areaKey : areaSection.getKeys(false)) {
		String worldName = conf.getString("restrictedareas." + areaKey + ".world");
		double multiplier = conf.getDouble("restrictedareas." + areaKey + ".multiplier", 0.0);
		World world = Bukkit.getServer().getWorld(worldName);
		if (world == null)
		    continue;
		Location point1 = new Location(world, conf.getDouble("restrictedareas." + areaKey + ".point1.x", 0.0), conf.getDouble("restrictedareas." + areaKey
		    + ".point1.y", 0.0), conf.getDouble("restrictedareas." + areaKey + ".point1.z", 0.0));

		Location point2 = new Location(world, conf.getDouble("restrictedareas." + areaKey + ".point2.x", 0.0), conf.getDouble("restrictedareas." + areaKey
		    + ".point2.y", 0.0), conf.getDouble("restrictedareas." + areaKey + ".point2.z", 0.0));
		this.restrictedAreas.add(new RestrictedArea(point1, point2, multiplier));
	    }
	}
	try {
	    conf.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Method to load the restricted areas configuration
     * 
     * loads from Jobs/restrictedAreas.yml
     */
    private synchronized void loadRestrictedBlocks() {
	File f = new File(plugin.getDataFolder(), "restrictedBlocks.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

	CommentedYamlConfiguration writer = new CommentedYamlConfiguration();

	config.options().copyDefaults(true);

	writer.addComment("restrictedblocks", "All block to be protected from place/break exploit.", "This will prevent piston moving all blocks in list",
	    "Dont count in vegetables or any single click break blocks");
	restrictedBlocks.add("14");
	restrictedBlocks.add("15");
	restrictedBlocks.add("16");
	restrictedBlocks.add("21");
	restrictedBlocks.add("48");
	restrictedBlocks.add("56");
	restrictedBlocks.add("73");
	restrictedBlocks.add("74");
	restrictedBlocks.add("129");
	restrictedBlocks.add("153");
	config.addDefault("restrictedblocks", restrictedBlocks);
	restrictedBlocks = (ArrayList<String>) config.getStringList("restrictedblocks");
	copySetting(config, writer, "restrictedblocks");

	writer.addComment("blockstimer", "Block protected by timer in sec",
	    "141-60 means that carrot can be harvested after 60 sec (remember to use id's from placed objects, not from your inventory)");
	restrictedBlocksTimer.add("2-60");
	restrictedBlocksTimer.add("3-60");
	restrictedBlocksTimer.add("6-60");
	restrictedBlocksTimer.add("12-60");
	restrictedBlocksTimer.add("18-60");
	restrictedBlocksTimer.add("31-60");
	restrictedBlocksTimer.add("32-60");
	restrictedBlocksTimer.add("37-60");
	restrictedBlocksTimer.add("38-60");
	restrictedBlocksTimer.add("39-60");
	restrictedBlocksTimer.add("40-60");
	restrictedBlocksTimer.add("55-60");
	restrictedBlocksTimer.add("59-60");
	restrictedBlocksTimer.add("80-60");
	restrictedBlocksTimer.add("81-60");
	restrictedBlocksTimer.add("83-60");
	restrictedBlocksTimer.add("103-60");
	restrictedBlocksTimer.add("106-60");
	restrictedBlocksTimer.add("111-60");
	restrictedBlocksTimer.add("141-60");
	restrictedBlocksTimer.add("142-60");
	restrictedBlocksTimer.add("161-60");
	restrictedBlocksTimer.add("171-60");
	restrictedBlocksTimer.add("175-60");
	config.addDefault("blockstimer", restrictedBlocksTimer);
	restrictedBlocksTimer = (ArrayList<String>) config.getStringList("blockstimer");
	copySetting(config, writer, "blockstimer");

	writer.addComment("PlacedBlockTimer", "Block place protected by timer in sec", "For this to work CoreProtect plugin should be installed");
	restrictedPlaceBlocksTimer.add(2);
	restrictedPlaceBlocksTimer.add(3);
	restrictedPlaceBlocksTimer.add(6);
	restrictedPlaceBlocksTimer.add(12);
	restrictedPlaceBlocksTimer.add(18);
	restrictedPlaceBlocksTimer.add(31);
	restrictedPlaceBlocksTimer.add(32);
	restrictedPlaceBlocksTimer.add(37);
	restrictedPlaceBlocksTimer.add(38);
	restrictedPlaceBlocksTimer.add(39);
	restrictedPlaceBlocksTimer.add(40);
	restrictedPlaceBlocksTimer.add(55);
	restrictedPlaceBlocksTimer.add(59);
	restrictedPlaceBlocksTimer.add(80);
	restrictedPlaceBlocksTimer.add(81);
	restrictedPlaceBlocksTimer.add(83);
	restrictedPlaceBlocksTimer.add(103);
	restrictedPlaceBlocksTimer.add(106);
	restrictedPlaceBlocksTimer.add(111);
	restrictedPlaceBlocksTimer.add(141);
	restrictedPlaceBlocksTimer.add(142);
	restrictedPlaceBlocksTimer.add(161);
	restrictedPlaceBlocksTimer.add(171);
	restrictedPlaceBlocksTimer.add(175);
	config.addDefault("PlacedBlockTimer", restrictedPlaceBlocksTimer);
	restrictedPlaceBlocksTimer = (ArrayList<Integer>) config.getIntegerList("PlacedBlockTimer");
	copySetting(config, writer, "PlacedBlockTimer");

	writer.addComment("PlacedBlockTimer", "Block place protected by timer in sec", "For this to work CoreProtect plugin should be installed");
	restrictedPlaceBlocksTimer.add(2);
	restrictedPlaceBlocksTimer.add(3);
	restrictedPlaceBlocksTimer.add(6);
	restrictedPlaceBlocksTimer.add(12);
	restrictedPlaceBlocksTimer.add(18);
	restrictedPlaceBlocksTimer.add(31);
	restrictedPlaceBlocksTimer.add(32);
	restrictedPlaceBlocksTimer.add(37);
	restrictedPlaceBlocksTimer.add(38);
	restrictedPlaceBlocksTimer.add(39);
	restrictedPlaceBlocksTimer.add(40);
	restrictedPlaceBlocksTimer.add(55);
	restrictedPlaceBlocksTimer.add(59);
	restrictedPlaceBlocksTimer.add(80);
	restrictedPlaceBlocksTimer.add(81);
	restrictedPlaceBlocksTimer.add(83);
	restrictedPlaceBlocksTimer.add(103);
	restrictedPlaceBlocksTimer.add(106);
	restrictedPlaceBlocksTimer.add(111);
	restrictedPlaceBlocksTimer.add(141);
	restrictedPlaceBlocksTimer.add(142);
	restrictedPlaceBlocksTimer.add(161);
	restrictedPlaceBlocksTimer.add(171);
	restrictedPlaceBlocksTimer.add(175);
	config.addDefault("PlacedBlockTimer", restrictedPlaceBlocksTimer);
	restrictedPlaceBlocksTimer = (ArrayList<Integer>) config.getIntegerList("PlacedBlockTimer");
	copySetting(config, writer, "PlacedBlockTimer");

	try {
	    writer.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Method to load the scheduler configuration
     * 
     * loads from Jobs/schedule.yml
     */
    public void loadScheduler() {
	File f = new File(plugin.getDataFolder(), "schedule.yml");
	YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

	conf.options().copyDefaults(true);

	if (!conf.contains("Boost"))
	    return;

	ArrayList<String> sections = new ArrayList<String>(conf.getConfigurationSection("Boost").getKeys(false));

	for (String OneSection : sections) {
	    ConfigurationSection path = conf.getConfigurationSection("Boost." + OneSection);

	    if (!path.contains("Enabled"))
		continue;

	    if (!conf.getConfigurationSection("Boost." + OneSection).getBoolean("Enabled"))
		continue;

	    Schedule sched = new Schedule();
	    sched.setName(OneSection);

	    if (!path.contains("From") || !path.getString("From").contains(":"))
		continue;

	    if (!path.contains("Until") || !path.getString("Until").contains(":"))
		continue;

	    if (!path.contains("Days") || !path.isList("Days"))
		continue;

	    if (!path.contains("Jobs") || !path.isList("Jobs"))
		continue;

	    if (!path.contains("Exp") || !path.isDouble("Exp"))
		continue;

	    if (!path.contains("Money") || !path.isDouble("Money"))
		continue;

	    sched.setDays(path.getStringList("Days"));
	    sched.setJobs(path.getStringList("Jobs"));
	    sched.setFrom(Integer.valueOf(path.getString("From").replace(":", "")));
	    sched.setUntil(Integer.valueOf(path.getString("Until").replace(":", "")));

	    if (path.contains("MessageOnStart") && path.isList("MessageOnStart"))
		sched.setMessageOnStart(path.getStringList("MessageOnStart"), path.getString("From"), path.getString("Until"));

	    if (path.contains("BroadcastOnStart"))
		sched.setBroadcastOnStart(path.getBoolean("BroadcastOnStart"));

	    if (path.contains("MessageOnStop") && path.isList("MessageOnStop"))
		sched.setMessageOnStop(path.getStringList("MessageOnStop"), path.getString("From"), path.getString("Until"));

	    if (path.contains("BroadcastOnStop"))
		sched.setBroadcastOnStop(path.getBoolean("BroadcastOnStop"));

	    if (path.contains("BroadcastInterval"))
		sched.setBroadcastInterval(path.getInt("BroadcastInterval"));

	    if (path.contains("BroadcastMessage") && path.isList("BroadcastMessage"))
		sched.setMessageToBroadcast(path.getStringList("BroadcastMessage"), path.getString("From"), path.getString("Until"));

	    sched.setExpBoost(path.getDouble("Exp"));
	    sched.setMoneyBoost(path.getDouble("Money"));

	    BoostSchedule.add(sched);
	}

//		try {
//			conf.save(f);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }

    private Boolean getBoolean(String path, Boolean boo, YamlConfiguration config, CommentedYamlConfiguration writer) {
	config.addDefault(path, boo);
	copySetting(config, writer, path);
	return config.getBoolean(path);
    }

    private int getInt(String path, int boo, YamlConfiguration config, CommentedYamlConfiguration writer) {
	config.addDefault(path, boo);
	copySetting(config, writer, path);
	return config.getInt(path);
    }

    private List<Integer> getIntArray(String path, List<Integer> boo, YamlConfiguration config, CommentedYamlConfiguration writer) {
	config.addDefault(path, boo);
	copySetting(config, writer, path);
	return config.getIntegerList(path);
    }

    private String getString(String path, String boo, YamlConfiguration config, CommentedYamlConfiguration writer) {
	config.addDefault(path, boo);
	copySetting(config, writer, path);
	return config.getString(path);
    }

    private String getString(String path, String boo, YamlConfiguration config, CommentedYamlConfiguration writer, boolean colorize) {
	config.addDefault(path, boo);
	copySetting(config, writer, path);
	return org.bukkit.ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }

    private Double getDouble(String path, Double boo, YamlConfiguration config, CommentedYamlConfiguration writer) {
	config.addDefault(path, boo);
	copySetting(config, writer, path);
	return config.getDouble(path);
    }

    private synchronized void loadItemList() {
	YmlMaker ItemFile = new YmlMaker((JavaPlugin) plugin, "ItemList.yml");
	ItemFile.saveDefaultConfig();
	List<String> section = ItemFile.getConfig().getStringList("ItemList");
	ListOfNames.clear();
	for (String one : section) {
	    if (!one.contains(" - "))
		continue;

	    if (!one.contains(" = "))
		continue;

	    String meta = "";
	    String id = one.split(" - ")[0];
	    String part2 = one.split(" - ")[1];
	    String part3 = "";

	    part3 = part2.split(" = ")[1];
	    part2 = part2.split(" = ")[0];

	    if (id.contains(":")) {
		meta = id.split(":")[1];
		id = id.split(":")[0];
	    }

	    ListOfNames.add(new NameList(id, meta, part2, part3));
	}

	section = ItemFile.getConfig().getStringList("EntityList");
	ListOfEntities.clear();
	for (String one : section) {
	    if (!one.contains(" - "))
		continue;

	    if (!one.contains(" = "))
		continue;

	    String meta = "";
	    String id = one.split(" - ")[0];
	    String part2 = one.split(" - ")[1];
	    String part3 = "";

	    part3 = part2.split(" = ")[1];
	    part2 = part2.split(" = ")[0];

	    if (id.contains(":")) {
		meta = id.split(":")[1];
		id = id.split(":")[0];
	    }

	    ListOfEntities.add(new NameList(id, meta, part2, part3));
	}

	section = ItemFile.getConfig().getStringList("EnchantList");
	ListOfEnchants.clear();
	for (String one : section) {
	    if (!one.contains(" - "))
		continue;

	    if (!one.contains(" = "))
		continue;

	    String id = one.split(" - ")[0];
	    String part2 = one.split(" - ")[1];
	    String part3 = "";

	    part3 = part2.split(" = ")[1];
	    part2 = part2.split(" = ")[0];

	    ListOfEnchants.add(new NameList(id, "", part2, part3));
	}

	section = ItemFile.getConfig().getStringList("ColorList");
	ListOfColors.clear();
	for (String one : section) {
	    if (!one.contains(" - "))
		continue;

	    if (!one.contains(" = "))
		continue;

	    String id = one.split(" - ")[0];
	    String part2 = one.split(" - ")[1];
	    String part3 = "";

	    part3 = part2.split(" = ")[1];
	    part2 = part2.split(" = ")[0];

	    ListOfColors.add(new NameList(id, "", part2, part3));
	}

    }

    /**
     * Method to load the language file configuration
     * 
     * loads from Jobs/locale/messages_en.yml
     */
    private synchronized void loadLanguage() {

	// Just copying default language files, except en, that one will be generated
	List<String> languages = new ArrayList<String>();
	languages.add("lt");
	languages.add("de");
	languages.add("cs");
	languages.add("fr");
	languages.add("ru");
	languages.add("cz");

	for (String lang : languages) {
	    YmlMaker langFile = new YmlMaker((JavaPlugin) plugin, "locale" + File.separator + "messages_" + lang + ".yml");
	    if (langFile != null)
		langFile.saveDefaultConfig();
	}

	languages.clear();
	languages.add("en");

	File customLocaleFile = new File(plugin.getDataFolder(), "locale" + File.separator + "messages_" + localeString + ".yml");
	if (!customLocaleFile.exists() && !localeString.equalsIgnoreCase("en"))
	    languages.add(localeString);

	for (String lang : languages) {

	    File f = new File(plugin.getDataFolder(), "locale" + File.separator + "messages_" + lang + ".yml");
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);

	    CommentedYamlConfiguration writer = new CommentedYamlConfiguration();

	    conf.options().copyDefaults(true);

	    GetConfigString("economy.error.nomoney", "Sorry, no money left in national bank!", writer, conf, true);
	    GetConfigString("limitedItem.error.levelup", "&cYou need to levelup in [jobname] to use this item!", writer, conf, true);

	    GetConfigString("command.moneyboost.help.info", "Boosts Money gain for all players", writer, conf, true);
	    GetConfigString("command.moneyboost.help.args", "[jobname] [rate]", writer, conf, true);
	    GetConfigString("command.moneyboost.output.allreset", "All money boost turned off", writer, conf, true);
	    GetConfigString("command.moneyboost.output.jobsboostreset", "Money boost for %jobname% was turned off", writer, conf, true);
	    GetConfigString("command.moneyboost.output.nothingtoreset", "Nothing to reset", writer, conf, true);
	    GetConfigString("command.moneyboost.output.boostalladded", "Money boost of %boost% added for all jobs!", writer, conf, true);
	    GetConfigString("command.moneyboost.output.boostadded", "Money boost of &e%boost% &aadded for &e%jobname%!", writer, conf, true);
	    GetConfigString("command.moneyboost.output.infostats", "&c-----> &aMoney rate x%boost% enabled&c <-------", writer, conf, true);

	    GetConfigString("command.expboost.help.info", "Boosts Exp gain for all players", writer, conf, true);
	    GetConfigString("command.expboost.help.args", "[jobname] [rate]", writer, conf, true);
	    GetConfigString("command.expboost.output.allreset", "All exp boost turned off", writer, conf, true);
	    GetConfigString("command.expboost.output.jobsboostreset", "Exp boost for %jobname% was turned off", writer, conf, true);
	    GetConfigString("command.expboost.output.nothingtoreset", "Nothing to reset", writer, conf, true);
	    GetConfigString("command.expboost.output.boostalladded", "Exp boost of %boost% added for all jobs!", writer, conf, true);
	    GetConfigString("command.expboost.output.boostadded", "Exp boost of &e%boost% &aadded for &e%jobname%!", writer, conf, true);
	    GetConfigString("command.expboost.output.infostats", "&c-----> &aExp rate x%boost% enabled&c <-------", writer, conf, true);

	    GetConfigString("command.convert.help.info",
		"Converts data base system from one system to another. if you currently running sqlite, this will convert to Mysql and vise versa.", writer, conf, true);
	    GetConfigString("command.convert.help.args", "", writer, conf, true);

	    GetConfigString("command.limit.help.info", "Shows payment limits for jobs", writer, conf, true);
	    GetConfigString("command.limit.help.args", "", writer, conf, true);

	    GetConfigString("command.limit.output.lefttime", "&eTime left until money limit resets: &2%hour% &ehour &2%min% &emin &2%sec% &esec", writer, conf, true);
	    GetConfigString("command.limit.output.moneylimit", "&eMoney limit: &2%money%&e/&2%totalmoney%", writer, conf, true);
	    GetConfigString("command.limit.output.leftexptime", "&eTime left until Exp limit resets: &2%hour% &ehour &2%min% &emin &2%sec% &esec", writer, conf, true);
	    GetConfigString("command.limit.output.explimit", "&eExp limit: &2%exp%&e/&2%totalexp%", writer, conf, true);

	    GetConfigString("command.limit.output.reachedlimit", "&4You have reached money limit in given time!", writer, conf, true);
	    GetConfigString("command.limit.output.reachedlimit2", "&eYou can check your limit with &2/jobs limit &ecommand", writer, conf, true);
	    GetConfigString("command.limit.output.reachedExplimit", "&4You have reached exp limit in given time!", writer, conf, true);
	    GetConfigString("command.limit.output.reachedExplimit2", "&eYou can check your limit with &2/jobs limit &ecommand", writer, conf, true);

	    GetConfigString("command.limit.output.notenabled", "&eMoney limit is not enabled", writer, conf, true);

	    GetConfigString("command.admin.error", "There was an error in the command.", writer, conf, true);
	    GetConfigString("command.admin.success", "Your command has been performed.", writer, conf, true);

	    GetConfigString("command.error.notNumber", "&ePlease use numbers!", writer, conf, true);
	    GetConfigString("command.error.job", "The job you have selected does not exist!", writer, conf, true);
	    GetConfigString("command.error.permission", "You do not have permission to do that!", writer, conf, true);

	    GetConfigString("command.help.output.info", "Type /jobs [cmd] ? for more information about a command.", writer, conf, true);
	    GetConfigString("command.help.output.usage", "Usage: %usage%", writer, conf, true);

	    GetConfigString("command.stats.help.info", "Show the level you are in each job you are part of.", writer, conf, true);
	    GetConfigString("command.stats.help.args", "[playername]", writer, conf, true);
	    GetConfigString("command.stats.error.nojob", "Please join a job first.", writer, conf, true);
	    GetConfigString("command.stats.output", "lvl%joblevel% %jobname% : %jobxp%/%jobmaxxp% xp", writer, conf, true);

	    GetConfigString("command.archive.help.info", "Shows all jobs saved in archive by user.", writer, conf, true);
	    GetConfigString("command.archive.help.args", "[playername]", writer, conf, true);
	    GetConfigString("command.archive.error.nojob", "There is no jobs saved.", writer, conf, true);
	    GetConfigString("command.archive.output", "lvl %joblevel% (%getbackjoblevel%) %jobname%", writer, conf, true);

	    GetConfigString("command.give.help.info", "Gives item by jobs name and item category name. Player name is optional", writer, conf, true);
	    GetConfigString("command.give.help.args", "[playername] [jobname] [itemname]", writer, conf, true);
	    GetConfigString("command.give.output.notonline", "&4Player [%playername%] is not online!", writer, conf, true);
	    GetConfigString("command.give.output.noitem", "&4Cant find any item by given name!", writer, conf, true);

	    GetConfigString("command.info.help.title", "&2*** &eJobs&2 ***", writer, conf, true);
	    GetConfigString("command.info.help.info", "Show how much each job is getting paid and for what.", writer, conf, true);
	    GetConfigString("command.info.help.penalty", "&eThis job have &c[penalty]% &epenalty because of too many players working in it.", writer, conf, true);
	    GetConfigString("command.info.help.bonus", "&eThis job have &2[bonus]% &ebonus because not enough players working in it.", writer, conf, true);
	    GetConfigString("command.info.help.args", "[jobname] [action]", writer, conf, true);
	    GetConfigString("command.info.help.actions", "&eValid actions are: &f%actions%", writer, conf, true);
	    GetConfigString("command.info.help.max", " - &emax level:&f ", writer, conf, true);
	    GetConfigString("command.info.help.material", "&7%material%", writer, conf, true);

	    GetConfigString("command.info.gui.pickjob", "&ePick your job!", writer, conf, true);
	    GetConfigString("command.info.gui.jobinfo", "&e[jobname] info!", writer, conf, true);
	    GetConfigString("command.info.gui.actions", "&eValid actions are:", writer, conf, true);
	    GetConfigString("command.info.gui.leftClick", "&eLeft Click for more info", writer, conf, true);
	    GetConfigString("command.info.gui.rightClick", "&eRight click to join job", writer, conf, true);
	    GetConfigString("command.info.gui.leftSlots", "&eLeft slots:&f ", writer, conf, true);
	    GetConfigString("command.info.gui.working", "&2&nAlready working", writer, conf, true);
	    GetConfigString("command.info.gui.max", "&eMax level:&f ", writer, conf, true);
	    GetConfigString("command.info.gui.back", "&e<<< Back", writer, conf, true);

	    GetConfigString("command.info.output.break.info", "Break", writer, conf, true);
	    GetConfigString("command.info.output.break.none", "%jobname% does not get money for breaking blocks.", writer, conf, true);
	    GetConfigString("command.info.output.tntbreak.info", "TNTBreak", writer, conf, true);
	    GetConfigString("command.info.output.tntbreak.none", "%jobname% does not get money for breaking blocks with tnt.", writer, conf, true);
	    GetConfigString("command.info.output.place.info", "Place", writer, conf, true);
	    GetConfigString("command.info.output.place.none", "%jobname% does not get money for placing blocks.", writer, conf, true);
	    GetConfigString("command.info.output.kill.info", "Kill", writer, conf, true);
	    GetConfigString("command.info.output.kill.none", "%jobname% does not get money for killing monsters.", writer, conf, true);
	    GetConfigString("command.info.output.mmkill.info", "MMKill", writer, conf, true);
	    GetConfigString("command.info.output.mmkill.none", "%jobname% does not get money for killing Mythic monsters.", writer, conf, true);
	    GetConfigString("command.info.output.fish.info", "Fish", writer, conf, true);
	    GetConfigString("command.info.output.fish.none", "%jobname% does not get money from fishing.", writer, conf, true);
	    GetConfigString("command.info.output.craft.info", "Craft", writer, conf, true);
	    GetConfigString("command.info.output.craft.none", "%jobname% does not get money from crafting.", writer, conf, true);
	    GetConfigString("command.info.output.smelt.info", "Smelt", writer, conf, true);
	    GetConfigString("command.info.output.smelt.none", "%jobname% does not get money from smelting.", writer, conf, true);
	    GetConfigString("command.info.output.brew.info", "Brew", writer, conf, true);
	    GetConfigString("command.info.output.brew.none", "%jobname% does not get money from brewing.", writer, conf, true);
	    GetConfigString("command.info.output.eat.info", "Eat", writer, conf, true);
	    GetConfigString("command.info.output.eat.none", "%jobname% does not get money from eating food.", writer, conf, true);
	    GetConfigString("command.info.output.enchant.info", "Enchant", writer, conf, true);
	    GetConfigString("command.info.output.enchant.none", "%jobname% does not get money from enchanting.", writer, conf, true);
	    GetConfigString("command.info.output.repair.info", "Repair", writer, conf, true);
	    GetConfigString("command.info.output.repair.none", "%jobname% does not get money from repairing.", writer, conf, true);
	    GetConfigString("command.info.output.breed.info", "Breed", writer, conf, true);
	    GetConfigString("command.info.output.breed.none", "%jobname% does not get money from breeding.", writer, conf, true);
	    GetConfigString("command.info.output.tame.info", "Tame", writer, conf, true);
	    GetConfigString("command.info.output.tame.none", "%jobname% does not get money from taming.", writer, conf, true);
	    GetConfigString("command.info.output.milk.info", "Milk", writer, conf, true);
	    GetConfigString("command.info.output.milk.none", "%jobname% does not get money from milking cows.", writer, conf, true);
	    GetConfigString("command.info.output.shear.info", "Shear", writer, conf, true);
	    GetConfigString("command.info.output.shear.none", "%jobname% does not get money from shearing sheeps.", writer, conf, true);
	    GetConfigString("command.info.output.explore.info", "Explore", writer, conf, true);
	    GetConfigString("command.info.output.explore.none", "%jobname% does not get money from exploring.", writer, conf, true);
	    GetConfigString("command.info.output.custom-kill.info", "Custom kill", writer, conf, true);
	    GetConfigString("command.info.output.custom-kill.none", "%jobname% does not get money from custom player kills.", writer, conf, true);

	    GetConfigString("command.playerinfo.help.info", "Show how much each job is getting paid and for what on another player.", writer, conf, true);
	    GetConfigString("command.playerinfo.help.args", "[playername] [jobname] [action]", writer, conf, true);

	    GetConfigString("command.join.help.info", "Join the selected job.", writer, conf, true);
	    GetConfigString("command.join.help.args", "[jobname]", writer, conf, true);
	    GetConfigString("command.join.error.alreadyin", "You are already in the job %jobname%.", writer, conf, true);
	    GetConfigString("command.join.error.fullslots", "You cannot join the job %jobname%, there are no slots available.", writer, conf, true);
	    GetConfigString("command.join.error.maxjobs", "You have already joined too many jobs.", writer, conf, true);
	    GetConfigString("command.join.success", "You have joined the job %jobname%.", writer, conf, true);

	    GetConfigString("command.leave.help.info", "Leave the selected job.", writer, conf, true);
	    GetConfigString("command.leave.help.args", "[jobname]", writer, conf, true);
	    GetConfigString("command.leave.success", "You have left the job %jobname%.", writer, conf, true);

	    GetConfigString("command.fixnames.help.info", "Tries to fix NULL player names in data base.", writer, conf, true);
	    GetConfigString("command.fixnames.help.args", "", writer, conf, true);

	    GetConfigString("command.leaveall.help.info", "Leave all your jobs.", writer, conf, true);
	    GetConfigString("command.leaveall.error.nojobs", "You do not have any jobs to leave!", writer, conf, true);
	    GetConfigString("command.leaveall.success", "You have left all your jobs.", writer, conf, true);

	    GetConfigString("command.browse.help.info", "List the jobs available to you.", writer, conf, true);
	    GetConfigString("command.browse.error.nojobs", "There are no jobs you can join.", writer, conf, true);
	    GetConfigString("command.browse.output.header", "You are allowed to join the following jobs:", writer, conf, true);
	    GetConfigString("command.browse.output.footer", "For more information type in /jobs info [JobName]", writer, conf, true);
	    GetConfigString("command.browse.output.totalWorkers", " &7Workers: &e[amount]", writer, conf, true);
	    GetConfigString("command.browse.output.penalty", " &4Penalty: &c[amount]%", writer, conf, true);
	    GetConfigString("command.browse.output.bonus", " &2Bonus: &a[amount]%", writer, conf, true);

	    GetConfigString("command.fire.help.info", "Fire the player from the job.", writer, conf, true);
	    GetConfigString("command.fire.help.args", "[playername] [jobname]", writer, conf, true);
	    GetConfigString("command.fire.error.nojob", "Player does not have the job %jobname%.", writer, conf, true);
	    GetConfigString("command.fire.output.target", "You have been fired from %jobname%.", writer, conf, true);

	    GetConfigString("command.fireall.help.info", "Fire player from all their jobs.", writer, conf, true);
	    GetConfigString("command.fireall.help.args", "[playername]", writer, conf, true);
	    GetConfigString("command.fireall.error.nojobs", "Player does not have any jobs to be fired from!", writer, conf, true);
	    GetConfigString("command.fireall.output.target", "You have been fired from all your jobs.", writer, conf, true);

	    GetConfigString("command.employ.help.info", "Employ the player to the job.", writer, conf, true);
	    GetConfigString("command.employ.help.args", "[playername] [jobname]", writer, conf, true);
	    GetConfigString("command.employ.error.alreadyin", "Player is already in the job %jobname%.", writer, conf, true);
	    GetConfigString("command.employ.output.target", "You have been employed as a %jobname%.", writer, conf, true);

	    GetConfigString("command.top.help.info", "Shows top 15 players by jobs name.", writer, conf, true);
	    GetConfigString("command.top.help.args", "[jobname]", writer, conf, true);
	    GetConfigString("command.top.error.nojob", "Cant find any job with this name.", writer, conf, true);
	    GetConfigString("command.top.output.topline", "&aTop&e 15 &aplayers by &e%jobname% &ajob", writer, conf, true);
	    GetConfigString("command.top.output.list", "&e%number%&a. &e%playername% &alvl &e%level% &awith&e %exp% &aexp", writer, conf, true);
	    GetConfigString("command.top.output.prev", "&e<<<<< Prev page &2|", writer, conf, true);
	    GetConfigString("command.top.output.next", "&2|&e Next Page >>>>", writer, conf, true);
	    GetConfigString("command.top.output.show", "&2Show from &e[from] &2until &e[until] &2top list", writer, conf, true);

	    GetConfigString("command.gtop.help.info", "Shows top 15 players by global jobs level.", writer, conf, true);
	    GetConfigString("command.gtop.help.args", "", writer, conf, true);
	    GetConfigString("command.gtop.error.nojob", "Cant find any information.", writer, conf, true);
	    GetConfigString("command.gtop.output.topline", "&aTop&e 15 &aplayers by global job level", writer, conf, true);
	    GetConfigString("command.gtop.output.list", "&e%number%&a. &e%playername% &alvl &e%level% &awith&e %exp% &aexp", writer, conf, true);
	    GetConfigString("command.gtop.output.prev", "&e<<<<< Prev page &2|", writer, conf, true);
	    GetConfigString("command.gtop.output.next", "&2|&e Next Page >>>>", writer, conf, true);
	    GetConfigString("command.gtop.output.show", "&2Show from &e[from] &2until &e[until] &2global top list", writer, conf, true);

	    GetConfigString("command.log.help.info", "Shows statistics.", writer, conf, true);
	    GetConfigString("command.log.help.args", "[playername]", writer, conf, true);
	    GetConfigString("command.log.output.topline", "&7************************* &6%playername% &7*************************", writer, conf, true);
	    GetConfigString("command.log.output.list", "&7* &6%number%. &3%action%: &6%item% &eqty: %qty% &6money: %money% &eexp: %exp%", writer, conf, true);
	    GetConfigString("command.log.output.bottomline", "&7***********************************************************", writer, conf, true);
	    GetConfigString("command.log.output.prev", "&e<<<<< Prev page &2|", writer, conf, true);
	    GetConfigString("command.log.output.next", "&2|&e Next Page >>>>", writer, conf, true);
	    GetConfigString("command.log.output.nodata", "&cData not found", writer, conf, true);

	    GetConfigString("command.glog.help.info", "Shows global statistics.", writer, conf, true);
	    GetConfigString("command.glog.help.args", "", writer, conf, true);
	    GetConfigString("command.glog.output.topline", "&7*********************** &6Global statistics &7***********************", writer, conf, true);
	    GetConfigString("command.glog.output.list", "&7* &6%number%. &3%username% &e%action%: &6%item% &eqty: %qty% &6money: %money% &eexp: %exp%", writer, conf,
		true);
	    GetConfigString("command.glog.output.bottomline", "&7**************************************************************", writer, conf, true);
	    GetConfigString("command.glog.output.nodata", "&cData not found", writer, conf, true);

	    GetConfigString("command.transfer.help.info", "Transfer a player's job from an old job to a new job.", writer, conf, true);
	    GetConfigString("command.transfer.help.args", "[playername] [oldjob] [newjob]", writer, conf, true);
	    GetConfigString("command.transfer.output.target", "You have been transferred from %oldjobname% to %newjobname%.", writer, conf, true);

	    GetConfigString("command.promote.help.info", "Promote the player X levels in a job.", writer, conf, true);
	    GetConfigString("command.promote.help.args", "[playername] [jobname] [levels]", writer, conf, true);
	    GetConfigString("command.promote.output.target", "You have been promoted %levelsgained% levels in %jobname%.", writer, conf, true);

	    GetConfigString("command.demote.help.info", "Demote the player X levels in a job.", writer, conf, true);
	    GetConfigString("command.demote.help.args", "[playername] [jobname] [levels]", writer, conf, true);
	    GetConfigString("command.demote.output.target", "You have been demoted %levelslost% levels in %jobname%.", writer, conf, true);

	    GetConfigString("command.grantxp.help.info", "Grant the player X experience in a job.", writer, conf, true);
	    GetConfigString("command.grantxp.help.args", "[playername] [jobname] [xp]", writer, conf, true);
	    GetConfigString("command.grantxp.output.target", "You have been granted %xpgained% experience in %jobname%.", writer, conf, true);

	    GetConfigString("command.removexp.help.info", "Remove X experience from the player in a job.", writer, conf, true);
	    GetConfigString("command.removexp.help.args", "[playername] [jobname] [xp]", writer, conf, true);
	    GetConfigString("command.removexp.output.target", "You have lost %xplost% experience in %jobname%.", writer, conf, true);

	    GetConfigString("command.signupdate.help.info", "Manualy updates sign by its name", writer, conf, true);
	    GetConfigString("command.signupdate.help.args", "[jobname]", writer, conf, true);

	    GetConfigString("command.reload.help.info", "Reload configurations.", writer, conf, true);

	    GetConfigString("message.skillup.broadcast", "%playername% has been promoted to a %titlename% %jobname%.", writer, conf, true);
	    GetConfigString("message.skillup.nobroadcast", "Congratulations, you have been promoted to a %titlename% %jobname%.", writer, conf, true);

	    GetConfigString("message.levelup.broadcast", "%playername% is now a level %joblevel% %jobname%.", writer, conf, true);
	    GetConfigString("message.levelup.nobroadcast", "You are now a level %joblevel% %jobname%.", writer, conf, true);

	    GetConfigString("message.cowtimer", "&eYou still need to wait &6%time% &esec to get paid for this job.", writer, conf, true);
	    GetConfigString("message.blocktimer", "&eYou need to wait: &3[time] &esec more to get paid for this!", writer, conf, true);
	    GetConfigString("message.placeblocktimer", "&eYou cant place block faster than &6[time] &esec interval in same place!", writer, conf, true);
	    GetConfigString("message.taxes", "&3[amount] &eserver taxes where transfered to this account", writer, conf, true);

	    GetConfigString("message.boostStarted", "&eJobs boost time have been started!", writer, conf, true);
	    GetConfigString("message.boostStoped", "&eJobs boost time have been ended!", writer, conf, true);

	    GetConfigString("command.toggle.help.info", "Toggles payment output on action bar.", writer, conf, true);
	    GetConfigString("command.toggle.output.turnedoff", "&4This feature are turned off!", writer, conf, true);
	    GetConfigString("command.toggle.output.paid", "&aYou got paid for &2[amount]&a and got &2[exp] &aexp", writer, conf, true);
	    GetConfigString("command.toggle.output.on", "&aToggled: &aON", writer, conf, true);
	    GetConfigString("command.toggle.output.off", "&aToggled: &4OFF", writer, conf, true);

	    GetConfigString("message.crafting.fullinventory", "Your inventory is full!", writer, conf, true);

	    GetConfigString("signs.List", "&0[number].&8[player]&7:&4[level]", writer, conf, true);
	    GetConfigString("signs.SpecialList.1.1", "&b** &8First &b**", writer, conf, true);
	    GetConfigString("signs.SpecialList.1.2", "&9[player]", writer, conf, true);
	    GetConfigString("signs.SpecialList.1.3", "&8[level] level", writer, conf, true);
	    GetConfigString("signs.SpecialList.1.4", "&b************", writer, conf, true);
	    GetConfigString("signs.SpecialList.2.1", "&b** &8Second &b**", writer, conf, true);
	    GetConfigString("signs.SpecialList.2.2", "&9[player]", writer, conf, true);
	    GetConfigString("signs.SpecialList.2.3", "&8[level] level", writer, conf, true);
	    GetConfigString("signs.SpecialList.2.4", "&b************", writer, conf, true);
	    GetConfigString("signs.SpecialList.3.1", "&b** &8Third &b**", writer, conf, true);
	    GetConfigString("signs.SpecialList.3.2", "&9[player]", writer, conf, true);
	    GetConfigString("signs.SpecialList.3.3", "&8[level] level", writer, conf, true);
	    GetConfigString("signs.SpecialList.3.4", "&b************", writer, conf, true);
	    GetConfigString("signs.cantcreate", "&4You can't create this sign!", writer, conf, true);
	    GetConfigString("signs.cantdestroy", "&4You can't destroy this sign!", writer, conf, true);
	    GetConfigString("signs.topline", "&2[Jobs]", writer, conf, true);
	    GetConfigString("signs.secondline.join", "&2Join", writer, conf, true);
	    GetConfigString("signs.secondline.leave", "&4Leave", writer, conf, true);
	    GetConfigString("signs.secondline.toggle", "&2Toggle", writer, conf, true);
	    GetConfigString("signs.secondline.top", "&2Top", writer, conf, true);
	    GetConfigString("signs.secondline.browse", "&2Browse", writer, conf, true);
	    GetConfigString("signs.secondline.stats", "&2Stats", writer, conf, true);
	    GetConfigString("signs.secondline.limit", "&2Limit", writer, conf, true);
	    GetConfigString("signs.secondline.info", "&2Info", writer, conf, true);
	    GetConfigString("signs.secondline.archive", "&2Archive", writer, conf, true);

	    //GetConfigString("scoreboard.clear", "&eIf you want to remove scoreboard, type &2/jobs top clear", writer, conf, true);
	    GetConfigString("scoreboard.topline", "&2Top &e%jobname%", writer, conf, true);
	    GetConfigString("scoreboard.gtopline", "&2Global top list", writer, conf, true);
	    GetConfigString("scoreboard.lines", "&2%number%. &e%playername%", writer, conf, true);

	    keys = new ArrayList<String>(conf.getConfigurationSection("signs.secondline").getKeys(false));

	    // Write back config
	    try {
		writer.save(f);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
