/**
 * Copyright (C) 2017 Zrips
 */

package com.gamingmesh.jobs.CMILib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class CMIReflections {

    //private Class<?> CraftServerClass;
    //private Object CraftServer;

    private static Class<?> NBTTagCompound;
    private static Class<?> NBTBase;
//    private Class<?> NBTTagList;

    private static Class<?> CraftItemStack;
    private static Class<?> Item;
    private static Class<?> IStack;

    static {
	try {
	    //CraftServerClass = getBukkitClass("CraftServer");
	    //CraftServer = CraftServerClass.cast(Bukkit.getServer());
	    NBTTagCompound = getMinecraftClass("NBTTagCompound");
	    NBTBase = getMinecraftClass("NBTBase");
	    /*try {
	    NBTTagList = getMinecraftClass("NBTTagList");
	    } catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
	    e.printStackTrace();
	    }*/
	    CraftItemStack = getBukkitClass("inventory.CraftItemStack");
	    try {
		Item = getMinecraftClass("Item");
	    } catch (ClassNotFoundException | SecurityException | IllegalArgumentException e) {
		e.printStackTrace();
	    }
	    IStack = getMinecraftClass("ItemStack");
	} catch (ClassCastException | ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public static String toJson(ItemStack item) {
	if (item == null)
	    return null;

	Object nmsStack = asNMSCopy(item);

	try {
	    Method meth = IStack.getMethod("save", NBTTagCompound);
	    Object res = meth.invoke(nmsStack, NBTTagCompound.newInstance());
	    return res.toString();
	} catch (Throwable e) {
	    e.printStackTrace();
	}

	return null;
    }

    private static Class<?> getBukkitClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("org.bukkit.craftbukkit." + Version.getCurrent() + "." + nmsClassString);
    }

    public static Class<?> getMinecraftClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("net.minecraft.server." + Version.getCurrent() + "." + nmsClassString);
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
	} catch (Throwable e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public ItemStack removeNbt(ItemStack item, String path) {
	if (item == null)
	    return null;
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method methTag = nmsStack.getClass().getMethod("getTag");
	    Object tag = methTag.invoke(nmsStack);
	    if (tag == null)
		return item;

	    Method meth = tag.getClass().getMethod("remove", String.class);
	    meth.invoke(tag, path);

	    Method meth2 = nmsStack.getClass().getMethod("setTag", NBTTagCompound);
	    meth2.invoke(nmsStack, tag);
	    return (ItemStack) asBukkitCopy(nmsStack);
	} catch (Throwable e) {
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
	} catch (Throwable e) {
	    return false;
	}
    }

    public boolean hasNbtString(ItemStack item, String base) {
	if (item == null)
	    return false;
	try {
	    Object nbt = getNbt(item);
	    if (nbt == null)
		return false;
	    Method meth = nbt.getClass().getMethod("getString", String.class);
	    Object res = meth.invoke(nbt, base);
	    return res != null;
	} catch (Throwable e) {
	    return false;
	}
    }

    public static Object getNbt(ItemStack item) {
	if (item == null)
	    return null;
	try {
	    Object nmsStack = asNMSCopy(item);
	    Method methTag = nmsStack.getClass().getMethod("getTag");
	    Object tag = methTag.invoke(nmsStack);
	    return tag;
	} catch (Throwable e) {
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
	} catch (Throwable e) {
	    return null;
	}
    }

    public static ItemStack setNbt(ItemStack item, String path, String value) {
	if (item == null)
	    return null;
	try {
	    Object nmsStack = asNMSCopy(item);
	    if (nmsStack == null)
		return item;
	    Method methTag = nmsStack.getClass().getMethod("getTag");
	    Object tag = methTag.invoke(nmsStack);
	    if (tag == null)
		tag = NBTTagCompound.newInstance();
	    Method meth = tag.getClass().getMethod("setString", String.class, String.class);
	    meth.invoke(tag, path, value);
	    Method meth2 = nmsStack.getClass().getMethod("setTag", NBTTagCompound);
	    meth2.invoke(nmsStack, tag);
	    return (ItemStack) asBukkitCopy(nmsStack);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public static Object getNbt(ItemStack item, String path) {
	if (item == null)
	    return null;
	try {
	    Object nbt = getNbt(item);
	    if (nbt == null)
		return null;

	    Method meth = nbt.getClass().getMethod("getString", String.class);
	    Object res = meth.invoke(nbt, path);
	    return res;
	} catch (Throwable e) {
	    return null;
	}
    }

    public static Object asNMSCopy(ItemStack item) {
	try {
	    Method meth = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
	    return meth.invoke(CraftItemStack, item);
	} catch (Throwable e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public static Object asBukkitCopy(Object item) {
	try {
	    Method meth = CraftItemStack.getMethod("asBukkitCopy", IStack);
	    return meth.invoke(CraftItemStack, item);
	} catch (Throwable e) {
	    return null;
	}
    }

    public static String getItemMinecraftName(ItemStack item) {
	try {

	    Object nmsStack = asNMSCopy(item);
	    if (nmsStack == null)
		return null;
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Object pre = nmsStack.getClass().getMethod("getItem").invoke(nmsStack);
		Object n = pre.getClass().getMethod("getName").invoke(pre);
		Class<?> ll = Class.forName("net.minecraft.server." + Version.getCurrent() + ".LocaleLanguage");
		Object lla = ll.getMethod("a").invoke(ll);
		return (String) lla.getClass().getMethod("a", String.class).invoke(lla, (String) n);
	    }

	    Field field = Item.getField("REGISTRY");
	    Object reg = field.get(field);
	    Method meth = reg.getClass().getMethod("b", Object.class);
	    meth.setAccessible(true);
	    Method secmeth = nmsStack.getClass().getMethod("getItem");
	    Object res2 = secmeth.invoke(nmsStack);
	    Object res = meth.invoke(reg, res2);
	    return res.toString();
	} catch (Exception e) {
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

    public static ItemStack getItemInOffHand(org.bukkit.entity.Player player) {
	return Version.getCurrent().isLower(Version.v1_9_R1) ? null : player.getInventory().getItemInOffHand();
    }

    public static int getEggId(ItemStack item) {
	EntityType type = getEggType(item);
	return type == null ? 0 : type.getTypeId();
    }

    public static EntityType getEggType(ItemStack item) {
	if (!CMIMaterial.isMonsterEgg(item.getType()))
	    return null;

	if (Version.isCurrentEqual(Version.v1_12_R1)) {
	    try {
		if (Version.isCurrentEqualOrLower(Version.v1_11_R1)) {
		    CMIEntityType cmiType = CMIEntityType.getById(item.getData().getData());
		    if (cmiType != null)
			return cmiType.getType();
		}
		Object tag = getNbt(item);
		Object base = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "EntityTag");
		String type = (String) base.getClass().getMethod("getString", String.class).invoke(base, "id");
		return EntityType.fromName(type.replace("minecraft:", "").toUpperCase());
	    } catch (Exception e) {
		return null;
	    }
	}

	CMIEntityType type = CMIEntityType.getByName(item.getType().toString().replace("_SPAWN_EGG", ""));
	return type == null ? null : type.getType();
    }

    public static ItemStack setEggType(ItemStack item, EntityType etype) {
	if (!item.getType().toString().contains("_EGG"))
	    return null;
	try {
	    Object tag = getNbt(item);

	    Object ttag = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "EntityTag");

	    if (ttag == null)
		ttag = NBTTagCompound.newInstance();

	    CMIEntityType ce = CMIEntityType.getByType(etype);
	    if (ce == null)
		return item;

	    ttag.getClass().getMethod("setString", String.class, String.class).invoke(ttag, "id", ce.getName());

	    tag.getClass().getMethod("set", String.class, NBTTagCompound).invoke(tag, "EntityTag", ttag);
	    setTag(item, tag);

	    return (ItemStack) asBukkitCopy(item);
	} catch (Exception e) {
	    return null;
	}
    }

    public static ItemStack setTag(ItemStack item, Object tag) {
	try {
	    Object nmsStack = asNMSCopy(item);
	    if (nmsStack == null) {
		return null;
	    }
	    Method meth2 = nmsStack.getClass().getMethod("setTag", NBTTagCompound);
	    meth2.invoke(nmsStack, tag);
	    return (ItemStack) asBukkitCopy(nmsStack);
	} catch (Throwable e) {
	    if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		e.printStackTrace();
	    return item;
	}
    }

    public static ItemStack setSkullTexture(ItemStack item, String customProfileName, String texture) {
	if (item == null)
	    return null;
	try {

	    GameProfile prof = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), null);
	    prof.getProperties().removeAll("textures");
	    prof.getProperties().put("textures", new Property("textures", texture));

//	    ItemMeta headMeta = item.getItemMeta();
	    SkullMeta headMeta = (SkullMeta) item.getItemMeta();

	    Field profileField = null;
	    try {
		profileField = headMeta.getClass().getDeclaredField("profile");
	    } catch (NoSuchFieldException | SecurityException e) {
		e.printStackTrace();
	    }
	    if (profileField != null) {
		profileField.setAccessible(true);
		try {
		    profileField.set(headMeta, prof);
		} catch (IllegalArgumentException | IllegalAccessException e) {
		    e.printStackTrace();
		}
		item.setItemMeta(headMeta);
	    }

	    Object i = new CMINBT(item).setString("Id", UUID.nameUUIDFromBytes(texture.getBytes()).toString());

	    return i == null ? null : (ItemStack) i;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
