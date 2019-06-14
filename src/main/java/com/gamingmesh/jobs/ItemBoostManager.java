package com.gamingmesh.jobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;

import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIEnchantment;
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;

public class ItemBoostManager {

    private static HashMap<String, JobItems> items = new HashMap<>();
    private static HashMap<String, JobItems> legacy = new HashMap<>();

    public static void load() {

	ConfigReader cfg = null;
	try {
	    cfg = new ConfigReader("boostedItems.yml");
	} catch (Throwable e) {
	    e.printStackTrace();
	}

	if (cfg == null)
	    return;
	items.clear();
	legacy.clear();
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
	    cfg = null;
	    try {
		cfg = new ConfigReader("boostedItems.yml");
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	    if (cfg == null)
		return;
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

	    // Ignoring example job
	    if (one.equalsIgnoreCase("exampleBoost"))
		continue;

	    CMIMaterial mat = null;

	    if (cfg.getC().isString(one + ".id")) {
		mat = CMIMaterial.get(cfg.get(one + ".id", "Stone"));
	    }

	    String name = null;

	    if (cfg.getC().isString(one + ".name")) {
		name = cfg.get(one + ".name", "");
	    }

	    List<String> lore = new ArrayList<>();
	    if (cfg.getC().contains(one + ".lore") && !cfg.getC().getStringList(one + ".lore").isEmpty()) {
		for (String eachLine : cfg.get(one + ".lore", Arrays.asList(""))) {
		    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
		}
	    }

	    HashMap<Enchantment, Integer> enchants = new HashMap<>();
	    if (cfg.getC().contains(one + ".enchants") && !cfg.getC().getStringList(one + ".enchants").isEmpty())
		for (String eachLine : cfg.get(one + ".enchants", Arrays.asList(""))) {
		    if (!eachLine.contains("="))
			continue;
		    Enchantment ench = CMIEnchantment.getEnchantment(eachLine.split("=")[0]);
		    Integer level = -1;
		    try {
			level = Integer.parseInt(eachLine.split("=")[1]);
		    } catch (NumberFormatException e) {
			continue;
		    }
		    if (ench != null && level != -1)
			enchants.put(ench, level);
		}

	    BoostMultiplier b = new BoostMultiplier();
	    for (CurrencyType oneC : CurrencyType.values()) {
		if (cfg.getC().isDouble(one + "." + oneC.toString().toLowerCase() + "Boost"))
		    b.add(oneC, cfg.get(one + "." + oneC.toString().toLowerCase() + "Boost", 1D) - 1);
	    }

	    List<String> jobsS = cfg.get(one + ".jobs", Arrays.asList(""));

	    List<Job> jobs = new ArrayList<>();
	    for (String oneJ : jobsS) {
		Job job = Jobs.getJob(oneJ);
		if (job == null && !oneJ.equalsIgnoreCase("all")) {
		    Jobs.getPluginLogger().warning("Cant determine job by " + oneJ + " name for " + one + " boosted item!");
		    continue;
		}
		if (oneJ.equalsIgnoreCase("all"))
		    jobs.addAll(Jobs.getJobs());
		else if (job != null)
		    jobs.add(job);
	    }

	    if (jobs.isEmpty()) {
		Jobs.getPluginLogger().warning("Jobs list is empty for " + one + " boosted item!");
		continue;
	    }
	    JobItems item = new JobItems(one.toLowerCase(), mat, 1, name, lore, enchants, b, jobs);

	    if (cfg.getC().isInt(one + ".levelFrom"))
		item.setFromLevel(cfg.get(one + ".levelFrom", 0));

	    if (cfg.getC().isInt(one + ".levelUntil"))
		item.setUntilLevel(cfg.get(one + ".levelUntil", 1000));

	    for (Job oneJ : jobs) {
		if (oneJ == null)
		    continue;
		oneJ.getItemBonus().put(one.toLowerCase(), item);
	    }

	    // Lets add into legacy map
	    if (one.contains("_")) {
		item.setLegacyKey((one.split("_")[1]).toLowerCase());
		legacy.put(item.getLegacyKey(), item);
	    }
	    items.put(one.toLowerCase(), item);

	}

	cfg.save();
    }

    public static List<JobItems> getItemsByJob(Job job) {
	List<JobItems> ls = new ArrayList<>();
	for (Entry<String, JobItems> one : items.entrySet()) {
	    if (one.getValue().getJobs().contains(job))
		ls.add(one.getValue());
	}
	return ls;
    }

    public static HashMap<String, JobItems> getItemsMapByJob(Job job) {
	HashMap<String, JobItems> i = new HashMap<>();
	for (Entry<String, JobItems> one : items.entrySet()) {
	    if (one.getValue().getJobs().contains(job))
		i.put(one.getKey(), one.getValue());
	}
	return i;
    }

    public static JobItems getItemByKey(String key) {
	JobItems item = items.get(key.toLowerCase());
	if (item != null) {
	    return item;
	}
	return legacy.get(key.toLowerCase());
    }

    public static HashMap<String, JobItems> getItems() {
	return items;
    }

    public static HashMap<String, JobItems> getLegacyItems() {
	return legacy;
    }

    public static void setItems(HashMap<String, JobItems> items) {
	ItemBoostManager.items = items;
    }
}
