/**
 * Copyright (C) 2017 Zrips
 */

package com.gamingmesh.jobs.CMILib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ItemReflection {

    private static Class<?> CraftServerClass;
    private static Object CraftServer;
    private static Class<?> CraftItemStack;
    private static Class<?> Item;
    private static Class<?> IStack;

    static {
	initialize();
    }

    private static void initialize() {
	try {
	    CraftServerClass = getBukkitClass("CraftServer");
	    CraftServer = CraftServerClass.cast(Bukkit.getServer());
	    CraftItemStack = getBukkitClass("inventory.CraftItemStack");
	    Item = getMinecraftClass("Item");
	    IStack = getMinecraftClass("ItemStack");
	} catch (ClassCastException | ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private static Class<?> getBukkitClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("org.bukkit.craftbukkit." + Version.getCurrent() + "." + nmsClassString);
    }

    public static Class<?> getMinecraftClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("net.minecraft.server." + Version.getCurrent() + "." + nmsClassString);
    }

    public static String getItemMinecraftName(ItemStack item) {
	try {
	    Object nmsStack = asNMSCopy(item);
	    Field field = Item.getField("REGISTRY");
	    Object reg = field.get(field);
	    Method meth = reg.getClass().getMethod("b", Object.class);
	    meth.setAccessible(true);
	    Method secmeth = nmsStack.getClass().getMethod("getItem");
	    Object res2 = secmeth.invoke(nmsStack);
	    Object res = meth.invoke(reg, res2);
	    return res.toString();
	} catch (Throwable e) {
	    return null;
	}
    }

    public String getItemMinecraftNamePath(ItemStack item) {
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method itemMeth = Item.getMethod("getById", int.class);
	    @SuppressWarnings("deprecation")
	    Object res = itemMeth.invoke(Item, item.getType().getId());
	    Method nameThingy = Item.getMethod("j", IStack);
	    Object resThingy = nameThingy.invoke(res, nmsStack);
	    return resThingy.toString();
	} catch (Throwable e) {
	    return null;
	}
    }

    public static Object asNMSCopy(ItemStack item) {
	try {
	    Method meth = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
	    return meth.invoke(CraftItemStack, item);
	} catch (Throwable e) {
	    return null;
	}
    }

    public Object asBukkitCopy(Object item) {
	try {
	    Method meth = CraftItemStack.getMethod("asBukkitCopy", IStack);
	    return meth.invoke(CraftItemStack, item);
	} catch (Throwable e) {
	    return null;
	}
    }

    public Object getCraftServer() {
	return CraftServer;
    }

    public static ItemStack getItemInOffHand(org.bukkit.entity.Player player) {
	return Version.getCurrent().isLower(Version.v1_9_R1) ? null : player.getInventory().getItemInOffHand();
    }

    public void setEndermiteActive(Entity ent, boolean state) {

    }

}
