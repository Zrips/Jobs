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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsDAO;
import com.gamingmesh.jobs.resources.jfep.Parser;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.Perm;

public class JobsPlayer {
	// the player the object belongs to
	private String userName;
	// progression of the player in each job
	public UUID playerUUID;
	public ArrayList<JobProgression> progression = new ArrayList<JobProgression>();

	private HashMap<String, ArrayList<BoostCounter>> boostCounter = new HashMap<String, ArrayList<BoostCounter>>();

	// display honorific
	private String honorific;
	// player save status
	private volatile boolean isSaved = true;
	// player online status
	private volatile boolean isOnline = false;

	private OfflinePlayer OffPlayer = null;
	private Player player = null;

	private double VipSpawnerMultiplier = -1;

	private int MoneyLimit = 0;
	private int ExpLimit = 0;
	private int PointLimit = 0;

	private int userid = -1;

	List<BossBarInfo> barMap = new ArrayList<BossBarInfo>();
	List<String> updateBossBarFor = new ArrayList<String>();
	// save lock
	// public final Object saveLock = new Object();

	// log
	private List<Log> logList = new ArrayList<Log>();

	public JobsPlayer(String userName, OfflinePlayer player) {
		this.userName = userName;
		this.OffPlayer = player;
		this.player = Bukkit.getPlayer(userName);
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

	public List<Log> getLog() {
		return this.logList;
	}

	public void setUserId(int id) {
		this.userid = id;
	}

	public int getUserId() {
		return this.userid;
	}

	/**
	 * Get the player
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		if (this.player != null)
			return this.player;
		return Bukkit.getPlayer(this.playerUUID);
	}

	/**
	 * Get the VipSpawnerMultiplier
	 * 
	 * @return the Multiplier
	 */
	public double getVipSpawnerMultiplier() {
		if (OffPlayer == null || !this.OffPlayer.isOnline())
			return 1.0;
		if (VipSpawnerMultiplier < 0)
			updateVipSpawnerMultiplier();
		return this.VipSpawnerMultiplier;
	}

	public void updateVipSpawnerMultiplier() {
		if (Perm.hasPermission(this.player, "jobs.vipspawner"))
			this.VipSpawnerMultiplier = Jobs.getGCManager().VIPpayNearSpawnerMultiplier;
		else
			this.VipSpawnerMultiplier = Jobs.getGCManager().payNearSpawnerMultiplier;
	}

	/**
	 * Get the Boost
	 * 
	 * @return the Boost
	 */
	public double getBoost(String JobName, BoostType type) {
		return getBoost(JobName, type, false);
	}

	public double getBoost(String JobName, BoostType type, boolean force) {

		double Boost = 1.0;

		if (this.OffPlayer == null)
			return Boost;

		if (this.player == null)
			this.player = Bukkit.getPlayer(this.OffPlayer.getUniqueId());

		if (this.player == null)
			return Boost;

		long time = System.currentTimeMillis();

		if (this.boostCounter.containsKey(JobName)) {
			ArrayList<BoostCounter> counterList = boostCounter.get(JobName);
			for (BoostCounter counter : counterList) {
				if (counter.getType() != type)
					continue;
				if (force || time - counter.getTime() > 1000 * 60) {
					Boost = getPlayerBoost(JobName, type);
					counter.setBoost(Boost);
					counter.setTime(time);
					return Boost;
				}
				return counter.getBoost();
			}
			Boost = getPlayerBoost(JobName, type);
			counterList.add(new BoostCounter(type, Boost, time));
			return Boost;
		}

		Boost = getPlayerBoost(JobName, type);

		ArrayList<BoostCounter> counterList = new ArrayList<BoostCounter>();
		counterList.add(new BoostCounter(type, Boost, time));

		boostCounter.put(JobName, counterList);
		return Boost;
	}

	private Double getPlayerBoost(String JobName, BoostType type) {
		double Boost = 1.0;

		if (Perm.hasPermission(player, "jobs.boost." + JobName + "." + type.getName().toLowerCase())
				|| Perm.hasPermission(player, "jobs.boost." + JobName + ".all")
				|| Perm.hasPermission(player, "jobs.boost.all.all")
				|| Perm.hasPermission(player, "jobs.boost.all." + type.getName().toLowerCase())) {
			Boost = Jobs.getGCManager().Boost.get(type);
		}
		return Boost;
	}

	/**
	 * Reloads max experience for this job.
	 */
	public void reloadMaxExperience() {
		for (JobProgression prog : progression) {
			prog.reloadMaxExperience();
		}
	}

	/**
	 * Reloads money limit for this player.
	 */
	public void reloadMoney() {
		int TotalLevel = 0;
		for (JobProgression prog : progression) {
			TotalLevel += prog.getLevel();
		}
		Parser eq = Jobs.getGCManager().maxMoneyEquation;
		eq.setVariable("totallevel", TotalLevel);
		MoneyLimit = (int) eq.getValue();
	}

	/**
	 * Reloads exp limit for this player.
	 */
	public void reloadExp() {
		int TotalLevel = 0;
		for (JobProgression prog : progression) {
			TotalLevel += prog.getLevel();
		}
		Parser eq = Jobs.getGCManager().maxExpEquation;
		eq.setVariable("totallevel", TotalLevel);
		ExpLimit = (int) eq.getValue();
	}

	/**
	 * Reloads exp limit for this player.
	 */
	public void reloadPoint() {
		int TotalLevel = 0;
		for (JobProgression prog : progression) {
			TotalLevel += prog.getLevel();
		}
		Parser eq = Jobs.getGCManager().maxPointEquation;
		eq.setVariable("totallevel", TotalLevel);
		PointLimit = (int) eq.getValue();
	}

	public void reloadLimits() {
		reloadMoney();
		reloadExp();
		reloadPoint();
	}

	public int getMoneyLimit() {
		return this.MoneyLimit;
	}

	public int getExpLimit() {
		return this.ExpLimit;
	}

	public int getPointLimit() {
		return this.PointLimit;
	}

	/**
	 * Get the list of job progressions
	 * 
	 * @return the list of job progressions
	 */
	public List<JobProgression> getJobProgression() {
		return Collections.unmodifiableList(progression);
	}

	/**
	 * Check if have permission
	 * 
	 * @return true if have
	 */
	public boolean havePermission(String perm) {
		if (this.player == null)
			this.player = Bukkit.getPlayer(this.playerUUID);
		if (this.player != null)
			return Perm.hasPermission(player, perm);
		return false;
	}

	/**
	 * Get the job progression with the certain job
	 * 
	 * @return the job progression
	 */
	public JobProgression getJobProgression(Job job) {
		for (JobProgression prog : progression) {
			if (prog.getJob().same(job))
				return prog;
		}
		return null;
	}

	/**
	 * get the userName
	 * 
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * get the playerUUID
	 * 
	 * @return the playerUUID
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
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
	 * 
	 * @param job
	 *            - the job joined
	 */
	public boolean joinJob(Job job) {
		// synchronized (saveLock) {
		if (!isInJob(job)) {
			int level = 1;
			int exp = 0;
			if (Jobs.getJobsDAO().checkArchive(this, job).size() > 0) {
				List<Integer> info = Jobs.getJobsDAO().checkArchive(this, job);
				level = info.get(0);
				// exp = info.get(1);
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
		// }
	}

	/**
	 * Player leaves a job
	 * 
	 * @param job
	 *            - the job left
	 */
	public boolean leaveJob(Job job) {
		// synchronized (saveLock) {
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
		// }
	}

	/**
	 * Leave all jobs
	 * 
	 * @return on success
	 */
	public boolean leaveAllJobs() {
		// synchronized (saveLock) {
		progression.clear();
		reloadHonorific();
		Jobs.getPermissionHandler().recalculatePermissions(this);
		return true;
		// }
	}

	/**
	 * Promotes player in job
	 * 
	 * @param job
	 *            - the job being promoted
	 * @param levels
	 *            - number of levels to promote
	 */
	public void promoteJob(Job job, int levels) {
		// synchronized (saveLock) {
		JobProgression prog = getJobProgression(job);
		if (prog == null)
			return;
		if (levels <= 0)
			return;
		int newLevel = prog.getLevel() + levels;

		int maxLevel = job.getMaxLevel();

		if (this.havePermission("jobs." + job.getName() + ".vipmaxlevel") && job.getVipMaxLevel() != 0)
			maxLevel = job.getVipMaxLevel();

		if (maxLevel > 0 && newLevel > maxLevel) {
			newLevel = maxLevel;
		}
		setLevel(job, newLevel);
		// }
	}

	/**
	 * Demotes player in job
	 * 
	 * @param job
	 *            - the job being deomoted
	 * @param levels
	 *            - number of levels to demote
	 */
	public void demoteJob(Job job, int levels) {
		// synchronized (saveLock) {
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
		// }
	}

	/**
	 * Sets player to a specific level
	 * 
	 * @param job
	 *            - the job
	 * @param level
	 *            - the level
	 */
	private void setLevel(Job job, int level) {
		// synchronized (saveLock) {
		JobProgression prog = getJobProgression(job);
		if (prog == null)
			return;

		if (level != prog.getLevel()) {
			prog.setLevel(level);
			reloadHonorific();
			Jobs.getPermissionHandler().recalculatePermissions(this);
		}
		// }
	}

	/**
	 * Player leaves a job
	 * 
	 * @param oldjob
	 *            - the old job
	 * @param newjob
	 *            - the new job
	 */
	public boolean transferJob(Job oldjob, Job newjob) {
		// synchronized (saveLock) {
		if (!isInJob(newjob)) {
			for (JobProgression prog : progression) {
				if (!prog.getJob().same(oldjob))
					continue;

				prog.setJob(newjob);

				int maxLevel = 0;
				if (this.havePermission("jobs." + newjob.getName() + ".vipmaxlevel"))
					maxLevel = newjob.getVipMaxLevel();
				else
					maxLevel = newjob.getMaxLevel();

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
		// }
	}

	/**
	 * Checks if the player is in this job.
	 * 
	 * @param job
	 *            - the job
	 * @return true - they are in the job
	 * @return false - they are not in the job
	 */
	public boolean isInJob(Job job) {
		for (JobProgression prog : progression) {
			if (prog.getJob().same(job))
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
				Title title = Jobs.gettitleManager().getTitleForLevel(prog.getLevel());

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

				if (numJobs > 1 && (method.equals(DisplayMethod.FULL) || method.equals(DisplayMethod.TITLE))
						|| method.equals(DisplayMethod.SHORT_FULL) || method.equals(DisplayMethod.SHORT_TITLE)) {
					// add title to honorific
					if (title != null) {
						String honorificpart = title.getChatColor() + title.getShortName() + ChatColor.WHITE;
						if (honorificpart.contains("{level}"))
							honorificpart = honorificpart.replace("{level}", String.valueOf(prog.getLevel()));
						builder.append(honorificpart);
						gotTitle = true;
					}
				}

				if (numJobs > 1 && (method.equals(DisplayMethod.FULL) || method.equals(DisplayMethod.JOB))
						|| method.equals(DisplayMethod.SHORT_FULL) || method.equals(DisplayMethod.SHORT_JOB)) {
					String honorificpart = prog.getJob().getChatColor() + prog.getJob().getShortName()
							+ ChatColor.WHITE;
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
					String honorificpart = Jobs.getNoneJob().getChatColor() + Jobs.getNoneJob().getName()
							+ ChatColor.WHITE;
					if (honorificpart.contains("{level}"))
						honorificpart = honorificpart.replace("{level}", "");
					builder.append(honorificpart);
				}

				if (metod.equals(DisplayMethod.SHORT_FULL) || metod.equals(DisplayMethod.SHORT_TITLE)
						|| metod.equals(DisplayMethod.SHORT_JOB)) {
					String honorificpart = Jobs.getNoneJob().getChatColor() + Jobs.getNoneJob().getShortName()
							+ ChatColor.WHITE;
					if (honorificpart.contains("{level}"))
						honorificpart = honorificpart.replace("{level}", "");
					builder.append(honorificpart);
				}
			}
		}

		honorific = builder.toString().trim();
		if (honorific.length() > 0)
			honorific = Jobs.getGCManager().getModifyChatPrefix() + honorific
					+ Jobs.getGCManager().getModifyChatSuffix();

	}

	/**
	 * Performs player save
	 * 
	 * @param dao
	 */
	public void save() {
		// synchronized (saveLock) {
		if (!isSaved()) {
			JobsDAO dao = Jobs.getJobsDAO();
			dao.save(this);
			dao.saveLog(this);
			dao.savePoints(this);
			setSaved(true);
		}
		// }
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
		// Jobs.getJobsDAO().savePoints(this);
		clearBossMaps();
		isOnline = false;
	}

	public void clearBossMaps() {
		for (BossBarInfo one : barMap) {
			one.cancel();
		}
		barMap.clear();
	}

	/**
	 * Whether or not player is online
	 * 
	 * @return true if online, otherwise false
	 */
	public boolean isOnline() {
		return isOnline;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean value) {
		isSaved = value;
	}
}
