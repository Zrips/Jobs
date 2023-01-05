package com.gamingmesh.jobs;

public class permissionInfo {

    private long time = 0L;
    private boolean state = false;
    private double value = 0D;

    public long getTime() {
        return time;
    }

    public permissionInfo setTime(long time) {
        this.time = time;
        return this;
    }

    public boolean getState() {
        return state;
    }

    public permissionInfo setState(boolean state) {
        this.state = state;
        return this;
    }

    public double getValue() {
        return value;
    }

    public permissionInfo setValue(double value) {
        this.value = value;
        return this;
    }

}
