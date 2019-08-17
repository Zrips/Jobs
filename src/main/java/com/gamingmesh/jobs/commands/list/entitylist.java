package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;

public class entitylist implements Cmd {

    @Override
    @JobCommand(1452)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "entitylist");
	    return true;
	}

	String msg = "";

	String c1 = "&e";
	String c2 = "&6";

	int i = 0;
	for (EntityType type : EntityType.values()) {

	    if (!type.isAlive())
		continue;
	    if (!type.isSpawnable())
		continue;

	    i++;
	    if (!msg.isEmpty())
		msg += ", ";
	    if (i > 1) {
		msg += c1;
		i = 0;
	    } else {
		msg += c2;
	    }
	    msg += type.name().toLowerCase();
	}

	sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', msg));

	return true;
    }

}
