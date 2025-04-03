package com.gamingmesh.jobs.container.blockOwnerShip;

import java.util.HashMap;

import net.Zrips.CMILib.Items.CMIMaterial;

public enum BlockTypes {

    BREWING_STAND("brewingstands", "Brewing", "BREWING_STAND", "LEGACY_BREWING_STAND"),
    FURNACE("furnaces", "Furnace", "FURNACE", "LEGACY_BURNING_FURNACE"),
    SMOKER("smokers", "Smoker"),
    BLAST_FURNACE("blastfurnaces", "BlastFurnace");

    private String[] names;
    private String path = "_";
    private String permission = "";
    private boolean reasign = false;
    private int maxDefault = 10;

    private static HashMap<CMIMaterial, BlockTypes> cache = new HashMap<>();

    public static boolean anyToReasign = false;
    
    public static boolean isAnyToReasign(){
        return anyToReasign;
    }
    
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

    BlockTypes(String permission, String path) {
        this.permission = permission;
        this.path = path;
        names = new String[] { toString() };
    }

    BlockTypes(String permission, String path, String... names) {
        this.permission = permission;
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

    public String getPermissionNode() {
        return permission;
    }

    public boolean isReasign() {
        return reasign;
    }

    public void setReasign(boolean reasign) {
        if (reasign)
            anyToReasign = true;
        this.reasign = reasign;
    }

    public int getMaxDefault() {
        return maxDefault;
    }

    public void setMaxDefault(int maxDefault) {
        this.maxDefault = maxDefault;
    }
}
