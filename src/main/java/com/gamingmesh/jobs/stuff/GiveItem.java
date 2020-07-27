package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIMaterial;

public class GiveItem {
    public static void GiveItemForPlayer(Player player, int id, int meta, int qty, String name, List<String> lore,
	    HashMap<Enchantment, Integer> enchants) {
	ItemStack itemStack = CMIMaterial.get(id, meta).newItemStack();
	itemStack.setAmount(qty);
	ItemMeta ItemMeta = itemStack.getItemMeta();

	if (lore != null && !lore.isEmpty()) {
	    List<String> TranslatedLore = new ArrayList<>();
	    for (String oneLore : lore) {
		TranslatedLore.add(CMIChatColor.translate(oneLore.replace("[player]", player.getName())));
	    }

	    ItemMeta.setLore(TranslatedLore);
	}

	if (enchants != null) {
	    if (itemStack.getType() == CMIMaterial.ENCHANTED_BOOK.getMaterial()) {
		EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) ItemMeta;
		for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
		    bookMeta.addStoredEnchant(oneEnch.getKey(), oneEnch.getValue(), true);
		}
	    } else {
		for (Entry<Enchantment, Integer> OneEnchant : enchants.entrySet()) {
		    ItemMeta.addEnchant(OneEnchant.getKey(), OneEnchant.getValue(), true);
		}
	    }
	}

	if (name != null)
	    ItemMeta.setDisplayName(CMIChatColor.translate(name));

	itemStack.setItemMeta(ItemMeta);
	GiveItemForPlayer(player, itemStack);
    }

    public static void GiveItemForPlayer(Player player, ItemStack item) {
	player.getInventory().addItem(item);
	player.updateInventory();
    }
}
