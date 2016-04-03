package com.gamingmesh.jobs.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class JobsExpGainEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer offlinePlayer;
    private double exp;
    private boolean cancelled;

    public JobsExpGainEvent(OfflinePlayer offlinePlayer, double exp) {
	this.offlinePlayer = offlinePlayer;
	this.exp = exp;
    }

    public OfflinePlayer getPlayer() {
	return this.offlinePlayer;
    }

    public double getExp() {
	return this.exp;
    }

    public boolean isCancelled() {
	return cancelled;
    }

    public void setCancelled(boolean cancel) {
	cancelled = cancel;
    }

    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }
}