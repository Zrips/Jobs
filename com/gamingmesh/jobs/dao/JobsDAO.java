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
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.stuff.Loging;
import com.gamingmesh.jobs.stuff.TimeManage;

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
	    else if (version <= 2)
		checkUpdate2();

	    checkUpdate4();
	    checkUpdate5();

	    if (version <= 5)
		checkUpdate6();

	    if (version <= 6)
		checkUpdate7();

	    // creating explore database
	    checkUpdate8();

	    if (version <= 8)
		checkUpdate9();

	    version = 9;
	    updateSchemaVersion(version);
	} finally {
	}
    }

    protected abstract void setupConfig() throws SQLException;

    protected abstract void checkUpdate1() throws SQLException;

    protected abstract void checkUpdate2() throws SQLException;

    protected abstract void checkUpdate4() throws SQLException;

    protected abstract void checkUpdate5() throws SQLException;

    protected abstract void checkUpdate6() throws SQLException;

    protected abstract void checkUpdate7() throws SQLException;

    protected abstract void checkUpdate8() throws SQLException;

    protected abstract void checkUpdate9() throws SQLException;

    /**
     * Gets the database prefix
     * @return the prefix
     */
    protected String getPrefix() {
	return prefix;
    }

    public synchronized List<JobsDAOData> getAllJobs(OfflinePlayer player) {
	return getAllJobs(player.getName(), player.getUniqueId());
    }

    /**
     * Get all jobs the player is part of.
     * @param playerUUID - the player being searched for
     * @return list of all of the names of the jobs the players are part of.
     */
    public synchronized List<JobsDAOData> getAllJobs(String playerName, UUID uuid) {

	int id = -1;
	PlayerInfo userData = Jobs.getPlayerManager().getPlayerMap().get(uuid.toString());
	if (userData == null) {
	    id = recordNewPlayer(playerName, uuid);
	} else
	    id = userData.getID();

	ArrayList<JobsDAOData> jobs = new ArrayList<JobsDAOData>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT `job`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `userid` = ?;");
	    prest.setInt(1, id);
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		jobs.add(new JobsDAOData(uuid, res.getString(1), res.getInt(2), res.getInt(3)));
	    }
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return jobs;
    }

    public int recordNewPlayer(Player player) {
	return recordNewPlayer((OfflinePlayer) player);
    }

    public int recordNewPlayer(OfflinePlayer player) {
	return recordNewPlayer(player.getName(), player.getUniqueId());
    }

    public int recordNewPlayer(String playerName, UUID uuid) {

	int id = -1;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return id;
	try {
	    PreparedStatement prest = conn.prepareStatement("INSERT INTO `" + prefix + "users` (`player_uuid`, `username`) VALUES (?, ?);");
	    prest.setString(1, uuid.toString());
	    prest.setString(2, playerName);
	    prest.executeUpdate();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT `id` FROM `" + prefix + "users` WHERE `player_uuid` = ?;");
	    prest.setString(1, uuid.toString());
	    ResultSet res = prest.executeQuery();
	    res.next();
	    id = res.getInt("id");
	    Jobs.getPlayerManager().getPlayerMap().put(uuid.toString(), new PlayerInfo(playerName, id));
	    res.close();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return id;
    }

    /**
     * Get player count for a job.
     * @param JobName - the job name
     * @return amount of player currently working.
     */
    public synchronized int getTotalPlayerAmountByJobName(String JobName) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return 0;
	int count = 0;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + prefix + "jobs` WHERE `job` = ?;");
	    prest.setString(1, JobName);
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		count = res.getInt(1);
	    }
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return count;
    }

    /**
     * Get player count for a job.
     * @return total amount of player currently working.
     */
    public synchronized int getTotalPlayers() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return 0;
	int count = 0;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + prefix + "jobs`;");
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		count = res.getInt(1);
	    }
	    res.close();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return count;
    }

    /**
     * Get all jobs the player is part of.
     * @param userName - the player being searched for
     * @return list of all of the names of the jobs the players are part of.
     */
    public synchronized List<JobsDAOData> getAllJobsOffline(String userName) {

	ArrayList<JobsDAOData> jobs = new ArrayList<JobsDAOData>();

	Entry<String, PlayerInfo> info = Jobs.getPlayerManager().getPlayerInfoByName(userName);
	if (info == null)
	    return jobs;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT `job`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `userid` = ?;");
	    prest.setInt(1, info.getValue().getID());
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		jobs.add(new JobsDAOData(UUID.fromString(info.getKey()), res.getString(2), res.getInt(3), res.getInt(4)));
	    }
	    prest.close();
	    res.close();
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
		deleteArchive(jPlayer, job);
	    }
	    PreparedStatement prest = conn.prepareStatement("INSERT INTO `" + prefix + "jobs` (`userid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getName());
	    prest.setInt(3, level);
	    prest.setInt(4, exp);
	    prest.execute();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Join a job (create player-job entry from storage)
     * @param player - player that wishes to join the job
     * @param job - job that the player wishes to join
     * @throws SQLException 
     */
    public List<Convert> convertDatabase(Player Player, String table) throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return null;

	List<Convert> list = new ArrayList<Convert>();
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT * FROM `" + prefix + table + "`");
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		list.add(new Convert(res.getInt("id"), res.getInt("userid"), res.getString("job"), res.getInt("level"), res.getInt("experience")));
	    }
	    res.close();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	try {
	    conn.closeConnection();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return list;
    }

    public void continueConvertions(List<Convert> list, String table) throws SQLException {
	JobsConnection conns = this.getConnection();
	if (conns == null)
	    return;
	PreparedStatement insert = null;
	int i = list.size();
	try {

	    Statement statement = conns.createStatement();
	    if (Jobs.getGCManager().storageMethod.equalsIgnoreCase("sqlite")) {
		statement.executeUpdate("TRUNCATE `" + getPrefix() + table + "`");
	    } else {
		statement.executeUpdate("DELETE from `" + getPrefix() + table + "`");
	    }

	    insert = conns.prepareStatement("INSERT INTO `" + getPrefix() + table + "` (`userid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?);");
	    conns.setAutoCommit(false);
	    while (i > 0) {
		i--;
		Convert convertData = list.get(i);
		insert.setInt(1, convertData.GetId());
		insert.setString(2, convertData.GetJobName());
		insert.setInt(3, convertData.GetLevel());
		insert.setInt(4, convertData.GetExp());
		insert.addBatch();
	    }
	    insert.executeBatch();
	    conns.commit();
	    conns.setAutoCommit(true);
	    statement.close();
	} finally {
	    if (insert != null) {
		try {
		    insert.close();
		} catch (SQLException e) {
		}
	    }
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
	    PreparedStatement prest = conn.prepareStatement("DELETE FROM `" + prefix + "jobs` WHERE `userid` = ? AND `job` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
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
    public void recordToArchive(JobsPlayer jPlayer, Job job) {
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
	    PreparedStatement prest = conn.prepareStatement("INSERT INTO `" + prefix
		+ "archive` (`userid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getName());
	    prest.setInt(3, level);
	    prest.setInt(4, exp);
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
	List<Integer> info = new ArrayList<Integer>();
	if (conn == null)
	    return info;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT `level`, `experience` FROM `" + prefix + "archive` WHERE `userid` = ? AND `job` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getName());
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		int level = (int) ((res.getInt(1) - (res.getInt(1) * (Jobs.getGCManager().levelLossPercentage / 100.0))));
		if (level < 1)
		    level = 1;

		int maxLevel = 0;
		if (jPlayer.havePermission("jobs." + job.getName() + ".vipmaxlevel") && job.getVipMaxLevel() != 0)
		    maxLevel = job.getVipMaxLevel();
		else
		    maxLevel = job.getMaxLevel();

		if (Jobs.getGCManager().fixAtMaxLevel && res.getInt(1) == maxLevel)
		    level = res.getInt(1);
		info.add(level);
		info.add(res.getInt(2));
	    }
	    res.close();
	    prest.close();
	    return info;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return info;
    }

    public List<TopList> getGlobalTopList() {
	return getGlobalTopList(0);
    }

    /**
     * Get all jobs from archive by player
     * @param player - targeted player
     * @return info - information about jobs
     */
    public List<TopList> getGlobalTopList(int start) {
	JobsConnection conn = getConnection();

	List<TopList> names = new ArrayList<TopList>();

	if (conn == null)
	    return null;
	try {

	    PreparedStatement prest = conn.prepareStatement("SELECT userid, COUNT(*) AS amount,  sum(level) AS totallvl FROM `" + prefix
		+ "jobs` GROUP BY userid ORDER BY totallvl DESC LIMIT " + start + ",20;");
	    ResultSet res = prest.executeQuery();

	    while (res.next()) {

		Entry<String, PlayerInfo> info = Jobs.getPlayerManager().getPlayerInfoById(res.getInt(1));

		if (info == null)
		    continue;

		if (info.getValue().getName() == null)
		    continue;

		TopList top = new TopList(res.getInt("userid"), res.getInt("totallvl"), 0);

		names.add(top);
	    }
	    res.close();
	    prest.close();
	    return names;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return names;
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
	    PreparedStatement prest = conn.prepareStatement("SELECT `job`, `level`, `experience`  FROM `" + prefix + "archive` WHERE `userid` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {

		int level = (int) ((res.getInt(2) - (res.getInt(2) * (Jobs.getGCManager().levelLossPercentage / 100.0))));
		if (level < 1)
		    level = 1;

		int maxLevel = 0;
		if (jPlayer.havePermission("jobs." + Jobs.getJob(res.getString(1)).getName() + ".vipmaxlevel"))
		    maxLevel = Jobs.getJob(res.getString(1)).getVipMaxLevel();
		else
		    maxLevel = Jobs.getJob(res.getString(1)).getMaxLevel();

		if (Jobs.getGCManager().fixAtMaxLevel && res.getInt(2) == maxLevel)
		    level = res.getInt(2);

		info.add(res.getString(1) + ":" + res.getInt(2) + ":" + level + ":" + res.getInt(3));
	    }
	    res.close();
	    prest.close();
	    return info;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public void loadPlayerData() {
	Jobs.getPlayerManager().getPlayerMap().clear();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("SELECT *  FROM `" + prefix + "users`;");
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		Jobs.getPlayerManager().getPlayerMap().put(res.getString("player_uuid"), new PlayerInfo(res.getString("username"), res.getInt("id")));
	    }
	    res.close();
	    return;
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    if (prest != null)
		try {
		    prest.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	return;
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
	    PreparedStatement prest = conn.prepareStatement("DELETE FROM `" + prefix + "archive` WHERE `userid` = ? AND `job` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
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
    public void save(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {
	    PreparedStatement prest = conn.prepareStatement("UPDATE `" + prefix
		+ "jobs` SET `level` = ?, `experience` = ? WHERE `userid` = ? AND `job` = ?;");
	    for (JobProgression progression : player.getJobProgression()) {
		prest.setInt(1, progression.getLevel());
		prest.setInt(2, (int) progression.getExperience());
		prest.setInt(3, player.getUserId());
		prest.setString(4, progression.getJob().getName());
		prest.execute();
	    }
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void savePoints(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {

	    PlayerPoints pointInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(player.getPlayerUUID());

	    String req = "UPDATE `" + prefix + "points` SET `totalpoints` = ?, `currentpoints` = ? WHERE `userid` = ?;";
	    if (pointInfo.isNewEntry()) {
		pointInfo.setNewEntry(false);
		req = "INSERT INTO `" + prefix + "points` (`totalpoints`, `currentpoints`, `userid`) VALUES (?, ?, ?);";
	    }

	    PreparedStatement prest = conn.prepareStatement(req);

	    prest.setDouble(1, pointInfo.getTotalPoints());
	    prest.setDouble(2, pointInfo.getCurrentPoints());
	    prest.setInt(3, player.getUserId());
	    prest.execute();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void loadPoints(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT `totalpoints`, `currentpoints` FROM `" + prefix + "points` WHERE `userid` = ?;");
	    prest.setInt(1, player.getUserId());
	    ResultSet res = prest.executeQuery();

	    if (res.next()) {
		Jobs.getPlayerManager().getPointsData().addPlayer(player.getPlayerUUID(), res.getDouble("currentpoints"), res.getDouble("totalpoints"));
	    } else {
		Jobs.getPlayerManager().getPointsData().addPlayer(player.getPlayerUUID());
	    }
	    res.close();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     */
    public void saveLog(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {

	    PreparedStatement prest = conn.prepareStatement("UPDATE `" + prefix
		+ "log` SET `count` = ?, `money` = ?, `exp` = ? WHERE `userid` = ? AND `time` = ? AND `action` = ? AND `itemname` = ?;");
	    for (Log log : player.getLog()) {
		for (Entry<String, LogAmounts> one : log.getAmountList().entrySet()) {
		    if (one.getValue().isNewEntry())
			continue;

		    prest.setInt(1, one.getValue().getCount());
		    prest.setDouble(2, one.getValue().getMoney());
		    prest.setDouble(3, one.getValue().getExp());

		    prest.setInt(4, player.getUserId());
		    prest.setInt(5, log.getDate());
		    prest.setString(6, log.getActionType());
		    prest.setString(7, one.getKey());
		    prest.execute();
		}
	    }
	    prest = conn.prepareStatement("INSERT INTO `" + prefix
		+ "log` (`userid`, `time`, `action`, `itemname`, `count`, `money`, `exp`) VALUES (?, ?, ?, ?, ?, ?, ?);");
	    for (Log log : player.getLog()) {
		for (Entry<String, LogAmounts> one : log.getAmountList().entrySet()) {

		    if (!one.getValue().isNewEntry())
			continue;

		    one.getValue().setNewEntry(false);

		    prest.setInt(1, player.getUserId());
		    prest.setInt(2, log.getDate());
		    prest.setString(3, log.getActionType());
		    prest.setString(4, one.getKey());
		    prest.setInt(5, one.getValue().getCount());
		    prest.setDouble(6, one.getValue().getMoney());
		    prest.setDouble(7, one.getValue().getExp());
		    prest.execute();
		}
	    }
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     */
    public void loadLog(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {

	    int time = TimeManage.timeInInt();

	    PreparedStatement prest = conn.prepareStatement("SELECT * FROM `" + prefix + "log` WHERE `userid` = ?  AND `time` = ? ;");
	    prest.setInt(1, player.getUserId());
	    prest.setInt(2, time);
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		Loging.loadToLog(player, res.getString("action"), res.getString("itemname"), res.getInt("count"), res.getDouble("money"), res.getDouble("exp"));
	    }
	    res.close();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Save player-explore information
     * @param jobexplore - the information getting saved
     */
    public void saveExplore() {
	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {

	    PreparedStatement prest = null;
	    if (Jobs.getGCManager().storageMethod.equalsIgnoreCase("sqlite")) {
		prest = conn.prepareStatement("DELETE from `" + prefix + "explore`;");
	    } else
		prest = conn.prepareStatement("TRUNCATE TABLE `" + prefix + "explore`;");

	    prest.execute();
	    prest.close();

	    PreparedStatement prest2 = conn.prepareStatement("INSERT INTO `" + prefix + "explore` (`worldname`, `chunkX`, `chunkZ`, `playerName`) VALUES (?, ?, ?, ?);");
	    conn.setAutoCommit(false);
	    for (Entry<String, ExploreRegion> worlds : Jobs.getExplore().getWorlds().entrySet()) {
		for (ExploreChunk oneChunk : worlds.getValue().getChunks()) {
		    for (String oneuser : oneChunk.getPlayers()) {
			prest2.setString(1, worlds.getKey());
			prest2.setInt(2, oneChunk.getX());
			prest2.setInt(3, oneChunk.getZ());
			prest2.setString(4, oneuser);
			prest2.addBatch();
		    }
		}
	    }
	    prest2.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);
	    prest2.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Save player-explore information
     * @param jobexplore - the information getting saved
     */
    public void loadExplore() {
	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {

	    PreparedStatement prest = conn.prepareStatement("SELECT * FROM `" + prefix + "explore`;");
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		Jobs.getExplore().ChunkRespond(res.getString("playerName"), res.getString("worldname"), res.getInt("chunkX"), res.getInt("chunkZ"));
	    }
	    res.close();
	    prest.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     * @return 
     */
    public List<Integer> getLognameList(int fromtime, int untiltime) {
	JobsConnection conn = getConnection();
	List<Integer> nameList = new ArrayList<Integer>();
	if (conn == null)
	    return nameList;
	try {

	    PreparedStatement prest = conn.prepareStatement("SELECT `userid` FROM `" + prefix + "log` WHERE `time` >= ?  AND `time` <= ? ;");
	    prest.setInt(1, fromtime);
	    prest.setInt(2, untiltime);
	    ResultSet res = prest.executeQuery();
	    while (res.next()) {
		if (!nameList.contains(res.getInt("userid")))
		    nameList.add(res.getInt("userid"));
	    }
	    res.close();
	    prest.close();
	    return nameList;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return nameList;
    }

    /**
     * Show top list
     * @param toplist - toplist by jobs name
     * @return 
     */
    public ArrayList<TopList> toplist(String jobsname, int limit) {
	ArrayList<TopList> jobs = new ArrayList<TopList>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;
	try {
	    PreparedStatement prest = conn.prepareStatement("SELECT `userid`, `level`, `experience` FROM `" + prefix
		+ "jobs` WHERE `job` LIKE ? ORDER BY `level` DESC, LOWER(userid) ASC LIMIT " + limit + ", 15;");
	    prest.setString(1, jobsname);
	    ResultSet res = prest.executeQuery();

	    while (res.next()) {

		Entry<String, PlayerInfo> info = Jobs.getPlayerManager().getPlayerInfoById(res.getInt(1));

		if (info == null)
		    continue;

		if (info.getValue().getName() == null)
		    continue;

		String name = info.getValue().getName();
		Player player = Bukkit.getPlayer(name);
		if (player != null) {

		    JobsPlayer jobsinfo = Jobs.getPlayerManager().getJobsPlayer(player);
		    Job job = Jobs.getJob(jobsname);
		    if (job != null) {
			JobProgression prog = jobsinfo.getJobProgression(job);
			jobs.add(new TopList(jobsinfo.getUserId(), prog.getLevel(), (int) prog.getExperience()));
		    }
		} else
		    jobs.add(new TopList(res.getInt(1), res.getInt(2), res.getInt(3)));
	    }
	    res.close();
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
	    res.close();
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
	    res.close();
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
