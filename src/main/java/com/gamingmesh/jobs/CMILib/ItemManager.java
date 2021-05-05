package com.gamingmesh.jobs.CMILib;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.gamingmesh.jobs.Jobs;

public class ItemManager {

    static HashMap<Material, CMIMaterial> byRealMaterial = new HashMap<>();
    static HashMap<Integer, CMIMaterial> byId = new HashMap<>();
    static HashMap<String, CMIMaterial> byName = new HashMap<>();

    public HashMap<Integer, CMIMaterial> idMap() {
	return byId;
    }

    public HashMap<String, CMIMaterial> NameMap() {
	return byName;
    }

    static {
	byRealMaterial.clear();
	for (CMIMaterial one : CMIMaterial.values()) {
	    if (one == null)
		continue;

	    // Ignoring legacy materials on new servers
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1) && one.isLegacy()) {
		continue;
	    }

	    one.updateMaterial();
	    Material mat = one.getMaterial();

	    if (mat == null) {
		continue;
	    }

//	    Integer id = one.getId();
	    short data = one.getLegacyData();
	    Integer legacyId = one.getLegacyId();
	    String cmiName = one.getName().replace("_", "").replace(" ", "").toLowerCase();
	    String materialName = one.toString().replace("_", "").replace(" ", "").toLowerCase();

	    String mojangName = null;
	    try {
		if (Version.isCurrentEqualOrLower(Version.v1_14_R1) || mat.isItem())
		    mojangName = CMIReflections.getItemMinecraftName(new ItemStack(mat));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (mojangName == null) {
		mojangName = mat.toString();
	    }

	    mojangName = mojangName == null ? mat.toString().replace("_", "").replace(" ", "").toLowerCase() : mojangName.replace("_", "").replace(" ", "").toLowerCase();

	    if (one.isCanHavePotionType()) {
		for (PotionType potType : PotionType.values()) {
		    byName.put(cmiName + ":" + potType.toString().toLowerCase(), one);
		}
	    }
	    if (byName.containsKey(cmiName) && Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
		byName.put(cmiName + ":" + data, one);
	    } else
		byName.put(cmiName, one);

	    byName.put(materialName, one);
	    if (Version.isCurrentEqualOrLower(Version.v1_13_R1) && !byName.containsKey(cmiName + ":" + data))
		byName.put(cmiName + ":" + data, one);

	    if (!one.getLegacyNames().isEmpty()) {
		for (String oneL : one.getLegacyNames()) {
		    String legacyName = oneL.replace("_", "").replace(" ", "").toLowerCase();
		    if (Version.isCurrentEqualOrLower(Version.v1_13_R1) && (byName.containsKey(legacyName) || data > 0)) {
			byName.put(legacyName + ":" + data, one);
		    }
		    byName.put(legacyName, one);
		}
	    }

	    if (byName.containsKey(mojangName) && Version.isCurrentEqualOrLower(Version.v1_13_R1))
		byName.put(mojangName + ":" + data, one);
	    else
		byName.put(mojangName, one);

	    if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
		Integer id = one.getId();
		if (byName.containsKey(String.valueOf(id)) || data > 0)
		    byName.put(id + ":" + data, one);
		else
		    byName.put(String.valueOf(id), one);
		if (!byId.containsKey(id))
		    byId.put(id, one);
		if (!byId.containsKey(one.getLegacyId()))
		    byId.put(one.getLegacyId(), one);
		if (one.getLegacyData() == 0)
		    byId.put(one.getLegacyId(), one);
		if (byName.containsKey(String.valueOf(legacyId)) || data > 0)
		    byName.put(legacyId + ":" + data, one);
		else
		    byName.put(String.valueOf(legacyId), one);
	    }

	    byRealMaterial.put(mat, one);
	}
    }

    @Deprecated
    public CMIItemStack getItem(Material mat) {
	CMIMaterial cmat = CMIMaterial.get(mat);
	return cmat == null || cmat == CMIMaterial.NONE ? null : new CMIItemStack(cmat);
    }

    public static CMIItemStack getItem(CMIMaterial mat) {
	return mat == null || mat == CMIMaterial.NONE ? null : new CMIItemStack(mat);
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

    HashMap<String, ItemStack> headCache = new HashMap<>();

    public CMIItemStack getItem(String name) {
	return getItem(name, null);
    }

    public CMIItemStack getItem(String name, CMIAsyncHead ahead) {
	if (name == null)
	    return null;
//	if (byBukkitName.isEmpty())
//	    load(); 
	CMIItemStack cm = null;
	String original = name.replace("minecraft:", "");
	name = name.toLowerCase().replace("minecraft:", "");
	name = name.replace("_", "");
	Integer amount = null;
	CMIEntityType entityType = null;

	String tag = null;

	if (name.contains("{") && name.contains("}")) {
	    Pattern ptr = Pattern.compile("(\\{).+(\\})");
	    Matcher match = ptr.matcher(name);
	    if (match.find()) {
		tag = match.group();
		name = name.replace(match.group(), "");
	    }
	    name = name.replace("  ", " ");
	}

	String subdata = null;
	if (name.contains(":")) {
	    CMIMaterial mat = byName.get(name);
	    if (mat != null)
		return new CMIItemStack(mat);
	    try {
		subdata = name.split(":")[1];
	    } catch (Throwable e) {

	    }
	}

	if (name.contains("-")) {
	    String[] split = name.split("-");
	    if (split.length > 1) {
		String a = name.split("-")[1];
		try {
		    amount = Integer.parseInt(a);
		} catch (Exception e) {
		}
	    }
	    name = name.split("-")[0];
	}

	if (name.contains(">")) {
	    String[] split = name.split(">");
	    if (split.length > 1) {
		String a = name.split(">")[1];
		try {
		    amount = Integer.parseInt(a);
		} catch (Exception e) {
		}
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
		entityType = CMIEntityType.getByName(name.split(":")[1]);
		if (entityType != null)
		    data = (short) entityType.getId();
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

	    if (original.contains(":")) {
		ItemStack old = headCache.get(original);
		if (old != null) {
		    cm.setItemStack(old);
		} else {
		    String[] split = original.split(":");
		    if (split.length > 1) {
			String d = split[1];

			if (d.length() > 36 || d.startsWith("eyJ0ZXh0dXJlcy")) {
			    ItemStack skull = CMIItemStack.getHead(d);
			    headCache.put(original, skull);
			    cm.setItemStack(skull);
			} else {
			    ItemStack skull = CMIMaterial.PLAYER_HEAD.newItemStack();
			    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
			    if (d.length() == 36) {
				try {
				    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(d));
				    skullMeta.setOwningPlayer(offPlayer);
				    skull.setItemMeta(skullMeta);
				} catch (Exception e) {
				}
			    } else {
				if (Version.isCurrentEqualOrHigher(Version.v1_16_R3)) {

				    if ((ahead != null && !ahead.isForce() || ahead == null) && Bukkit.getPlayer(d) != null) {
					Player player = Bukkit.getPlayer(d);
					skullMeta.setOwningPlayer(player);
					skull.setItemMeta(skullMeta);
					headCache.put(original, skull);
				    } else {

					if (ahead != null) {
					    ahead.setAsyncHead(true);
					}
					Bukkit.getScheduler().runTaskAsynchronously(Jobs.getInstance(), () -> {
					    OfflinePlayer offlineP = Bukkit.getOfflinePlayer(d);
					    if (offlineP != null) {
						skullMeta.setOwningPlayer(offlineP);
						skull.setItemMeta(skullMeta);
						headCache.put(original, skull);

						// Forcing server to load skin information
						Bukkit.createInventory(null, InventoryType.CHEST, "").addItem(skull);

						skull.setItemMeta(skullMeta);
						headCache.put(original, skull);

						if (ahead != null)
						    ahead.afterAsyncUpdate(skull);
					    }
					});
				    }

				} else {
				    skullMeta.setOwner(d);
				    skull.setItemMeta(skullMeta);
				}

				if (ahead == null || !ahead.isAsyncHead()) {
				    skull.setItemMeta(skullMeta);
				    headCache.put(original, skull);
				}
			    }
			}
		    }
		}
	    }
	    break;
	case "map":
	    cm = CMIMaterial.FILLED_MAP.newCMIItemStack();

	    if (original.contains(":") && data > 0) {
		ItemStack stack = cm.getItemStack();
		MapMeta map = (MapMeta) stack.getItemMeta();
		map.setMapId(data);
		stack.setItemMeta(map);
		cm.setItemStack(stack);
		return cm;
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

	if (cm == null) {
	    try {
		Material match = Material.matchMaterial(original);
		if (match != null) {
		    if (Version.isCurrentLower(Version.v1_13_R1) || !CMIMaterial.get(match).isLegacy() && CMIMaterial.get(match) != CMIMaterial.NONE) {
			cm = new CMIItemStack(match);
		    }
		}
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	}

	if (cm != null && entityType != null)
	    cm.setEntityType(entityType);

	CMIItemStack ncm = null;
	if (cm != null)
	    ncm = cm.clone();

	if (ncm != null && data != -999) {
	    if (ncm.getMaxDurability() > 15) {
		ncm.setData((short) 0);
	    } else {
		ncm.setData(data);
	    }
	}

//	if (ncm != null && tag != null) {
//	    ncm.setTag(CMIChatColor.translate(tag));
//	}

	if (ncm != null && amount != null)
	    ncm.setAmount(amount);

	if (ncm != null && subdata != null) {
	    if (ncm.getCMIType().isPotion() || ncm.getCMIType() == CMIMaterial.SPLASH_POTION
		|| ncm.getCMIType() == CMIMaterial.TIPPED_ARROW) {
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

    public Material getMaterial(String name) {
	CMIItemStack cm = getItem(name);
	return cm == null ? Material.AIR : cm.getType();
    }
}
