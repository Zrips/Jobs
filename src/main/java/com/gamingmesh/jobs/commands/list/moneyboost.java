package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;

public class moneyboost implements Cmd {

	@Override
	@JobCommand(2300)
	public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
		if (args.length > 3 || args.length <= 1) {
			Jobs.getCommandManager().sendUsage(sender, "moneyboost");
			return true;
		}

		String time = "";
		double rate = 1.0;
		if (args[0].equalsIgnoreCase("all")) {
			if (args.length > 2) {
				time = args[1].toLowerCase();

				try {
					rate = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					Jobs.getCommandManager().sendUsage(sender, "moneyboost");
					return true;
				}
			} else {
				try {
					rate = Double.parseDouble(args[1]);
				} catch (NumberFormatException e) {
					Jobs.getCommandManager().sendUsage(sender, "moneyboost");
					return true;
				}
			}

			if (args.length > 2) {
				int sec = 0;
				int min = 0;
				int hour = 0;

				if (!time.isEmpty()) {
					if (time.contains("s")) {
						sec = Integer.parseInt(time.split("s")[0]);
					}

					if (time.contains("m")) {
						min = Integer.parseInt(time.split("m")[0]);
					}

					if (time.contains("h")) {
						hour = Integer.parseInt(time.split("h")[0]);
					}
				}

				boolean succeed = false;
				for (Job one : Jobs.getJobs()) {
					if (time.isEmpty()) {
						one.addBoost(CurrencyType.MONEY, rate);
						succeed = true;
					} else if (sec != 0 || min != 0 || hour != 0) {
						one.addBoost(CurrencyType.MONEY, rate, hour, min, sec);
						succeed = true;
					}
				}

				if (succeed) {
					sender.sendMessage(
							Jobs.getLanguage().getMessage("command.moneyboost.output.boostalladded", "%boost%", rate));
					return true;
				}
			} else {
				for (Job job : Jobs.getJobs()) {
					job.addBoost(CurrencyType.MONEY, rate);
				}

				sender.sendMessage(
						Jobs.getLanguage().getMessage("command.moneyboost.output.boostalladded", "%boost%", rate));
				return true;
			}
		}

		if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("all")) {
			for (Job one : Jobs.getJobs()) {
				one.addBoost(CurrencyType.MONEY, 1.0);
			}

			sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.allreset"));
			return true;
		}

		if (args[0].equalsIgnoreCase("reset") && args.length > 1) {
			Job job = Jobs.getJob(args[1]);
			if (job == null) {
				sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
				return true;
			}

			boolean found = false;
			for (Job one : Jobs.getJobs()) {
				if (one.getName().equalsIgnoreCase(args[1])) {
					one.addBoost(CurrencyType.MONEY, 1.0);
					found = true;
					break;
				}
			}

			if (found) {
				sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.jobsboostreset",
						"%jobname%", job.getName()));
				return true;
			}
		}

		Job job = Jobs.getJob(args[0]);
		if (job == null) {
			sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
			return true;
		}

		if (args.length > 2) {
			time = args[1].toLowerCase();

			try {
				rate = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				Jobs.getCommandManager().sendUsage(sender, "moneyboost");
				return true;
			}
		} else {
			try {
				rate = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				Jobs.getCommandManager().sendUsage(sender, "moneyboost");
				return true;
			}
		}

		if (!time.isEmpty()) {
			int sec = 0;
			int min = 0;
			int hour = 0;

			if (time.contains("s")) {
				sec = Integer.parseInt(time.split("s")[0]);
			}

			if (time.contains("m")) {
				min = Integer.parseInt(time.split("m")[0]);
			}

			if (time.contains("h")) {
				hour = Integer.parseInt(time.split("h")[0]);
			}

			if (sec != 0 || min != 0 || hour != 0) {
				sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.boostadded", "%boost%",
						rate, "%jobname%", job.getName()));
				job.addBoost(CurrencyType.MONEY, rate, hour, min, sec);
				return true;
			}
		}

		job.addBoost(CurrencyType.MONEY, rate);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.moneyboost.output.boostadded", "%boost%", rate,
				"%jobname%", job.getName()));
		return true;
	}
}
