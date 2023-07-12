package com.gamingmesh.jobs.container;

import org.bukkit.util.Vector;

import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class BlockProtection {

    private static long pre = (int) (System.currentTimeMillis() / 10000000000L) * 10000000000L;

    private int id = -1;
    private CMITask scheduler = null;
    private int time = -1;
    private int recorded = -1;
    private DBAction action;
    private boolean paid = false;
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

    public long getTime() {
        return deconvert(time);
    }

    private static int convert(long time) {
        return time == -1L ? -1 : (int) ((time - pre) / 1000L);
    }

    private static long deconvert(int time) {
        return time == -1 ? -1 : (time * 1000L) + pre;
    }

    public void setTime(long time) {
        this.time = time == -1 ? -1 : convert(time);
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

    public long getRecorded() {
        return deconvert(recorded);
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void setRecorded(long recorded) {
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

    public CMITask getScheduler() {
        return scheduler;
    }

    public void setScheduler(CMITask cmiTask) {
        this.scheduler = cmiTask;
    }
}
