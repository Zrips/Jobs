package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.stuff.GiveItem;

public class give implements Cmd {

    private enum actions {
	items, limiteditems;
	public static actions getByname(String name) {
	    for (actions one : actions.values()) {
		if (one.name().equalsIgnoreCase(name))
		    return one;
	    }
	    return null;
	}
    }

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	Player player = null;
	Job job = null;
	actions name = null;
	String itemName = null;

	for (String one : args) {
	    if (player == null) {
		player = Bukkit.getPlayer(one);
		if (player != null)
		    continue;
	    }

	    if (job == null) {
		job = Jobs.getJob(one);
		if (job != null)
		    continue;
	    }

	    if (name == null) {
		name = actions.getByname(one);
		if (name != null)
		    continue;
	    }
	    itemName = one;
	}

	if (player == null && sender instanceof Player)
	    player = (Player) sender;

	if (player == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.notonline"));
	    return true;
	}

	if (name == null)
	    name = actions.items;

	if (itemName == null) {
	    Jobs.getCommandManager().sendUsage(sender, "give");
	    return true;
	}

	switch (name) {
	case items:
	    JobItems jItem = ItemBoostManager.getItemByKey(itemName);
	    ItemStack item = jItem == null ? null : jItem.getItemStack(player);

	    if (item == null) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.noitem"));
		return true;
	    }

	    GiveItem.giveItemForPlayer(player, item);
	    break;
	case limiteditems:
	    if (job == null) {
		Jobs.getCommandManager().sendUsage(sender, "give");
		return true;
	    }

	    JobLimitedItems jLItem = job.getLimitedItems().get(itemName.toLowerCase());
	    ItemStack limItem = jLItem == null ? null : jLItem.getItemStack(player);

	    if (limItem == null) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.noitem"));
		return true;
	    }

	    GiveItem.giveItemForPlayer(player, limItem);
	    break;
	default:
	    break;
	}
	return true;
    }
}