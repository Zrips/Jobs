package com.gamingmesh.jobs;

public class JobsPermissionInfo {

    private long time = 0L;
    private boolean state = false;
    private double value = 0D;

    public long getTime() {
        return time;
    }

    public JobsPermissionInfo setTime(long time) {
        this.time = time;
        return this;
    }

    public boolean getState() {
        return state;
    }

    public JobsPermissionInfo setState(boolean state) {
        this.state = state;
        return this;
    }

    public double getValue() {
        return value;
    }

    public JobsPermissionInfo setValue(double value) {
        this.value = value;
        return this;
    }

}
