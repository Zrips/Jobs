package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.FurnaceBrewingHandling;

public class clearownership implements Cmd {

    @Override
    @JobCommand(400)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!Jobs.hasPermission(sender, "jobs.command.admin.clearownership", true)) {
		return true;
	    }
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	} else if (sender instanceof Player) {
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	}

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    else
		Jobs.getCommandManager().sendUsage(sender, "clearownership");
	    return true;
	}

	int furnace = FurnaceBrewingHandling.clearFurnaces(jPlayer.getPlayerUUID());
	int brewing = FurnaceBrewingHandling.clearBrewingStands(jPlayer.getPlayerUUID());

	sender.sendMessage(Jobs.getLanguage().getMessage("command.clearownership.output.cleared", "[furnaces]", furnace, "[brewing]", brewing));

	return true;
    }
}
