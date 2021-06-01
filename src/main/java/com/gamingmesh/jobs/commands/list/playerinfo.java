package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class playerinfo implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "playerinfo");
	    Jobs.getCommandManager().sendValidActions(sender);
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	int page = 1;
	String type = "";
	if (args.length >= 3) {
	    try {
		page = Integer.parseInt(args[2]);
	    } catch (NumberFormatException e) {
		type = args[2];
	    }
	}

	Jobs.getCommandManager().jobInfoMessage(sender, jPlayer, job, type, page);
	return true;
    }
}
