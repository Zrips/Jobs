package com.gamingmesh.jobs.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.Job;

public final class JobsExpGainEvent extends BaseEvent implements Cancellable {

    private OfflinePlayer offlinePlayer;
    private double exp;
    private Job job;
    private Block block;
    private Entity entity;
    private LivingEntity living;
    private ActionInfo info;

    private boolean cancelled = false;

    public JobsExpGainEvent(OfflinePlayer offlinePlayer, Job job, double exp) {
	this.offlinePlayer = offlinePlayer;
	this.job = job;
	this.exp = exp;
    }

    public JobsExpGainEvent(OfflinePlayer offlinePlayer, Job job, double exp, Block block,
		Entity entity, LivingEntity living, ActionInfo info) {
	this(offlinePlayer, job, exp);

	this.block = block;
	this.entity = entity;
	this.living = living;
	this.info = info;
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

    public Block getBlock() {
	return block;
    }

    public Entity getEntity() {
	return entity;
    }

    public LivingEntity getLivingEntity() {
	return living;
    }

    public ActionInfo getActionInfo() {
	return info;
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