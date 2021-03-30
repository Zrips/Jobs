package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.stuff.Util;

public class LanguageManager {

    public final List<String> signKeys = new ArrayList<>();

    private List<String> languages = new ArrayList<>();

    public List<String> getLanguages() {
	return languages;
    }

    /**
     * Method to load the language file configuration
     * 
     * loads from Jobs/locale/messages_en.yml
     */
    void load() {
	// This should be present to copy over default locale files into locale folder if file doesn't exist. Grabs all files from plugin file.
	languages = new ArrayList<>();
	try {
	    languages.addAll(Util.getFilesFromPackage("locale", "messages_", "yml"));
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	for (Iterator<String> e1 = this.languages.iterator(); e1.hasNext();) {
	    String lang = e1.next();
	    YmlMaker langFile = new YmlMaker(Jobs.getFolder(), "locale" + File.separator + "messages_" + lang + ".yml");
	    langFile.saveDefaultConfig();
	}
	//Up to here.

	String ls = Jobs.getGCManager().localeString;
	if (ls.isEmpty())
	    ls = "en";

	languages.clear();
	languages.add("en");

	File customLocaleFile = new File(Jobs.getFolder(), "locale" + File.separator + "messages_" + ls + ".yml");
	if (!customLocaleFile.exists() && !ls.equalsIgnoreCase("en"))
	    languages.add(ls);

	for (String lang : languages) {
	    File f = new File(Jobs.getFolder(), "locale" + File.separator + "messages_" + lang + ".yml");

	    // Fail safe if file get corrupted and being created with corrupted data, we need to recreate it
	    if ((f.length() / 1024) > 1024) {
		f.delete();
		f = new File(Jobs.getFolder(), "locale" + File.separator + "messages_" + lang + ".yml");
	    }

	    ConfigReader c = new ConfigReader(f);
	    c.copyDefaults(true);

	    Jobs.getGCManager().getCommandArgs().clear();

	    c.get("economy.error.nomoney", "&cSorry, no money left in national bank!");
	    c.get("limitedItem.error.levelup", "&cYou need to level up in [jobname] to use this item!");
	    c.get("general.info.toplineseparator", "&7*********************** &6%playername% &7***********************");
	    c.get("general.info.separator", "&7*******************************************************");
	    c.get("general.info.time.days", "&e%days% &6days ");
	    c.get("general.info.time.hours", "&e%hours% &6hours ");
	    c.get("general.info.time.mins", "&e%mins% &6min ");
	    c.get("general.info.time.secs", "&e%secs% &6sec ");
	    c.get("general.info.invalidPage", "&cInvalid page");
	    c.get("general.info.true", "&2True");
	    c.get("general.info.false", "&cFalse");
	    c.get("general.info.blocks.furnace", "Furnace");
	    c.get("general.info.blocks.smoker", "Smoker");
	    c.get("general.info.blocks.blastfurnace", "Blast furnace");
	    c.get("general.info.blocks.brewingstand", "Brewing stand");
	    c.get("general.admin.error", "&cThere was an error in the command.");
	    c.get("general.admin.success", "&eYour command has been performed.");
	    c.get("general.error.noHelpPage", "&cThere is no help page by this number!");
	    c.get("general.error.notNumber", "&ePlease use numbers!");
	    c.get("general.error.job", "&cThe job you selected does not exist or you not joined to this!");
	    c.get("general.error.noCommand", "&cThere is no command by this name!");
	    c.get("general.error.permission", "&cYou do not have permission to do that!");
	    c.get("general.error.noinfo", "&cNo information found!");
	    c.get("general.error.noinfoByPlayer", "&cNo information found by [%playername%] player name!");
	    c.get("general.error.ingame", "&cYou can use this command only in game!");
	    c.get("general.error.fromconsole", "&cYou can use this command only from console!");
	    c.get("general.error.worldisdisabled", "&cYou can't use command in this world!");

	    c.get("general.error.newRegistration", "&eRegistered new ownership for [block] &7[current]&e/&f[max]");
	    c.get("general.error.noRegistration", "&cYou've reached max [block] count!");

	    c.get("command.help.output.info", "Type /jobs [cmd] ? for more information about a command.");
	    c.get("command.help.output.cmdUsage", "&2Usage: &7[command]");
	    c.get("command.help.output.label", "Jobs");

	    c.get("command.help.output.cmdInfoFormat", "[command] &f- &2[description]");
	    c.get("command.help.output.cmdFormat", "&7/[command] &f[arguments]");
	    c.get("command.help.output.helpPageDescription", "&2* [description]");

	    c.get("command.help.output.title", "&e-------&e ======= &6Jobs &e======= &e-------");
	    c.get("command.help.output.page", "&e-----&e ====== Page &6[1] &eof &6[2] &e====== &e-----");
	    c.get("command.help.output.fliperSimbols", "&e----------");

	    c.get("command.help.output.prevPage", "&2----<< &6Prev ");
	    c.get("command.help.output.prevPageOff", "&7----<< Prev ");
	    c.get("command.help.output.nextPage", "&6 Next &2>>----");
	    c.get("command.help.output.nextPageOff", "&7 Next >>----");
	    c.get("command.help.output.pageCount", "&2[current]/[total]");
	    c.get("command.help.output.pageCountHover", "&e[totalEntries] entries");
	    c.get("command.help.output.prevPageGui", "&6Previous page ");
	    c.get("command.help.output.nextPageGui", "&6Next Page");

	    c.get("command.moneyboost.help.info", "Boosts money gain for all players");
	    c.get("command.moneyboost.help.args", "[jobname]/all/reset [time]/[rate]");
	    Jobs.getGCManager().getCommandArgs().put("moneyboost", Arrays.asList("[jobname]%%all%%reset", "[time]%%[rate]"));
	    c.get("command.moneyboost.output.allreset", "All money boosts turned off");
	    c.get("command.moneyboost.output.jobsboostreset", "Money boost has been turned off for %jobname%");
	    c.get("command.moneyboost.output.nothingtoreset", "Nothing to reset");
	    c.get("command.moneyboost.output.boostalladded", "Money boost of %boost% added for all jobs!");
	    c.get("command.moneyboost.output.boostadded", "Money boost of &e%boost% &aadded for &e%jobname%!");
	    c.get("command.moneyboost.output.infostats", "&c-----> &aMoney rate x%boost% enabled&c <-------");

	    c.get("command.pointboost.help.info", "Boosts point gain for all players");
	    c.get("command.pointboost.help.args", "[jobname]/all/reset [time]/[rate]");
	    Jobs.getGCManager().getCommandArgs().put("pointboost", Arrays.asList("[jobname]%%all%%reset", "[time]%%[rate]"));
	    c.get("command.pointboost.output.allreset", "All point boosts turned off");
	    c.get("command.pointboost.output.jobsboostreset", "Point boost has been turned off for %jobname%");
	    c.get("command.pointboost.output.nothingtoreset", "Nothing to reset");
	    c.get("command.pointboost.output.boostalladded", "Points boost of %boost% added for all jobs!");
	    c.get("command.pointboost.output.boostadded", "Points boost of &e%boost% &aadded for &e%jobname%!");
	    c.get("command.pointboost.output.infostats", "&c-----> &aPoints rate x%boost% enabled&c <-------");

	    c.get("command.expboost.help.info", "Boosts exp gain for all players");
	    c.get("command.expboost.help.args", "[jobname]/all/reset [time]/[rate]");
	    Jobs.getGCManager().getCommandArgs().put("expboost", Arrays.asList("[jobname]%%all%%reset", "[time]%%[rate]"));
	    c.get("command.expboost.output.allreset", "All exp boosts turned off");
	    c.get("command.expboost.output.jobsboostreset", "Exp boost has been turned off for %jobname%");
	    c.get("command.expboost.output.nothingtoreset", "Nothing to reset");
	    c.get("command.expboost.output.boostalladded", "Exp boost of %boost% added for all jobs!");
	    c.get("command.expboost.output.boostadded", "Exp boost of &e%boost% &aadded for &e%jobname%!");
	    c.get("command.expboost.output.infostats", "&c-----> &aExp rate x%boost% enabled&c <-------");

	    c.get("command.schedule.help.info", "Enables the given scheduler");
	    c.get("command.schedule.help.args", "enable [scheduleName] [untilTime]");
	    Jobs.getGCManager().getCommandArgs().put("schedule", Arrays.asList("enable", "[scheduleName]", "[untilTime]"));
	    c.get("command.schedule.output.noScheduleFound", "&cSchedule with this name not found.");
	    c.get("command.schedule.output.alreadyEnabled", "&cThis schedule already enabled.");
	    c.get("command.schedule.output.enabled", "&eSchedule have been enabled from&a %from%&e until&a %until%");

	    c.get("command.itembonus.help.info", "Check item bonus");
	    c.get("command.itembonus.help.args", "");
	    c.get("command.itembonus.output.list", "&e[jobname]: %money% %points% %exp%");
	    c.get("command.itembonus.output.notAplyingList", "&7[jobname]: %money% %points% %exp%");
	    c.get("command.itembonus.output.hover", "&7%itemtype%");
	    c.get("command.itembonus.output.hoverLevelLimits", "&7From level: %from% \n&7Until level: %until%");

	    c.get("command.edititembonus.help.info", "Edit item boost bonus");
	    c.get("command.edititembonus.help.args", "list/add/remove [jobname] [itemBoostName]");
	    Jobs.getGCManager().getCommandArgs().put("edititembonus", Arrays.asList("list%%add%%remove", "[boosteditems]"));

	    c.get("command.bonus.help.info", "Show job bonuses");
	    c.get("command.bonus.help.args", "[jobname]");
	    Jobs.getGCManager().getCommandArgs().put("bonus", Arrays.asList("[jobname]"));
	    c.get("command.bonus.output.topline", "&7**************** &2[money] &6[points] &e[exp] &7****************");
	    c.get("command.bonus.output.permission", " &ePerm bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.item", " &eItem bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.global", " &eGlobal bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.dynamic", " &eDynamic bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.nearspawner", " &eSpawner bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.petpay", " &ePetPay bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.area", " &eArea bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.mcmmo", " &eMcMMO bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.final", " &eFinal bonus: &2%money% &6%points% &e%exp%");
	    c.get("command.bonus.output.finalExplanation", " &eDoes not include Petpay and Near spawner bonus/penalty");

	    c.get("command.convert.help.info",
		"Converts the database system from one system to another. If you are currently running SQLite, this will convert it to MySQL and vice versa.");
	    c.get("command.convert.help.args", "");

	    c.get("command.limit.help.info", "Shows payment limits for jobs");
	    c.get("command.limit.help.args", "[playername]");
	    c.get("command.limit.output.moneytime", "&eTime left until money limit resets: &2%time%");
	    c.get("command.limit.output.moneyLimit", "&eMoney limit: &2%current%&e/&2%total%");
	    c.get("command.limit.output.exptime", "&eTime left until Exp limit resets: &2%time%");
	    c.get("command.limit.output.expLimit", "&eExp limit: &2%current%&e/&2%total%");
	    c.get("command.limit.output.pointstime", "&eTime left until Point limit resets: &2%time%");
	    c.get("command.limit.output.pointsLimit", "&ePoint limit: &2%current%&e/&2%total%");
	    c.get("command.limit.output.reachedmoneylimit", "&4You have reached money limit in given time!");
	    c.get("command.limit.output.reachedmoneylimit2", "&eYou can check your limit with &2/jobs limit &ecommand");
	    c.get("command.limit.output.reachedmoneylimit3", "&eMoney earned is now reduced exponentially... But you still earn a little!");
	    c.get("command.limit.output.reachedexplimit", "&4You have reached exp limit in given time!");
	    c.get("command.limit.output.reachedexplimit2", "&eYou can check your limit with &2/jobs limit &ecommand");
	    c.get("command.limit.output.reachedpointslimit", "&4You have reached exp limit in given time!");
	    c.get("command.limit.output.reachedpointslimit2", "&eYou can check your limit with &2/jobs limit &ecommand");
	    c.get("command.limit.output.notenabled", "&eMoney limit is not enabled");

	    c.get("command.resetlimit.help.info", "Resets a player's payment limits");
	    c.get("command.resetlimit.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("resetlimit", Arrays.asList("[playername]"));
	    c.get("command.resetlimit.output.reseted", "&ePayment limits have been reset for: &2%playername%");

	    c.get("command.resetquest.help.info", "Resets a player's quest");
	    c.get("command.resetquest.help.args", "[playername] [jobname]");
	    Jobs.getGCManager().getCommandArgs().put("resetquest", Arrays.asList("[playername]", "[jobname]"));
	    c.get("command.resetquest.output.reseted", "&eQuest has been reset for: &2%playername%");
	    c.get("command.resetquest.output.noQuests", "&eCan't find any quests");

	    c.get("command.points.help.info", "Shows how much points does a player have.");
	    c.get("command.points.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("points", Arrays.asList("[playername]"));
	    c.get("command.points.currentpoints", " &eCurrent point amount: &6%currentpoints%");
	    c.get("command.points.totalpoints", " &eTotal amount of collected points until now: &6%totalpoints%");

	    c.get("command.editpoints.help.info", "Edit player's points.");
	    c.get("command.editpoints.help.args", "set/add/take [playername] [amount]");
	    Jobs.getGCManager().getCommandArgs().put("editpoints", Arrays.asList("set%%add%%take", "[playername]"));
	    c.get("command.editpoints.output.set", "&ePlayers (&6%playername%&e) points was set to &6%amount%");
	    c.get("command.editpoints.output.add", "&ePlayer (&6%playername%&e) got additional &6%amount% &epoints. Now they have &6%total%");
	    c.get("command.editpoints.output.take", "&ePlayer (&6%playername%&e) lost &6%amount% &epoints. Now they have &6%total%");

	    c.get("command.editjobs.help.info", "Edit current jobs.");
	    c.get("command.editjobs.help.args", "");
	    c.get("command.editjobs.help.list.job", "&eJobs:");
	    c.get("command.editjobs.help.list.jobs", "  -> [&e%jobname%&r]");
	    c.get("command.editjobs.help.list.actions", "    -> [&e%actionname%&r]");
	    c.get("command.editjobs.help.list.material", "      -> [&e%materialname%&r]      ");
	    c.get("command.editjobs.help.list.materialRemove", "&c[X]");
	    c.get("command.editjobs.help.list.materialAdd", "      -> &e[&2+&e]");
	    c.get("command.editjobs.help.list.money", "        -> &eMoney: &6%amount%");
	    c.get("command.editjobs.help.list.exp", "        -> &eExp: &6%amount%");
	    c.get("command.editjobs.help.list.points", "        -> &ePoints: &6%amount%");
	    c.get("command.editjobs.help.modify.newValue", "&eEnter new value");
	    c.get("command.editjobs.help.modify.enter", "&eEnter new name or press ");
	    c.get("command.editjobs.help.modify.hand", "&6HAND ");
	    c.get("command.editjobs.help.modify.handHover", "&6Press to grab info from item in your hand");
	    c.get("command.editjobs.help.modify.or", "&eor ");
	    c.get("command.editjobs.help.modify.look", "&6LOOKING AT");
	    c.get("command.editjobs.help.modify.lookHover", "&6Press to grab info from block you are looking");

	    c.get("command.editquests.help.info", "Edit current quests.");
	    c.get("command.editquests.help.args", "");
	    c.get("command.editquests.help.list.quest", "&eQuests:");
	    c.get("command.editquests.help.list.jobs", "  -> [&e%jobname%&r]");
	    c.get("command.editquests.help.list.quests", "  -> [&e%questname%&r]");
	    c.get("command.editquests.help.list.actions", "    -> [&e%actionname%&r]");
	    c.get("command.editquests.help.list.objectives", "      -> [&e%objectivename%&r]");
	    c.get("command.editquests.help.list.objectiveRemove", "&c[X]");
	    c.get("command.editquests.help.list.objectiveAdd", "      -> &e[&2+&e]");
	    c.get("command.editquests.help.modify.newValue", "&eEnter new value");
	    c.get("command.editquests.help.modify.enter", "&eEnter new name or press ");
	    c.get("command.editquests.help.modify.hand", "&6HAND ");
	    c.get("command.editquests.help.modify.handHover", "&6Press to grab info from item in your hand");
	    c.get("command.editquests.help.modify.or", "&eor ");
	    c.get("command.editquests.help.modify.look", "&6LOOKING AT");
	    c.get("command.editquests.help.modify.lookHover", "&6Press to grab info from block you are looking");

	    c.get("command.blockinfo.help.info", "Shows information for the block you are looking at.");
	    c.get("command.blockinfo.help.args", "");
	    c.get("command.blockinfo.output.name", " &eBlock name: &6%blockname%");
	    c.get("command.blockinfo.output.id", " &eBlock id: &6%blockid%");
	    c.get("command.blockinfo.output.data", " &eBlock data: &6%blockdata%");
	    c.get("command.blockinfo.output.usage", " &eUsage: &6%first% &eor &6%second%");

	    c.get("command.iteminfo.help.info", "Shows information for the item you are holding.");
	    c.get("command.iteminfo.help.args", "");
	    c.get("command.iteminfo.output.name", " &eItem name: &6%itemname%");
	    c.get("command.iteminfo.output.id", " &eItem id: &6%itemid%");
	    c.get("command.iteminfo.output.data", " &eItem data: &6%itemdata%");
	    c.get("command.iteminfo.output.usage", " &eUsage: &6%first% &eor &6%second%");

	    c.get("command.placeholders.help.info", "List out all placeholders");
	    c.get("command.placeholders.help.args", "(parse) (placeholder)");
	    c.get("command.placeholders.output.list", "&e[place]. &7[placeholder]");
	    c.get("command.placeholders.output.outputResult", " &eresult: &7[result]");
	    c.get("command.placeholders.output.parse", "&6[placeholder] &7by [source] &6result &8|&f[result]&8|");

	    c.get("command.entitylist.help.info", "Shows all possible entities that can be used with the plugin.");
	    c.get("command.entitylist.help.args", "");

	    c.get("command.stats.help.info", "Show the level you are in each job you are part of.");
	    c.get("command.stats.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("stats", Arrays.asList("[playername]"));
	    c.get("command.stats.error.nojob", "Please join a job first.");
	    c.get("command.stats.output.message", "Level %joblevel% for %jobname%: %jobxp%/%jobmaxxp% xp");
	    c.get("command.stats.output.max-level", "     &cMax level   -   %jobname%");
	    c.get("command.stats.bossBarOutput", "Lvl %joblevel% %jobname%: %jobxp%/%jobmaxxp% xp%gain%");
	    c.get("command.stats.bossBarGain", " &7(&f%gain%&7)");

	    c.get("command.shop.help.info", "Opens special jobs shop.");
	    c.get("command.shop.help.args", "");
	    c.get("command.shop.info.title", "&e------- &8Jobs shop &e-------");
	    c.get("command.shop.info.currentPoints", "&eYou have: &6%currentpoints%");
	    c.get("command.shop.info.price", "&ePrice: &6%price%");
	    c.get("command.shop.info.reqJobs", "&eRequired jobs:");
	    c.get("command.shop.info.reqJobsList", "  &6%jobsname%&e: &e%level% lvl");
	    c.get("command.shop.info.reqTotalLevel", "&6Required total level: &e%totalLevel%");
	    c.get("command.shop.info.reqJobsColor", "&4");
	    c.get("command.shop.info.reqJobsLevelColor", "&4");
	    c.get("command.shop.info.reqTotalLevelColor", "&4");
	    c.get("command.shop.info.cantOpen", "&cCan't open this page");
	    c.get("command.shop.info.NoPermForItem", "&cYou don't have required permissions for this item!");
	    c.get("command.shop.info.NoPermToBuy", "&cNo permissions to buy this item");
	    c.get("command.shop.info.NoJobReqForitem", "&cYou don't have the required job (&6%jobname%&e) with required (&6%joblevel%&e) level");
	    c.get("command.shop.info.NoPoints", "&cYou don't have enough points");
	    c.get("command.shop.info.NoTotalLevel", "&cTotal jobs level is too low (%totalLevel%)");
	    c.get("command.shop.info.Paid", "&eYou have paid &6%amount% &efor this item");

	    c.get("command.archive.help.info", "Shows all jobs saved in archive by user.");
	    c.get("command.archive.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("archive", Arrays.asList("[playername]"));
	    c.get("command.archive.error.nojob", "There is no jobs saved.");

	    c.get("command.give.help.info", "Gives item by jobs name and item category name. Player name is optional");
	    c.get("command.give.help.args", "[playername] [jobname] [items/limiteditems] [jobitemname]");
	    Jobs.getGCManager().getCommandArgs().put("give", Arrays.asList("[playername]", "[jobname]%%[jobitemname]", "[jobitemname]"));
	    c.get("command.give.output.notonline", "&4Player with that name is not online!");
	    c.get("command.give.output.noitem", "&4Can't find any item by given name!");

	    c.get("command.info.help.title", "&2*** &eJobs&2 ***");
	    c.get("command.info.help.info", "Show how much each job is getting paid and for what.");
	    c.get("command.info.help.penalty", "&eThis job has a penalty of &c[penalty]% &ebecause there are too many players working in it.");
	    c.get("command.info.help.bonus", "&eThis job has a bonus of &2[bonus]% &ebecause there are not enough players working in it.");
	    c.get("command.info.help.args", "[jobname] [action]");
	    Jobs.getGCManager().getCommandArgs().put("info", Arrays.asList("[jobname]", "[action]"));
	    c.get("command.info.help.actions", "&eValid actions are: &f%actions%");
	    c.get("command.info.help.max", " - &emax level:&f ");
	    c.get("command.info.help.newMax", " &eMax level: &f[max]");
	    c.get("command.info.help.material", "&7%material%");

	    c.get("command.info.help.levelRange", " &a(&e%levelFrom% &a- &e%levelUntil% &alevels)");
	    c.get("command.info.help.levelFrom", " &a(from &e%levelFrom% &alevel)");
	    c.get("command.info.help.levelUntil", " &a(until &e%levelUntil% &alevel)");

	    c.get("command.info.help.money", " &2%money%\u0024");
	    c.get("command.info.help.points", " &6%points%pts");
	    c.get("command.info.help.exp", " &e%exp%xp");

	    c.get("command.info.gui.pickjob", "&ePick your job!");
	    c.get("command.info.gui.jobinfo", "&e[jobname] info!");
	    c.get("command.info.gui.actions", "&eValid actions are:");
	    c.get("command.info.gui.leftClick", "&eLeft Click for more info");
	    c.get("command.info.gui.middleClick", "&eMiddle Click to leave this job");
	    c.get("command.info.gui.rightClick", "&eRight Click to join job");
	    c.get("command.info.gui.leftSlots", "&eLeft slots:&f ");
	    c.get("command.info.gui.working", "&2&nAlready working");
	    c.get("command.info.gui.cantJoin", "&cYou can't join to the selected job.");
	    c.get("command.info.gui.max", "&eMax level:&f ");
	    c.get("command.info.gui.back", "&e<<< Back");
	    c.get("command.info.gui.next", "&eNext >>>");

	    c.get("command.info.output.break.info", "&eBreak");
	    c.get("command.info.output.break.none", "%jobname% does not get money for breaking blocks.");
	    c.get("command.info.output.tntbreak.info", "&eTNTBreak");
	    c.get("command.info.output.tntbreak.none", "%jobname% does not get money for breaking blocks with TNT.");
	    c.get("command.info.output.place.info", "&ePlace");
	    c.get("command.info.output.place.none", "%jobname% does not get money for placing blocks.");
	    c.get("command.info.output.striplogs.info", "&eStrip logs");
	    c.get("command.info.output.striplogs.none", "%jobname% does not get money for stripping logs.");
	    c.get("command.info.output.kill.info", "&eKill");
	    c.get("command.info.output.kill.none", "%jobname% does not get money for killing monsters.");
	    c.get("command.info.output.mmkill.info", "&eMMKill");
	    c.get("command.info.output.mmkill.none", "%jobname% does not get money for killing Mythic monsters.");
	    c.get("command.info.output.fish.info", "&eFish");
	    c.get("command.info.output.fish.none", "%jobname% does not get money from fishing.");
	    c.get("command.info.output.craft.info", "&eCraft");
	    c.get("command.info.output.craft.none", "%jobname% does not get money from crafting.");
	    c.get("command.info.output.smelt.info", "&eSmelt");
	    c.get("command.info.output.smelt.none", "%jobname% does not get money from smelting.");
	    c.get("command.info.output.brew.info", "&eBrew");
	    c.get("command.info.output.brew.none", "%jobname% does not get money from brewing.");
	    c.get("command.info.output.eat.info", "&eEat");
	    c.get("command.info.output.eat.none", "%jobname% does not get money from eating food.");
	    c.get("command.info.output.dye.info", "&eDye");
	    c.get("command.info.output.dye.none", "%jobname% does not get money from dyeing.");
	    c.get("command.info.output.enchant.info", "&eEnchant");
	    c.get("command.info.output.enchant.none", "%jobname% does not get money from enchanting.");
	    c.get("command.info.output.vtrade.info", "&eVillager trade");
	    c.get("command.info.output.vtrade.none", "%jobname% does not get money for trading a villager.");
	    c.get("command.info.output.repair.info", "&eRepair");
	    c.get("command.info.output.repair.none", "%jobname% does not get money from repairing.");
	    c.get("command.info.output.breed.info", "&eBreed");
	    c.get("command.info.output.breed.none", "%jobname% does not get money from breeding.");
	    c.get("command.info.output.tame.info", "&eTame");
	    c.get("command.info.output.tame.none", "%jobname% does not get money from taming.");
	    c.get("command.info.output.milk.info", "&eMilk");
	    c.get("command.info.output.milk.none", "%jobname% does not get money from milking cows.");
	    c.get("command.info.output.shear.info", "&eShear");
	    c.get("command.info.output.shear.none", "%jobname% does not get money from shearing sheep.");
	    c.get("command.info.output.explore.info", "&eExplore");
	    c.get("command.info.output.explore.none", "%jobname% does not get money from exploring.");
	    c.get("command.info.output.custom-kill.info", "&eCustom kill");
	    c.get("command.info.output.custom-kill.none", "%jobname% does not get money from custom player kills.");
	    c.get("command.info.output.collect.info", "&eCollect");
	    c.get("command.info.output.collect.none", "%jobname% does not get money for collecting blocks.");
	    c.get("command.info.output.bake.info", "&eBake");
	    c.get("command.info.output.bake.none", "%jobname% does not get money for cooking foods.");

	    c.get("command.playerinfo.help.info", "Show how much each job is getting paid and for what on another player.");
	    c.get("command.playerinfo.help.args", "[playername] [jobname] [action]");
	    Jobs.getGCManager().getCommandArgs().put("playerinfo", Arrays.asList("[playername]", "[jobname]", "[action]"));

	    c.get("command.join.help.info", "Join the selected job.");
	    c.get("command.join.help.args", "[jobname]");
	    Jobs.getGCManager().getCommandArgs().put("join", Arrays.asList("[jobname]"));
	    c.get("command.join.error.alreadyin", "You are already in the job %jobname%.");
	    c.get("command.join.error.fullslots", "You cannot join the job %jobname%, there are no slots available.");
	    c.get("command.join.error.maxjobs", "You have already joined too many jobs.");
	    c.get("command.join.error.rejoin", "&cCan't rejoin this job. Wait [time]");
	    c.get("command.join.rejoin", "&aClick to rejoin this job: ");
	    c.get("command.join.success", "You have joined the job %jobname%.");
	    c.get("command.join.confirm", "&2Click to confirm joining action for the &7[jobname] &2job.");

	    c.get("command.leave.help.info", "Leave the selected job.");
	    c.get("command.leave.help.args", "[oldplayerjob]");
	    Jobs.getGCManager().getCommandArgs().put("leave", Arrays.asList("[oldplayerjob]"));
	    c.get("command.leave.success", "You have left the job %jobname%.");
	    c.get("command.leave.confirmationNeed", "&cAre you sure you want to leave from&e [jobname]&c job? Type the command again within&6 [time] seconds &cto confirm!");

	    c.get("command.leaveall.help.info", "Leave all your jobs.");
	    c.get("command.leaveall.error.nojobs", "You do not have any jobs to leave!");
	    c.get("command.leaveall.success", "You have left all your jobs.");
	    c.get("command.leaveall.confirmationNeed", "&cAre you sure you want to leave from all jobs? Type the command again within&6 [time] seconds &cto confirm!");

	    c.get("command.explored.help.info", "Check who visited this chunk");
	    c.get("command.explored.error.noexplore", "No one visited this chunk");
	    c.get("command.explored.fullExplore", "&aThis chunk is fully explored");
	    c.get("command.explored.list", "&e%place%. %playername%");

	    c.get("command.browse.help.info", "List the jobs available to you.");
	    c.get("command.browse.error.nojobs", "There are no jobs you can join.");
	    c.get("command.browse.output.header", "You are allowed to join the following jobs:");
	    c.get("command.browse.output.footer", "For more information type in /jobs info [JobName]");
	    c.get("command.browse.output.totalWorkers", " &7Workers: &e[amount]");
	    c.get("command.browse.output.penalty", " &4Penalty: &c[amount]%");
	    c.get("command.browse.output.bonus", " &2Bonus: &a[amount]%");

	    c.get("command.browse.output.newHeader", "&2========== [amount] Available Jobs =========");
	    c.get("command.browse.output.description", "[description]");
	    c.get("command.browse.output.list", "    &8[place]. &7[jobname]");

	    c.get("command.browse.output.console.newHeader", "&2========== [amount] Available Jobs =========");
	    c.get("command.browse.output.console.description", "[description]");
	    c.get("command.browse.output.console.totalWorkers", " &7Workers: &e[amount]");
	    c.get("command.browse.output.console.penalty", " &4Penalty: &c[amount]%");
	    c.get("command.browse.output.console.bonus", " &2Bonus: &a[amount]%");
	    c.get("command.browse.output.console.list", " &6[jobname]");
	    c.get("command.browse.output.console.newMax", " &eMax level: &f[max]");

	    c.get("command.browse.output.click", "&bClick on the job to see more info about it!");
	    c.get("command.browse.output.detailed", "&bClick to see more detailed list on job actions");

	    c.get("command.browse.output.jobHeader", "&2========== [jobname] =========");
	    c.get("command.browse.output.chooseJob", "&7&n&oChoose this job");
	    c.get("command.browse.output.chooseJobHover", "&7Click here to get this job");

	    c.get("command.clearownership.help.info", "Clear block ownership");
	    c.get("command.clearownership.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("clearownership", Arrays.asList("[playername]"));
	    c.get("command.clearownership.output.cleared", "&2Removed &7[furnaces] &2furnaces, &7[brewing] &2brewing stands, &7[smoker]&2 smokers and &7[blast]&2 blast furnaces.");

	    c.get("command.skipquest.help.info", "Skip defined quest and get new one");
	    c.get("command.skipquest.help.args", "[jobname] [questname] (playerName)");
	    c.get("command.skipquest.output.questSkipForCost", "&2You skipped the quest and paid:&e %amount%$");
	    Jobs.getGCManager().getCommandArgs().put("skipquest", Arrays.asList("[jobname]", "[questname]", "[playername]"));

	    c.get("command.quests.help.info", "List available quests");
	    c.get("command.quests.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("quests", Arrays.asList("[playername]"));
	    c.get("command.quests.error.noquests", "&cThere are no quests");
	    c.get("command.quests.toplineseparator", "&7*********************** &6[playerName] &2(&f[questsDone]&2) &7***********************");
	    c.get("command.quests.status.changed", "&2The quests status has been changed to&r %status%");
	    c.get("command.quests.status.started", "&aStarted");
	    c.get("command.quests.status.stopped", "&cStopped");
	    c.get("command.quests.output.completed", "&2      !Completed!&r      ");
	    c.get("command.quests.output.questLine", "[progress] &7[questName] &f[done]&7/&8[required]");
	    c.get("command.quests.output.skip", "&7Click to skip this quest");
	    c.get("command.quests.output.skips", "&7Left skips: &f[skips]");
	    c.get("command.quests.output.hover", "&f[jobName] \n[desc] \n&7New quest in: [time]");

	    c.get("command.fire.help.info", "Fire the player from the job.");
	    c.get("command.fire.help.args", "[playername] [jobname]");
	    Jobs.getGCManager().getCommandArgs().put("fire", Arrays.asList("[playername]", "[jobname]"));
	    c.get("command.fire.error.nojob", "Player does not have the job %jobname%.");
	    c.get("command.fire.output.target", "You have been fired from %jobname%.");

	    c.get("command.fireall.help.info", "Fire player from all their jobs.");
	    c.get("command.fireall.help.args", "[playername]/all");
	    Jobs.getGCManager().getCommandArgs().put("fireall", Arrays.asList("[playername]"));
	    c.get("command.fireall.error.nojobs", "Player does not have any jobs to be fired from!");
	    c.get("command.fireall.output.target", "You have been fired from all your jobs.");

	    c.get("command.employ.help.info", "Employ the player to the job.");
	    c.get("command.employ.help.args", "[playername] [jobname]");
	    Jobs.getGCManager().getCommandArgs().put("employ", Arrays.asList("[playername]", "[jobname]"));
	    c.get("command.employ.error.alreadyin", "Player is already in the job %jobname%.");
	    c.get("command.employ.error.fullslots", "You cannot join the job %jobname%, there are no slots available.");
	    c.get("command.employ.output.target", "You have been employed as a %jobname%.");

	    c.get("command.top.help.info", "Shows top players by jobs name.");
	    c.get("command.top.help.args", "[jobname]/clear pageNumber");
	    Jobs.getGCManager().getCommandArgs().put("top", Arrays.asList("[jobname]"));
	    c.get("command.top.error.nojob", "Can't find any job with this name.");
	    c.get("command.top.output.topline", "&aTop&e %amount% &aplayers by &e%jobname% &ajob");
	    c.get("command.top.output.list", "&e%number%&a. &e%playername% &alvl &e%level% &awith&e %exp% &aexp");
	    c.get("command.top.output.prev", "&e<<<<< Prev page &2|");
	    c.get("command.top.output.next", "&2|&e Next Page >>>>");
	    c.get("command.top.output.show", "&2Show from &e[from] &2until &e[until] &2top list");

	    c.get("command.gtop.help.info", "Shows top players by global jobs level.");
	    c.get("command.gtop.help.args", "clear/pageNumber");
	    c.get("command.gtop.error.nojob", "Can't find any information.");
	    c.get("command.gtop.output.topline", "&aTop&e %amount% &aplayers by global job level");
	    c.get("command.gtop.output.list", "&e%number%&a. &e%playername% &alvl &e%level% &awith&e %exp% &aexp");
	    c.get("command.gtop.output.prev", "&e<<<<< Prev page &2|");
	    c.get("command.gtop.output.next", "&2|&e Next Page >>>>");
	    c.get("command.gtop.output.show", "&2Show from &e[from] &2until &e[until] &2global top list");

	    c.get("command.area.help.info", "Modify restricted areas.");
	    c.get("command.area.help.args", "add/remove/info/list");
	    Jobs.getGCManager().getCommandArgs().put("area", Arrays.asList("add%%remove%%info%%list"));
	    c.get("command.area.help.addUsage", "&eUsage: &6/Jobs area add [areaName/wg:worldGuardAreaName] [bonus]");
	    c.get("command.area.help.removeUsage", "&eUsage: &6/Jobs area remove [areaName]");
	    c.get("command.area.output.addedNew", "&eAdded a new restricted area with &6%bonus% &ebonus");
	    c.get("command.area.output.removed", "&eRemoved the restricted area &6%name%");
	    c.get("command.area.output.list", "&e%number%&a. &e%areaname% &e%worldname% (&a%x1%:%y1%:%z1%/&e%x2%:%y2%:%z2%) &6%bonus%");
	    c.get("command.area.output.wgList", "&e%number%&a. WorldGuard: &e%areaname% &6%bonus%");
	    c.get("command.area.output.noAreas", "&eThere are no saved restricted areas");
	    c.get("command.area.output.noAreasByLoc", "&eThere are no restricted areas in this location");
	    c.get("command.area.output.areaList", "&eRestricted areas by your location: &6%list%");
	    c.get("command.area.output.selected1", "&eSelected the first point: &6%x%:%y%:%z%");
	    c.get("command.area.output.selected2", "&eSelected the second point: &6%x%:%y%:%z%");
	    c.get("command.area.output.select", "&eSelect 2 points with the selection tool (%tool%)");
	    c.get("command.area.output.exist", "&eRestriction area by this name already exists");
	    c.get("command.area.output.dontExist", "&eRestriction area by this name does not exist");
	    c.get("command.area.output.wgDontExist", "&eWorldGuard area by this name does not exist");

	    c.get("command.log.help.info", "Shows statistics.");
	    c.get("command.log.help.args", "[playername]");
	    Jobs.getGCManager().getCommandArgs().put("log", Arrays.asList("[playername]"));
	    c.get("command.log.output.topline", "&7************************* &6%playername% &7*************************");
	    c.get("command.log.output.ls", "&7* &6%number%. &3%action%: &6%item% &eqty: %qty% %money%%exp%%points%");
	    c.get("command.log.output.money", "&6money: %amount% ");
	    c.get("command.log.output.exp", "&eexp: %amount% ");
	    c.get("command.log.output.points", "&6points: %amount%");
	    c.get("command.log.output.totalIncomes", "    &6Total money:&2 %money%&6, Total exp:&2 %exp%&6, Total points:&2 %points%");
	    c.get("command.log.output.bottomline", "&7***********************************************************");
	    c.get("command.log.output.prev", "&e<<<<< Prev page &2|");
	    c.get("command.log.output.next", "&2|&e Next Page >>>>");
	    c.get("command.log.output.nodata", "&cData not found");

	    c.get("command.glog.help.info", "Shows global statistics.");
	    c.get("command.glog.help.args", "");
	    c.get("command.glog.output.topline", "&7*********************** &6Global statistics &7***********************");
	    c.get("command.glog.output.ls", "&7* &6%number%. &3%action%: &6%item% &eqty: %qty% %money%%exp%%points%");
	    c.get("command.glog.output.money", "&6money: %amount% ");
	    c.get("command.glog.output.exp", "&eexp: %amount% ");
	    c.get("command.glog.output.points", "&6points: %amount%");
	    c.get("command.glog.output.totalIncomes", "    &6Total money:&2 %money%&6, Total exp:&2 %exp%&6, Total points:&2 %points%");
	    c.get("command.glog.output.bottomline", "&7**************************************************************");
	    c.get("command.glog.output.nodata", "&cData not found");

	    c.get("command.transfer.help.info", "Transfer a player's job from an old job to a new job.");
	    c.get("command.transfer.help.args", "[playername] [oldjob] [newjob]");
	    Jobs.getGCManager().getCommandArgs().put("transfer", Arrays.asList("[playername]", "[oldjob]", "[newjob]"));
	    c.get("command.transfer.output.target", "You have been transferred from %oldjobname% to %newjobname%.");

	    c.get("command.promote.help.info", "Promote the player X levels in a job.");
	    c.get("command.promote.help.args", "[playername] [jobname] [levels]");
	    Jobs.getGCManager().getCommandArgs().put("promote", Arrays.asList("[playername]", "[jobname]", "[levels]"));
	    c.get("command.promote.output.target", "You have been promoted %levelsgained% levels in %jobname%.");

	    c.get("command.exp.help.info", "Change the player exp for job.");
	    c.get("command.exp.help.args", "[playername] [jobname] set/add/take [amount]");
	    Jobs.getGCManager().getCommandArgs().put("exp", Arrays.asList("[playername]", "[jobname]", "set%%add%%take"));
	    c.get("command.exp.error.nojob", "&cThis player must first join a job.");
	    c.get("command.exp.output.target", "&eYour exp was changed for %jobname% &eand now you at &6%level%lvl &eand with &6%exp%exp.");

	    c.get("command.level.help.info", "Change the player's level in a job.");
	    c.get("command.level.help.args", "[playername] [jobname] set/add/take [amount]");
	    Jobs.getGCManager().getCommandArgs().put("level", Arrays.asList("[playername]", "[jobname]", "set%%add%%take"));
	    c.get("command.level.error.nojob", "&cThis player must first join a job.");
	    c.get("command.level.output.target", "&eYour level was changed for %jobname% &eand now you at &6%level%lvl &eand with &6%exp%exp.");

	    c.get("command.demote.help.info", "Demote the player X levels in a job.");
	    c.get("command.demote.help.args", "[playername] [jobname] [levels]");
	    Jobs.getGCManager().getCommandArgs().put("demote", Arrays.asList("[playername]", "[jobname]", "[levels]"));
	    c.get("command.demote.output.target", "You have been demoted %levelslost% levels in %jobname%.");

	    c.get("command.grantxp.help.info", "Grants the player X experience in a job.");
	    c.get("command.grantxp.help.args", "[playername] [jobname] [xp]");
	    Jobs.getGCManager().getCommandArgs().put("grantxp", Arrays.asList("[playername]", "[jobname]", "[xp]"));
	    c.get("command.grantxp.output.target", "You have been granted %xpgained% experience in %jobname%.");

	    c.get("command.removexp.help.info", "Remove X experience from the player in a job.");
	    c.get("command.removexp.help.args", "[playername] [jobname] [xp]");
	    Jobs.getGCManager().getCommandArgs().put("removexp", Arrays.asList("[playername]", "[jobname]", "[xp]"));
	    c.get("command.removexp.output.target", "You have lost %xplost% experience in %jobname%.");

	    c.get("command.signupdate.help.info", "Manually updates a sign by its name");
	    c.get("command.signupdate.help.args", "[jobname]");
	    Jobs.getGCManager().getCommandArgs().put("signupdate", Arrays.asList("[jobname]"));

	    c.get("command.bp.help.info", "Shows block protections around you in 10 block radius");
	    c.get("command.bp.help.args", "");
	    c.get("command.bp.output.found", "&eFound &6%amount% &eprotected blocks around you");
	    c.get("command.bp.output.notFound", "&eNo protected blocks found around you");

	    c.get("command.reload.help.info", "Reload configurations.");

	    c.get("command.toggle.help.info", "Toggles payment output on action bar or bossbar.");
	    c.get("command.toggle.help.args", "actionbar/bossbar");
	    Jobs.getGCManager().getCommandArgs().put("toggle", Arrays.asList("actionbar%%bossbar"));
	    c.get("command.toggle.output.turnedoff", "&4This feature is turned off!");
	    c.get("command.toggle.output.paid.main", "&aYou got:");
	    c.get("command.toggle.output.paid.money", "&e[amount] money");
	    c.get("command.toggle.output.paid.exp", "&7[exp] exp");
	    c.get("command.toggle.output.paid.points", "&6[points] points");
	    c.get("command.toggle.output.on", "&aToggled: &aON");
	    c.get("command.toggle.output.off", "&aToggled: &4OFF");

	    c.get("message.skillup.broadcast", "%playername% has been promoted to a %titlename% %jobname%.");
	    c.get("message.skillup.nobroadcast", "Congratulations, you have been promoted to a %titlename% %jobname%.");

	    c.get("message.levelup.broadcast", "%playername% is now a level %joblevel% %jobname%.");
	    c.get("message.levelup.nobroadcast", "You are now level %joblevel% %jobname%.");
	    c.get("message.leveldown.message", "&cYou lost level&e %lostLevel%&c in&e %jobname%&c job! Level:&6 %joblevel%&c.");

	    c.get("message.cowtimer", "&eYou still need to wait &6%time% &esec to get paid for this job.");
	    c.get("message.blocktimer", "&eYou need to wait &3[time] &esec more to get paid for this!");
	    c.get("message.taxes", "&3[amount] &eserver taxes were transferred to this account");

	    c.get("message.boostStarted", "&eJobs boost time have been started!");
	    c.get("message.boostStoped", "&eJobs boost time have been ended!");

	    c.get("message.crafting.fullinventory", "&cYour inventory is full!");

	    c.get("signs.List", "&0[number].&8[player]&7:&4[level]");
	    c.get("signs.questList", "&0[number].&8[player]&7:&4[quests]");
	    c.get("signs.SpecialList.p1", "&b** &8First &b**");
	    c.get("signs.SpecialList.p2", "&b** &8Second &b**");
	    c.get("signs.SpecialList.p3", "&b** &8Third &b**");
	    c.get("signs.SpecialList.p4", "&b** &8Fourth &b**");
	    c.get("signs.SpecialList.p5", "&b** &8Fifth &b**");
	    c.get("signs.SpecialList.p6", "&b** &8Sixth &b**");
	    c.get("signs.SpecialList.p7", "&b** &8Seventh &b**");
	    c.get("signs.SpecialList.p8", "&b** &8Eight &b**");
	    c.get("signs.SpecialList.p9", "&b** &8Ninth &b**");
	    c.get("signs.SpecialList.p10", "&b** &8Tenth &b**");
	    c.get("signs.SpecialList.name", "&9[player]");
	    c.get("signs.SpecialList.level", "&8[level] level");
	    c.get("signs.SpecialList.quests", "&8[quests] quests");
	    c.get("signs.SpecialList.bottom", "&b************");
	    c.get("signs.cantcreate", "&4You can't create this sign!");
	    c.get("signs.cantdestroy", "&4You can't destroy this sign!");
	    c.get("signs.topline", "&0[Jobs]");
	    c.get("signs.secondline.join", "&0Join");
	    c.get("signs.secondline.leave", "&0Leave");
	    c.get("signs.secondline.toggle", "&0Toggle");
	    c.get("signs.secondline.top", "&0Top");
	    c.get("signs.secondline.browse", "&0Browse");
	    c.get("signs.secondline.stats", "&0Stats");
	    c.get("signs.secondline.limit", "&0Limit");
	    c.get("signs.secondline.info", "&0Info");
	    c.get("signs.secondline.archive", "&0Archive");

	    //c.get("scoreboard.clear", "&eIf you want to remove scoreboard, type &2/jobs top clear");
	    c.get("scoreboard.topline", "&2Top &e%jobname%");
	    c.get("scoreboard.gtopline", "&2Global top list");
	    c.get("scoreboard.line", "&2%number%. &e%playername% (&6%level%&e)");

	    signKeys.clear();
	    signKeys.addAll(c.getC().getConfigurationSection("signs.secondline").getKeys(false));

	    // Write back config
	    c.save();
	}
    }
}
