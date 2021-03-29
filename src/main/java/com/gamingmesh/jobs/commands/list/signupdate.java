package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Signs.SignTopType;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;

public class signupdate implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!Jobs.getGCManager().SignsEnabled)
	    return true;

	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "signupdate");
	    return true;
	}

	if (args[0].equalsIgnoreCase("all")) {
	    Jobs.getJobs().forEach(Jobs.getSignUtil()::signUpdate);
	    return true;
	}

	Job oldjob = Jobs.getJob(args[0]);
	if (oldjob == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (args.length == 2) {
	    SignTopType type = SignTopType.getType(args[1]);
	    if (type != null) {
		Jobs.getSignUtil().signUpdate(oldjob, type);
	    }

	    return true;
	}

	Jobs.getSignUtil().signUpdate(oldjob);
	return true;
    }
}
