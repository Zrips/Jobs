package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class info implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length < 1) {
	    Jobs.getCommandManager().sendUsage(sender, "info");
	    Jobs.getCommandManager().sendValidActions(sender);
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
	if (jPlayer == null) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", pSender.getName()));
	    return true;
	}

	Job job = Jobs.getJob(args[0]);
	if (job == null) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (Jobs.getGCManager().hideJobsInfoWithoutPermission && !Jobs.getCommandManager().hasJobPermission(pSender, job)) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}

	if (Jobs.getGCManager().jobsInfoOpensBrowse) {
	    Jobs.getGUIManager().openJobsBrowseGUI(pSender, job, true);
	    return true;
	}

	String type = "";
	if (args.length >= 2) {
	    try {
		Integer.parseInt(args[1]);
	    } catch (NumberFormatException e) {
		type = args[1];
	    }
	}
	int page = 1;
	try {
	    page = Integer.parseInt(args[args.length - 1]);
	} catch (NumberFormatException e) {
	}

	Jobs.getCommandManager().jobInfoMessage(pSender, jPlayer, job, type, page);
	return true;
    }

}
