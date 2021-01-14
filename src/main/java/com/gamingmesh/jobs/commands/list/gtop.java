package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.PageInfo;

public class gtop implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length > 1) {
	    Jobs.getCommandManager().sendUsage(sender, "gtop");
	    return true;
	}

	Player player = (Player) sender;
	int page = 1;
	if (args.length == 1) {
	    if (args[0].equalsIgnoreCase("clear")) {
		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		plugin.getCMIScoreboardManager().removeScoreBoard(player);
		return true;
	    }

	    try {
		page = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
		return true;
	    }
	}

	if (page < 1)
	    page = 1;

	int amount = Jobs.getGCManager().JobsTopAmount;
	PageInfo pi = new PageInfo(amount, Jobs.getPlayerManager().getPlayersCache().size(), page);

	List<TopList> FullList = Jobs.getJobsDAO().getGlobalTopList(pi.getStart());
	if (FullList.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.error.nojob"));
	    return true;
	}

	if (!Jobs.getGCManager().ShowToplistInScoreboard) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.topline", "%amount%", amount));

	    int i = 0;
	    for (TopList One : FullList) {
		if (i >= amount)
		    break;

		sender.sendMessage(Jobs.getLanguage().getMessage("command.gtop.output.list",
		    "%number%", pi.getPositionForOutput(i),
		    "%playername%", One.getPlayerInfo().getName(),
		    "%level%", One.getLevel(),
		    "%exp%", One.getExp()));
		++i;
	    }
	} else {
	    List<String> ls = new ArrayList<>();
	    int i = 0;
	    for (TopList one : FullList) {
		if (i >= amount)
		    break;

		ls.add(Jobs.getLanguage().getMessage("scoreboard.line",
		    "%number%", pi.getPositionForOutput(i),
		    "%playername%", one.getPlayerInfo().getName(),
		    "%level%", one.getLevel()));
		++i;
	    }

	    plugin.getCMIScoreboardManager().setScoreBoard(player, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls);
	    plugin.getCMIScoreboardManager().addNew(player);
	}

	Jobs.getInstance().showPagination(sender, pi, "jobs gtop");
	return true;
    }
}
