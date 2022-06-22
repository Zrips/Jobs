package com.gamingmesh.jobs.Signs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.gamingmesh.jobs.container.Job;

import net.Zrips.CMILib.Container.CMIWorld;

public class jobsSign {

    private String worldName;

    private Integer x, y, z;
    private World world;
    private Location loc;

    private Integer number;
    private String jobName;
    private boolean special = false;
    private SignTopType type;

    public void setSpecial(boolean special) {
	this.special = special;
    }

    public boolean isSpecial() {
	return special;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    public String getJobName() {
	return jobName;
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

    public void setWorldName(String worldName) {
	this.worldName = worldName;
    }

    public String getWorldName() {
	return worldName;
    }

    public Location getLocation() {
	if (loc != null)
	    return loc;
	if (worldName == null)
	    return null;
	if ((world = Bukkit.getWorld(worldName)) == null)
	    return null;
	return loc = new Location(world, x, y, z);
    }

    public void setNumber(int number) {
	this.number = number;
    }

    public int getNumber() {
	return number;
    }

    public String locToBlockString() {
	return worldName + ";" + x + ";" + y + ";" + z;
    }

    public static String locToBlockString(Location loc) {
	return loc == null ? "" : loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    public void setLoc(Location loc) {
	if (loc == null)
	    return;
	this.worldName = loc.getWorld().getName();
	this.x = loc.getBlockX();
	this.y = loc.getBlockY();
	this.z = loc.getBlockZ();
	this.world = loc.getWorld();
	this.loc = loc;
    }

    public void setLoc(String string) {
	if (!string.contains(";"))
	    return;

	String[] split = string.replace(',', '.').split(";", 4);

	int x = 0, y = 0, z = 0;

	if (split.length > 0)
	    try {
		x = Integer.parseInt(split[1]);
	    } catch (NumberFormatException e) {
		return;
	    }

	if (split.length > 1)
	    try {
		y = Integer.parseInt(split[2]);
	    } catch (NumberFormatException e) {
		return;
	    }

	if (split.length > 2)
	    try {
		z = Integer.parseInt(split[3]);
	    } catch (NumberFormatException e) {
		return;
	    }

	World world = CMIWorld.getWorld(split[0]);
	if (world == null)
	    return;

	setLoc(new Location(world, x, y, z));
    }

    public SignTopType getType() {
	return type == null ? SignTopType.toplist : type;
    }

    public void setType(SignTopType type) {
	this.type = type;
    }

    public String getIdentifier() {
	SignTopType type = getType();

	if (type != SignTopType.toplist)
	    return type.toString();

	return jobName != null ? jobName + ":" + type.toString() : type.toString();
    }

    public static String getIdentifier(Job job, SignTopType type) {
	if (type != SignTopType.toplist)
	    return type.toString();
	return job != null ? job.getName() + ":" + type.toString() : type.toString();
    }
}
