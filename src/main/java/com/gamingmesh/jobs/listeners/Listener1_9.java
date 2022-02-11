package com.gamingmesh.jobs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import com.gamingmesh.jobs.Jobs;

public class Listener1_9 implements Listener {

    @EventHandler
    public void onPlayerHandSwap(PlayerSwapHandItemsEvent event) {
	Jobs.getPlayerManager().resetItemBonusCache(event.getPlayer().getUniqueId());
    }
}
