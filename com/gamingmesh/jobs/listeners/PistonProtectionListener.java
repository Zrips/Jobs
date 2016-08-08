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

public class PistonProtectionListener implements Listener {

    @SuppressWarnings("unused")
    private Jobs plugin;

    public PistonProtectionListener(Jobs plugin) {
	this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public boolean CheckBlock(Block block) {
	for (String BlockId : Jobs.getRestrictedBlockManager().restrictedBlocks) {
	    if (BlockId.equalsIgnoreCase(String.valueOf(block.getTypeId()))) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public boolean CheckPlaceBlock(Block block) {
	for (int BlockId : Jobs.getRestrictedBlockManager().restrictedPlaceBlocksTimer) {
	    if (BlockId == block.getTypeId()) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public boolean CheckVegy(Block block) {
	for (String ConfigOneBlock : Jobs.getRestrictedBlockManager().restrictedBlocksTimer) {
	    int ConfigPlacedBlockId = 0;
	    try {
		ConfigPlacedBlockId = Integer.parseInt(ConfigOneBlock.split("-")[0]);
	    } catch (NumberFormatException e) {
		continue;
	    }
	    if (block.getTypeId() == ConfigPlacedBlockId) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    public boolean checkVegybreak(Block block, Player player) {
	for (String ConfigOneBlock : Jobs.getRestrictedBlockManager().restrictedBlocksTimer) {
	    int ConfigPlacedBlockId = 0;
	    int ConfigPlacedBlockTimer = 0;
	    try {
		ConfigPlacedBlockId = Integer.parseInt(ConfigOneBlock.split("-")[0]);
		ConfigPlacedBlockTimer = Integer.parseInt(ConfigOneBlock.split("-")[1]);
	    } catch (NumberFormatException e) {
		continue;
	    }
	    if (block.getTypeId() == ConfigPlacedBlockId) {
		if (CheckVegyTimer(block, ConfigPlacedBlockTimer, player)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean CheckVegyTimer(Block block, int time, Player player) {
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
    public void OnBlockMove(BlockPistonExtendEvent event) {
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
    public void OnBlockRetractMove(BlockPistonRetractEvent event) {
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
