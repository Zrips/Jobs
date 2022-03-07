package com.gamingmesh.jobs.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.PlayerCamp;

import net.Zrips.CMILib.Logs.CMIDebug;

public final class JobsPayment14Listener implements Listener {

    // BlockCookEvent does not have "cooking owner"
    private final Map<UUID, List<PlayerCamp>> campPlayers = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTransformEvent(EntityTransformEvent event) {
	    
	if (!Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	if (!event.getEntity().hasMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata()))
	    return;

	// Converting to string for backwards compatibility
	switch (event.getTransformReason().toString()) {
	case "CURED":
	    break;
	case "DROWNED":
	    if (!event.getEntityType().equals(EntityType.ZOMBIE))
		return;

	    event.getTransformedEntity().setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(Jobs.getInstance(), true));
	    break;
	case "FROZEN":
	    if (!event.getEntityType().equals(EntityType.SKELETON))
		return;
	    event.getTransformedEntity().setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(Jobs.getInstance(), true));
	    break;
	case "INFECTION":
	    break;
	case "LIGHTNING":
	    break;
	case "PIGLIN_ZOMBIFIED":
	    break;
	case "SHEARED":
	    break;
	case "SPLIT":

	    if (!event.getEntityType().equals(EntityType.SLIME) && !event.getEntityType().equals(EntityType.MAGMA_CUBE))
		return;
	    for (Entity entity : event.getTransformedEntities()) {
		entity.setMetadata(Jobs.getPlayerManager().getMobSpawnerMetadata(), new FixedMetadataValue(Jobs.getInstance(), true));
	    }
	    break;
	case "UNKNOWN":
	    break;
	default:
	    break;
	}
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCook(BlockCookEvent event) {
	if (event.getBlock().getType() != Material.CAMPFIRE || campPlayers.isEmpty())
	    return;

	if (!Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;

	Location blockLoc = event.getBlock().getLocation();

	for (Map.Entry<UUID, List<PlayerCamp>> map : campPlayers.entrySet()) {
	    List<PlayerCamp> camps = map.getValue();

	    if (camps.isEmpty()) {
		campPlayers.remove(map.getKey());
		continue;
	    }

	    for (PlayerCamp camp : new ArrayList<>(camps)) {
		if (camp.getBlock().getLocation().equals(blockLoc)) {
		    if (camp.getItem().equals(event.getSource())) {
			camps.remove(camp);

			if (camps.isEmpty()) {
			    campPlayers.remove(map.getKey());
			} else {
			    campPlayers.put(map.getKey(), camps);
			}
		    }

		    Jobs.action(Jobs.getPlayerManager().getJobsPlayer(map.getKey()), new ItemActionInfo(event.getSource(), ActionType.BAKE));
		    break;
		}
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
	if (event.getBlock().getType() != Material.CAMPFIRE || campPlayers.isEmpty())
	    return;

	UUID playerUId = event.getPlayer().getUniqueId();
	List<PlayerCamp> camps = campPlayers.get(playerUId);
	if (camps == null)
	    return;

	if (camps.isEmpty()) {
	    campPlayers.remove(playerUId);
	    return;
	}

	Location blockLoc = event.getBlock().getLocation();

	for (PlayerCamp camp : new ArrayList<>(camps)) {
	    if (camp.getBlock().getLocation().equals(blockLoc)) {
		camps.remove(camp);

		if (camps.isEmpty()) {
		    campPlayers.remove(playerUId);
		} else {
		    campPlayers.put(playerUId, camps);
		}

		break;
	    }
	}
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCampPlace(PlayerInteractEvent ev) {
	org.bukkit.block.Block click = ev.getClickedBlock();

	if (click == null || click.getType() != Material.CAMPFIRE || !ev.hasItem())
	    return;

	if (!JobsPaymentListener.payIfCreative(ev.getPlayer()) || !Jobs.getGCManager().canPerformActionInWorld(click.getWorld()))
	    return;

	List<PlayerCamp> camps = campPlayers.getOrDefault(ev.getPlayer().getUniqueId(), new ArrayList<>());
	camps.add(new PlayerCamp(ev.getItem(), click));

	campPlayers.put(ev.getPlayer().getUniqueId(), camps);
    }
}
