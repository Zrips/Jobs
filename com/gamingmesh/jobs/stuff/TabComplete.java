package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.i18n.Language;

public class TabComplete implements TabCompleter {

	private static final List<String> BaseCommands = new ArrayList<>(Arrays.asList("Convert", "Limit", "Join", "Transfer", "Info", "Top", "Gtop", "Reload", "Removexp", "Leaveall", "Promote", "Browse", "Playerinfo", "Fireall", "Demote", "Grantxp", "Employ", "Fire", "Give", "Leave", "Stats", "Toggle", "Boost", "Archive"));

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completionList = new ArrayList<>();

		if (args.length == 1) {
			String PartOfCommand = args[0];
			List<String> temp = new ArrayList<String>();

			for (String BCmd : BaseCommands) {
				if (sender.hasPermission("jobs.command." + BCmd.toLowerCase())) {
					temp.add(BCmd);
				}
			}
			StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
		}
		if (args.length > 1)
			for (int i = 1; i <= args.length; i++)
				if (args.length == i + 1) {

					String PartOfCommand = args[i];
					String CommandArgs = Language.getDefaultMessage("command." + args[0].toLowerCase() + ".help.args");
					List<String> ArgsList = new ArrayList<String>();

					if (!CommandArgs.contains(" ")) {
						ArgsList.add(CommandArgs);
					} else {
						ArgsList.addAll(Arrays.asList(CommandArgs.split(" ")));
					}

					if (ArgsList.size() < i)
						continue;

					String arg = ArgsList.get(i - 1);
					List<String> temp = new ArrayList<String>();
					if (arg.equalsIgnoreCase("[jobname]") || arg.equalsIgnoreCase("[oldjob]") || arg.equalsIgnoreCase("[newjob]")) {
						List<Job> Jobsai = Jobs.getJobs();
						for (Job one : Jobsai) {
							temp.add(one.getName());
						}
					}

					if (arg.equalsIgnoreCase("[playername]")) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							temp.add(player.getName());
						}
					}

					if (arg.equalsIgnoreCase("[action]")) {
						for (ActionType action : ActionType.values()) {
							temp.add(action.getName());
						}
					}

					if (arg.equalsIgnoreCase("[itemname]")) {
						Job Jobsai = Jobs.getJob(args[i - 1]);
						if (Jobsai != null)
							for (JobItems item : Jobsai.getItems()) {
								temp.add(item.getNode());
							}
					}
					StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
				}
		Collections.sort(completionList);
		return completionList;
	}
}
