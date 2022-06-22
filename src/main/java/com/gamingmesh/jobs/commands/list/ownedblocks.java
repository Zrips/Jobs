package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobsCommands;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockOwnerShip;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
import com.gamingmesh.jobs.stuff.blockLoc;

import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Locale.Snd;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class ownedblocks implements Cmd {

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	JobsPlayer jPlayer = null;
	if (args.length >= 1) {
	    if (!Jobs.hasPermission(sender, "jobs.command.admin.ownedblocks", true))
		return true;
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	} else if (sender instanceof Player)
	    jPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	if (jPlayer == null) {
	    if (args.length >= 1)
		CMIMessages.sendMessage(sender, LC.info_NoInformation);
	    else
		Jobs.getCommandManager().sendUsage(sender, "ownedblocks");
	    return true;
	}

	final JobsPlayer jp = jPlayer;

	RawMessage rm = new RawMessage();

	Snd snd = new Snd();
	snd.setSender(sender);
	snd.setTargetName(jp.getName());

	rm.addText(LC.info_PlayerSpliter.getLocale(snd));

	int i = 0;
	for (BlockTypes type : BlockTypes.values()) {

	    Optional<BlockOwnerShip> ownerShip = plugin.getBlockOwnerShip(type);

	    if (!ownerShip.isPresent())
		continue;

	    HashMap<String, blockLoc> records = ownerShip.get().getBlockOwnerShips().get(jp.getUniqueId());
	    if (records == null)
		continue;

	    for (Entry<String, blockLoc> record : records.entrySet()) {
		i++;
		rm.addText("\n");

		CMIMaterial material = CMIMaterial.get(type.toString());

		CMILocation loc = CMILocation.fromString(record.getKey(), ":");

		rm.addText(Jobs.getLanguage().getMessage("command.ownedblocks.output.list", "[place]", i, "[type]", material.getName(), "[location]", LC.Location_Full.getLocale((Location) loc)));
		rm.addHover(Jobs.getLanguage().getMessage("command.ownedblocks.output.listHover", "[location]", LC.Location_Full.getLocale((Location) loc)));
		rm.addCommand(JobsCommands.LABEL + " " + clearownership.class.getSimpleName() + " " + jp.getName() + " " + record.getKey());
		if (record.getValue().isDisabled()) {
		    rm.addText(Jobs.getLanguage().getMessage("command.ownedblocks.output.disabled"));
		    rm.addHover(Jobs.getLanguage().getMessage("command.ownedblocks.output.disabledHover"));
		}
	    }
	}
	rm.show(sender);
	if (i == 0)
	    LC.info_nothingToShow.sendMessage(sender);

	return true;
    }
}
