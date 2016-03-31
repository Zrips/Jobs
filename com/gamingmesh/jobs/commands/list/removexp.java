
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

public class removexp implements Cmd {

    @JobCommand(2200)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	if (args.length < 3) {
	    Jobs.getCommandManager().sendUsage(sender, "removexp");
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
	double xpLost;
	try {
	    xpLost = Double.parseDouble(args[2]);
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}
	if (xpLost <= 0) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
	    return true;
	}
	// check if player already has the job
	if (jPlayer.isInJob(job)) {
	    Jobs.getPlayerManager().removeExperience(jPlayer, job, xpLost);

	    Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
	    if (player != null) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.removexp.output.target",
		    "%jobname%", job.getChatColor() + job.getName() + ChatColor.WHITE,
		    "%xplost%", xpLost));
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	}
	return true;
    }
}
