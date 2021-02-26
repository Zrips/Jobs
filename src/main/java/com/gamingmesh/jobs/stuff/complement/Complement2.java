package com.gamingmesh.jobs.stuff.complement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public final class Complement2 implements Complement {

	private String serialize(Component component) {
		return PlainComponentSerializer.plain().serialize(component);
	}

	private Component deserialize(String t) {
		return PlainComponentSerializer.plain().deserialize(t);
	}

	@Override
	public String getDisplayName(ItemMeta meta) {
		return serialize(meta.displayName());
	}

	@Override
	public String getLine(SignChangeEvent event, int line) {
		return serialize(event.line(line));
	}

	@Override
	public void setLine(SignChangeEvent event, int line, String text) {
		event.line(line, deserialize(text));
	}

	@Override
	public String getLine(Sign sign, int line) {
		return serialize(sign.line(line));
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, int size, String title) {
		return Bukkit.createInventory(owner, size, deserialize(title));
	}

	@Override
	public void setLore(ItemMeta meta, List<String> lore) {
		List<Component> l = new ArrayList<>();

		for (String e : lore) {
			l.add(deserialize(e));
		}

		meta.lore(l);
	}

	@Override
	public void setDisplayName(ItemMeta meta, String name) {
		meta.displayName(deserialize(name));
	}

	@Override
	public String getDisplayName(Player player) {
		return serialize(player.displayName());
	}

	@Override
	public void setLine(Sign sign, int line, String text) {
		sign.line(line, deserialize(text));
	}

	@Override
	public List<String> getLore(ItemMeta meta) {
		List<String> lore = new ArrayList<>();

		for (Component comp : meta.lore()) {
			lore.add(serialize(comp));
		}

		return lore;
	}
}
