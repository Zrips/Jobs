/**
 * Copyright (C) 2017 Zrips
 */

package com.gamingmesh.jobs;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Reflections {

    private Class<?> CraftServerClass;
    private Object CraftServer;

    private Class<?> NBTTagCompound;
    private Class<?> NBTBase;

    private Class<?> CraftItemStack;
    private Class<?> Item;
    private Class<?> IStack;
    private Jobs plugin;

    public Reflections(Jobs plugin) {
	this.plugin = plugin;
	initialize();
    }

    private void initialize() {
	try {
	    CraftServerClass = getBukkitClass("CraftServer");
	} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}
	try {
	    CraftServer = CraftServerClass.cast(Bukkit.getServer());
	} catch (SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}
	try {
	    NBTTagCompound = getMinecraftClass("NBTTagCompound");
	} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}
	try {
	    NBTBase = getMinecraftClass("NBTBase");
	} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}

	try {
	    CraftItemStack = getBukkitClass("inventory.CraftItemStack");
	} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}
	try {
	    Item = getMinecraftClass("Item");
	} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}
	try {
	    IStack = getMinecraftClass("ItemStack");
	} catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	}
    }

    private static Class<?> getBukkitClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("org.bukkit.craftbukkit." + Jobs.getVersionCheckManager().getVersion() + "." + nmsClassString);
    }

    public Class<?> getMinecraftClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("net.minecraft.server." + Jobs.getVersionCheckManager().getVersion() + "." + nmsClassString);
    }

    public String getItemMinecraftName(ItemStack item) {
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method itemMeth = Item.getMethod("getById", int.class);
	    Object res = itemMeth.invoke(Item, item.getTypeId());

	    String ff = "b";
	    switch (Jobs.getVersionCheckManager().getVersion()) {
	    case v1_10_R1:
	    case v1_9_R1:
	    case v1_9_R2:
	    case v1_8_R1:
	    case v1_8_R3:
	    case v1_8_R2:
		ff = "a";
		break;
	    case v1_11_R1:
	    case v1_11_R2:
	    case v1_12_R1:
	    case v1_13_R2:
	    case v1_12_R2:
	    case v1_13_R1:
		ff = "b";
		break;
	    case v1_7_R1:
	    case v1_7_R2:
	    case v1_7_R3:
	    case v1_7_R4:
		ff = "n";
		break;
	    default:
		break;
	    }

	    Method meth2 = res.getClass().getMethod(ff, IStack);
	    Object name = meth2.invoke(res, nmsStack);
	    return name.toString();
	} catch (Exception e) {
	    return item != null ? item.getType().name() : "";
	}
    }

    public ItemStack removeNbt(ItemStack item, String base, String path) {
	if (item == null)
	    return null;
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method methTag = nmsStack.getClass().getMethod("getTag");
	    Object tag = methTag.invoke(nmsStack);
	    if (tag == null)
		return item;

	    Method compountMeth = tag.getClass().getMethod("getCompound", String.class);
	    Object compountTag = compountMeth.invoke(tag, base);

	    if (compountTag == null)
		return item;

	    Method meth = compountTag.getClass().getMethod("remove", String.class);
	    meth.invoke(compountTag, path);

	    Method mm = tag.getClass().getMethod("set", String.class, NBTBase);
	    mm.invoke(tag, base, compountTag);

	    Method meth2 = nmsStack.getClass().getMethod("setTag", NBTTagCompound);
	    meth2.invoke(nmsStack, tag);
	    return (ItemStack) asBukkitCopy(nmsStack);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public boolean hasNbt(ItemStack item, String base) {
	if (item == null)
	    return false;
	try {
	    Object nbt = getNbt(item);
	    if (nbt == null)
		return false;
	    Method meth = nbt.getClass().getMethod("getCompound", String.class);
	    Object res = meth.invoke(nbt, base);
	    return res != null;
	} catch (Exception e) {
	    return false;
	}
    }

    public Object getNbt(ItemStack item) {
	if (item == null)
	    return null;
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method methTag = nmsStack.getClass().getMethod("getTag");
	    Object tag = methTag.invoke(nmsStack);
	    return tag;
	} catch (Exception e) {
	    return null;
	}
    }

    public Object getNbt(ItemStack item, String base, String path) {
	if (item == null)
	    return null;
	try {
	    Object nbt = getNbt(item);
	    if (nbt == null)
		return null;

	    Method compoundMeth = nbt.getClass().getMethod("getCompound", String.class);
	    Object compoundRes = compoundMeth.invoke(nbt, base);

	    if (compoundRes == null)
		return null;

	    Method meth = compoundRes.getClass().getMethod("getString", String.class);
	    Object res = meth.invoke(compoundRes, path);
	    return res;
	} catch (Exception e) {
	    return null;
	}
    }

    public ItemStack setNbt(ItemStack item, String base, String path, String value) {
	if (item == null)
	    return null;
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method methTag = nmsStack.getClass().getMethod("getTag");
	    Object tag = methTag.invoke(nmsStack);
	    if (tag == null)
		tag = NBTTagCompound.newInstance();

	    Method compountMeth = tag.getClass().getMethod("getCompound", String.class);
	    Object compountTag = compountMeth.invoke(tag, base);

	    if (compountTag == null)
		compountTag = NBTTagCompound.newInstance();

	    Method meth = compountTag.getClass().getMethod("setString", String.class, String.class);
	    meth.invoke(compountTag, path, value);

	    Method mm = tag.getClass().getMethod("set", String.class, NBTBase);
	    mm.invoke(tag, base, compountTag);

	    Method meth2 = nmsStack.getClass().getMethod("setTag", NBTTagCompound);
	    meth2.invoke(nmsStack, tag);
	    return (ItemStack) asBukkitCopy(nmsStack);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public Object asNMSCopy(ItemStack item) {
	try {
	    Method meth = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
	    return meth.invoke(CraftItemStack, item);
	} catch (Exception e) {
	    return null;
	}
    }

    public Object asBukkitCopy(Object item) {
	try {
	    Method meth = CraftItemStack.getMethod("asBukkitCopy", IStack);
	    return meth.invoke(CraftItemStack, item);
	} catch (Exception e) {
	    return null;
	}
    }

    public Object getCraftServer() {
	return CraftServer;
    }

}
