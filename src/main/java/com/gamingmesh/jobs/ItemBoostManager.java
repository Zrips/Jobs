package com.gamingmesh.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;

public final class ItemBoostManager {

    private static final Map<String, JobItems> ITEMS = new HashMap<>();

    static boolean informed = false;

    public static void load() {
        ConfigReader cfg;
        try {
            cfg = new ConfigReader(Jobs.getInstance(), "boostedItems.yml");
        } catch (Exception e2) {
            e2.printStackTrace();
            return;
        }

        ITEMS.clear();

        Set<String> keys = cfg.getC().getKeys(false);

        cfg.addComment("exampleBoost", "Attention! If category name has _ in it, that means its legacy item which was converted from jobConfig.yml file",
            "Keep this format until you will be sure that all legacy items have been converted throw usage, which is automatic process when player uses items with boost in them",
            "",
            "Name which will be used to identify this particular item boost",
            "This is EXAMPLE boost and will be ignored");
        cfg.addComment("exampleBoost.ItemStack", "Item information, on usage read more at https://www.zrips.net/cmi/commands/icwol/",
            "You can use ingame command /jobs edititemboost to give particular boost to any item you are holding");
        cfg.get("exampleBoost.ItemStack", "Golden_shovel;n{&2Custom_item_name};l{&2Some_random\\n&5Lore_with_some\\n{#pink}Colors};FIRE_ASPECT:1,DAMAGE_ALL:1");

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

            BoostMultiplier b = new BoostMultiplier();
            for (CurrencyType oneC : CurrencyType.values()) {
                String typeName = oneC.toString().toLowerCase();

                if (cfg.getC().isDouble(one + "." + typeName + "Boost"))
                    b.add(oneC, cfg.get(one + "." + typeName + "Boost", 1D) - 1);
            }

            String node = one.toLowerCase();

            JobItems jitem = new JobItems(node).setType(JobsItemType.Boosted);

            jitem.setJobs(jobs);
            jitem.setBoostMultiplier(b);

            if (cfg.getC().isInt(one + ".levelFrom"))
                jitem.setFromLevel(cfg.get(one + ".levelFrom", 0));

            if (cfg.getC().isInt(one + ".levelUntil"))
                jitem.setUntilLevel(cfg.get(one + ".levelUntil", 1000));

            // Old format, should be removed down the line
            if (cfg.getC().isString(one + ".id")) {

                if (!informed) {
                    CMIMessages.consoleMessage("&5Update boosted item " + one + " item section to use new 'ItemStack' format");
                    informed = true;
                }

                CMIMaterial mat = cfg.getC().isString(one + ".id") ? CMIMaterial.get(cfg.get(one + ".id", "Stone")) : null;

                String name = cfg.getC().isString(one + ".name") ? cfg.get(one + ".name", "") : null;

                if (mat == null) {
                    CMIMessages.consoleMessage("&cCould not determine boosted item material (" + node + ")");
                    continue;
                }
                List<String> lore = cfg.get(one + ".lore", new ArrayList<String>());
                for (int a = 0; a < lore.size(); a++) {
                    lore.set(a, CMIChatColor.translate(lore.get(a)).replace(" ", "_"));
                }

                StringBuilder enchants = new StringBuilder();

                if (cfg.getC().isList(one + ".enchants"))
                    for (String eachLine : cfg.get(one + ".enchants", Arrays.asList(""))) {
                        String[] split = eachLine.split("=", 2);
                        if (split.length == 0)
                            continue;

                        Enchantment ench = CMIEnchantment.getByName(split[0]);

                        if (ench == null)
                            continue;

                        int level = -1;

                        if (split.length > 1) {
                            try {
                                level = Integer.parseInt(split[1]);
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        }

                        if (level == -1)
                            continue;

                        if (!enchants.toString().isEmpty())
                            enchants.append(",");
                        enchants.append(split[0] + ":" + level);

                    }

                String lc = cfg.getC().getString(one + ".leather-color", "");

                String itemSring = mat.toString();
                if (name != null)
                    itemSring += ";n{" + name.replace(" ", "_") + "}";

                if (!lore.isEmpty())
                    itemSring += ";l{" + CMIList.listToString(lore, "\\n") + "}";

                if (lc != null)
                    itemSring += ";" + lc;

                if (!enchants.toString().isEmpty())
                    itemSring += ";" + enchants.toString();

                jitem.setItemString(itemSring);
            } else if (cfg.getC().isString(one + ".ItemStack")) {
                String itemString = cfg.get(one + ".ItemStack", cfg.getC().getString(one + ".ItemStack"));
                CMIItemStack item = CMIItemStack.deserialize(itemString, null);

                if (item == null || item.getCMIType().isNone()) {
                    CMIMessages.consoleMessage("&cInvalid ItemStack for boosted item (" + node + ")");
                    continue;
                }
                jitem.setItemString(itemString);
            }
            if (jitem.getItem() != null)
                ITEMS.put(node, jitem);
        }

        cfg.save();
        CMIMessages.consoleMessage("&eLoaded &6" + ITEMS.size() + " &eboosted items");
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
        return ITEMS.get(key.toLowerCase());
    }

    /**
     * @return the current cached map of items.
     */
    public static Map<String, JobItems> getItems() {
        return ITEMS;
    }

    private static final String jobsItemBoost = "JobsItemBoost";

    /**
     * Checks if the given item is a boosted jobs item.
     * 
     * @param item the item to check
     * @return true if the item is a boosted jobs item, false otherwise
     * @deprecated Use {@link #isBoostedJobsItem(ItemStack)} instead.
     */
    @Deprecated
    public static boolean containsItemBoostByNBT(ItemStack item) {
        return isBoostedJobsItem(item);
    }

    public static boolean isBoostedJobsItem(ItemStack item) {
        return item != null && new CMINBT(item).hasNBT(jobsItemBoost);
    }

    public static JobItems getJobsItemByNbt(ItemStack item) {
        if (item == null)
            return null;

        CMINBT nbt = new CMINBT(item);
        Object itemName = nbt.getString(jobsItemBoost);

        if (itemName == null || itemName.toString().isEmpty()) {
            // Checking old boost items and converting to new format if needed
            if (nbt.hasNBT(jobsItemBoost)) {
                for (Job one : Jobs.getJobs()) {
                    itemName = nbt.getString(jobsItemBoost + "." + one.getName());
                    if (itemName != null) {
                        JobItems b = getItemByKey(itemName.toString());
                        if (b != null) {
                            ItemStack ic = (ItemStack) nbt.setString(jobsItemBoost, b.getNode());
                            item.setItemMeta(ic.getItemMeta());
                        }
                        break;
                    }
                }
            }
            if (itemName == null)
                return null;
        }

        return getItemByKey(itemName.toString());
    }

    public static ItemStack applyNBT(ItemStack item, String node) {
        if (item == null)
            return null;
        return (ItemStack) new CMINBT(item).setString(jobsItemBoost, node);
    }
}
