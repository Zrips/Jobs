package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ItemManager {

    static HashMap<Material, CMIMaterial> byRealMaterial = new HashMap<Material, CMIMaterial>();
    static HashMap<Integer, CMIMaterial> byId = new HashMap<Integer, CMIMaterial>();
    static HashMap<String, CMIMaterial> byName = new HashMap<String, CMIMaterial>();

    public HashMap<Integer, CMIMaterial> idMap() {
	return byId;
    }

    public HashMap<String, CMIMaterial> NameMap() {
	return byName;
    }

    static {
	for (CMIMaterial one : CMIMaterial.values()) {
	    if (one == null)
		continue;
	    one.updateMaterial();
	    Material mat = one.getMaterial();
	    if (mat == null) {
		continue;
	    }

	    Integer id = one.getId();
	    short data = one.getLegacyData();
	    Integer legacyId = one.getLegacyId();
	    String cmiName = one.getName().replace("_", "").replace(" ", "").toLowerCase();
	    String materialName = one.toString().replace("_", "").replace(" ", "").toLowerCase();

	    String mojangName = null;
	    try {
		mojangName = ItemReflection.getItemMinecraftName(new ItemStack(mat));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    mojangName = mojangName == null ? mat.toString().replace("_", "").replace(" ", "").toLowerCase()
			: mojangName.replace("_", "").replace(" ", "").toLowerCase();

	    if (byName.containsKey(cmiName)) {
		byName.put(cmiName + ":" + data, one);
	    } else
		byName.put(cmiName, one);

	    if (byName.containsKey(materialName))
		byName.put(materialName + ":" + data, one);
	    else
		byName.put(materialName, one);

	    if (!one.getLegacyNames().isEmpty()) {
		for (String oneL : one.getLegacyNames()) {
		    String legacyName = oneL.replace("_", "").replace(" ", "").toLowerCase();
		    if (byName.containsKey(legacyName) || data > 0)
			byName.put(legacyName + ":" + data, one);
		    else
			byName.put(legacyName, one);
		}
	    }

	    if (byName.containsKey(mojangName))
		byName.put(mojangName + ":" + data, one);
	    else
		byName.put(mojangName, one);

	    if (byName.containsKey(String.valueOf(id)) || data > 0)
		byName.put(id + ":" + data, one);
	    else
		byName.put(String.valueOf(id), one);

	    if (byName.containsKey(String.valueOf(legacyId)) || data > 0)
		byName.put(legacyId + ":" + data, one);
	    else
		byName.put(String.valueOf(legacyId), one);

	    if (!byId.containsKey(id))
		byId.put(id, one);
	    if (!byId.containsKey(one.getLegacyId()))
		byId.put(one.getLegacyId(), one);
	    if (one.getLegacyData() == 0)
		byId.put(one.getLegacyId(), one);
	    byRealMaterial.put(mat, one);

	}
//	plugin.consoleMessage("Loaded " + byName2.size() + " " + byId2.size() + " " + byRealMaterial2.size()); 

//	for (CMIMaterial one : CMIMaterial.values()) {
//	    if (one == null)
//		continue;
//	    one.updateMaterial();
//	    Material mat = one.getMaterial();
//
//	    if (mat == null) {
//		continue;
//	    }
//
//	    Integer id = one.getId();
//	    short data = one.getLegacyData();
//	    Integer legacyId = one.getLegacyId();
//
//	    String bukkitName = one.name();
//	    String mojangName = one.name();
////	    String realName = plugin.getRef().getItemRealName(new ItemStack(mat));
//	    try {
//		mojangName = plugin.getRef().getItemMinecraftName(new ItemStack(mat));
//	    } catch (Exception e) {
//		e.printStackTrace();
//	    }
//
//	    mojangName = mojangName == null ? mat.toString() : mojangName;
//	    CMIItemStack cm = new CMIItemStack(one);
//
//	    cm.setId(id);
//	    cm.setData(data > 0 ? data : 0);
//
//	    cm.setBukkitName(bukkitName);
//	    cm.setMojangName(mojangName);
//
//	    byBukkitName.put(bukkitName + ":" + cm.getData(), cm);
//	    byBukkitName.put(id + ":" + cm.getData(), cm);
//	    byBukkitName.put(legacyId + ":" + cm.getData(), cm);
//	    if (!one.getLegacyNames().isEmpty()) {
//		byBukkitName.put(one.getLegacyName().toLowerCase().replace("_", "").replace(" ", ""), cm);
//	    }
//	    byBukkitName.put(one.name().toLowerCase().replace("_", "").replace(" ", ""), cm);
//
//	    String n = mojangName.toLowerCase().replace("_", "").replace(" ", "").replace("minecraft:", "");
//
//	    if (!byMojangName.containsKey(n))
//		byMojangName.put(n, cm);
//	    byMaterial.put(one, cm);
//	    if (!byId.containsKey(id))
//		byId.put(id, cm);
//	    if (!byId.containsKey(one.getLegacyId()))
//		byId.put(one.getLegacyId(), cm);
//	    if (one.getLegacyData() == 0)
//		byId.put(one.getLegacyId(), cm);
//	    byRealMaterial.put(mat, one);
//	}
//
//	for (Material one : Material.class.getEnumConstants()) {
//	    CMIMaterial mat = CMIMaterial.get(one);
//	    if (mat == CMIMaterial.NONE && !one.toString().startsWith("LEGACY_")) {
//		CMIItemStack cm = new CMIItemStack(new ItemStack(one));
//		if (Version.isCurrentEqualOrLower(Version.v1_13_R2))
//		    cm.setId(one.getId());
//		cm.setBukkitName(one.name());
////		String realName = plugin.getRef().getItemRealName(new ItemStack(one));
//
//		if (Version.isCurrentEqualOrLower(Version.v1_13_R2))
//		    byBukkitName.put(one.getId() + ":" + cm.getData(), cm);
//		byBukkitName.put(one.name().toLowerCase().replace("_", "").replace(" ", ""), cm);
//		String mojangName = one.name();
//		try {
//		    mojangName = plugin.getRef().getItemMinecraftName(new ItemStack(one));
//		} catch (Exception e) {
//		}
//		mojangName = mojangName == null ? mat.toString() : mojangName;
//		cm.setMojangName(mojangName);
//		String n = mojangName.toLowerCase().replace("_", "").replace(" ", "").replace("minecraft:", "");
//		if (!byMojangName.containsKey(n))
//		    byMojangName.put(n, cm);
//		if (Version.isCurrentEqualOrLower(Version.v1_13_R2))
//		    if (!byId.containsKey(one.getId()))
//			byId.put(one.getId(), cm);
//		byRealMaterial.put(one, mat);
//	    }
//	}

    }

    @Deprecated
    public CMIItemStack getItem(Material mat) {
	CMIMaterial cmat = CMIMaterial.get(mat);
	if (cmat == null || cmat.equals(CMIMaterial.NONE))
	    return null;
	return new CMIItemStack(cmat);
    }

    public static CMIItemStack getItem(CMIMaterial mat) {
	if (mat == null || mat.equals(CMIMaterial.NONE))
	    return null;
	return new CMIItemStack(mat);
    }

    public static CMIItemStack getItem(ItemStack item) {
	if (item == null)
	    item = new ItemStack(Material.AIR);
	CMIItemStack cm = getItem(CMIMaterial.get(item));
	if (cm == null)
	    return new CMIItemStack(Material.AIR);
	cm.setItemStack(item);
	return cm;
    }

    HashMap<String, ItemStack> headCache = new HashMap<String, ItemStack>();

    public CMIItemStack getItem(String name) {
//	if (byBukkitName.isEmpty())
//	    load();
	CMIItemStack cm = null;
	name = name.toLowerCase().replace("minecraft:", "");
	String original = name;
	name = name.replace("_", "");
	Integer amount = null;

	String subdata = null;
	if (name.contains(":")) {
	    CMIMaterial mat = byName.get(name);
	    if (mat != null)
		return new CMIItemStack(mat);
	    subdata = name.split(":")[1];
	}

	if (name.contains("-")) {
	    String a = name.split("-")[1];
	    try {
		amount = Integer.parseInt(a);
	    } catch (NumberFormatException e) {
	    }
	    name = name.split("-")[0];
	}

	if (name.contains(">")) {
	    String a = name.split(">")[1];
	    try {
		amount = Integer.parseInt(a);
	    } catch (NumberFormatException e) {
	    }
	    name = name.split(">")[0];
	}

	short data = -999;

	if (name.contains(":")) {
	    try {
		data = (short) Integer.parseInt(name.split(":")[1]);
	    } catch (Exception e) {
	    }
	    try {
		CMIEntityType e = CMIEntityType.getByName(name.split(":")[1]);
		if (e != null)
		    data = (short) e.getId();
	    } catch (Exception e) {
	    }
	    name = name.split(":")[0];
	}

	switch (name.toLowerCase()) {
	case "skull":
	    cm = CMIMaterial.SKELETON_SKULL.newCMIItemStack();
	    break;
	case "door":
	    cm = CMIMaterial.SPRUCE_DOOR.newCMIItemStack();
	    break;
	case "head":
	    cm = CMIMaterial.PLAYER_HEAD.newCMIItemStack();
	    data = 3;

	    main: if (original.contains(":")) {

		ItemStack old = headCache.get(original);
		if (old != null) {
		    cm.setItemStack(old);
		} else {
		    String d = original.split(":")[1];
		    ItemStack skull = CMIMaterial.PLAYER_HEAD.newItemStack();
		    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		    if (d.length() == 36) {
			try {
			    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(d));
			    skullMeta.setOwningPlayer(offPlayer);
			} catch (Exception e) {
			    break main;
			}
			skull.setItemMeta(skullMeta);
		    } else {
			skullMeta.setOwner(d);
			skull.setItemMeta(skullMeta);
		    }
		    headCache.put(original, skull);
		    cm.setItemStack(skull);
		}
	    }

	    break;
	default:
	    break;
	}

	CMIMaterial cmat = CMIMaterial.get(subdata == null ? name : name + ":" + subdata);
	if (cmat == null || cmat.equals(CMIMaterial.NONE)) {
	    cmat = CMIMaterial.get(name);
	}

	if (cmat != null && !cmat.equals(CMIMaterial.NONE)) {
	    cm = cmat.newCMIItemStack();
	} else
	    cmat = CMIMaterial.get(subdata == null ? original : original + ":" + subdata);

	if (cmat != null && !cmat.equals(CMIMaterial.NONE))
	    cm = cmat.newCMIItemStack();

//	main: if (cm == null) {
//	    cm = byBukkitName.get(name);
//	    if (subdata != null && cm != null) {
//		cm = byBukkitName.get(cm.getCMIType() + ":" + subdata);
//		if (cm != null)
//		    break main;
//	    }
//	    cm = byBukkitName.get(name);
//
//	    if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
//		if (cm != null && data != -999) {
//		    CMIMaterial t = CMIMaterial.get(cm.getCMIType().getLegacyId(), data);
//		    if (t != null) {
//			cm = byMaterial.get(t);
//		    }
//		}
//	    }
//
//	    if (data != -999) {
//		try {
//		    cm = byId.get(Integer.parseInt(name));
//		    CMIMaterial t = CMIMaterial.get(original);
//		    if (t != null) {
//			cm = this.byMaterial.get(t);
//		    }
//		} catch (Exception e) {
//		}
//	    }
//
//	    if (cm == null) {
//		try {
//		    cm = byId.get(Integer.parseInt(name));
//		} catch (Exception e) {
//		}
//
//		if (cm == null) {
//		    cm = byMojangName.get(name);
//		    if (cm == null) {
//			for (Material one : Material.class.getEnumConstants()) {
//			    if (one.name().replace("_", "").equalsIgnoreCase(name)) {
//				cm = byMaterial.get(CMIMaterial.get(one));
//				break;
//			    }
//			}
//			if (cm == null) {
//			    for (CMIMaterial one : CMIMaterial.values()) {
//				if (one.getName().replace(" ", "").equalsIgnoreCase(name)) {
//				    cm = byMaterial.get(one);
//				    if (cm != null && data == -999) {
//					data = one.getData();
//				    }
//				    break;
//				}
//			    }
//			    if (cm == null) {
//				for (CMIMaterial one : CMIMaterial.values()) {
//				    if (one.getName().replace(" ", "").toLowerCase().startsWith(name)) {
//					cm = byMaterial.get(one);
//					if (cm != null && data == -999) {
//					    data = one.getData();
//					}
//					break;
//				    }
//				}
//			    }
//			    if (cm == null) {
//				for (CMIMaterial one : CMIMaterial.values()) {
//				    if (one.getName().replace(" ", "").toLowerCase().contains(name)) {
//					cm = byMaterial.get(one);
//					if (cm != null && data == -999) {
//					    data = one.getData();
//					}
//					break;
//				    }
//				}
//			    }
//			    if (cm == null) {
//				for (Entry<String, CMIItemStack> one : byMojangName.entrySet()) {
//				    if (one.getKey().contains(name)) {
//					cm = one.getValue();
//					if (cm != null && data == -999) {
//					    data = one.getValue().getData();
//					}
//					break;
//				    }
//				}
//			    }
//			}
//		    }
////		    }
//		}
//	    }
//	}

	CMIItemStack ncm = null;
	if (cm != null)
	    ncm = cm.clone();

	if (ncm != null && data != -999) {
	    if (ncm.getMaxDurability() > 15)
		ncm.setData((short) 0);
	    else {
		ncm.setData(data);
	    }
	}
	if (ncm != null && amount != null)
	    ncm.setAmount(amount);

	if (ncm != null && subdata != null) {
	    if (ncm.getCMIType().isPotion() || ncm.getCMIType().equals(CMIMaterial.SPLASH_POTION)
			|| ncm.getCMIType().equals(CMIMaterial.TIPPED_ARROW)) {
		Integer d = null;
		PotionEffectType type = null;
		Boolean upgraded = false;
		Boolean extended = false;
		String[] split = subdata.split("-");
		try {
		    d = Integer.parseInt(split.length > 0 ? split[0] : subdata);
		    type = PotionEffectType.getById(d);
		} catch (Exception e) {
		}
		try {
		    String n = (split.length > 0 ? split[0] : subdata).replace("_", "");
		    for (PotionEffectType one : PotionEffectType.values()) {
			if (one == null)
			    continue;
			if (n.equalsIgnoreCase(one.getName().replace("_", ""))) {
			    type = one;
			    break;
			}
		    }

		    if (split.length > 1) {
			upgraded = Boolean.parseBoolean(split[1]);
		    }
		    if (split.length > 2) {
			extended = Boolean.parseBoolean(split[2]);
		    }
		    ItemStack item = ncm.getItemStack();
		    if (extended && upgraded)
			extended = false;
		    PotionMeta meta = (PotionMeta) item.getItemMeta();
		    meta.setBasePotionData(new PotionData(PotionType.getByEffect(type), extended, upgraded));
		    item.setItemMeta(meta);
		} catch (Exception e) {
		    e.printStackTrace();
		}

	    }
	}

	return ncm;
    }

    public List<Recipe> getAllRecipes() {
	List<Recipe> results = new ArrayList<Recipe>();
	Iterator<Recipe> iter = Bukkit.recipeIterator();
	while (iter.hasNext()) {
	    Recipe recipe = iter.next();
	    results.add(recipe);
	}
	return results;
    }

    public List<Recipe> getRecipesFor(ItemStack result) {
	List<Recipe> results = new ArrayList<Recipe>();
	Iterator<Recipe> iter = Bukkit.recipeIterator();
	while (iter.hasNext()) {
	    Recipe recipe = iter.next();
	    ItemStack stack = recipe.getResult();
	    if (stack.getType() != result.getType()) {
		continue;
	    }
	    if (result.getDurability() == -1 || result.getDurability() == stack.getDurability()) {
		results.add(recipe);
	    }
	}
	return results;
    }

    public Material getMaterial(String name) {
	CMIItemStack cm = getItem(name);
	if (cm == null)
	    return Material.AIR;
	return cm.getType();
    }

//    public CMIMaterial getRealName(CMIItemStack item) {
//	return getRealName(item, false);
//    }
//
//    public CMIMaterial getRealName(CMIItemStack item, boolean safe) {
//
////	for (CMIMaterial one : CMIMaterial.values()) {
////	    if (one.getId() == item.getId() && one.getDataList().contains(item.getData()))
////		return one;
////	}
////	if (safe)
////	    for (CMIMaterial one : CMIMaterial.values()) {
////		if (one.getId() == item.getId())
////		    return one;
////	    }
////	return safe ? CMIMaterial.AIR : null;
//	return item.getRealName();
//    }

//    private static CMIMaterialLegacy proccessItemName(CMIMaterialLegacy one) {
//	if (one.getName().contains("[colorNames]"))
//	    one.setName(one.getName().replace("[colorNames]", colorNames.getById(one.getData()).getName()));
//	else if (one.getName().contains("[entityNames]"))
//	    one.setName(one.getName().replace("[entityNames]", CMIEntityType.getById(one.getData()).getName()));
//	return one;
//    }
    public enum SlabType {
	TOP,
	BOTTOM,
	DOUBLE,
	NOTSLAB;
    }
}
