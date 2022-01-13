package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.Colors.CMIChatColor;

public class demote implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length < 3) {
	    Jobs.getCommandManager().sendUsage(sender, "demote");
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

	try {
	    // check if player already has the job
	    if (jPlayer.isInJob(job)) {
		int levelsLost = 0;
		try {
		    levelsLost = Integer.parseInt(args[2]);
		} catch (NumberFormatException ex) {
		    return true;
		}

		Jobs.getPlayerManager().demoteJob(jPlayer, job, levelsLost);

		Player player = jPlayer.getPlayer();
		if (player != null) {
		    String message = Jobs.getLanguage().getMessage("command.demote.output.target",
			"%jobname%", job.getDisplayName() + CMIChatColor.WHITE,
			"%levelslost%", levelsLost);
		    player.sendMessage(message);
		}

		sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	    }
	} catch (Throwable e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
