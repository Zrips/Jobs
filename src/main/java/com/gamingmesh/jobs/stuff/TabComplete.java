package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItemBonus;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
	List<String> completionList = new ArrayList<>();

	if (args.length == 1) {
	    String PartOfCommand = args[0];
	    List<String> temp = new ArrayList<String>();

	    for (Entry<String, Integer> BCmd : Jobs.getCommandManager().GetCommands(sender).entrySet()) {
		temp.add(BCmd.getKey());
	    }
	    StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
	}
	if (args.length > 1)
	    for (int i = 1; i <= args.length; i++)
		if (args.length == i + 1) {

		    String PartOfCommand = args[i];

		    if (!Jobs.getGCManager().getCommandArgs().containsKey(args[0].toLowerCase()))
			break;

		    List<String> ArgsList = Jobs.getGCManager().getCommandArgs().get(args[0].toLowerCase());

		    if (ArgsList.size() < i)
			continue;

		    String arg = ArgsList.get(i - 1);
		    List<String> temp = new ArrayList<String>();

		    if (arg.contains("%%"))
			for (String one : arg.split("%%")) {
			    temp.add(one);
			}

		    switch (arg) {
		    case "[jobname]":
			List<Job> Jobsai = Jobs.getJobs();
			for (Job one : Jobsai) {
			    temp.add(one.getName());
			}
			break;
		    case "[playername]":
			for (Player player : Bukkit.getOnlinePlayers()) {
			    temp.add(player.getName());
			}
			break;
		    case "[action]":
			for (ActionType action : ActionType.values()) {
			    temp.add(action.getName());
			}
			break;
		    case "[jobitemname]":
			Job oneJob = Jobs.getJob(args[i - 1]);
			if (oneJob != null)
			    for (Entry<String, JobItems> item : oneJob.getItemBonus().entrySet()) {
				temp.add(item.getValue().getNode());
			    }
			break;
		    case "[oldjob]":
			JobsPlayer onePlayerJob = Jobs.getPlayerManager().getJobsPlayer(args[i - 1]);
			if (onePlayerJob != null)
			    for (JobProgression oneOldJob : onePlayerJob.getJobProgression()) {
				temp.add(oneOldJob.getJob().getName());
			    }
			break;
		    case "[oldplayerjob]":
			if (sender instanceof Player) {
			    onePlayerJob = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
			    if (onePlayerJob != null)
				for (JobProgression oneOldJob : onePlayerJob.getJobProgression()) {
				    temp.add(oneOldJob.getJob().getName());
				}
			}
			break;
		    }

		    StringUtil.copyPartialMatches(PartOfCommand, temp, completionList);
		}

	if (completionList.isEmpty())
	    Jobs.getCommandManager().sendUsage(sender, args[0].toLowerCase());
	Collections.sort(completionList);
	return completionList;
    }
}
