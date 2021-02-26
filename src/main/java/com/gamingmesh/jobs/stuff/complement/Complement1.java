package com.gamingmesh.jobs.stuff.complement;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public final class Complement1 implements Complement {

	@Override
	public String getDisplayName(ItemMeta meta) {
		return meta.getDisplayName();
	}

	@Override
	public String getLine(SignChangeEvent event, int line) {
		return event.getLine(line);
	}

	@Override
	public void setLine(SignChangeEvent event, int line, String text) {
		event.setLine(line, text);
	}

	@Override
	public String getLine(Sign sign, int line) {
		return sign.getLine(line);
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, int size, String title) {
		return Bukkit.createInventory(owner, size, title);
	}

	@Override
	public void setLore(ItemMeta meta, List<String> lore) {
		meta.setLore(lore);
	}

	@Override
	public void setDisplayName(ItemMeta meta, String name) {
		meta.setDisplayName(name);
	}

	@Override
	public String getDisplayName(Player player) {
		return player.getDisplayName();
	}

	@Override
	public void setLine(Sign sign, int line, String text) {
		sign.setLine(line, text);
	}

	@Override
	public List<String> getLore(ItemMeta meta) {
		return meta.getLore();
	}
}
