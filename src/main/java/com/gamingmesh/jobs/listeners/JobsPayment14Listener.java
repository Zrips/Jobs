package com.gamingmesh.jobs.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.block.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerCamp;

public class JobsPayment14Listener implements Listener {

    private Map<Player, PlayerCamp> campPlayers = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onCook(BlockCookEvent event) {
	if (!Jobs.getInstance().isEnabled())
	    return;

	if (event.isCancelled())
	    return;

	if (event.getBlock() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getBlock().getWorld()))
	    return;

	if (!(event.getBlock().getState() instanceof Campfire))
	    return;

	for (Iterator<Map.Entry<Player, PlayerCamp>> it = campPlayers.entrySet().iterator(); it.hasNext();) {
	    Map.Entry<Player, PlayerCamp> camps = it.next();
	    if (camps == null) {
		continue;
	    }

	    if (!camps.getValue().getBlock().getLocation().equals(event.getBlock().getLocation())) {
		continue;
	    }

	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(camps.getKey());
	    if (jPlayer == null)
		return;

	    Jobs.action(jPlayer, new ItemActionInfo(event.getSource(), ActionType.BAKE));
	    if (camps.getValue().getItem().equals(event.getSource())) {
		it.remove();
	    }
	}
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCampPlace(PlayerInteractEvent ev) {
	if (!Jobs.getInstance().isEnabled())
	    return;

	org.bukkit.block.Block click = ev.getClickedBlock();
	if (click == null || !click.getType().isBlock() || !click.getType().equals(org.bukkit.Material.CAMPFIRE))
	    return;

	if (!Jobs.getGCManager().canPerformActionInWorld(click.getWorld()))
	    return;

	if (!(click.getState() instanceof Campfire))
	    return;

	if (!ev.hasItem())
	    return;

	Player p = ev.getPlayer();

	if (!JobsPaymentListener.payIfCreative(p))
	    return;

	campPlayers.put(p, new PlayerCamp(ev.getItem(), click));
    }
}
