package com.gamingmesh.jobs.commands.list;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.BlockProtection;
import com.gamingmesh.jobs.container.DBAction;

public class bp implements Cmd {

    @SuppressWarnings("deprecation")
    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}
	boolean all = false;
	if (args.length > 0 && args[0].equalsIgnoreCase("-a"))
	    all = true;

	final Player player = (Player) sender;

	Location loc = player.getLocation();

	final List<Block> changedBlocks = new ArrayList<>();

	for (int x = -10; x < 10; x++) {
	    for (int y = -10; y < 10; y++) {
		for (int z = -10; z < 10; z++) {
		    Location l = loc.clone().add(x, y, z);
		    BlockProtection bp = Jobs.getBpManager().getBp(l);
		    if (bp != null) {
			Long time = bp.getTime();
			if (!all) {
			    if (bp.getAction() == DBAction.DELETE)
				continue;
			    if (time != -1 && time < System.currentTimeMillis()) {
				Jobs.getBpManager().remove(l);
				continue;
			    }
			}
			changedBlocks.add(l.getBlock());

			if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
			    player.sendBlockChange(l, (bp.getAction() == DBAction.DELETE ?
				CMIMaterial.RED_STAINED_GLASS :
				time == -1 ? CMIMaterial.BLACK_STAINED_GLASS : CMIMaterial.WHITE_STAINED_GLASS).getMaterial().createBlockData());
			} else {
			    if (bp.getAction() == DBAction.DELETE)
				player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 14);
			    else if (time == -1)
				player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 15);
			    else
				player.sendBlockChange(l, CMIMaterial.RED_STAINED_GLASS.getMaterial(), (byte) 0);
			}
		    }
		}
	    }
	}

	if (changedBlocks.isEmpty())
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.bp.output.notFound"));
	else
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.bp.output.found", "%amount%", changedBlocks.size()));

	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    @Override
	    public void run() {
		if (Version.isCurrentEqualOrHigher(Version.v1_15_R1))
		    for (Block one : changedBlocks) {
			player.sendBlockChange(one.getLocation(), one.getBlockData());
		    }
		else
		    for (Block one : changedBlocks) {
			player.sendBlockChange(one.getLocation(), one.getType(), one.getData());
		    }
	    }
	}, 120L);

	return true;
    }
}
