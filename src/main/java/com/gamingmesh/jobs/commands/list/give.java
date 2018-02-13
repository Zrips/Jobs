package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.stuff.GiveItem;

public class give implements Cmd {

    @Override
    @JobCommand(2500)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	Player player = null;
	Job job = null;
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
	    itemName = one;
	}

	if (player == null && sender instanceof Player)
	    player = (Player) sender;

	if (player == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.notonline", "%playername%", args[0]));
	    return true;
	}

	if (job == null || itemName == null) {
	    Jobs.getCommandManager().sendUsage(sender, "give");
	    return true;
	}

	JobItems jItem = job.getItemBonus(itemName);

	if (jItem == null || jItem.getItemStack(player) == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.noitem"));
	    return true;
	}

	GiveItem.GiveItemForPlayer(player, jItem.getItemStack(player, job));
	return true;
    }
}