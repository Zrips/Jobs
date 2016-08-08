package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class info implements Cmd {

    @Override
    @JobCommand(300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length < 1) {
	    Jobs.getCommandManager().sendUsage(sender, "info");
	    Jobs.getCommandManager().sendValidActions(sender);
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (Jobs.getGCManager().hideJobsInfoWithoutPermission)
	    if (!Jobs.getCommandManager().hasJobPermission(pSender, job)) {
		sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.permission"));
		return true;
	    }

	String type = "";
	if (args.length >= 2) {
	    try {
		Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
		type = args[1];
	    }
	}
	int page = 1;
	try {
	    page = Integer.parseInt(args[args.length - 1]);
	} catch (NumberFormatException e) {
	}

	Jobs.getCommandManager().jobInfoMessage(sender, jPlayer, job, type, page);

	return true;
    }

}
