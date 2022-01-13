
package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class grantxp implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 3) {
	    Jobs.getCommandManager().sendUsage(sender, "grantxp");
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
	double xpGained;
	try {
	    xpGained = Double.parseDouble(args[2]);
	} catch (NumberFormatException e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}
	if (xpGained <= 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}

	// check if player already has the job
	if (jPlayer.isInJob(job)) {
	    Jobs.getPlayerManager().addExperience(jPlayer, job, xpGained);

	    Player player = jPlayer.getPlayer();
	    if (player != null) {
		String message = Jobs.getLanguage().getMessage("command.grantxp.output.target",
		    "%jobname%", job.getDisplayName(),
		    "%xpgained%", xpGained);
		player.sendMessage(message);
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	}
	return true;
    }
}
