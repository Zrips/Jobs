package com.gamingmesh.jobs.listeners;

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

	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	if (event.isCancelled())
	    return;

	if (!Jobs.getGCManager().useBlockProtection)
	    return;

	BlockFace dir = event.getDirection();

	int x = dir.getModX();
	int y = dir.getModY();
	int z = dir.getModZ();
	for (Block one : event.getBlocks()) {

	    Location oldLoc = one.getLocation();
	    Location newLoc = oldLoc.clone().add(x, y, z);

	    Long bp = Jobs.getBpManager().getTime(oldLoc);
	    if (bp != null) {
		Jobs.getBpManager().addP(newLoc, bp, true);
		Jobs.getBpManager().remove(oldLoc);
	    } else {
		Integer cd = Jobs.getBpManager().getBlockDelayTime(one);
		if (cd != null)
		    Jobs.getBpManager().add(newLoc, cd);
		else if (Jobs.getGCManager().useGlobalTimer)
		    Jobs.getBpManager().add(newLoc, Jobs.getGCManager().globalblocktimer);
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnBlockRetractMove(BlockPistonRetractEvent event) {

	//disabling plugin in world
	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;
	if (event.isCancelled())
	    return;

	if (!Jobs.getGCManager().useBlockProtection)
	    return;

	BlockFace dir = event.getDirection();

	int x = dir.getModX();
	int y = dir.getModY();
	int z = dir.getModZ();

	for (Block one : Jobs.getNms().getPistonRetractBlocks(event)) {
	    Location oldLoc = one.getLocation();
	    Location newLoc = oldLoc.clone().add(x, y, z);
	    Long bp = Jobs.getBpManager().getTime(oldLoc);
	    if (bp != null) {
		Jobs.getBpManager().addP(newLoc, bp, true);
		Jobs.getBpManager().remove(oldLoc);
	    } else {
		Integer cd = Jobs.getBpManager().getBlockDelayTime(one);
		if (cd != null)
		    Jobs.getBpManager().add(newLoc, cd);
		else if (Jobs.getGCManager().useGlobalTimer)
		    Jobs.getBpManager().add(newLoc, Jobs.getGCManager().globalblocktimer);
	    }
	}
    }
}
