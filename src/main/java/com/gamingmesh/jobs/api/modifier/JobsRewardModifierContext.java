package com.gamingmesh.jobs.api.modifier;

import com.gamingmesh.jobs.container.ActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class JobsRewardModifierContext {

    private final OfflinePlayer player;
    private final Job job;
    private final Map<CurrencyType, Double> rewards;
    private final Block block;
    private final Entity entity;
    private final LivingEntity livingEntity;
    private final ActionInfo actionInfo;
    private final ActionType actionType;
    private final String actionName;
    private final Location actionLocation;
    private final Material material;
    private final String entityKey;

    public JobsRewardModifierContext(OfflinePlayer player, Job job, Map<CurrencyType, Double> rewards, Block block, Entity entity,
            LivingEntity livingEntity, ActionInfo actionInfo) {
        this(player, job, rewards, block, entity, livingEntity, actionInfo, null, null, null, null, null);
    }

    public JobsRewardModifierContext(OfflinePlayer player, Job job, Map<CurrencyType, Double> rewards, Block block, Entity entity,
            LivingEntity livingEntity, ActionInfo actionInfo, Location actionLocation, Material material, String entityKey) {
        this(player, job, rewards, block, entity, livingEntity, actionInfo, null, null, actionLocation, material, entityKey);
    }

    public JobsRewardModifierContext(OfflinePlayer player, Job job, Map<CurrencyType, Double> rewards, Block block, Entity entity,
            LivingEntity livingEntity, ActionInfo actionInfo, ActionType actionType, String actionName, Location actionLocation, Material material, String entityKey) {
        this.player = player;
        this.job = job;
        this.rewards = Collections.unmodifiableMap(new LinkedHashMap<>(rewards));
        this.block = block;
        this.entity = entity;
        this.livingEntity = livingEntity;
        this.actionInfo = actionInfo;
        this.actionType = actionType == null && actionInfo != null ? actionInfo.getType() : actionType;
        this.actionName = actionName;
        this.actionLocation = actionLocation;
        this.material = material;
        this.entityKey = entityKey;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public Job getJob() {
        return job;
    }

    public Map<CurrencyType, Double> getRewards() {
        return rewards;
    }

    public double getReward(CurrencyType type) {
        return rewards.getOrDefault(type, 0D);
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public ActionInfo getActionInfo() {
        return actionInfo;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getActionName() {
        if (actionName != null)
            return actionName;
        return actionType == null ? "ANY" : actionType.name();
    }

    public Location getActionLocation() {
        if (actionLocation != null)
            return actionLocation;
        if (block != null)
            return block.getLocation();

        Entity target = livingEntity != null ? livingEntity : entity;
        return target == null ? null : target.getLocation();
    }

    public Material getMaterial() {
        if (material != null)
            return material;
        return block == null ? null : block.getType();
    }

    public String getEntityKey() {
        if (entityKey != null)
            return entityKey;
        Entity target = livingEntity != null ? livingEntity : entity;
        return target == null ? null : target.getType().name();
    }
}
