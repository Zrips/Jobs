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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.Signs.SignTopType;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.blockOwnerShip.BlockTypes;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.stuff.TimeManage;

import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Equations.Parser;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Logs.CMIDebug;

public class JobsPlayer {

    private final Jobs plugin = org.bukkit.plugin.java.JavaPlugin.getPlugin(Jobs.class);

    private String userName = "Unknown";

    public UUID playerUUID;

    // progression of the player in each job
    public final List<JobProgression> progression = new ArrayList<>();

    public int maxJobsEquation = 0;

    private ArchivedJobs archivedJobs = new ArchivedJobs();
    private PaymentData paymentLimits;

    private final Map<String, List<BoostCounter>> boostCounter = new HashMap<>();

    // display honorific
    private String honorific;
    // player save status
    private volatile boolean isSaved = true;
    // player online status
    private volatile boolean isOnline = false;

    private final Map<CurrencyType, Integer> limits = new HashMap<>();

    private int userid = -1;

    private final List<BossBarInfo> barMap = new ArrayList<>();
    private final List<String> updateBossBarFor = new ArrayList<>();
    // save lock
//    public final Object saveLock = new Object();

    private Map<String, Log> logList = new HashMap<>();

    private Long seen = System.currentTimeMillis();

    private Map<String, Boolean> permissionsCache;
    private Long lastPermissionUpdate = -1L;

    private final Map<String, Map<String, QuestProgression>> qProgression = new HashMap<>();
    private int doneQuests = 0;
    private int skippedQuests = 0;

    private final Map<UUID, Map<Job, Long>> leftTimes = new HashMap<>();

    private PlayerPoints pointsData = new PlayerPoints();

    private Set<String> blockOwnerShipInform = null;

    public JobsPlayer(OfflinePlayer player) {
	this.userName = player.getName() == null ? "Unknown" : player.getName();
	this.playerUUID = player.getUniqueId();
    }

    public JobsPlayer(Player player) {
	this.userName = player.getName() == null ? "Unknown" : player.getName();
	this.playerUUID = player.getUniqueId();
    }

    public JobsPlayer(String userName) {
	this.userName = userName == null ? "Unknown" : userName;
    }

    /**
     * @return the cached or new instance of {@link PlayerPoints}
     */
    public PlayerPoints getPointsData() {
	return pointsData;
    }

    /**
     * Adds points to this player.
     * 
     * @param points the amount of points
     */
    public void addPoints(double points) {
	pointsData.addPoints(points);
	this.setSaved(false);
    }

    /**
     * Takes points from this player.
     * 
     * @param points the amount of points
     */
    public void takePoints(double points) {
	pointsData.takePoints(points);
	this.setSaved(false);
    }

    /**
     * Sets points for this player.
     * 
     * @param points the amount of points
     */
    public void setPoints(double points) {
	pointsData.setPoints(points);
	this.setSaved(false);
    }

    /**
     * Sets points for this player from the given {@link PlayerPoints}
     * 
     * @param points {@link PlayerPoints}
     */
    public void setPoints(PlayerPoints points) {
	pointsData.setPoints(points.getCurrentPoints());
	pointsData.setTotalPoints(points.getTotalPoints());
	pointsData.setDbId(points.getDbId());
    }

    /**
     * Checks whenever have enough points for the given one.
     * 
     * @param points amount of points
     * @return true if yes
     */
    public boolean havePoints(double points) {
	return pointsData.getCurrentPoints() >= points;
    }

    /**
     * @return the cached instance of {@link ArchivedJobs}
     */
    public ArchivedJobs getArchivedJobs() {
	return archivedJobs;
    }

    /**
     * Returns the given archived job progression.
     * 
     * @param job {@link Job}
     * @return the given archived job progression
     */
    public JobProgression getArchivedJobProgression(Job job) {
	return archivedJobs.getArchivedJobProgression(job);
    }

    public void setArchivedJobs(ArchivedJobs archivedJob) {
	this.archivedJobs = archivedJob;
    }

    /**
     * @return the total level of all jobs for this player
     */
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

    /**
     * @return the limit of {@link PaymentData}
     */
    public PaymentData getPaymentLimit() {
	if (paymentLimits == null)
	    paymentLimits = Jobs.getJobsDAO().getPlayersLimits(this);

	if (paymentLimits == null)
	    paymentLimits = new PaymentData();
	return paymentLimits;
    }

    /**
     * Checks whenever this player is under limit for specific {@link CurrencyType}
     * 
     * @param type {@link CurrencyType}
     * @param amount amount of points
     * @return true if it is under
     */
    public boolean isUnderLimit(CurrencyType type, double amount) {
	if (amount == 0)
	    return true;

	Player player = getPlayer();
	if (player == null)
	    return true;

	CurrencyLimit limit = Jobs.getGCManager().getLimit(type);
	if (!limit.isEnabled())
	    return true;

	PaymentData data = getPaymentLimit();

	if (data.isReachedLimit(type, getLimit(type))) {
	    String name = type.getName().toLowerCase();

	    if (!data.isInformed() && player.isOnline() && !data.isReseted(type)) {
		if (Jobs.getGCManager().useMaxPaymentCurve) {
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit"));
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit2"));
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit3"));
		} else {
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit"));
		    player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + name + "limit2"));
		}

		data.setInformed(true);
	    }

	    if (data.isAnnounceTime(limit.getAnnouncementDelay()) && player.isOnline())
		CMIActionBar.send(player, Jobs.getLanguage().getMessage("command.limit.output." + name + "time", "%time%", TimeManage.to24hourShort(data.getLeftTime(type))));

	    if (data.isReseted(type))
		data.setReseted(type, false);

	    return false;
	}

	data.addAmount(type, amount);
	return true;
    }

    public double percentOverLimit(CurrencyType type) {
	return getPaymentLimit().percentOverLimit(type, getLimit(type));
    }

    /**
     * Attempt to load log for this player.
     * 
     * @deprecated use {@link JobsDAO#loadLog(JobsPlayer)} instead
     */
    @Deprecated
    public void loadLogFromDao() {
	Jobs.getJobsDAO().loadLog(this);
    }

    public List<String> getUpdateBossBarFor() {
	return updateBossBarFor;
    }

    @Deprecated
    public void clearUpdateBossBarFor() {
	updateBossBarFor.clear();
    }

    public List<BossBarInfo> getBossBarInfo() {
	return barMap;
    }

    public void hideBossBars() {
	for (BossBarInfo one : barMap) {
	    one.getBar().setVisible(false);
	}
    }

    public Map<String, Log> getLog() {
	return logList;
    }

    public void setLog(Map<String, Log> logList) {
	this.logList = logList;
    }

    public void setUserId(int userid) {
	this.userid = userid;
    }

    public int getUserId() {
	return userid;
    }

    /**
     * @return {@link Player} or null if not exist
     */
    public Player getPlayer() {
	return playerUUID != null ? plugin.getServer().getPlayer(playerUUID) : null;
    }

    /**
     * Attempts to get the boost from specific job and {@link CurrencyType}
     * 
     * @param jobName
     * @param type {@link CurrencyType}
     * @see #getBoost(String, CurrencyType, boolean)
     * @return amount of boost
     */
    public double getBoost(String jobName, CurrencyType type) {
	return getBoost(jobName, type, false);
    }

    /**
     * Attempts to get the boost from specific job and {@link CurrencyType}
     * 
     * @param jobName
     * @param type {@link CurrencyType}
     * @param force whenever to retrieve as soon as possible without time
     * @return amount of boost
     */
    public double getBoost(String jobName, CurrencyType type, boolean force) {
	double boost = 0D;

	if (type == null || !isOnline())
	    return boost;

	long time = System.currentTimeMillis();

	List<BoostCounter> counterList = boostCounter.get(jobName);
	if (counterList != null) {
	    for (BoostCounter counter : counterList) {
		if (counter.getType() != type)
		    continue;

		if (force || time - counter.getTime() > 1000 * 60) {
		    boost = getPlayerBoostNew(jobName, type);
		    counter.setBoost(boost);
		    counter.setTime(time);
		    return boost;
		}

		return counter.getBoost();
	    }

	    boost = getPlayerBoostNew(jobName, type);
	    counterList.add(new BoostCounter(type, boost, time));
	    return boost;
	}

	boost = getPlayerBoostNew(jobName, type);

	counterList = new ArrayList<>();
	counterList.add(new BoostCounter(type, boost, time));

	boostCounter.put(jobName, counterList);
	return boost;
    }

    private Double getPlayerBoostNew(String jobName, CurrencyType type) {
	Double v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost." + jobName + "." + type.getName(), true, false);
	Double boost = v1;

	v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost." + jobName + ".all", false, false);
	if (v1 != 0d && (v1 > boost || v1 < boost))
	    boost = v1;

	v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost.all.all", false, false);
	if (v1 != 0d && (v1 > boost || v1 < boost))
	    boost = v1;

	v1 = Jobs.getPermissionManager().getMaxPermission(this, "jobs.boost.all." + type.getName(), false, false);
	if (v1 != 0d && (v1 > boost || v1 < boost))
	    boost = v1;

	return boost;
    }

    public int getPlayerMaxQuest(String jobName) {
	int m1 = (int) Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxquest.all", false, true);
	if (m1 != 0)
	    return m1;
	return (int) Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxquest." + jobName, false, true);
    }

    /**
     * Reloads max experience for all jobs for this player.
     */
    public void reloadMaxExperience() {
	progression.forEach(JobProgression::reloadMaxExperience);
    }

    /**
     * Reloads limit for this player.
     */
    public void reload(CurrencyType type) {
	Parser eq = Jobs.getGCManager().getLimit(type).getMaxEquation();
	eq.setVariable("totallevel", getTotalLevels());

	maxJobsEquation = Jobs.getPlayerManager().getMaxJobs(this);
	limits.put(type, (int) eq.getValue());
	setSaved(false);
    }

    public void reloadLimits() {
	for (CurrencyType type : CurrencyType.values()) {
	    reload(type);
	}
    }

    public int getLimit(CurrencyType type) {
	return limits.getOrDefault(type, 0);
    }

    public void resetPaymentLimit() {
	if (paymentLimits == null)
	    getPaymentLimit();
	if (paymentLimits != null)
	    paymentLimits.resetLimits();
	setSaved(false);
    }

    /**
     * @return an unmodifiable list of job progressions
     */
    public List<JobProgression> getJobProgression() {
	return Collections.unmodifiableList(progression);
    }

    /**
     * Get the job progression from the certain job
     * 
     * @param job {@link Job}
     * @return the job progression or null if job not exists
     */
    public JobProgression getJobProgression(Job job) {
	if (job != null) {
	    for (JobProgression prog : progression) {
		if (prog.getJob().isSame(job))
		    return prog;
	    }
	}

	return null;
    }

    /**
     * get the userName
     * @return the userName
     */
    @Deprecated
    public String getUserName() {
	return getName();
    }

    /**
     * @return the name of this player
     */
    public String getName() {
	Player player = getPlayer();
	if (player != null)
	    userName = player.getName();
	return userName;
    }

    /**
     * get the playerUUID
     * @return the playerUUID
     */
    @Deprecated
    public UUID getPlayerUUID() {
	return getUniqueId();
    }

    /**
     * @return the {@link UUID} of this player
     */
    public UUID getUniqueId() {
	return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
	setUniqueId(playerUUID);
    }

    public void setUniqueId(UUID playerUUID) {
	this.playerUUID = playerUUID;
    }

    public String getDisplayHonorific() {
	if (honorific == null)
	    reloadHonorific();
	return honorific;
    }

    /**
     * Attempts to join this player to the given job.
     * 
     * @param job where to join
     */
    public boolean joinJob(Job job) {
//	synchronized (saveLock) {
	if (!isInJob(job)) {
	    int level = 1;
	    double exp = 0;

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

	if (jp.getLevel() == getMaxJobLevelAllowed(jp.getJob())) {
	    level = jp.getLevel();

	    if (!Jobs.getGCManager().fixAtMaxLevel) {
		level = (int) (level - (level * (Jobs.getGCManager().levelLossPercentageFromMax / 100.0)));
		if (level < 1)
		    level = 1;
	    }
	}

	return level;
    }

    public double getExpAfterRejoin(JobProgression jp, int level) {
	if (jp == null)
	    return 1;

	int max = jp.getMaxExperience(level);
	double exp = jp.getExperience();
	if (exp > max)
	    exp = max;

	if (exp > 0) {
	    if (jp.getLevel() == getMaxJobLevelAllowed(jp.getJob())) {
		if (!Jobs.getGCManager().fixAtMaxLevel)
		    exp = (exp - (exp * (Jobs.getGCManager().levelLossPercentageFromMax / 100.0)));
	    } else
		exp = (exp - (exp * (Jobs.getGCManager().levelLossPercentage / 100.0)));
	}

	return exp;
    }

    /**
     * Player leaves a job
     * @param job - the job left
     */
    public boolean leaveJob(Job job) {
//	synchronized (saveLock) {
	if (progression.remove(getJobProgression(job))) {
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
     * Attempts to leave all jobs from this player.
     * @return true if success
     */
    public boolean leaveAllJobs() {
//	synchronized (saveLock) {
	progression.clear();
	reloadHonorific();
	Jobs.getPermissionHandler().recalculatePermissions(this);
	reloadLimits();
	return true;
//	}
    }

    /**
     * Promotes player in job
     * @param job - the job being promoted
     * @param levels - number of levels to promote
     */
    public void promoteJob(Job job, int levels) {
	if (levels <= 0)
	    return;

//	synchronized (saveLock) {
	JobProgression prog = getJobProgression(job);
	if (prog == null)
	    return;

	int oldLevel = prog.getLevel(),
	    newLevel = oldLevel + levels,
	    maxLevel = job.getMaxLevel(this);

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
	if (levels <= 0)
	    return;

//	synchronized (saveLock) {
	JobProgression prog = getJobProgression(job);
	if (prog == null)
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

	int oldLevel = prog.getLevel();

	if (level != oldLevel) {
	    if (prog.setLevel(level)) {
		JobsLevelUpEvent levelUpEvent = new JobsLevelUpEvent(this, job, prog.getLevel(),
		    Jobs.getTitleManager().getTitle(oldLevel, prog.getJob().getName()),
		    Jobs.getTitleManager().getTitle(prog.getLevel(), prog.getJob().getName()),
		    Jobs.getGCManager().SoundLevelupSound,
		    Jobs.getGCManager().SoundLevelupVolume,
		    Jobs.getGCManager().SoundLevelupPitch,
		    Jobs.getGCManager().SoundTitleChangeSound,
		    Jobs.getGCManager().SoundTitleChangeVolume,
		    Jobs.getGCManager().SoundTitleChangePitch);

		plugin.getServer().getPluginManager().callEvent(levelUpEvent);
	    }

	    reloadHonorific();
	    reloadLimits();
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
	Player player = getPlayer();

	int maxLevel = 0;
	if (player != null && (player.hasPermission("jobs." + job.getName() + ".vipmaxlevel") || player.hasPermission("jobs.all.vipmaxlevel")))
	    maxLevel = job.getVipMaxLevel() > job.getMaxLevel() ? job.getVipMaxLevel() : job.getMaxLevel();
	else
	    maxLevel = job.getMaxLevel();

	int tMax = (int) Jobs.getPermissionManager().getMaxPermission(this, "jobs." + job.getName() + ".vipmaxlevel");
	if (tMax > maxLevel)
	    maxLevel = tMax;

	tMax = (int) Jobs.getPermissionManager().getMaxPermission(this, "jobs.all.vipmaxlevel");
	if (tMax > maxLevel)
	    maxLevel = tMax;

	return maxLevel;
    }

    /**
     * Checks if the player is in the given job.
     * 
     * @param job {@link Job}
     * @return true if this player is in the given job, otherwise false
     */
    public boolean isInJob(Job job) {
	return getJobProgression(job) != null;
    }

    /**
     * Function that reloads this player honorific
     */
    public void reloadHonorific() {
	StringBuilder builder = new StringBuilder();

	if (progression.size() > 0) {
	    for (JobProgression prog : progression) {
		DisplayMethod method = prog.getJob().getDisplayMethod();
		if (method == DisplayMethod.NONE)
		    continue;
		if (builder.length() > 0) {
		    builder.append(Jobs.getGCManager().modifyChatSeparator);
		}
		processesChat(method, builder, prog.getLevel(), Jobs.getTitleManager().getTitle(prog.getLevel(),
		    prog.getJob().getName()), prog.getJob());
	    }
	} else {
	    Job nonejob = Jobs.getNoneJob();
	    if (nonejob != null) {
		processesChat(nonejob.getDisplayMethod(), builder, -1, null, nonejob);
	    }
	}

	honorific = builder.toString().trim();
	if (!honorific.isEmpty())
	    honorific = CMIChatColor.translate(Jobs.getGCManager().modifyChatPrefix + honorific + Jobs.getGCManager().modifyChatSuffix);
    }

    private static void processesChat(DisplayMethod method, StringBuilder builder, int level, Title title, Job job) {
	String levelS = level < 0 ? "" : Integer.toString(level);
	switch (method) {
	case FULL:
	    processesTitle(builder, title, levelS);
	    processesJob(builder, job, levelS);
	    break;
	case TITLE:
	    processesTitle(builder, title, levelS);
	    break;
	case JOB:
	    processesJob(builder, job, levelS);
	    break;
	case SHORT_FULL:
	    processesShortTitle(builder, title, levelS);
	    processesShortJob(builder, job, levelS);
	    break;
	case SHORT_TITLE:
	    processesShortTitle(builder, title, levelS);
	    break;
	case SHORT_JOB:
	    processesShortJob(builder, job, levelS);
	    break;
	case SHORT_TITLE_JOB:
	    processesShortTitle(builder, title, levelS);
	    processesJob(builder, job, levelS);
	    break;
	case TITLE_SHORT_JOB:
	    processesTitle(builder, title, levelS);
	    processesShortJob(builder, job, levelS);
	    break;
	default:
	    break;
	}
    }

    private static void processesShortTitle(StringBuilder builder, Title title, String levelS) {
	if (title == null)
	    return;
	builder.append(title.getChatColor());
	builder.append(title.getShortName().replace("{level}", levelS));
	builder.append(CMIChatColor.WHITE);
    }

    private static void processesTitle(StringBuilder builder, Title title, String levelS) {
	if (title == null)
	    return;
	builder.append(title.getChatColor());
	builder.append(title.getName().replace("{level}", levelS));
	builder.append(CMIChatColor.WHITE);
    }

    private static void processesShortJob(StringBuilder builder, Job job, String levelS) {
	if (job == null)
	    return;
	builder.append(job.getChatColor());
	builder.append(job.getShortName().replace("{level}", levelS));
	builder.append(CMIChatColor.WHITE);
    }

    private static void processesJob(StringBuilder builder, Job job, String levelS) {
	if (job == null)
	    return;
	builder.append(job.getChatColor());
	builder.append(job.getName().replace("{level}", levelS));
	builder.append(CMIChatColor.WHITE);
    }

    /**
     * Performs player save into database
     */
    public void save() {
//	synchronized (saveLock) {
	if (!isSaved) {
	    JobsDAO dao = Jobs.getJobsDAO();
	    dao.save(this);
	    dao.saveLog(this);
	    dao.savePoints(this);
	    dao.recordPlayersLimits(this);
	    dao.updateSeen(this);
	    setSaved(true);

	    Player player = getPlayer();
	    if (player == null || !player.isOnline()) {
		Jobs.getPlayerManager().addPlayerToCache(this);
		Jobs.getPlayerManager().removePlayer(player);
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
     * Perform disconnect for this player
     */
    public void onDisconnect() {
	clearBossMaps();
	isOnline = false;
	blockOwnerShipInform = null;
	Jobs.getPlayerManager().addPlayerToCache(this);
    }

    public void clearBossMaps() {
	for (BossBarInfo one : barMap) {
	    one.getBar().removeAll();
	    one.cancel();
	}

	barMap.clear();
    }

    /**
     * Whether or not player is online
     * @return true if online, otherwise false
     */
    public boolean isOnline() {
	Player player = getPlayer();
	return player != null ? player.isOnline() : isOnline;
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

    public Map<String, Boolean> getPermissionsCache() {
	return permissionsCache;
    }

    public void setPermissionsCache(Map<String, Boolean> permissionsCache) {
	this.permissionsCache = permissionsCache;
    }

    public void setPermissionsCache(String permission, Boolean state) {
	permissionsCache.put(permission, state);
    }

    public Long getLastPermissionUpdate() {
	return lastPermissionUpdate;
    }

    public void setLastPermissionUpdate(Long lastPermissionUpdate) {
	this.lastPermissionUpdate = lastPermissionUpdate;
    }

    /**
     * Checks whenever this player can get paid for the given action.
     * 
     * @param info {@link ActionInfo}
     * @return true if yes
     */
    public boolean canGetPaid(ActionInfo info) {
	int numjobs = progression.size();

	if (numjobs == 0) {
	    if (Jobs.getNoneJob() == null)
		return false;
	    JobInfo jobinfo = Jobs.getNoneJob().getJobInfo(info, 1);
	    if (jobinfo == null)
		return false;
	    double income = jobinfo.getIncome(1, numjobs, maxJobsEquation);
	    double points = jobinfo.getPoints(1, numjobs, maxJobsEquation);
	    if (income == 0D && points == 0D)
		return false;
	}

	for (JobProgression prog : progression) {
	    int level = prog.getLevel();
	    JobInfo jobinfo = prog.getJob().getJobInfo(info, level);
	    if (jobinfo == null)
		continue;

	    double income = jobinfo.getIncome(level, numjobs, maxJobsEquation);
	    double pointAmount = jobinfo.getPoints(level, numjobs, maxJobsEquation);
	    double expAmount = jobinfo.getExperience(level, numjobs, maxJobsEquation);
	    if (income != 0D || pointAmount != 0D || expAmount != 0D)
		return true;
	}

	return false;
    }

    public boolean inDailyQuest(Job job, String questName) {
	Map<String, QuestProgression> qpl = qProgression.get(job.getName());
	if (qpl == null)
	    return false;

	for (QuestProgression one : qpl.values()) {
	    Quest quest = one.getQuest();

	    if (quest != null && quest.getConfigName().equalsIgnoreCase(questName))
		return true;
	}

	return false;
    }

    private List<String> getQuestNameList(Job job, ActionType type) {
	List<String> ls = new ArrayList<>();
	if (!isInJob(job))
	    return ls;

	Map<String, QuestProgression> qpl = qProgression.get(job.getName());
	if (qpl == null)
	    return ls;

	for (QuestProgression prog : qpl.values()) {
	    if (prog.isEnded())
		continue;

	    Quest quest = prog.getQuest();
	    if (quest == null)
		continue;

	    for (Map<String, QuestObjective> oneAction : quest.getObjectives().values()) {
		for (QuestObjective oneObjective : oneAction.values()) {
		    if (type == null || type.name().equals(oneObjective.getAction().name())) {
			ls.add(quest.getConfigName().toLowerCase());
			break;
		    }
		}
	    }
	}

	return ls;
    }

    public void resetQuests(Job job) {
	resetQuests(getQuestProgressions(job));
    }

    public void resetQuests(List<QuestProgression> quests) {
	for (QuestProgression oneQ : quests) {
	    oneQ.reset();
	    Quest quest = oneQ.getQuest();
	    if (quest != null) {
		Map<String, QuestProgression> map = qProgression.get(quest.getJob().getName());

		if (map != null) {
		    map.clear();
		}
	    }
	}
    }

    public void resetQuests() {
	for (JobProgression prog : progression) {
	    resetQuests(prog.getJob());
	}
    }

    public void getNewQuests() {
	qProgression.clear();
    }

    public void getNewQuests(Job job) {
	Map<String, QuestProgression> prog = qProgression.get(job.getName());
	if (prog != null) {
	    prog.clear();
	    qProgression.put(job.getName(), prog);
	}
    }

    public void replaceQuest(Quest quest) {
	Job job = quest.getJob();
	Map<String, QuestProgression> orProg = qProgression.get(job.getName());

	Quest q = job.getNextQuest(getQuestNameList(job, null), getJobProgression(job).getLevel());
	if (q == null) {
	    for (JobProgression one : progression) {
		if (one.getJob().isSame(job))
		    continue;

		q = one.getJob().getNextQuest(getQuestNameList(one.getJob(), null), getJobProgression(one.getJob()).getLevel());
		if (q != null)
		    break;
	    }
	}

	if (q == null)
	    return;

	Job qJob = q.getJob();

	Map<String, QuestProgression> prog = qProgression.get(qJob.getName());
	if (prog == null) {
	    prog = new HashMap<>();
	    qProgression.put(qJob.getName(), prog);
	}

	if (q.getConfigName().equals(quest.getConfigName()))
	    return;

	String confName = q.getConfigName().toLowerCase();

	if (prog.containsKey(confName))
	    return;

	if (!qJob.isSame(job) && prog.size() >= qJob.getMaxDailyQuests())
	    return;

	if (orProg != null) {
	    orProg.remove(quest.getConfigName().toLowerCase());
	}

	prog.put(confName, new QuestProgression(q));
	skippedQuests++;
    }

    public List<QuestProgression> getQuestProgressions() {
	List<QuestProgression> g = new ArrayList<>();
	for (JobProgression one : progression) {
	    g.addAll(getQuestProgressions(one.getJob()));
	}
	return g;
    }

    public List<QuestProgression> getQuestProgressions(Job job) {
	return getQuestProgressions(job, null);
    }

    public List<QuestProgression> getQuestProgressions(Job job, ActionType type) {
	if (!isInJob(job))
	    return new ArrayList<>();

	Map<String, QuestProgression> g = new HashMap<>();

	Map<String, QuestProgression> qProg = qProgression.get(job.getName());
	if (qProg != null)
	    g = new HashMap<>(qProg);

	for (Entry<String, QuestProgression> one : new HashMap<>(g).entrySet()) {
	    QuestProgression qp = one.getValue();

	    if (qp.isEnded()) {
		g.remove(one.getKey().toLowerCase());
		skippedQuests = 0;
	    }
	}

	if (g.size() < job.getMaxDailyQuests()) {
	    int i = 0;
	    while (i <= job.getQuests().size()) {
		++i;

		Quest q = job.getNextQuest(new ArrayList<>(g.keySet()), getJobProgression(job).getLevel());
		if (q == null)
		    continue;

		QuestProgression qp = new QuestProgression(q);
		Quest quest = qp.getQuest();
		if (quest != null)
		    g.put(quest.getConfigName().toLowerCase(), qp);

		if (g.size() >= job.getMaxDailyQuests())
		    break;
	    }
	}

	if (g.size() > job.getMaxDailyQuests()) {
	    int i = g.size();
	    while (i > 0) {
		--i;

		g.remove(g.keySet().iterator().next());

		if (g.size() <= job.getMaxDailyQuests())
		    break;
	    }
	}

	qProgression.put(job.getName(), g);

	Map<String, QuestProgression> tmp = new HashMap<>();
	for (QuestProgression oneJ : g.values()) {
	    Quest q = oneJ.getQuest();
	    if (q == null) {
		continue;
	    }

	    if (type == null) {
		tmp.put(q.getConfigName().toLowerCase(), oneJ);
		continue;
	    }

	    Map<String, QuestObjective> old = q.getObjectives().get(type);
	    if (old != null)
		for (QuestObjective one : old.values()) {
		    if (type.name().equals(one.getAction().name())) {
			tmp.put(q.getConfigName().toLowerCase(), oneJ);
			break;
		    }
		}
	}

	return new ArrayList<>(tmp.values());
    }

    public String getQuestProgressionString() {
	String prog = "";

	for (QuestProgression one : getQuestProgressions()) {
	    Quest q = one.getQuest();
	    if (q == null || q.getObjectives().isEmpty())
		continue;

	    if (!prog.isEmpty())
		prog += ";:;";

	    prog += q.getJob().getName() + ":" + q.getConfigName() + ":" + one.getValidUntil() + ":";

	    for (Map<String, QuestObjective> oneA : q.getObjectives().values()) {
		for (Entry<String, QuestObjective> oneO : oneA.entrySet()) {
		    prog += oneO.getValue().getAction().toString() + ";" + oneO.getKey() + ";" + one.getAmountDone(oneO.getValue()) + ":;:";
		}
	    }

	    if (prog.endsWith(":;:"))
		prog = prog.substring(0, prog.length() - 3);
	}

	return prog.isEmpty() ? null : prog.endsWith(";:") ? prog.substring(0, prog.length() - 2) : prog;
    }

    public void setQuestProgressionFromString(String qprog) {
	if (qprog == null || qprog.isEmpty())
	    return;

	for (String one : qprog.split(";:;")) {
	    String jname = one.split(":", 2)[0];
	    Job job = Jobs.getJob(jname);
	    if (job == null)
		continue;

	    one = one.substring(jname.length() + 1);

	    String qname = one.split(":", 2)[0];
	    Quest quest = job.getQuest(qname);
	    if (quest == null)
		continue;

	    one = one.substring(qname.length() + 1);

	    String longS = one.split(":", 2)[0];
	    long validUntil = Long.parseLong(longS);
	    one = one.substring(longS.length() + 1);

	    Map<String, QuestProgression> currentProgression = qProgression.get(job.getName());

	    if (currentProgression == null) {
		currentProgression = new HashMap<>();
		qProgression.put(job.getName(), currentProgression);
	    }

	    String questName = qname.toLowerCase();
	    QuestProgression qp = currentProgression.get(questName);
	    if (qp == null) {
		qp = new QuestProgression(quest);
		qp.setValidUntil(validUntil);
		currentProgression.put(questName, qp);
	    }

	    for (String oneA : one.split(":;:")) {
		String prog = oneA.split(";", 2)[0];
		ActionType action = ActionType.getByName(prog);
		if (action == null || oneA.length() < prog.length() + 1)
		    continue;

		Map<String, QuestObjective> old = quest.getObjectives().get(action);
		if (old == null)
		    continue;

		oneA = oneA.substring(prog.length() + 1);

		String target = oneA.split(";", 2)[0];
		QuestObjective obj = old.get(target);
		if (obj == null)
		    continue;

		oneA = oneA.substring(target.length() + 1);

		qp.setAmountDone(obj, Integer.parseInt(oneA.split(";", 2)[0]));
	    }

	    if (qp.isCompleted())
		qp.setGivenReward(true);
	}
    }

    public int getDoneQuests() {
	return doneQuests;
    }

    public void setDoneQuests(int doneQuests) {
	this.doneQuests = doneQuests;
    }

    private Integer questSignUpdateShed;

    public void addDoneQuest(final Job job) {
	doneQuests++;
	setSaved(false);

	if (questSignUpdateShed == null) {
	    questSignUpdateShed = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
		Jobs.getSignUtil().signUpdate(job, SignTopType.questtoplist);
		questSignUpdateShed = null;
	    }, Jobs.getGCManager().getSavePeriod() * 60 * 20L);
	}
    }

    /**
     * @deprecated {@link Jobs#getBlockOwnerShip(BlockTypes)}
     * @return the furnace count
     */
    @Deprecated
    public int getFurnaceCount() {
	return !plugin.getBlockOwnerShip(BlockTypes.FURNACE).isPresent() ? 0 : plugin.getBlockOwnerShip(BlockTypes.FURNACE).get().getTotal(getUniqueId());
    }

    /**
     * @deprecated {@link Jobs#getBlockOwnerShip(BlockTypes)}
     * @return the brewing stand count
     */
    @Deprecated
    public int getBrewingStandCount() {
	return !plugin.getBlockOwnerShip(BlockTypes.BREWING_STAND).isPresent() ? 0 : plugin.getBlockOwnerShip(BlockTypes.BREWING_STAND).get().getTotal(getUniqueId());
    }

    /**
     * @deprecated use {@link #getMaxOwnerShipAllowed(CMIMaterial)}
     * @return max allowed brewing stands
     */
    @Deprecated
    public int getMaxBrewingStandsAllowed() {
	Double maxV = Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxbrewingstands");

	if (maxV == 0)
	    maxV = (double) Jobs.getGCManager().getBrewingStandsMaxDefault();

	return maxV.intValue();
    }

    /**
     * @deprecated use {@link #getMaxOwnerShipAllowed(CMIMaterial)}
     * @return the max allowed furnaces
     */
    @Deprecated
    public int getMaxFurnacesAllowed() {
	return getMaxOwnerShipAllowed(BlockTypes.FURNACE);
    }

    /**
     * Returns the max allowed owner ship for the given block type.
     * @param type {@link BlockTypes}
     * @return max allowed owner ship
     */
    public int getMaxOwnerShipAllowed(BlockTypes type) {
	double maxV = Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxownership");
	if (maxV > 0D) {
	    return (int) maxV;
	}

	if (type != BlockTypes.BREWING_STAND &&
	    (maxV = Jobs.getPermissionManager().getMaxPermission(this, "jobs.maxfurnaceownership")) > 0D) {
	    return (int) maxV;
	}

	String perm = "jobs.max" + (type == BlockTypes.FURNACE
	    ? "furnaces" : type == BlockTypes.BLAST_FURNACE ? "blastfurnaces" : type == BlockTypes.SMOKER ? "smokers"
		: type == BlockTypes.BREWING_STAND ? "brewingstands" : "");

	maxV = Jobs.getPermissionManager().getMaxPermission(this, perm);

	if (maxV == 0D && type == BlockTypes.FURNACE)
	    maxV = Jobs.getGCManager().getFurnacesMaxDefault();

	if (maxV == 0D && type == BlockTypes.BLAST_FURNACE)
	    maxV = Jobs.getGCManager().BlastFurnacesMaxDefault;

	if (maxV == 0D && type == BlockTypes.SMOKER)
	    maxV = Jobs.getGCManager().SmokersMaxDefault;

	if (maxV == 0D && type == BlockTypes.BREWING_STAND)
	    maxV = Jobs.getGCManager().getBrewingStandsMaxDefault();

	return (int) maxV;
    }

    public int getSkippedQuests() {
	return skippedQuests;
    }

    public void setSkippedQuests(int skippedQuests) {
	this.skippedQuests = skippedQuests;
    }

    public Map<UUID, Map<Job, Long>> getLeftTimes() {
	return leftTimes;
    }

    public boolean isLeftTimeEnded(Job job) {
	Map<Job, Long> map = leftTimes.get(getUniqueId());
	Long time = map != null ? map.get(job) : null;
	return time != null && time.longValue() < System.currentTimeMillis();
    }

    public void setLeftTime(Job job) {
	UUID uuid = getUniqueId();
	leftTimes.remove(uuid);

	int hour = Jobs.getGCManager().jobExpiryTime;
	if (hour == 0)
	    return;

	Calendar cal = Calendar.getInstance();
	cal.setTime(new Date());

	cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hour);

	Map<Job, Long> map = new HashMap<>();
	map.put(job, cal.getTimeInMillis());
	leftTimes.put(uuid, map);
    }

    public boolean hasBlockOwnerShipInform(String location) {
	if (blockOwnerShipInform == null)
	    return false;
	return blockOwnerShipInform.contains(location);
    }

    public void addBlockOwnerShipInform(String location) {
	if (blockOwnerShipInform == null)
	    blockOwnerShipInform = new HashSet<String>();
	this.blockOwnerShipInform.add(location);
    }
}
