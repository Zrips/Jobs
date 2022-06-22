package com.gamingmesh.jobs.commands.list;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;

public class blockinfo implements Cmd {

    @Override
    @SuppressWarnings("deprecation")
    public boolean perform(Jobs plugin, CommandSender sender, String[] args) {
	if (!(sender instanceof Player)) {
	    CMIMessages.sendMessage(sender, LC.info_Ingame);
	    return false;
	}

	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "blockinfo");
	    return true;
	}

	Block block = Util.getTargetBlock((Player) sender, 15);
	CMIMaterial cmat = CMIMaterial.get(block);

	if (block == null || cmat.isAir())
	    return true;

	short blockData = CMIMaterial.getBlockData(block);
	String dataString = blockData == 0 ? "" : "-" + blockData;

	RawMessage rm = new RawMessage();

	rm.addText(Jobs.getLanguage().getMessage("general.info.separator") + "\n");
	rm.addText(Jobs.getLanguage().getMessage("command.blockinfo.output.material", "%blockname%", CMIMaterial.get(block).getName()) + "\n");
	rm.addSuggestion(CMIMaterial.get(block).getName());

	if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
	    rm.addText(Jobs.getLanguage().getMessage("command.blockinfo.output.id", "%blockid%", block.getType().getId()) + "\n");
	    rm.addSuggestion(String.valueOf(block.getType().getId()));
	}
	if (blockData != 0) {
	    rm.addText(Jobs.getLanguage().getMessage("command.blockinfo.output.state", "%blockdata%", blockData) + "\n");
	    rm.addSuggestion(String.valueOf(blockData));
	}
	if (Version.isCurrentEqualOrHigher(Version.v1_14_R1))
	    rm.addText(Jobs.getLanguage().getMessage("command.blockinfo.output.use", "%usage%", block.getType().name() + dataString) + "\n");
	else
	    rm.addText(Jobs.getLanguage().getMessage("command.blockinfo.output.deprecated", "%first%", block.getType().getId() + dataString, "%second%", block.getType().name() + dataString) + "\n");
	rm.addSuggestion(block.getType().name() + dataString);

	rm.addText(Jobs.getLanguage().getMessage("general.info.separator"));
	rm.show(sender);

	return true;
    }
}
