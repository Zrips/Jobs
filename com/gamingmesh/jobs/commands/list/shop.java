package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;

public class shop implements Cmd {

    @Override
    @JobCommand(750)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
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

	Player player = (Player) sender;

	Jobs.getShopManager().openInventory(player, page);

	return true;
    }
}
