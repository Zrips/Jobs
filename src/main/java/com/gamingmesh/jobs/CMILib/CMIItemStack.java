package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;

public class CMIItemStack {

    private int id = 0;
    private short data = 0;
    private short durability = 0;
    private int amount = 0;

    private String bukkitName = null;
    private String mojangName = null;
    private CMIMaterial cmiMaterial = null;
    private Material material = null;
    private ItemStack item;

    public CMIItemStack(Material material) {
	this.material = material;
	this.cmiMaterial = CMIMaterial.get(material);
    }

    public CMIItemStack(CMIMaterial cmiMaterial) {
	this.cmiMaterial = cmiMaterial;
	if (cmiMaterial != null)
	    this.material = cmiMaterial.getMaterial();
    }

    public CMIItemStack(ItemStack item) {
	setItemStack(item);
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
	cm.setCMIMaterial(cmiMaterial);
	cm.setMaterial(material);
	cm.setItemStack(item != null ? item.clone() : null);
	return cm;
    }

    public int getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public short getData() {
	return data;
    }

    public boolean isTool() {
	return getMaxDurability() > 0;
    }

    public short getDurability() {
	return Jobs.getNms().getDurability(getItemStack());
    }

    public short getMaxDurability() {
	return material.getMaxDurability();
    }

    public void setData(short data) {
//    	CMIMaterial got = CMIMaterial.get(id, data);
//    	if (got != null && got.getLegacyData() == data)
	this.data = data;
    }

    public CMIItemStack setDisplayName(String name) {
	ItemMeta meta = getItemStack().getItemMeta();
	if (meta != null) {
	    if (name == null) {
		meta.setDisplayName(null);
	    } else
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	}
	getItemStack().setItemMeta(meta);
	return this;
    }

    public String getDisplayName() {
	ItemMeta meta = getItemStack().getItemMeta();
	return meta == null || meta.getDisplayName() == null || meta.getDisplayName().isEmpty() ? getRealName() : meta.getDisplayName();
    }

    public CMIItemStack addLore(String string) {
	if (string == null)
	    return this;
	ItemMeta meta = getItemStack().getItemMeta();
	List<String> lore = meta.getLore();
	if (lore == null)
	    lore = new ArrayList<String>();
	lore.add(ChatColor.translateAlternateColorCodes('&', string));
	meta.setLore(lore);
	getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack clearLore() {
	ItemMeta meta = getItemStack().getItemMeta();
	List<String> t = new ArrayList<>();
	meta.setLore(t);
	getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack setLore(List<String> lore) {
	if (lore == null || lore.isEmpty())
	    return this;
	ItemMeta meta = getItemStack().getItemMeta();
	List<String> t = new ArrayList<>();
	for (String one : lore) {
	    t.add(ChatColor.translateAlternateColorCodes('&', one));
	}
	meta.setLore(t);
	getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack addEnchant(Enchantment enchant, Integer level) {
	if (enchant == null)
	    return this;
	ItemMeta meta = getItemStack().getItemMeta();
	meta.addEnchant(enchant, level, true);
	getItemStack().setItemMeta(meta);
	return this;
    }

    public CMIItemStack addEnchant(HashMap<Enchantment, Integer> enchants) {
	if (enchants == null || enchants.isEmpty())
	    return this;
	for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
	    addEnchant(oneEnch.getKey(), oneEnch.getValue());
	}
	return this;
    }

    public CMIItemStack clearEnchants() {
	ItemMeta meta = getItemStack().getItemMeta();
	meta.getEnchants().clear();
	getItemStack().setItemMeta(meta);
	return this;
    }

    public List<String> getLore() {
	ItemMeta meta = this.getItemStack().getItemMeta();
//    List<String> lore = null;
	if (meta != null) {
	    List<String> lore = meta.getLore();
	    if (lore == null) {
		lore = new ArrayList<String>();
		meta.setLore(lore);
//    	this.getItemStack().setItemMeta(meta);
	    }

	    return meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
	}
	return new ArrayList<String>();
    }

    public String getRealName() {
	return getCMIType() == null || getCMIType() == CMIMaterial.NONE ? getType().name() : getCMIType().getName();
//    if (this.getItemStack() != null) {
    //
////        String translated = CMI.getInstance().getItemManager().getTranslatedName(this.getItemStack());
////        if (translated != null)
////    	return translated;
//        try {
//    	return CMI.getInstance().getRef().getItemMinecraftName(this.getItemStack());
//        } catch (Exception e) {
//        }
//    }
//    return CMI.getInstance().getItemManager().getRealName(this, true).getName();
    }

    public String getBukkitName() {
	return bukkitName == null || bukkitName.isEmpty() ? null : bukkitName;
    }

    public void setBukkitName(String bukkitName) {
	this.bukkitName = bukkitName;
    }

    public String getMojangName() {
//    if (getCMIType().isSkull() && !Version.isCurrentEqualOrHigher(Version.v1_13_R1))
//        mojangName = "skull";
//    try {
//        mojangName = CMI.getInstance().getRef().getItemMinecraftName(getItemStack()).replace("minecraft:", "");
//    } catch (Exception e) {
    //
//    }
	return mojangName == null || mojangName.isEmpty() ? getCMIType().getMaterial().name() : mojangName;
    }

    public void setMojangName(String mojangName) {
	if (mojangName != null)
	    this.mojangName = mojangName.replace("minecraft:", "");
    }

    public Material getType() {
	if (material == null && cmiMaterial != null)
	    return cmiMaterial.getMaterial();
	return material;
    }

    public CMIMaterial getCMIType() {
	return cmiMaterial == null ? CMIMaterial.get(material) : cmiMaterial;
    }

    /**
     * Gets the material
     * 
     * @deprecated Use {@link #getType()}
     * @return Material
     */
    @Deprecated
    public Material getMaterial() {
	return getType();
    }

    public void setMaterial(Material material) {
	this.cmiMaterial = CMIMaterial.get(material);
	this.material = material;
    }

    public void setCMIMaterial(CMIMaterial material) {
	this.cmiMaterial = material;
	this.material = material == null ? null : material.getMaterial();
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemStack() {
	if (item == null) {
	    if (cmiMaterial.isMonsterEgg()) {
		if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		    item = new ItemStack(getType());
		    item.setAmount(getAmount());
		} else
		    item = new ItemStack(getType(), amount == 0 ? 1 : amount, data == 0 ? (short) 90 : data);
	    } else {
		if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		    item = new ItemStack(getType());
		    item.setAmount(getAmount());
		} else
		    item = new ItemStack(getType(), amount == 0 ? 1 : amount, data);
	    }

	    if (getCMIType().isPotion() || item.getType().name().contains("SPLASH_POTION") || item.getType().name().contains("TIPPED_ARROW")) {
		PotionMeta potion = (PotionMeta) item.getItemMeta();
		PotionEffectType effect = PotionEffectType.getById(data);
		if (effect != null) {
		    potion.addCustomEffect(new PotionEffect(effect, 60, 0), true);
		}
		item.setItemMeta(potion);
		item.setDurability((short) 0);
		potion = (PotionMeta) item.getItemMeta();
		potion.setDisplayName(getRealName());
		item.setItemMeta(potion);
	    }
	}
	return item;
    }

    @SuppressWarnings("deprecation")
    public CMIItemStack setItemStack(ItemStack item) {
	this.item = item == null ? null : item;
	if (item != null) {
	    this.amount = item.getAmount();
	    this.material = item.getType();
	    this.cmiMaterial = CMIMaterial.get(this.material);
	    if (Version.isCurrentEqualOrLower(Version.v1_13_R2))
		this.id = item.getType().getId();
	    else if (cmiMaterial != null) {
		this.id = cmiMaterial.getId();
	    }
	    if ((getType().isBlock() || getType().isSolid()))
		data = item.getData().getData();

	    if (item.getType().getMaxDurability() - item.getDurability() < 0)
		data = item.getData().getData();

	    if (item.getType().getMaxDurability() > 15)
		data = (short) 0;

	    if (CMIMaterial.SPAWNER.equals(item.getType()))
		data = getEntityType().getTypeId();
	    else if (item.getType() == Material.POTION || item.getType().name().contains("SPLASH_POTION") || item.getType().name().contains("TIPPED_ARROW")) {
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
	return amount <= 0 ? 1 : amount;
    }

    public void setAmount(int amount) {
	this.amount = amount;
	if (item != null)
	    this.item.setAmount(this.amount == 0 ? item.getAmount() : this.amount);
    }

    public boolean isSimilar(ItemStack item) {
	return isSimilar(ItemManager.getItem(item));
    }

    public boolean isSimilar(CMIItemStack item) {
	if (item == null)
	    return false;

	try {
	    if ((item.getCMIType().isPotion() || item.getCMIType().equals(CMIMaterial.TIPPED_ARROW)) &&
		(getCMIType().isPotion() || getCMIType().equals(CMIMaterial.TIPPED_ARROW)) &&
		getType().equals(item.getType())) {
		PotionMeta potion = (PotionMeta) item.getItemStack().getItemMeta();
		PotionMeta potion2 = (PotionMeta) getItemStack().getItemMeta();
		try {
		    if (potion != null && potion.getBasePotionData() != null) {
			PotionData base1 = potion.getBasePotionData();
			if (base1.getType() != null) {
			    if (potion2 != null && potion2.getBasePotionData() != null) {
				PotionData base2 = potion2.getBasePotionData();
				if (base2.getType() != null) {
				    if (base1.getType().equals(base2.getType()) && base1.isExtended() == base2.isExtended() && base1.isUpgraded() == base2.isUpgraded())
					return true;
				}
			    }
			}
		    }
		    return false;
		} catch (NoSuchMethodError e) {
		}
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	try {
	    if (this.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta && item.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) {
		EnchantmentStorageMeta meta1 = (EnchantmentStorageMeta) this.getItemStack().getItemMeta();
		EnchantmentStorageMeta meta2 = (EnchantmentStorageMeta) item.getItemStack().getItemMeta();

		for (Entry<Enchantment, Integer> one : meta1.getEnchants().entrySet()) {
		    if (!meta2.getEnchants().containsKey(one.getKey()) || meta2.getEnchants().get(one.getKey()) != one.getValue())
			return false;
		}

		for (Entry<Enchantment, Integer> one : meta1.getStoredEnchants().entrySet()) {
		    if (!meta2.getStoredEnchants().containsKey(one.getKey()) || meta2.getStoredEnchants().get(one.getKey()) != one.getValue())
			return false;
		}
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	}

	if ((item.getCMIType() == CMIMaterial.SPAWNER || item.getCMIType().isMonsterEgg()) && (getCMIType() == CMIMaterial.SPAWNER || getCMIType().isMonsterEgg())) {
	    if (this.cmiMaterial != item.cmiMaterial)
		return false;
	    if (getEntityType() != item.getEntityType())
		return false;
	    return true;
	}

	if (Version.isCurrentEqualOrHigher(Version.v1_13_R1))
	    return this.cmiMaterial == item.cmiMaterial;
	return this.cmiMaterial == item.cmiMaterial && this.getData() == item.getData();
    }

    public EntityType getEntityType() {
	if (getItemStack() == null)
	    return null;
	ItemStack is = getItemStack().clone();
	if (is.getItemMeta() instanceof org.bukkit.inventory.meta.BlockStateMeta) {
	    org.bukkit.inventory.meta.BlockStateMeta bsm = (org.bukkit.inventory.meta.BlockStateMeta) is.getItemMeta();
	    if (bsm.getBlockState() instanceof CreatureSpawner) {
		CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
		return bs.getSpawnedType();
	    }
	}

	return EntityType.fromId(is.getData().getData());
    }

    public void setDurability(short durability) {
	this.durability = durability;
    }

    public String toOneLiner() {
	String liner = getType().toString();
	if (getCMIType().isPotion() || getType().name().contains("TIPPED_ARROW")) {
	    PotionMeta potion = (PotionMeta) item.getItemMeta();
	    try {
		if (potion != null && potion.getBasePotionData() != null && potion.getBasePotionData().getType() != null && potion.getBasePotionData().getType().getEffectType() != null) {
		    liner += ":" + potion.getBasePotionData().getType().getEffectType().getName() + "-" + potion.getBasePotionData().isUpgraded() + "-" + potion.getBasePotionData().isExtended();
		}
	    } catch (NoSuchMethodError e) {
	    }
	} else {
	    if (Version.isCurrentLower(Version.v1_13_R1))
		liner += ":" + getData();
	}
	if (this.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) {
	    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) getItemStack().getItemMeta();
	    String s = "";
	    for (Entry<Enchantment, Integer> one : meta.getStoredEnchants().entrySet()) {
		if (!s.isEmpty())
		    s += ";";
		s += CMIEnchantment.get(one.getKey()) + "x" + one.getValue();
	    }

	    for (Entry<Enchantment, Integer> one : meta.getEnchants().entrySet()) {
		if (!s.isEmpty())
		    s += ";";
		s += CMIEnchantment.get(one.getKey()) + "x" + one.getValue();
	    }
	    if (!s.isEmpty())
		liner += ":" + s;
	}

	return liner;
    }

}
