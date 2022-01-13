package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

import net.Zrips.CMILib.RawMessages.RawMessage;

public class join implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 1 && args.length != 0 && args.length != 2) {
	    Jobs.getCommandManager().sendUsage(sender, "join");
	    return true;
	}

	if (args.length == 0) {
	    plugin.getServer().dispatchCommand(sender, "jobs browse");
	    return true;
	}

	Job job = Jobs.getJob(args[0]);
	if (job == null) {
	    // job does not exist
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (!Jobs.getCommandManager().hasJobPermission(sender, job)) {
	    // The player do not have permission to join the job
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.permission"));
	    return true;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
	if (jPlayer == null) { // Load player into cache
	    Jobs.getPlayerManager().playerJoin(pSender);
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);
	}

	if (jPlayer == null) {
	    return true;
	}

	if (jPlayer.isInJob(job)) {
	    // already in job message
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.join.error.alreadyin", "%jobname%", job.getDisplayName()));
	    return true;
	}

	if (job.getMaxSlots() != null && Jobs.getUsedSlots(job) >= job.getMaxSlots()) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.join.error.fullslots", "%jobname%", job.getDisplayName()));
	    return true;
	}

	if (!job.isIgnoreMaxJobs() && !Jobs.getPlayerManager().getJobsLimit(jPlayer, (short) jPlayer.progression.size())) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.join.error.maxjobs"));
	    return true;
	}

	if (args.length == 2 && args[1].equalsIgnoreCase("-needConfirmation")) {
	    new RawMessage().addText(Jobs.getLanguage().getMessage("command.join.confirm", "[jobname]", job.getName()))
		.addHover(Jobs.getLanguage().getMessage("command.join.confirm", "[jobname]", job.getName()))
		.addCommand("jobs join " + job.getName()).show(pSender);
	    return true;
	}

	JobProgression ajp = jPlayer.getArchivedJobProgression(job);
	if (ajp != null && !ajp.canRejoin()) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.join.error.rejoin", "[time]", ajp.getRejoinTimeMessage()));
	    return true;
	}

	Jobs.getPlayerManager().joinJob(jPlayer, job);
	pSender.sendMessage(Jobs.getLanguage().getMessage("command.join.success", "%jobname%", job.getDisplayName()));
	return true;
    }
}
