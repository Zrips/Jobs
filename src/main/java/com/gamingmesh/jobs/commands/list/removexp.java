package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class removexp implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 3) {
	    Jobs.getCommandManager().sendUsage(sender, "removexp");
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	double xpLost = 0D;
	try {
	    xpLost = Double.parseDouble(args[2]);
	} catch (NumberFormatException e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}
	if (xpLost <= 0 || xpLost > Double.MAX_VALUE) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}
	// check if player already has the job
	if (jPlayer.isInJob(job)) {
	    Jobs.getPlayerManager().removeExperience(jPlayer, job, xpLost);

	    Player player = jPlayer.getPlayer();
	    if (player != null) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.removexp.output.target",
		    "%jobname%", job.getDisplayName(),
		    "%xplost%", xpLost));
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	}
	return true;
    }
}
