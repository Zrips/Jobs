package com.gamingmesh.jobs.container;

import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.CMILib.CMIMaterial;

public enum ArmorTypes {
    HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8), ELYTRA(6);

    private final int slot;

    ArmorTypes(int slot) {
	this.slot = slot;
    }

    public final static ArmorTypes matchType(final ItemStack itemStack) {
	switch (CMIMaterial.get(itemStack)) {
	case DIAMOND_HELMET:
	case GOLDEN_HELMET:
	case IRON_HELMET:
	case CHAINMAIL_HELMET:
	case LEATHER_HELMET:
	    return HELMET;
	case DIAMOND_CHESTPLATE:
	case GOLDEN_CHESTPLATE:
	case IRON_CHESTPLATE:
	case CHAINMAIL_CHESTPLATE:
	case LEATHER_CHESTPLATE:
	    return CHESTPLATE;
	case DIAMOND_LEGGINGS:
	case GOLDEN_LEGGINGS:
	case IRON_LEGGINGS:
	case CHAINMAIL_LEGGINGS:
	case LEATHER_LEGGINGS:
	    return LEGGINGS;
	case DIAMOND_BOOTS:
	case GOLDEN_BOOTS:
	case IRON_BOOTS:
	case CHAINMAIL_BOOTS:
	case LEATHER_BOOTS:
	    return BOOTS;
	case ELYTRA:
	    return ELYTRA;
	default:
	    return null;
	}
    }

    public int getSlot() {
	return slot;
    }
}
