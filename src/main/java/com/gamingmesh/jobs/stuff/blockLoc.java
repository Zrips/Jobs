package com.gamingmesh.jobs.stuff;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import net.Zrips.CMILib.Container.CMIWorld;

public class blockLoc {
    private int x;
    private int y;
    private int z;
    private World w;
    private String worldName = null;
    private boolean disabled = false;

    public blockLoc(String loc) {
        fromString(loc);
    }

    public blockLoc(Location loc) {
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        w = loc.getWorld();
    }

    public String getWorldName() {
        return getWorld() != null ? getWorld().getName() : worldName != null ? worldName : "__";
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return getWorldName() + ":" + x + ":" + y + ":" + z;
    }

    public String toVectorString() {
        return x + ":" + y + ":" + z;
    }

    public boolean fromString(String loc) {
        String[] split = loc.split(":", 4);
        if (split.length == 0) {
            return false;
        }

        World w = CMIWorld.getWorld(split[0]);
        if (w != null)
            this.w = w;
        else
            this.worldName = split[0];

        if (split.length < 4) {
            return false;
        }

        try {
            x = Integer.parseInt(split[1]);
            y = Integer.parseInt(split[2]);
            z = Integer.parseInt(split[3]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public @Nullable Block getBlock() {
        Location loc = getLocation();
        return loc == null ? null : loc.getBlock();
    }

    public @Nullable Location getLocation() {
        if (getWorldName() == null && w == null)
            return null;

        // Make sure cached world is loaded
        World w = this.w == null ? Bukkit.getWorld(getWorldName()) : Bukkit.getWorld(this.w.getName());
        if (w == null)
            return null;

        this.w = w;

        return new Location(w, x, y, z);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public @Nullable World getWorld() {
        if (w == null && worldName != null)
            w = CMIWorld.getWorld(worldName);
        return w;
    }

    public void setWorld(World world) {
        this.w = world;
    }
}
