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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Items.CMIMaterial;

public enum ActionType {
	BREAK(CMIMaterial.DIAMOND_PICKAXE, ActionSubType.BLOCK, ActionSubType.PROTECTED),
	STRIPLOGS("StripLogs", CMIMaterial.STRIPPED_ACACIA_LOG, ActionSubType.BLOCK),
	TNTBREAK("TNTBreak", CMIMaterial.TNT, ActionSubType.BLOCK),
	PLACE(CMIMaterial.BRICKS, ActionSubType.BLOCK, ActionSubType.PROTECTED),
	KILL(CMIMaterial.DIAMOND_SWORD, ActionSubType.ENTITY),
	MMKILL("MMKill", CMIMaterial.WOODEN_SWORD, ActionSubType.ENTITY, ActionSubType.CUSTOM),
	FISH(CMIMaterial.FISHING_ROD, ActionSubType.MATERIAL),
	PYROFISHINGPRO("PyroFishingPro", CMIMaterial.PUFFERFISH, ActionSubType.CUSTOM),
	CUSTOMFISHING("CustomFishing", CMIMaterial.TROPICAL_FISH, ActionSubType.CUSTOM),
	CRAFT(CMIMaterial.CRAFTING_TABLE, ActionSubType.MATERIAL),
	VTRADE("VTrade", CMIMaterial.EMERALD, ActionSubType.MATERIAL),
	SMELT(CMIMaterial.FURNACE, ActionSubType.MATERIAL),
	BREW(CMIMaterial.BREWING_STAND, ActionSubType.MATERIAL),
	ENCHANT(CMIMaterial.ENCHANTING_TABLE, ActionSubType.ENCHANTMENT, ActionSubType.MATERIAL),
	REPAIR(CMIMaterial.ANVIL, ActionSubType.MATERIAL),
	BREED(CMIMaterial.APPLE, ActionSubType.ENTITY),
	TAME(CMIMaterial.LEAD, ActionSubType.ENTITY),
	DYE(CMIMaterial.PURPLE_DYE, ActionSubType.MATERIAL),
	SHEAR(CMIMaterial.SHEARS, ActionSubType.ENTITY),
	MILK(CMIMaterial.MILK_BUCKET, ActionSubType.ENTITY),
	EXPLORE(CMIMaterial.LEATHER_BOOTS, ActionSubType.CUSTOM),
	EAT(CMIMaterial.BREAD, ActionSubType.MATERIAL),
	CUSTOMKILL("custom-kill", CMIMaterial.PLAYER_HEAD, ActionSubType.ENTITY, ActionSubType.CUSTOM),
	COLLECT(CMIMaterial.SWEET_BERRIES, ActionSubType.MATERIAL),
	BAKE(CMIMaterial.CAKE, ActionSubType.MATERIAL),
	BUCKET(CMIMaterial.BUCKET, ActionSubType.MATERIAL),
	BRUSH(CMIMaterial.BRUSH, ActionSubType.BLOCK, ActionSubType.MATERIAL),
	WAX(CMIMaterial.HONEYCOMB, ActionSubType.BLOCK, ActionSubType.PROTECTED),
	SCRAPE(CMIMaterial.WOODEN_AXE, ActionSubType.BLOCK, ActionSubType.PROTECTED);

	private String name;

	private List<ItemStack> guiItems = new ArrayList<ItemStack>();

	private EnumSet<ActionSubType> subTypes = EnumSet.noneOf(ActionSubType.class);

	ActionType(CMIMaterial guiMat, ActionSubType... subTypes) {
		this(null, guiMat, subTypes);
	}

	ActionType(String name, CMIMaterial guiMat, ActionSubType... subTypes) {
		this.subTypes = (subTypes == null || subTypes.length == 0) ? EnumSet.noneOf(ActionSubType.class) : EnumSet.copyOf(Arrays.asList(subTypes));
		this.name = name == null ? CMIText.firstToUpperCase(this.toString()) : name;
		getGuiItems().add(guiMat.newItemStack());
	}

	public String getName() {
		return name;
	}

	public boolean hasSubType(ActionSubType subType) {
		return subTypes.contains(subType);
	}

	public EnumSet<ActionSubType> getSubTypes() {
		return subTypes;
	}

	public static ActionType getByName(String name) {
		if (name != null) {
			name = name.replace("_", "");

			// Temp fix to rename vax to wax
			if (name.equalsIgnoreCase("vax"))
				name = "wax";

			for (ActionType one : ActionType.values()) {
				if (one.name.equalsIgnoreCase(name))
					return one;
			}
		}

		return null;
	}

	public List<ItemStack> getGuiItems() {
		return guiItems;
	}

	public void setGuiItems(List<ItemStack> guiItems) {
		this.guiItems = guiItems;
	}
}
