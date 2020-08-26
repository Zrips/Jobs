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
    AWKWARD,
    FIRE_RESISTANCE,
    INSTANT_DAMAGE,
    INSTANT_HEAL,
    INVISIBILITY,
    JUMP,
    LUCK,
    MUNDANE,
    NIGHT_VISION,
    LONG_NIGHT_VISION,
    POISON,
    REGENERATION,
    SLOW_FALLING,
    SLOWNESS,
    SPEED,
    STRENGTH,
    THICK,
    TURTLE_MASTER,
    WATER_BREATHING,
    WEAKNESS;

    public static Potion getByName(String name) {
        for (Potion one : Potion.values()) {
            if (one.toString().equalsIgnoreCase(name)) {
                return one;
            }
        }

        return null;
    }
}
