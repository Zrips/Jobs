package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

import com.gamingmesh.jobs.stuff.Util;

public enum CMIEnchantment {

    AQUA_AFFINITY("WATER_WORKER"),
    BANE_OF_ARTHROPODS("DAMAGE_ARTHROPODS", "ARTHROPODS_DAMAGE"),
    BINDING_CURSE,
    BLAST_PROTECTION("PROTECTION_EXPLOSIONS", "EXPLOSION_PROTECTION", "EXPLOSIONS_PROTECTION"),
    CHANNELING,
    DEPTH_STRIDER,
    EFFICIENCY("DIG_SPEED"),
    FEATHER_FALLING("PROTECTION_FALL", "FALL_PROTECTION"),
    FIRE_ASPECT,
    FIRE_PROTECTION("PROTECTION_FIRE"),
    FLAME("ARROW_FIRE", "FIRE_ARROW"),
    FORTUNE("LOOT_BONUS_BLOCKS"),
    FROST_WALKER,
    IMPALING,
    INFINITY("ARROW_INFINITE", "INFINITE_ARROW"),
    KNOCKBACK,
    LOOTING("LOOT_BONUS_MOBS"),
    LOYALTY,
    LUCK_OF_THE_SEA("LUCK"),
    LURE,
    MENDING,
    MULTISHOT("MULTSHOT"),
    PIERCING,
    POWER("ARROW_DAMAGE"),
    PROJECTILE_PROTECTION("PROTECTION_PROJECTILE"),
    PROTECTION("PROTECTION_ENVIRONMENTAL", "ENVIRONMENTAL_PROTECTION"),
    PUNCH("ARROW_KNOCKBACK", "KNOCKBACK_ARROW"),
    QUICK_CHARGE,
    RESPIRATION("OXYGEN"),
    RIPTIDE,
    SHARPNESS("DAMAGE_ALL"),
    SILK_TOUCH,
    SMITE("DAMAGE_UNDEAD", "UNDEAD_DAMAGE"),
    SOUL_SPEED,
    SWEEPING("SWEEPING_EDGE"),
    THORNS,
    UNBREAKING("DURABILITY"),
    VANISHING_CURSE;

    private static Map<String, CMIEnchantment> map = new HashMap<>();
    private static Map<Enchantment, CMIEnchantment> emap = new HashMap<>();
    private static Map<String, Enchantment> gmap = new HashMap<>();

    private List<String> subName = new ArrayList<>();
    private List<String> customNames = new ArrayList<>();
    private Enchantment enchantment;

    @SuppressWarnings("deprecation")
    CMIEnchantment(String... subName) {
	if (subName != null)
	    this.subName.addAll(Arrays.asList(subName));

	String temp = toString().toLowerCase().replace("_", "");

	for (Enchantment one : Enchantment.values()) {
	    try {
		if (one.getName().toLowerCase().replace("_", "").equalsIgnoreCase(temp)) {
		    enchantment = one;
		    break;
		}
	    } catch (Exception | Error e) {
		try {
		    if (one.getKey().toString().split(":", 2)[1].toLowerCase().replace("_", "").equalsIgnoreCase(temp)) {
			enchantment = one;
			break;
		    }
		} catch (Exception | Error ex) {
		}
	    }
	}

	// Worst case scenario
	if (enchantment == null) {
	    for (Enchantment one : Enchantment.values()) {
		if (one.toString().toLowerCase().replace("_", "").equalsIgnoreCase(temp)) {
		    enchantment = one;
		    break;
		}
	    }
	}

	// now checks for subnames
	if (enchantment == null) {
	    en: for (Enchantment one : Enchantment.values()) {
		for (String subs : this.subName) {
		    try {
			if (one.getName().toLowerCase().replace("_", "").equalsIgnoreCase(subs.toLowerCase().replace("_", ""))) {
			    enchantment = one;
			    break en;
			}
		    } catch (Exception | Error e) {
			try {
			    if (one.getKey().toString().split(":", 2)[1].toLowerCase().replace("_", "").equalsIgnoreCase(temp)) {
				enchantment = one;
				break en;
			    }
			} catch (Exception | Error ex) {
			}
		    }
		}
	    }
	}

	if (enchantment == null) {
	    o: for (Enchantment one : Enchantment.values()) {
		for (String subs : this.subName) {
		    if (one.toString().toLowerCase().replace("_", "").equalsIgnoreCase(subs.toLowerCase().replace("_", ""))) {
			enchantment = one;
			break o;
		    }
		}
	    }
	}
    }

    public List<String> getSubNames() {
	return subName;
    }

    private static void fillUpMap() {
	map.clear();
	emap.clear();

	for (CMIEnchantment one : CMIEnchantment.values()) {
	    map.put(one.toString().toLowerCase().replace("_", ""), one);
	    for (String oneC : one.getSubNames()) {
		map.put(oneC.toLowerCase().replace("_", ""), one);
	    }
	    for (String oneC : one.getCustomNames()) {
		map.put(oneC.toLowerCase().replace("_", ""), one);
	    }
	    emap.put(one.getEnchantment(), one);
	}
	try {
	    for (Enchantment one : Enchantment.values()) {
		String name = one.getKey().getKey().toLowerCase().replace("_", "").replace("minecraft:", "");
		if (!map.containsKey(name)) {
		    gmap.put(name, one);
		}
	    }
	} catch (Throwable e) {
	}
    }

    public static CMIEnchantment get(String name) {
	if (map.isEmpty())
	    fillUpMap();

	name = name.contains(":") ? name.split(":", 2)[0] : name.contains("-") ? name.split("-", 2)[0] : name;
	name = name.toLowerCase().replace("_", "");
	return map.get(name);
    }

    public static Enchantment getEnchantment(String name) {
	if (map.isEmpty())
	    fillUpMap();
	name = name.contains(":") ? name.split(":", 2)[0] : name.contains("-") ? name.split("-", 2)[0] : name;
	name = name.toLowerCase().replace("_", "");

	CMIEnchantment ec = map.get(name);

	if (ec == null) {
	    return gmap.get(name);
	}

	return ec.getEnchantment();
    }

    public static CMIEnchantment get(Enchantment enchantment) {
	if (map.isEmpty())
	    fillUpMap();

	return emap.get(enchantment);
    }

    public List<String> getCustomNames() {
	return customNames;
    }

    public void setCustomNames(List<String> customNames) {
	this.customNames = customNames;
	fillUpMap();
    }

    public void addCustomName(String customName) {
	this.customNames.add(customName);
	fillUpMap();
    }

    public Enchantment getEnchantment() {
	return enchantment;
    }

    public String getName() {
	return Util.firstToUpperCase(toString());
    }

    public static String getName(Enchantment enchant) {
	CMIEnchantment ce = get(enchant);
	if (ce == null)
	    return "Unknown";

	return ce.getName();
    }
}
