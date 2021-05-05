package com.gamingmesh.jobs.CMILib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CMINBT {

    Object tag;
    Object object;

    nmbtType type;

    public enum nmbtType {
	item, block, entity;
    }

    static {

    }

    public CMINBT(ItemStack item) {
	tag = CMIReflections.getNbt(item);
	object = item;
	type = nmbtType.item;
    }

    public Integer getInt(String path) {
	if (tag == null)
	    return null;
	if (!this.hasNBT(path))
	    return null;
	try {
	    return (Integer) tag.getClass().getMethod("getInt", String.class).invoke(tag, path);
	} catch (Exception e) {
	    return null;
	}
    }

    public Byte getByte(String path) {
	if (tag == null)
	    return null;
	if (!this.hasNBT(path))
	    return null;
	try {
	    return (Byte) tag.getClass().getMethod("getByte", String.class).invoke(tag, path);
	} catch (Exception e) {
	    return null;
	}
    }

    public Long getLong(String path) {
	if (tag == null)
	    return null;
	if (!this.hasNBT(path))
	    return null;
	try {
	    return (Long) tag.getClass().getMethod("getLong", String.class).invoke(tag, path);
	} catch (Exception e) {
	    return null;
	}
    }

    public Boolean getBoolean(String path) {
	if (tag == null)
	    return null;
	if (!this.hasNBT(path))
	    return null;
	try {
	    return (Boolean) tag.getClass().getMethod("getBoolean", String.class).invoke(tag, path);
	} catch (Exception e) {
	    return null;
	}
    }

    public String getString(String path) {
	if (tag == null)
	    return null;
	if (!this.hasNBT(path))
	    return null;
	try {

	    if (tag != null && path.contains(".")) {
		List<String> keys = new ArrayList<String>();
		keys.addAll(Arrays.asList(path.split("\\.")));
		try {
		    Object nbtbase = tag.getClass().getMethod("get", String.class).invoke(tag, keys.get(0));
		    for (int i = 1; i < keys.size(); i++) {
			if (i + 1 < keys.size()) {
			    nbtbase = nbtbase.getClass().getMethod("get", String.class).invoke(nbtbase, keys.get(i));
			} else {
			    return nbtbase != null ? (String) nbtbase.getClass().getMethod("getString", String.class).invoke(nbtbase, keys.get(i)) : null;
			}
		    }
		} catch (Throwable e) {
		}
	    }

	    return (String) tag.getClass().getMethod("getString", String.class).invoke(tag, path);
	} catch (Exception e) {
	    return null;
	}
    }

    public List<String> getList(String path) {
	if (tag == null)
	    return null;
	if (!this.hasNBT(path))
	    return null;
	List<String> list = new ArrayList<String>();
	try {
	    Object ls = tag.getClass().getMethod("getList", String.class, int.class).invoke(tag, path, 8);
	    int size = (int) ls.getClass().getMethod("size").invoke(ls);
	    Method method = ls.getClass().getMethod("get", int.class);

	    if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
		method = ls.getClass().getMethod("getString", int.class);
		for (int i = 0; i < size; i++) {
		    Object ress = method.invoke(ls, i);
		    String line = (String) ress;
		    list.add(line);
		}
	    } else {
		Object nbtbase = tag.getClass().getMethod("get", String.class).invoke(tag, path);
		Method baseMethod = nbtbase.getClass().getMethod(Version.isCurrentEqualOrLower(Version.v1_12_R1) ? "toString" : "asString");
		for (int i = 0; i < size; i++) {
		    list.add((String) baseMethod.invoke(method.invoke(ls, i)));
		}
	    }
	    return list;
	} catch (Exception e) {
	    return null;
	}
    }

    public Short getShort(String path) {
	if (tag == null)
	    return null;
	try {
	    return (Short) tag.getClass().getMethod("getShort", String.class).invoke(tag, path);
	} catch (Exception e) {
	    return null;
	}
    }

    public Object setBoolean(String path, Boolean value) {
	switch (type) {
	case block:
	    break;
	case entity:
	    break;
	case item:
	    try {
		if (value == null) {
		    Method meth = tag.getClass().getMethod("remove", String.class);
		    meth.invoke(tag, path);
		} else {
		    Method meth = tag.getClass().getMethod("setBoolean", String.class, boolean.class);
		    meth.invoke(tag, path, value);
		}
		return CMIReflections.setTag((ItemStack) object, tag);
	    } catch (Throwable e) {
		if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		    e.printStackTrace();
		return object;
	    }
	default:
	    break;
	}
	return object;
    }

    public Object setByte(String path, Byte value) {
	switch (type) {
	case block:
	    break;
	case entity:
	    break;
	case item:
	    try {
		if (value == null) {
		    Method meth = tag.getClass().getMethod("remove", String.class);
		    meth.invoke(tag, path);
		} else {
		    Method meth = tag.getClass().getMethod("setByte", String.class, byte.class);
		    meth.invoke(tag, path, value);
		}
		return CMIReflections.setTag((ItemStack) object, tag);
	    } catch (Throwable e) {
		if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		    e.printStackTrace();
		return object;
	    }
	default:
	    break;
	}
	return object;
    }

    public Object setShort(String path, Short value) {
	switch (type) {
	case block:
	    break;
	case entity:
	    break;
	case item:
	    try {
		if (value == null) {
		    Method meth = tag.getClass().getMethod("remove", String.class);
		    meth.invoke(tag, path);
		} else {
		    Method meth = tag.getClass().getMethod("setShort", String.class, short.class);
		    meth.invoke(tag, path, value);
		}
		return CMIReflections.setTag((ItemStack) object, tag);
	    } catch (Throwable e) {
		if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		    e.printStackTrace();
		return object;
	    }
	default:
	    break;
	}
	return object;
    }

    public Object setString(String path, String value) {
	switch (type) {
	case block:
	    break;
	case entity:
	    break;
	case item:
	    try {
		if (value == null) {
		    Method meth = tag.getClass().getMethod("remove", String.class);
		    meth.invoke(tag, path);
		} else {
		    Method meth = tag.getClass().getMethod("setString", String.class, String.class);
		    meth.invoke(tag, path, value);
		}
		return CMIReflections.setTag((ItemStack) object, tag);
	    } catch (Throwable e) {
		if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		    e.printStackTrace();
		return object;
	    }
	default:
	    break;
	}
	return object;
    }

    public Object setInt(String path, Integer value) {
	switch (type) {
	case block:
	    break;
	case entity:
	    break;
	case item:
	    try {

		if (value == null) {
		    Method meth = tag.getClass().getMethod("remove", String.class);
		    meth.invoke(tag, path);
		} else {
		    Method meth = tag.getClass().getMethod("setInt", String.class, int.class);
		    meth.invoke(tag, path, value);
		}
		return CMIReflections.setTag((ItemStack) object, tag);
	    } catch (Throwable e) {
		if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		    e.printStackTrace();
		return object;
	    }
	default:
	    break;
	}
	return object;
    }

    public Object setLong(String path, Long value) {
	switch (type) {
	case block:
	    break;
	case entity:
	    break;
	case item:
	    try {
		if (value == null) {
		    Method meth = tag.getClass().getMethod("remove", String.class);
		    meth.invoke(tag, path, value);
		} else {
		    Method meth = tag.getClass().getMethod("setLong", String.class, long.class);
		    meth.invoke(tag, path, value);
		}
		return CMIReflections.setTag((ItemStack) object, tag);
	    } catch (Throwable e) {
		if (Version.isCurrentEqualOrHigher(Version.v1_7_R4))
		    e.printStackTrace();
		return object;
	    }
	default:
	    break;
	}
	return object;
    }

    public boolean hasNBT() {
	return tag != null;
    }

    public boolean hasNBT(String key) {
	if (tag != null && key.contains(".")) {
	    List<String> keys = new ArrayList<String>();
	    keys.addAll(Arrays.asList(key.split("\\.")));
	    try {
		Object nbtbase = tag.getClass().getMethod("get", String.class).invoke(tag, keys.get(0));
		for (int i = 1; i < keys.size(); i++) {
		    if (i + 1 < keys.size()) {
			nbtbase = nbtbase.getClass().getMethod("get", String.class).invoke(nbtbase, keys.get(i));
		    } else {
			return nbtbase != null && (Boolean) nbtbase.getClass().getMethod("hasKey", String.class).invoke(nbtbase, keys.get(i));
		    }
		}
	    } catch (Throwable e) {
	    }
	    return false;
	}
	try {
	    return tag != null && (Boolean) tag.getClass().getMethod("hasKey", String.class).invoke(tag, key);
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	return false;
    }

    public Object getNbt() {
	return tag;
    }
}
