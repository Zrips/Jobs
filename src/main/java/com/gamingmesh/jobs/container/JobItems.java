/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;

public class JobItems {
    private String node;
    ItemStack item;
    private BoostMultiplier boostMultiplier = new BoostMultiplier();
    private List<Job> jobs = new ArrayList<Job>();

    public JobItems(String node, CMIMaterial mat, int amount, String name, List<String> lore, HashMap<Enchantment, Integer> enchants, BoostMultiplier boostMultiplier, List<Job> jobs) {

	try {
	    item = mat.newItemStack();
	    item.setAmount(amount);
	    ItemMeta meta = item.getItemMeta();
	    if (name != null)
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	    if (lore != null && !lore.isEmpty()) {
		meta.setLore(lore);
	    }
	    if (enchants != null)
		if (mat == CMIMaterial.ENCHANTED_BOOK) {
		    EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
		    for (Entry<Enchantment, Integer> oneEnch : bookMeta.getEnchants().entrySet()) {
			bookMeta.addStoredEnchant(oneEnch.getKey(), oneEnch.getValue(), true);
		    }
		} else
		    for (Entry<Enchantment, Integer> OneEnchant : enchants.entrySet()) {
			meta.addEnchant(OneEnchant.getKey(), OneEnchant.getValue(), true);
		    }
	    item.setItemMeta(meta);
	    item = Jobs.getReflections().setNbt(item, "JobsItemBoost", node);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	this.node = node;
	this.boostMultiplier = boostMultiplier;
	this.jobs = jobs;
    }

    public String getNode() {
	return node;
    }

    public ItemStack getItemStack(Player player) {
	if (player == null)
	    return this.item;
	try {
	    ItemStack item = this.item.clone();

	    ItemMeta meta = item.getItemMeta();
	    if (meta.hasDisplayName())
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName().replace("[player]", player == null ? "[player]" : player.getName())));
	    if (meta.hasLore()) {
		List<String> TranslatedLore = new ArrayList<>();
		for (String oneLore : meta.getLore()) {
		    TranslatedLore.add(ChatColor.translateAlternateColorCodes('&', oneLore.replace("[player]", player == null ? "[player]" : player.getName())));
		}
		meta.setLore(TranslatedLore);
	    }
	    item.setItemMeta(meta);
	    return item;
	} catch (Exception e) {

	}
	return null;
    }

    public BoostMultiplier getBoost() {
	return boostMultiplier.clone();
    }

    public List<Job> getJobs() {
	return jobs;
    }

    public void setJobs(List<Job> jobs) {
	this.jobs = jobs;
    }
}
