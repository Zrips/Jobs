package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.PageInfo;

public class gtop implements Cmd {

    @Override
    @JobCommand(600)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 1 && args.length != 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.help.info", "%amount%", Jobs.getGCManager().JobsTopAmount));
	    return true;
	}

	if (!(sender instanceof Player))
	    return false;

	Player player = (Player) sender;
	if (args.length == 1) {
	    if (args[0].equalsIgnoreCase("clear")) {
		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		plugin.getCMIScoreboardManager().removeScoreBoard(player);
		return true;
	    }
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

	PageInfo pi = new PageInfo(Jobs.getGCManager().JobsTopAmount, Jobs.getPlayerManager().getPlayersCache().size(), page);

	List<TopList> FullList = Jobs.getJobsDAO().getGlobalTopList(pi.getStart());
	if (FullList.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.error.nojob"));
	    return true;
	}

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.topline", "%amount%", Jobs.getGCManager().JobsTopAmount));
	    int i = pi.getStart();
	    for (TopList One : FullList) {
		i++;
		String PlayerName = One.getPlayerName() != null ? One.getPlayerName() : "Unknown";
		sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.list", 
		    "%number%", i, 
		    "%playername%", PlayerName,
		    "%level%", One.getLevel(),
		    "%exp%", One.getExp()));
	    }

	    Jobs.getInstance().ShowPagination(sender, pi, "jobs gtop");
	} else {

	    List<String> ls = new ArrayList<>();

	    int i = pi.getStart();
	    for (TopList one : FullList) {
		i++;
		String playername = one.getPlayerName() != null ? one.getPlayerName() : "Unknown";
		ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
		    "%number%", i,
		    "%playername%", playername,
		    "%level%", one.getLevel()));
	    }

	    plugin.getCMIScoreboardManager().setScoreBoard(player, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls);
	    plugin.getCMIScoreboardManager().addNew(player);

	    Jobs.getInstance().ShowPagination(sender, pi, "jobs gtop");
	}
	return true;
    }
}
