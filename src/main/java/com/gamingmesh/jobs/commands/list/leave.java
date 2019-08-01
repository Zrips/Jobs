package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.Util;

public class leave implements Cmd {

    @Override
    @JobCommand(800)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player))
	    return false;

	if (args.length < 1) {
	    Jobs.getCommandManager().sendUsage(sender, "leave");
	    return true;
	}

	Player pSender = (Player) sender;

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (Jobs.getGCManager().EnableConfirmation) {
	    String uuid = pSender.getUniqueId().toString();

	    if (!Util.confirmLeave.contains(uuid)) {
		Util.confirmLeave.add(uuid);

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> Util.confirmLeave.remove(uuid),
		    20 * Jobs.getGCManager().ConfirmExpiryTime);

		pSender.sendMessage(Jobs.getLanguage().getMessage("command.leave.confirmationNeed", "[jobname]", jobName,
			"[time]", Jobs.getGCManager().ConfirmExpiryTime));
		return true;
	    }

	    Util.confirmLeave.remove(uuid);
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	if (Jobs.getPlayerManager().leaveJob(jPlayer, job))
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.leave.success", "%jobname%", job.getChatColor() + job.getName()));
	else
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));

	return true;
    }
}
