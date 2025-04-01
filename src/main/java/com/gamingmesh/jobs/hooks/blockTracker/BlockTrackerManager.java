package com.gamingmesh.jobs.hooks.blockTracker;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class BlockTrackerManager {
    private final Method isTrackedMethod;
    public BlockTrackerManager() {
        try {
            Class<? extends Plugin> pluginClazz = (Class<? extends Plugin>) Class.forName("dev.krakenied.blocktracker.bukkit.BukkitBlockTrackerPlugin");
            this.isTrackedMethod = pluginClazz.getMethod("isTracked", Block.class);
        } catch (ClassCastException | ClassNotFoundException | NoSuchMethodException ignored) {
            throw new IllegalStateException("BlockTracker plugin not found");
        }
    }

    public boolean isTracked(Block block) {
        try {
            return (boolean) this.isTrackedMethod.invoke(null, block);
        } catch (Throwable e) {
            return false;
        }
    }
}
