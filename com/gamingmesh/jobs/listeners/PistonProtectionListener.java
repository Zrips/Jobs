package com.gamingmesh.jobs.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.JobsPlugin;

public class PistonProtectionListener implements Listener {

    @SuppressWarnings("unused")
    private JobsPlugin plugin;

    public PistonProtectionListener(JobsPlugin plugin) {
	this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public static boolean CheckBlock(Block block) {
	for (String BlockId : Jobs.getRestrictedBlockManager().restrictedBlocks) {
	    if (BlockId.equalsIgnoreCase(String.valueOf(block.getTypeId()))) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean CheckPlaceBlock(Block block) {
	for (int BlockId : Jobs.getRestrictedBlockManager().restrictedPlaceBlocksTimer) {
	    if (BlockId == block.getTypeId()) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean CheckVegy(Block block) {
	for (String ConfigOneBlock : Jobs.getRestrictedBlockManager().restrictedBlocksTimer) {
	    int ConfigPlacedBlockId = Integer.valueOf(ConfigOneBlock.split("-")[0]);
	    if (block.getTypeId() == ConfigPlacedBlockId) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean checkVegybreak(Block block, Player player) {
	for (String ConfigOneBlock : Jobs.getRestrictedBlockManager().restrictedBlocksTimer) {
	    int ConfigPlacedBlockId = Integer.valueOf(ConfigOneBlock.split("-")[0]);
	    if (block.getTypeId() == ConfigPlacedBlockId) {
		if (CheckVegyTimer(block, Integer.valueOf(ConfigOneBlock.split("-")[1]), player)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public static boolean CheckVegyTimer(Block block, int time, Player player) {
	long currentTime = System.currentTimeMillis();
	if (!block.hasMetadata(JobsPaymentListener.VegyMetadata))
	    return false;
	long BlockTime = block.getMetadata(JobsPaymentListener.VegyMetadata).get(0).asLong();

	if (currentTime >= BlockTime + time * 1000) {
	    return false;
	}

	int sec = Math.round((((BlockTime + time * 1000) - currentTime)) / 1000);

	Jobs.getActionBar().send(player, Jobs.getLanguage().getMessage("message.blocktimer", "[time]", sec));
	return true;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public static void OnBlockMove(BlockPistonExtendEvent event) {
	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	if (event.isCancelled())
	    return;

	if (!Jobs.getGCManager().useBlockPiston)
	    return;

	List<Block> block = event.getBlocks();
	for (Block OneBlock : block) {
	    if (CheckBlock(OneBlock)) {
		event.setCancelled(true);
		break;
	    }
	}
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void OnBlockRetractMove(BlockPistonRetractEvent event) {
	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	if (event.isCancelled())
	    return;

	if (!Jobs.getGCManager().useBlockPiston)
	    return;

	List<Block> block = Jobs.getNms().getPistonRetractBlocks(event);
	for (Block OneBlock : block) {
	    if (CheckBlock(OneBlock)) {
		event.setCancelled(true);
		break;
	    }
	}
    }
}
