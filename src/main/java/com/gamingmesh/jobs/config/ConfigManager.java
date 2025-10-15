/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Gui.GuiItem;
import com.gamingmesh.jobs.container.ActionSubType;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.DisplayMethod;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobCommands;
import com.gamingmesh.jobs.container.JobConditions;
import com.gamingmesh.jobs.container.JobInfo;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.container.JobPermission;
import com.gamingmesh.jobs.container.Quest;
import com.gamingmesh.jobs.container.QuestObjective;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Equations.ParseError;
import net.Zrips.CMILib.Equations.Parser;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;

public class ConfigManager {

    private final Jobs plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class);

    @Deprecated
    private File jobFile;
    private File jobsPathFolder;

    private final Set<YmlMaker> jobFiles = new HashSet<>();

    public static final String EXAMPLEJOBNAME = "_EXAMPLE";
    public static final String EXAMPLEJOBINTERNALNAME = "exampleJob";

    public ConfigManager() {
        this.jobFile = new File(Jobs.getFolder(), "jobConfig.yml");
        this.jobsPathFolder = new File(Jobs.getFolder(), "jobs");

        migrateJobs();
    }

    /**
     * Returns all of existing jobs files in Jobs/jobs folder
     * 
     * @return {@link HashSet}
     */
    public Set<YmlMaker> getJobFiles() {
        return jobFiles;
    }

    @Deprecated
    public YamlConfiguration getJobConfig() {
        return !jobFile.exists() ? null : YamlConfiguration.loadConfiguration(jobFile);
    }

    @Deprecated
    public File getJobFile() {
        return jobFile;
    }

    public void changeJobsSettings(String jobName, String path, Object value) {
        path = path.replace('/', '.');
        jobName = jobName.toLowerCase();

        for (YmlMaker yml : jobFiles) {
            if (yml.getConfigFile().getName().contains(jobName)) {
                yml.getConfig().set(path, value);
                yml.saveConfig();
                break;
            }
        }
    }

    public class KeyValues {

        private String type, subType = "", meta = "";
        private int id = 0;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }

        public String getMeta() {
            return meta;
        }

        public void setMeta(String meta) {
            this.meta = meta;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @SuppressWarnings("deprecation")
    public KeyValues getKeyValue(String myKey, ActionType actionType, String jobName) {
        String subType = "", meta = "";

        final String finalMyKey = myKey;

        String[] keySplit = myKey.split("-", 2);

        if (keySplit.length > 0) {
            if (keySplit.length > 1) {
                subType = ":" + keySplit[1];
                meta = keySplit[1];
            }

            myKey = keySplit[0];
        } else if ((keySplit = myKey.split(":", 2)).length > 0) { // when we uses tipped arrow effect types
            meta = keySplit.length > 1 ? keySplit[1] : myKey;
            subType = ":all";
            myKey = keySplit[0];
        }

        String type = null;
        int id = 0;

        CMIMaterial material = CMIMaterial.NONE;

        if (actionType.hasSubType(ActionSubType.BLOCK) ||
            actionType.hasSubType(ActionSubType.MATERIAL)) {

            material = CMIMaterial.get(myKey + (subType));

            if (material == CMIMaterial.NONE)
                material = CMIMaterial.get(myKey.replace(' ', '_').toUpperCase());

            if (material == CMIMaterial.NONE) {
                // try integer method
                Integer matId = null;
                try {
                    matId = Integer.valueOf(myKey);
                } catch (NumberFormatException ignored) {
                }

                if (matId != null && (material = CMIMaterial.get(matId)) != CMIMaterial.NONE) {
                    CMIMessages.consoleMessage("Job " + jobName + " " + actionType.getName() + " is using ID: " + myKey + "!");
                    CMIMessages.consoleMessage("Please use the Material name instead: " + material.toString() + "!");
                }
            }

        }

        if (actionType == ActionType.STRIPLOGS && Version.isCurrentLower(Version.v1_13_R1))
            return null;

        if (material.getMaterial() != null && material.isAir()) {
            CMIMessages.consoleMessage("Job " + jobName + " " + actionType.getName() + " can't recognize material! (" + myKey + ")");
            return null;
        }

        if (Version.isCurrentLower(Version.v1_13_R1) && meta.isEmpty())
            meta = Integer.toString(material.getData());

        c: if (material != CMIMaterial.NONE && material.getMaterial() != null && !material.isAir()) {
            // Need to include those ones and count as regular blocks
            switch (myKey.replace("_", "").toLowerCase()) {
            case "itemframe":
                type = "ITEM_FRAME";
                id = 18;
                meta = "1";
                break c;
            case "glowitemframe":
                type = "GLOW_ITEM_FRAME";
                id = 0;
                meta = "0";
                break c;
            case "painting":
                type = "PAINTING";
                id = 9;
                meta = "1";
                break c;
            case "armorstand":
                type = "ARMOR_STAND";
                id = 30;
                meta = "1";
                break c;
            default:
                break;
            }

            // These actions MUST be blocks
            if (actionType.hasSubType(ActionSubType.BLOCK) && !actionType.hasSubType(ActionSubType.MATERIAL) && (!material.isBlock() || material.getMaterial().toString().equalsIgnoreCase("AIR"))) {
                CMIMessages.consoleMessage("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + material
                    + " (" + myKey + ")! Material must be a block! Use \"/jobs blockinfo\" on a target block");
                return null;
            }

            // START HACK
            /* 
             * Historically, GLOWING_REDSTONE_ORE would ONLY work as REDSTONE_ORE, and putting
             * GLOWING_REDSTONE_ORE in the configuration would not work.  Unfortunately, this is 
             * completely backwards and wrong.
             * 
             * To maintain backwards compatibility, all instances of REDSTONE_ORE should normalize
             * to GLOWING_REDSTONE_ORE, and warn the user to change their configuration.  In the
             * future this hack may be removed and anybody using REDSTONE_ORE will have their
             * configurations broken.
             */
            if (material == CMIMaterial.REDSTONE_ORE && actionType == ActionType.BREAK && Version.isCurrentLower(Version.v1_13_R1)) {
                CMIMessages.consoleMessage("Job " + jobName + " is using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE.");
                CMIMessages.consoleMessage("Automatically changing block to GLOWING_REDSTONE_ORE. Please update your configuration.");
                CMIMessages.consoleMessage("In vanilla minecraft, REDSTONE_ORE changes to GLOWING_REDSTONE_ORE when interacted with.");
                CMIMessages.consoleMessage("In the future, Jobs using REDSTONE_ORE instead of GLOWING_REDSTONE_ORE may fail to work correctly.");
                material = CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE;
            } else if (material == CMIMaterial.LEGACY_GLOWING_REDSTONE_ORE && actionType == ActionType.BREAK && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                CMIMessages.consoleMessage("Job " + jobName + " is using GLOWING_REDSTONE_ORE instead of REDSTONE_ORE.");
                CMIMessages.consoleMessage("Automatically changing block to REDSTONE_ORE. Please update your configuration.");
                material = CMIMaterial.REDSTONE_ORE;
            }
            // END HACK

            type = material.getMaterial().toString();

            if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
                short legacyData = material.getLegacyData();

                if (legacyData > 0)
                    subType = ":" + legacyData;
            }

            id = material.getId();
        } else if (actionType.hasSubType(ActionSubType.ENTITY) && !actionType.hasSubType(ActionSubType.CUSTOM)) {
            // check entities
            CMIEntityType entity = CMIEntityType.getByName(myKey);

            // Change pig zombie -> piglin in 1.16+
            if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && entity == CMIEntityType.PIG_ZOMBIE) {
                entity = CMIEntityType.PIGLIN;
            }

            if (entity != null && (entity.isAlive() || entity == CMIEntityType.ENDER_CRYSTAL)) {
                type = entity.toString();
                id = entity.getId();
            }

            // Pre 1.13 checks for custom names
            if (entity == null) {
                switch (myKey.toLowerCase()) {
                case "skeletonwither":
                case "witherskeleton":
                    type = CMIEntityType.WITHER_SKELETON.name();
                    id = 51;
                    meta = "1";
                    break;
                case "skeletonstray":
                case "strayskeleton":
                    type = CMIEntityType.STRAY.name();
                    id = 51;
                    meta = "2";
                    break;
                case "zombievillager":
                    type = CMIEntityType.ZOMBIE_VILLAGER.name();
                    id = 54;
                    meta = "1";
                    break;
                case "zombiehusk":
                    type = CMIEntityType.HUSK.name();
                    id = 54;
                    meta = "2";
                    break;
                case "horseskeleton":
                case "skeletonhorse":
                    type = CMIEntityType.SKELETON_HORSE.name();
                    id = 100;
                    meta = "1";
                    break;
                case "horsezombie":
                case "zombiehorse":
                    type = CMIEntityType.ZOMBIE_HORSE.name();
                    id = 100;
                    meta = "2";
                    break;
                case "guardianelder":
                case "elderguardian":
                    type = CMIEntityType.ELDER_GUARDIAN.name();
                    id = 68;
                    meta = "1";
                    break;
                default:
                    break;
                }
            }
        }

        if (actionType == ActionType.ENCHANT) {

            CMIEnchantment cmiEnchant = CMIEnchantment.getCMIByName(myKey);

            if (cmiEnchant == null && material == CMIMaterial.NONE) {
                CMIMessages.consoleMessage("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
                return null;
            }

            type = cmiEnchant != null ? cmiEnchant.getKeyName() : myKey;

        }

        if (actionType == ActionType.EXPLORE) {
            type = myKey;

            int amount = 10;
            try {
                amount = Integer.valueOf(myKey);
            } catch (NumberFormatException e) {
                CMIMessages.consoleMessage("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
                return null;
            }

            Jobs.getExploreManager().setExploreEnabled();
            Jobs.getExploreManager().setPlayerAmount(amount);

            Jobs.getChunkExplorationManager().setExploreEnabled();
            Jobs.getChunkExplorationManager().setPlayerAmount(amount);

        }

        if (actionType == ActionType.CRAFT) {
            if (myKey.startsWith("!")) {
                type = myKey.substring(1, myKey.length());
            }

            String[] split = myKey.split(":", 2);
            if (split.length > 1) {
                subType = split[1];
            }
        }

        if (actionType == ActionType.SHEAR && !myKey.startsWith("color")) {
            type = myKey;
        }

        if (actionType.hasSubType(ActionSubType.CUSTOM)) {
            type = myKey;
        }

        if (finalMyKey.endsWith("-all") || finalMyKey.endsWith(":all")) {
            type = finalMyKey.split(":|-", 2)[0];
        }

        if (type == null) {
            CMIMessages.consoleMessage("Job " + jobName + " has an invalid " + actionType.getName() + " type property: " + myKey + "!");
            return null;
        }

        if (":ALL".equalsIgnoreCase(subType)) {
            meta = "ALL";
            // case for ":all" identifier
            type = (actionType == ActionType.SHEAR && myKey.startsWith("color")) ? "color" : CMIMaterial.getGeneralMaterialName(type);

            CMIEntityType entity = CMIEntityType.get(type);
            if (entity != null) {
                type = entity.toString();
            }

        }

        if (actionType == ActionType.TNTBREAK)
            Jobs.getGCManager().setTntFinder(true);

        // using breeder finder
        if (actionType == ActionType.BREED)
            Jobs.getGCManager().useBreederFinder = true;

        KeyValues kv = new KeyValues();
        kv.setId(id);
        kv.setMeta(meta);
        kv.setSubType(subType);
        kv.setType(type);
        return kv;
    }

    private boolean migrateJobs() {
        YamlConfiguration oldConf = getJobConfig();
        if (oldConf == null) {
            jobsPathFolder.mkdirs();

            if (jobsPathFolder.isDirectory() && jobsPathFolder.listFiles().length == 0) {
                try {
                    for (String f : Util.getFilesFromPackage("jobs", "", "yml")) {
                        plugin.saveResource("jobs" + File.separator + f + ".yml", false);
                    }
                } catch (Exception c) {
                }
            }

            return false;
        }

        if (!jobsPathFolder.isDirectory()) {
            jobsPathFolder.mkdirs();
        }

        ConfigurationSection jobsSection = oldConf.getConfigurationSection("Jobs");
        if (jobsSection == null || jobsSection.getKeys(false).isEmpty()) {
            return false;
        }

        jobFiles.clear();

        Jobs.getPluginLogger().info("Started migrating jobConfig to /jobs folder...");

        for (String jobKey : jobsSection.getKeys(false)) {

            String fileName = jobKey.equalsIgnoreCase(EXAMPLEJOBNAME) ? jobKey.toUpperCase() : jobKey.toLowerCase();

            YmlMaker newJobFile = new YmlMaker(jobsPathFolder, fileName + ".yml");
            newJobFile.createNewFile();

            FileConfiguration conf = newJobFile.getConfig();
            conf.options().pathSeparator(File.separatorChar);

            for (Map.Entry<String, Object> m : jobsSection.getValues(true).entrySet()) {
                if (m.getKey().equalsIgnoreCase(jobKey)) {
                    conf.set(m.getKey(), m.getValue());
                }
            }

            newJobFile.saveConfig();

            if (!fileName.equalsIgnoreCase(EXAMPLEJOBNAME)) {
                jobFiles.add(newJobFile);
            }
        }

        if (!jobFiles.isEmpty()) {
            Jobs.getPluginLogger().info("Done. Migrated jobs amount: " + jobFiles.size());
        }

        ConfigReader cfg = null;
        try {
            cfg = new ConfigReader(plugin, "jobConfig.yml");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        cfg.saveToBackup(false);
        cfg.header(Arrays.asList("-----------------------------------------------------",
            "Jobs have been moved into jobs subfolder",
            "Old jobs content was saved into backup folder",
            "-----------------------------------------------------"));
        cfg.save();

        return true;
    }

    public void reload() {
        jobFiles.clear();
        migrateJobs();

        ExampleJob.updateExampleFile();

        if (jobFiles.isEmpty()) {
            File[] files = jobsPathFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml")
                && !name.equalsIgnoreCase(EXAMPLEJOBNAME + ".yml"));
            if (files != null) {
                for (File file : files) {
                    jobFiles.add(new YmlMaker(jobsPathFolder, file));
                }
            }
        }

        if (jobFiles.isEmpty()) {
            return;
        }

        Map<String, Job> map = new TreeMap<>();

        for (YmlMaker conf : jobFiles) {
            Job job = loadJobs(conf.getConfig().getConfigurationSection(""));
            if (job == null)
                continue;

            map.put(job.getName(), job);

        }

        List<Job> jobs = new ArrayList<>();
        jobs.addAll(map.values());
        Jobs.setJobs(jobs);

        if (!jobs.isEmpty()) {
            CMIMessages.consoleMessage("&eLoaded &6" + jobs.size() + " &ejobs");
        }

        ItemBoostManager.load();
    }

    private static String escapeUnicode(String input) {
        StringBuilder result = new StringBuilder(input.length());
        boolean escaping = false;
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (!escaping) {
                if (currentChar == '\\') {
                    escaping = true;
                } else {
                    result.append(currentChar);
                }
            } else {
                switch (currentChar) {
                case 'n':
                    result.append('\n');
                    break;
                case 't':
                    result.append('\t');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case 'f':
                    result.append('\f');
                    break;
                case 'b':
                    result.append('\b');
                    break;
                case '\\':
                    result.append('\\');
                    break;
                case '\'':
                    result.append('\'');
                    break;
                case '\"':
                    result.append('\"');
                    break;
                default:
                    result.append(currentChar);
                    break;
                }
                escaping = false;
            }
        }
        return result.toString();
    }

    boolean informedGUI = false;
    boolean informedLimited = false;

    private Job loadJobs(ConfigurationSection jobsSection) {
        java.util.logging.Logger log = Jobs.getPluginLogger();

        for (String jobConfigName : jobsSection.getKeys(false)) {
            // Ignore example job
            if (jobConfigName.equalsIgnoreCase(EXAMPLEJOBINTERNALNAME))
                continue;

            // Translating unicode
            jobConfigName = escapeUnicode(jobConfigName);

            ConfigurationSection jobSection = jobsSection.getConfigurationSection(jobConfigName);
            if (jobSection == null)
                continue;

            Job job = new Job(jobConfigName)
                .setDisplayName(jobSection.getString("displayName"));

            String jobFullName = jobSection.getString("fullname");
            if (jobFullName == null) {
                log.warning("Job " + jobConfigName + " has an invalid fullname property. Skipping job!");
                continue;
            }
            // Translating unicode
            job.setJobFullName(escapeUnicode(jobFullName));

            String jobShortName = jobSection.getString("shortname");
            if (jobShortName == null) {
                log.warning("Job " + jobConfigName + " is missing the shortname property. Skipping job!");
                continue;
            }
            job.setShortName(jobShortName);
            job.setMaxLevel(jobSection.getInt("max-level"));
            job.setVipMaxLevel(jobSection.getInt("vip-max-level"));
            job.setMaxSlots(jobSection.getInt("slots"));
            job.setRejoinCd(jobSection.getLong("rejoinCooldown", 0L) * 1000L);

            job.setDescription(CMIChatColor.translate(jobSection.getString("description", "")));

            List<String> fDescription = jobSection.getStringList("FullDescription");
            if (jobSection.isString("FullDescription"))
                fDescription.add(jobSection.getString("FullDescription"));
            for (int i = 0; i < fDescription.size(); i++) {
                fDescription.set(i, CMIChatColor.translate(fDescription.get(i)));
            }
            job.setFullDescription(fDescription);

            CMIChatColor color = CMIChatColor.WHITE;
            String c = jobSection.getString("ChatColour");
            if (c != null) {
                color = CMIChatColor.getColor(c);

                if (color == null && !c.isEmpty())
                    color = CMIChatColor.getColor("&" + c.charAt(0));

                if (color == null) {
                    color = CMIChatColor.WHITE;
                    log.warning("Job " + jobConfigName + " has an invalid ChatColour property. Defaulting to WHITE!");
                }
            }
            job.setChatColor(color);

            String bossbar = jobSection.getString("BossBarColour");
            if (bossbar != null && bossbar.isEmpty()) {
                bossbar = "GREEN";
                log.warning("Job " + jobConfigName + " has an invalid BossBarColour property.");
            }
            job.setBossbar(bossbar);

            DisplayMethod displayMethod = DisplayMethod.matchMethod(jobSection.getString("chat-display", ""));
            if (displayMethod == null) {
                log.warning("Job " + jobConfigName + " has an invalid chat-display property. Defaulting to None!");
                displayMethod = DisplayMethod.NONE;
            }
            job.setDisplayMethod(displayMethod);

            boolean isNoneJob = jobConfigName.equalsIgnoreCase("none");

            Parser maxExpEquation;
            String maxExpEquationInput = isNoneJob ? "0" : jobSection.getString("leveling-progression-equation", "0");
            try {
                maxExpEquation = new Parser(maxExpEquationInput);
                // test equation
                maxExpEquation.setVariable("numjobs", 1);
                maxExpEquation.setVariable("maxjobs", 2);
                maxExpEquation.setVariable("joblevel", 1);
            } catch (ParseError e) {
                log.warning("Job " + jobConfigName + " has an invalid leveling-progression-equation property. Skipping job!");
                continue;
            }
            job.setMaxExpEquation(maxExpEquation);

            Parser incomeEquation = new Parser("0");
            String incomeEquationInput = jobSection.getString("income-progression-equation");
            if (incomeEquationInput != null) {
                try {
                    incomeEquation = new Parser(incomeEquationInput);
                    // test equation
                    incomeEquation.setVariable("numjobs", 1);
                    incomeEquation.setVariable("maxjobs", 2);
                    incomeEquation.setVariable("joblevel", 1);
                    incomeEquation.setVariable("baseincome", 1);
                } catch (ParseError e) {
                    log.warning("Job " + jobConfigName + " has an invalid income-progression-equation property. Skipping job!");
                    continue;
                }
            }
            job.setMoneyEquation(incomeEquation);

            Parser expEquation;
            String expEquationInput = isNoneJob ? "0" : jobSection.getString("experience-progression-equation", "0");
            try {
                expEquation = new Parser(expEquationInput);
                // test equation
                expEquation.setVariable("numjobs", 1);
                expEquation.setVariable("maxjobs", 2);
                expEquation.setVariable("joblevel", 1);
                expEquation.setVariable("baseexperience", 1);
            } catch (ParseError e) {
                log.warning("Job " + jobConfigName + " has an invalid experience-progression-equation property. Skipping job!");
                continue;
            }
            job.setXpEquation(expEquation);

            Parser pointsEquation = new Parser("0");
            String pointsEquationInput = jobSection.getString("points-progression-equation");
            if (pointsEquationInput != null) {
                try {
                    pointsEquation = new Parser(pointsEquationInput);
                    // test equation
                    pointsEquation.setVariable("numjobs", 1);
                    pointsEquation.setVariable("maxjobs", 2);
                    pointsEquation.setVariable("joblevel", 1);
                    pointsEquation.setVariable("basepoints", 1);
                } catch (ParseError e) {
                    log.warning("Job " + jobConfigName + " has an invalid points-progression-equation property. Skipping job!");
                    continue;
                }
            }
            job.setPointsEquation(pointsEquation);

            // Gui item

            ItemStack guiItem = CMIMaterial.GREEN_WOOL.newItemStack();
            ConfigurationSection guiSection = jobSection.getConfigurationSection("Gui");

            GuiItem gItem = new GuiItem();
            if (guiSection != null) {
                if (guiSection.isString("ItemStack")) {

                    CMIAsyncHead ahead = new CMIAsyncHead() {
                        @Override
                        public void afterAsyncUpdate(ItemStack item) {
                            gItem.setGuiItem(item);
                        }
                    };

                    CMIItemStack item = CMIItemStack.deserialize(guiSection.getString("ItemStack"), ahead);

                    if (!ahead.isAsyncHead() && item != null && !item.getCMIType().isNone()) {
                        gItem.setGuiItem(item.getItemStack());
                    }

                } else if (guiSection.isString("Item")) {
                    String item = guiSection.getString("Item");

                    String subType = "";

                    String[] itemSplit = item.split("-", 2);
                    if (itemSplit.length > 1) {
                        subType = ":" + itemSplit[1];
                        item = itemSplit[0];
                    } else if ((itemSplit = item.split(":", 2)).length > 0) { // when we uses tipped arrow effect types
                        item = itemSplit[0];
                    }

                    CMIMaterial material = CMIMaterial.get(item + (subType));

                    if (material == CMIMaterial.NONE)
                        material = CMIMaterial.get(item.replace(' ', '_').toUpperCase());

                    if (material == CMIMaterial.NONE) {
                        // try integer method
                        Integer matId = null;
                        try {
                            matId = Integer.valueOf(item);
                        } catch (NumberFormatException e) {
                        }

                        if (matId != null && (material = CMIMaterial.get(matId)) != CMIMaterial.NONE) {
                            log.warning("Job " + jobFullName + " is using GUI item ID: " + item + "!");
                            log.warning("Please use the Material name instead: " + material.toString() + "!");
                        }
                    }

                    if (material != CMIMaterial.NONE)
                        guiItem = material.newItemStack();

                    if (!informedGUI) {
                        CMIMessages.consoleMessage("&5Update " + jobConfigName + " jobs gui item section to use `ItemStack` instead of `Item` sections format. More information inside _EXAMPLE job file");
                        informedGUI = true;
                    }

                    gItem.setGuiItem(guiItem);
                } else if (guiSection.isInt("Id") && guiSection.isInt("Data")) {
                    guiItem = CMIMaterial.get(guiSection.getInt("Id"), guiSection.getInt("Data")).newItemStack();
                    gItem.setGuiItem(guiItem);
                    CMIMessages.consoleMessage("Update " + jobConfigName + " jobs gui item section to use `Item` instead of `Id` and `Data` sections");
                } else
                    log.warning("Job " + jobConfigName + " has an invalid (" + guiSection.getString("Item") + ") Gui property. Please fix this if you want to use it!");

                if (guiSection.isList("Enchantments")) {
                    for (String str4 : guiSection.getStringList("Enchantments")) {
                        String[] id = str4.split(":", 2);

                        if (id.length < 2)
                            continue;

                        Enchantment enchant = CMIEnchantment.getByName(id[0]);
                        if (enchant == null)
                            continue;

                        int level = 1;
                        try {
                            level = Integer.parseInt(id[1]);
                        } catch (NumberFormatException ex) {
                        }

                        if (guiItem.getItemMeta() instanceof EnchantmentStorageMeta) {
                            EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) guiItem.getItemMeta();
                            enchantMeta.addStoredEnchant(enchant, level, true);
                            guiItem.setItemMeta(enchantMeta);
                        } else
                            guiItem.addUnsafeEnchantment(enchant, level);
                    }
                    gItem.setGuiItem(guiItem);
                }

                String customSkull = guiSection.getString("CustomSkull", "");
                if (!customSkull.isEmpty()) {
                    gItem.setGuiItem(Util.getSkull(customSkull));
                }
                gItem.setGuiSlot(guiSection.getInt("slot", -1));
            }

            job.setGuiItem(gItem);

            // Permissions
            List<JobPermission> jobPermissions = new ArrayList<>();
            ConfigurationSection permissionsSection = jobSection.getConfigurationSection("permissions");
            if (permissionsSection != null) {
                for (String permissionKey : permissionsSection.getKeys(false)) {
                    ConfigurationSection permissionSection = permissionsSection.getConfigurationSection(permissionKey);
                    if (permissionSection == null) {
                        log.warning("Job " + jobConfigName + " has an invalid permission key " + permissionKey + "!");
                        continue;
                    }

                    String node = permissionSection.getString("permission");
                    boolean value = permissionSection.getBoolean("value", true);
                    int levelRequirement = permissionSection.getInt("level");
                    jobPermissions.add(new JobPermission(node, value, levelRequirement));
                }
            }
            job.setPermissions(jobPermissions);

            // Conditions
            List<JobConditions> jobConditions = new ArrayList<>();
            ConfigurationSection conditionsSection = jobSection.getConfigurationSection("conditions");
            if (conditionsSection != null) {
                for (String conditionKey : conditionsSection.getKeys(false)) {
                    ConfigurationSection permissionSection = conditionsSection.getConfigurationSection(conditionKey);

                    if (permissionSection == null) {
                        log.warning("Job " + jobConfigName + " has an invalid condition key " + conditionKey + "!");
                        continue;
                    }

                    List<String> requires = permissionSection.getStringList("requires");
                    List<String> perform = permissionSection.getStringList("perform");

                    if (requires.isEmpty() || perform.isEmpty()) {
                        log.warning("Job " + jobConfigName + " has an invalid condition requirement " + conditionKey + "!");
                        continue;
                    }

                    jobConditions.add(new JobConditions(conditionKey.toLowerCase(), requires, perform));
                }
            }
            job.setConditions(jobConditions);

            // Commands
            List<JobCommands> jobCommand = new ArrayList<>();
            ConfigurationSection commandsSection = jobSection.getConfigurationSection("commands");
            if (commandsSection != null) {
                for (String commandKey : commandsSection.getKeys(false)) {
                    ConfigurationSection commandSection = commandsSection.getConfigurationSection(commandKey);

                    if (commandSection == null) {
                        log.warning("Job " + jobConfigName + " has an invalid command key " + commandKey + "!");
                        continue;
                    }

                    List<String> commands = commandSection.getStringList("command");

                    if (commandSection.isString("command"))
                        commands.add(commandSection.getString("command"));

                    int levelFrom = commandSection.getInt("levelFrom", 0);
                    int levelUntil = commandSection.getInt("levelUntil", job.getMaxLevel());
                    jobCommand.add(new JobCommands(commandKey.toLowerCase(), commands, levelFrom, levelUntil));
                }
            }
            job.setCommands(jobCommand);

            // Limited Items
            Map<String, JobLimitedItems> jobLimitedItems = new HashMap<>();
            ConfigurationSection limitedItemsSection = jobSection.getConfigurationSection("limitedItems");
            if (limitedItemsSection != null) {
                for (String itemKey : limitedItemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = limitedItemsSection.getConfigurationSection(itemKey);

                    if (itemSection == null) {
                        log.warning("Job " + jobConfigName + " has an invalid item key " + itemKey + "!");
                        continue;
                    }

                    String node = itemKey.toLowerCase();

                    if (itemSection.contains("id")) {

                        CMIMaterial mat = CMIMaterial.NONE;

                        if (itemSection.isInt("id")) {
                            mat = CMIMaterial.get(itemSection.getInt("id"));
                        } else {
                            mat = CMIMaterial.get(itemSection.getString("id"));
                        }

                        if (mat == CMIMaterial.NONE) {
                            log.warning("Job " + jobConfigName + " has incorrect limitedItems material id!");
                            continue;
                        }

                        List<String> lore = itemSection.getStringList("lore");

                        if (lore != null)
                            for (int a = 0; a < lore.size(); a++) {
                                lore.set(a, CMIChatColor.translate(lore.get(a).replace(" ", "_")));
                            }

                        StringBuilder enchants = new StringBuilder();
                        for (String eachLine : itemSection.getStringList("enchants")) {
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
                                }
                            }

                            if (level == -1)
                                continue;

                            if (!enchants.toString().isEmpty())
                                enchants.append(",");
                            enchants.append(split[0] + ":" + level);
                        }

                        String itemString = "";

                        String name = CMIChatColor.translate(itemSection.getString("name"));

                        if (name != null)
                            itemString += ";n{" + name.replace(" ", "_") + "}";

                        if (lore != null)
                            for (int b = 0; b < lore.size(); b++) {
                                lore.set(b, CMIChatColor.translate(lore.get(b).replace(" ", "_")));
                            }

                        if (lore != null && !lore.isEmpty())
                            itemString += ";l{" + CMIList.listToString(lore, "\\n") + "}";

                        jobLimitedItems.put(node, new JobLimitedItems(node, itemString, itemSection.getInt("level")));

                        if (!informedLimited) {
                            CMIMessages.consoleMessage("&5Update " + jobConfigName
                                + " jobs limited items section to use `ItemStack` instead of `id` sections format. More information inside _EXAMPLE job file");
                            informedLimited = true;
                        }

                    } else if (itemSection.contains("ItemStack")) {

                        CMIItemStack limitedItem = CMIItemStack.deserialize(itemSection.getString("ItemStack"));

                        if (limitedItem == null || limitedItem.getCMIType().isNone()) {
                            log.warning("Job " + jobConfigName + " has incorrect limitedItems material id!");
                            continue;
                        }

                        jobLimitedItems.put(node, new JobLimitedItems(node, itemSection.getString("ItemStack"), itemSection.getInt("level")));

                    }
                }
            }

            job.setLimitedItems(jobLimitedItems);

            job.setCmdOnJoin(jobSection.getStringList("cmd-on-join"));
            job.setCmdOnLeave(jobSection.getStringList("cmd-on-leave"));
            job.setWorldBlacklist(jobSection.getStringList("world-blacklist"));
            job.setMaxLevelCommands(jobSection.getStringList("commands-on-max-level"));
            job.setIgnoreMaxJobs(jobSection.getBoolean("ignore-jobs-max"));
            job.setReversedWorldBlacklist(jobSection.getBoolean("reverse-world-blacklist-functionality"));

            ConfigurationSection qsection = jobSection.getConfigurationSection("Quests");
            if (qsection != null) {
                List<Quest> quests = new ArrayList<>();

                for (String one : qsection.getKeys(false)) {
                    try {
                        ConfigurationSection sqsection = qsection.getConfigurationSection(one);
                        if (sqsection == null)
                            continue;

                        Quest quest = new Quest(sqsection.getString("Name", one), job);
                        ActionType actionType = ActionType.getByName(sqsection.getString("Action"));

                        quest.setConfigName(one);

                        if (actionType != null) {
                            KeyValues kv = getKeyValue(sqsection.getString("Target").toUpperCase(), actionType, jobFullName);
                            if (kv != null) {
                                int amount = sqsection.getInt("Amount", 1);
                                QuestObjective newObjective = new QuestObjective(actionType, kv.getId(), kv.getMeta(), (kv.getType() + kv.getSubType()).toUpperCase(), amount);
                                quest.addObjective(newObjective);
                            }
                        }

                        for (String oneObjective : sqsection.getStringList("Objectives")) {
                            List<QuestObjective> objectives = QuestObjective.get(oneObjective, jobFullName);

                            quest.addObjectives(objectives);
                        }

                        quest.setMinLvl(sqsection.getInt("fromLevel"));

                        if (sqsection.isInt("toLevel"))
                            quest.setMaxLvl(sqsection.getInt("toLevel"));

                        quest.setChance(sqsection.getInt("Chance", 100));
                        quest.setRewardAmount(sqsection.getDouble("RewardAmount"));
                        quest.setRewardCmds(sqsection.getStringList("RewardCommands"));
                        quest.setDescription(sqsection.getStringList("RewardDesc"));
                        quest.setRestrictedArea(sqsection.getStringList("RestrictedAreas"));
                        quest.setEnabled(sqsection.getBoolean("Enabled", true));

                        quests.add(quest);
                    } catch (Exception e) {
                        CMIMessages.consoleMessage("&cCan't load &6" + one + " &cquest for &6" + jobFullName);
                        e.printStackTrace();
                    }
                }

                job.setQuests(quests);
                CMIMessages.consoleMessage("&eLoaded &6" + quests.size() + " &equests for &6" + jobFullName);
            }
            job.setMaxDailyQuests(jobSection.getInt("maxDailyQuests", 1));

            Integer softIncomeLimit = null;
            Integer softExpLimit = null;
            Integer softPointsLimit = null;

            if (jobSection.isInt("softIncomeLimit"))
                softIncomeLimit = jobSection.getInt("softIncomeLimit");
            if (jobSection.isInt("softExpLimit"))
                softExpLimit = jobSection.getInt("softExpLimit");
            if (jobSection.isInt("softPointsLimit"))
                softPointsLimit = jobSection.getInt("softPointsLimit");

            for (ActionType actionType : ActionType.values()) {
                ConfigurationSection typeSection = jobSection.getConfigurationSection(actionType.getName());
                List<JobInfo> jobInfo = new ArrayList<>();

                // Crude fix for incorrect wax name
                if (typeSection == null && actionType.equals(ActionType.WAX))
                    typeSection = jobSection.getConfigurationSection("Vax");

                if (typeSection != null) {
                    if (!typeSection.getStringList("materials").isEmpty()) {
                        for (String mat : typeSection.getStringList("materials")) {
                            if (!mat.contains(";")) {
                                continue;
                            }

                            KeyValues keyValue = null;
                            String[] sep = mat.split(";", 4);
                            if (sep.length >= 1) {
                                keyValue = getKeyValue(sep[0], actionType, jobConfigName);
                            }

                            if (keyValue == null) {
                                continue;
                            }

                            int id = keyValue.getId();
                            String type = keyValue.getType(),
                                subType = keyValue.getSubType(),
                                meta = keyValue.getMeta();

                            double income = 0D;
                            if (sep.length >= 2) {
                                try {
                                    income = Double.parseDouble(sep[1]);
                                    income = updateValue(CurrencyType.MONEY, income);
                                } catch (NumberFormatException e) {
                                }
                            }

                            double points = 0D;
                            if (sep.length >= 3) {
                                try {
                                    points = Double.parseDouble(sep[2]);
                                    points = updateValue(CurrencyType.POINTS, points);
                                } catch (NumberFormatException e) {
                                }
                            }

                            double experience = 0D;
                            if (sep.length >= 4) {
                                try {
                                    experience = Double.parseDouble(sep[3]);
                                    experience = updateValue(CurrencyType.EXP, experience);
                                } catch (NumberFormatException e) {
                                }
                            }

                            jobInfo.add(new JobInfo(actionType, id, meta, type + subType, income, incomeEquation, experience, expEquation, pointsEquation, points, 1,
                                -1, typeSection.getCurrentPath(), null, null, null));
                        }
                        job.setJobInfo(actionType, jobInfo);
                    }

                    for (String key : typeSection.getKeys(false)) {

                        if (key.equalsIgnoreCase("materials"))
                            continue;

                        ConfigurationSection section = typeSection.getConfigurationSection(key);
                        if (section == null) {
                            continue;
                        }

                        KeyValues keyValue = getKeyValue(key, actionType, jobConfigName);
                        if (keyValue == null)
                            continue;

                        int id = keyValue.getId();
                        String type = keyValue.getType(),
                            subType = keyValue.getSubType(),
                            meta = keyValue.getMeta();

                        double income = section.getDouble("income");
                        income = updateValue(CurrencyType.MONEY, income);
                        double points = section.getDouble("points");
                        points = updateValue(CurrencyType.POINTS, points);
                        double experience = section.getDouble("experience");
                        experience = updateValue(CurrencyType.EXP, experience);

                        int fromlevel = section.getInt("from-level", 1);

                        int untilLevel = -1;
                        if (section.isInt("until-level")) {
                            untilLevel = section.getInt("until-level");
                            if (untilLevel < fromlevel) {
                                log.warning("Job " + jobConfigName + " has an invalid until-level in " + actionType.getName() + " for type property: " + key
                                    + "! It will be not set.");
                                untilLevel = -1;
                            }
                        }

                        Integer itemSoftIncomeLimit = softIncomeLimit;
                        if (section.isInt("softIncomeLimit"))
                            itemSoftIncomeLimit = section.getInt("softIncomeLimit");
                        Integer itemSoftExpLimit = softExpLimit;
                        if (section.isInt("softExpLimit"))
                            itemSoftExpLimit = section.getInt("softExpLimit");
                        Integer itemSoftPointsLimit = softPointsLimit;
                        if (section.isInt("softPointsLimit"))
                            itemSoftPointsLimit = section.getInt("softPointsLimit");

                        jobInfo.add(new JobInfo(actionType, id, meta, type + subType, income, incomeEquation, experience, expEquation, pointsEquation, points, fromlevel,
                            untilLevel, section.getCurrentPath(), itemSoftIncomeLimit, itemSoftExpLimit, itemSoftPointsLimit));
                    }
                }
                job.setJobInfo(actionType, jobInfo);
            }

            if (isNoneJob)
                Jobs.setNoneJob(job);
            else {
                return job;
            }
        }

        return null;
    }

    private double updateValue(CurrencyType type, double amount) {
        return amount += (amount * Jobs.getGCManager().getGeneralMulti(type));
    }
}
