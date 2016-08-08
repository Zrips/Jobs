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

	if (args.length < 1 || Jobs.getJob(args[0]) == null && Jobs.getJob(args[1]) == null) {
	    Jobs.getCommandManager().sendUsage(sender, "give");
	    return true;
	}

	if (args.length == 2 && sender instanceof Player) {
	    Job job = Jobs.getJob(args[0]);
	    for (JobItems item : job.getItems()) {
		if (item.getNode().equalsIgnoreCase(args[1])) {
		    GiveItem.GiveItemForPlayer((Player) sender, item.getId(), 0, 1, item.getName(), item.getLore(), item.getEnchants());
		    return true;
		}
	    }
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.noitem"));
	    return true;
	} else if (args.length == 3) {
	    Job job = Jobs.getJob(args[1]);
	    Player player = Bukkit.getPlayer(args[0]);
	    if (player == null) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.notonline", "%playername%", args[0]));
		return true;
	    }
	    for (JobItems item : job.getItems()) {
		if (item.getNode().equalsIgnoreCase(args[2])) {
		    GiveItem.GiveItemForPlayer(player, item.getId(), 0, 1, item.getName(), item.getLore(), item.getEnchants());
		    return true;
		}
	    }
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.give.output.noitem"));
	    return true;
	} else {
	    Jobs.getCommandManager().sendUsage(sender, "give");
	    return true;
	}
    }
}
