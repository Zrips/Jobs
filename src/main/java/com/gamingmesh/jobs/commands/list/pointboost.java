package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;

public class pointboost implements Cmd {

	@Override
	@JobCommand(2303)
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

			for (Job job : Jobs.getJobs()) {
				job.addBoost(CurrencyType.POINTS, rate, parseTime(args));
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

		job.addBoost(CurrencyType.POINTS, rate, parseTime(args));
		sender.sendMessage(Jobs.getLanguage().getMessage("command.pointboost.output.boostadded", "%boost%", rate,
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

		if (time.contains("s") || time.contains("second")) {
			arr[0] = Integer.parseInt(time.split("s|second")[0]);
		}

		if (time.contains("m") || time.contains("minute")) {
			arr[1] = Integer.parseInt(time.split("m|minute")[0]);
		}

		if (time.contains("h") || time.contains("hour")) {
			arr[2] = Integer.parseInt(time.split("h|hour")[0]);
		}

		if (arr[0] == 0 && arr[1] == 0 && arr[2] == 0) {
			return new int[3];
		}

		return arr;
	}
}
