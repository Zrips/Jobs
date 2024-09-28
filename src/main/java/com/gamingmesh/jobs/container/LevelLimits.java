package com.gamingmesh.jobs.container;

public class LevelLimits {

    private int fromLevel = 0;
    private int untilLevel = Integer.MAX_VALUE;

    public LevelLimits(int fromLevel, int untilLevel) {
        this.fromLevel = fromLevel;
        this.untilLevel = untilLevel;
    }

    public int getFromLevel() {
        return fromLevel;
    }

    public void setFromLevel(int fromLevel) {
        this.fromLevel = fromLevel;
    }

    public int getUntilLevel() {
        return untilLevel;
    }

    public void setUntilLevel(int fromLevel) {
        this.untilLevel = fromLevel;
    }

}
