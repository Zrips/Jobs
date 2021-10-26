package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;

public class moneyboost implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length > 3 || args.length <= 1) {
	    Jobs.getCommandManager().sendUsage(sender, "moneyboost");
	    return true;
	}

	double rate = 1.0;
	if (args[0].equalsIgnoreCase("all")) {
	    try {
		rate = Double.parseDouble(args[args.length > 2 ? 2 : 1]);
	    } catch (NumberFormatException e) {
		Jobs.getCommandManager().sendUsage(sender, "moneyboost");
		return true;
	    }

	    int[] times = parseTime(args);

	    for (Job job : Jobs.getJobs()) {
		job.addBoost(CurrencyType.MONEY, rate, times);
	    }

	    sender.sendMessage(
		Jobs.getLanguage().getMessage("command.moneyboost.output.boostalladded", "%boost%", rate));
	    return true;
	}

	if (args[0].equalsIgnoreCase("reset")) {
	    if (args[1].equalsIgnoreCase("all")) {
		for (Job one : Jobs.getJobs()) {
		    one.addBoost(CurrencyType.MONEY, 1.0);
		}

		sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.allreset"));
	    } else if (args.length > 1) {
		Job job = Jobs.getJob(args[1]);
		if (job == null) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
		    return true;
		}

		job.addBoost(CurrencyType.MONEY, 1.0);

		sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.jobsboostreset",
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
	    Jobs.getCommandManager().sendUsage(sender, "moneyboost");
	    return true;
	}

	job.addBoost(CurrencyType.MONEY, rate, parseTime(args));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.boostadded", "%boost%", rate,
	    "%jobname%", job.getName()));
	return true;
    }

    private int[] parseTime(String[] args) {
	int[] arr = new int[3];

	if (args.length < 2) {
	    return arr;
	}

	String time = args[1].toLowerCase();
	if (time.isEmpty()) {
	    return arr;
	}

	String[] split = time.split("h|hour", 2);

	if (split.length > 0) {
	    try {
		arr[2] = Integer.parseInt(split[0]);
	    } catch (NumberFormatException e) {
		arr[2] = 0;
	    }

	    time = time.replaceAll(arr[2] + "+[h|hour]+", "");
	}

	if ((split = time.split("m|minute", 2)).length > 0) {
	    try {
		arr[1] = Integer.parseInt(split[0]);
	    } catch (NumberFormatException e) {
		arr[1] = 0;
	    }

	    time = time.replaceAll(arr[1] + "+[m|minute]+", "");
	}

	if ((split = time.split("s|second", 2)).length > 0) {
	    try {
		arr[0] = Integer.parseInt(split[0]);
	    } catch (NumberFormatException e) {
		arr[0] = 0;
	    }

	    time = time.replaceAll(arr[0] + "+[s|second]+", "");
	}

	if (arr[0] == 0 && arr[1] == 0 && arr[2] == 0) {
	    return new int[3];
	}

	return arr;
    }
}
