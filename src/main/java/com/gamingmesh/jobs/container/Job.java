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
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;

public class Job {
    // job info
    private EnumMap<ActionType, List<JobInfo>> jobInfo = new EnumMap<ActionType, List<JobInfo>>(ActionType.class);
    // permissions
    private List<JobPermission> jobPermissions;
    // commands
    private List<JobCommands> jobCommands;
    // conditions
    private List<JobConditions> jobConditions;
    // items
    private List<JobItems> jobItems;
    // limited items
    private List<JobLimitedItems> jobLimitedItems;
    // job name
    private String jobName = "N/A";
    // job short name (for use in multiple jobs)
    private String jobShortName;
    // short description of the job
    private String description;
    // job chat colour
    private ChatColor jobColour;
    // job leveling equation
    private Parser maxExpEquation;
    // display method
    private DisplayMethod displayMethod;
    // max level
    private int maxLevel;
    // vip max level
    private int vipmaxLevel = 0;
    // max number of people allowed with this job on the server.
    private Integer maxSlots;
    // Commands to be performed on player job join
    private List<String> CmdOnJoin = new ArrayList<String>();
    // Commands to be performed on player job leave
    private List<String> CmdOnLeave = new ArrayList<String>();
    // Item for GUI
    private ItemStack GUIitem;
    // Item for GUI
    private Long rejoinCd = 0L;

    private int totalPlayers = -1;
    private Double bonus = null;

    private BoostMultiplier boost = new BoostMultiplier();
    private String bossbar;

    private Parser moneyEquation, xpEquation, pointsEquation;

    private List<String> fDescription = new ArrayList<String>();

    private List<Quest> quests = new ArrayList<Quest>();
    private int maxDailyQuests = 1;

    /**
     * Constructor
     * @param jobName - the name of the job
     * @param jobShortName - the shortened version of the name of the job.
     * @param description - a short description of the job.
     * @param jobColour - the colour of the job title as displayed in chat.
     * @param maxExpEquation - the equation by which the exp needed to level up is calculated
     * @param displayMethod - the display method for this job.
     * @param maxLevel - the maximum level allowed (null for no max level)
     * @param vipmaxLevel - the maximum vip level allowed (null for no max level)
     * @param maxSlots - the maximum number of people allowed to have this job at one time (null for no limits)
     * @param jobPermissions - permissions gained for having the job
     * @param jobCommands - commands to perform on levelup
     * @param jobItems - items with boost
     * @param jobLimitedItems - limited items by lvl
     * @param CmdOnJoin - commands performed on player join
     * @param CmdOnLeave - commands performed on player leave
     * @param jobConditions - jobs conditions
     */
    public Job(String jobName, String jobShortName, String description, ChatColor jobColour, Parser maxExpEquation, DisplayMethod displayMethod, int maxLevel,
	int vipmaxLevel, Integer maxSlots, List<JobPermission> jobPermissions, List<JobCommands> jobCommands, List<JobConditions> jobConditions, List<JobItems> jobItems,
	List<JobLimitedItems> jobLimitedItems, List<String> CmdOnJoin, List<String> CmdOnLeave, ItemStack GUIitem, String bossbar, Long rejoinCD) {
	this.jobName = jobName;
	this.jobShortName = jobShortName;
	this.description = description;
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
	this.CmdOnJoin = CmdOnJoin;
	this.CmdOnLeave = CmdOnLeave;
	this.GUIitem = GUIitem;
	this.bossbar = bossbar;
	this.rejoinCd = rejoinCD;
    }

    public void addBoost(CurrencyType type, double Point) {
	this.boost.add(type, Point - 1D);
    }

    public void setBoost(BoostMultiplier BM) {
	this.boost = BM;
    }

    public BoostMultiplier getBoost() {
	return this.boost;
    }

    public boolean isSame(Job job) {
	if (job == null)
	    return false;
	return this.getName().equalsIgnoreCase(job.getName());
    }

    public int getTotalPlayers() {
	if (this.totalPlayers == -1) {
	    this.totalPlayers = Jobs.getJobsDAO().getTotalPlayerAmountByJobName(this.jobName);
	    updateBonus();
	}
	return this.totalPlayers;
    }

    public void updateTotalPlayers() {
	this.totalPlayers = Jobs.getJobsDAO().getTotalPlayerAmountByJobName(this.jobName);
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
	if (this.bonus == null)
	    updateBonus();
	return this.bonus == null ? 0D : this.bonus;
    }

    public List<String> getCmdOnJoin() {
	return this.CmdOnJoin;
    }

    public List<String> getCmdOnLeave() {
	return this.CmdOnLeave;
    }

    public ItemStack getGuiItem() {
	return this.GUIitem;
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
	for (JobInfo info : getJobInfo(action.getType())) {
	    if (info.getName().equalsIgnoreCase(action.getName()) || info.getName().equalsIgnoreCase(action.getNameWithSub())) {
		if (!info.isInLevelRange(level))
		    break;
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
	return jobName;
    }

    /**
     * Get the shortened version of the jobName
     * @return the shortened version of the jobName
     */
    public String getShortName() {
	return jobShortName;
    }

    /**
     * Gets the description
     * @return description
     */
    public String getDescription() {
	return description;
    }

    /**
     * Get the Color of the job for chat
     * @return the Color of the job for chat
     */
    public ChatColor getChatColor() {
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
    public double getMaxExp(Map<String, Double> param) {
	for (Map.Entry<String, Double> temp : param.entrySet()) {
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
	if (player == null)
	    return getMaxLevel();
	return player.getMaxJobLevelAllowed(this);
    }

    public int getMaxLevel(CommandSender sender) {
	if (sender == null)
	    return getMaxLevel();
	if (sender instanceof Player) {
	    JobsPlayer player = Jobs.getPlayerManager().getJobsPlayer((Player) sender);
	    if (player != null)
		return player.getMaxJobLevelAllowed(this);
	}
	return getMaxLevel() > getVipMaxLevel() ? getMaxLevel() : getVipMaxLevel();
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
    public List<JobItems> getItems() {
	return Collections.unmodifiableList(jobItems);
    }

    /**
     * Get the limited item nodes for this job
     * @return Limited items for this job
     */
    public List<JobLimitedItems> getLimitedItems() {
	return Collections.unmodifiableList(jobLimitedItems);
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
	this.fDescription = fDescription;
    }

    public List<Quest> getQuests() {
	return quests;
    }

    public Quest getQuest(String name) {
	for (Quest one : quests) {
	    if (one.getConfigName().equalsIgnoreCase(name))
		return one;
	}
	return null;
    }

    public void setQuests(List<Quest> quests) {
	this.quests.clear();
	this.quests = quests;
    }

//    public Quest getNextQuest() {
//	return getNextQuest(null, null);
//    }

    public Quest getNextQuest(List<String> excludeQuests, Integer level) {
	List<Quest> ls = new ArrayList<Quest>(this.quests);
	Collections.shuffle(ls);

	int i = 0;
	while (true) {
	    i++;
	    Random rand = new Random(System.nanoTime());
	    int target = rand.nextInt(100);
	    for (Quest one : ls) {
		if (one.getChance() >= target)
		    if (excludeQuests == null || !excludeQuests.contains(one.getConfigName().toLowerCase())) {

			if (!one.isInLevelRange(level))
			    continue;

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
}
