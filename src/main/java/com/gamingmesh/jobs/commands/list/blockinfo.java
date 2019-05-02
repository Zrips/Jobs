package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.stuff.Util;

public class blockinfo implements Cmd {

    @Override
    @SuppressWarnings("deprecation")
    @JobCommand(1450)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "blockinfo");
	    return true;
	}

	Player player = (Player) sender;
	Block block = Util.getTargetBlock(player, 15);

	if (block == null || block.getState().getType() == Material.AIR)
	    return true;

	String dataString = getData(block) == 0 ? "" : "-" + getData(block);

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.name", "%blockname%", block.getType().name()));
	if (Version.isCurrentEqualOrLower(Version.v1_13_R2))
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.id", "%blockid%", block.getType().getId()));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.data", "%blockdata%", getData(block)));
	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1))
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.usage", "%first%", "",
		"%second%", block.getType().name() + dataString));
	else
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.usage", "%first%", block.getType().getId() + dataString,
		"%second%", block.getType().name() + dataString));
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }

    private static byte getData(Block block) {
	@SuppressWarnings("deprecation")
	byte data = block.getData();
	if (block.getType() == Material.COCOA)
	    switch (data) {
	    case 0:
	    case 1:
	    case 2:
	    case 3:
		data = 0;
		break;
	    case 4:
	    case 5:
	    case 6:
	    case 7:
		data = 1;
		break;
	    case 8:
	    case 9:
	    case 10:
	    case 11:
		data = 2;
		break;
	    }
	return data;
    }

}
