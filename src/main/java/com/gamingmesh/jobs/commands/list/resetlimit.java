package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;

public class resetlimit implements Cmd {

    @Override
    @JobCommand(707)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "resetlimit");
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	jPlayer.resetPaymentLimit();
	sender.sendMessage(Jobs.getLanguage().getMessage("command.resetlimit.output.reseted", "%playername%", jPlayer.getUserName()));

	return true;
    }
}
