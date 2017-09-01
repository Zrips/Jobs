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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Perm;
import com.gamingmesh.jobs.stuff.TimeManage;

public class JobsPlayer {
    // the player the object belongs to
    private String userName;
    // progression of the player in each job
    public UUID playerUUID;
    public ArrayList<JobProgression> progression = new ArrayList<JobProgression>();
    private ArchivedJobs archivedJobs = new ArchivedJobs();

    private PaymentData paymentLimits = null;

    private HashMap<String, ArrayList<BoostCounter>> boostCounter = new HashMap<String, ArrayList<BoostCounter>>();

    // display honorific
    private String honorific;
    // player save status
    private volatile boolean isSaved = true;
    // player online status
    private volatile boolean isOnline = false;

    private OfflinePlayer OffPlayer = null;
    private Player player = null;

    private HashMap<CurrencyType, Integer> limits = new HashMap<CurrencyType, Integer>();

    private int userid = -1;

    List<BossBarInfo> barMap = new ArrayList<BossBarInfo>();
    List<String> updateBossBarFor = new ArrayList<String>();
    // save lock
//    public final Object saveLock = new Object();

    // log
    private HashMap<String, Log> logList = new HashMap<String, Log>();

    private Long seen = System.currentTimeMillis();

    private HashMap<String, Boolean> permissionsCache = null;
    private Long lastPermissionUpdate = -1L;

    public JobsPlayer(String userName, OfflinePlayer player) {
	this.userName = userName;
	this.OffPlayer = player;
	this.player = Bukkit.getPlayer(userName);
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

    public PaymentData getPaymentLimit() {
	if (paymentLimits == null) {
	    paymentLimits = Jobs.getJobsDAO().getPlayersLimits(this);
	}
	if (paymentLimits == null)
	    paymentLimits = new PaymentData();
	return paymentLimits;
    }

    public boolean isUnderLimit(CurrencyType type, Double amount) {
	Player player = this.getPlayer();
	if (player == null)
	    return true;
	if (amount == 0)
	    return true;
	CurrencyLimit limit = Jobs.getGCManager().getLimit(type);
	if (!limit.isEnabled())
	    return true;
	PaymentData data = getPaymentLimit();
	Integer value = this.limits.get(type);
	if (data.IsReachedLimit(type, value == null ? 0 : value)) {
	    if (player.isOnline() && !data.isInformed() && !data.isReseted()) {
		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + type.getName().toLowerCase() + "limit"));
		player.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.reached" + type.getName().toLowerCase() + "limit2"));
		data.setInformed();
	    }
	    if (data.IsAnnounceTime(limit.getAnnouncmentDelay()) && player.isOnline()) {
		Jobs.getActionBar().send(player, Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "time", "%time%", TimeManage.to24hourShort(data.GetLeftTime(type))));
	    }
	    if (data.isReseted())
		data.setReseted(false);
	    return false;
	}
	data.AddAmount(type, amount);
	return true;
    }

    public void setPlayer(Player p) {
	this.player = p;
    }

    public void loadLogFromDao() {
	Jobs.getJobsDAO().loadLog(this);
    }

    public synchronized List<String> getUpdateBossBarFor() {
	return this.updateBossBarFor;
    }

    public synchronized void clearUpdateBossBarFor() {
	this.updateBossBarFor.clear();
    }

    public synchronized List<BossBarInfo> getBossBarInfo() {
	return this.barMap;
    }

    public synchronized void hideBossBars() {
	for (BossBarInfo one : this.barMap) {
	    one.getBar().setVisible(false);
	}
    }

    public HashMap<String, Log> getLog() {
	return this.logList;
    }

    public void setLog(HashMap<String, Log> l) {
	this.logList = l;
    }

    public void setUserId(int id) {
	this.userid = id;
    }

    public int getUserId() {
	return this.userid;
    }

    /**
     * Get the player
     * @return the player
     */
    public Player getPlayer() {
	if (this.playerUUID != null) {
	    Player p = Bukkit.getPlayer(this.playerUUID);
	    if (p != null) {
		this.player = p;
		this.OffPlayer = p;
		this.userName = player.getName();
	    }
	}
	return this.player;
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
	if (!this.isOnline())
	    return Boost;

	long time = System.currentTimeMillis();

	if (this.boostCounter.containsKey(JobName)) {
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

	ArrayList<BoostCounter> counterList = new ArrayList<BoostCounter>();
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
	Parser eq = Jobs.getGCManager().currencyLimitUse.get(type).getMaxEquation();
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
	Integer value = this.limits.get(type);
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
	if (this.player == null)
	    this.player = Bukkit.getPlayer(this.getPlayerUUID());
	if (this.player != null)
	    return Perm.hasPermission(player, perm);
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
	if (player != null)
	    userName = player.getName();
	if (userName == null && OffPlayer != null)
	    userName = OffPlayer.getName();
	return userName;
    }

    /**
     * get the playerUUID
     * @return the playerUUID
     */
    public UUID getPlayerUUID() {
	if (this.playerUUID == null && player != null)
	    this.playerUUID = player.getUniqueId();
	if (this.playerUUID == null && this.OffPlayer != null)
	    this.playerUUID = OffPlayer.getUniqueId();
	return this.playerUUID;
    }

    public void setPlayerUUID(UUID uuid) {
	playerUUID = uuid;
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

	    JobProgression archived = this.getArchivedJobProgression(job);
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
	int maxLevel = this.getMaxJobLevelAllowed(job);
	if (Jobs.getGCManager().fixAtMaxLevel && jp.getLevel() == maxLevel)
	    level = jp.getLevel();

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
	int newLevel = prog.getLevel() + levels;

	int maxLevel = job.getMaxLevel(this);

	if (maxLevel > 0 && newLevel > maxLevel) {
	    newLevel = maxLevel;
	}
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
	if (newLevel < 1) {
	    newLevel = 1;
	}
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

		if (newjob.getMaxLevel() > 0 && prog.getLevel() > maxLevel) {
		    prog.setLevel(maxLevel);
		}
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
	if (this.havePermission("jobs." + job.getName() + ".vipmaxlevel"))
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
		    builder.append(Jobs.getGCManager().getModifyChatSeparator());
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
	    honorific = Jobs.getGCManager().getModifyChatPrefix() + honorific + Jobs.getGCManager().getModifyChatSuffix();

    }

    /**
     * Performs player save
     * @param dao
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

	    if (this.getPlayer() == null || !this.getPlayer().isOnline()) {
		Jobs.getPlayerManager().addPlayerToCache(this);
		Jobs.getPlayerManager().removePlayer(this.getPlayer());
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
	if (this.getPlayer() != null)
	    return this.getPlayer().isOnline();
	return isOnline;
    }

    public boolean isSaved() {
	return isSaved;
    }

    public void setSaved(boolean value) {
	isSaved = value;
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
}
