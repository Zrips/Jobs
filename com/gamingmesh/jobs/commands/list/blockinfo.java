package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;

public class blockinfo implements Cmd {

    @SuppressWarnings("deprecation")
    @JobCommand(1450)
    public boolean perform(JobsPlugin plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length != 0) {
	    Jobs.getCommandManager().sendUsage(sender, "blockinfo");
	    return true;
	}

	Player player = (Player) sender;

	Block block = Jobs.getNms().getTargetBlock(player, 15);

	if (block == null || block.getState().getType() == Material.AIR)
	    return true;

	String dataString = block.getData() == 0 ? "" : "-" + block.getData();

	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.name", "%blockname%", block.getType().name()));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.id", "%blockid%", block.getTypeId()));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.data", "%blockdata%", block.getData()));
	sender.sendMessage(Jobs.getLanguage().getMessage("command.blockinfo.output.usage", "%first%", block.getTypeId() + dataString,
	    "%second%", block.getType().name() + dataString));
	sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));

	return true;
    }

}
