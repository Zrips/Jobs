package com.gamingmesh.jobs.container.blockOwnerShip;

import java.util.HashMap;

import net.Zrips.CMILib.Items.CMIMaterial;

public enum BlockTypes {

    BREWING_STAND("BREWING_STAND", "LEGACY_BREWING_STAND"),
    FURNACE("FURNACE", "LEGACY_BURNING_FURNACE"),
    SMOKER,
    BLAST_FURNACE;

    private final String[] names;

    private static final HashMap<CMIMaterial, BlockTypes> cache = new HashMap<>();
    static {
	for (CMIMaterial one : CMIMaterial.values()) {
	    for (BlockTypes b : values()) {
		for (String name : b.names) {
		    if (name.equals(one.toString())) {
			cache.put(one, b);
		    }
		}
	    }
	}
    }

    BlockTypes() {
	names = new String[] { toString() };
    }

    BlockTypes(String... names) {
	this.names = names;
    }

    public String[] getNames() {
	return names;
    }

    public static BlockTypes getFromCMIMaterial(CMIMaterial type) {
	return cache.get(type);
    }
}
