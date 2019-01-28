package com.gamingmesh.jobs.commands.list;

import java.util.List;

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

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}
	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.fireall.error.nojobs"));
	    return true;
	}

	try {
	    Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	    Player player = jPlayer.getPlayer();
	    if (player != null)
		player.sendMessage(Jobs.getLanguage().getMessage("command.fireall.output.target"));

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Throwable e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
