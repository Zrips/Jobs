package com.gamingmesh.jobs.stuff.complement;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.CMILib.CMIChatColor;

public interface Complement {

    String getDisplayName(ItemMeta meta);

    String getDisplayName(Player player);

    String getLine(SignChangeEvent event, int line);

    String getLine(Sign sign, int line);

    void setLine(SignChangeEvent event, int line, String text);

    void setLine(Sign sign, int line, String text);

    org.bukkit.inventory.Inventory createInventory(InventoryHolder owner, int size, String title);

    void setLore(ItemMeta meta, List<String> lore);

    List<String> getLore(ItemMeta meta);

    void setDisplayName(ItemMeta meta, String name);

    default void broadcastMessage(String message) {
	message = CMIChatColor.translate(message);
	for (Player player : Bukkit.getOnlinePlayers()) {
	    player.sendMessage(message);
	}
    }

    default void broadcastMessage(List<String> messages) {
	for (String msg : messages) {
	    msg = CMIChatColor.translate(msg);
	    for (Player player : Bukkit.getOnlinePlayers()) {
		player.sendMessage(msg);
	    }
	}
    }
}
