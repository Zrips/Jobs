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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;

public class JobItems {
    private String node;
    private int id;
    private int data;
    private int amount;
    private String name;
    private List<String> lore;
    private HashMap<Enchantment, Integer> enchants;
    private BoostMultiplier boostMultiplier = new BoostMultiplier();

    public JobItems(String node, int id, int data, int amount, String name, List<String> lore, HashMap<Enchantment, Integer> enchants, BoostMultiplier boostMultiplier) {
	this.node = node;
	this.id = id;
	this.data = data;
	this.amount = amount;
	this.name = name;
	this.lore = lore;
	this.enchants = enchants;
	this.boostMultiplier = boostMultiplier;
    }

    public String getNode() {
	return this.node;
    }

    public ItemStack getItemStack(Player player) {
	return getItemStack(player, null);
    }

    public ItemStack getItemStack(Player player, Job job) {
	try {
	    ItemStack item = new ItemStack(Material.getMaterial(id), amount, (short) data);
	    ItemMeta meta = item.getItemMeta();
	    if (this.name != null)
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	    if (lore != null) {
		List<String> TranslatedLore = new ArrayList<String>();
		for (String oneLore : lore) {
		    TranslatedLore.add(ChatColor.translateAlternateColorCodes('&', oneLore.replace("[player]", player.getName())));
		}
		meta.setLore(TranslatedLore);
	    }
	    if (enchants != null)
		for (Entry<Enchantment, Integer> OneEnchant : enchants.entrySet()) {
		    meta.addEnchant(OneEnchant.getKey(), OneEnchant.getValue(), true);
		}
	    item.setItemMeta(meta);

	    if (job != null)
		item = Jobs.getReflections().setNbt(item, "JobsItemBoost", job.getName(), node);
	    
	    return item;
	} catch (Exception e) {

	}
	return null;
    }

    public int getId() {
	return this.id;
    }

    public int getData() {
	return this.data;
    }

    public int getAmount() {
	return this.amount;
    }

    public String getName() {
	return this.name;
    }

    public List<String> getLore() {
	return this.lore;
    }

    public HashMap<Enchantment, Integer> getEnchants() {
	return this.enchants;
    }

    public BoostMultiplier getBoost() {
	return this.boostMultiplier.clone();
    }
}
