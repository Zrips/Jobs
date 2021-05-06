package com.gamingmesh.jobs.stuff;

import java.util.ArrayList;
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

    public static void giveItemForPlayer(Player player, int id, int meta, int qty, String name, List<String> lore,
	    java.util.Map<Enchantment, Integer> enchants) {
	ItemStack itemStack = CMIMaterial.get(id, meta).newItemStack();
	itemStack.setAmount(qty);
	ItemMeta itemMeta = itemStack.getItemMeta();
	if (itemMeta == null) {
	    return;
	}

	if (lore != null && !lore.isEmpty()) {
	    List<String> translatedLore = new ArrayList<>();
	    for (String oneLore : lore) {
		translatedLore.add(CMIChatColor.translate(oneLore.replace("[player]", player.getName())));
	    }

	    itemMeta.setLore(translatedLore);
	}

	if (enchants != null) {
	    if (itemStack.getType() == CMIMaterial.ENCHANTED_BOOK.getMaterial()) {
		EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemMeta;
		for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
		    bookMeta.addStoredEnchant(oneEnch.getKey(), oneEnch.getValue(), true);
		}
	    } else {
		for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
		    itemMeta.addEnchant(oneEnchant.getKey(), oneEnchant.getValue(), true);
		}
	    }
	}

	if (name != null)
	    itemMeta.setDisplayName(CMIChatColor.translate(name));

	itemStack.setItemMeta(itemMeta);
	giveItemForPlayer(player, itemStack);
    }

    public static void giveItemForPlayer(Player player, ItemStack item) {
	player.getInventory().addItem(item);
	player.updateInventory();
    }
}
