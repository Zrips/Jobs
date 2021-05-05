package com.gamingmesh.jobs.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.Job;

/**
 * Called when a player gains experience from specific jobs.
 */
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

    /**
     * Returns the player who got experience.
     * 
     * @return {@link OfflinePlayer}
     */
    public OfflinePlayer getPlayer() {
	return offlinePlayer;
    }

    /**
     * Returns the job where the player got the experience from.
     * 
     * @return {@link Job}
     */
    public Job getJob() {
	return job;
    }

    /**
     * Returns the amount of gained experience for player.
     * 
     * @return the amount of experience the player got
     */
    public double getExp() {
	return exp;
    }

    /**
     * Sets the experience to a new value.
     * 
     * @param exp the new value
     */
    public void setExp(double exp) {
	this.exp = exp;
    }

    /**
     * Returns the block which the player broken and got income.
     * 
     * @return {@link Block}
     */
    public Block getBlock() {
	return block;
    }

    /**
     * Returns the entity that the player killed or did something before.
     * <p>
     * This method is used for Citizens NPCs and armour stand breaking.
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