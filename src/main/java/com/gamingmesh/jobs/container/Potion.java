/*
  Jobs Plugin for Bukkit
  Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.container;

/**
 * <a href="https://minecraft.gamepedia.com/Potion#Java_Edition">Minecraft-WIKI reference</a>
 */
public enum Potion {
    NIGHT_VISION("Night Vision"),
    INVISIBILITY("Invisibility"),
    LEAPING("Leaping"),
    FIRE_RESISTANCE("Fire Resistance"),
    SWIFTNESS("Swiftness"),
    SLOWNESS("Slowness"),
    WATER_BREATHING("Water Breathing"),
    HEALING("Instant Health"),
    HARMING("Harming"),
    POISON("Poison"),
    REGENERATION("Regeneration"),
    STRENGTH("Strength"),
    WEAKNESS("Weakness"),
    LUCK("Luck"),
    TURTLE_MASTER("The Turtle Master"),
    SLOW_FALLING("Slow Falling");

    private final String name;

    Potion(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public static Potion getByName(String name) {
	name = name.replace("_", "");
	for (Potion one : Potion.values()) {
	    if (one.name.equalsIgnoreCase(name))
		return one;
	}
	return null;
    }
}
