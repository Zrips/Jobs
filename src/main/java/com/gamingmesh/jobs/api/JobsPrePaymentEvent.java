package com.gamingmesh.jobs.api;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.Job;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

/**
 * Event fired, before the payment calculation process should beginning.
 */
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

    /**
     * Returns the player who performed something in a job.
     * 
     * @return {@link OfflinePlayer}
     */
    public OfflinePlayer getPlayer() {
	return offlinePlayer;
    }

    /**
     * Returns the amount of expected income.
     * 
     * @return expected income before calculations
     */
    public double getAmount() {
	return money;
    }

    /**
     * Returns the amount of expected points.
     * 
     * @return expected points before calculations
     */
    public double getPoints() {
	return points;
    }

    /**
     * Returns the job where the payment will calculate.
     * 
     * @return {@link Job}
     */
    public Job getJob() {
	return job;
    }

    /**
     * Sets a new money amount before calculations.
     * 
     * @param money new amount
     */
    public void setAmount(double money) {
	this.money = money;
    }

    /**
     * Sets a new points amount before calculations.
     * 
     * @param points
     */
    public void setPoints(double points) {
	this.points = points;
    }

    /**
     * Returns the block which the player performed to break.
     * 
     * @return {@link Block}
     */
    public Block getBlock() {
	return block;
    }

    /**
     * Returns the entity that the player killed or did something before.
     * <p>
     * This method is used for Citizens NPCs and armor stand breaking.
     * 
     * @return {@link Entity}
     */
    public Entity getEntity() {
	return entity;
    }

    /**
     * Returns the living entity that the player killed.
     * 
     * @return {@link LivingEntity}
     */
    public LivingEntity getLivingEntity() {
	return living;
    }

    /**
     * Returns the action info, containing the action which the player performed.
     * 
     * @return {@link ActionInfo}
     */
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
