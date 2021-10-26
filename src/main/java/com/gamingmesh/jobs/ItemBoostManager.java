package com.gamingmesh.jobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;

import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;

public final class ItemBoostManager {

    private static final Map<String, JobItems> ITEMS = new HashMap<>();
    private static final Map<String, JobItems> LEGACY = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static void load() {
	ConfigReader cfg;
	try {
	    cfg = new ConfigReader(Jobs.getInstance(), "boostedItems.yml");
	} catch (Exception e2) {
	    e2.printStackTrace();
	    return;
	}

	ITEMS.clear();
	LEGACY.clear();

	// Converting from existing records in Jobs from old format which was located in jobConfig.yml file 
	boolean save = false;
	for (Job one : Jobs.getJobs()) {
	    for (Entry<String, JobItems> oneI : one.getItemBonus().entrySet()) {
		JobItems item = oneI.getValue();

		String name = one.getName() + "_" + oneI.getKey();
		org.bukkit.inventory.ItemStack stack = item.getItemStack(null);

		cfg.getC().set(name + ".id", CMIMaterial.get(stack).toString());
		cfg.getC().set(name + ".jobs", Arrays.asList(one.getName()));
		if (stack.hasItemMeta()) {
		    cfg.getC().set(name + ".name", stack.getItemMeta().hasDisplayName() ? CMIChatColor.deColorize(stack.getItemMeta().getDisplayName()) : null);
		    cfg.getC().set(name + ".lore", stack.getItemMeta().hasLore() ? CMIChatColor.deColorize(stack.getItemMeta().getLore()) : null);
		}
		List<String> ench = new ArrayList<>();
		for (Entry<Enchantment, Integer> oneE : stack.getEnchantments().entrySet()) {
		    ench.add(CMIEnchantment.get(oneE.getKey()) + "=" + oneE.getValue());
		}
		cfg.getC().set(name + ".enchants", ench);
		for (CurrencyType oneC : CurrencyType.values()) {
		    cfg.getC().set(name + "." + oneC.toString().toLowerCase() + "Boost", ((int) (item.getBoost().get(oneC) * 100D) / 100D) + 1D);
		}
		save = true;
	    }

	    one.getItemBonus().clear();
	}

	if (save) {
	    try {
		cfg.getC().save(new File(Jobs.getFolder(), "boostedItems.yml"));
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    try {
		cfg = new ConfigReader(Jobs.getInstance(), "boostedItems.yml");
	    } catch (Exception e) {
		e.printStackTrace();
		return;
	    }
	}

	Set<String> keys = cfg.getC().getKeys(false);

	cfg.addComment("exampleBoost", "Attention! If category name has _ in it, that means its legacy item which was converted from jobConfig.yml file",
	    "Keep this format until you will be sure that all legacy items have been converted throw usage, which is automatic process when player uses items with boost in them",
	    "",
	    "Name which will be used to identify this particular item boost",
	    "This is EXAMPLE boost and will be ignored");
	cfg.addComment("exampleBoost.id", "Item Id which can be any material name as of 1.13 update",
	    "This is only used when performing command like /jobs give, but boost itself is not dependent on item type",
	    "You can use ingame command /jobs edititemboost to give particular boost to any item you are holding");
	cfg.get("exampleBoost.id", "Golden_shovel");
	cfg.addComment("exampleBoost.name", "(Optional) Item custom name", "Custom colors like &2 &5 can be used");
	cfg.get("exampleBoost.name", "&2Custom item name");
	cfg.addComment("exampleBoost.lore", "(Optional) Item custom lore", "Same as name, supports color codes");
	cfg.get("exampleBoost.lore", Arrays.asList("&2Some random", "&5Lore with some", "&7Colors"));
	cfg.addComment("exampleBoost.enchants", "(Optional) Item custom enchants",
	    "All enchantment names can be found https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html");
	cfg.get("exampleBoost.enchants", Arrays.asList("FIRE_ASPECT=1", "DAMAGE_ALL=1"));
	cfg.addComment("exampleBoost.leather-color", "(Optional) Leather armour colors (0-255)");
	cfg.get("exampleBoost.leather-color", "82,34,125");
	cfg.addComment("exampleBoost.moneyBoost", "[Required] Money boost: 1.1 is equals 10% more income when 0.9 is equals 10% less from base income");
	for (CurrencyType oneC : CurrencyType.values()) {
	    cfg.get("exampleBoost." + oneC.toString().toLowerCase() + "Boost", 1D);
	}
	cfg.addComment("exampleBoost.jobs", "[Required] Jobs which should receive this boost",
	    "Can be specific jobs or use 'all' to give this boost for every job");
	cfg.get("exampleBoost.jobs", Arrays.asList("Miner", "Woodcutter", "all"));

	cfg.addComment("exampleBoost.levelFrom", "(Optional) Defines level of job from which this boost should be applied",
	    "Keep in mind that if boost have multiple jobs, then level will be checked by job which is requesting boost value");
	cfg.get("exampleBoost.levelFrom", 0);
	cfg.addComment("exampleBoost.levelUntil", "(Optional) Defines level of job until which this boost should be applied");
	cfg.get("exampleBoost.levelUntil", 50);

	for (String one : keys) {
	    if (!cfg.getC().isConfigurationSection(one))
		continue;

	    // Ignoring example boost
	    if (one.equalsIgnoreCase("exampleBoost"))
		continue;

	    List<Job> jobs = new ArrayList<>();
	    List<String> j = cfg.get(one + ".jobs", Arrays.asList(""));

	    if (j.contains("all")) {
		jobs.addAll(Jobs.getJobs());
	    } else {
		for (String oneJ : j) {
		    Job job = Jobs.getJob(oneJ);

		    if (job != null) {
			jobs.add(job);
		    } else {
			Jobs.getPluginLogger().warning("Cant determine job by " + oneJ + " name for " + one + " boosted item!");
		    }
		}
	    }

	    if (jobs.isEmpty()) {
		Jobs.getPluginLogger().warning("Jobs list is empty for " + one + " boosted item!");
		continue;
	    }

	    List<String> lore = cfg.get(one + ".lore", Arrays.asList(""));
	    for (int a = 0; a < lore.size(); a++) {
		lore.set(a, CMIChatColor.translate(lore.get(a)));
	    }

	    Map<Enchantment, Integer> enchants = new HashMap<>();
	    if (cfg.getC().isList(one + ".enchants"))
		for (String eachLine : cfg.get(one + ".enchants", Arrays.asList(""))) {
		    String[] split = eachLine.split("=", 2);
		    if (split.length == 0)
			continue;

		    Enchantment ench = CMIEnchantment.getEnchantment(split[0]);
		    int level = -1;

		    if (split.length > 1) {
			try {
			    level = Integer.parseInt(split[1]);
			} catch (NumberFormatException e) {
			    continue;
			}
		    }

		    if (ench != null && level != -1)
			enchants.put(ench, level);
		}

	    BoostMultiplier b = new BoostMultiplier();
	    for (CurrencyType oneC : CurrencyType.values()) {
		String typeName = oneC.toString().toLowerCase();

		if (cfg.getC().isDouble(one + "." + typeName + "Boost"))
		    b.add(oneC, cfg.get(one + "." + typeName + "Boost", 1D) - 1);
	    }

	    CMIMaterial mat = cfg.getC().isString(one + ".id") ? CMIMaterial.get(cfg.get(one + ".id", "Stone")) : null;

	    String name = cfg.getC().isString(one + ".name") ? cfg.get(one + ".name", "") : null;
	    String node = one.toLowerCase();

	    Color leatherColor = null;
	    String lc = cfg.getC().getString(one + ".leather-color", "");
	    if (!lc.isEmpty()) {
		String[] split = lc.split(",", 3);

		if (split.length != 0) {
		    int red = Integer.parseInt(split[0]);
		    int green = split.length > 0 ? Integer.parseInt(split[1]) : 0;
		    int blue = split.length > 1 ? Integer.parseInt(split[2]) : 0;

		    try {
			leatherColor = Color.fromRGB(red, green, blue);
		    } catch (IllegalArgumentException e) {
		    }
		}
	    }

	    JobItems item = new JobItems(node, mat, 1, name, lore, enchants, b, jobs, null, leatherColor);

	    if (cfg.getC().isInt(one + ".levelFrom"))
		item.setFromLevel(cfg.get(one + ".levelFrom", 0));

	    if (cfg.getC().isInt(one + ".levelUntil"))
		item.setUntilLevel(cfg.get(one + ".levelUntil", 1000));

	    for (Job oneJ : jobs) {
		oneJ.getItemBonus().put(node, item);
	    }

	    // Lets add into legacy map
	    String[] split = one.split("_", 2);
	    if (split.length > 1) {
		item.setLegacyKey(split[1].toLowerCase());
		LEGACY.put(item.getLegacyKey(), item);
	    }

	    ITEMS.put(node, item);
	}

	cfg.save();
	Jobs.consoleMsg("&eLoaded &6" + ITEMS.size() + " &eboosted items");
    }

    /**
     * Returns a copy list of {@link JobItems} from the specific job.
     * 
     * @param job {@link Job}
     * @return List of {@link JobItems}
     */
    public static List<JobItems> getItemsByJob(Job job) {
	List<JobItems> ls = new ArrayList<>();
	for (JobItems one : ITEMS.values()) {
	    if (one.getJobs().contains(job))
		ls.add(one);
	}

	return ls;
    }

    /** Returns a map of items from the specific job.
     * 
     * @param job {@link Job}
     * @return map of items
     */
    public static Map<String, JobItems> getItemsMapByJob(Job job) {
	Map<String, JobItems> i = new HashMap<>();
	for (Entry<String, JobItems> one : ITEMS.entrySet()) {
	    if (one.getValue().getJobs().contains(job))
		i.put(one.getKey(), one.getValue());
	}

	return i;
    }

    /**
     * Returns {@link JobItems} from specific key.
     * 
     * @param key items or legacy key name
     * @return {@link JobItems}
     */
    public static JobItems getItemByKey(String key) {
	key = key.toLowerCase();

	JobItems item = ITEMS.get(key);
	return item != null ? item : LEGACY.get(key);
    }

    /**
     * @return the current cached map of items.
     */
    public static Map<String, JobItems> getItems() {
	return ITEMS;
    }

    /**
     * @return the current cached map of legacy items.
     */
    public static Map<String, JobItems> getLegacyItems() {
	return LEGACY;
    }
}
