package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.TopList;

import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Scoreboards.CMIScoreboard;

public class gtop implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
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
		CMIScoreboard.removeScoreBoard(player);
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
		    "%playerdisplayname%", One.getPlayerInfo().getDisplayName(),
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
		    "%playerdisplayname%", one.getPlayerInfo().getDisplayName(),
		    "%level%", one.getLevel()));
		++i;
	    }

	    CMIScoreboard.show(player, Jobs.getLanguage().getMessage("scoreboard.gtopline"), ls, Jobs.getGCManager().ToplistInScoreboardInterval);
	}

	plugin.showPagination(sender, pi, "jobs gtop");
	return true;
    }
}
