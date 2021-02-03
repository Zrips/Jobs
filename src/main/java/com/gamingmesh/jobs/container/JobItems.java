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
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.CMILib.CMIReflections;
import com.gamingmesh.jobs.CMILib.Version;

@SuppressWarnings("deprecation")
public class JobItems {

    private String node;
    private String legacyKey;
    private ItemStack item;

    private Object potion;

    private final Map<Enchantment, Integer> enchants = new HashMap<>();
    private BoostMultiplier boostMultiplier = new BoostMultiplier();

    private final List<Job> jobs = new ArrayList<>();

    private int fromLevel = 0;
    private int untilLevel = Integer.MAX_VALUE;

    public JobItems(String node, CMIMaterial mat, int amount, String name, List<String> lore, HashMap<Enchantment, Integer> enchants, BoostMultiplier boostMultiplier, List<Job> jobs) {
	this(node, mat, amount, name, lore, enchants, boostMultiplier, jobs, null);
    }

    public JobItems(String node, CMIMaterial mat, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants, BoostMultiplier boostMultiplier, List<Job> jobs,
    Object potion) {
	if (mat == null) {
	    mat = CMIMaterial.STONE;
	}

	if (enchants != null) {
	    this.enchants.putAll(enchants);
	}

	this.node = node;

	if (boostMultiplier != null) {
	    this.boostMultiplier = boostMultiplier;
	}

	setJobs(jobs);

	ItemMeta meta = (item = mat.newItemStack()).getItemMeta();
	if (CMIMaterial.isPotion(mat.getMaterial()) && potion != null && meta instanceof PotionMeta) {
	    PotionMeta potionMeta = (PotionMeta) meta;

	    if (Version.isCurrentEqualOrHigher(Version.v1_10_R1) && potion instanceof org.bukkit.potion.PotionData) {
		potionMeta.setBasePotionData((org.bukkit.potion.PotionData) potion);
	    } else if (potion instanceof org.bukkit.potion.Potion) {
		PotionEffectType effectType = ((org.bukkit.potion.Potion) potion).getType().getEffectType();
		if (effectType != null) {
		    potionMeta.setMainEffect(effectType);
		}
	    }

	    meta = potionMeta;
	}

	if (meta != null) {
	    if (name != null)
		meta.setDisplayName(CMIChatColor.translate(name));

	    if (lore != null)
		meta.setLore(lore);

	    if (enchants != null) {
		if (mat == CMIMaterial.ENCHANTED_BOOK) {
		    EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
		    for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
			bookMeta.addStoredEnchant(oneEnch.getKey(), oneEnch.getValue(), true);
		    }
		} else {
		    for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
			meta.addEnchant(oneEnchant.getKey(), oneEnchant.getValue(), true);
		    }
		}
	    }

	    item.setItemMeta(meta);
	}

	item.setAmount(amount);
	item = CMIReflections.setNbt(item, "JobsItemBoost", node);
    }

    public String getNode() {
	return node;
    }

    public ItemStack getItemStack(Player player) {
	if (player == null)
	    return item;

	ItemStack item = this.item.clone();
	ItemMeta meta = item.getItemMeta();
	if (meta == null) {
	    return item;
	}

	if (CMIMaterial.isPotion(item.getType()) && potion != null && meta instanceof PotionMeta) {
	    PotionMeta potionMeta = (PotionMeta) meta;

	    if (Version.isCurrentEqualOrHigher(Version.v1_10_R1) && potion instanceof org.bukkit.potion.PotionData) {
		potionMeta.setBasePotionData((org.bukkit.potion.PotionData) potion);
	    } else if (potion instanceof org.bukkit.potion.Potion) {
		PotionEffectType effectType = ((org.bukkit.potion.Potion) potion).getType().getEffectType();
		if (effectType != null) {
		    potionMeta.setMainEffect(effectType);
		}
	    }

	    meta = potionMeta;
	}

	if (meta.hasDisplayName())
	    meta.setDisplayName(CMIChatColor.translate(meta.getDisplayName().replace("[player]", player.getName())));

	if (meta.hasLore()) {
	    List<String> translatedLore = new ArrayList<>();
	    for (String oneLore : meta.getLore()) {
		translatedLore.add(CMIChatColor.translate(oneLore.replace("[player]", player.getName())));
	    }
	    meta.setLore(translatedLore);
	}

	if (enchants != null) {
	    if (item.getType() == CMIMaterial.ENCHANTED_BOOK.getMaterial()) {
		EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
		for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
		    bookMeta.addStoredEnchant(oneEnch.getKey(), oneEnch.getValue(), true);
		}
	    } else {
		for (Entry<Enchantment, Integer> oneEnchant : enchants.entrySet()) {
		    meta.addEnchant(oneEnchant.getKey(), oneEnchant.getValue(), true);
		}
	    }
	}

	item.setItemMeta(meta);
	return item;
    }

    public BoostMultiplier getBoost() {
	return boostMultiplier.clone();
    }

    public BoostMultiplier getBoost(JobProgression job) {
	if (job == null || !jobs.contains(job.getJob()))
	    return new BoostMultiplier();
	if (job.getLevel() < getFromLevel() || job.getLevel() > getUntilLevel())
	    return new BoostMultiplier();
	return boostMultiplier.clone();
    }

    public List<Job> getJobs() {
	return jobs;
    }

    public void setJobs(List<Job> jobs) {
	this.jobs.clear();
	this.jobs.addAll(jobs);
    }

    public Map<Enchantment, Integer> getEnchants() {
	return enchants;
    }

    public int getFromLevel() {
	return fromLevel;
    }

    public void setFromLevel(int fromLevel) {
	this.fromLevel = fromLevel;
    }

    public int getUntilLevel() {
	return untilLevel;
    }

    public void setUntilLevel(int untilLevel) {
	this.untilLevel = untilLevel;
    }

    public String getLegacyKey() {
	return legacyKey;
    }

    public void setLegacyKey(String legacyKey) {
	this.legacyKey = legacyKey;
    }
}
