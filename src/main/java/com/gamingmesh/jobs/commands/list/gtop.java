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
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.RawMessage;

public class gtop implements Cmd {

    @Override
    @JobCommand(600)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (args.length != 1 && args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "gtop");
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;
	Player player = (Player) sender;

	if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    return true;
	}

	int page = 1;
	if (args.length == 1)
	    try {
		page = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	if (page < 1)
	    page = 1;

	int st = (page * 15) - 15;
	
	List<TopList> FullList = Jobs.getJobsDAO().getGlobalTopList(st);
	if (FullList.size() <= 0) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("command.gtop.error.nojob"));
	    return true;
	}

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.topline"));
	    int i = st;
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";
		sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.list", "%number%", i, "%playername%", PlayerName, "%level%", One.getLevel(),
		    "%exp%", One.getExp()));
	    }
	} else {

	    List<String> ls = new ArrayList<String>();

	    int i = st;
	    for (TopList one : FullList) {
		i++;
		String playername = one.getPlayerName() != null ? one.getPlayerName() : "Unknown";
		ls.add(Jobs.getLanguage().getMessage("scoreboard.line", "%number%", i, "%playername%", playername, "%level%", one.getLevel()));
	    }

	    plugin.getCMIScoreboardManager().setScoreBoard(player, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls);
	    plugin.getCMIScoreboardManager().addNew(player);

	    int prev = page < 2 ? 1 : page - 1;
	    int next = page + 1;

	    RawMessage rm = new RawMessage();
	    rm.add(Jobs.getLanguage().getMessage("command.gtop.output.prev"),
		Jobs.getLanguage().getMessage("command.gtop.output.show", "[from]", prev * 15 - 15, "[until]", (prev * 15)), "jobs gtop " + prev);
	    rm.add(Jobs.getLanguage().getMessage("command.gtop.output.next"),
		Jobs.getLanguage().getMessage("command.gtop.output.show", "[from]", (next * 15), "[until]", (next * 15 + 15)), "jobs gtop " + next);
	    rm.show(player);
	}
	return true;
    }
}
