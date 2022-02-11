package com.gamingmesh.jobs.commands.list;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class resetquesttotal implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 0 && args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "resetquesttotal");
	    return true;
	}

	if (args[0].equalsIgnoreCase("all")) {
	    for (Entry<UUID, JobsPlayer> pl : Jobs.getPlayerManager().getPlayersCache().entrySet()) {
		pl.getValue().setDoneQuests(0);
	    }
	    Jobs.getJobsDAO().resetDoneQuests();
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.resetquesttotal.output.reseted", "%playername%", Jobs.getPlayerManager().getPlayersCache().size()));
	    return true;
	}

	JobsPlayer jPlayer = null;
	Job job = null;

	for (String one : args) {
	    if (job == null) {
		job = Jobs.getJob(one);
		if (job != null)
		    continue;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(one);
	}

	if (jPlayer == null && sender instanceof Player)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args.length > 0 ? args[0] : ""));
	    return true;
	}

	jPlayer.setDoneQuests(0);
	jPlayer.setSaved(false);
	jPlayer.save();
	sender.sendMessage(Jobs.getLanguage().getMessage("command.resetquesttotal.output.reseted", "%playername%", jPlayer.getName()));
	return true;
    }
}
