package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class points implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (args.length != 0 && args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "points");
	    return true;
	}

	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!Jobs.hasPermission(sender, "jobs.command.admin.points", true)) {
		return true;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    if (args.length >= 1)
		CMIMessages.sendMessage(sender, LC.info_NoInformation);
	    else
		Jobs.getCommandManager().sendUsage(sender, "points");
	    return true;
	}

	PlayerPoints pointInfo = jPlayer.getPointsData();

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName()));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.points.currentpoints", "%currentpoints%", (int) (pointInfo.getCurrentPoints() * 100) / 100D));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.points.totalpoints", "%totalpoints%", (int) (pointInfo.getTotalPoints() * 100) / 100D));
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}
