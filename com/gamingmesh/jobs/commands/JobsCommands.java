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

package com.gamingmesh.jobs.commands;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.Gui.GuiTools;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.GiveItem;
import com.gamingmesh.jobs.stuff.OfflinePlayerList;
import com.gamingmesh.jobs.stuff.Perm;
import com.gamingmesh.jobs.stuff.Sorting;
import com.gamingmesh.jobs.stuff.TimeManage;
import com.gamingmesh.jobs.stuff.TranslateName;

public class JobsCommands implements CommandExecutor {
    private static final String label = "jobs";
    private JobsPlugin plugin;

    public JobsCommands(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (args.length == 0)
	    return help(sender);
	String cmd = args[0].toLowerCase();

	try {
	    Method m = getClass().getMethod(cmd, CommandSender.class, String[].class);
	    if (m.isAnnotationPresent(JobCommand.class)) {
		if (!hasCommandPermission(sender, cmd)) {
		    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
		    return true;
		}
		String[] myArgs = reduceArgs(args);
		if (myArgs.length > 0) {
		    if (myArgs[myArgs.length - 1].equals("?")) {
			sendUsage(sender, cmd);
			return true;
		    }
		}

		return (Boolean) m.invoke(this, sender, myArgs);
	    }
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	}

	return help(sender);
    }

    private static String[] reduceArgs(String[] args) {
	if (args.length <= 1)
	    return new String[0];

	return Arrays.copyOfRange(args, 1, args.length);
    }

    public static boolean hasCommandPermission(CommandSender sender, String cmd) {
	return sender.hasPermission("jobs.command." + cmd);
    }

    private String getUsage(String cmd) {
	StringBuilder builder = new StringBuilder();
	builder.append(ChatColor.GREEN.toString());
	builder.append('/').append(label).append(' ');
	builder.append(cmd);
	builder.append(ChatColor.YELLOW);
	String key = "command." + cmd + ".help.args";
	if (Language.containsKey(key)) {
	    builder.append(' ');
	    builder.append(Language.getMessage(key));
	}
	return builder.toString();
    }

    public void sendUsage(CommandSender sender, String cmd) {
	String message = ChatColor.YELLOW + Language.getMessage("command.help.output.usage");
	message = message.replace("%usage%", getUsage(cmd));
	sender.sendMessage(message);
	sender.sendMessage(ChatColor.YELLOW + "* " + Language.getMessage("command." + cmd + ".help.info"));
    }

    public void sendValidActions(CommandSender sender) {
	StringBuilder builder = new StringBuilder();
	boolean first = true;
	for (ActionType action : ActionType.values()) {
	    if (!first)
		builder.append(',');
	    builder.append(action.getName());
	    first = false;

	}
	sender.sendMessage(Language.getMessage("command.info.help.actions").replace("%actions%", builder.toString()));
    }

    protected boolean help(CommandSender sender) {
	sender.sendMessage(Language.getMessage("command.info.help.title"));
	for (Method m : getClass().getMethods()) {
	    if (m.isAnnotationPresent(JobCommand.class)) {
		String cmd = m.getName();
		if (!hasCommandPermission(sender, cmd))
		    continue;
		sender.sendMessage(getUsage(cmd));
	    }
	}
	sender.sendMessage(ChatColor.YELLOW + Language.getMessage("command.help.output.info"));
	return true;
    }

    @JobCommand
    public boolean fixnames(CommandSender sender, String[] args) throws IOException {

	if (args.length > 0) {
	    sendUsage(sender, "fixnames");
	    return true;
	}

	sender.sendMessage(ChatColor.GOLD + "[Jobs] Starting name fix proccess, this can take up to minute depending on your data base size.");
	Jobs.getJobsDAO().fixName(sender);

	return true;
    }

    @JobCommand
    public boolean fixuuid(CommandSender sender, String[] args) throws IOException {

	if (args.length > 0) {
	    sendUsage(sender, "fixuuid");
	    return true;
	}

	sender.sendMessage(ChatColor.GOLD + "[Jobs] Starting uuid fix proccess, this can take up to minute depending on your data base size.");
	Jobs.getJobsDAO().fixUuid(sender);

	return true;
    }

    @JobCommand
    public boolean convert(CommandSender sender, String[] args) throws IOException {

	if (!(sender instanceof Player))
	    return false;

	if (args.length > 0) {
	    sendUsage(sender, "convert");
	    return true;
	}

	Player pSender = (Player) sender;

	List<Convert> list = null;
	List<Convert> archivelist = null;

	try {
	    list = Jobs.getJobsDAO().convertDatabase(pSender, "jobs");
	    archivelist = Jobs.getJobsDAO().convertDatabase(pSender, "archive");
	} catch (SQLException e) {
	    e.printStackTrace();
	    sender.sendMessage(ChatColor.RED + "Can't read data from data base, please send error log to dev's.");
	    return false;
	}
	Jobs.ChangeDatabase();

	if (list == null & archivelist == null)
	    return false;
	try {
	    Jobs.getJobsDAO().continueConvertions(list, "jobs");
	    Jobs.getJobsDAO().continueConvertions(archivelist, "archive");
	} catch (SQLException e) {
	    e.printStackTrace();
	    sender.sendMessage(ChatColor.RED + "Can't write data to data base, please send error log to dev's.");
	    return false;
	}

	Jobs.reload();

	String from = "MysSQL";
	String to = "SqLite";

	if (ConfigManager.getJobsConfiguration().storageMethod.equalsIgnoreCase("sqlite")) {
	    from = "SqLite";
	    to = "MySQL";
	}

	sender.sendMessage(ChatColor.GOLD + "Data base was converted from " + ChatColor.GREEN + from + ChatColor.GOLD + " to " + ChatColor.GREEN + to + ChatColor.GOLD
	    + "! Now you can stop the server, change storage-method to " + ChatColor.GREEN + to + ChatColor.GOLD
	    + " in general config file and start server again on your new database system.");

	return true;
    }

    @JobCommand
    public boolean join(CommandSender sender, String[] args) {
	if (!(sender instanceof Player))
	    return false;

	if (args.length != 1 && args.length != 0) {
	    sendUsage(sender, "join");
	    return true;
	}

	if (args.length == 0) {
	    if (sender instanceof Player && ConfigManager.getJobsConfiguration().JobsGUIOpenOnJoin)
		((Player) sender).openInventory(GuiTools.CreateJobsGUI((Player) sender));
	    else
		return false;
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    // job does not exist
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}

	if (!hasJobPermission(pSender, job)) {
	    // you do not have permission to join the job
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
	    return true;
	}

	if (jPlayer.isInJob(job)) {
	    // already in job message
	    String message = ChatColor.RED + Language.getMessage("command.join.error.alreadyin");
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}

	if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
	    String message = ChatColor.RED + Language.getMessage("command.join.error.fullslots");
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}

	int confMaxJobs = ConfigManager.getJobsConfiguration().getMaxJobs();
	short PlayerMaxJobs = (short) jPlayer.getJobProgression().size();
	if (confMaxJobs > 0 && PlayerMaxJobs >= confMaxJobs && !Jobs.getPlayerManager().getJobsLimit(pSender, PlayerMaxJobs)) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.join.error.maxjobs"));
	    return true;
	}

	Jobs.getPlayerManager().joinJob(jPlayer, job);

	String message = Language.getMessage("command.join.success");
	message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
	sender.sendMessage(message);
	return true;
    }

    @JobCommand
    public boolean leave(CommandSender sender, String[] args) {
	if (!(sender instanceof Player))
	    return false;

	if (args.length < 1) {
	    sendUsage(sender, "leave");
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}

	Jobs.getPlayerManager().leaveJob(jPlayer, job);
	String message = Language.getMessage("command.leave.success");
	message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
	sender.sendMessage(message);
	return true;
    }

    @JobCommand
    public boolean leaveall(CommandSender sender, String[] args) {
	if (!(sender instanceof Player))
	    return false;

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.size() == 0) {
	    sender.sendMessage(Language.getMessage("command.leaveall.error.nojobs"));
	    return true;
	}

	Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	sender.sendMessage(Language.getMessage("command.leaveall.success"));
	return true;
    }

    @JobCommand
    public boolean info(CommandSender sender, String[] args) {
	if (!(sender instanceof Player))
	    return false;

	if (args.length < 1) {
	    sendUsage(sender, "info");
	    sendValidActions(sender);
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}

	if (ConfigManager.getJobsConfiguration().hideJobsInfoWithoutPermission)
	    if (!hasJobPermission(pSender, job)) {
		sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
		return true;
	    }

	String type = "";
	if (args.length >= 2) {
	    type = args[1];
	}
	sender.sendMessage(jobInfoMessage(jPlayer, job, type).split("\n"));
	return true;
    }

    @JobCommand
    public boolean stats(CommandSender sender, String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!sender.hasPermission("jobs.command.admin.stats")) {
		sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
		return true;
	    }
//	    @SuppressWarnings("deprecation")
//	    OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);

	    OfflinePlayer offlinePlayer = OfflinePlayerList.getPlayer(args[0]);
	    if (offlinePlayer != null)
		jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);
	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    sendUsage(sender, "stats");
	    return true;
	}

	if (jPlayer.getJobProgression().size() == 0) {
	    sender.sendMessage(Language.getMessage("command.stats.error.nojob"));
	    return true;
	}

	for (JobProgression jobProg : jPlayer.getJobProgression()) {
	    sender.sendMessage(jobStatsMessage(jobProg).split("\n"));
	}
	return true;
    }

    @JobCommand
    public boolean toggle(CommandSender sender, String[] args) {

	if (!ConfigManager.getJobsConfiguration().ToggleActionBar) {
	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.toggle.output.turnedoff"));
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;
	if (args.length > 0) {
	    sendUsage(sender, "toggle");
	    return true;
	}

	String PlayerName = sender.getName();

	if (PlayerName == null) {
	    sendUsage(sender, "toggle");
	    return true;
	}

	if (Jobs.actionbartoggle.containsKey(PlayerName))
	    if (Jobs.actionbartoggle.get(PlayerName)) {
		Jobs.actionbartoggle.put(PlayerName, false);
		sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.toggle.output.off"));
	    } else {
		Jobs.actionbartoggle.put(PlayerName, true);
		sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.toggle.output.on"));
	    }
	else {
	    Jobs.actionbartoggle.put(PlayerName, true);
	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.toggle.output.on"));
	}

	return true;
    }

    @JobCommand
    public boolean expboost(CommandSender sender, String[] args) {
	//if (!(sender instanceof Player))
	//	return false;

	if (args.length > 2 || args.length <= 1) {
	    sendUsage(sender, "expboost");
	    return true;
	}

	double rate = 1.0;
	if (!args[1].equalsIgnoreCase("all") && !args[0].equalsIgnoreCase("reset"))
	    try {
		rate = Double.parseDouble(args[1]);
	    } catch (NumberFormatException e) {
		sendUsage(sender, "expboost");
		return true;
	    }

	String PlayerName = sender.getName();
	String jobName = args[0];
	Job job = Jobs.getJob(jobName);

	if (PlayerName == null) {
	    sendUsage(sender, "expboost");
	    return true;
	}

	if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("all")) {
	    for (Job one : Jobs.getJobs()) {
		one.setExpBoost(1.0);
	    }
	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.expboost.output.allreset"));
	    return true;
	} else if (args[0].equalsIgnoreCase("reset")) {
	    boolean found = false;
	    for (Job one : Jobs.getJobs()) {
		if (one.getName().equalsIgnoreCase(args[1])) {
		    one.setExpBoost(1.0);
		    found = true;
		    break;
		}
	    }

	    if (found) {
		sender.sendMessage(ChatColor.RED + Language.getMessage("command.expboost.output.jobsboostreset").replace("%jobname%", job.getName()));
		return true;
	    }
	}

	if (args[0].equalsIgnoreCase("all")) {

	    for (Job one : Jobs.getJobs()) {
		one.setExpBoost(rate);
	    }

	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.expboost.output.boostalladded").replace("%boost%", String.valueOf(rate)));
	    return true;
	} else {
	    if (job == null) {
		sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.error.job"));
		return true;
	    }
	    job.setExpBoost(rate);
	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.expboost.output.boostadded").replace("%boost%", String.valueOf(rate)).replace("%jobname%",
		job.getName()));
	    return true;
	}
    }

    @JobCommand
    public boolean moneyboost(CommandSender sender, String[] args) {
	//if (!(sender instanceof Player))
	//	return false;

	if (args.length > 2 || args.length <= 1) {
	    sendUsage(sender, "moneyboost");
	    return true;
	}

	double rate = 1.0;
	if (!args[1].equalsIgnoreCase("all") && !args[0].equalsIgnoreCase("reset"))
	    try {
		rate = Double.parseDouble(args[1]);
	    } catch (NumberFormatException e) {
		sendUsage(sender, "moneyboost");
		return true;
	    }

	String PlayerName = sender.getName();
	String jobName = args[0];
	Job job = Jobs.getJob(jobName);

	if (PlayerName == null) {
	    sendUsage(sender, "moneyboost");
	    return true;
	}

	if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("all")) {
	    for (Job one : Jobs.getJobs()) {
		one.setMoneyBoost(1.0);
	    }
	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.moneyboost.output.allreset"));
	    return true;
	} else if (args[0].equalsIgnoreCase("reset")) {
	    boolean found = false;
	    for (Job one : Jobs.getJobs()) {
		if (one.getName().equalsIgnoreCase(args[1])) {
		    one.setMoneyBoost(1.0);
		    found = true;
		    break;
		}
	    }

	    if (found) {
		sender.sendMessage(ChatColor.RED + Language.getMessage("command.moneyboost.output.jobsboostreset").replace("%jobname%", job.getName()));
		return true;
	    }
	}

	if (args[0].equalsIgnoreCase("all")) {

	    for (Job one : Jobs.getJobs()) {
		one.setMoneyBoost(rate);
	    }

	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.moneyboost.output.boostalladded").replace("%boost%", String.valueOf(rate)));
	    return true;
	} else {
	    if (job == null) {
		sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.error.job"));
		return true;
	    }
	    job.setMoneyBoost(rate);
	    sender.sendMessage(ChatColor.GREEN + Language.getMessage("command.moneyboost.output.boostadded").replace("%boost%", String.valueOf(rate)).replace("%jobname%",
		job.getName()));
	    return true;
	}
    }

    @SuppressWarnings("deprecation")
    @JobCommand
    public boolean archive(CommandSender sender, String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!sender.hasPermission("jobs.command.admin.archive")) {
		sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.permission"));
		return true;
	    }
	    OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	    jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    sendUsage(sender, "archive");
	    return true;
	}

	List<String> AllJobs = Jobs.getJobsDAO().getJobsFromArchive(jPlayer);

	if (AllJobs.size() == 0) {
	    sender.sendMessage(Language.getMessage("command.archive.error.nojob"));
	    return true;
	}

	for (String jobInfo : AllJobs) {
	    sender.sendMessage(jobStatsMessage(jobInfo));
	}
	return true;
    }

    @JobCommand
    public boolean browse(CommandSender sender, String[] args) {
	ArrayList<String> lines = new ArrayList<String>();
	for (Job job : Jobs.getJobs()) {
	    if (ConfigManager.getJobsConfiguration().getHideJobsWithoutPermission()) {
		if (!hasJobPermission(sender, job))
		    continue;
	    }
	    StringBuilder builder = new StringBuilder();
	    builder.append("  ");
	    builder.append(job.getChatColor().toString());
	    builder.append(job.getName());
	    if (job.getMaxLevel() > 0) {
		builder.append(ChatColor.WHITE.toString());
		builder.append(Language.getMessage("command.info.help.max"));
		if (Perm.hasPermission(sender, "jobs." + job.getName() + ".vipmaxlevel") && job.getVipMaxLevel() != 0)
		    builder.append(job.getVipMaxLevel());
		else
		    builder.append(job.getMaxLevel());
	    }

	    if (ConfigManager.getJobsConfiguration().ShowTotalWorkers)
		builder.append(Language.getMessage("command.browse.output.totalWorkers").replace("[amount]", String.valueOf(job.getTotalPlayers())));

	    if (ConfigManager.getJobsConfiguration().useDynamicPayment && ConfigManager.getJobsConfiguration().ShowPenaltyBonus)
		if (job.getBonus() < 0)
		    builder.append(Language.getMessage("command.browse.output.penalty").replace("[amount]", String.valueOf((int) (job.getBonus() * 100) / 100.0 * -1)));
		else
		    builder.append(Language.getMessage("command.browse.output.bonus").replace("[amount]", String.valueOf((int) (job.getBonus() * 100) / 100.0)));

	    lines.add(builder.toString());
	    if (!job.getDescription().isEmpty()) {
		lines.add("  - " + job.getDescription().replace("/n", ""));
	    }
	}

	if (lines.size() == 0) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.browse.error.nojobs"));
	    return true;
	}

	if (sender instanceof Player && ConfigManager.getJobsConfiguration().JobsGUIOpenOnBrowse) {
	    ((Player) sender).openInventory(GuiTools.CreateJobsGUI((Player) sender));
	}

	if (ConfigManager.getJobsConfiguration().JobsGUIShowChatBrowse) {
	    sender.sendMessage(Language.getMessage("command.browse.output.header"));
	    for (String line : lines) {
		sender.sendMessage(line);
	    }
	    sender.sendMessage(Language.getMessage("command.browse.output.footer"));
	}
	return true;
    }

    @JobCommand
    public boolean playerinfo(CommandSender sender, String[] args) {
	if (args.length < 2) {
	    sendUsage(sender, "playerinfo");
	    sendValidActions(sender);
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	String jobName = args[1];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	String type = "";
	if (args.length >= 3) {
	    type = args[2];
	}
	sender.sendMessage(jobInfoMessage(jPlayer, job, type).split("\n"));
	return true;
    }

    @JobCommand
    public boolean reload(CommandSender sender, String[] args) {
	try {
	    Jobs.reload();
	    sender.sendMessage(Language.getMessage("command.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));

	    String message = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&4There was an error when performing a reload: ");
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    console.sendMessage(message);

	    //Jobs.getPluginLogger().severe("There was an error when performing a reload: ");
	    e.printStackTrace();
	}
	return true;
    }

    @JobCommand
    public boolean fire(CommandSender sender, String[] args) {
	if (args.length < 2) {
	    sendUsage(sender, "fire");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	if (!jPlayer.isInJob(job)) {
	    String message = ChatColor.RED + Language.getMessage("command.fire.error.nojob");
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}
	try {
	    Jobs.getPlayerManager().leaveJob(jPlayer, job);
	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		String message = Language.getMessage("command.fire.output.target");
		message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		player.sendMessage(message);
	    }

	    sender.sendMessage(Language.getMessage("command.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	}
	return true;
    }

    @JobCommand
    public boolean fireall(CommandSender sender, String[] args) {
	if (args.length < 1) {
	    sendUsage(sender, "fireall");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.size() == 0) {
	    sender.sendMessage(Language.getMessage("command.fireall.error.nojobs"));
	    return true;
	}

	try {
	    Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		player.sendMessage(Language.getMessage("command.fireall.output.target"));
	    }

	    sender.sendMessage(Language.getMessage("command.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	}
	return true;
    }

    @JobCommand
    public boolean employ(CommandSender sender, String[] args) {
	if (args.length < 2) {
	    sendUsage(sender, "employ");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	if (jPlayer.isInJob(job)) {
	    // already in job message
	    String message = ChatColor.RED + Language.getMessage("command.employ.error.alreadyin");
	    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}
	try {
	    // check if player already has the job
	    Jobs.getPlayerManager().joinJob(jPlayer, job);
	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		String message = Language.getMessage("command.employ.output.target");
		message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		player.sendMessage(message);
	    }

	    sender.sendMessage(Language.getMessage("command.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	}
	return true;
    }

    @JobCommand
    public boolean promote(CommandSender sender, String[] args) {
	if (args.length < 3) {
	    sendUsage(sender, "promote");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	try {
	    // check if player already has the job
	    if (jPlayer.isInJob(job)) {
		Integer levelsGained = Integer.parseInt(args[2]);
		Jobs.getPlayerManager().promoteJob(jPlayer, job, levelsGained);

		Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
		if (player != null) {
		    String message = Language.getMessage("command.promote.output.target");
		    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		    message = message.replace("%levelsgained%", Integer.valueOf(levelsGained).toString());
		    player.sendMessage(message);
		}

		sender.sendMessage(Language.getMessage("command.admin.success"));
	    }
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	}
	return true;
    }

    @JobCommand
    public boolean demote(CommandSender sender, String[] args) {
	if (args.length < 3) {
	    sendUsage(sender, "demote");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	try {
	    // check if player already has the job
	    if (jPlayer.isInJob(job)) {
		Integer levelsLost = Integer.parseInt(args[2]);
		Jobs.getPlayerManager().demoteJob(jPlayer, job, levelsLost);

		Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
		if (player != null) {
		    String message = Language.getMessage("command.demote.output.target");
		    message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		    message = message.replace("%levelslost%", Integer.valueOf(levelsLost).toString());
		    player.sendMessage(message);
		}

		sender.sendMessage(Language.getMessage("command.admin.success"));
	    }
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	}
	return true;
    }

    @JobCommand
    public boolean grantxp(CommandSender sender, String[] args) {
	if (args.length < 3) {
	    sendUsage(sender, "grantxp");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	double xpGained;
	try {
	    xpGained = Double.parseDouble(args[2]);
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	    return true;
	}
	if (xpGained <= 0) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	    return true;
	}
	// check if player already has the job
	if (jPlayer.isInJob(job)) {
	    Jobs.getPlayerManager().addExperience(jPlayer, job, xpGained);

	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		String message = Language.getMessage("command.grantxp.output.target");
		message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		message = message.replace("%xpgained%", Double.valueOf(xpGained).toString());
		player.sendMessage(message);
	    }

	    sender.sendMessage(Language.getMessage("command.admin.success"));
	}
	return true;
    }

    @JobCommand
    public boolean removexp(CommandSender sender, String[] args) {
	if (args.length < 3) {
	    sendUsage(sender, "removexp");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	double xpLost;
	try {
	    xpLost = Double.parseDouble(args[2]);
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	    return true;
	}
	if (xpLost <= 0) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	    return true;
	}
	// check if player already has the job
	if (jPlayer.isInJob(job)) {
	    Jobs.getPlayerManager().removeExperience(jPlayer, job, xpLost);

	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		String message = Language.getMessage("command.removexp.output.target");
		message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		message = message.replace("%xplost%", Double.valueOf(xpLost).toString());
		player.sendMessage(message);
	    }

	    sender.sendMessage(Language.getMessage("command.admin.success"));
	}
	return true;
    }

    @JobCommand
    public boolean transfer(CommandSender sender, String[] args) {
	if (args.length < 3) {
	    sendUsage(sender, "transfer");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job oldjob = Jobs.getJob(args[1]);
	Job newjob = Jobs.getJob(args[2]);
	if (oldjob == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	if (newjob == null) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	try {
	    if (jPlayer.isInJob(oldjob) && !jPlayer.isInJob(newjob)) {
		Jobs.getPlayerManager().transferJob(jPlayer, oldjob, newjob);

		Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
		if (player != null) {
		    String message = Language.getMessage("command.transfer.output.target");
		    message = message.replace("%oldjobname%", oldjob.getChatColor() + oldjob.getName() + ChatColor.WHITE);
		    message = message.replace("%newjobname%", newjob.getChatColor() + newjob.getName() + ChatColor.WHITE);
		    player.sendMessage(message);
		}

		sender.sendMessage(Language.getMessage("command.admin.success"));
	    }
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.admin.error"));
	}
	return true;
    }

    @JobCommand
    public boolean signupdate(CommandSender sender, String[] args) {
	if (args.length != 1) {
	    sendUsage(sender, "signupdate");
	    return true;
	}

	Job oldjob = Jobs.getJob(args[0]);

	if (oldjob == null && !args[0].equalsIgnoreCase("gtoplist")) {
	    sender.sendMessage(ChatColor.RED + Language.getMessage("command.error.job"));
	    return true;
	}
	if (!args[0].equalsIgnoreCase("gtoplist"))
	    Jobs.getSignUtil().SignUpdate(oldjob.getName());
	else
	    Jobs.getSignUtil().SignUpdate("gtoplist");

	return true;
    }

    @JobCommand
    public boolean gtop(CommandSender sender, String[] args) {

	if (args.length != 1 && args.length != 0) {
	    sendUsage(sender, "gtop");
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;
	Player player = (Player) sender;

	if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    return true;
	}

	int start = 0;
	if (args.length == 1)
	    try {
		start = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	if (start < 0)
	    start = 0;

	List<TopList> FullList = Jobs.getJobsDAO().getGlobalTopList(start);
	if (FullList.size() <= 0) {
	    player.sendMessage(ChatColor.RED + Language.getMessage("command.gtop.error.nojob"));
	    return false;
	}

	if (!ConfigManager.getJobsConfiguration().ShowToplistInScoreboard) {
	    player.sendMessage(Language.getMessage("command.gtop.output.topline"));
	    int i = start;
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";
		player.sendMessage(Language.getMessage("command.gtop.output.list").replace("%number%", String.valueOf(i)).replace("%playername%", PlayerName).replace(
		    "%level%", String.valueOf(One.getLevel())).replace("%exp%", String.valueOf(One.getExp())));
	    }
	} else {

	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

	    ScoreboardManager manager = Bukkit.getScoreboardManager();
	    Scoreboard board = manager.getNewScoreboard();
	    Objective objective = board.registerNewObjective("JobsTopPlayers", "dummy");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    objective.setDisplayName(Language.getMessage("scoreboard.gtopline"));
	    int i = start;
	    for (TopList One : FullList) {
		i++;
		String playername = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";

		Score score = objective.getScore(Language.getMessage("scoreboard.lines").replace("%number%", String.valueOf(i)).replace("%playername%", playername));
		score.setScore(One.getLevel());

	    }
	    player.setScoreboard(board);

	    Jobs.getScboard().addNew(player);

	    //player.sendMessage(ChatColor.GOLD + Language.getMessage("scoreboard.clear"));

	    int from = start;
	    if (start >= 15)
		from = start - 15;
	    int until = start + 15;

	    String prev = "[\"\",{\"text\":\"" + Language.getMessage("command.gtop.output.prev") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs gtop "
		+ from + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Language.getMessage("command.gtop.output.show")
		    .replace("[from]", String.valueOf(from)).replace("[until]", String.valueOf((from + 15))) + "\"}]}}}";
	    String next = " {\"text\":\"" + Language.getMessage("command.gtop.output.next") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs gtop "
		+ until + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Language.getMessage(
		    "command.gtop.output.show").replace("[from]", String.valueOf(until + 1)).replace("[until]", String.valueOf((until + 15))) + "\"}]}}}]";

	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + prev + "," + next);
	}
	return true;
    }

    @JobCommand
    public boolean top(CommandSender sender, String[] args) {

	if (args.length != 1 && args.length != 2) {
	    sendUsage(sender, "top");
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;
	Player player = (Player) sender;

	if (args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    return true;
	}

	int start = 0;
	if (args.length == 2)
	    try {
		start = Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	if (start < 0)
	    start = 0;

	List<TopList> FullList = Jobs.getJobsDAO().toplist(args[0], start);
	if (FullList.size() <= 0) {
	    player.sendMessage(ChatColor.RED + Language.getMessage("command.top.error.nojob"));
	    return false;
	}

	Job job = Jobs.getJob(args[0]);
	String jobName = args[0];
	if (job != null)
	    jobName = job.getName();

	if (!ConfigManager.getJobsConfiguration().ShowToplistInScoreboard) {
	    player.sendMessage(Language.getMessage("command.top.output.topline").replace("%jobname%", jobName));
	    int i = start;
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";
		player.sendMessage(Language.getMessage("command.top.output.list").replace("%number%", String.valueOf(i)).replace("%playername%", PlayerName).replace(
		    "%level%", String.valueOf(One.getLevel())).replace("%exp%", String.valueOf(One.getExp())));
	    }
	} else {

	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

	    ScoreboardManager manager = Bukkit.getScoreboardManager();
	    Scoreboard board = manager.getNewScoreboard();
	    Objective objective = board.registerNewObjective("JobsTopPlayers", "dummy");
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    objective.setDisplayName(Language.getMessage("scoreboard.topline").replace("%jobname%", jobName));
	    int i = start;
	    for (TopList One : FullList) {
		i++;
		String playername = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";

		Score score = objective.getScore(Language.getMessage("scoreboard.lines").replace("%number%", String.valueOf(i)).replace("%playername%", playername));
		score.setScore(One.getLevel());

	    }
	    player.setScoreboard(board);
	    //player.sendMessage(ChatColor.GOLD + Language.getMessage("scoreboard.clear"));

	    Jobs.getScboard().addNew(player);

	    int from = start;
	    if (start >= 15)
		from = start - 15;
	    int until = start + 15;

	    String prev = "[\"\",{\"text\":\"" + Language.getMessage("command.top.output.prev") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs top "
		+ jobName + " " + from + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Language.getMessage(
		    "command.top.output.show").replace("[from]", String.valueOf(from)).replace("[until]", String.valueOf((from + 15))) + "\"}]}}}";
	    String next = " {\"text\":\"" + Language.getMessage("command.top.output.next") + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jobs top "
		+ jobName + " " + until + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Language.getMessage(
		    "command.top.output.show").replace("[from]", String.valueOf(until + 1)).replace("[until]", String.valueOf((until + 15))) + "\"}]}}}]";

	    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + prev + "," + next);
	}
	return true;
    }

    @JobCommand
    public boolean give(CommandSender sender, String[] args) {

	if (args.length < 1 || Jobs.getJob(args[0]) == null && Jobs.getJob(args[1]) == null) {
	    sendUsage(sender, "give");
	    return true;
	}

	if (args.length == 2 && sender instanceof Player) {
	    Job job = Jobs.getJob(args[0]);
	    for (JobItems item : job.getItems()) {
		if (item.getNode().equalsIgnoreCase(args[1])) {
		    GiveItem.GiveItemForPlayer((Player) sender, item.getId(), 0, 1, item.getName(), item.getLore(), item.getenchants());
		    return true;
		}
	    }
	    sender.sendMessage(Language.getMessage("command.give.output.noitem"));
	    return true;
	} else if (args.length == 3) {
	    Job job = Jobs.getJob(args[1]);
	    Player player = Bukkit.getPlayer(args[0]);
	    if (player == null) {
		sender.sendMessage(Language.getMessage("command.give.output.notonline").replace("%playername%", args[0]));
		return true;
	    }
	    for (JobItems item : job.getItems()) {
		if (item.getNode().equalsIgnoreCase(args[2])) {
		    GiveItem.GiveItemForPlayer(player, item.getId(), 0, 1, item.getName(), item.getLore(), item.getenchants());
		    return true;
		}
	    }
	    sender.sendMessage(Language.getMessage("command.give.output.noitem"));
	    return true;
	} else {
	    sendUsage(sender, "give");
	    return true;
	}
    }

    @JobCommand
    public boolean limit(CommandSender sender, String[] args) {
	if (args.length > 0) {
	    sendUsage(sender, "limit");
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;

	Player player = (Player) sender;

	if (!ConfigManager.getJobsConfiguration().EconomyLimitUse && !ConfigManager.getJobsConfiguration().EconomyExpLimitUse) {
	    player.sendMessage(Language.getMessage("command.limit.output.notenabled"));
	    return true;
	}
	JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	String playername = player.getName();

	if (ConfigManager.getJobsConfiguration().EconomyLimitUse)
	    if (Jobs.paymentLimit.containsKey(playername) && Jobs.paymentLimit.get(playername).GetLeftTime(ConfigManager
		.getJobsConfiguration().EconomyLimitTimeLimit) > 0) {
		PaymentData data = Jobs.paymentLimit.get(playername);

		String lefttimemessage = Language.getMessage("command.limit.output.lefttime").replace("%hour%", String.valueOf(data.GetLeftHour(ConfigManager
		    .getJobsConfiguration().EconomyLimitTimeLimit)));
		lefttimemessage = lefttimemessage.replace("%min%", String.valueOf(data.GetLeftMin(ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit)));
		lefttimemessage = lefttimemessage.replace("%sec%", String.valueOf(data.GetLeftsec(ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit)));
		player.sendMessage(lefttimemessage);

		String message = Language.getMessage("command.limit.output.moneylimit");
		message = message.replace("%money%", String.valueOf(data.GetAmountBylimit(JPlayer.getMoneyLimit())));
		message = message.replace("%totalmoney%", String.valueOf(JPlayer.getMoneyLimit()));
		player.sendMessage(message);

	    } else {

		int lefttime1 = ConfigManager.getJobsConfiguration().EconomyLimitTimeLimit;

		int hour = 0;
		int min = 0;
		int sec = 0;

		if (lefttime1 >= 3600) {
		    hour = lefttime1 / 3600;
		    lefttime1 = lefttime1 - (hour * 3600);
		    if (lefttime1 > 60 && lefttime1 < 3600) {
			min = lefttime1 / 60;
			sec = lefttime1 - (min * 60);
		    } else if (lefttime1 < 60)
			sec = lefttime1;
		} else if (lefttime1 > 60 && lefttime1 < 3600) {
		    min = lefttime1 / 60;
		    lefttime1 = lefttime1 - (min * 60);
		} else
		    sec = lefttime1;

		String message = Language.getMessage("command.limit.output.lefttime").replace("%hour%", String.valueOf(hour));
		message = message.replace("%min%", String.valueOf(min));
		message = message.replace("%sec%", String.valueOf(sec));
		player.sendMessage(message);

		message = Language.getMessage("command.limit.output.moneylimit").replace("%money%", "0.0");
		message = message.replace("%totalmoney%", String.valueOf(JPlayer.getMoneyLimit()));
		player.sendMessage(message);
	    }

	if (ConfigManager.getJobsConfiguration().EconomyExpLimitUse)
	    if (Jobs.ExpLimit.containsKey(playername) && Jobs.ExpLimit.get(playername).GetLeftTime(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit) > 0) {
		PaymentData data = Jobs.ExpLimit.get(playername);

		String lefttimemessage = Language.getMessage("command.limit.output.leftexptime").replace("%hour%", String.valueOf(data.GetLeftHour(ConfigManager
		    .getJobsConfiguration().EconomyExpTimeLimit)));
		lefttimemessage = lefttimemessage.replace("%min%", String.valueOf(data.GetLeftMin(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit)));
		lefttimemessage = lefttimemessage.replace("%sec%", String.valueOf(data.GetLeftsec(ConfigManager.getJobsConfiguration().EconomyExpTimeLimit)));
		player.sendMessage(lefttimemessage);

		String message = Language.getMessage("command.limit.output.explimit");
		message = message.replace("%exp%", String.valueOf(data.GetExpBylimit(JPlayer.getExpLimit())));
		message = message.replace("%totalexp%", String.valueOf(JPlayer.getExpLimit()));
		player.sendMessage(message);

	    } else {

		int lefttime1 = ConfigManager.getJobsConfiguration().EconomyExpTimeLimit;

		int hour = 0;
		int min = 0;
		int sec = 0;

		if (lefttime1 >= 3600) {
		    hour = lefttime1 / 3600;
		    lefttime1 = lefttime1 - (hour * 3600);
		    if (lefttime1 > 60 && lefttime1 < 3600) {
			min = lefttime1 / 60;
			sec = lefttime1 - (min * 60);
		    } else if (lefttime1 < 60)
			sec = lefttime1;
		} else if (lefttime1 > 60 && lefttime1 < 3600) {
		    min = lefttime1 / 60;
		    lefttime1 = lefttime1 - (min * 60);
		} else
		    sec = lefttime1;

		String message = Language.getMessage("command.limit.output.leftexptime").replace("%hour%", String.valueOf(hour));
		message = message.replace("%min%", String.valueOf(min));
		message = message.replace("%sec%", String.valueOf(sec));
		player.sendMessage(message);

		message = Language.getMessage("command.limit.output.explimit").replace("%exp%", "0.0");
		message = message.replace("%totalexp%", String.valueOf(JPlayer.getExpLimit()));
		player.sendMessage(message);
	    }

	return true;
    }

    @JobCommand
    public boolean log(CommandSender sender, String[] args) {

	if (!(sender instanceof Player) && args.length != 1)
	    return false;

	if (args.length != 1 && args.length != 0) {
	    sendUsage(sender, "log");
	    return true;
	}
	JobsPlayer JPlayer = null;
	if (args.length == 0)
	    JPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	else if (args.length == 1) {
	    if (!sender.hasPermission("jobs.commands.log.others")) {
		sender.sendMessage(Language.getMessage("command.error.permission"));
		return true;
	    }
	    JPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	    if (JPlayer == null)
		JPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(OfflinePlayerList.getPlayer(args[0]));
	}

	if (JPlayer == null) {
	    sendUsage(sender, "log");
	    return true;
	}

	List<Log> logList = JPlayer.getLog();

	if (logList.size() == 0) {
	    sender.sendMessage(Language.getMessage("command.log.output.bottomline"));
	    sender.sendMessage(Language.getMessage("command.log.output.nodata"));
	    sender.sendMessage(Language.getMessage("command.log.output.bottomline"));
	    return true;
	}

	Map<String, Double> unsortMap = new HashMap<String, Double>();

	for (Log one : logList) {
	    HashMap<String, LogAmounts> AmountList = one.getAmountList();
	    for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
		unsortMap.put(oneMap.getKey(), oneMap.getValue().getMoney());
	    }
	}

	unsortMap = Sorting.sortDoubleDESC(unsortMap);
	int count = 0;
	int max = 10;
	sender.sendMessage(Language.getMessage("command.log.output.topline").replace("%playername%", JPlayer.getUserName()));
	for (Log one : logList) {
	    HashMap<String, LogAmounts> AmountList = one.getAmountList();
	    for (Entry<String, Double> oneSorted : unsortMap.entrySet()) {
		for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
		    if (oneMap.getKey().equalsIgnoreCase(oneSorted.getKey())) {
			count++;
			String msg = Language.getMessage("command.log.output.list")
			    .replace("%number%", String.valueOf(count))
			    .replace("%action%", one.getActionType())
			    .replace("%item%", oneMap.getValue().getItemName().replace(":0", "").replace("_", " ").toLowerCase())
			    .replace("%qty%", String.valueOf(oneMap.getValue().getCount()))
			    .replace("%money%", String.valueOf(oneMap.getValue().getMoney()))
			    .replace("%exp%", String.valueOf(oneMap.getValue().getExp()));
			msg = org.bukkit.ChatColor.translateAlternateColorCodes('&', msg);
			sender.sendMessage(msg);
			break;
		    }
		}
		if (count > max)
		    break;
	    }
	    if (count > max)
		break;
	}
	sender.sendMessage(Language.getMessage("command.log.output.bottomline"));

	return true;
    }

    @JobCommand
    public boolean glog(final CommandSender sender, String[] args) {
	if (args.length != 0) {
	    sendUsage(sender, "glog");
	    return true;
	}
	Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	    @Override
	    public void run() {
		Map<LogAmounts, Double> unsortMap = new HashMap<LogAmounts, Double>();

		int time = TimeManage.timeInInt();

		for (String OneP : Jobs.getJobsDAO().getLognameList(time, time)) {
		    JobsPlayer JPlayer = Jobs.getPlayerManager().getJobsPlayer(OneP);
		    if (JPlayer == null) {
			OfflinePlayer offp = OfflinePlayerList.getPlayer(OneP);
			if (offp != null)
			    JPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(OfflinePlayerList.getPlayer(OneP));
		    }
		    if (JPlayer == null)
			continue;
		    List<Log> logList = JPlayer.getLog();
		    if (logList.size() == 0)
			continue;

		    for (Log one : logList) {
			HashMap<String, LogAmounts> AmountList = one.getAmountList();
			for (Entry<String, LogAmounts> oneMap : AmountList.entrySet()) {
			    oneMap.getValue().setUsername(OneP);
			    oneMap.getValue().setAction(one.getActionType());
			    unsortMap.put(oneMap.getValue(), oneMap.getValue().getMoney());
			}
		    }
		}

		unsortMap = Sorting.sortDoubleDESCByLog(unsortMap);

		int count = 1;
		int max = 10;

		sender.sendMessage(Language.getMessage("command.glog.output.topline"));
		for (Entry<LogAmounts, Double> one : unsortMap.entrySet()) {
		    LogAmounts info = one.getKey();

		    String msg = Language.getMessage("command.glog.output.list")
			.replace("%username%", one.getKey().getUsername())
			.replace("%number%", String.valueOf(count))
			.replace("%action%", info.getAction())
			.replace("%item%", one.getKey().getItemName().replace(":0", "").replace("_", " ").toLowerCase())
			.replace("%qty%", String.valueOf(one.getKey().getCount()))
			.replace("%money%", String.valueOf(one.getKey().getMoney()))
			.replace("%exp%", String.valueOf(one.getKey().getExp()));
		    msg = org.bukkit.ChatColor.translateAlternateColorCodes('&', msg);
		    sender.sendMessage(msg);
		    count++;

		    if (count > max)
			break;
		}
		if (unsortMap.size() == 0) {
		    sender.sendMessage(Language.getMessage("command.glog.output.nodata"));
		}
		sender.sendMessage(Language.getMessage("command.glog.output.bottomline"));

		return;
	    }
	});
	return true;
    }

    /**
     * Displays info about a job
     * @param player - the player of the job
     * @param job - the job we are displaying info about
     * @param type - type of info
     * @return the message
     */
    private String jobInfoMessage(JobsPlayer player, Job job, String type) {
	if (job == null) {
	    // job doesn't exist
	    return ChatColor.RED + Language.getMessage("command.error.job");
	}

	if (type == null) {
	    type = "";
	} else {
	    type = type.toLowerCase();
	}

	StringBuilder message = new StringBuilder();

	int showAllTypes = 1;
	for (ActionType actionType : ActionType.values()) {
	    if (type.startsWith(actionType.getName().toLowerCase())) {
		showAllTypes = 0;
		break;
	    }
	}

	if (job.getExpBoost() != 1.0)
	    message.append(ChatColor.GOLD + Language.getMessage("command.expboost.output.infostats").replace("%boost%", String.valueOf(job.getExpBoost())) + "\n");

	if (job.getMoneyBoost() != 1.0)
	    message.append(ChatColor.GOLD + Language.getMessage("command.moneyboost.output.infostats").replace("%boost%", String.valueOf(job.getMoneyBoost())) + "\n");

	if (ConfigManager.getJobsConfiguration().useDynamicPayment)
	    if (job.getBonus() < 0)
		message.append(ChatColor.GOLD + Language.getMessage("command.info.help.penalty").replace("[penalty]", String.valueOf((int) (job.getBonus() * 100) / 100.0
		    * -1)) + "\n");
	    else
		message.append(ChatColor.GOLD + Language.getMessage("command.info.help.bonus").replace("[bonus]", String.valueOf((int) (job.getBonus() * 100) / 100.0))
		    + "\n");

	for (ActionType actionType : ActionType.values()) {
	    if (showAllTypes == 1 || type.startsWith(actionType.getName().toLowerCase())) {
		List<JobInfo> info = job.getJobInfo(actionType);
		if (info != null && !info.isEmpty()) {
		    message.append(jobInfoMessage(player, job, actionType));
		} else if (showAllTypes == 0) {
		    String myMessage = Language.getMessage("command.info.output." + actionType.getName().toLowerCase() + ".none");
		    myMessage = myMessage.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		    message.append(myMessage);
		}
	    }
	}
	return message.toString();
    }

    /**
     * Displays info about a particular action
     * @param player - the player of the job
     * @param prog - the job we are displaying info about
     * @param type - the type of action
     * @return the message
     */
    public static String jobInfoMessage(JobsPlayer player, Job job, ActionType type) {

	// money exp boost
	Player dude = Bukkit.getServer().getPlayer(player.getPlayerUUID());
	Double MoneyBoost = Jobs.getPlayerManager().GetMoneyBoost(dude, job);
	Double ExpBoost = Jobs.getPlayerManager().GetExpBoost(dude, job);

	// Global boost
	Double JobGlobalBoost = job.getExpBoost();

	Double DynamicBonus = job.getBonus();

	StringBuilder message = new StringBuilder();

	message.append(Language.getMessage("command.info.output." + type.getName().toLowerCase() + ".info"));
	message.append(":\n");

	int level = 1;

	JobProgression prog = player.getJobProgression(job);
	if (prog != null)
	    level = prog.getLevel();
	int numjobs = player.getJobProgression().size();
	List<JobInfo> jobInfo = job.getJobInfo(type);
	for (JobInfo info : jobInfo) {
	    String materialName = info.getName().toLowerCase().replace('_', ' ');
	    materialName = Character.toUpperCase(materialName.charAt(0)) + materialName.substring(1);

	    materialName = TranslateName.Translate(materialName, info);

	    materialName = org.bukkit.ChatColor.translateAlternateColorCodes('&', materialName);

	    double income = info.getIncome(level, numjobs);

	    if (ConfigManager.getJobsConfiguration().useDynamicPayment) {
		double moneyBonus = (income * (DynamicBonus / 100));
		income += moneyBonus;
	    }

	    income = income + ((income * MoneyBoost) - income) + ((income * JobGlobalBoost) - income);

	    ChatColor incomeColor = income >= 0 ? ChatColor.GREEN : ChatColor.DARK_RED;

	    double xp = info.getExperience(level, numjobs);

	    if (ConfigManager.getJobsConfiguration().useDynamicPayment) {
		double expBonus = (xp * (DynamicBonus / 100));
		xp += expBonus;
	    }

	    xp = xp + ((xp * ExpBoost) - xp) + ((xp * JobGlobalBoost) - xp);
	    ChatColor xpColor = xp >= 0 ? ChatColor.YELLOW : ChatColor.GRAY;
	    String xpString = String.format("%.2f xp", xp);

	    message.append("  ");

	    message.append(Language.getMessage("command.info.help.material").replace("%material%", materialName));
	    message.append(" -> ");

	    message.append(xpColor.toString());
	    message.append(xpString);
	    message.append(' ');

	    message.append(incomeColor.toString());
	    message.append(Jobs.getEconomy().format(income));

	    message.append('\n');
	}
	return message.toString();
    }

    /**
     * Displays job stats about a particular player's job
     * @param jobProg - the job progress of the players job
     * @return the message
     */
    private String jobStatsMessage(JobProgression jobProg) {
	String message = Language.getMessage("command.stats.output");
	message = message.replace("%joblevel%", Integer.valueOf(jobProg.getLevel()).toString());
	message = message.replace("%jobname%", jobProg.getJob().getChatColor() + jobProg.getJob().getName() + ChatColor.WHITE);
	message = message.replace("%jobxp%", Double.toString(Math.round((Double) jobProg.getExperience() * 100.0) / 100.0));
	message = message.replace("%jobmaxxp%", Integer.toString(jobProg.getMaxExperience()));
	return message;
    }

    /**
     * Displays job stats about a particular player's job from archive
     * @param jobInfo - jobinfo string line
     * @return the message
     */
    private String jobStatsMessage(String jobInfo) {
	String[] splited = jobInfo.split(":");
	if (Jobs.getJob(splited[0]) == null)
	    return "";
	String message = Language.getMessage("command.archive.output");
	message = message.replace("%joblevel%", Integer.valueOf(splited[1]).toString());
	message = message.replace("%getbackjoblevel%", Integer.valueOf(splited[2]).toString());
	message = message.replace("%jobname%", Jobs.getJob(splited[0]).getChatColor() + splited[0] + ChatColor.WHITE);
	message = message.replace("%jobxp%", Double.toString(Math.round((Double) Double.valueOf(splited[3]) * 100.0) / 100.0));
	return message;
    }

    /**
     * Check Job joining permission
     */
    public static boolean hasJobPermission(Player sender, Job job) {
	return hasJobPermission((CommandSender) sender, job);
    }

    private static boolean hasJobPermission(CommandSender sender, Job job) {
	if (!sender.hasPermission("jobs.use")) {
	    return false;
	} else {
	    return sender.hasPermission("jobs.join." + job.getName().toLowerCase());
	}
    }
}
