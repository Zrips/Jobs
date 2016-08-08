package com.gamingmesh.jobs.commands.list;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;

public class leaveall implements Cmd {

    @Override
    @JobCommand(900)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	Player pSender = (Player) sender;
	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pSender);

	List<JobProgression> jobs = jPlayer.getJobProgression();
	if (jobs.size() == 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.error.nojobs"));
	    return true;
	}

	Jobs.getPlayerManager().leaveAllJobs(jPlayer);
	sender.sendMessage(Jobs.getLanguage().getMessage("command.leaveall.success"));
	return true;
    }
}
