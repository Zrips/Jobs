package com.gamingmesh.jobs.api;

import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public final class JobsLeaveEvent extends BaseEvent implements Cancellable {

	private JobsPlayer player;
	private Job job;
	private boolean cancelled = false;

	public JobsLeaveEvent(JobsPlayer jPlayer, Job job) {
		this.player = jPlayer;
		this.job = job;
	}

	/**
	 * Returns the player who left a job.
	 * 
	 * @return {@link JobsPlayer}
	 */
	public JobsPlayer getPlayer() {
		return player;
	}

	/**
	 * Returns the job where the player left.
	 * 
	 * @return {@link Job}
	 */
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
}