package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class shop implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player)) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
	    return true;
	}

	if (args.length != 0 && args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "shop");
	    return true;
	}

	int page = 1;
	if (args.length == 1)
	    try {
		page = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
	    }

	Jobs.getShopManager().openShopGui((Player) sender, page);
	return true;
    }
}
