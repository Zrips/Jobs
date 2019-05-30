package com.gamingmesh.jobs.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.Job;

public final class JobsExpGainEvent extends BaseEvent implements Cancellable {
    private OfflinePlayer offlinePlayer;
    private double exp;
    private Job job;
    private boolean cancelled = false;

    public JobsExpGainEvent(OfflinePlayer offlinePlayer, Job job, double exp) {
	this.offlinePlayer = offlinePlayer;
	this.job = job;
	this.exp = exp;
    }

    public OfflinePlayer getPlayer() {
	return offlinePlayer;
    }

    public Job getJob() {
	return job;
    }

    public double getExp() {
	return exp;
    }

    public void setExp(double exp) {
	this.exp = exp;
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