package com.gamingmesh.jobs.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class JobsPaymentEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private OfflinePlayer offlinePlayer;
	private double money;
	private boolean cancelled;

	public JobsPaymentEvent(OfflinePlayer offlinePlayer, double money) {
		this.offlinePlayer = offlinePlayer;
		this.money = money;
	}

	public OfflinePlayer getPlayer() {
		return this.offlinePlayer;
	}

	public double getAmount() {
		return this.money;
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