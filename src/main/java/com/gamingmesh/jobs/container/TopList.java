package com.gamingmesh.jobs.container;

public final class TopList {
    private int level;
    private int exp;
    private PlayerInfo info;

    public TopList(PlayerInfo info, int level, int exp) {
	this.info = info;
	this.level = level;
	this.exp = exp;
    }

    public String getPlayerName() {
	return info.getName();
    }

    public int getLevel() {
	return level;
    }

    public int getExp() {
	return exp;
    }
}