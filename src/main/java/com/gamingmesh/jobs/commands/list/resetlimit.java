package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;

public class resetlimit implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "resetlimit");
	    return true;
	}

	if (args[0].equalsIgnoreCase("all")) {
	    for (org.bukkit.entity.Player pl : org.bukkit.Bukkit.getOnlinePlayers()) {
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pl);
		if (jPlayer != null) {
		    jPlayer.resetPaymentLimit();
		}
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage("command.resetlimit.output.reseted", "%playername%", ""));
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	jPlayer.resetPaymentLimit();
	sender.sendMessage(Jobs.getLanguage().getMessage("command.resetlimit.output.reseted", "%playername%", jPlayer.getName(), "%playerdisplayname%", jPlayer.getDisplayName()));
	return true;
    }
}
