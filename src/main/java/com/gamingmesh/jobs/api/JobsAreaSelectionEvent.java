package com.gamingmesh.jobs.api;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.container.CuboidArea;

public final class JobsAreaSelectionEvent extends BaseEvent {
    private CuboidArea area;
    private Player player;

    public JobsAreaSelectionEvent(Player player, CuboidArea area) {
	this.player = player;
	this.area = area;
    }

    public Player getPlayer() {
	return player;
    }

    public CuboidArea getArea() {
	return area;
    }
}