package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class employ implements Cmd {

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "employ");
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

	if (jPlayer.isInJob(job)) {
	    // already in job message
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.employ.error.alreadyin", "%jobname%", job.getDisplayName()));
	    return true;
	}

	if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
	    String message = Jobs.getLanguage().getMessage("command.employ.error.fullslots");
	    message = message.replace("%jobname%", job.getDisplayName());
	    sender.sendMessage(message);
	    return true;
	}

	try {
	    // check if player already has the job
	    Jobs.getPlayerManager().joinJob(jPlayer, job);
	    Player player = jPlayer.getPlayer();
	    if (player != null)
		player.sendMessage(Jobs.getLanguage().getMessage("command.employ.output.target", "%jobname%", job.getDisplayName()));

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Throwable e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
