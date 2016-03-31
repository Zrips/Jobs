package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;

public class shop implements Cmd {

    @JobCommand(750)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return true;
	}

	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "shop");
	    return true;
	}

	Player player = (Player) sender;

	Inventory inv = Jobs.getShopManager().CreateJobsGUI(player);

	Inventory topinv = player.getOpenInventory().getTopInventory();
	if (topinv != null)
	    player.closeInventory();

	Jobs.getShopManager().GuiList.add(player.getName());

	player.openInventory(inv);

	return true;
    }
}
