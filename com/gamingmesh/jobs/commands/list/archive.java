package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class archive implements Cmd {

    @Override
    @JobCommand(1400)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!sender.hasPermission("jobs.command.admin.archive")) {
		sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.permission"));
		return true;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	List<String> AllJobs = Jobs.getJobsDAO().getJobsFromArchive(jPlayer);

	if (AllJobs.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.archive.error.nojob"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", jPlayer.getUserName()));
	for (String jobInfo : AllJobs) {
	    sender.sendMessage(Jobs.getCommandManager().jobStatsMessage(jobInfo));
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}
