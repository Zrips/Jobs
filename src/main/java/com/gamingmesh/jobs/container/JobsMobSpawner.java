package com.gamingmesh.jobs.container;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import com.gamingmesh.jobs.Jobs;

import net.Zrips.CMILib.PersistentData.CMIPersistentDataContainer;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class JobsMobSpawner {

    private static final String MOBSPAWNERMETADATA = "JMS";

    /**
    * @return the cached mob spawner meta name
    */
    public static String getMobSpawnerMetadata() {
        return MOBSPAWNERMETADATA;
    }

    public static boolean invalidForPaymentSpawnerMob(Entity entity) {
        return invalidForPaymentSpawnerMob(entity, false);
    }

    public static boolean invalidForPaymentSpawnerMob(Entity entity, boolean delay) {
        if (Jobs.getGCManager().payNearSpawner())
            return false;

        if (!isSpawnerEntity(entity))
            return false;

        if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
            if (delay)
                CMIScheduler.runTaskLater(Jobs.getInstance(), () -> removeSpawnerMeta(entity), 200L);
            else
                removeSpawnerMeta(entity);
            return true;

        }

        if (delay)
            CMIScheduler.runTaskLater(Jobs.getInstance(), () -> removeSpawnerMeta(entity), 200L);
        else
            removeSpawnerMeta(entity);
        return true;
    }

    public static boolean isSpawnerEntity(Entity entity) {
        if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
            return CMIPersistentDataContainer.get(entity).hasKey(getMobSpawnerMetadata());
        }
        return entity.hasMetadata(getMobSpawnerMetadata());
    }

    public static void removeSpawnerMeta(Entity entity) {
        if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
            CMIPersistentDataContainer.get(entity).remove(getMobSpawnerMetadata());
        } else {
            entity.removeMetadata(getMobSpawnerMetadata(), Jobs.getInstance());
        }
    }

    public static void setSpawnerMeta(Entity entity) {
        if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
            CMIPersistentDataContainer.get(entity).set(getMobSpawnerMetadata(), true);
        } else {
            entity.setMetadata(getMobSpawnerMetadata(), new FixedMetadataValue(Jobs.getInstance(), true));
        }
    }
}
