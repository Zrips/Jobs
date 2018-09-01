package com.gamingmesh.jobs.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public final class JobsLeaveEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private JobsPlayer player;
	private Job job;
	private boolean cancelled = false;

	public JobsLeaveEvent(JobsPlayer jPlayer, Job job) {
		this.player = jPlayer;
		this.job = job;
	}

	public JobsPlayer getPlayer() {
		return player;
	}

	public Job getJob() {
		return job;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}