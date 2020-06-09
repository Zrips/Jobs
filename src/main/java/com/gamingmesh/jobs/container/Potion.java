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
 * <a href="https://papermc.io/javadocs/paper/1.13/org/bukkit/potion/PotionType.html">API reference for names</a>
 */
public enum Potion {
    AWKWARD("awkward"),
    FIRE_RESISTANCE("fire_resistance"),
    INSTANT_DAMAGE("instant_damage"),
    INSTANT_HEAL("instant_heal"),
    INVISIBILITY("invisibility"),
    JUMP("jump"),
    LUCK("luck"),
    MUNDANE("mundane"),
    NIGHT_VISION("night_vision"),
    LONG_NIGHT_VISION("long_night_vision"),
    POISON("poison"),
    REGEN("regen"),
    SLOW_FALLING("slow_falling"),
    SLOWNESS("slowness"),
    SPEED("speed"),
    STRENGTH("strength"),
    THICK("thick"),
    TURTLE_MASTER("turtle_master"),
    WATER_BREATHING("water_breathing"),
    WEAKNESS("weakness");

    private final String name;

    Potion(String name) {
        this.name = name.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public static Potion getByName(String name) {
        for (Potion one : Potion.values()) {
            if (one.name.equalsIgnoreCase(name)) {
                return one;
            }
        }

        return null;
    }
}
