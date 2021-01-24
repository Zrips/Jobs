package com.gamingmesh.jobs.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.gamingmesh.jobs.Jobs;

public class PistonProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnBlockMove(BlockPistonExtendEvent event) {
	if (event.isCancelled() || !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld())
	    || !Jobs.getGCManager().useBlockProtection)
	    return;

	BlockFace dir = event.getDirection();
	int x = dir.getModX(),
	    y = dir.getModY(),
	    z = dir.getModZ();

	for (int i = event.getBlocks().size() - 1; i >= 0; i--) {
	    Block one = event.getBlocks().get(i);
	    Location oldLoc = one.getLocation();
	    Location newLoc = oldLoc.clone().add(x, y, z);
	    Long bp = Jobs.getBpManager().getTime(oldLoc);
	    if (bp != null) {
		Jobs.getBpManager().addP(newLoc, bp, false, true);
		Jobs.getBpManager().remove(oldLoc);
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnBlockRetractMove(BlockPistonRetractEvent event) {
	if (event.isCancelled() || !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld())
	    || !Jobs.getGCManager().useBlockProtection)
	    return;

	BlockFace dir = event.getDirection();
	int x = dir.getModX(),
	    y = dir.getModY(),
	    z = dir.getModZ();

	List<Block> blocks = Jobs.getNms().getPistonRetractBlocks(event);
	for (int i = blocks.size() - 1; i >= 0; i--) {
	    Block one = blocks.get(i);
	    Location oldLoc = one.getLocation();
	    Location newLoc = oldLoc.clone().add(x, y, z);
	    Long bp = Jobs.getBpManager().getTime(oldLoc);
	    if (bp != null) {
		Jobs.getBpManager().addP(newLoc, bp, false, true);
		Jobs.getBpManager().remove(oldLoc);
	    }
	}
    }
}
