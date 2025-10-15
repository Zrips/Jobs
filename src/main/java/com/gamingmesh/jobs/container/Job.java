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

package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Gui.GuiItem;
import com.gamingmesh.jobs.actions.EnchantActionInfo;
import com.gamingmesh.jobs.actions.PotionItemActionInfo;
import com.gamingmesh.jobs.container.JobsTop.topStats;
import com.gamingmesh.jobs.stuff.Util;
import com.gamingmesh.jobs.BoostManager;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Equations.Parser;
import net.Zrips.CMILib.Items.CMIMaterial;

public class Job {

    private Map<ActionType, List<JobInfo>> jobInfo = new EnumMap<>(ActionType.class);

    private List<JobPermission> jobPermissions;
    private List<JobCommands> jobCommands;
    private List<JobConditions> jobConditions;

//    private Map<String, JobItems> jobItems;
    private Map<String, JobLimitedItems> jobLimitedItems;

    private String jobName = "N/A";
    private String jobDisplayName;
    private String fullName = "N/A";

    // job short name (for use in multiple jobs)
    private String jobShortName;
    private String description;

    private CMIChatColor jobColour;
    private Parser maxExpEquation;
    private DisplayMethod displayMethod;

    private int maxLevel;
    private int vipmaxLevel = 0;

    // max number of people allowed with this job on the server.
    private Integer maxSlots;

    private List<String> cmdOnJoin = new ArrayList<>(), cmdOnLeave = new ArrayList<>();

    private GuiItem guiItem = null;

    private long rejoinCd = 0L;

    private int totalPlayers = -1;
    private Double bonus;

    private BoostMultiplier boost = new BoostMultiplier();
    private String bossbar;

    private Parser moneyEquation, xpEquation, pointsEquation;

    private final List<String> fDescription = new ArrayList<>(), maxLevelCommands = new ArrayList<>();
    private List<String> worldBlacklist = new ArrayList<>();
    private boolean reversedWorldBlacklist = false;

    private final List<Quest> quests = new ArrayList<>();
    private int maxDailyQuests = 1;
    private int id = 0;
    private int legacyId = 0;
    private boolean ignoreMaxJobs = false;

    private JobsTop topList = new JobsTop();

    public Job(String jobName) {
        this.jobName = jobName == null ? "" : jobName;
    }

    @Deprecated
    public Job(String jobName, String jobDisplayName, String fullName, String jobShortName, String description, CMIChatColor jobColour, Parser maxExpEquation, DisplayMethod displayMethod, int maxLevel,
        int vipmaxLevel, Integer maxSlots, List<JobPermission> jobPermissions, List<JobCommands> jobCommands, List<JobConditions> jobConditions, Map<String, JobItems> jobItems,
        Map<String, JobLimitedItems> jobLimitedItems, List<String> cmdOnJoin, List<String> cmdOnLeave, ItemStack guiItem, int guiSlot, String bossbar, Long rejoinCD, List<String> worldBlacklist) {
        this(jobName, jobDisplayName, fullName, jobShortName, jobColour, maxExpEquation, displayMethod, maxLevel,
            vipmaxLevel, maxSlots, jobPermissions, jobCommands, jobConditions,
            jobLimitedItems, cmdOnJoin, cmdOnLeave, guiItem, guiSlot, worldBlacklist);

//        this.jobItems = jobItems;
        this.description = description;
    }

    @Deprecated
    public Job(String jobName, String jobDisplayName, String fullName, String jobShortName, CMIChatColor jobColour, Parser maxExpEquation, DisplayMethod displayMethod, int maxLevel,
        int vipmaxLevel, Integer maxSlots, List<JobPermission> jobPermissions, List<JobCommands> jobCommands, List<JobConditions> jobConditions,
        Map<String, JobLimitedItems> jobLimitedItems, List<String> cmdOnJoin, List<String> cmdOnLeave, ItemStack guiItem, int guiSlot, List<String> worldBlacklist) {
        this.jobName = jobName == null ? "" : jobName;
        this.fullName = fullName == null ? "" : fullName;
        this.jobShortName = jobShortName;
        this.jobColour = jobColour;
        this.maxExpEquation = maxExpEquation;
        this.displayMethod = displayMethod;
        this.maxLevel = maxLevel;
        this.vipmaxLevel = vipmaxLevel;
        this.maxSlots = maxSlots;
        this.jobPermissions = jobPermissions;
        this.jobCommands = jobCommands;
        this.jobConditions = jobConditions;
        this.jobLimitedItems = jobLimitedItems;
        this.cmdOnJoin = cmdOnJoin;
        this.cmdOnLeave = cmdOnLeave;

        this.guiItem = (new GuiItem()).setGuiItem(guiItem).setGuiSlot(guiSlot);

        this.jobDisplayName = CMIChatColor.translate(jobDisplayName);

        this.worldBlacklist = worldBlacklist != null ? worldBlacklist : null;

    }

    /**
     * Adds specific amount of boost to the given currency type. If there was a boost
     * added before with the same currency type, it will be overridden to the new one.
     * 
     * @param type the type of {@link CurrencyType}}
     * @param point the amount of boost to add
     */
    public void addBoost(CurrencyType type, double point) {
        boost.add(type, point);
        // Notify boost manager to save the updated boosts
        try {
            BoostManager.onBoostAdded();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds specific amount of boost to the given currency type with the
     * specified array of times. If there was a boost added before with
     * the same currency type, it will be overridden to the new one.
     * <p>
     * The array of integer need at least to contain 3 elements
     * to calculate the time in milliseconds using {@link Calendar}.
     * 
     * @param type the type of {@link CurrencyType}}
     * @param point the amount of boost to add
     * @param duration boost duration in seconds
     */
    public void addBoost(CurrencyType type, double point, long duration) {

        if (duration <= 0) {
            addBoost(type, point);
            return;
        }

        boost.add(type, point, System.currentTimeMillis() + (duration * 1000L));
        // Notify boost manager to save the updated boosts
        try {
            BoostManager.onBoostAdded();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setBoost(BoostMultiplier boost) {
        this.boost = boost;
    }

    public BoostMultiplier getBoost() {
        return boost;
    }

    /**
     * Checks if the given {@link Job} is the same with this instance.
     * 
     * @param job the {@link Job} to compare with it
     * @return true if same
     */
    public boolean isSame(Job job) {
        return job != null && (id == job.getId() || jobName.equalsIgnoreCase(job.getName())
            || fullName.equalsIgnoreCase(job.getJobFullName()) || fullName.equalsIgnoreCase(job.getName()));
    }

    /**
     * Returns the total players retrieved synchronously from current database.
     * 
     * @return the amount of total players in this job
     */
    public int getTotalPlayers() {
        if (totalPlayers == -1) {
            updateTotalPlayers();
        }

        return totalPlayers;
    }

    /**
     * 
     * @param change the amount to change
     * @return the new total
     */
    public int modifyTotalPlayerWorking(int change) {
        totalPlayers = CMINumber.clamp(totalPlayers + change, 0, Integer.MAX_VALUE);
        updateBonus();
        return totalPlayers;
    }

    /**
     * Updates the total players property from database synchronously.
     */
    public void updateTotalPlayers() {
        totalPlayers = Jobs.getJobsDAO().getTotalPlayerAmountByJobName(jobName);

        if (totalPlayers <= 0) {
            totalPlayers = Jobs.getJobsDAO().getTotalPlayerAmountByJobName(fullName);
        }

        updateBonus();
    }

    public void updateBonus() {
        if (!Jobs.getGCManager().useDynamicPayment)
            return;

        Parser eq = Jobs.getGCManager().DynamicPaymentEquation;
        eq.setVariable("totalworkers", Jobs.getJobsDAO().getTotalPlayers());
        eq.setVariable("totaljobs", Jobs.getJobs().size());
        eq.setVariable("jobstotalplayers", getTotalPlayers());

        double now = 0D;
        try {
            now = eq.getValue();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (now > Jobs.getGCManager().DynamicPaymentMaxBonus)
            now = Jobs.getGCManager().DynamicPaymentMaxBonus;

        if (now < Jobs.getGCManager().DynamicPaymentMaxPenalty)
            now = Jobs.getGCManager().DynamicPaymentMaxPenalty;

        if (Double.isNaN(now))
            now = 0;

        this.bonus = now;
    }

    public double getBonus() {
        if (bonus == null)
            updateBonus();

        return bonus == null ? 0D : bonus;
    }

    public List<String> getCmdOnJoin() {
        return cmdOnJoin;
    }

    public Job setCmdOnJoin(List<String> cmdOnJoin) {
        this.cmdOnJoin = cmdOnJoin;
        return this;
    }

    public List<String> getCmdOnLeave() {
        return cmdOnLeave;
    }

    public Job setCmdOnLeave(List<String> cmdOnLeave) {
        this.cmdOnLeave = cmdOnLeave;
        return this;
    }

    public void setGuiItem(GuiItem guiItem) {
        this.guiItem = guiItem;
    }

    public ItemStack getGuiItem() {
        return guiItem == null ? CMIMaterial.STONE.newItemStack() : guiItem.getGuiItem();
    }

    public int getGuiSlot() {
        return guiItem == null ? -1 : guiItem.getGuiSlot();
    }

    /**
     * Sets job info for action type
     * @param type - The action type
     * @param info - the job info
     */
    public void setJobInfo(ActionType type, List<JobInfo> info) {
        jobInfo.put(type, info);
    }

    /**
     * Gets the job info for the particular type
     * @param type - The action type
     * @return Job info list
     */
    public List<JobInfo> getJobInfo(ActionType type) {
        return jobInfo.get(type);
    }

    /**
     * Gets the job info list
     * @return Job info list
     */
    public Map<ActionType, List<JobInfo>> getJobInfoList() {
        return jobInfo;
    }

    public JobInfo getJobInfo(ActionInfo action, int level) {
        BiPredicate<JobInfo, ActionInfo> condition = (jobInfo, actionInfo) -> {
            if (actionInfo instanceof PotionItemActionInfo) {
                String subName = ((PotionItemActionInfo) action).getNameWithSub();
                return jobInfo.getName().equalsIgnoreCase(subName) || (jobInfo.getName() + ":" + jobInfo.getMeta()).equalsIgnoreCase(subName);
            }

            if (actionInfo instanceof EnchantActionInfo) {
                return Util.enchantMatchesActionInfo(jobInfo.getName(), (EnchantActionInfo) actionInfo);
            }

            return jobInfo.getName().equalsIgnoreCase(action.getNameWithSub()) ||
                (jobInfo.getName() + ":" + jobInfo.getMeta()).equalsIgnoreCase(action.getNameWithSub()) ||
                jobInfo.getName().equalsIgnoreCase(action.getName());
        };

        String shortActionName = CMIMaterial.getGeneralMaterialName(action.getName());

        for (JobInfo info : getJobInfo(action.getType())) {
            if (condition.test(info, action)) {
                if (!info.isInLevelRange(level)) {
                    break;
                }
                return info;
            }

            if ((shortActionName + ":ALL").equalsIgnoreCase(info.getName())) {
                return info;
            }
        }

        return null;
    }

    /**
     * Returns the name of this job
     * 
     * @return the name of this job
     */
    public String getName() {
        return jobName;
    }

    public String getJobFullName() {
        return fullName;
    }

    public Job setJobFullName(String fullName) {
        this.fullName = fullName == null ? "" : fullName;
        return this;
    }

    @Deprecated
    public String getJobDisplayName() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return jobDisplayName == null ? jobColour + fullName : jobDisplayName;
    }

    public Job setDisplayName(String jobDisplayName) {
        this.jobDisplayName = CMIChatColor.translate(jobDisplayName);
        return this;
    }

    /**
     * Return the job full name with the set of color.
     * 
     * @return the full name with color
     * @deprecated use {@link #getJobDisplayName()} instead
     */
    @Deprecated
    public String getNameWithColor() {
        return jobColour + fullName;
    }

    /**
     * Get the shortened version of the jobName
     * 
     * @return the shortened version of the jobName
     */
    public String getShortName() {
        return jobShortName;
    }

    public Job setShortName(String jobShortName) {
        this.jobShortName = jobShortName;
        return this;
    }

    /**
     * Gets the description
     * 
     * @return description
     * @deprecated Description can be list instead
     * of plain string, use {@link #getFullDescription()}
     */
    @Deprecated
    public String getDescription() {
        return description;
    }

    public Job setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get the Color of the job for chat
     * @return the Color of the job for chat
     */
    public CMIChatColor getChatColor() {
        return jobColour;
    }

    public Job setChatColor(CMIChatColor jobColour) {
        this.jobColour = jobColour;
        return this;
    }

    /**
     * Get the MaxExpEquation of the job
     * @return the MaxExpEquation of the job
     */
    public Parser getMaxExpEquation() {
        return maxExpEquation;
    }

    public Job setMaxExpEquation(Parser maxExpEquation) {
        this.maxExpEquation = maxExpEquation;
        return this;
    }

    /**
     * Function to return the appropriate max exp for this level
     * @param level - current level
     * @return the correct max exp for this level
     */
    public double getMaxExp(Map<String, Double> level) {
        for (Map.Entry<String, Double> temp : level.entrySet()) {
            maxExpEquation.setVariable(temp.getKey(), temp.getValue());
        }
        return maxExpEquation.getValue();
    }

    /**
     * Function to get the display method
     * @return the display method
     */
    public DisplayMethod getDisplayMethod() {
        return displayMethod;
    }

    public Job setDisplayMethod(DisplayMethod displayMethod) {
        this.displayMethod = displayMethod;
        return this;
    }

    /**
     * Function to return the maximum level of this job.
     * 
     * @return the max level
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    public Job setMaxLevel(int maxLevel) {
        this.maxLevel = CMINumber.clamp(maxLevel, 0, Integer.MAX_VALUE);
        return this;
    }

    /**
     * Returns the maximum level of the specific {@link JobsPlayer}.
     * 
     * @param player the {@link JobsPlayer} or null
     * @return the max level of player
     */
    public int getMaxLevel(JobsPlayer player) {
        return player == null ? maxLevel : player.getMaxJobLevelAllowed(this);
    }

    public int getMaxLevel(CommandSender sender) {
        if (sender == null)
            return maxLevel;

        if (sender instanceof Player) {
            JobsPlayer player = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
            if (player != null)
                return player.getMaxJobLevelAllowed(this);
        }

        return maxLevel > vipmaxLevel ? maxLevel : vipmaxLevel;
    }

    /**
     * Function to return the maximum level
     * @return the max level
     * @return null - no max level
     */
    public int getVipMaxLevel() {
        return vipmaxLevel;
    }

    public Job setVipMaxLevel(int vipmaxLevel) {
        this.vipmaxLevel = CMINumber.clamp(vipmaxLevel, 0, Integer.MAX_VALUE);
        return this;
    }

    /**
     * Function to return the maximum slots
     * @return the max slots
     * @return null - no max slots
     */
    public Integer getMaxSlots() {
        return maxSlots;
    }

    public Job setMaxSlots(Integer maxSlots) {
        this.maxSlots = maxSlots <= 0 ? null : maxSlots;
        return this;
    }

    /**
     * Get the permission nodes for this job
     * @return Permissions for this job
     */
    public List<JobPermission> getPermissions() {
        return Collections.unmodifiableList(jobPermissions);
    }

    public Job setPermissions(List<JobPermission> jobPermissions) {
        this.jobPermissions = jobPermissions;
        return this;
    }

    /**
     * Get the command nodes for this job
     * @return Commands for this job
     */
    public List<JobCommands> getCommands() {
        return Collections.unmodifiableList(jobCommands);
    }

    public Job setCommands(List<JobCommands> jobCommands) {
        this.jobCommands = jobCommands;
        return this;
    }

    /**
     * Get the conditions for this job
     * @return Conditions for this job
     */
    public List<JobConditions> getConditions() {
        return Collections.unmodifiableList(jobConditions);
    }

    public Job setConditions(List<JobConditions> jobConditions) {
        this.jobConditions = jobConditions;
        return this;
    }

    /**
     * Get the limited item nodes for this job
     * @return Limited items for this job
     */
    public Map<String, JobLimitedItems> getLimitedItems() {
        return jobLimitedItems;
    }

    public Job setLimitedItems(Map<String, JobLimitedItems> jobLimitedItems) {
        this.jobLimitedItems = jobLimitedItems;
        return this;
    }

    public JobLimitedItems getLimitedItems(String key) {
        return jobLimitedItems.get(key.toLowerCase());
    }

    public String getBossbar() {
        return bossbar;
    }

    public void setBossbar(String bossbar) {
        this.bossbar = bossbar;

        // Need to reset boss bar cache for all online players in case jobs config file was reloaded
        for (Player player : Bukkit.getOnlinePlayers()) {
            JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
            if (jPlayer != null)
                jPlayer.clearBossMaps();
        }
    }

    public Parser getMoneyEquation() {
        return moneyEquation;
    }

    public void setMoneyEquation(Parser moneyEquation) {
        this.moneyEquation = moneyEquation;
    }

    public Parser getXpEquation() {
        return xpEquation;
    }

    public void setXpEquation(Parser xpEquation) {
        this.xpEquation = xpEquation;
    }

    public Parser getPointsEquation() {
        return pointsEquation;
    }

    public void setPointsEquation(Parser pointsEquation) {
        this.pointsEquation = pointsEquation;
    }

    public Long getRejoinCd() {
        return rejoinCd;
    }

    public void setRejoinCd(Long rejoinCd) {
        this.rejoinCd = CMINumber.clamp(rejoinCd, 0, Long.MAX_VALUE);
    }

    public List<String> getFullDescription() {
        return fDescription;
    }

    public void setFullDescription(List<String> fDescription) {
        this.fDescription.clear();

        if (fDescription != null) {
            this.fDescription.addAll(fDescription);
            this.description = String.join("\n", this.fDescription);
        }
    }

    public void setMaxLevelCommands(List<String> commands) {
        maxLevelCommands.clear();

        if (commands != null) {
            maxLevelCommands.addAll(commands);
        }
    }

    public List<String> getMaxLevelCommands() {
        return maxLevelCommands;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public Quest getQuest(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        for (Quest one : quests) {
            if (one.getConfigName().equalsIgnoreCase(name))
                return one;
        }

        return null;
    }

    public void setQuests(List<Quest> quests) {
        this.quests.clear();

        if (quests != null) {
            this.quests.addAll(quests);
        }
    }

    public Quest getNextQuest(List<String> excludeQuests, Integer level) {
        List<Quest> ls = new ArrayList<>(quests);
        Collections.shuffle(ls);

        int i = 0;
        while (true) {
            i++;

            int target = new Random(System.nanoTime()).nextInt(100);
            for (Quest one : ls) {
                if (one.isEnabled() && one.getChance() >= target && (excludeQuests == null || !excludeQuests.contains(one.getConfigName().toLowerCase()))
                    && one.isInLevelRange(level)) {
                    return one;
                }
            }

            if (i > 20)
                return null;
        }
    }

    public int getMaxDailyQuests() {
        return maxDailyQuests;
    }

    public void setMaxDailyQuests(int maxDailyQuests) {
        this.maxDailyQuests = maxDailyQuests;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    public Job setWorldBlacklist(List<String> worldBlacklist) {
        this.worldBlacklist = worldBlacklist != null ? worldBlacklist : null;
        return this;
    }

    public boolean isWorldBlackListed(Entity ent) {
        return isWorldBlackListed(null, ent, null);
    }

    public boolean isWorldBlackListed(Block block) {
        return isWorldBlackListed(block, null, null);
    }

    @Deprecated
    public boolean isWorldBlackListed(Block block, Entity ent) {
        return isWorldBlackListed(block, ent, null);
    }

    public boolean isWorldBlackListed(Block block, Entity ent, LivingEntity lent) {
        if (block != null)
            return isWorldBlackListed(block.getWorld());
        if (ent != null)
            return isWorldBlackListed(ent.getWorld());
        if (lent != null)
            return isWorldBlackListed(lent.getWorld());
        return false;
    }

    public boolean isWorldBlackListed(World world) {
        if (worldBlacklist.isEmpty())
            return reversedWorldBlacklist;
        return world != null && worldBlacklist.contains(world.getName()) != reversedWorldBlacklist;
    }

    public boolean isReversedWorldBlacklist() {
        return reversedWorldBlacklist;
    }

    public void setReversedWorldBlacklist(boolean reversedWorldBlacklist) {
        this.reversedWorldBlacklist = reversedWorldBlacklist;
    }

    public boolean isIgnoreMaxJobs() {
        return ignoreMaxJobs;
    }

    public void setIgnoreMaxJobs(boolean ignoreMaxJobs) {
        this.ignoreMaxJobs = ignoreMaxJobs;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Job && isSame((Job) obj);
    }

    public void setJobDisplayName(String jobDisplayName) {
        this.jobDisplayName = jobDisplayName;
    }

    public int getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(int legacyId) {
        this.legacyId = legacyId;
    }

    public void updateTop(@NotNull UUID uuid, int level, double experience) {
        topList.updateAsync(uuid, level, experience);
    }

    public void removeFromTop(UUID uuid) {
        topList.removeAsync(uuid);
    }

    public UUID getTop(int index) {
        return topList.getByPosition(index);
    }

    public List<UUID> getTopList(int limit) {
        return topList.getTop(limit);
    }

    public topStats getTopStats(UUID uuid) {
        return topList.getStats(uuid);
    }
}
