package com.gamingmesh.jobs.api;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.Job;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

public final class JobsPrePaymentEvent extends BaseEvent implements Cancellable {
    private OfflinePlayer offlinePlayer;
    private double money;
    private double points;
    private Job job;
    private Block block;
    private Entity entity;
    private LivingEntity living;
    private ActionInfo info;
    private boolean cancelled = false;

    @Deprecated
    public JobsPrePaymentEvent(OfflinePlayer offlinePlayer, Job job, double money, double points) {
	this.job = job;
	this.offlinePlayer = offlinePlayer;
	this.money = money;
	this.points = points;
    }

    public JobsPrePaymentEvent(OfflinePlayer offlinePlayer, Job job, double money, double points, Block block,
    	Entity entity, LivingEntity living, ActionInfo info) {
	this.job = job;
	this.offlinePlayer = offlinePlayer;
	this.money = money;
	this.points = points;
	this.block = block;
	this.entity = entity;
	this.living = living;
	this.info = info;
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
