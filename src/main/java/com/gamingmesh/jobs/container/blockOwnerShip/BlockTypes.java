package com.gamingmesh.jobs.container.blockOwnerShip;

import java.util.HashMap;

import net.Zrips.CMILib.Items.CMIMaterial;

public enum BlockTypes {

    BREWING_STAND("Brewing", "BREWING_STAND", "LEGACY_BREWING_STAND"),
    FURNACE("Furnace", "FURNACE", "LEGACY_BURNING_FURNACE"),
    SMOKER("Smoker"),
    BLAST_FURNACE("BlastFurnace");

    private String[] names;
    private String path = "_";

    private static HashMap<CMIMaterial, BlockTypes> cache = new HashMap<>();

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

    BlockTypes(String path) {
        this.path = path;
        names = new String[] { toString() };
    }

    BlockTypes(String path, String... names) {
        this.path = path;
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public static BlockTypes getFromCMIMaterial(CMIMaterial type) {
        return cache.get(type);
    }

    public String getPath() {
        return path;
    }
}
