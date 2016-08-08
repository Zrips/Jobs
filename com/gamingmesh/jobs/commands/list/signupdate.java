package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.ChatColor;

public class signupdate implements Cmd {

    @Override
    @JobCommand(2700)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "signupdate");
	    return true;
	}

	Job oldjob = Jobs.getJob(args[0]);

	if (oldjob == null && !args[0].equalsIgnoreCase("gtoplist")) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	if (!args[0].equalsIgnoreCase("gtoplist") && oldjob != null)
	    Jobs.getSignUtil().SignUpdate(oldjob.getName());
	else
	    Jobs.getSignUtil().SignUpdate("gtoplist");

	return true;
    }
}
