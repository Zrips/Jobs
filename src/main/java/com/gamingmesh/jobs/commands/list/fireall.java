package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class fireall implements Cmd {

	@Override
	@JobCommand(2000)
	public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
		if (args.length < 1) {
			Jobs.getCommandManager().sendUsage(sender, "fireall");
			return true;
		}

		@SuppressWarnings("deprecation")
		OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayerOffline(offlinePlayer);

		List<JobProgression> jobs = jPlayer.getJobProgression();
		if (jobs.size() == 0) {
			sender.sendMessage(Jobs.getLanguage().getMessage("command.fireall.error.nojobs"));
			return true;
		}

		try {
			Jobs.getPlayerManager().leaveAllJobs(jPlayer);
			Player player = Bukkit.getServer().getPlayer(offlinePlayer.getUniqueId());
			if (player != null) {
				player.sendMessage(Jobs.getLanguage().getMessage("command.fireall.output.target"));
			}

			sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + Jobs.getLanguage().getMessage("general.admin.error"));
		}
		return true;
	}
}
