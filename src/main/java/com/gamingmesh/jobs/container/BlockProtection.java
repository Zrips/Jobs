package com.gamingmesh.jobs.container;

import org.bukkit.util.Vector;

public class BlockProtection {

    private static long pre = (int) (System.currentTimeMillis() / 10000000000L) * 10000000000L;

    private int id;
    private Integer time;
    private Integer recorded;
    private DBAction action;
    private Boolean paid;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    public BlockProtection(Vector pos) {
	this(DBAction.INSERT, pos);
    }

    @Deprecated
    public BlockProtection(DBAction action, Vector pos) {
	this(action, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    public BlockProtection(DBAction action, int x, int y, int z) {
	this.action = action;
	if (action == DBAction.NONE)
	    action = null;
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public Long getTime() {
	return deconvert(time);
    }

    private static int convert(long time) {
	if (time == -1L)
	    return -1;
	return (int) ((time - pre) / 1000L);
    }

    private static Long deconvert(Integer time) {
	return time == null ? -1L : ((time.longValue() * 1000L) + pre);
    }

    public void setTime(Long time) {
	if (time == -1)
	    this.time = null;
	else
	    this.time = convert(time);
	this.recorded = convert(System.currentTimeMillis());
    }

    public DBAction getAction() {
	return action == null ? DBAction.NONE : action;
    }

    public void setAction(DBAction action) {
	if (action == DBAction.NONE)
	    action = null;
	this.action = action;
    }

    public Long getRecorded() {
	return deconvert(recorded);
    }

    public Boolean isPaid() {
	return paid == null ? true : paid;
    }

    public void setPaid(Boolean paid) {
	if (!paid)
	    this.paid = paid;
	else
	    this.paid = null;
    }

    public void setRecorded(Long recorded) {
	this.recorded = convert(recorded);
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    @Deprecated
    public Vector getPos() {
	return new Vector(x, y, z);
    }

    @Deprecated
    public void setPos(Vector pos) {
	x = pos.getBlockX();
	y = pos.getBlockY();
	z = pos.getBlockZ();
    }

    public void setPos(int x, int y, int z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public int getZ() {
	return z;
    }
}
