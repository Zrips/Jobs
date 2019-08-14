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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Signs.SignTopType;
import com.gamingmesh.jobs.Signs.SignUtil;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.FurnaceBrewingHandling;
import com.gamingmesh.jobs.stuff.TimeManage;

public class JobsPlayer {
    // the player the object belongs to
    private String userName;
    // progression of the player in each job
    public UUID playerUUID;
    public ArrayList<JobProgression> progression = new ArrayList<>();
    private ArchivedJobs archivedJobs = new ArchivedJobs();

    private PaymentData paymentLimits = null;

    private HashMap<String, ArrayList<BoostCounter>> boostCounter = new HashMap<>();

    // display honorific
    private String honorific;
    // player save status
    private volatile boolean isSaved = true;
    // player online status
    private volatile boolean isOnline = false;

    private HashMap<CurrencyType, Integer> limits = new HashMap<>();

    private int userid = -1;

    private List<BossBarInfo> barMap = new ArrayList<>();
    private List<String> updateBossBarFor = new ArrayList<>();
    // save lock
//    public final Object saveLock = new Object();

    // log
    private HashMap<String, Log> logList = new HashMap<>();

    private Long seen = System.currentTimeMillis();

    private HashMap<String, Boolean> permissionsCache = null;
    private Long lastPermissionUpdate = -1L;

    private HashMap<String, HashMap<String, QuestProgression>> qProgression = new HashMap<>();
    private int doneQuests = 0;

    public JobsPlayer(String userName) {
	this.userName = userName;
    }

    public ArchivedJobs getArchivedJobs() {
	return archivedJobs;
    }

    public JobProgression getArchivedJobProgression(Job job) {
	return archivedJobs.getArchivedJobProgression(job);
    }

    public void setArchivedJobs(ArchivedJobs archivedJob) {
	this.archivedJobs = archivedJob;
    }

    public int getTotalLevels() {
	int i = 0;
	for (JobProgression job : progression) {
	    i += job.getLevel();
	}
	return i;
    }

    public void setPaymentLimit(PaymentData paymentLimits) {
	this.paymentLimits = paymentLimits;
    }

    public PaymentData getPaymentLimit() {
	if (paymentLimits == null)
	    paymentLimits = Jobs.getJobsDAO().getPlayersLimits(this);

	if (paymentLimits == null)
	    paymentLimits = new PaymentData();
	return paymentLimits;
    }

    public boolean isUnderLimit(CurrencyType type, Double amount) {
	Player player = getPlayer();
	if (player == null)
	    return true;
	if (amount == 0)
	    return true;
	CurrencyLimit limit = Jobs.getGCManager().getLimit(type);
	if (!limit.isEnabled())
	    return true;
	PaymentData data = getPaymentLimit();
	Integer value = limits.get(type);
	if (data.IsReachedLimit(type, value == null ? 0 : value)) {
	    String name = type.getName().toLowerCase();

	    if (player.isOnline() && !data.isInformed() && !data.isReseted()) {
		if (Jobs.getGCManager().useMaxPaymentCurve) {
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit"));
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit2"));
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit3"));
		} else {
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit"));
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit2"));
		}
		data.setInformed();
	    }
	    if (data.IsAnnounceTime(limit.getAnnouncementDelay()) && player.isOnline())
		Jobs.getActionBar().send(player, Jobs.getLanguage().getMessage("command.limit.output." + name + "time", "%time%", TimeManage.to24hourShort(data.GetLeftTime(type))));
	    if (data.isReseted())
		data.setReseted(false);
	    return false;
	}
	data.AddAmount(type, amount);
	return true;
    }

    public double percentOverLimit(CurrencyType type) {
	Integer value = limits.get(type);
	PaymentData data = getPaymentLimit();
	return data.percentOverLimit(type, value == null ? 0 : value);
    }

    public void loadLogFromDao() {
	Jobs.getJobsDAO().loadLog(this);
    }

    public synchronized List<String> getUpdateBossBarFor() {
	return updateBossBarFor;
    }

    public synchronized void clearUpdateBossBarFor() {
	updateBossBarFor.clear();
    }

    public synchronized List<BossBarInfo> getBossBarInfo() {
	return barMap;
    }

    public synchronized void hideBossBars() {
	for (BossBarInfo one : barMap) {
	    one.getBar().setVisible(false);
	}
    }

    public HashMap<String, Log> getLog() {
	return logList;
    }

    public void setLog(HashMap<String, Log> logList) {
	this.logList = logList;
    }

    public void setUserId(int userid) {
	this.userid = userid;
    }

    public int getUserId() {
	return userid;
    }

    /**
     * Get the player
     * @return the player
     */
    public Player getPlayer() {
	if (playerUUID != null)
	    return Bukkit.getPlayer(playerUUID);

	return null;
    }

    /**
     * Get the Boost
     * @return the Boost
     */
    public double getBoost(String JobName, CurrencyType type) {
	return getBoost(JobName, type, false);
    }

    public double getBoost(String JobName, CurrencyType type, boolean force) {
	double Boost = 0D;
	if (!isOnline())
	    return Boost;
	if (type == null)
	    return Boost;

	long time = System.currentTimeMillis();

	if (boostCounter.containsKey(JobName)) {
	    ArrayList<BoostCounter> counterList = boostCounter.get(JobName);
	    for (BoostCounter counter : counterList) {
		if (counter.getType() != type)
		    continue;
		if (force || time - counter.getTime() > 1000 * 60) {
		    Boost = getPlayerBoostNew(JobName, type);
		    counter.setBoost(Boost);
		    counter.setTime(time);
		    return Boost;
		}
		return counter.getBoost();
	    }
	    Boost = getPlayerBoostNew(JobName, type);
	    counterList.add(new BoostCounter(type, Boost, time));
	    return Boost;
	}

	Boost = getPlayerBoostNew(JobName, type);

	ArrayList<BoostCounter> counterList = new ArrayList<>();
	counterList.add(new BoostCounter(type, Boost, time));

	boostCounter.put(JobName, counterList);
	return Boost;
    }

    private Double getPlayerBoostNew(String JobName, CurrencyType type) {
	Double Boost = null;
	Double v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost." + JobName + "." + type.getName().toLowerCase(), true);
	Boost = v1;
	v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost." + JobName + ".all");
	if (Boost == null || v1 != null && v1 > Boost)
	    Boost = v1;
	v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost.all.all");
	if (Boost == null || v1 != null && v1 > Boost)
	    Boost = v1;
	v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost.all." + type.getName().toLowerCase());
	if (Boost == null || v1 != null && v1 > Boost)
	    Boost = v1;
	return Boost == null ? 0D : Boost;
    }

    // New method is in use
//    private Double getPlayerBoost(String JobName, CurrencyType type) {
//	double Boost = 0D;
//	if (Perm.hasPermission(player, "jobs.boost." + JobName + "." + type.getName().toLowerCase()) ||
//	    Perm.hasPermission(player, "jobs.boost." + JobName + ".all") ||
//	    Perm.hasPermission(player, "jobs.boost.all.all") ||
//	    Perm.hasPermission(player, "jobs.boost.all." + type.getName().toLowerCase())) {
//	    Boost = Jobs.getGCManager().Boost.get(type);
//	}
//	return Boost;
//    }

    /**
     * Reloads max experience for this job.
     */
    public void reloadMaxExperience() {
	for (JobProgression prog : progression) {
	    prog.reloadMaxExperience();
	}
    }

    /**
     * Reloads limit for this player.
     */
    public void reload(CurrencyType type) {
	int TotalLevel = 0;
	for (JobProgression prog : progression) {
	    TotalLevel += prog.getLevel();
	}
	Parser eq = Jobs.getGCManager().getLimit(type).getMaxEquation();
	eq.setVariable("totallevel", TotalLevel);
	limits.put(type, (int) eq.getValue());
	setSaved(false);
    }

    public void reloadLimits() {
	for (CurrencyType type : CurrencyType.values()) {
	    reload(type);
	}
    }

    public int getLimit(CurrencyType type) {
	if (type == null)
	    return 0;
	Integer value = limits.get(type);
	return value == null ? 0 : value;
    }

    public void resetPaymentLimit() {
	if (paymentLimits == null)
	    getPaymentLimit();
	if (paymentLimits != null)
	    paymentLimits.resetLimits();
	setSaved(false);
    }

    /**
     * Get the list of job progressions
     * @return the list of job progressions
     */
    public List<JobProgression> getJobProgression() {
	return Collections.unmodifiableList(progression);
    }

    /**
     * Check if have permission
     * @return true if have
     */
    public boolean havePermission(String perm) {
	Player player = Bukkit.getPlayer(getPlayerUUID());
	if (player != null)
	    return player.hasPermission(perm);
	return false;
    }

    /**
     * Get the job progression with the certain job
     * @return the job progression
     */
    public JobProgression getJobProgression(Job job) {
	for (JobProgression prog : progression) {
	    if (prog.getJob().isSame(job))
		return prog;
	}
	return null;
    }

    /**
     * get the userName
     * @return the userName
     */
    public String getUserName() {
	Player player = Bukkit.getPlayer(getPlayerUUID());
	if (player != null)
	    userName = player.getName();
	return userName;
    }

    /**
     * get the playerUUID
     * @return the playerUUID
     */
    public UUID getPlayerUUID() {
	return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
	this.playerUUID = playerUUID;
    }

    public String getDisplayHonorific() {
	if (honorific == null)
	    reloadHonorific();
	return honorific;
    }

    /**
     * Player joins a job
     * @param job - the job joined
     */
    public boolean joinJob(Job job) {
//	synchronized (saveLock) {
	if (!isInJob(job)) {
	    int level = 1;
	    int exp = 0;

	    JobProgression archived = getArchivedJobProgression(job);
	    if (archived != null) {
		level = getLevelAfterRejoin(archived);
		exp = getExpAfterRejoin(archived, level);
		Jobs.getJobsDAO().deleteArchive(this, job);
	    }

	    progression.add(new JobProgression(job, this, level, exp));
	    reloadMaxExperience();
	    reloadLimits();
	    reloadHonorific();
	    Jobs.getPermissionHandler().recalculatePermissions(this);
	    return true;
	}
	return false;
//	}
    }

    public int getLevelAfterRejoin(JobProgression jp) {
	if (jp == null)
	    return 1;

	int level = jp.getLevel();

	level = (int) ((level - (level * (Jobs.getGCManager().levelLossPercentage / 100.0))));
	if (level < 1)
	    level = 1;

	Job job = jp.getJob();
	int maxLevel = getMaxJobLevelAllowed(job);
	if (jp.getLevel() == maxLevel) {
	    if (Jobs.getGCManager().fixAtMaxLevel)
		level = jp.getLevel();
	    else {
		level = jp.getLevel();
		level = (int) ((level - (level * (Jobs.getGCManager().levelLossPercentageFromMax / 100.0))));
		if (level < 1)
		    level = 1;
	    }
	}

	return level;
    }

    public int getExpAfterRejoin(JobProgression jp, int level) {
	if (jp == null)
	    return 1;
	Integer max = jp.getMaxExperience(level);
	Double exp = jp.getExperience();
	if (exp > max)
	    exp = max.doubleValue();

	if (exp > 0) {
	    Job job = jp.getJob();
	    int maxLevel = getMaxJobLevelAllowed(job);
	    if (jp.getLevel() == maxLevel) {
		if (!Jobs.getGCManager().fixAtMaxLevel)
		    exp = (exp - (exp * (Jobs.getGCManager().levelLossPercentageFromMax / 100.0)));
	    } else
		exp = (exp - (exp * (Jobs.getGCManager().levelLossPercentage / 100.0)));
	}
	return exp.intValue();
    }

    /**
     * Player leaves a job
     * @param job - the job left
     */
    public boolean leaveJob(Job job) {
//	synchronized (saveLock) {
	JobProgression prog = getJobProgression(job);
	if (prog != null) {
	    progression.remove(prog);
	    reloadMaxExperience();
	    reloadLimits();
	    reloadHonorific();
	    Jobs.getPermissionHandler().recalculatePermissions(this);
	    return true;
	}
	return false;
//	}
    }

    /**
     * Leave all jobs
     * @return on success
     */
    public boolean leaveAllJobs() {
//	synchronized (saveLock) {
	progression.clear();
	reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(this);
	return true;
//	}
    }

    /**
     * Promotes player in job
     * @param job - the job being promoted
     * @param levels - number of levels to promote
     */
    public void promoteJob(Job job, int levels) {
//	synchronized (saveLock) {
	JobProgression prog = getJobProgression(job);
	if (prog == null)
	    return;
	if (levels <= 0)
	    return;
	int oldLevel = prog.getLevel();
	int newLevel = oldLevel + levels;

	int maxLevel = job.getMaxLevel(this);

	if (maxLevel > 0 && newLevel > maxLevel)
	    newLevel = maxLevel;

	setLevel(job, newLevel);
//	}
    }

    /**
     * Demotes player in job
     * @param job - the job being deomoted
     * @param levels - number of levels to demote
     */
    public void demoteJob(Job job, int levels) {
//	synchronized (saveLock) {
	JobProgression prog = getJobProgression(job);
	if (prog == null)
	    return;
	if (levels <= 0)
	    return;
	int newLevel = prog.getLevel() - levels;
	if (newLevel < 1)
	    newLevel = 1;

	setLevel(job, newLevel);
//	}
    }

    /**
     * Sets player to a specific level
     * @param job - the job
     * @param level - the level
     */
    private void setLevel(Job job, int level) {
//	synchronized (saveLock) {
	JobProgression prog = getJobProgression(job);
	if (prog == null)
	    return;

	if (level != prog.getLevel()) {
	    prog.setLevel(level);
	    reloadHonorific();
	    Jobs.getPermissionHandler().recalculatePermissions(this);
	}
//	}
    }

    /**
     * Player leaves a job
     * @param oldjob - the old job
     * @param newjob - the new job
     */
    public boolean transferJob(Job oldjob, Job newjob) {
//	synchronized (saveLock) {
	if (!isInJob(newjob)) {
	    for (JobProgression prog : progression) {
		if (!prog.getJob().isSame(oldjob))
		    continue;

		prog.setJob(newjob);

		int maxLevel = getMaxJobLevelAllowed(newjob);

		if (newjob.getMaxLevel() > 0 && prog.getLevel() > maxLevel)
		    prog.setLevel(maxLevel);

		reloadMaxExperience();
		reloadLimits();
		reloadHonorific();
		Jobs.getPermissionHandler().recalculatePermissions(this);

		return true;
	    }
	}
	return false;
//	}
    }

    public int getMaxJobLevelAllowed(Job job) {
	int maxLevel = 0;
	if (havePermission("jobs." + job.getName() + ".vipmaxlevel"))
	    maxLevel = job.getVipMaxLevel() > job.getMaxLevel() ? job.getVipMaxLevel() : job.getMaxLevel();
	else
	    maxLevel = job.getMaxLevel();
	int tMax = Jobs.getPermissionManager().getMaxPermission(this, "jobs." + job.getName() + ".vipmaxlevel").intValue();
	if (tMax > maxLevel)
	    maxLevel = tMax;
	return maxLevel;
    }

    /**
     * Checks if the player is in this job.
     * @param job - the job
     * @return true - they are in the job
     * @return false - they are not in the job
     */
    public boolean isInJob(Job job) {
	if (job == null)
	    return false;
	for (JobProgression prog : progression) {
	    if (prog.getJob().isSame(job))
		return true;
	}
	return false;
    }

    /**
     * Function that reloads your honorific
     */
    public void reloadHonorific() {
	StringBuilder builder = new StringBuilder();
	int numJobs = progression.size();
	boolean gotTitle = false;

	if (numJobs > 0) {
	    for (JobProgression prog : progression) {
		DisplayMethod method = prog.getJob().getDisplayMethod();
		if (method.equals(DisplayMethod.NONE))
		    continue;
		if (gotTitle) {
		    builder.append(Jobs.getGCManager().modifyChatSeparator);
		    gotTitle = false;
		}
		Title title = Jobs.gettitleManager().getTitle(prog.getLevel(), prog.getJob().getName());

		if (numJobs == 1) {
		    if (method.equals(DisplayMethod.FULL) || method.equals(DisplayMethod.TITLE)) {
			if (title != null) {
			    String honorificpart = title.getChatColor() + title.getName() + ChatColor.WHITE;
			    if (honorificpart.contains("{level}"))
				honorificpart = honorificpart.replace("{level}", String.valueOf(prog.getLevel()));
			    builder.append(honorificpart);
			    gotTitle = true;
			}
		    }
		    if (method.equals(DisplayMethod.FULL) || method.equals(DisplayMethod.JOB)) {
			if (gotTitle) {
			    builder.append(" ");
			}
			String honorificpart = prog.getJob().getChatColor() + prog.getJob().getName() + ChatColor.WHITE;
			if (honorificpart.contains("{level}"))
			    honorificpart = honorificpart.replace("{level}", String.valueOf(prog.getLevel()));
			builder.append(honorificpart);
			gotTitle = true;
		    }
		}

		if (numJobs > 1 && (method.equals(DisplayMethod.FULL) || method.equals(DisplayMethod.TITLE)) || method.equals(DisplayMethod.SHORT_FULL) || method.equals(DisplayMethod.SHORT_TITLE)) {
		    // add title to honorific
		    if (title != null) {
			String honorificpart = title.getChatColor() + title.getShortName() + ChatColor.WHITE;
			if (honorificpart.contains("{level}"))
			    honorificpart = honorificpart.replace("{level}", String.valueOf(prog.getLevel()));
			builder.append(honorificpart);
			gotTitle = true;
		    }
		}

		if (numJobs > 1 && (method.equals(DisplayMethod.FULL) || method.equals(DisplayMethod.JOB)) || method.equals(DisplayMethod.SHORT_FULL) || method.equals(
		    DisplayMethod.SHORT_JOB)) {
		    String honorificpart = prog.getJob().getChatColor() + prog.getJob().getShortName() + ChatColor.WHITE;
		    if (honorificpart.contains("{level}"))
			honorificpart = honorificpart.replace("{level}", String.valueOf(prog.getLevel()));
		    builder.append(honorificpart);
		    gotTitle = true;
		}
	    }
	} else {
	    Job nonejob = Jobs.getNoneJob();
	    if (nonejob != null) {
		DisplayMethod metod = nonejob.getDisplayMethod();
		if (metod.equals(DisplayMethod.FULL) || metod.equals(DisplayMethod.TITLE)) {
		    String honorificpart = Jobs.getNoneJob().getChatColor() + Jobs.getNoneJob().getName() + ChatColor.WHITE;
		    if (honorificpart.contains("{level}"))
			honorificpart = honorificpart.replace("{level}", "");
		    builder.append(honorificpart);
		}

		if (metod.equals(DisplayMethod.SHORT_FULL) || metod.equals(DisplayMethod.SHORT_TITLE) || metod.equals(DisplayMethod.SHORT_JOB)) {
		    String honorificpart = Jobs.getNoneJob().getChatColor() + Jobs.getNoneJob().getShortName() + ChatColor.WHITE;
		    if (honorificpart.contains("{level}"))
			honorificpart = honorificpart.replace("{level}", "");
		    builder.append(honorificpart);
		}
	    }
	}

	honorific = builder.toString().trim();
	if (honorific.length() > 0)
	    honorific = org.bukkit.ChatColor.translateAlternateColorCodes('&',
		Jobs.getGCManager().modifyChatPrefix + honorific + Jobs.getGCManager().modifyChatSuffix);

    }

    /**
     * Performs player save
     */
    public void save() {
//	synchronized (saveLock) {
	if (!isSaved()) {
	    JobsDAO dao = Jobs.getJobsDAO();
	    dao.save(this);
	    dao.saveLog(this);
	    dao.savePoints(this);
	    dao.recordPlayersLimits(this);
	    dao.updateSeen(this);
	    setSaved(true);

	    if (getPlayer() == null || !getPlayer().isOnline()) {
		Jobs.getPlayerManager().addPlayerToCache(this);
		Jobs.getPlayerManager().removePlayer(getPlayer());
	    }
	}
//	}
    }

    /**
     * Perform connect
     */
    public void onConnect() {
	isOnline = true;
    }

    /**
     * Perform disconnect
     * 
     */
    public void onDisconnect() {
//	Jobs.getJobsDAO().savePoints(this);
	clearBossMaps();
	isOnline = false;
	Jobs.getPlayerManager().addPlayerToCache(this);
    }

    public void clearBossMaps() {
	for (BossBarInfo one : barMap) {
	    one.cancel();
	}
	barMap.clear();
    }

    /**
     * Whether or not player is online
     * @return true if online, otherwise false
     */
    public boolean isOnline() {
	if (getPlayer() != null)
	    return getPlayer().isOnline();
	return isOnline;
    }

    public boolean isSaved() {
	return isSaved;
    }

    public void setSaved(boolean isSaved) {
	this.isSaved = isSaved;
    }

    public Long getSeen() {
	return seen;
    }

    public void setSeen(Long seen) {
	this.seen = seen;
    }

    public HashMap<String, Boolean> getPermissionsCache() {
	return permissionsCache;
    }

    public void setPermissionsCache(HashMap<String, Boolean> permissionsCache) {
	this.permissionsCache = permissionsCache;
    }

    public void setPermissionsCache(String permission, Boolean state) {
	this.permissionsCache.put(permission, state);
    }

    public Long getLastPermissionUpdate() {
	return lastPermissionUpdate;
    }

    public void setLastPermissionUpdate(Long lastPermissionUpdate) {
	this.lastPermissionUpdate = lastPermissionUpdate;
    }

    public boolean canGetPaid(ActionInfo info) {

	List<JobProgression> progression = getJobProgression();
	int numjobs = progression.size();

	if (numjobs == 0) {
	    if (Jobs.getNoneJob() == null)
		return false;
	    JobInfo jobinfo = Jobs.getNoneJob().getJobInfo(info, 1);
	    if (jobinfo == null)
		return false;
	    Double income = jobinfo.getIncome(1, numjobs);
	    Double points = jobinfo.getPoints(1, numjobs);
	    if (income == 0D && points == 0D)
		return false;
	}

	for (JobProgression prog : progression) {
	    int level = prog.getLevel();
	    JobInfo jobinfo = prog.getJob().getJobInfo(info, level);
	    if (jobinfo == null)
		continue;
	    Double income = jobinfo.getIncome(level, numjobs);
	    Double pointAmount = jobinfo.getPoints(level, numjobs);
	    Double expAmount = jobinfo.getExperience(level, numjobs);
	    if (income != 0D || pointAmount != 0D || expAmount != 0D)
		return true;
	}

	return false;
    }

    public boolean inDailyQuest(Job job, String questName) {

	HashMap<String, QuestProgression> qpl = qProgression.get(job.getName());
	if (qpl == null)
	    return false;

	for (Entry<String, QuestProgression> one : qpl.entrySet()) {
	    if (one.getValue().getQuest().getConfigName().equalsIgnoreCase(questName))
		return true;
	}

	return false;
    }

    private List<String> getQuestNameList(Job job, ActionType type) {
	List<String> ls = new ArrayList<>();
	if (!isInJob(job))
	    return ls;

	HashMap<String, QuestProgression> qpl = qProgression.get(job.getName());

	if (qpl == null)
	    return ls;

	for (Entry<String, QuestProgression> one : qpl.entrySet()) {
	    QuestProgression prog = one.getValue();
	    if (prog.isEnded())
		continue;

	    for (Entry<String, QuestObjective> oneObjective : prog.getQuest().getObjectives().entrySet()) {
		if (type == null || type.name().equals(oneObjective.getValue().getAction().name())) {
		    ls.add(prog.getQuest().getConfigName().toLowerCase());
		    break;
		}
	    }
	}

	return ls;
    }

    public void resetQuests(Job job) {
	for (QuestProgression oneQ : getQuestProgressions(job)) {
	    oneQ.setValidUntil(0l);
	    for (java.util.Map.Entry<String, QuestObjective> obj : oneQ.getQuest().getObjectives().entrySet()) {
		oneQ.setAmountDone(obj.getValue(), 0);
	    }
	    setDoneQuests(0);
	    getQuestProgressions(job).clear();
	}
    }

    public void resetQuests() {
	for (JobProgression one : getJobProgression()) {
	    for (QuestProgression oneQ : getQuestProgressions(one.getJob())) {
		for (java.util.Map.Entry<String, QuestObjective> obj : oneQ.getQuest().getObjectives().entrySet()) {
		    oneQ.setAmountDone(obj.getValue(), 0);
		}
		oneQ.setValidUntil(0L);
	    }

	    setDoneQuests(0);
	    getQuestProgressions(one.getJob()).clear();
	}
    }

    public List<QuestProgression> getQuestProgressions() {
	List<QuestProgression> g = new ArrayList<>();
	for (JobProgression one : getJobProgression()) {
	    g.addAll(getQuestProgressions(one.getJob()));
	}
	return g;
    }

    public List<QuestProgression> getQuestProgressions(Job job) {
	return getQuestProgressions(job, null);
    }

    public List<QuestProgression> getQuestProgressions(Job job, ActionType type) {
	if (!isInJob(job))
	    return new ArrayList<QuestProgression>();
	HashMap<String, QuestProgression> g = new HashMap<>();

	if (qProgression.get(job.getName()) != null)
	    g = new HashMap<String, QuestProgression>(qProgression.get(job.getName()));

	HashMap<String, QuestProgression> tmp = new HashMap<>();
	for (Entry<String, QuestProgression> one : (new HashMap<String, QuestProgression>(g)).entrySet()) {
	    QuestProgression qp = one.getValue();

	    if (qp == null || !qp.isValid()) {
		Quest q = job.getNextQuest(getQuestNameList(job, type), getJobProgression(job).getLevel());

		if (q == null)
		    continue;

		qp = new QuestProgression(q);

		if (g.size() >= job.getMaxDailyQuests())
		    continue;

		g.put(qp.getQuest().getConfigName(), qp);
	    }

	    if (qp.getQuest() == null) {
		g.remove(one.getKey());
		continue;
	    }
	}

	if (g.size() < job.getMaxDailyQuests()) {
	    int i = 0;
	    while (i <= job.getQuests().size()) {
		++i;
		Quest q = job.getNextQuest(getQuestNameList(job, type), getJobProgression(job).getLevel());
		if (q == null)
		    continue;
		QuestProgression qp = new QuestProgression(q);
		g.put(qp.getQuest().getConfigName(), qp);
		if (g.size() >= job.getMaxDailyQuests())
		    break;
	    }
	}

	qProgression.put(job.getName(), g);

	for (Entry<String, QuestProgression> oneJ : g.entrySet()) {
	    if (type == null) {
		tmp.put(oneJ.getValue().getQuest().getConfigName(), oneJ.getValue());
	    } else
		for (Entry<String, QuestObjective> one : oneJ.getValue().getQuest().getObjectives().entrySet()) {
		    if (type.name().equals(one.getValue().getAction().name())) {
			tmp.put(oneJ.getValue().getQuest().getConfigName(), oneJ.getValue());
			break;
		    }
		}
	}

	List<QuestProgression> pr = new ArrayList<>();
	for (Entry<String, QuestProgression> one : tmp.entrySet()) {
	    pr.add(one.getValue());
	}

	return pr;
    }

    public String getQuestProgressionString() {
	String prog = "";

	for (QuestProgression one : this.getQuestProgressions()) {
	    if (one.getQuest().getObjectives().isEmpty())
		continue;
	    if (!prog.isEmpty())
		prog += ";:;";
	    prog += one.getQuest().getJob().getName() + ":" + one.getQuest().getConfigName() + ":" + one.getValidUntil() + ":";
	    for (Entry<String, QuestObjective> oneO : one.getQuest().getObjectives().entrySet()) {
		prog += oneO.getValue().getAction().toString() + ";" + oneO.getKey() + ";" + one.getAmountDone(oneO.getValue()) + ":;:";
	    }
	    prog = prog.endsWith(":;:") ? prog.substring(0, prog.length() - 3) : prog;
	}

	return prog.isEmpty() ? null : prog.endsWith(";:") ? prog.substring(0, prog.length() - 2) : prog;
    }

    public void setQuestProgressionFromString(String qprog) {
	if (qprog == null || qprog.isEmpty())
	    return;
	String[] byJob = qprog.split(";:;");

	for (String one : byJob) {

	    try {
		String jname = one.split(":")[0];
		Job job = Jobs.getJob(jname);

		if (job == null)
		    continue;

		one = one.substring(jname.length() + 1);
		String qname = one.split(":")[0];
		Quest quest = job.getQuest(qname);

		if (quest == null)
		    continue;

		one = one.substring(qname.length() + 1);
		String longS = one.split(":")[0];
		Long validUntil = Long.parseLong(longS);
		one = one.substring(longS.length() + 1);

		HashMap<String, QuestProgression> currentProgression = qProgression.get(job.getName());

		if (currentProgression == null) {
		    currentProgression = new HashMap<String, QuestProgression>();
		    qProgression.put(job.getName(), currentProgression);
		}

		QuestProgression qp = currentProgression.get(qname.toLowerCase());
		if (qp == null) {
		    qp = new QuestProgression(quest);
		    qp.setValidUntil(validUntil);
		    currentProgression.put(qname.toLowerCase(), qp);
		}

		for (String oneA : one.split(":;:")) {

		    String prog = oneA.split(";")[0];
		    ActionType action = ActionType.getByName(prog);
		    if (action == null)
			continue;
		    if (oneA.length() < prog.length() + 1)
			continue;
		    oneA = oneA.substring(prog.length() + 1);

		    String target = oneA.split(";")[0];
		    QuestObjective obj = quest.getObjectives().get(target);

		    if (obj == null)
			continue;

		    oneA = oneA.substring(target.length() + 1);
		    String doneS = oneA.split(";")[0];
		    int done = Integer.parseInt(doneS);
		    qp.setAmountDone(obj, done);
		}

	    } catch (Exception | Error e) {
		e.printStackTrace();
	    }
	}
    }

    public int getDoneQuests() {
	return doneQuests;
    }

    public void setDoneQuests(int doneQuests) {
	this.doneQuests = doneQuests;
    }

    public void addDoneQuest() {
	this.doneQuests++;
	this.setSaved(false);

	if (SignUtil.questSignUpdateShed == null) {
	    SignUtil.questSignUpdateShed = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Jobs.getInstance(), new Runnable() {
		@Override
		public void run() {
		    Jobs.getSignUtil().SignUpdate(SignTopType.questtoplist);
		    SignUtil.questSignUpdateShed = null;
		}
	    }, Jobs.getGCManager().getSavePeriod() * 60 * 20L);
	}
    }

    public int getFurnaceCount() {
	return FurnaceBrewingHandling.getTotalFurnaces(getPlayerUUID());
    }

    public int getBrewingStandCount() {
	return FurnaceBrewingHandling.getTotalBrewingStands(getPlayerUUID());
    }

    public int getMaxBrewingStandsAllowed() {
	Double maxV = Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxbrewingstands");

	if (maxV == null || maxV == 0)
	    maxV = (double) Jobs.getGCManager().getBrewingStandsMaxDefault();

	int max = maxV.intValue();
	return max;
    }

    public int getMaxFurnacesAllowed() {
	Double maxV = Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxfurnaces");

	if (maxV == null || maxV == 0)
	    maxV = (double) Jobs.getGCManager().getFurnacesMaxDefault();

	int max = maxV.intValue();

	return max;
    }
}
