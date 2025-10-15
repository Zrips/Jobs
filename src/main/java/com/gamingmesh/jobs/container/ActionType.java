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

import java.util.Arrays;
import java.util.EnumSet;

import net.Zrips.CMILib.Container.CMIText;

public enum ActionType {
    BREAK(ActionSubType.BLOCK, ActionSubType.PROTECTED),
    STRIPLOGS("StripLogs", ActionSubType.BLOCK),
    TNTBREAK("TNTBreak", ActionSubType.BLOCK),
    PLACE(ActionSubType.BLOCK, ActionSubType.PROTECTED),
    KILL(ActionSubType.ENTITY),
    MMKILL("MMKill", ActionSubType.ENTITY, ActionSubType.CUSTOM),
    FISH(ActionSubType.MATERIAL),
    PYROFISHINGPRO("PyroFishingPro", ActionSubType.CUSTOM),
    CUSTOMFISHING("CustomFishing", ActionSubType.CUSTOM),
    CRAFT(ActionSubType.MATERIAL),
    VTRADE("VTrade", ActionSubType.MATERIAL),
    SMELT(ActionSubType.MATERIAL),
    BREW(ActionSubType.MATERIAL),
    ENCHANT(ActionSubType.ENCHANTMENT, ActionSubType.MATERIAL),
    REPAIR(ActionSubType.MATERIAL),
    BREED(ActionSubType.ENTITY),
    TAME(ActionSubType.ENTITY),
    DYE(ActionSubType.MATERIAL),
    SHEAR(ActionSubType.ENTITY),
    MILK(ActionSubType.ENTITY),
    EXPLORE(ActionSubType.CUSTOM),
    EAT(ActionSubType.MATERIAL),
    CUSTOMKILL("custom-kill", ActionSubType.ENTITY, ActionSubType.CUSTOM),
    COLLECT(ActionSubType.MATERIAL),
    BAKE(ActionSubType.MATERIAL),
    BUCKET(ActionSubType.MATERIAL),
    BRUSH(ActionSubType.BLOCK, ActionSubType.MATERIAL),
    WAX(ActionSubType.BLOCK, ActionSubType.PROTECTED),
    SCRAPE(ActionSubType.BLOCK, ActionSubType.PROTECTED);

    private String name;

    private EnumSet<ActionSubType> subTypes = EnumSet.noneOf(ActionSubType.class);

    ActionType(ActionSubType... subTypes) {
        this(null, subTypes);
    }

    ActionType(String name, ActionSubType... subTypes) {
        this.subTypes = (subTypes == null || subTypes.length == 0)
            ? EnumSet.noneOf(ActionSubType.class)
            : EnumSet.copyOf(Arrays.asList(subTypes));
        this.name = name == null ? CMIText.firstToUpperCase(this.toString()) : name;
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
}
