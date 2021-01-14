package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;

public class clearownership implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!Jobs.hasPermission(sender, "jobs.command.admin.clearownership", true))
		    return true;
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	} else if (sender instanceof Player)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	if (jPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    else
		Jobs.getCommandManager().sendUsage(sender, "clearownership");
	    return true;
	}

	BlockOwnerShip furnaceOwnerShip = plugin.getBlockOwnerShip(BlockTypes.FURNACE).orElse(null),
		brewingOwnerShip = plugin.getBlockOwnerShip(BlockTypes.BREWING_STAND).orElse(null),
		smokerOwnerShip = plugin.getBlockOwnerShip(BlockTypes.SMOKER).orElse(null),
		blastFurnaceOwnerShip = plugin.getBlockOwnerShip(BlockTypes.BLAST_FURNACE).orElse(null);
	int furnace = furnaceOwnerShip != null ? furnaceOwnerShip.clear(jPlayer.getUniqueId()) : 0,
		brewing = brewingOwnerShip != null ? brewingOwnerShip.clear(jPlayer.getUniqueId()) : 0,
		smoker = smokerOwnerShip != null ? smokerOwnerShip.clear(jPlayer.getUniqueId()) : 0,
		blast = blastFurnaceOwnerShip != null ? blastFurnaceOwnerShip.clear(jPlayer.getUniqueId()) : 0;

	sender.sendMessage(Jobs.getLanguage().getMessage("command.clearownership.output.cleared", "[furnaces]", furnace,
		"[brewing]", brewing, "[smoker]", Version.isCurrentEqualOrHigher(Version.v1_14_R1) ? smoker : "",
		"[blast]", Version.isCurrentEqualOrHigher(Version.v1_14_R1) ? blast : ""));
	return true;
    }
}
