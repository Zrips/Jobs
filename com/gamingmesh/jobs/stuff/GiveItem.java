package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveItem {
	public static boolean GiveItemForPlayer(Player player, int id, int meta, int qty, String name, List<String> lore, List<String> enchants) {
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
		if (enchants != null)
			for (String OneEnchant : enchants) {
				ItemMeta.addEnchant(Enchantment.getByName(OneEnchant.split("=")[0]), Integer.parseInt(OneEnchant.split("=")[1]), true);
			}
		if (name != null)
			ItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		itemStack.setItemMeta(ItemMeta);
		player.getInventory().addItem(itemStack);
		player.getPlayer().updateInventory();
		return true;
	}
}
