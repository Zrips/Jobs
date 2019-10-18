package com.gamingmesh.jobs.Gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

public class JobsInventoryHolder implements InventoryHolder {

	private Player player = null;

	public JobsInventoryHolder(Player player) {
		this.player = player;
	}

	@Override
	public Inventory getInventory() {
		return player.getOpenInventory().getTopInventory();
	}

	public InventoryView getOpenInventory() {
		return player.getOpenInventory();
	}

	public Player getPlayer() {
		return player;
	}
}
