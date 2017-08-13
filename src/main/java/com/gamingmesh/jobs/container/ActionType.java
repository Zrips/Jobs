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

public enum ActionType {
    BREAK("Break"),
    TNTBREAK("TNTBreak"),
    PLACE("Place"),
    KILL("Kill"),
    MMKILL("MMKill"),
    FISH("Fish"),
    CRAFT("Craft"),
    SMELT("Smelt"),
    BREW("Brew"),
    ENCHANT("Enchant"),
    REPAIR("Repair"),
    BREED("Breed"),
    TAME("Tame"),
    DYE("Dye"),
    SHEAR("Shear"),
    MILK("Milk"),
    EXPLORE("Explore"),
    EAT("Eat"),
    CUSTOMKILL("custom-kill");

    private String name;

    private ActionType(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public static ActionType getByName(String name) {
	name = name.replace("_", "");
	for (ActionType one : ActionType.values()) {
	    if (one.name.equalsIgnoreCase(name))
		return one;
	}
	return null;
    }
}
