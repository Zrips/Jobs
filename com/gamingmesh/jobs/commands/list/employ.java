package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class employ implements Cmd {

    @JobCommand(1800)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "employ");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	if (jPlayer.isInJob(job)) {
	    // already in job message
	    String message = ChatColor.RED + Jobs.getLanguage().getMessage("command.employ.error.alreadyin", "%jobname%", job.getChatColor() + job.getName()
		+ ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}
	try {
	    // check if player already has the job
	    Jobs.getPlayerManager().joinJob(jPlayer, job);
	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null)
		player.sendMessage(Jobs.getLanguage().getMessage("command.employ.output.target", "%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE));

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
