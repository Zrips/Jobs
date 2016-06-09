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

public class fire implements Cmd {

    @JobCommand(1900)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "fire");
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
	
	if (jPlayer == null){
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}
	
	if (!jPlayer.isInJob(job)) {
	    String message = ChatColor.RED + Jobs.getLanguage().getMessage("command.fire.error.nojob", "%jobname%", job.getChatColor() + job.getName() + ChatColor.RED);
	    sender.sendMessage(message);
	    return true;
	}
	try {
	    Jobs.getPlayerManager().leaveJob(jPlayer, job);
	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		String message = Jobs.getLanguage().getMessage("command.fire.output.target", "%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE);
		player.sendMessage(message);
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
