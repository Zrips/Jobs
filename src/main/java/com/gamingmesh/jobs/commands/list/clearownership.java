package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class clearownership implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	String location = null;

	for (String one : args) {

	    if (!one.contains(":") && jPlayer == null && !sender.getName().equalsIgnoreCase(one) && Jobs.hasPermission(sender, "jobs.command.admin.clearownership", true)) {
		jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
		if (jPlayer != null)
		    continue;
	    }

	    if (one.contains(":") && location == null) {
		location = one;
	    }
	}

	if (jPlayer==null && sender instanceof Player)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	if (jPlayer == null) {
	    if (args.length >= 1)
		CMIMessages.sendMessage(sender, LC.info_NoInformation);
	    else
		Jobs.getCommandManager().sendUsage(sender, "clearownership");
	    return true;
	}

	final JobsPlayer jp = jPlayer;
	final java.util.Map<BlockTypes, Integer> amounts = new java.util.WeakHashMap<>();
	for (BlockTypes type : BlockTypes.values()) {

	    if (location == null)
		plugin.getBlockOwnerShip(type).ifPresent(ownerShip -> amounts.put(type, ownerShip.clear(jp.getUniqueId())));
	    else {
		String l = location;
		plugin.getBlockOwnerShip(type).ifPresent(ownerShip -> amounts.put(type, ownerShip.remove(jp.getUniqueId(), l)));
	    }
	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.clearownership.output.cleared", "[furnaces]", amounts.getOrDefault(BlockTypes.FURNACE, 0), "[brewing]", amounts.getOrDefault(
	    BlockTypes.BREWING_STAND, 0), "[smoker]", amounts.getOrDefault(BlockTypes.SMOKER, 0), "[blast]", amounts.getOrDefault(BlockTypes.BLAST_FURNACE, 0)));
	return true;
    }
}
