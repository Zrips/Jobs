package com.gamingmesh.jobs.container;

import java.util.UUID;

public final class TopList {

    private int level;
    private int exp;
    private UUID uuid;

    public TopList(UUID uuid, int level, int exp) {
        this.uuid = uuid;
        this.level = level;
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}