/*
  Jobs Plugin for Bukkit
  Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
  <p>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.actions;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import com.gamingmesh.jobs.container.ActionType;

public class PotionItemActionInfo extends ItemActionInfo {
    private final PotionType potionType;

    public PotionItemActionInfo(ItemStack items, ActionType type, PotionType potionType) {
        super(items, type);
        this.potionType = potionType;
    }

    @Override
    public String getNameWithSub() {
        return getName() + ":" + potionType.toString();
    }
}
