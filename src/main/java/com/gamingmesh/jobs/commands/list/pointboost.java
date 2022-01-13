package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.stuff.Util;

public class pointboost implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length > 3 || args.length <= 1) {
	    Jobs.getCommandManager().sendUsage(sender, "pointboost");
	    return true;
	}

	double rate = 1.0;
	if (args[0].equalsIgnoreCase("all")) {
	    try {
		rate = Double.parseDouble(args[args.length > 2 ? 2 : 1]);
	    } catch (NumberFormatException e) {
		Jobs.getCommandManager().sendUsage(sender, "pointboost");
		return true;
	    }

	    int[] times = Util.parseTime(args);

	    for (Job job : Jobs.getJobs()) {
		job.addBoost(CurrencyType.POINTS, rate, times);
	    }

	    sender.sendMessage(
		Jobs.getLanguage().getMessage("command.pointboost.output.boostalladded", "%boost%", rate));
	    return true;
	}

	if (args[0].equalsIgnoreCase("reset")) {
	    if (args[1].equalsIgnoreCase("all")) {
		for (Job one : Jobs.getJobs()) {
		    one.addBoost(CurrencyType.POINTS, 1.0);
		}

		sender.sendMessage(Jobs.getLanguage().getMessage("command.pointboost.output.allreset"));
	    } else if (args.length > 1) {
		Job job = Jobs.getJob(args[1]);
		if (job == null) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
		    return true;
		}

		job.addBoost(CurrencyType.POINTS, 1.0);

		sender.sendMessage(Jobs.getLanguage().getMessage("command.pointboost.output.jobsboostreset",
		    "%jobname%", job.getName()));
	    }

	    return true;
	}

	Job job = Jobs.getJob(args[0]);
	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	try {
	    rate = Double.parseDouble(args[args.length > 2 ? 2 : 1]);
	} catch (NumberFormatException e) {
	    Jobs.getCommandManager().sendUsage(sender, "pointboost");
	    return true;
	}

	job.addBoost(CurrencyType.POINTS, rate, Util.parseTime(args));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.pointboost.output.boostadded", "%boost%", rate,
	    "%jobname%", job.getName()));
	return true;
    }
}
