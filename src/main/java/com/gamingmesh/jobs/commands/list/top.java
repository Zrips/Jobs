package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.PageInfo;

public class top implements Cmd {

    @Override
    @JobCommand(500)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player player = (Player) sender;

	if (args.length != 1 && args.length != 2) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.top.error.nojob"));
	    return false;
	}

	if (args[0].equalsIgnoreCase("clear")) {
	    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	    plugin.getCMIScoreboardManager().removeScoreBoard(player);
	    return true;
	}

	int page = 1;
	if (args.length == 2) {
	    try {
		page = Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	}
	if (page < 1)
	    page = 1;

	Job job = Jobs.getJob(args[0]);
	if (job == null) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.top.error.nojob"));
	    return false;
	}

	int workingIn = Jobs.getUsedSlots(job);
	PageInfo pi = new PageInfo(Jobs.getGCManager().JobsTopAmount, workingIn, page);

	List<TopList> FullList = Jobs.getJobsDAO().toplist(job.getName(), pi.getStart());
	if (FullList.size() <= 0) {
	    player.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    return true;
	}

	player.sendMessage(Jobs.getLanguage().getMessage("command.top.help.info", "%amount%", Jobs.getGCManager().JobsTopAmount));

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    player.sendMessage(Jobs.getLanguage().getMessage("command.top.output.topline", "%jobname%", job.getName(), "%amount%", pi.getPerPageCount()));
	    int i = pi.getStart();
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";

		player.sendMessage(Jobs.getLanguage().getMessage("command.top.output.list",
		    "%number%", i,
		    "%playername%", PlayerName,
		    "%level%", One.getLevel(),
		    "%exp%", One.getExp()));
	    }
	    Jobs.getInstance().ShowPagination(sender, pi, "jobs top " + job.getName());
	} else {

	    List<String> ls = new ArrayList<>();

	    int i = pi.getStart();
	    for (TopList one : FullList) {
		i++;
		String playername = one.getPlayerName() != null ? one.getPlayerName() : "Unknown";
		ls.add(Jobs.getLanguage().getMessage("scoreboard.line", "%number%", i, "%playername%", playername, "%level%", one.getLevel()));
	    }

	    plugin.getCMIScoreboardManager().setScoreBoard(player, Jobs.getLanguage().getMessage("scoreboard.topline", "%jobname%", job.getName()), ls);
	    plugin.getCMIScoreboardManager().addNew(player);

	    Jobs.getInstance().ShowPagination(sender, pi, "jobs top " + job.getName());
	}
	return true;
    }
}
