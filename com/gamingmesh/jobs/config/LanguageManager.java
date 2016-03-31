package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.container.LocaleReader;

public class LanguageManager {
    private JobsPlugin plugin;

    public LanguageManager(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    /**
     * Method to load the language file configuration
     * 
     * loads from Jobs/locale/messages_en.yml
     */
    synchronized void load() {

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

	languages.add("en");

	File customLocaleFile = new File(plugin.getDataFolder(), "locale" + File.separator + "messages_" + Jobs.getGCManager().localeString + ".yml");
	if (!customLocaleFile.exists() && !Jobs.getGCManager().localeString.equalsIgnoreCase("en"))
	    languages.add(Jobs.getGCManager().localeString);

	for (String lang : languages) {

	    File f = new File(plugin.getDataFolder(), "locale" + File.separator + "messages_" + lang + ".yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	    CommentedYamlConfiguration writer = new CommentedYamlConfiguration();

	    LocaleReader c = new LocaleReader(config, writer);

	    c.getC().options().copyDefaults(true);
	    Jobs.getGCManager().commandArgs.clear();

	    c.get("economy.error.nomoney", "&cSorry, no money left in national bank!");
	    c.get("limitedItem.error.levelup", "&cYou need to levelup in [jobname] to use this item!");
	    c.get("general.info.toplineseparator", "&7*********************** &6%playername% &7***********************");
	    c.get("general.info.separator", "&7*******************************************************");
	    c.get("general.admin.error", "&cThere was an error in the command.");
	    c.get("general.admin.success", "&eYour command has been performed.");
	    c.get("general.error.notNumber", "&ePlease use numbers!");
	    c.get("general.error.job", "&cThe job you have selected does not exist!");
	    c.get("general.error.permission", "&cYou do not have permission to do that!");
	    c.get("general.error.noinfo", "&cNo information found!");
	    c.get("general.error.noinfoByPlayer", "&cNo information found by [%playername%] player name!");
	    c.get("general.error.ingame", "&cYou can use this command only in game!");
	    c.get("general.error.fromconsole", "&cYou can use this command only from console!");
	    c.get("general.error.worldisdisabled", "&cYou cant use command in this world!");

	    c.get("command.moneyboost.help.info", "Boosts Money gain for all players");
	    c.get("command.moneyboost.help.args", "[jobname] [rate]");
	    Jobs.getGCManager().commandArgs.put("moneyboost", Arrays.asList("[jobname]", "[rate]"));
	    c.get("command.moneyboost.output.allreset", "All money boost turned off");
	    c.get("command.moneyboost.output.jobsboostreset", "Money boost for %jobname% was turned off");
	    c.get("command.moneyboost.output.nothingtoreset", "Nothing to reset");
	    c.get("command.moneyboost.output.boostalladded", "Money boost of %boost% added for all jobs!");
	    c.get("command.moneyboost.output.boostadded", "Money boost of &e%boost% &aadded for &e%jobname%!");
	    c.get("command.moneyboost.output.infostats", "&c-----> &aMoney rate x%boost% enabled&c <-------");

	    c.get("command.pointboost.help.info", "Boosts points gain for all players");
	    c.get("command.pointboost.help.args", "[jobname] [rate]");
	    Jobs.getGCManager().commandArgs.put("pointboost", Arrays.asList("[jobname]", "[rate]"));
	    c.get("command.pointboost.output.allreset", "All points boost turned off");
	    c.get("command.pointboost.output.jobsboostreset", "Points boost for %jobname% was turned off");
	    c.get("command.pointboost.output.nothingtoreset", "Nothing to reset");
	    c.get("command.pointboost.output.boostalladded", "Points boost of %boost% added for all jobs!");
	    c.get("command.pointboost.output.boostadded", "Points boost of &e%boost% &aadded for &e%jobname%!");
	    c.get("command.pointboost.output.infostats", "&c-----> &aPoints rate x%boost% enabled&c <-------");

	    c.get("command.expboost.help.info", "Boosts Exp gain for all players");
	    c.get("command.expboost.help.args", "[jobname] [rate]");
	    Jobs.getGCManager().commandArgs.put("expboost", Arrays.asList("[jobname]", "[rate]"));
	    c.get("command.expboost.output.allreset", "All exp boost turned off");
	    c.get("command.expboost.output.jobsboostreset", "Exp boost for %jobname% was turned off");
	    c.get("command.expboost.output.nothingtoreset", "Nothing to reset");
	    c.get("command.expboost.output.boostalladded", "Exp boost of %boost% added for all jobs!");
	    c.get("command.expboost.output.boostadded", "Exp boost of &e%boost% &aadded for &e%jobname%!");
	    c.get("command.expboost.output.infostats", "&c-----> &aExp rate x%boost% enabled&c <-------");

	    c.get("command.bonus.help.info", "Show job bonuses");
	    c.get("command.bonus.help.args", "[jobname]");
	    Jobs.getGCManager().commandArgs.put("bonus", Arrays.asList("[jobname]"));
	    c.get("command.bonus.output.topline", "&7**************** &2[money] &6[points] &e[exp] &7****************");
	    c.get("command.bonus.output.permission", " &ePermission bonus: %money% %points% %exp%");
	    c.get("command.bonus.output.item", " &eItem bonus: %money% %points% %exp%");
	    c.get("command.bonus.output.global", " &eGlobal bonus: %money% %points% %exp%");
	    c.get("command.bonus.output.dynamic", " &eDynamic payment bonus: %money% %points% %exp%");
	    c.get("command.bonus.output.area", " &eArea bonus: %money% %points% %exp%");
	    c.get("command.bonus.output.final", " &eFinal bonus: %money% %points% %exp%");

	    c.get("command.convert.help.info",
		"Converts data base system from one system to another. if you currently running sqlite, this will convert to Mysql and vise versa.");
	    c.get("command.convert.help.args", "");

	    c.get("command.limit.help.info", "Shows payment limits for jobs");
	    c.get("command.limit.help.args", "");
	    c.get("command.limit.output.lefttime", "&eTime left until money limit resets: &2%hour% &ehour &2%min% &emin &2%sec% &esec");
	    c.get("command.limit.output.moneylimit", "&eMoney limit: &2%money%&e/&2%totalmoney%");
	    c.get("command.limit.output.leftexptime", "&eTime left until Exp limit resets: &2%hour% &ehour &2%min% &emin &2%sec% &esec");
	    c.get("command.limit.output.explimit", "&eExp limit: &2%exp%&e/&2%totalexp%");
	    c.get("command.limit.output.leftpointtime", "&eTime left until Point limit resets: &2%hour% &ehour &2%min% &emin &2%sec% &esec");
	    c.get("command.limit.output.pointlimit", "&ePoint limit: &2%current%&e/&2%total%");
	    c.get("command.limit.output.reachedlimit", "&4You have reached money limit in given time!");
	    c.get("command.limit.output.reachedlimit2", "&eYou can check your limit with &2/jobs limit &ecommand");
	    c.get("command.limit.output.reachedExplimit", "&4You have reached exp limit in given time!");
	    c.get("command.limit.output.reachedExplimit2", "&eYou can check your limit with &2/jobs limit &ecommand");
	    c.get("command.limit.output.reachedPointlimit", "&4You have reached exp limit in given time!");
	    c.get("command.limit.output.reachedPointlimit2", "&eYou can check your limit with &2/jobs limit &ecommand");
	    c.get("command.limit.output.notenabled", "&eMoney limit is not enabled");

	    c.get("command.help.output.info", "Type /jobs [cmd] ? for more information about a command.");
	    c.get("command.help.output.usage", "Usage: %usage%");
	    c.get("command.help.output.title", "&e-------&e ======= &6Jobs &e======= &e-------");
	    c.get("command.help.output.page", "&e-----&e ====== Page &6[1] &eof &6[2] &e====== &e-----");
	    c.get("command.help.output.prev", "&e--- <<<<< &6Prev page &e|");
	    c.get("command.help.output.next", "&e|&6 Next Page &e>>>> ---");

	    c.get("command.points.help.info", "Shows how much points player have.");
	    c.get("command.points.help.args", "[playername]");
	    Jobs.getGCManager().commandArgs.put("points", Arrays.asList("[playername]"));
	    c.get("command.points.currentpoints", " &eCurrent point amount: &6%currentpoints%");
	    c.get("command.points.totalpoints", " &eTotal amount of collected points until now: &6%totalpoints%");

	    c.get("command.editpoints.help.info", "Edit players points.");
	    c.get("command.editpoints.help.args", "[set/add/take] [playername] [amount]");
	    Jobs.getGCManager().commandArgs.put("editpoints", Arrays.asList("set%%add%%take", "[playername]"));
	    c.get("command.editpoints.output.set", "&ePlayers (&6%playername%&e) points was set to &6%amount%");
	    c.get("command.editpoints.output.add", "&ePlayer (&6%playername%&e) got aditinal &6%amount% &epoints. Now he has &6%total%");
	    c.get("command.editpoints.output.take", "&ePlayer (&6%playername%&e) lost &6%amount% &epoints. Now he has &6%total%");

	    c.get("command.stats.help.info", "Show the level you are in each job you are part of.");
	    c.get("command.stats.help.args", "[playername]");
	    Jobs.getGCManager().commandArgs.put("stats", Arrays.asList("[playername]"));
	    c.get("command.stats.error.nojob", "Please join a job first.");
	    c.get("command.stats.output", " lvl%joblevel% %jobname% : %jobxp%/%jobmaxxp% xp");

	    c.get("command.shop.help.info", "Opens special jobs shop.");
	    c.get("command.shop.help.args", "");
	    c.get("command.shop.info.title", "&e------- &8Jobs shop &e-------");
	    c.get("command.shop.info.currentPoints", "&eYou have: &6%currentpoints%");
	    c.get("command.shop.info.price", "&ePrice: &6%price%");
	    c.get("command.shop.info.reqJobs", "&eRequired jobs:");
	    c.get("command.shop.info.reqJobsList", "  &6%jobsname%&e: &e%level% lvl");
	    
	    c.get("command.shop.info.NoPermForItem", "&cYou don't have required permissions for this item!");
	    c.get("command.shop.info.NoPermToBuy", "&cNo permissions to buy this item");
	    c.get("command.shop.info.NoJobReqForitem", "&cYou don't have required job (&6%jobname%&e) with required (&6%joblevel%&e) level");
	    c.get("command.shop.info.NoPoints", "&cYou don't have enough points");
	    c.get("command.shop.info.Paid", "&eYou have paid &6%amount% &efor this item");
	    c.get("command.shop.info.reqJobsList", "  &6%jobsname%&e: &e%level% lvl");

	    c.get("command.archive.help.info", "Shows all jobs saved in archive by user.");
	    c.get("command.archive.help.args", "[playername]");
	    Jobs.getGCManager().commandArgs.put("archive", Arrays.asList("[playername]"));
	    c.get("command.archive.error.nojob", "There is no jobs saved.");
	    c.get("command.archive.output", "lvl %joblevel% (%getbackjoblevel%) %jobname%");

	    c.get("command.give.help.info", "Gives item by jobs name and item category name. Player name is optional");
	    c.get("command.give.help.args", "[playername] [jobname] [itemname]");
	    Jobs.getGCManager().commandArgs.put("give", Arrays.asList("[playername]", "[jobname]", "[jobitemname]"));
	    c.get("command.give.output.notonline", "&4Player [%playername%] is not online!");
	    c.get("command.give.output.noitem", "&4Cant find any item by given name!");

	    c.get("command.info.help.title", "&2*** &eJobs&2 ***");
	    c.get("command.info.help.info", "Show how much each job is getting paid and for what.");
	    c.get("command.info.help.penalty", "&eThis job have &c[penalty]% &epenalty because of too many players working in it.");
	    c.get("command.info.help.bonus", "&eThis job have &2[bonus]% &ebonus because not enough players working in it.");
	    c.get("command.info.help.args", "[jobname] [action]");
	    Jobs.getGCManager().commandArgs.put("info", Arrays.asList("[jobname]", "[action]"));
	    c.get("command.info.help.actions", "&eValid actions are: &f%actions%");
	    c.get("command.info.help.max", " - &emax level:&f ");
	    c.get("command.info.help.material", "&7%material%");

	    c.get("command.info.help.levelRange", " &a(&e%levelFrom% &a- &e%levelUntil% &alevels)");
	    c.get("command.info.help.levelFrom", " &a(from &e%levelFrom% &alevel)");
	    c.get("command.info.help.levelUntil", " &a(until &e%levelUntil% &alevel)");

	    c.get("command.info.gui.pickjob", "&ePick your job!");
	    c.get("command.info.gui.jobinfo", "&e[jobname] info!");
	    c.get("command.info.gui.actions", "&eValid actions are:");
	    c.get("command.info.gui.leftClick", "&eLeft Click for more info");
	    c.get("command.info.gui.rightClick", "&eRight click to join job");
	    c.get("command.info.gui.leftSlots", "&eLeft slots:&f ");
	    c.get("command.info.gui.working", "&2&nAlready working");
	    c.get("command.info.gui.max", "&eMax level:&f ");
	    c.get("command.info.gui.back", "&e<<< Back");

	    c.get("command.info.output.break.info", "Break");
	    c.get("command.info.output.break.none", "%jobname% does not get money for breaking blocks.");
	    c.get("command.info.output.tntbreak.info", "TNTBreak");
	    c.get("command.info.output.tntbreak.none", "%jobname% does not get money for breaking blocks with tnt.");
	    c.get("command.info.output.place.info", "Place");
	    c.get("command.info.output.place.none", "%jobname% does not get money for placing blocks.");
	    c.get("command.info.output.kill.info", "Kill");
	    c.get("command.info.output.kill.none", "%jobname% does not get money for killing monsters.");
	    c.get("command.info.output.mmkill.info", "MMKill");
	    c.get("command.info.output.mmkill.none", "%jobname% does not get money for killing Mythic monsters.");
	    c.get("command.info.output.fish.info", "Fish");
	    c.get("command.info.output.fish.none", "%jobname% does not get money from fishing.");
	    c.get("command.info.output.craft.info", "Craft");
	    c.get("command.info.output.craft.none", "%jobname% does not get money from crafting.");
	    c.get("command.info.output.smelt.info", "Smelt");
	    c.get("command.info.output.smelt.none", "%jobname% does not get money from smelting.");
	    c.get("command.info.output.brew.info", "Brew");
	    c.get("command.info.output.brew.none", "%jobname% does not get money from brewing.");
	    c.get("command.info.output.eat.info", "Eat");
	    c.get("command.info.output.eat.none", "%jobname% does not get money from eating food.");
	    c.get("command.info.output.dye.info", "Dye");
	    c.get("command.info.output.dye.none", "%jobname% does not get money from dyeing.");
	    c.get("command.info.output.enchant.info", "Enchant");
	    c.get("command.info.output.enchant.none", "%jobname% does not get money from enchanting.");
	    c.get("command.info.output.repair.info", "Repair");
	    c.get("command.info.output.repair.none", "%jobname% does not get money from repairing.");
	    c.get("command.info.output.breed.info", "Breed");
	    c.get("command.info.output.breed.none", "%jobname% does not get money from breeding.");
	    c.get("command.info.output.tame.info", "Tame");
	    c.get("command.info.output.tame.none", "%jobname% does not get money from taming.");
	    c.get("command.info.output.milk.info", "Milk");
	    c.get("command.info.output.milk.none", "%jobname% does not get money from milking cows.");
	    c.get("command.info.output.shear.info", "Shear");
	    c.get("command.info.output.shear.none", "%jobname% does not get money from shearing sheeps.");
	    c.get("command.info.output.explore.info", "Explore");
	    c.get("command.info.output.explore.none", "%jobname% does not get money from exploring.");
	    c.get("command.info.output.custom-kill.info", "Custom kill");
	    c.get("command.info.output.custom-kill.none", "%jobname% does not get money from custom player kills.");

	    c.get("command.playerinfo.help.info", "Show how much each job is getting paid and for what on another player.");
	    c.get("command.playerinfo.help.args", "[playername] [jobname] [action]");
	    Jobs.getGCManager().commandArgs.put("playerinfo", Arrays.asList("[playername]", "[jobname]", "[action]"));

	    c.get("command.join.help.info", "Join the selected job.");
	    c.get("command.join.help.args", "[jobname]");
	    Jobs.getGCManager().commandArgs.put("join", Arrays.asList("[jobname]"));
	    c.get("command.join.error.alreadyin", "You are already in the job %jobname%.");
	    c.get("command.join.error.fullslots", "You cannot join the job %jobname%, there are no slots available.");
	    c.get("command.join.error.maxjobs", "You have already joined too many jobs.");
	    c.get("command.join.success", "You have joined the job %jobname%.");

	    c.get("command.leave.help.info", "Leave the selected job.");
	    c.get("command.leave.help.args", "[oldplayerjob]");
	    Jobs.getGCManager().commandArgs.put("leave", Arrays.asList("[oldplayerjob]"));
	    c.get("command.leave.success", "You have left the job %jobname%.");

	    c.get("command.leaveall.help.info", "Leave all your jobs.");
	    c.get("command.leaveall.error.nojobs", "You do not have any jobs to leave!");
	    c.get("command.leaveall.success", "You have left all your jobs.");

	    c.get("command.browse.help.info", "List the jobs available to you.");
	    c.get("command.browse.error.nojobs", "There are no jobs you can join.");
	    c.get("command.browse.output.header", "You are allowed to join the following jobs:");
	    c.get("command.browse.output.footer", "For more information type in /jobs info [JobName]");
	    c.get("command.browse.output.totalWorkers", " &7Workers: &e[amount]");
	    c.get("command.browse.output.penalty", " &4Penalty: &c[amount]%");
	    c.get("command.browse.output.bonus", " &2Bonus: &a[amount]%");

	    c.get("command.fire.help.info", "Fire the player from the job.");
	    c.get("command.fire.help.args", "[playername] [jobname]");
	    Jobs.getGCManager().commandArgs.put("fire", Arrays.asList("[playername]", "[oldjob]"));
	    c.get("command.fire.error.nojob", "Player does not have the job %jobname%.");
	    c.get("command.fire.output.target", "You have been fired from %jobname%.");

	    c.get("command.fireall.help.info", "Fire player from all their jobs.");
	    c.get("command.fireall.help.args", "[playername]");
	    Jobs.getGCManager().commandArgs.put("fireall", Arrays.asList("[playername]"));
	    c.get("command.fireall.error.nojobs", "Player does not have any jobs to be fired from!");
	    c.get("command.fireall.output.target", "You have been fired from all your jobs.");

	    c.get("command.employ.help.info", "Employ the player to the job.");
	    c.get("command.employ.help.args", "[playername] [jobname]");
	    Jobs.getGCManager().commandArgs.put("employ", Arrays.asList("[playername]", "[jobname]"));
	    c.get("command.employ.error.alreadyin", "Player is already in the job %jobname%.");
	    c.get("command.employ.output.target", "You have been employed as a %jobname%.");

	    c.get("command.top.help.info", "Shows top 15 players by jobs name.");
	    c.get("command.top.help.args", "[jobname]");
	    Jobs.getGCManager().commandArgs.put("top", Arrays.asList("[jobname]"));
	    c.get("command.top.error.nojob", "Cant find any job with this name.");
	    c.get("command.top.output.topline", "&aTop&e 15 &aplayers by &e%jobname% &ajob");
	    c.get("command.top.output.list", "&e%number%&a. &e%playername% &alvl &e%level% &awith&e %exp% &aexp");
	    c.get("command.top.output.prev", "&e<<<<< Prev page &2|");
	    c.get("command.top.output.next", "&2|&e Next Page >>>>");
	    c.get("command.top.output.show", "&2Show from &e[from] &2until &e[until] &2top list");

	    c.get("command.gtop.help.info", "Shows top 15 players by global jobs level.");
	    c.get("command.gtop.help.args", "");
	    c.get("command.gtop.error.nojob", "Cant find any information.");
	    c.get("command.gtop.output.topline", "&aTop&e 15 &aplayers by global job level");
	    c.get("command.gtop.output.list", "&e%number%&a. &e%playername% &alvl &e%level% &awith&e %exp% &aexp");
	    c.get("command.gtop.output.prev", "&e<<<<< Prev page &2|");
	    c.get("command.gtop.output.next", "&2|&e Next Page >>>>");
	    c.get("command.gtop.output.show", "&2Show from &e[from] &2until &e[until] &2global top list");

	    c.get("command.log.help.info", "Shows statistics.");
	    c.get("command.log.help.args", "[playername]");
	    Jobs.getGCManager().commandArgs.put("log", Arrays.asList("[playername]"));
	    c.get("command.log.output.topline", "&7************************* &6%playername% &7*************************");
	    c.get("command.log.output.list", "&7* &6%number%. &3%action%: &6%item% &eqty: %qty% &6money: %money% &eexp: %exp%");
	    c.get("command.log.output.bottomline", "&7***********************************************************");
	    c.get("command.log.output.prev", "&e<<<<< Prev page &2|");
	    c.get("command.log.output.next", "&2|&e Next Page >>>>");
	    c.get("command.log.output.nodata", "&cData not found");

	    c.get("command.glog.help.info", "Shows global statistics.");
	    c.get("command.glog.help.args", "");
	    c.get("command.glog.output.topline", "&7*********************** &6Global statistics &7***********************");
	    c.get("command.glog.output.list", "&7* &6%number%. &3%username% &e%action%: &6%item% &eqty: %qty% &6money: %money% &eexp: %exp%");
	    c.get("command.glog.output.bottomline", "&7**************************************************************");
	    c.get("command.glog.output.nodata", "&cData not found");

	    c.get("command.transfer.help.info", "Transfer a player's job from an old job to a new job.");
	    c.get("command.transfer.help.args", "[playername] [oldjob] [newjob]");
	    Jobs.getGCManager().commandArgs.put("transfer", Arrays.asList("[playername]", "[oldjob]", "[jobname]"));
	    c.get("command.transfer.output.target", "You have been transferred from %oldjobname% to %newjobname%.");

	    c.get("command.promote.help.info", "Promote the player X levels in a job.");
	    c.get("command.promote.help.args", "[playername] [jobname] [levels]");
	    Jobs.getGCManager().commandArgs.put("promote", Arrays.asList("[playername]", "[jobname]", "[levels]"));
	    c.get("command.promote.output.target", "You have been promoted %levelsgained% levels in %jobname%.");

	    c.get("command.demote.help.info", "Demote the player X levels in a job.");
	    c.get("command.demote.help.args", "[playername] [jobname] [levels]");
	    Jobs.getGCManager().commandArgs.put("demote", Arrays.asList("[playername]", "[jobname]", "[levels]"));
	    c.get("command.demote.output.target", "You have been demoted %levelslost% levels in %jobname%.");

	    c.get("command.grantxp.help.info", "Grant the player X experience in a job.");
	    c.get("command.grantxp.help.args", "[playername] [jobname] [xp]");
	    Jobs.getGCManager().commandArgs.put("grantxp", Arrays.asList("[playername]", "[jobname]", "[xp]"));
	    c.get("command.grantxp.output.target", "You have been granted %xpgained% experience in %jobname%.");

	    c.get("command.removexp.help.info", "Remove X experience from the player in a job.");
	    c.get("command.removexp.help.args", "[playername] [jobname] [xp]");
	    Jobs.getGCManager().commandArgs.put("removexp", Arrays.asList("[playername]", "[jobname]", "[xp]"));
	    c.get("command.removexp.output.target", "You have lost %xplost% experience in %jobname%.");

	    c.get("command.signupdate.help.info", "Manualy updates sign by its name");
	    c.get("command.signupdate.help.args", "[jobname]");
	    Jobs.getGCManager().commandArgs.put("signupdate", Arrays.asList("[jobname]"));

	    c.get("command.reload.help.info", "Reload configurations.");

	    c.get("command.toggle.help.info", "Toggles payment output on action bar.");
	    c.get("command.toggle.help.args", "");
	    c.get("command.toggle.output.turnedoff", "&4This feature are turned off!");
	    c.get("command.toggle.output.paid.main", "&aYou got:");
	    c.get("command.toggle.output.paid.money", "&e[amount] money");
	    c.get("command.toggle.output.paid.exp", "&7[exp] exp");
	    c.get("command.toggle.output.paid.points", "&6[points] points");
	    c.get("command.toggle.output.on", "&aToggled: &aON");
	    c.get("command.toggle.output.off", "&aToggled: &4OFF");
	    Jobs.getGCManager().commandArgs.put("toggle", Arrays.asList("ActionBar%%BossBar"));

	    c.get("message.skillup.broadcast", "%playername% has been promoted to a %titlename% %jobname%.");
	    c.get("message.skillup.nobroadcast", "Congratulations, you have been promoted to a %titlename% %jobname%.");

	    c.get("message.levelup.broadcast", "%playername% is now a level %joblevel% %jobname%.");
	    c.get("message.levelup.nobroadcast", "You are now a level %joblevel% %jobname%.");

	    c.get("message.cowtimer", "&eYou still need to wait &6%time% &esec to get paid for this job.");
	    c.get("message.blocktimer", "&eYou need to wait: &3[time] &esec more to get paid for this!");
	    c.get("message.placeblocktimer", "&eYou cant place block faster than &6[time] &esec interval in same place!");
	    c.get("message.taxes", "&3[amount] &eserver taxes where transfered to this account");

	    c.get("message.boostStarted", "&eJobs boost time have been started!");
	    c.get("message.boostStoped", "&eJobs boost time have been ended!");

	    c.get("message.crafting.fullinventory", "Your inventory is full!");

	    c.get("signs.List", "&0[number].&8[player]&7:&4[level]");
	    c.get("signs.SpecialList.1.1", "&b** &8First &b**");
	    c.get("signs.SpecialList.1.2", "&9[player]");
	    c.get("signs.SpecialList.1.3", "&8[level] level");
	    c.get("signs.SpecialList.1.4", "&b************");
	    c.get("signs.SpecialList.2.1", "&b** &8Second &b**");
	    c.get("signs.SpecialList.2.2", "&9[player]");
	    c.get("signs.SpecialList.2.3", "&8[level] level");
	    c.get("signs.SpecialList.2.4", "&b************");
	    c.get("signs.SpecialList.3.1", "&b** &8Third &b**");
	    c.get("signs.SpecialList.3.2", "&9[player]");
	    c.get("signs.SpecialList.3.3", "&8[level] level");
	    c.get("signs.SpecialList.3.4", "&b************");
	    c.get("signs.cantcreate", "&4You can't create this sign!");
	    c.get("signs.cantdestroy", "&4You can't destroy this sign!");
	    c.get("signs.topline", "&2[Jobs]");
	    c.get("signs.secondline.join", "&2Join");
	    c.get("signs.secondline.leave", "&4Leave");
	    c.get("signs.secondline.toggle", "&2Toggle");
	    c.get("signs.secondline.top", "&2Top");
	    c.get("signs.secondline.browse", "&2Browse");
	    c.get("signs.secondline.stats", "&2Stats");
	    c.get("signs.secondline.limit", "&2Limit");
	    c.get("signs.secondline.info", "&2Info");
	    c.get("signs.secondline.archive", "&2Archive");

	    //c.get("scoreboard.clear", "&eIf you want to remove scoreboard, type &2/jobs top clear");
	    c.get("scoreboard.topline", "&2Top &e%jobname%");
	    c.get("scoreboard.gtopline", "&2Global top list");
	    c.get("scoreboard.lines", "&2%number%. &e%playername%");

//	    keys = new ArrayList<String>(c.getC().getConfigurationSection("signs.secondline").getKeys(false));

	    // Write back config
	    try {
		c.getW().save(f);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
