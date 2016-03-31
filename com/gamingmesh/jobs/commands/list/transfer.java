
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

public class transfer implements Cmd {

    @JobCommand(1500)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	if (args.length < 3) {
	    Jobs.getCommandManager().sendUsage(sender, "transfer");
	    return true;
	}

	@SuppressWarnings("deprecation")
	OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

	Job oldjob = Jobs.getJob(args[1]);
	Job newjob = Jobs.getJob(args[2]);
	if (oldjob == null) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	if (newjob == null) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	try {
	    if (jPlayer.isInJob(oldjob) && !jPlayer.isInJob(newjob)) {
		Jobs.getPlayerManager().transferJob(jPlayer, oldjob, newjob);

		Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
		if (player != null) {
		    String message = Jobs.getLanguage().getMessage("command.transfer.output.target",
			"%oldjobname%", oldjob.getChatColor() + oldjob.getName() + ChatColor.WHITE,
			"%newjobname%", newjob.getChatColor() + newjob.getName() + ChatColor.WHITE);
		    player.sendMessage(message);
		}

		sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	    }
	} catch (Exception e) {
	    sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
