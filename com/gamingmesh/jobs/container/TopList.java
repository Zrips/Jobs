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
	return this.info.getName();
    }

    public int getLevel() {
	return this.level;
    }

    public int getExp() {
	return this.exp;
    }
}