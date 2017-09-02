package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveItem {
    public static boolean GiveItemForPlayer(Player player, int id, int meta, int qty, String name, List<String> lore, HashMap<Enchantment, Integer> hashMap) {
	@SuppressWarnings("deprecation")
	ItemStack itemStack = new ItemStack(Material.getMaterial(id), qty, (short) meta);
	ItemMeta ItemMeta = itemStack.getItemMeta();

	if (lore != null) {
	    List<String> TranslatedLore = new ArrayList<String>();
	    for (String oneLore : lore) {
		TranslatedLore.add(ChatColor.translateAlternateColorCodes('&', oneLore.replace("[player]", player.getName())));
	    }
	    ItemMeta.setLore(TranslatedLore);
	}
	for (Entry<Enchantment, Integer> OneEnchant : hashMap.entrySet()) {
	    ItemMeta.addEnchant(OneEnchant.getKey(), OneEnchant.getValue(), true);
	}
	if (name != null)
	    ItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	itemStack.setItemMeta(ItemMeta);
	player.getInventory().addItem(itemStack);
	player.getPlayer().updateInventory();
	return true;
    }

    public static boolean GiveItemForPlayer(Player player, ItemStack item) {
	player.getInventory().addItem(item);
	player.getPlayer().updateInventory();
	return true;
    }
}
