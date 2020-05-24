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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;
import com.gamingmesh.jobs.container.CurrencyLimit;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Schedule;
import com.gamingmesh.jobs.resources.jfep.Parser;

public class GeneralConfigManager {
    public List<Integer> BroadcastingLevelUpLevels = new ArrayList<>();
    protected Locale locale;
    protected int savePeriod;
    protected boolean economyAsync;
    protected boolean isBroadcastingSkillups;
    protected boolean isBroadcastingLevelups;
    protected boolean payInCreative;
    protected boolean payExploringWhenFlying;
    public boolean payExploringWhenGliding;
    public boolean disablePaymentIfRiding;
    protected boolean addXpPlayer;
    public boolean boostedItemsInOffHand;
    public boolean payItemDurabilityLoss;
    public HashMap<CMIMaterial, HashMap<Enchantment, Integer>> whiteListedItems = new HashMap<>();
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
    private HashMap<CurrencyType, Double> generalMulti = new HashMap<>();
    private String getSelectionTool;

    public boolean enableSchedule;

    public int jobExpiryTime;

    private int ResetTimeHour;
    private int ResetTimeMinute;
    private int DailyQuestsSkips;
    public double skipQuestCost;
    private ConfigReader c = null;

    // Limits
    private HashMap<CurrencyType, CurrencyLimit> currencyLimitUse = new HashMap<>();

    public boolean PayForRenaming, PayForEnchantingOnAnvil, PayForEachCraft, SignsEnabled,
	SignsColorizeJobName, ShowToplistInScoreboard, useGlobalTimer, useSilkTouchProtection, UseCustomNames,
	PreventSlimeSplit, PreventMagmaCubeSplit, PreventHopperFillUps, PreventBrewingStandFillUps,
	BrowseUseNewLook;
    public int globalblocktimer, CowMilkingTimer, InfoUpdateInterval;
    public Double TreeFellerMultiplier, gigaDrillMultiplier, superBreakerMultiplier;
    public String localeString = "";

    private boolean FurnacesReassign, BrewingStandsReassign;
    private int FurnacesMaxDefault, BrewingStandsMaxDefault, BrowseAmountToShow;

    public boolean useBlockProtection;
    public int BlockProtectionDays;

    public boolean applyToNegativeIncome;
    public boolean useMinimumOveralPayment;
    public boolean useMinimumOveralPoints;
    public boolean useBreederFinder = false;
    private boolean useTnTFinder = false;
    public boolean CancelCowMilking;
    public boolean fixAtMaxLevel, TitleChangeChat, TitleChangeActionBar, LevelChangeChat,
	LevelChangeActionBar, SoundLevelupUse, SoundTitleChangeUse, UseServerAccount, EmptyServerAccountChat,
	EmptyServerAccountActionBar, ActionBarsMessageByDefault, ShowTotalWorkers, ShowPenaltyBonus, useDynamicPayment,
	JobsGUIOpenOnBrowse, JobsGUIShowChatBrowse, JobsGUISwitcheButtons, UseInversedClickToLeave, ShowActionNames,
	DisableJoiningJobThroughGui;

    public boolean FireworkLevelupUse, UseRandom, UseFlicker, UseTrail;
    public String FireworkType;
    public List<String> FwColors = new ArrayList<>();
    public int FireworkPower, ShootTime;

    private int JobsGUIRows, JobsGUIBackButton,
	JobsGUIStartPosition,
	JobsGUIGroupAmount,
	JobsGUISkipAmount;

    private String DecimalPlacesMoney, DecimalPlacesExp, DecimalPlacesPoints;

    public ItemStack guiBackButton;
    public ItemStack guiFiller;

    public boolean UsePerPermissionForLeaving, EnableConfirmation, FilterHiddenPlayerFromTabComplete;
    public int JobsTopAmount, PlaceholdersPage, ConfirmExpiryTime;

    public Integer levelLossPercentageFromMax, levelLossPercentage, SoundLevelupVolume, SoundLevelupPitch, SoundTitleChangeVolume,
	SoundTitleChangePitch, ToplistInScoreboardInterval;
    public double MinimumOveralPaymentLimit;
    public double MinimumOveralPointsLimit;

    public boolean MonsterDamageUse = false;
    public double MonsterDamagePercentage;
    public double DynamicPaymentMaxPenalty;
    public double DynamicPaymentMaxBonus;
    public boolean useMaxPaymentCurve;
    public float maxPaymentCurveFactor;
    public double TaxesAmount;
    public String SoundLevelupSound, SoundTitleChangeSound, ServerAccountName, ServertaxesAccountName;
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
    public int SegementCount;
    public int BossBarTimer;
    public boolean BossBarsMessageByDefault;

    public Parser DynamicPaymentEquation;

    public boolean ExploreCompact;

    public boolean DisabledWorldsUse;
    public boolean UseAsWhiteListWorldList;
    public List<String> DisabledWorldsList = new ArrayList<>();

    public List<Schedule> BoostSchedule = new ArrayList<>();

    private HashMap<String, List<String>> commandArgs = new HashMap<>();

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

    public void setTntFinder(boolean state) {
	this.useTnTFinder = state;
    }

    public boolean isUseTntFinder() {
	return useTnTFinder;
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

    /**
     * Get the chat prefix string from file
     * @deprecated Use {@link #modifyChatPrefix}
     * @return
     */
    @Deprecated
    public String getModifyChatPrefix() {
	return modifyChatPrefix;
    }

    /**
     * Get the chat suffix string from file
     * @deprecated Use {@link #modifyChatSuffix}
     * @return
     */
    @Deprecated
    public String getModifyChatSuffix() {
	return modifyChatSuffix;
    }

    /**
     * Get the chat separator string from file
     * @deprecated Use {@link #modifyChatSeparator}
     * @return
     */
    @Deprecated
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
	if (ent == null || ent.getWorld() == null)
	    return true;

	return canPerformActionInWorld(ent.getWorld());
    }

    public boolean canPerformActionInWorld(Player player) {
	if (player == null)
	    return true;

	return canPerformActionInWorld(player.getWorld());
    }

    public boolean canPerformActionInWorld(World world) {
	return canPerformActionInWorld(world.getName());
    }

    public boolean canPerformActionInWorld(String world) {
	if (world == null || !DisabledWorldsUse)
	    return true;

	if (UseAsWhiteListWorldList) {
	    if (DisabledWorldsList.isEmpty()) {
		return false;
	    }

	    if (DisabledWorldsList.contains(world)) {
		return true;
	    }

	    return false;
	}

	if (DisabledWorldsList.isEmpty())
	    return true;

	if (DisabledWorldsList.contains(world))
	    return false;

	return true;
    }

    public synchronized void reload() {
	// general settings
	loadGeneralSettings();
	Jobs.getJobsDAO().cleanJobs();
	Jobs.getJobsDAO().cleanUsers();
	// Load locale
	Jobs.getLanguageManager().load();
	// title settings
	Jobs.gettitleManager().load();
	// restricted areas
	Jobs.getRestrictedAreaManager().load();
	// restricted blocks
	Jobs.getRestrictedBlockManager().load();
	// Item/Block/mobs name list
	Jobs.getNameTranslatorManager().load();
	// signs information
	Jobs.getSignUtil().LoadSigns();
	// Shop
	Jobs.getShopManager().load();
    }

    /**
     * Method to load the general configuration
     * 
     * loads from Jobs/generalConfig.yml
     */
    private synchronized void loadGeneralSettings() {
	try {
	    c = new ConfigReader("generalConfig.yml");
	} catch (Exception t) {
	    t.printStackTrace();
	}
	if (c == null)
	    return;

	c.header(Arrays.asList("General configuration.",
	    "  The general configuration for the jobs plugin mostly includes how often the plugin",
	    "  saves user data (when the user is in the game), the storage method, whether",
	    "  to broadcast a message to the server when a user goes up a skill level.",
	    "  It also allows admins to set the maximum number of jobs a player can have at",
	    "  any one time."));
	c.copyDefaults(true);
	c.addComment("locale-language", "Default language.", "Example: en, ru", "File in locale folder with same name should exist. Example: messages_ru.yml");

	localeString = c.get("locale-language", "en");
	try {
	    int i = localeString.indexOf('_');
	    if (i == -1)
		locale = new Locale(localeString);
	    else
		locale = new Locale(localeString.substring(0, i), localeString.substring(i + 1));
	} catch (IllegalArgumentException e) {
	    locale = Locale.getDefault();
	    Jobs.getPluginLogger().warning("Invalid locale \"" + localeString + "\" defaulting to " + locale.getLanguage());
	}

	Jobs.getDBManager().start();

	c.addComment("save-period", "How often in minutes you want it to save. This must be a non-zero number");
	c.get("save-period", 10);
	if (c.getInt("save-period") <= 0) {
	    Jobs.getPluginLogger().severe("Save period must be greater than 0! Defaulting to 10 minutes!");
	    c.set("save-period", 10);
	}
	savePeriod = c.getInt("save-period");

	c.addComment("save-on-disconnect", "Should player data be saved on disconnect?",
	    "Player data is always periodically auto-saved and autosaved during a clean shutdown.",
	    "Only enable this if you have a multi-server setup, or have a really good reason for enabling this.", "Turning this on will decrease database performance.");
	saveOnDisconnect = c.get("save-on-disconnect", false);

	c.addComment("selectionTool", "Tool used when selecting bounds for restricted area.");
	getSelectionTool = c.get("selectionTool", "golden_hoe");

	c.addComment("MultiServerCompatability", "Enable if you are using one data base for multiple servers across bungee network",
	    "This will force to load players data every time he is logging in to have most up to date data instead of having preloaded data",
	    "This will enable automatically save-on-disconnect feature");
	MultiServerCompatability = c.get("MultiServerCompatability", false);
	if (MultiServerCompatability) {
	    c.set("save-on-disconnect", true);
	    saveOnDisconnect = true;
	}

	c.addComment("Optimizations.NewVersion",
	    "When set to true staff will be informed about new Jobs plugin version", "You need to have jobs.versioncheck permission node");
	ShowNewVersion = c.get("Optimizations.NewVersion", true);

	c.addComment("Optimizations.DecimalPlaces.Money",
	    "Decimal places to be shown");
	DecimalPlacesMoney = "%." + c.get("Optimizations.DecimalPlaces.Money", 2) + "f";
	DecimalPlacesExp = "%." + c.get("Optimizations.DecimalPlaces.Exp", 2) + "f";
	DecimalPlacesPoints = "%." + c.get("Optimizations.DecimalPlaces.Points", 2) + "f";

	c.addComment("Optimizations.DBCleaning.Jobs.Use",
	    "Warning!!! before enabling this feature, please make data base backup, just in case there will be some issues with data base cleaning",
	    "When set to true, jobs data base will be cleaned on each startup to avoid having not used jobs",
	    "keep in mind that this will only clean actual jobs, but not recorded players");
	DBCleaningJobsUse = c.get("Optimizations.DBCleaning.Jobs.Use", false);
	c.addComment("Optimizations.DBCleaning.Jobs.Level", "Any one who has jobs level equal or less then set, hies job will be removed from data base");
	DBCleaningJobsLvl = c.get("Optimizations.DBCleaning.Jobs.Level", 1);

	c.addComment("Optimizations.DBCleaning.Users.Use",
	    "Warning!!! before enabling this feature, please make data base backup, just in case there will be some issues with data base cleaning",
	    "When set to true, data base will be cleaned on each startup from user data to avoid having old player data");
	DBCleaningUsersUse = c.get("Optimizations.DBCleaning.Users.Use", false);
	c.addComment("Optimizations.DBCleaning.Users.Days", "Any one who not played for defined amount of days, will be removed from data base");
	DBCleaningUsersDays = c.get("Optimizations.DBCleaning.Users.Days", 60);

	c.addComment("Optimizations.AutoJobJoin.Use", "Use or not auto join jobs feature",
	    "If you are not using auto join feature, keep it disabled");
	AutoJobJoinUse = c.get("Optimizations.AutoJobJoin.Use", false);
	c.addComment("Optimizations.AutoJobJoin.Delay", "Delay in seconds to perform auto join job if used after player joins server",
	    "If you using offline server, try to keep it slightly more than your login plugin gives time to enter password",
	    "For player to auto join job add permission node jobs.autojoin.[jobname]",
	    "Op players are ignored");
	AutoJobJoinDelay = c.get("Optimizations.AutoJobJoin.Delay", 15);

	c.addComment("Optimizations.AllowDelevel", "When set to true players who gets negative experience can delevel job up to level 1",
	    "ATTENTION! Set it to true only if you certain that commands performed on level up will not cause issues if player start level and delevel in a row.");
	AllowDelevel = c.get("Optimizations.AllowDelevel", false);

//	c.addComment("Optimizations.UseLocalOfflinePlayersData", "With this set to true, offline player data will be taken from local player data files",
//	    "This will eliminate small lag spikes when request is being send to mojangs servers for offline players data",
//	    "Theroticali this should work without issues, but if you havving some, just disable",
//	    "But then you can feal some small (100-200ms) lag spikes while performings some jobs commands");
//	LocalOfflinePlayersData = c.get("Optimizations.UseLocalOfflinePlayersData", true);

	c.addComment("Optimizations.DisabledWorlds.Use", "By setting this to true, Jobs plugin will be disabled in given worlds",
	    "Only commands can be performed from disabled worlds with jobs.disabledworld.commands permission node");
	DisabledWorldsUse = c.get("Optimizations.DisabledWorlds.Use", false);
	c.addComment("Optimizations.DisabledWorlds.UseAsWhiteList", "If true, will changes the list behavior, so if a world is added to list",
	    "the payments will only works in the given worlds.");
	UseAsWhiteListWorldList = c.get("Optimizations.DisabledWorlds.UseAsWhiteList", false);
	DisabledWorldsList = c.get("Optimizations.DisabledWorlds.List", Arrays.asList(Bukkit.getWorlds().get(0).getName()));

	c.addComment("Optimizations.Explore.Compact",
	    "By setting this to true when there is max amount of players explored a chunk then it will be marked as fully explored and exact players who explored it will not be saved to save some memory");
	ExploreCompact = c.get("Optimizations.Explore.Compact", true);

//	c.addComment("Optimizations.Purge.Use", "By setting this to true, Jobs plugin will clean data base on startup from all jobs with level 1 and at 0 exp");
//	PurgeUse = c.get("Optimizations.Purge.Use", false);

	c.addComment("Logging.Use", "With this set to true all players jobs actions will be logged to database for easy to see statistics",
	    "This is still in development and in future it will expand");
	LoggingUse = c.get("Logging.Use", false);

	c.addComment("broadcast.on-skill-up.use", "Do all players get a message when someone goes up a skill level?");
	isBroadcastingSkillups = c.get("broadcast.on-skill-up.use", false);

	c.addComment("broadcast.on-level-up.use", "Do all players get a message when someone goes up a level?");
	isBroadcastingLevelups = c.get("broadcast.on-level-up.use", false);
	c.addComment("broadcast.on-level-up.levels", "For what levels you want to broadcast message? Keep it at 0 if you want for all of them");
	BroadcastingLevelUpLevels = c.getIntList("broadcast.on-level-up.levels", Arrays.asList(0));

	c.addComment("DailyQuests.ResetTime", "Defines time in 24hour format when we want to give out new daily quests",
	    "Any daily quests given before reset will be invalid and new ones will be given out");
	ResetTimeHour = c.get("DailyQuests.ResetTime.Hour", 4);
	ResetTimeMinute = c.get("DailyQuests.ResetTime.Minute", 0);
	c.addComment("DailyQuests.Skips", "Defines amount of skips player can do on a quest", "This allows player to abandon current quest and get new one");
	DailyQuestsSkips = c.get("DailyQuests.Skips", 1);
	c.addComment("DailyQuests.SkipQuestCost", "The cost of the quest skip (money).", "Default 0, disabling cost of skipping quest.");
	skipQuestCost = c.get("DailyQuests.SkipQuestCost", 0d);

	c.addComment("ScheduleManager", "Enables the schedule manager to boost the server.");
	enableSchedule = c.get("ScheduleManager.Use", true);

	c.addComment("JobExpirationTime", "Fire players if their work time has expired at a job.", "Setting time to 0, will not works.",
	    "For this to work, the player needs to get a new job for the timer to start.", "Counting in hours");
	jobExpiryTime = c.get("JobExpirationTime", 0);

	c.addComment("max-jobs", "Maximum number of jobs a player can join.", "Use 0 for no maximum", "Keep in mind that jobs.max.[amount] will bypass this setting");
	maxJobs = c.get("max-jobs", 3);

	c.addComment("hide-jobs-without-permission", "Hide jobs from player if they lack the permission to join the job");
	hideJobsWithoutPermission = c.get("hide-jobs-without-permission", false);

	c.addComment("hide-jobsinfo-without-permission", "Hide jobs info from player if they lack the permission to join the job");
	hideJobsInfoWithoutPermission = c.get("hide-jobsinfo-without-permission", false);

	c.addComment("enable-pay-near-spawner",
	    "Option to allow payment to be made when killing mobs from a spawner.",
	    "Use jobs.nearspawner.[amount] to define multiplayer. Example jobs.nearspawner.-0.5 will pay half of payment, jobs.nearspawner.-1 will not pay at all");
	payNearSpawner = c.get("enable-pay-near-spawner", false);

	c.addComment("enable-pay-creative", "Option to allow payment to be made in creative mode. This ignoring when a group has 'jobs.paycreative' permission.");
	payInCreative = c.get("enable-pay-creative", false);

	c.addComment("enable-pay-for-exploring-when-flying", "Option to allow payment to be made for exploring when player flies");
	payExploringWhenFlying = c.get("enable-pay-for-exploring-when-flying", false);

	if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R1)) {
	    c.addComment("enable-pay-for-exploring-when-gliding", "Option to allow payment to be made for exploring when player gliding.");
	    payExploringWhenGliding = c.get("enable-pay-for-exploring-when-gliding", false);
	}

	c.addComment("disablePaymentIfRiding", "Disables the payment when the player riding on an entity.");
	disablePaymentIfRiding = c.get("disablePaymentIfRiding", false);

	c.addComment("add-xp-player", "Adds the Jobs xp received to the player's Minecraft XP bar");
	addXpPlayer = c.get("add-xp-player", false);

	if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
	    c.addComment("enable-boosted-items-in-offhand", "Do the jobs boost ignore the boosted items usage in off hand?");
	    boostedItemsInOffHand = c.get("enable-boosted-items-in-offhand", true);
	}

	c.addComment("allow-pay-for-durability-loss", "Allows, when losing maximum durability of item then it does not pay the player until it is repaired.",
	    "E.g. the player wants to enchant a item with enchanting table and the item has durability loss then not paying.");
	c.addComment("allow-pay-for-durability-loss.Use", "Do not disable this if you don't know what mean this option.");
	payItemDurabilityLoss = c.get("allow-pay-for-durability-loss.Use", true);
	c.addComment("allow-pay-for-durability-loss.WhiteListedItems", "What items (tools) are whitelisted the player get paid, when this item has durability loss?",
	    "Enchantments are supported, usage:", "itemName=ENCHANTMENT_NAME-level");
	List<String> tempList = c.get("allow-pay-for-durability-loss.WhiteListedItems",
	    Arrays.asList("wooden_pickaxe=DURABILITY-1", "fishing_rod"));
	whiteListedItems.clear();

	for (String one : tempList) {
	    String mname = one.contains("=") ? one.split("=")[0] : one;
	    String ench = one.contains("=") ? one.split("=")[1] : null;
	    String value = ench != null && ench.contains("-") ? ench.split("-")[1] : null;
	    ench = value != null && ench != null ? ench.substring(0, ench.length() - (value.length() + 1)) : ench;
	    CMIMaterial mat = CMIMaterial.get(mname);
	    if (mat == CMIMaterial.NONE) {
		Jobs.consoleMsg("Failed to recognize " + one + " entry from config file");
		continue;
	    }
	    Enchantment enchant = null;
	    if (ench != null) {
		enchant = CMIEnchantment.getEnchantment(ench);
	    }
	    Integer level = null;
	    if (value != null) {
		try {
		    level = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
	    }
	    HashMap<Enchantment, Integer> submap = new HashMap<>();
	    if (enchant != null)
		submap.put(enchant, level);

	    whiteListedItems.put(mat, submap);
	}

	c.addComment("modify-chat", "Modifys chat to add chat titles. If you're using a chat manager, you may add the tag {jobs} to your chat format and disable this.");
	modifyChat = c.get("modify-chat.use", false);
	modifyChatPrefix = c.get("modify-chat.prefix", "&c[");
	modifyChatSuffix = c.get("modify-chat.suffix", "&c]&r ");
	modifyChatSeparator = c.get("modify-chat.separator", " ");

	c.addComment("UseCustomNames", "Do you want to use custom item/block/mob/enchant/color names?",
	    "With this set to true names like Stone:1 will be translated to Granite", "Name list is in TranslatableWords.yml file");
	UseCustomNames = c.get("UseCustomNames", true);

	c.addComment("economy-batch-delay", "Changes how often, in seconds, players are paid out.  Default is 5 seconds.",
	    "Setting this too low may cause tick lag.  Increase this to improve economy performance (at the cost of delays in payment)");
	economyBatchDelay = c.get("economy-batch-delay", 5);

	c.addComment("economy-async", "Enable async economy calls.", "Disable this if you have issues with payments or your plugin is not thread safe.");
	economyAsync = c.get("economy-async", true);

	c.addComment("Economy.PaymentMethods",
	    "By disabling one of these, players no longer will get particular payment.",
	    "Useful for removing particular payment method without editing whole jobConfig file");
	PaymentMethodsMoney = c.get("Economy.PaymentMethods.Money", true);
	PaymentMethodsPoints = c.get("Economy.PaymentMethods.Points", true);
	PaymentMethodsExp = c.get("Economy.PaymentMethods.Exp", true);

	c.addComment("Economy.GeneralMulti",
	    "Can be used to change payment amounts for all jobs and all actions if you want to readjust them",
	    "Amounts are in percentage, above 0 will increase payments",
	    "Amount belove 0 will decrease payments",
	    "If action pays negative amount, then value above 0 will increase that negative value",
	    "So if placing diamond ore takes 10 bucks from you, then by setting 50 for money income, you will be charged 15 bucks for placing it",
	    "If you are getting paid 10 for placing wood, then same value of 50 for money income, will result in you getting 15 bucks",
	    "This only effects base income value");
	for (CurrencyType one : CurrencyType.values()) {
	    generalMulti.put(one, c.get("Economy.GeneralMulti." + one.name(), 0D) / 100D);
	}

	c.addComment("Economy.ApplyToNegativeIncome",
	    "When set to true income which is belove 0 will get bonus aplied to it",
	    "In example, if you would loose 5 bucks for placing diamond block, with 100% payment bonus, that penalty disapears",
	    "When this left at false penalty for action will remain unchanged");
	applyToNegativeIncome = c.get("Economy.ApplyToNegativeIncome", false);

	c.addComment("Economy.MinimumOveralPayment.use",
	    "Determines minimum payment. In example if player uses McMMO treefeller and earns only 20%, but at same time he gets 25% penalty from dynamic payment. He can 'get' negative amount of money",
	    "This will limit it to particular percentage", "Works only when original payment is above 0");
	useMinimumOveralPayment = c.get("Economy.MinimumOveralPayment.use", true);
	MinimumOveralPaymentLimit = c.get("Economy.MinimumOveralPayment.limit", 0.1);
	c.addComment("Economy.MinimumOveralPoints.use",
	    "Determines minimum payment. In example if player uses McMMO treefeller and earns only 20%, but at same time he gets 25% penalty from dynamic payment. He can 'get' negative amount of money",
	    "This will limit it to particular percentage", "Works only when original payment is above 0");
	useMinimumOveralPoints = c.get("Economy.MinimumOveralPoints.use", true);
	MinimumOveralPointsLimit = c.get("Economy.MinimumOveralPoints.limit", 0.1);

	c.addComment("Economy.DynamicPayment.use", "Do you want to use dynamic payment dependent on how many players already working for jobs?",
	    "This can help automatically lift up payments for not so popular jobs and lower for most popular ones");
	useDynamicPayment = c.get("Economy.DynamicPayment.use", false);

	c.addComment("Economy.DynamicPayment.equation", "totalworkers: The total number of players on the server who have jobs",
	    "totaljobs: The number of jobs that are enabled",
	    "jobstotalplayers: The number of people in that particular job",
	    "Exponential equation: totalworkers / totaljobs / jobstotalplayers - 1",
	    "Linear equation: ((totalworkers / totaljobs) - jobstotalplayers)/10.0");
	String maxExpEquationInput = c.get("Economy.DynamicPayment.equation", "totalworkers / totaljobs / jobstotalplayers - 1");
	try {
	    DynamicPaymentEquation = new Parser(maxExpEquationInput);
	    // test equation
	    DynamicPaymentEquation.setVariable("totalworkers", 100);
	    DynamicPaymentEquation.setVariable("totaljobs", 10);
	    DynamicPaymentEquation.setVariable("jobstotalplayers", 10);
	    DynamicPaymentEquation.getValue();
	} catch (Throwable e) {
	    Jobs.consoleMsg("&cDynamic payment equation has an invalid property. Disabling feature!");
	    useDynamicPayment = false;
	}

	DynamicPaymentMaxPenalty = c.get("Economy.DynamicPayment.MaxPenalty", 50.0);
	DynamicPaymentMaxBonus = c.get("Economy.DynamicPayment.MaxBonus", 300.0);
	c.addComment("Economy.MaxPayment.curve.use", "Enabling this feature will mean players will still earn once they reach cap but " +
	    "will loose a percentage the higher over cap they go. Controlled by a factor. math is ```100/((1/factor*percentOver^2)+1)```");
	useMaxPaymentCurve = c.get("Economy.MaxPayment.curve.use", false);
	int temp = c.get("Economy.MaxPayment.curve.factor", 10);
	maxPaymentCurveFactor = ((float) temp) / 1000;
	c.addComment("Economy.UseServerAccount", "Server economy account", "With this enabled, players will get money from defined user (server account)",
	    "If this account don't have enough money to pay for players for, player will get message");
	UseServerAccount = c.get("Economy.UseServerAccount", false);
	c.addComment("Economy.AccountName", "Username should be with Correct capitalization");
	ServerAccountName = c.get("Economy.AccountName", "Server");
	c.addComment("Economy.Taxes.use", "Do you want to use taxes feature for jobs payment?", "You can bypass taxes with permission jobs.tax.bypass");
	UseTaxes = c.get("Economy.Taxes.use", false);
	c.addComment("Economy.Taxes.AccountName", "Username should be with Correct capitalization, it can be same as setup in server account before");
	ServertaxesAccountName = c.get("Economy.Taxes.AccountName", "Server");
	c.addComment("Economy.Taxes.Amount", "Amount in percentage");
	TaxesAmount = c.get("Economy.Taxes.Amount", 15.0);
	c.addComment("Economy.Taxes.TransferToServerAccount", "Do you want to transfer taxes to server account?");
	TransferToServerAccount = c.get("Economy.Taxes.TransferToServerAccount", true);
	c.addComment("Economy.Taxes.TakeFromPlayersPayment",
	    "With this true, taxes will be taken from players payment and he will get less money than its shown in jobs info",
	    "When its false player will get full payment and server account will get taxes amount to hes account");
	TakeFromPlayersPayment = c.get("Economy.Taxes.TakeFromPlayersPayment", false);

	// Money limit
	CurrencyLimit limit = new CurrencyLimit();
	c.addComment("Economy.Limit.Money", "Money gain limit", "With this enabled, players will be limited how much they can make in defined time",
	    "Time in seconds: 60 = 1 min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setEnabled(c.get("Economy.Limit.Money.Use", false));
	List<CurrencyType> list = new ArrayList<>();
	c.addComment("Economy.Limit.Money.StopWithExp", "Do you want to stop money gain when exp limit reached?");
	if (c.get("Economy.Limit.Money.StopWithExp", false))
	    list.add(CurrencyType.EXP);
	c.addComment("Economy.Limit.Money.StopWithPoint", "Do you want to stop money gain when point limit reached?");
	if (c.get("Economy.Limit.Money.StopWithPoint", false))
	    list.add(CurrencyType.POINTS);
	limit.setStopWith(list);
	c.addComment("Economy.Limit.Money.MoneyLimit",
	    "Equation to calculate max limit. Option to use total level to include players total amount levels of current jobs",
	    "You can always use simple number to set money limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String MoneyLimit = c.get("Economy.Limit.Money.MoneyLimit", "500+500*(totallevel/100)");
	try {
	    Parser Equation = new Parser(MoneyLimit);
	    Equation.setVariable("totallevel", 1);
	    Equation.getValue();
	    limit.setMaxEquation(Equation);
	} catch (Throwable e) {
	    Jobs.getPluginLogger().warning("MoneyLimit has an invalid value. Disabling money limit!");
	    limit.setEnabled(false);
	}
	c.addComment("Economy.Limit.Money.TimeLimit", "Time in seconds: 60 = 1 min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setTimeLimit(c.get("Economy.Limit.Money.TimeLimit", 3600));
	c.addComment("Economy.Limit.Money.AnnouncementDelay", "Delay between announcements about reached money limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	limit.setAnnouncementDelay(c.get("Economy.Limit.Money.AnnouncementDelay", 30));
	currencyLimitUse.put(CurrencyType.MONEY, limit);

	// Point limit
	limit = new CurrencyLimit();
	list = new ArrayList<>();
	c.addComment("Economy.Limit.Point", "Point gain limit", "With this enabled, players will be limited how much they can make in defined time");
	limit.setEnabled(c.get("Economy.Limit.Point.Use", false));
	c.addComment("Economy.Limit.Point.StopWithExp", "Do you want to stop Point gain when exp limit reached?");
	if (c.get("Economy.Limit.Point.StopWithExp", false))
	    list.add(CurrencyType.EXP);
	c.addComment("Economy.Limit.Point.StopWithMoney", "Do you want to stop Point gain when money limit reached?");
	if (c.get("Economy.Limit.Point.StopWithMoney", false))
	    list.add(CurrencyType.MONEY);
	limit.setStopWith(list);
	c.addComment("Economy.Limit.Point.Limit",
	    "Equation to calculate max limit. Option to use total level to include players total amount levels of current jobs",
	    "You can always use simple number to set limit",
	    "Default equation is: 500+500*(totallevel/100), this will add 1% from 500 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 685 limit");
	String PointLimit = c.get("Economy.Limit.Point.Limit", "500+500*(totallevel/100)");
	try {
	    Parser Equation = new Parser(PointLimit);
	    Equation.setVariable("totallevel", 1);
	    Equation.getValue();
	    limit.setMaxEquation(Equation);
	} catch (Throwable e) {
	    Jobs.getPluginLogger().warning("PointLimit has an invalid value. Disabling money limit!");
	    limit.setEnabled(false);
	}
	c.addComment("Economy.Limit.Point.TimeLimit", "Time in seconds: 60 = 1 min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setTimeLimit(c.get("Economy.Limit.Point.TimeLimit", 3600));
	c.addComment("Economy.Limit.Point.AnnouncementDelay", "Delay between announcements about reached limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	limit.setAnnouncementDelay(c.get("Economy.Limit.Point.AnnouncementDelay", 30));
	currencyLimitUse.put(CurrencyType.POINTS, limit);

	// Exp limit
	limit = new CurrencyLimit();
	list = new ArrayList<>();
	c.addComment("Economy.Limit.Exp", "Exp gain limit", "With this enabled, players will be limited how much they can get in defined time",
	    "Time in seconds: 60 = 1 min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setEnabled(c.get("Economy.Limit.Exp.Use", false));
	c.addComment("Economy.Limit.Exp.StopWithMoney", "Do you want to stop exp gain when money limit reached?");
	if (c.get("Economy.Limit.Exp.StopWithMoney", false))
	    list.add(CurrencyType.MONEY);
	c.addComment("Economy.Limit.Exp.StopWithPoint", "Do you want to stop exp gain when point limit reached?");
	if (c.get("Economy.Limit.Exp.StopWithPoint", false))
	    list.add(CurrencyType.POINTS);
	limit.setStopWith(list);
	c.addComment("Economy.Limit.Exp.Limit", "Equation to calculate max money limit. Option to use total level to include players total amount of current jobs",
	    "You can always use simple number to set exp limit",
	    "Default equation is: 5000+5000*(totallevel/100), this will add 1% from 5000 for each level player have",
	    "So player with 2 jobs with level 15 and 22 will have 6850 limit");
	String expLimit = c.get("Economy.Limit.Exp.Limit", "5000+5000*(totallevel/100)");
	try {
	    Parser Equation = new Parser(expLimit);
	    Equation.setVariable("totallevel", 1);
	    Equation.getValue();
	    limit.setMaxEquation(Equation);
	} catch (Throwable e) {
	    Jobs.getPluginLogger().warning("ExpLimit has an invalid value. Disabling money limit!");
	    limit.setEnabled(false);
	}
	c.addComment("Economy.Limit.Exp.TimeLimit", "Time in seconds: 60 = 1 min, 3600 = 1 hour, 86400 = 24 hours");
	limit.setTimeLimit(c.get("Economy.Limit.Exp.TimeLimit", 3600));
	c.addComment("Economy.Limit.Exp.AnnouncementDelay", "Delay between announcements about reached Exp limit",
	    "Keep this from 30 to 5 min (300), as players can get annoyed of constant message displaying");
	limit.setAnnouncementDelay(c.get("Economy.Limit.Exp.AnnouncementDelay", 30));
	currencyLimitUse.put(CurrencyType.EXP, limit);

	c.addComment("Economy.Repair.PayForRenaming", "Do you want to give money for only renaming items in anvil?",
	    "Players will get full pay as they would for remaining two items when they only rename one",
	    "This is not a big issue, but if you want to disable it, you can");
	PayForRenaming = c.get("Economy.Repair.PayForRenaming", true);

	c.addComment("Economy.Enchant.PayForEnchantingOnAnvil", "Do you want to give money for enchanting items in anvil?");
	PayForEnchantingOnAnvil = c.get("Economy.Enchant.PayForEnchantingOnAnvil", false);

	c.addComment("Economy.Crafting.PayForEachCraft",
	    "With this true, player will get money for all crafted items instead of each crafting action (like with old payment mechanic)",
	    "By default its false, as you can make ALOT of money if prices kept from old payment mechanics");
	PayForEachCraft = c.get("Economy.Crafting.PayForEachCraft", false);

	c.addComment("Economy.MilkingCow.CancelMilking", "With this true, when timer is still going, cow milking event will be canceled",
	    "With this false, player will get bucket of milk, but still no payment");
	CancelCowMilking = c.get("Economy.MilkingCow.CancelMilking", false);
	c.addComment("Economy.MilkingCow.Timer",
	    "How ofter player can milk cows in seconds. Keep in mind that by default player can milk cow indefinitely and as often as he wants",
	    "Set to 0 if you want to disable timer");
	CowMilkingTimer = c.get("Economy.MilkingCow.Timer", 30) * 1000;

	c.addComment("ExploitProtections.Furnaces.Reassign",
	    "When enabled, players interacted furnaces will be saved into a file and will be reassigned after restart to keep giving out money",
	    "Players will no longer need to click on furnace to get paid from it after server restart");
	FurnacesReassign = c.get("ExploitProtections.Furnaces.Reassign", true);
	c.addComment("ExploitProtections.Furnaces.MaxDefaultAvailable",
	    "Defines max available furnaces each player can have to get paid from",
	    "This can be overridden with jobs.maxfurnaces.[amount] permission node");
	FurnacesMaxDefault = c.get("ExploitProtections.Furnaces.MaxDefaultAvailable", 20);

	c.addComment("ExploitProtections.BrewingStands.Reassign",
	    "When enabled, players interacted brewing stands will be saved into file and will be reassigned after restart to keep giving out money",
	    "Players will no longer need to click on brewing stand to get paid from it after server restart");
	BrewingStandsReassign = c.get("ExploitProtections.BrewingStands.Reassign", true);
	c.addComment("ExploitProtections.BrewingStands.MaxDefaultAvailable",
	    "Defines max available brewing stands each player can have to get paid from",
	    "Set to 0 if you want to disable this limitation",
	    "This can be overridden with jobs.maxbrewingstands.[amount] permission node");
	BrewingStandsMaxDefault = c.get("ExploitProtections.BrewingStands.MaxDefaultAvailable", 20);

	c.addComment("ExploitProtections.General.PlaceAndBreakProtection",
	    "Enable blocks protection, like ore, from exploiting by placing and destroying same block again and again.",
	    "Modify restrictedBlocks.yml for blocks you want to protect");
	useBlockProtection = c.get("ExploitProtections.General.PlaceAndBreakProtection", true);

	c.addComment("ExploitProtections.General.KeepDataFor",
	    "For how long in days to keep block protection data in data base", "This will clean block data which ones have -1 as cooldown value",
	    "Data base cleanup will be performed on each server startup", "This cant be more then 14 days");
	BlockProtectionDays = c.get("ExploitProtections.General.KeepDataFor", 14);
	BlockProtectionDays = BlockProtectionDays > 14 ? 14 : BlockProtectionDays;

	c.addComment("ExploitProtections.General.GlobalBlockTimer", "All blocks will be protected X sec after player places it on ground.");
	useGlobalTimer = c.get("ExploitProtections.General.GlobalBlockTimer.use", true);
	globalblocktimer = c.get("ExploitProtections.General.GlobalBlockTimer.timer", 3);

	c.addComment("ExploitProtections.General.SilkTouchProtection", "Enable silk touch protection.",
	    "With this enabled players wont get paid for broken blocks from restrictedblocks list with silk touch tool.");
	useSilkTouchProtection = c.get("ExploitProtections.General.SilkTouchProtection", false);

	c.addComment("ExploitProtections.General.MonsterDamage.Use", "This section controls how much damage player should do to monster for player to get paid",
	    "This prevents from killing monsters in one hit when they suffer in example fall damage");
	MonsterDamageUse = c.get("ExploitProtections.General.MonsterDamage.Use", false);
	MonsterDamagePercentage = c.get("ExploitProtections.General.MonsterDamage.Percentage", 60);

	c.addComment("ExploitProtections.McMMO", "McMMO abilities");
	c.addComment("ExploitProtections.McMMO.TreeFellerMultiplier", "Players will get part of money from cutting trees with treefeller ability enabled.",
	    "0.2 means 20% of original price");
	TreeFellerMultiplier = c.get("ExploitProtections.McMMO.TreeFellerMultiplier", 0.2);
	c.addComment("ExploitProtections.McMMO.gigaDrillMultiplier", "Players will get part of money from braking blocks with gigaDrill ability enabled.",
	    "0.2 means 20% of original price");
	gigaDrillMultiplier = c.get("ExploitProtections.McMMO.gigaDrillMultiplier", 0.2);
	c.addComment("ExploitProtections.McMMO.superBreakerMultiplier", "Players will get part of money from braking blocks with super breaker ability enabled.",
	    "0.2 means 20% of original price");
	superBreakerMultiplier = c.get("ExploitProtections.McMMO.superBreakerMultiplier", 0.2);

	c.addComment("ExploitProtections.MythicMobs", "MythicMobs plugin support", "Disable if you having issues with it or using old version");
	MythicMobsEnabled = c.get("ExploitProtections.MythicMobs.enabled", true);

	c.addComment("ExploitProtections.Spawner.PreventSlimeSplit", "Prevent slime splitting when they are from spawner",
	    "Protects agains exploiting as new splited slimes is treated as naturally spawned and not from spawner");
	PreventSlimeSplit = c.get("ExploitProtections.Spawner.PreventSlimeSplit", true);
	c.addComment("ExploitProtections.Spawner.PreventMagmaCubeSplit", "Prevent magmacube splitting when they are from spawner");
	PreventMagmaCubeSplit = c.get("ExploitProtections.Spawner.PreventMagmaCubeSplit", true);

	c.addComment("ExploitProtections.Smelt.PreventHopperFillUps", "Prevent payments when hoppers moving items into furnace", "Player will not get paid, but items will be smelted");
	PreventHopperFillUps = c.get("ExploitProtections.Smelt.PreventHopperFillUps", true);
	c.addComment("ExploitProtections.Smelt.PreventMagmaCubeSplit", "Prevent payments when hoppers moving items into brewing stands",
	    "Player will not get paid, but items will be brewd as they supose too");
	PreventBrewingStandFillUps = c.get("ExploitProtections.Brew.PreventBrewingStandFillUps", true);

	c.addComment("use-breeder-finder", "Breeder finder.",
	    "If you are not using breeding payment, you can disable this to save little resources. Really little.");
	useBreederFinder = c.get("use-breeder-finder", true);

	c.addComment("old-job",
	    "Old job save", "Players can leave job and return later with some level loss during that",
	    "You can fix players level if his job level is at max level");
	levelLossPercentage = c.get("old-job.level-loss-percentage", 30);
	fixAtMaxLevel = c.get("old-job.fix-at-max-level", true);
	c.addComment("old-job.level-loss-from-max-level",
	    "Percentage to loose when leaving job at max level",
	    "Only works when fix-at-max-level is set to false");
	levelLossPercentageFromMax = c.get("old-job.level-loss-from-max-level", levelLossPercentage);

	c.addComment("ActionBars.Messages.EnabledByDefault", "When this set to true player will see action bar messages by default");
	ActionBarsMessageByDefault = c.get("ActionBars.Messages.EnabledByDefault", true);

	if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R1)) {
	    c.addComment("BossBar.Enabled", "Enables BossBar feature", "Works only from 1.9 mc version");
	    BossBarEnabled = c.get("BossBar.Enabled", true);

	    c.addComment("BossBar.Messages.EnabledByDefault", "When this set to true player will see Bossbar messages by default");
	    BossBarsMessageByDefault = c.get("BossBar.Messages.EnabledByDefault", true);

	    c.addComment("BossBar.ShowOnEachAction", "If enabled boss bar will update after each action",
		"If disabled, BossBar will update only on each payment. This can save some server resources");
	    BossBarShowOnEachAction = c.get("BossBar.ShowOnEachAction", false);
	    c.addComment("BossBar.SegementCount", "Defines in how many parts bossabr will be split visually","Valid options: 1, 6, 10, 12, 20");
	    SegementCount = c.get("BossBar.SegementCount", 1);
	    c.addComment("BossBar.Timer", "How long in sec to show BossBar for player",
		"If you have disabled ShowOnEachAction, then keep this number higher than payment interval for better experience");
	    BossBarTimer = c.get("BossBar.Timer", economyBatchDelay + 1);
	}

	c.addComment("ShowActionBars", "You can enable/disable message shown for players in action bar");
	TitleChangeActionBar = c.get("ShowActionBars.OnTitleChange", true);
	LevelChangeActionBar = c.get("ShowActionBars.OnLevelChange", true);
	EmptyServerAccountActionBar = c.get("ShowActionBars.OnEmptyServerAccount", true);

	c.addComment("ShowChatMessage", "Chat messages", "You can enable/disable message shown for players in chat");
	TitleChangeChat = c.get("ShowChatMessage.OnTitleChange", true);
	LevelChangeChat = c.get("ShowChatMessage.OnLevelChange", true);
	EmptyServerAccountChat = c.get("ShowChatMessage.OnEmptyServerAccount", true);

	c.addComment("Sounds", "Sounds", "Extra sounds on some events",
	    "All sounds can be found in https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
	SoundLevelupUse = c.get("Sounds.LevelUp.use", true);
	SoundLevelupSound = c.get("Sounds.LevelUp.sound", Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_9_R1) ? "LEVEL_UP " : "ENTITY_PLAYER_LEVELUP");
	SoundLevelupVolume = c.get("Sounds.LevelUp.volume", 1);
	SoundLevelupPitch = c.get("Sounds.LevelUp.pitch", 3);
	SoundTitleChangeUse = c.get("Sounds.TitleChange.use", true);
	SoundTitleChangeSound = c.get("Sounds.TitleChange.sound", Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_9_R1) ? "LEVEL_UP " : "ENTITY_PLAYER_LEVELUP");
	SoundTitleChangeVolume = c.get("Sounds.TitleChange.volume", 1);
	SoundTitleChangePitch = c.get("Sounds.TitleChange.pitch", 3);

	c.addComment("Fireworks", "Extra firework shooting in some events");
	FireworkLevelupUse = c.get("Fireworks.LevelUp.use", false);
	c.addComment("Fireworks.LevelUp.Random", "Makes the firework to randomize, such as random colors, type, power and so on.",
	    "These are under settings will not be work, when this enabled.");
	UseRandom = c.get("Fireworks.LevelUp.Random", true);
	UseFlicker = c.get("Fireworks.LevelUp.flicker", true);
	UseTrail = c.get("Fireworks.LevelUp.trail", true);
	c.addComment("Fireworks.LevelUp.type", "Firework types",
	    "All types can be found in https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/FireworkEffect.Type.html");
	FireworkType = c.get("Fireworks.LevelUp.type", "STAR");
	FwColors = c.get("Fireworks.LevelUp.colors", Arrays.asList("230,0,0", "0,90,0", "0,0,104"));
	FireworkPower = c.get("Fireworks.LevelUp.power", 1);
	c.addComment("Fireworks.LevelUp.ShootTime", "Fire shooting time in ticks.", "Example: 20 tick = 1 second");
	ShootTime = c.get("Fireworks.LevelUp.ShootTime", 20);

	c.addComment("Signs", "You can disable this to save SMALL amount of server resources");
	SignsEnabled = c.get("Signs.Enable", true);
	SignsColorizeJobName = c.get("Signs.Colors.ColorizeJobName", true);
	c.addComment("Signs.InfoUpdateInterval",
	    "This is interval in sec in which signs will be updated. This is not continues update, signs are updated only on levelup, job leave, job join or similar action.");
	c.addComment("Signs.InfoUpdateInterval",
	    "This is update for same job signs, to avoid huge lag if you have bunch of same type signs. Keep it from 1 to as many sec you want");
	InfoUpdateInterval = c.get("Signs.InfoUpdateInterval", 5);

	c.addComment("Scoreboard.ShowToplist", "This will enable to show top list in scoreboard instead of chat");
	ShowToplistInScoreboard = c.get("Scoreboard.ShowToplist", true);

	c.addComment("Scoreboard.interval", "For how long to show scoreboard");
	ToplistInScoreboardInterval = c.get("Scoreboard.interval", 10);

	c.addComment("JobsBrowse.ShowTotalWorkers", "Do you want to show total amount of workers for job in jobs browse window?");
	ShowTotalWorkers = c.get("JobsBrowse.ShowTotalWorkers", true);
	c.addComment("JobsBrowse.ShowPenaltyBonus", "Do you want to show penalty and bonus in jobs browse window? Only works if this feature is enabled");
	ShowPenaltyBonus = c.get("JobsBrowse.ShowPenaltyBonus", true);

	c.addComment("JobsBrowse.UseNewLook", "Defines if you want to use new /jobs browse look or old one");
	BrowseUseNewLook = c.get("JobsBrowse.UseNewLook", true);
	c.addComment("JobsBrowse.AmountToShow", "Defines amount of jobs to be shown in one page for /jobs browse");
	BrowseAmountToShow = c.get("JobsBrowse.AmountToShow", 5);

	c.addComment("JobsGUI.OpenOnBrowse", "Do you want to show GUI when performing /jobs browse command?");
	JobsGUIOpenOnBrowse = c.get("JobsGUI.OpenOnBrowse", true);
	c.addComment("JobsGUI.ShowChatBrowse", "Do you want to show chat information when performing /jobs browse command?");
	JobsGUIShowChatBrowse = c.get("JobsGUI.ShowChatBrowse", true);
	c.addComment("JobsGUI.SwitcheButtons", "With true left mouse button will join job and right will show more info.",
	    "With false left mouse button will show more info, right will join job or inversely.", "Don't forget to adjust locale file!");
	JobsGUISwitcheButtons = c.get("JobsGUI.SwitcheButtons", false);
	c.addComment("JobsGUI.UseInversedClickToLeave", "Do you want to use the left/right click button to leave from job?",
	    "This option click type depend from SwitcheButtons option, if true using the left button and inversely.",
	    "Don't forget to adjust locale file");
	UseInversedClickToLeave = c.get("JobsGUI.UseInversedClickToLeave", false);
	c.addComment("JobsGUI.DisableJoiningJobThroughGui", "Allows players to join a specified job via GUI.");
	DisableJoiningJobThroughGui = c.get("JobsGUI.DisableJoiningJobThroughGui", false);
	c.addComment("JobsGUI.ShowActionNames", "Do you want to show the action names in GUI?");
	ShowActionNames = c.get("JobsGUI.ShowActionNames", true);
	c.addComment("JobsGUI.Rows", "Defines size in rows of GUI");
	JobsGUIRows = c.get("JobsGUI.Rows", 5);
	c.addComment("JobsGUI.BackButtonSlot", "Defines back button slot in GUI");
	JobsGUIBackButton = c.get("JobsGUI.BackButtonSlot", 37);
	c.addComment("JobsGUI.StartPosition", "Defines start position in gui from which job icons will be shown");
	JobsGUIStartPosition = c.get("JobsGUI.StartPosition", 11);
	c.addComment("JobsGUI.GroupAmount", "Defines by how many jobs we need to group up");
	JobsGUIGroupAmount = c.get("JobsGUI.GroupAmount", 7);
	c.addComment("JobsGUI.SkipAmount", "Defines by how many slots we need to skip after group");
	JobsGUISkipAmount = c.get("JobsGUI.SkipAmount", 2);

	c.addComment("Commands.FilterHiddenPlayersInTabComplete", "Do you want to filter the hidden player names from tab-complete?");
	FilterHiddenPlayerFromTabComplete = c.get("Commands.FilterHiddenPlayersInTabComplete", false);
	c.addComment("Commands.PageRow.JobsTop.AmountToShow", "Defines amount of players to be shown in one page for /jobs top & /jobs gtop");
	JobsTopAmount = c.get("Commands.PageRow.JobsTop.AmountToShow", 15);
	c.addComment("Commands.PageRow.Placeholders.AmountToShow", "Defines amount of placeholders to be shown in one page for /jobs placeholders");
	PlaceholdersPage = c.get("Commands.PageRow.Placeholders.AmountToShow", 10);
	c.addComment("Commands.JobsLeave.UsePerPermissionLeave", "Defines how job leave works.",
	    "If this is true, then the user must have another permission to leave the job. jobs.command.leave.jobName");
	UsePerPermissionForLeaving = c.get("Commands.JobsLeave.UsePerPermissionLeave", false);
	c.addComment("Commands.JobsLeave.EnableConfirmation", "Allows to confirm the /jobs leave and leaveall commands, to confirm the leave.");
	EnableConfirmation = c.get("Commands.JobsLeave.EnableConfirmation", false);
	c.addComment("Commands.JobsLeave.ConfirmExpiryTime", "Specify the confirm expiry time.", "Time in seconds.");
	ConfirmExpiryTime = c.get("Commands.JobsLeave.ConfirmExpiryTime", 10);

	CMIMaterial tmat = null;
	tmat = CMIMaterial.get(c.get("JobsGUI.BackButton.Material", "JACK_O_LANTERN").toUpperCase());
	guiBackButton = tmat == null ? CMIMaterial.JACK_O_LANTERN.newItemStack() : tmat.newItemStack();

	tmat = CMIMaterial.get(c.get("JobsGUI.Filler.Material", "GREEN_STAINED_GLASS_PANE").toUpperCase());
	guiFiller = tmat == null ? CMIMaterial.GREEN_STAINED_GLASS_PANE.newItemStack() : tmat.newItemStack();

//	c.addComment("Schedule.Boost.Enable", "Do you want to enable scheduler for global boost?");
//	useGlobalBoostScheduler = c.get("Schedule.Boost.Enable", false);

	c.save();
    }

    public String getSelectionTool() {
	return getSelectionTool;
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

    public ConfigReader getConfig() {
	return c;
    }

    public int getDailyQuestsSkips() {
	return DailyQuestsSkips;
    }

}
