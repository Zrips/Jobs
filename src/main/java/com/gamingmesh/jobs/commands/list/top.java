package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.RawMessage;

public class top implements Cmd {

    @Override
    @JobCommand(500)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (args.length != 1 && args.length != 2) {
	    Jobs.getCommandManager().sendUsage(sender, "top");
	    return true;
	}

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player player = (Player) sender;

	if (args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    return true;
	}

	int page = 1;
	if (args.length == 2)
	    try {
		page = Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	if (page < 1)
	    page = 1;

	if (Jobs.getJob(args[0]) == null) {
	    player.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.top.error.nojob"));
	    return false;
	}
	int st = (page * 15) - 15;

	List<TopList> FullList = Jobs.getJobsDAO().toplist(args[0], st);
	if (FullList.size() <= 0) {
	    player.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.noinfo"));
	    return true;
	}

	Job job = Jobs.getJob(args[0]);
	String jobName = args[0];
	if (job != null)
	    jobName = job.getName();

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.top.output.topline", "%jobname%", jobName));
	    int i = st;
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";

		player.sendMessage(Jobs.getLanguage().getMessage("command.top.output.list", "%number%", i, "%playername%", PlayerName, "%level%", One.getLevel(), "%exp%",
		    One.getExp()));
	    }
	} else {

	    List<String> ls = new ArrayList<String>();

	    int i = st;
	    for (TopList one : FullList) {
		i++;
		String playername = one.getPlayerName() != null ? one.getPlayerName() : "Unknown";
		ls.add(Jobs.getLanguage().getMessage("scoreboard.line", "%number%", i, "%playername%", playername, "%level%", one.getLevel()));
	    }

	    plugin.getCMIScoreboardManager().setScoreBoard(player, Jobs.getLanguage().getMessage("scoreboard.topline", "%jobname%", jobName), ls);

	    plugin.getCMIScoreboardManager().addNew(player);

	    int prev = page < 2 ? 1 : page - 1;
	    int next = page + 1;

	    RawMessage rm = new RawMessage();
	    rm.add(Jobs.getLanguage().getMessage("command.gtop.output.prev"),
		Jobs.getLanguage().getMessage("command.gtop.output.show", "[from]", prev * 15 - 15, "[until]", (prev * 15)), "jobs top " + jobName + " " + prev);
	    rm.add(Jobs.getLanguage().getMessage("command.gtop.output.next"),
		Jobs.getLanguage().getMessage("command.gtop.output.show", "[from]", (next * 15), "[until]", (next * 15 + 15)), "jobs top " + jobName + " " + next);
	    rm.show(player);
	}
	return true;
    }
}
