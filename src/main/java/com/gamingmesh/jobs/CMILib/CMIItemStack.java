/**
 * Copyright (C) 2017 Zrips
 */
package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.stuff.VersionChecker.Version;

public class CMIItemStack {

    private int id = 0;
    private short data = 0;
    private short durability = 0;
    private int amount = 0;

    private String bukkitName = null;
    private String mojangName = null;
    private CMIMaterial material = null;
    private ItemStack item;

    public CMIItemStack(Material material) {
	this.material = CMIMaterial.get(material);
    }

    public CMIItemStack(CMIMaterial material) {
	this.material = material;
    }

    public CMIItemStack(ItemStack item) {
	this.setItemStack(item);
    }

    @Override
    public CMIItemStack clone() {
	CMIItemStack cm = new CMIItemStack(material);
	cm.setId(id);
	cm.setData(data);
	cm.setAmount(amount);
	cm.setDurability(durability);
	cm.setBukkitName(bukkitName);
	cm.setMojangName(mojangName);
	cm.setCMIMaterial(material);
	cm.setItemStack(this.item != null ? this.item.clone() : null);
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

    public CMIItemStack setDisplayName(String name) {
	ItemMeta meta = this.getItemStack().getItemMeta();
	if (meta != null) {
	    if (name == null) {
		meta.setDisplayName(null);
	    } else
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	}
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public String getDisplayName() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	return meta == null || meta.getDisplayName() == null || meta.getDisplayName().isEmpty() ? this.material.getName() : meta.getDisplayName();
    }

    public CMIItemStack addLore(String string) {
	if (string == null)
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	List<String> lore = meta.getLore();
	if (lore == null)
	    lore = new ArrayList<>();
	lore.add(ChatColor.translateAlternateColorCodes('&', string));
	meta.setLore(lore);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack clearLore() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	List<String> t = new ArrayList<>();
	meta.setLore(t);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack setLore(List<String> lore) {
	if (lore == null || lore.isEmpty())
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	List<String> t = new ArrayList<>();
	for (String one : lore) {
	    t.add(ChatColor.translateAlternateColorCodes('&', one));
	}
	meta.setLore(t);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack addEnchant(Enchantment enchant, Integer level) {
	if (enchant == null)
	    return this;
	ItemMeta meta = this.getItemStack().getItemMeta();
	meta.addEnchant(enchant, level, true);
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack addEnchant(HashMap<Enchantment, Integer> enchants) {
	if (enchants == null || enchants.isEmpty())
	    return this;
	for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
	    this.addEnchant(oneEnch.getKey(), oneEnch.getValue());
	}
	return this;
    }

    public CMIItemStack clearEnchants() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	meta.getEnchants().clear();
	this.getItemStack().setItemMeta(meta);
	return this;
    }

    public List<String> getLore() {
	ItemMeta meta = this.getItemStack().getItemMeta();
	if (meta != null) {
	    List<String> lore = meta.getLore();
	    if (lore == null) {
		lore = new ArrayList<>();
		meta.setLore(lore);
	    }

	    return meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
	}
	return new ArrayList<String>();
    }

    public String getRealName() {

	return this.material.getName();
    }

    public String getBukkitName() {
	return bukkitName == null || bukkitName.isEmpty() ? null : bukkitName;
    }

    public void setBukkitName(String bukkitName) {
	this.bukkitName = bukkitName;
    }

    public String getMojangName() {
	return mojangName == null || mojangName.isEmpty() ? null : mojangName;
    }

    public void setMojangName(String mojangName) {
	if (mojangName != null)
	    this.mojangName = mojangName.replace("minecraft:", "");
    }

    public Material getType() {
	if (material == null)
	    return null;
	return material.getMaterial();
    }

    public CMIMaterial getCMIType() {
	return material;
    }

    @Deprecated
    public Material getMaterial() {
	return getType();
    }

    public void setMaterial(Material material) {
	this.material = CMIMaterial.get(material);
    }

    public void setCMIMaterial(CMIMaterial material) {
	this.material = material;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemStack() {
	if (item == null) {
	    if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_13_R1)) {
		this.item = new ItemStack(material.getMaterial(), this.amount == 0 ? 1 : this.amount);
	    } else {
		this.item = new ItemStack(material.getMaterial(), this.amount == 0 ? 1 : this.amount, data);
	    }

	    if (this.item.getType() == Material.POTION || item.getType().name().contains("SPLASH_POTION") || item.getType().name().contains("TIPPED_ARROW")) {
		PotionMeta potion = (PotionMeta) item.getItemMeta();
		PotionEffectType effect = PotionEffectType.getById(data);
		if (effect != null) {
		    potion.addCustomEffect(new PotionEffect(PotionEffectType.getById(data), 60, 0), true);
		}
		item.setItemMeta(potion);
		item.setDurability((short) 0);

		potion = (PotionMeta) item.getItemMeta();
		potion.setDisplayName(this.getRealName());
		item.setItemMeta(potion);
	    }
	}
	return item;
    }

    @SuppressWarnings("deprecation")
    public CMIItemStack setItemStack(ItemStack item) {
	this.item = item;
	if (item != null) {
	    this.id = item.getType().getId();
	    this.amount = item.getAmount();
	    this.material = CMIMaterial.get(item);
	    if ((material.isBlock() || material.isSolid())) {
		data = item.getData().getData();
	    }
	    if (item.getType().getMaxDurability() - item.getDurability() < 0) {
		data = item.getData().getData();
	    }

	    if (item.getType().getMaxDurability() > 15) {
		data = (short) 0;
	    }

	    if (item.getType() == Material.POTION || item.getType().name().contains("SPLASH_POTION") || item.getType().name().contains("TIPPED_ARROW")) {
		PotionMeta potion = (PotionMeta) item.getItemMeta();
		try {
		    if (potion != null && potion.getBasePotionData() != null && potion.getBasePotionData().getType() != null && potion.getBasePotionData().getType().getEffectType() != null) {
			data = (short) potion.getBasePotionData().getType().getEffectType().getId();
		    }
		} catch (NoSuchMethodError e) {
		}
	    }
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
	return isSimilar(ItemManager.getItem(item));
    }

    public boolean isSimilar(CMIItemStack item) {
	if (item == null)
	    return false;
	return this.material.equals(item.material) && this.getData() == item.getData();
    }

//    public boolean hasNbtTag() {
//	return CMI.getInstance().getRef().hasNbt(this.getItemStack());
//    }

//    public List<Recipe> getRecipesFor() {
//	ItemStack i = getItemStack().clone();
//	i.getData().setData((byte) data);
//	if (i.getType().getMaxDurability() > 15)
//	    i.setDurability((short) 0);
//	return Bukkit.getRecipesFor(i);
//    }
//
//    public List<Recipe> getRecipesFrom() {
//	ItemStack i = getItemStack().clone();
//	i.getData().setData((byte) data);
//	if (i.getType().getMaxDurability() > 15)
//	    i.setDurability((short) 0);
//	Iterator<Recipe> it = Bukkit.recipeIterator();
//	List<Recipe> recipes = new ArrayList<Recipe>();
//	while (it.hasNext()) {
//	    Recipe rec = it.next();
//	    for (ItemStack one : CMI.getInstance().getRecipeManager().getIngredientsList(rec)) {
//		if (one.isSimilar(i)) {
//		    recipes.add(rec);
//		    break;
//		}
//	    }
//	}
//
//	return recipes;
//    }

    public void setDurability(short durability) {
	this.durability = durability;
    }

//    public Set<Enchantment> getValidEnchants() {
//	Set<Enchantment> enchants = new HashSet<Enchantment>();
//	for (Enchantment one : CMIEnchantment.values()) {
//	    if (!CMIEnchantment.isEnabled(one))
//		continue;
//	    if (one.canEnchantItem(this.getItemStack()))
//		enchants.add(one);
//	}
//	return enchants;
//    }

}
