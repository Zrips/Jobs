package com.gamingmesh.jobs.commands.list;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class leave implements Cmd {

    private Set<CommandSender> confirm = new HashSet<>();

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
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	String jobName = args[0];
	Job job = Jobs.getJob(jobName);
	if (job == null) {
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (Jobs.getGCManager().EnableConfirmation) {
	    if (!confirm.contains(pSender)) {
		confirm.add(pSender);
		org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> confirm.remove(pSender), 20 * Jobs.getGCManager().ConfirmExpiryTime);
		pSender.sendMessage(Jobs.getLanguage().getMessage("command.leave.confirmationNeed", "[jobname]", jobName,
			"[time]", Jobs.getGCManager().ConfirmExpiryTime));
		return true;
	    }
	    confirm.remove(pSender);
	}

	if (Jobs.getPlayerManager().leaveJob(jPlayer, job))
	    pSender.sendMessage(Jobs.getLanguage().getMessage("command.leave.success", "%jobname%", job.getChatColor() + job.getName()));
	else
	    pSender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));

	return true;
    }
}
