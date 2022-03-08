package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

public class resetexploreregion implements Cmd {

    private static String WORLD = "world";
    private static String REGEX = "^[0-9a-zA-Z_-]+$";

    @Override
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (args.length != 2 || !WORLD.equals(args[0])) {
	    Jobs.getCommandManager().sendUsage(sender, "resetexploreregion");
	    return true;
	}

	if (!Jobs.getGCManager().resetExploringData) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.resetexploreregion.output.notenabled"));
	    return true;
	}

	final String worldName = args[1];
	if (!worldName.matches(REGEX)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.resetexploreregion.output.invalidname"));
	    return true;
	}

	Jobs.getExploreManager().resetRegion(worldName);
	sender.sendMessage(Jobs.getLanguage().getMessage("command.resetexploreregion.output.reseted", "%worldname%", worldName));
	return true;
    }
}
