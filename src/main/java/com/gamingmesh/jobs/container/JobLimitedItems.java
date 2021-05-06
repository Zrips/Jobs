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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIMaterial;

public class JobLimitedItems {
    private String node;
    CMIMaterial mat;
    private int amount;
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private int level;

    public JobLimitedItems(String node, CMIMaterial material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants, int level) {
	this.node = node;
	this.amount = amount;
	this.name = name;
	this.lore = lore;
	this.enchants = enchants;
	this.level = level;
	this.mat = material;
    }

    public String getNode() {
	return node;
    }

    public ItemStack getItemStack(Player player) {

	ItemStack item = mat.newItemStack();
	item.setAmount(amount);

	ItemMeta meta = item.getItemMeta();
	if (meta == null) {
	    return item;
	}

	Jobs plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class);

	if (name != null)
	    meta.setDisplayName(CMIChatColor.translate(name));

	if (lore != null && !lore.isEmpty()) {
	    List<String> translatedLore = new ArrayList<>();
	    for (String oneLore : lore) {
		translatedLore.add(CMIChatColor.translate(oneLore.replace("[player]", player.getName())));
	    }

	    meta.setLore(translatedLore);
	}

	if (enchants != null)
	    for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
		meta.addEnchant(oneEnchant.getKey(), oneEnchant.getValue(), true);
	    }

	item.setItemMeta(meta);
	return item;
    }

    @Deprecated
    public int getId() {
	return mat.getId();
    }

    public CMIMaterial getType(){
	return mat;
    }

    public String getName() {
	return name;
    }

    public List<String> getLore() {
	return lore;
    }

    public Map<Enchantment, Integer> getEnchants() {
	return enchants;
    }

    public int getLevel() {
	return level;
    }
}
