package com.gamingmesh.jobs.api;

import com.gamingmesh.jobs.container.Job;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

public final class JobsPrePaymentEvent extends BaseEvent implements Cancellable {
    private OfflinePlayer offlinePlayer;
    private double money;
    private double points;
    private Job job;
    private boolean cancelled = false;

    public JobsPrePaymentEvent(OfflinePlayer offlinePlayer, Job job, double money, double points) {
	this.job = job;
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

    public Job getJob() {
	return job;
    }

    public void setAmount(double money) {
	this.money = money;
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
}
