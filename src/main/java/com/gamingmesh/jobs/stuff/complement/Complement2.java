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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Complement2 implements Complement {

	protected String serialize(Component component) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	protected TextComponent deserialize(String t) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(t);
	}

	@Override
	public String getDisplayName(ItemMeta meta) {
		Component dName = null;

		try {
			dName = meta.displayName();
		} catch (NoSuchMethodError e) {
		}

		return dName == null ? "" : serialize(dName);
	}

	@Override
	public String getLine(SignChangeEvent event, int line) {
		Component l = event.line(line);
		return l == null ? "" : serialize(l);
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

		if (meta.hasLore()) {
			for (Component comp : meta.lore()) {
				lore.add(serialize(comp));
			}
		}

		return lore;
	}
}
