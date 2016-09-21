package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class playerinfo implements Cmd {

    @Override
    @JobCommand(1300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "playerinfo");
	    Jobs.getCommandManager().sendValidActions(sender);
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

	String jobName = args[1];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	String type = "";
	if (args.length >= 3) {
	    try {
		Integer.parseInt(args[2]);
	    } catch (NumberFormatException e) {
		type = args[2];
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
