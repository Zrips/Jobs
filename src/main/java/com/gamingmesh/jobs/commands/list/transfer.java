
package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class transfer implements Cmd {

    @Override
    @JobCommand(1500)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 3) {
	    Jobs.getCommandManager().sendUsage(sender, "transfer");
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}
	Job oldjob = Jobs.getJob(args[1]);
	Job newjob = Jobs.getJob(args[2]);
	if (oldjob == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	if (newjob == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}
	try {
	    if (jPlayer.isInJob(oldjob) && !jPlayer.isInJob(newjob)) {
		Jobs.getPlayerManager().transferJob(jPlayer, oldjob, newjob);

		Player player = jPlayer.getPlayer();
		if (player != null) {
		    String message = Jobs.getLanguage().getMessage("command.transfer.output.target",
			"%oldjobname%", oldjob.getChatColor() + oldjob.getName(),
			"%newjobname%", newjob.getChatColor() + newjob.getName());
		    player.sendMessage(message);
		}

		sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	    }
	} catch (Throwable e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
