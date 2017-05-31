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

package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.JobsConfiguration;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.util.UUIDUtil;

/**
 * Data Access Object interface for the Jobs plugin
 * 
 * Interface that holds all methods that a DAO needs to have
 * @author Alex
 *
 */
public abstract class JobsDAO {

	private JobsConnectionPool pool;
	private String prefix;

	protected JobsDAO(String driverName, String url, String username, String password, String prefix) {
		this.prefix = prefix;
		try {
			pool = new JobsConnectionPool(driverName, url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final synchronized void setUp() throws SQLException {
		setupConfig();
		int version = getSchemaVersion();
		if (version == 0) {
			Jobs.getPluginLogger().severe("Could not initialize database!  Could not determine schema version!");
			return;
		}

		try {
			if (version <= 1)
				checkUpdate1();

			if (version <= 2)
				checkUpdate2();

			if (version <= 3)
				checkUpdate4();

			version = 4;
		} finally {
			updateSchemaVersion(version);
		}
	}

	protected abstract void setupConfig() throws SQLException;

	protected abstract void checkUpdate1() throws SQLException;

	protected abstract void checkUpdate2() throws SQLException;

	protected abstract void checkUpdate4() throws SQLException;

	/**
	 * Gets the database prefix
	 * @return the prefix
	 */
	protected String getPrefix() {
		return prefix;
	}

	/**
	 * Get all jobs the player is part of.
	 * @param playerUUID - the player being searched for
	 * @return list of all of the names of the jobs the players are part of.
	 */
	public synchronized List<JobsDAOData> getAllJobs(OfflinePlayer player) {
		ArrayList<JobsDAOData> jobs = new ArrayList<JobsDAOData>();
		JobsConnection conn = getConnection();
		if (conn == null)
			return jobs;
		try {
			PreparedStatement prest = conn.prepareStatement("SELECT `player_uuid`, `job`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `player_uuid` = ?;");
			prest.setBytes(1, UUIDUtil.toBytes(player.getUniqueId()));
			ResultSet res = prest.executeQuery();
			while (res.next()) {
				jobs.add(new JobsDAOData(UUIDUtil.fromBytes(res.getBytes(1)), res.getString(2), res.getInt(3), res.getInt(4)));
			}
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobs;
	}

	/**
	 * Get all jobs the player is part of.
	 * @param userName - the player being searched for
	 * @return list of all of the names of the jobs the players are part of.
	 */
	public synchronized List<JobsDAOData> getAllJobsOffline(String userName) {
		ArrayList<JobsDAOData> jobs = new ArrayList<JobsDAOData>();
		JobsConnection conn = getConnection();
		if (conn == null)
			return jobs;
		try {
			PreparedStatement prest = conn.prepareStatement("SELECT `player_uuid`, `job`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `username` LIKE ?;");
			prest.setString(1, userName);
			ResultSet res = prest.executeQuery();
			while (res.next()) {
				jobs.add(new JobsDAOData(UUIDUtil.fromBytes(res.getBytes(1)), res.getString(2), res.getInt(3), res.getInt(4)));
			}
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobs;
	}

	/**
	 * Join a job (create player-job entry from storage)
	 * @param player - player that wishes to join the job
	 * @param job - job that the player wishes to join
	 */
	public synchronized void joinJob(JobsPlayer jPlayer, Job job) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return;
		try {
			int level = 1;
			int exp = 0;
			if (checkArchive(jPlayer, job).size() > 0) {
				List<Integer> info = checkArchive(jPlayer, job);
				level = info.get(0);
				//exp = info.get(1);
				deleteArchive(jPlayer, job);
			}
			PreparedStatement prest = conn.prepareStatement("INSERT INTO `" + prefix + "jobs` (`player_uuid`, `username`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?, ?);");
			prest.setBytes(1, UUIDUtil.toBytes(jPlayer.getPlayerUUID()));
			prest.setString(2, jPlayer.getUserName());
			prest.setString(3, job.getName());
			prest.setInt(4, level);
			prest.setInt(5, exp);
			prest.execute();
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Quit a job (delete player-job entry from storage)
	 * @param player - player that wishes to quit the job
	 * @param job - job that the player wishes to quit
	 */
	public synchronized void quitJob(JobsPlayer jPlayer, Job job) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return;
		try {
			PreparedStatement prest = conn.prepareStatement("DELETE FROM `" + prefix + "jobs` WHERE `player_uuid` = ? AND `job` = ?;");
			prest.setBytes(1, UUIDUtil.toBytes(jPlayer.getPlayerUUID()));
			prest.setString(2, job.getName());
			prest.execute();
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Record job to archive
	 * @param player - player that wishes to quit the job
	 * @param job - job that the player wishes to quit
	 */
	public synchronized void recordToArchive(JobsPlayer jPlayer, Job job) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return;
		try {
			int level = 1;
			int exp = 0;
			for (JobProgression progression : jPlayer.getJobProgression()) {
				if (progression.getJob().getName().equalsIgnoreCase(job.getName())) {
					level = progression.getLevel();
					exp = (int) progression.getExperience();
				}
			}
			PreparedStatement prest = conn.prepareStatement("INSERT INTO `" + prefix + "archive` (`player_uuid`, `username`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?, ?);");
			prest.setBytes(1, UUIDUtil.toBytes(jPlayer.getPlayerUUID()));
			prest.setString(2, jPlayer.getUserName());
			prest.setString(3, job.getName());
			prest.setInt(4, level);
			prest.setInt(5, exp);
			prest.execute();
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check job in archive
	 * @param player - player that wishes to quit the job
	 * @param job - job that the player wishes to quit
	 */
	public synchronized List<Integer> checkArchive(JobsPlayer jPlayer, Job job) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return null;
		try {
			List<Integer> info = new ArrayList<Integer>();
			PreparedStatement prest = conn.prepareStatement("SELECT `level`, `experience` FROM `" + prefix + "archive` WHERE `player_uuid` = ? AND `job` = ?;");
			prest.setBytes(1, UUIDUtil.toBytes(jPlayer.getPlayerUUID()));
			prest.setString(2, job.getName());
			ResultSet res = prest.executeQuery();
			if (res.next()) {
				int level = (int) ((res.getInt(1) - (res.getInt(1) * (JobsConfiguration.levelLossPercentage / 100.0))));
				if (level < 1)
					level = 1;
				if (JobsConfiguration.fixAtMaxLevel && res.getInt(1) == job.getMaxLevel())
					level = res.getInt(1);
				info.add(level);
				info.add(res.getInt(2));
			}
			prest.close();
			return info;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all jobs from archive by player
	 * @param player - targeted player
	 * @return info - information about jobs
	 */
	public synchronized List<String> getJobsFromArchive(JobsPlayer jPlayer) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return null;
		try {
			List<String> info = new ArrayList<String>();
			PreparedStatement prest = conn.prepareStatement("SELECT `job`, `level`, `experience`  FROM `" + prefix + "archive` WHERE `player_uuid` = ?;");
			prest.setBytes(1, UUIDUtil.toBytes(jPlayer.getPlayerUUID()));
			ResultSet res = prest.executeQuery();
			while (res.next()) {

				int level = (int) ((res.getInt(2) - (res.getInt(2) * (JobsConfiguration.levelLossPercentage / 100.0))));
				if (level < 1)
					level = 1;
				if (JobsConfiguration.fixAtMaxLevel && res.getInt(2) == Jobs.getJob(res.getString(1)).getMaxLevel())
					level = res.getInt(2);

				info.add(res.getString(1) + ":" + res.getInt(2) + ":" + level + ":" + res.getInt(3));
			}
			prest.close();
			return info;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Delete job from archive
	 * @param player - player that wishes to quit the job
	 * @param job - job that the player wishes to quit
	 */
	public synchronized void deleteArchive(JobsPlayer jPlayer, Job job) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return;
		try {
			PreparedStatement prest = conn.prepareStatement("DELETE FROM `" + prefix + "archive` WHERE `player_uuid` = ? AND `job` = ?;");
			prest.setBytes(1, UUIDUtil.toBytes(jPlayer.getPlayerUUID()));
			prest.setString(2, job.getName());
			prest.execute();
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save player-job information
	 * @param jobInfo - the information getting saved
	 */
	public synchronized void save(JobsPlayer player) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return;
		try {
			PreparedStatement prest = conn.prepareStatement("UPDATE `" + prefix + "jobs` SET `level` = ?, `experience` = ? WHERE `player_uuid` = ? AND `job` = ?;");
			for (JobProgression progression : player.getJobProgression()) {
				prest.setInt(1, progression.getLevel());
				prest.setInt(2, (int) progression.getExperience());
				prest.setBytes(3, UUIDUtil.toBytes(player.getPlayerUUID()));
				prest.setString(4, progression.getJob().getName());
				prest.execute();
			}
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show top list
	 * @param toplist - toplist by jobs name
	 * @return 
	 */
	public synchronized ArrayList<String> toplist(String jobsname) {
		ArrayList<String> jobs = new ArrayList<String>();
		JobsConnection conn = getConnection();
		if (conn == null)
			return jobs;
		try {
			PreparedStatement prest = conn.prepareStatement("SELECT `username`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `job` LIKE ? ORDER BY `level` DESC LIMIT 10;");
			prest.setString(1, jobsname);
			ResultSet res = prest.executeQuery();
			while (res.next()) {
				jobs.add(res.getString(1) + "%" + res.getInt(2) + "%" + res.getInt(3));
			}
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobs;
	}

	/**
	 * Get the number of players that have a particular job
	 * @param job - the job
	 * @return  the number of players that have a particular job
	 */
	public synchronized int getSlotsTaken(Job job) {
		int slot = 0;
		JobsConnection conn = getConnection();
		if (conn == null)
			return slot;
		try {
			PreparedStatement prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + prefix + "jobs` WHERE `job` = ?;");
			prest.setString(1, job.getName());
			ResultSet res = prest.executeQuery();
			if (res.next()) {
				slot = res.getInt(1);
			}
			prest.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return slot;
	}

	/**
	 * Gets the current schema version
	 * @return schema version number
	 */
	protected int getSchemaVersion() {
		JobsConnection conn = getConnection();
		if (conn == null)
			return 0;
		PreparedStatement prest = null;
		try {
			prest = conn.prepareStatement("SELECT `value` FROM `" + prefix + "config` WHERE `key` = ?;");
			prest.setString(1, "version");
			ResultSet res = prest.executeQuery();
			if (res.next()) {
				return Integer.valueOf(res.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			if (prest != null) {
				try {
					prest.close();
				} catch (SQLException e) {
				}
			}
		}

		return 0;
	}

	/**
	 * Updates schema to version number
	 * @param version
	 */
	protected void updateSchemaVersion(int version) {
		updateSchemaConfig("version", Integer.toString(version));
	}

	/**
	 * Updates configuration value
	 * @param key - the configuration key
	 * @param value - the configuration value
	 */
	private void updateSchemaConfig(String key, String value) {
		JobsConnection conn = getConnection();
		if (conn == null)
			return;
		PreparedStatement prest = null;
		try {
			prest = conn.prepareStatement("UPDATE `" + prefix + "config` SET `value` = ? WHERE `key` = ?;");
			prest.setString(1, value);
			prest.setString(2, key);
			prest.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prest != null) {
				try {
					prest.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * Executes an SQL query
	 * @param sql - The SQL
	 * @throws SQLException
	 */
	public void executeSQL(String sql) throws SQLException {
		JobsConnection conn = getConnection();
		Statement stmt = conn.createStatement();
		try {
			stmt.execute(sql);
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * Get a database connection
	 * @return  JobsConnection object
	 * @throws SQLException 
	 */
	protected JobsConnection getConnection() {
		try {
			return pool.getConnection();
		} catch (SQLException e) {
			Jobs.getPluginLogger().severe("Unable to connect to the database: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Close all active database handles
	 */
	public synchronized void closeConnections() {
		pool.closeConnection();
	}
}
