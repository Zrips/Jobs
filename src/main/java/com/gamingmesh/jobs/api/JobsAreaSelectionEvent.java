package com.gamingmesh.jobs.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gamingmesh.jobs.container.CuboidArea;

public final class JobsAreaSelectionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private CuboidArea area;
    private Player player;

    public JobsAreaSelectionEvent(Player player, CuboidArea area) {
	this.player = player;
	this.area = area;
    }

    public Player getPlayer() {
	return player;
    }

    public CuboidArea getArea() {
	return area;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }
}