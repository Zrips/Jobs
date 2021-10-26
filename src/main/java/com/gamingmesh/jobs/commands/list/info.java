package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.ActionType;
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
	    plugin.getGUIManager().openJobsBrowseGUI(pSender, job, true);
	    return true;
	}

	int page = 1;
	String type = null;

	for (int i = 1; i < args.length; i++) {
	    String one = args[i];
	    if (type == null) {
		ActionType t = ActionType.getByName(one);
		if (t != null) {
		    type = t.getName();
		    continue;
		}
	    }
	    try {
		page = Integer.parseInt(args[i]);
	    } catch (NumberFormatException e) {
	    }
	}

	Jobs.getCommandManager().jobInfoMessage(pSender, jPlayer, job, type, page);
	return true;
    }

}
