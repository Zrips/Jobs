package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;


public class leave implements Cmd {

    @JobCommand(800)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
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
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	Jobs.getPlayerManager().leaveJob(jPlayer, job);
	String message = Jobs.getLanguage().getMessage("command.leave.success");
	message = message.replace("%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
	sender.sendMessage(message);
	return true;
    }
}
