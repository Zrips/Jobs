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

import net.Zrips.CMILib.Container.CMIText;

public enum ActionType {
    BREAK(),
    STRIPLOGS("StripLogs"),
    TNTBREAK("TNTBreak"),
    PLACE(),
    KILL(),
    MMKILL("MMKill"),
    FISH(),
    PYROFISHINGPRO("PyroFishingPro"),
    CRAFT(),
    VTRADE("VTrade"),
    SMELT(),
    BREW(),
    ENCHANT(),
    REPAIR(),
    BREED(),
    TAME(),
    DYE(),
    SHEAR(),
    MILK(),
    EXPLORE(),
    EAT(),
    CUSTOMKILL("custom-kill"),
    COLLECT(),
    BAKE(),
    BUCKET(),
    BRUSH();

    private String name;

    ActionType(String name) {
        this.name = name;
    }

    ActionType() {
        this.name = CMIText.firstToUpperCase(this.toString());
    }

    public String getName() {
        return name;
    }

    public static ActionType getByName(String name) {
        if (name != null) {
            name = name.replace("_", "");

            for (ActionType one : ActionType.values()) {
                if (one.name.equalsIgnoreCase(name))
                    return one;
            }
        }

        return null;
    }
}
