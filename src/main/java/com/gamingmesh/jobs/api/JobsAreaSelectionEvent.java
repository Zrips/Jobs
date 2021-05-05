package com.gamingmesh.jobs.api;

import org.bukkit.entity.Player;

import com.gamingmesh.jobs.container.CuboidArea;

/**
 * Called when a player attempted to select an area.
 */
public final class JobsAreaSelectionEvent extends BaseEvent {

    private CuboidArea area;
    private Player player;

    public JobsAreaSelectionEvent(Player player, CuboidArea area) {
	this.player = player;
	this.area = area;
    }

    /**
     * The player who selected an area.
     * 
     * @return {@link Player}
     */
    public Player getPlayer() {
	return player;
    }

    /**
     * Gets the selected area.
     * 
     * @return {@link CuboidArea}
     */
    public CuboidArea getArea() {
	return area;
    }
}