package com.gamingmesh.jobs.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public final class JobsJoinEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private JobsPlayer player;
	private Job job;
	private boolean cancelled;

	public JobsJoinEvent(JobsPlayer jPlayer, Job job) {
		this.player = jPlayer;
		this.job = job;
	}

	public JobsPlayer getPlayer() {
		return this.player;
	}

	public Job getJob() {
		return this.job;
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