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

import java.util.HashMap;
import java.util.List;

import org.bukkit.enchantments.Enchantment;

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
