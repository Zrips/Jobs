package com.gamingmesh.jobs.stuff;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class blockLoc {
    private int x;
    private int y;
    private int z;
    private String worldName;
    private World w;

    public blockLoc(String loc) {
	this.fromString(loc);
    }

    public blockLoc(Location loc) {
	x = loc.getBlockX();
	y = loc.getBlockY();
	z = loc.getBlockZ();
	w = loc.getWorld();
	worldName = loc.getWorld().getName();
    }

    public int getX() {
	return x;
    }

    public void setX(int x) {
	this.x = x;
    }

    public int getY() {
	return y;
    }

    public void setY(int y) {
	this.y = y;
    }

    public int getZ() {
	return z;
    }

    public void setZ(int z) {
	this.z = z;
    }

    public String getWorldName() {
	return worldName;
    }

    public void setWorldName(String worldName) {
	this.worldName = worldName;
    }

    @Override
    public String toString() {
	return (w == null ? worldName : w.getName()) + ":" + x + ":" + y + ":" + z;
    }

    public boolean fromString(String loc) {
	if (!loc.contains(":"))
	    return false;
	String[] split = loc.split(":");

	World w = Bukkit.getWorld(split[0]);
	if (w == null)
	    return false;
	this.w = w;

	try {
	    x = Integer.parseInt(split[1]);
	    y = Integer.parseInt(split[2]);
	    z = Integer.parseInt(split[3]);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    public Block getBlock() {
	Location loc = this.getLocation();

	if (loc == null)
	    return null;

	return loc.getBlock();
    }

    public Location getLocation() {
	World w = this.w == null ? Bukkit.getWorld(worldName) : this.w;
	if (w == null)
	    return null;
	this.w = w;

	Location loc = new Location(w, x, y, z);

	return loc;
    }
}
