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
import com.gamingmesh.jobs.CMILib.ConfigReader;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;

public class ItemBoostManager {

    static HashMap<String, JobItems> items = new HashMap<String, JobItems>();

    public ItemBoostManager() {

    }

    public static void load() {

	ConfigReader cfg = null;
	try {
	    cfg = new ConfigReader("boostedItems.yml");
	} catch (Exception e) {
	    e.printStackTrace();
	}

	if (cfg == null)
	    return;
	items.clear();
	// Converting from existing records in Jobs from old format which was located in jobConfig.yml file 
	boolean save = false;
	for (Job one : Jobs.getJobs()) {
	    for (Entry<String, JobItems> oneI : one.getItemBonus().entrySet()) {
		JobItems item = oneI.getValue();
		cfg.getC().set(oneI.getKey() + ".id", CMIMaterial.get(item.getItemStack(null)).toString());
//		Jobs.consoleMsg(cfg.getString(oneI.getKey() + ".id") + "   " + item.getItemStack(null).toString());
		cfg.getC().set(oneI.getKey() + ".jobs", Arrays.asList(one.getName()));
		if (item.getItemStack(null).hasItemMeta()) {
		    cfg.getC().set(oneI.getKey() + ".name", item.getItemStack(null).getItemMeta().hasDisplayName() ? CMIChatColor.deColorize(item.getItemStack(null).getItemMeta().getDisplayName()) : null);
		    cfg.getC().set(oneI.getKey() + ".lore", item.getItemStack(null).getItemMeta().hasLore() ? CMIChatColor.deColorize(item.getItemStack(null).getItemMeta().getLore()) : null);
		}
		List<String> ench = new ArrayList<String>();
		for (Entry<Enchantment, Integer> oneE : item.getItemStack(null).getEnchantments().entrySet()) {
		    ench.add(oneE.getKey().getName() + "=" + oneE.getValue());
		}
		cfg.getC().set(oneI.getKey() + ".enchants", ench);
		for (CurrencyType oneC : CurrencyType.values()) {
		    cfg.getC().set(oneI.getKey() + "." + oneC.toString().toLowerCase() + "Boost", ((int) (item.getBoost().get(oneC) * 100D) / 100D) + 1D);
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
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (cfg == null)
		return;
	}

	Set<String> keys = cfg.getC().getKeys(false);

	for (String one : keys) {
	    if (!cfg.getC().isConfigurationSection(one))
		continue;

	    CMIMaterial mat = CMIMaterial.get(cfg.get(one + ".id", "Stone"));
	    if (mat == null) {
		Jobs.getPluginLogger().warning("Cant load " + one + " boosted item!");
		continue;
	    }
	    String name = null;
	    if (cfg.getC().isString(one + ".name"))
		name = cfg.get(one + ".name", "");

	    List<String> lore = new ArrayList<>();
	    if (cfg.getC().getStringList(one + ".lore") != null && !cfg.getC().getStringList(one + ".lore").isEmpty())
		for (String eachLine : cfg.get(one + ".lore", Arrays.asList(""))) {
		    lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', eachLine));
		}

	    HashMap<Enchantment, Integer> enchants = new HashMap<>();
	    if (cfg.getC().getStringList(one + ".enchants") != null && !cfg.getC().getStringList(one + ".enchants").isEmpty())
		for (String eachLine : cfg.get(one + ".enchants", Arrays.asList(""))) {
		    if (!eachLine.contains("="))
			continue;
		    Enchantment ench = Enchantment.getByName(eachLine.split("=")[0]);
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

	    List<Job> jobs = new ArrayList<Job>();
	    for (String oneJ : jobsS) {
		Job job = Jobs.getJob(oneJ);
		if (job == null && !oneJ.equalsIgnoreCase("all")) {
		    Jobs.getPluginLogger().warning("Cant determine job by " + oneJ + " name for " + one + " boosted item!");
		    continue;
		}
		jobs.add(job);
	    }

	    if (jobs.isEmpty()) {
		Jobs.getPluginLogger().warning("Jobs list is empty for " + one + " boosted item!");
		continue;
	    }
	    JobItems item = new JobItems(one.toLowerCase(), mat, 1, name, lore, enchants, b, jobs);
	    for (Job oneJ : jobs) {
		oneJ.getItemBonus().put(one.toLowerCase(), item);
	    }

	    items.put(one.toLowerCase(), item);

	}

	cfg.save();
    }

    public static List<JobItems> getItemsByJob(Job job) {
	List<JobItems> ls = new ArrayList<JobItems>();
	for (Entry<String, JobItems> one : items.entrySet()) {
	    if (one.getValue().getJobs().contains(job))
		ls.add(one.getValue());
	}
	return ls;
    }

    public static HashMap<String, JobItems> getItemsMapByJob(Job job) {
	HashMap<String, JobItems> i = new HashMap<String, JobItems>();
	for (Entry<String, JobItems> one : items.entrySet()) {
	    if (one.getValue().getJobs().contains(job))
		i.put(one.getKey(), one.getValue());
	}
	return i;
    }

    public static JobItems getItemByKey(String key) {
	return items.get(key.toLowerCase());
    }
}
