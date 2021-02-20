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
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiPredicate;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.actions.PotionItemActionInfo;
import com.gamingmesh.jobs.resources.jfep.Parser;

public class Job {

    private EnumMap<ActionType, List<JobInfo>> jobInfo = new EnumMap<>(ActionType.class);

    private List<JobPermission> jobPermissions;
    private List<JobCommands> jobCommands;
    private List<JobConditions> jobConditions;

    private HashMap<String, JobItems> jobItems;
    private HashMap<String, JobLimitedItems> jobLimitedItems;

    private String jobName = "N/A";
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

    private ItemStack guiItem;
    private int guiSlot = 0;

    private Long rejoinCd = 0L;

    private int totalPlayers = -1;
    private Double bonus;

    private BoostMultiplier boost = new BoostMultiplier();
    private String bossbar;

    private Parser moneyEquation, xpEquation, pointsEquation;

    private final List<String> fDescription = new ArrayList<>();
    private List<String> worldBlacklist = new ArrayList<>();

    private final List<Quest> quests = new ArrayList<>();
    private int maxDailyQuests = 1;
    private int id = 0;

    @Deprecated
    public Job(String jobName, String fullName, String jobShortName, String description, CMIChatColor jobColour, Parser maxExpEquation, DisplayMethod displayMethod, int maxLevel,
	int vipmaxLevel, Integer maxSlots, List<JobPermission> jobPermissions, List<JobCommands> jobCommands, List<JobConditions> jobConditions, HashMap<String, JobItems> jobItems,
	HashMap<String, JobLimitedItems> jobLimitedItems, List<String> cmdOnJoin, List<String> cmdOnLeave, ItemStack guiItem, int guiSlot, String bossbar, Long rejoinCD, List<String> worldBlacklist) {
	this(jobName, fullName, jobShortName, jobColour, maxExpEquation, displayMethod, maxLevel,
	    vipmaxLevel, maxSlots, jobPermissions, jobCommands, jobConditions, jobItems,
	    jobLimitedItems, cmdOnJoin, cmdOnLeave, guiItem, guiSlot, bossbar, rejoinCD, worldBlacklist);

	this.description = description;
    }

    public Job(String jobName, String fullName, String jobShortName, CMIChatColor jobColour, Parser maxExpEquation, DisplayMethod displayMethod, int maxLevel,
	int vipmaxLevel, Integer maxSlots, List<JobPermission> jobPermissions, List<JobCommands> jobCommands, List<JobConditions> jobConditions, HashMap<String, JobItems> jobItems,
	HashMap<String, JobLimitedItems> jobLimitedItems, List<String> cmdOnJoin, List<String> cmdOnLeave, ItemStack guiItem, int guiSlot, String bossbar, Long rejoinCD, List<String> worldBlacklist) {
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
	this.jobItems = jobItems;
	this.jobLimitedItems = jobLimitedItems;
	this.cmdOnJoin = cmdOnJoin;
	this.cmdOnLeave = cmdOnLeave;
	this.guiItem = guiItem;
	this.guiSlot = guiSlot;
	this.bossbar = bossbar;
	this.rejoinCd = rejoinCD;

	if (worldBlacklist != null) {
	    this.worldBlacklist = worldBlacklist;
	}
    }

    public void addBoost(CurrencyType type, double point) {
	boost.add(type, point);
    }

    public void addBoost(CurrencyType type, double point, int[] times) {
	final int h = times[2], m = times[1], s = times[0];
	if (h == 0 && m == 0 && s == 0) {
	    addBoost(type, point);
	    return;
	}

	final Calendar cal = Calendar.getInstance();
	cal.setTime(new Date());

	if (h > 0) {
	    cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + h);
	}

	if (m > 0) {
	    cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + m);
	}

	if (s > 0) {
	    cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + s);
	}

	boost.add(type, point, cal.getTimeInMillis());
    }

    public void setBoost(BoostMultiplier boost) {
	this.boost = boost;
    }

    public BoostMultiplier getBoost() {
	return boost;
    }

    public boolean isSame(Job job) {
	return job != null && (getName().equalsIgnoreCase(job.getName()) || id == job.getId());
    }

    public int getTotalPlayers() {
	if (totalPlayers == -1) {
	    updateTotalPlayers();
	}

	return totalPlayers;
    }

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

	double now = eq.getValue();
	if (now > Jobs.getGCManager().DynamicPaymentMaxBonus)
	    now = Jobs.getGCManager().DynamicPaymentMaxBonus;
	if (now < Jobs.getGCManager().DynamicPaymentMaxPenalty * -1)
	    now = Jobs.getGCManager().DynamicPaymentMaxPenalty * -1;

	this.bonus = (now / 100D);
    }

    public double getBonus() {
	if (bonus == null)
	    updateBonus();

	return bonus == null ? 0D : bonus;
    }

    public List<String> getCmdOnJoin() {
	return cmdOnJoin;
    }

    public List<String> getCmdOnLeave() {
	return cmdOnLeave;
    }

    public ItemStack getGuiItem() {
	return guiItem;
    }

    public int getGuiSlot() {
	return guiSlot;
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
    public EnumMap<ActionType, List<JobInfo>> getJobInfoList() {
	return jobInfo;
    }

    public JobInfo getJobInfo(ActionInfo action, int level) {
	BiPredicate<JobInfo, ActionInfo> condition = (jobInfo, actionInfo) -> {
	    if (actionInfo instanceof PotionItemActionInfo) {
		return jobInfo.getName().equalsIgnoreCase(((PotionItemActionInfo) action).getNameWithSub()) ||
		    (jobInfo.getName() + ":" + jobInfo.getMeta()).equalsIgnoreCase(((PotionItemActionInfo) action).getNameWithSub());
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
     * Get the job name
     * @return the job name
     */
    public String getName() {
	return fullName;
    }

    public String getNameWithColor() {
	return jobColour + fullName;
    }

    /**
     * Get the job name from the config
     * @return the job name from the config
     */
    public String getJobKeyName() {
	return jobName;
    }

    /**
     * Get the shortened version of the jobName
     * 
     * @return the shortened version of the jobName
     */
    public String getShortName() {
	return jobShortName;
    }

    /**
     * Gets the description
     * 
     * @return description
     * @deprecated Not used anymore, use {@link #getFullDescription()} instead
     */
    @Deprecated
    public String getDescription() {
	return description;
    }

    /**
     * Get the Color of the job for chat
     * @return the Color of the job for chat
     */
    public CMIChatColor getChatColor() {
	return jobColour;
    }

    /**
     * Get the MaxExpEquation of the job
     * @return the MaxExpEquation of the job
     */
    public Parser getMaxExpEquation() {
	return maxExpEquation;
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

    /**
     * Function to return the maximum level
     * @return the max level
     * @return null - no max level
     */
    public int getMaxLevel() {
	return maxLevel;
    }

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

    /**
     * Function to return the maximum slots
     * @return the max slots
     * @return null - no max slots
     */
    public Integer getMaxSlots() {
	return maxSlots;
    }

    /**
     * Get the permission nodes for this job
     * @return Permissions for this job
     */
    public List<JobPermission> getPermissions() {
	return Collections.unmodifiableList(jobPermissions);
    }

    /**
     * Get the command nodes for this job
     * @return Commands for this job
     */
    public List<JobCommands> getCommands() {
	return Collections.unmodifiableList(jobCommands);
    }

    /**
     * Get the conditions for this job
     * @return Conditions for this job
     */
    public List<JobConditions> getConditions() {
	return Collections.unmodifiableList(jobConditions);
    }

    /**
     * Get the item nodes for this job
     * @return Items for this job
     */
    @Deprecated
    public HashMap<String, JobItems> getItemBonus() {
	if (jobItems == null)
	    jobItems = new HashMap<String, JobItems>();
	return jobItems;
    }

    @Deprecated
    public JobItems getItemBonus(String key) {
	return jobItems.get(key.toLowerCase());
    }

    /**
     * Get the limited item nodes for this job
     * @return Limited items for this job
     */
    public HashMap<String, JobLimitedItems> getLimitedItems() {
	return jobLimitedItems;
    }

    public JobLimitedItems getLimitedItems(String key) {
	return jobLimitedItems.get(key.toLowerCase());
    }

    public String getBossbar() {
	return bossbar;
    }

    public void setBossbar(String bossbar) {
	this.bossbar = bossbar;
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
	this.rejoinCd = rejoinCd;
    }

    public List<String> getFullDescription() {
	return fDescription;
    }

    public void setFullDescription(List<String> fDescription) {
	this.fDescription.clear();

	if (fDescription != null) {
	    this.fDescription.addAll(fDescription);
	}
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
	this.quests.addAll(quests == null ? new ArrayList<>() : quests);
    }

//    public Quest getNextQuest() {
//	return getNextQuest(null, null);
//    }

    public Quest getNextQuest(List<String> excludeQuests, Integer level) {
	List<Quest> ls = new ArrayList<>(quests);
	Collections.shuffle(ls);

	int i = 0;
	while (true) {
	    i++;

	    int target = new Random(System.nanoTime()).nextInt(100);
	    for (Quest one : ls) {
		if (one.getChance() >= target && (excludeQuests == null || !excludeQuests.contains(one.getConfigName().toLowerCase()))
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
	Jobs.getJobsIds().remove(this.id);

	this.id = id;
	if (id != 0)
	    Jobs.getJobsIds().put(id, this);
    }

    public List<String> getWorldBlacklist() {
	return worldBlacklist;
    }

    public boolean isWorldBlackListed(Entity ent) {
	return isWorldBlackListed(null, ent);
    }

    public boolean isWorldBlackListed(Block block) {
	return isWorldBlackListed(block, null);
    }

    public boolean isWorldBlackListed(Block block, Entity ent) {
	if (worldBlacklist.isEmpty())
	    return false;

	if (block != null && worldBlacklist.contains(block.getWorld().getName()))
	    return true;

	return ent != null && worldBlacklist.contains(ent.getWorld().getName());
    }
}
