package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Signs.SignTopType;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;

public class signupdate implements Cmd {

    @Override
    @JobCommand(2700)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!Jobs.getGCManager().SignsEnabled)
	    return true;

	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "signupdate");
	    return true;
	}

	Job oldjob = Jobs.getJob(args[0]);

	SignTopType type = SignTopType.getType(args[0]);

	if (type == SignTopType.toplist && oldjob == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	Jobs.getSignUtil().SignUpdate(oldjob, type);

	return true;
    }
}
