package com.gamingmesh.jobs.CMILib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;

import com.gamingmesh.jobs.stuff.Util;

public class CMIEnchantment {

    private static final Map<String, Enchantment> byName = new HashMap<String, Enchantment>();

    public static void saveEnchants() {
	try {
	    for (Enchantment one : Enchantment.values()) {
		if (one == null || one.getName() == null)
		    continue;
		byName.put(one.getName().replace("_", ""), one);
	    }
	} catch (Exception | Error e) {
	    e.printStackTrace();
	}
    }

    public static Enchantment get(String nameId) {
	Enchantment enchant = getByName(nameId);
	return enchant;
    }

    public static Enchantment getByName(String name) {
	name = name.replace("_", "");

	for (Entry<String, Enchantment> one : byName.entrySet()) {
	    if (one == null || one.getValue().getName() == null)
		continue;
	    if (one.getValue().getName().replace("_", "").equalsIgnoreCase(name)) {
		return one.getValue();
	    }
	}
	return null;
    }

    public static Enchantment[] values() {
	return byName.values().toArray(new Enchantment[byName.size()]);
    }

    public static String getName(Enchantment enchant) {
	if (enchant == null || enchant.getName() == null)
	    return "Unknown";
	return Util.firstToUpperCase(enchant.getName());
    }

}
