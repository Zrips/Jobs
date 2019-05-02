package com.gamingmesh.jobs.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class JobsPaymentEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer offlinePlayer;
    private double money;
    private double points;
    private boolean cancelled = false;

    public JobsPaymentEvent(OfflinePlayer offlinePlayer, double money, double points) {
	super(true);
	this.offlinePlayer = offlinePlayer;
	this.money = money;
	this.points = points;
    }

    public OfflinePlayer getPlayer() {
	return offlinePlayer;
    }

    public double getAmount() {
	return money;
    }

    public double getPoints() {
	return points;
    }

    public void setPoints(double points) {
	this.points = points;
    }

    @Override
    public boolean isCancelled() {
	return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
	this.cancelled = cancelled;
    }

    public void setAmount(double money) {
	this.money = money;
    }

    @Override
    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }
}
