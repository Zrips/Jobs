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

import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;

public class JobLimitedItems {
    private String node;
    CMIMaterial mat;
    private String name;
    private String itemString;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private int level;

    public JobLimitedItems(String node, String itemString, int level) {
        this.node = node;
        this.itemString = itemString.replace(" ", "_");

        CMIItemStack citem = CMIItemStack.deserialize(itemString);

        ItemStack item = citem.getItemStack();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName())
                name = meta.getDisplayName();
            if (meta.hasLore())
                lore = meta.getLore();
        }
        enchants = item.getEnchantments();
        mat = citem.getCMIType();

        this.level = level;
    }

    public String getNode() {
        return node;
    }

    public CMIItemStack getItemStack(Player player, CMIAsyncHead ahead) {
        return CMIItemStack.deserialize(itemString.replace("[player]", player == null ? "" : player.getName()), ahead);
    }

    @Deprecated
    public int getId() {
        return mat.getId();
    }

    public CMIMaterial getType() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public int getLevel() {
        return level;
    }

    public static ItemStack applyNBT(ItemStack item, int jobId, String node) {
        CMINBT nbt = new CMINBT(item);
        nbt.setInt("JobsLimited", jobId);
        return (ItemStack) nbt.setString("JobsLimitedNode", node);
    }
}
