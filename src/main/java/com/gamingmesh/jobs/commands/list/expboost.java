package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;

import net.Zrips.CMILib.Time.timeModifier;

public class expboost implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length > 3 || args.length <= 1) {
	    Jobs.getCommandManager().sendUsage(sender, "expboost");
	    return true;
	}

	Double rate = null;
	Long timeDuration = null;
	String jobName = null;
	boolean reset = false;

	for (String one : args) {
	    if (one.equalsIgnoreCase("reset")) {
		reset = true;
		continue;
	    }

	    if (jobName == null) {
		jobName = one;
		continue;
	    }

	    if (rate == null) {
		try {
		    rate = Double.parseDouble(one);
		    continue;
		} catch (NumberFormatException e) {
		}
	    }

	    try {
		Long t = timeModifier.getTimeRangeFromString(one);
		if (t != null)
		    timeDuration = t;
		continue;
	    } catch (Exception e) {
	    }
	} 

	if (!reset && rate == null || jobName == null) {
	    Jobs.getCommandManager().sendUsage(sender, "expboost");
	    return false;
	}
	
	if (rate == null)
	    rate = 1D;

	if (timeDuration == null)
	    timeDuration = 0L;

	if (!reset && jobName.equalsIgnoreCase("all")) {
	    for (Job job : Jobs.getJobs()) {
		job.addBoost(CurrencyType.EXP, rate, timeDuration);
	    }
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.expboost.output.boostalladded", "%boost%", rate));
	    return true;
	}

	if (reset) {
	    if (jobName.equalsIgnoreCase("all")) {
		for (Job one : Jobs.getJobs()) {
		    one.addBoost(CurrencyType.EXP, 1.0);
		}

		sender.sendMessage(Jobs.getLanguage().getMessage("command.expboost.output.allreset"));
	    } else if (args.length > 1) {
		Job job = Jobs.getJob(jobName);
		if (job == null) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
		    return true;
		}

		job.addBoost(CurrencyType.EXP, 1.0);

		sender.sendMessage(Jobs.getLanguage().getMessage("command.expboost.output.jobsboostreset", "%jobname%", job.getName()));
	    }

	    return true;
	}

	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	
	job.addBoost(CurrencyType.EXP, rate, timeDuration);
	sender.sendMessage(Jobs.getLanguage().getMessage("command.expboost.output.boostadded", "%boost%", rate,
	    "%jobname%", job.getName()));
	return true;
    }
}
