package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class stats implements Cmd {

    @JobCommand(400)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!sender.hasPermission("jobs.command.admin.stats")) {
		sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.permission"));
		return true;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	    if (jPlayer == null)
		jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(args[0]);
	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    else
		Jobs.getCommandManager().sendUsage(sender, "stats");
	    return true;
	}

	if (jPlayer.getJobProgression().size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.stats.error.nojob"));
	    return true;
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.toplineseparator", "%playername%", jPlayer.getUserName()));
	for (JobProgression jobProg : jPlayer.getJobProgression()) {
	    String[] msg = Jobs.getCommandManager().jobStatsMessage(jobProg).split("\n");
	    sender.sendMessage(msg);
	}
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	return true;
    }
}
