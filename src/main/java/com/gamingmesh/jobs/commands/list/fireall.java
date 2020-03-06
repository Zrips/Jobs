package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class fireall implements Cmd {

    @Override
    @JobCommand(2000)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 1) {
	    Jobs.getCommandManager().sendUsage(sender, "fireall");
	    return true;
	}

	if (args[0].equalsIgnoreCase("all")) {
	    boolean success = false;
	    for (Player player : Bukkit.getOnlinePlayers()) {
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
		List<JobProgression> jobs = jPlayer.getJobProgression();
		if (jobs.isEmpty()) {
		    continue;
		}

		Jobs.getPlayerManager().leaveAllJobs(jPlayer);

		player.sendMessage(Jobs.getLanguage().getMessage("command.fireall.output.target"));
		success = true;
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage(success ? "general.admin.success" : "command.fireall.error.nojobs"));
		return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.isEmpty()) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.fireall.error.nojobs"));
	    return true;
	}

	Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	Player player = jPlayer.getPlayer();
	if (player != null)
	    player.sendMessage(Jobs.getLanguage().getMessage("command.fireall.output.target"));

	sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	return true;
    }
}
