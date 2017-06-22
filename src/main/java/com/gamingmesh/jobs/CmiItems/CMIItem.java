package com.gamingmesh.jobs.CmiItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.stuff.Util;

public class CMIItem {

    private int id = 0;
    private short data = 0;
    private int amount = 0;

    private String bukkitName = null;
    private Material material = null;
    private ItemStack item;

    public CMIItem(Material material) {
	this.material = material;
    }

    @Override
    public CMIItem clone() {
	CMIItem cm = new CMIItem(material);
	cm.setId(id);
	cm.setData(data);
	cm.setBukkitName(bukkitName);
	cm.setItemStack(this.item);
	return cm;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public short getData() {
	return data;
    }

    public boolean isTool() {
	return getMaxDurability() > 0;
    }

    public short getDurability() {
	return this.getItemStack().getDurability();
    }

    public short getMaxDurability() {
	return this.material.getMaxDurability();
    }

    public void setData(short data) {
	this.data = data;
    }

    public CMIItem setDisplayName(String name) {
	if (name == null)
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public String getDisplayName() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	return meta.getDisplayName() == null ? this.getRealName() : meta.getDisplayName();
    }

    public CMIItem addLore(String string) {
	if (string == null)
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	List<String> lore = meta.getLore();
	if (lore == null)
	    lore = new ArrayList<String>();
	lore.add(ChatColor.translateAlternateColorCodes('&', string));
	meta.setLore(lore);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItem setLore(List<String> lore) {
	if (lore == null)
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	List<String> t = new ArrayList<String>();
	for (String one : lore) {
	    t.add(ChatColor.translateAlternateColorCodes('&', one));
	}
	meta.setLore(t);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItem addEnchant(Enchantment enchant, Integer level) {
	if (enchant == null)
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	meta.addEnchant(enchant, level, true);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItem addEnchant(HashMap<Enchantment, Integer> enchants) {
	if (enchants == null || enchants.isEmpty())
	    return this;
	for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
	    this.addEnchant(oneEnch.getKey(), oneEnch.getValue());
	}
	return this;
    }

    public CMIItem clearEnchants() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	meta.getEnchants().clear();
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public List<String> getLore() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	return meta.getLore();
    }

    public String getRealName() {
	return Jobs.getItemManager().getRealName(this, true).getName();
    }

    public String getBukkitName() {
	return bukkitName;
    }

    public void setBukkitName(String bukkitName) {
	this.bukkitName = bukkitName;
    }

    public Material getMaterial() {
	return material;
    }

    public void setMaterial(Material material) {
	this.material = material;
    }

    public ItemStack getItemStack() {
	if (item == null) {
	    this.item = new ItemStack(material, this.amount == 0 ? 1 : this.amount, data);
	}

	if (this.item.getType() == Material.MOB_SPAWNER) {
	    if (data == 0)
		data = 90;
	    EntityType type = EntityType.fromId(data);
	    if (type != null)
		this.item = Util.setEntityType(this.item, type);
	}

	return item;
    }

    public CMIItem setItemStack(ItemStack item) {
	this.item = item;
	if (item != null) {
	    this.amount = item.getAmount();
	    if ((material.isBlock() || material.isSolid()))
		data = item.getData().getData();
	    if (item.getType().getMaxDurability() - item.getDurability() < 0)
		data = item.getDurability();
	    if (item.getType() == Material.MOB_SPAWNER)
		data = Util.getEntityType(item).getTypeId();
	}
	return this;
    }

    public int getAmount() {
	return amount;
    }

    public void setAmount(int amount) {
	this.amount = amount;
    }

    public boolean isSimilar(ItemStack item) {
	return isSimilar(Jobs.getItemManager().getItem(item));
    }

    public boolean isSimilar(CMIItem item) {
	if (item == null)
	    return false;
	return this.getMaterial().equals(item.material) && this.getData() == item.getData();
    }

}
