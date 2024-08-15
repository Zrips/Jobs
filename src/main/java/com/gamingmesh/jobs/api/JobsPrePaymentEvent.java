package com.gamingmesh.jobs.api;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;

import java.util.HashMap;
import java.util.Map;

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
    Map<CurrencyType, Double> amounts = new HashMap<>();
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
        amounts.put(CurrencyType.MONEY, money);
        amounts.put(CurrencyType.POINTS, points);
    }

    @Deprecated
    public JobsPrePaymentEvent(OfflinePlayer offlinePlayer, Job job, double money, double points, Block block, Entity entity, LivingEntity living, ActionInfo info) {
        this(offlinePlayer, job, money, 0, points, block, entity, living, info);
    }

    public JobsPrePaymentEvent(OfflinePlayer offlinePlayer, Job job, double money, double exp, double points, Block block, Entity entity, LivingEntity living, ActionInfo info) {
        this.job = job;
        this.offlinePlayer = offlinePlayer;
        amounts.put(CurrencyType.MONEY, money);
        amounts.put(CurrencyType.EXP, exp);
        amounts.put(CurrencyType.POINTS, points);
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
        return amounts.getOrDefault(CurrencyType.MONEY, 0D);
    }

    /**
     * Returns the amount of expected points.
     * 
     * @return expected points before calculations
     */
    public double getPoints() {
        return amounts.getOrDefault(CurrencyType.POINTS, 0D);
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
        amounts.put(CurrencyType.MONEY, money);
    }

    /**
     * Sets a new points amount before calculations.
     * 
     * @param points
     */
    public void setPoints(double points) {
        amounts.put(CurrencyType.POINTS, points);
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

    /**
     * Returns the amount of expected exp.
     * 
     * @return expected exp before calculations
     */
    public double getExp() {
        return amounts.getOrDefault(CurrencyType.EXP, 0D);
    }

    /**
     * Sets a new exp amount before calculations.
     * 
     * @param exp
     */
    public void setExp(double exp) {
        amounts.put(CurrencyType.EXP, exp);
    }
}
