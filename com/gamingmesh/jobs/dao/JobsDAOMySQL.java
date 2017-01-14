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
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.stuff.UUIDUtil;

public class JobsDAOMySQL extends JobsDAO {
    private String database;

    private JobsDAOMySQL(Jobs plugin, String hostname, String database, String username, String password, String prefix) {
	super(plugin, "com.mysql.jdbc.Driver", "jdbc:mysql://" + hostname + "/" + database, username, password, prefix);
	this.database = database;
    }

    public static JobsDAOMySQL initialize(Jobs plugin, String hostname, String database, String username, String password, String prefix) {
	JobsDAOMySQL dao = new JobsDAOMySQL(plugin, hostname, database, username, password, prefix);
	try {
	    dao.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return dao;
    }

    private static void close(ResultSet res) {
	if (res != null) {
	    try {
		res.close();
	    } catch (SQLException e) {
	    }
	}
    }

    private static void close(PreparedStatement prest) {
	if (prest != null) {
	    try {
		prest.close();
	    } catch (SQLException e) {
	    }
	}
    }

    @Override
    protected synchronized void setupConfig() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	ResultSet res = null;
	try {
	    // Check for config table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "config");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    close(res);
	    close(prest);
	}

	if (rows == 0) {
	    PreparedStatement insert = null;
	    try {
		executeSQL("CREATE TABLE `" + getPrefix() + "config` (`key` varchar(50) NOT NULL PRIMARY KEY, `value` varchar(100) NOT NULL);");

		insert = conn.prepareStatement("INSERT INTO `" + getPrefix() + "config` (`key`, `value`) VALUES (?, ?);");
		insert.setString(1, "version");
		insert.setString(2, "1");
		insert.execute();
	    } finally {
		close(insert);
	    }
	}
    }

    @Override
    protected synchronized void checkUpdate() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	createDefaultJobsBase();
	createDefaultLogBase();
	createDefaultArchiveBase();
	createDefaultPointsBase();
	createDefaultExploreBase();
	createDefaultUsersBase();
	createDefaultBlockProtection();
	createDefaultLimitBase();
    }

    @Override
    protected synchronized void checkUpdate2() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	ResultSet res = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "jobs");
	    prest.setString(3, "username");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    if (rows == 0)
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` ADD COLUMN `username` varchar(20);");
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate4() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	ResultSet res = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "archive");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    if (rows == 0)
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate5() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	ResultSet res = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "log");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    if (rows == 0)
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate6() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	try {
	    executeSQL("ALTER TABLE `" + getPrefix() + "log` MODIFY `itemname` VARCHAR(60);");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    protected synchronized void checkUpdate7() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}

	boolean convertJobs = true;
	PreparedStatement tempPst = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "jobs`;");
	ResultSet tempRes = tempPst.executeQuery();

	boolean noJobsdata = true;
	try {
	    while (tempRes.next()) {
		noJobsdata = false;
		tempRes.getByte("player_uuid");
		break;
	    }
	} catch (Exception e) {
	    convertJobs = false;
	} finally {
	    tempRes.close();
	    tempPst.close();
	}
	if (noJobsdata) {
	    dropDataBase("jobs");
	    createDefaultJobsBase();
	    convertJobs = false;
	}

	if (convertJobs) {
	    Jobs.getPluginLogger().info("Converting byte uuids to string.  This could take a long time!!!");
	    // Converting jobs players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "jobs_temp` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL,`username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pst1 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "jobs`;");
	    ResultSet rs = pst1.executeQuery();
	    PreparedStatement insert = null;

	    conn.setAutoCommit(false);

	    while (rs.next()) {

		byte[] uuidBytes = rs.getBytes("player_uuid");

		if (uuidBytes == null)
		    continue;

		String uuid = UUIDUtil.fromBytes(uuidBytes).toString();

		if (uuid != null) {
		    insert = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "jobs_temp` (`player_uuid`, `username`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?, ?);");
		    insert.setString(1, uuid);
		    insert.setString(2, rs.getString("username"));
		    insert.setString(3, rs.getString("job"));
		    insert.setInt(4, rs.getInt("experience"));
		    insert.setInt(5, rs.getInt("level"));
		    insert.addBatch();
		}
	    }

	    if (insert != null)
		insert.executeBatch();

	    conn.commit();
	    conn.setAutoCommit(true);

	    if (insert != null)
		insert.close();
	    rs.close();
	    pst1.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "jobs`;");
	    executeSQL("ALTER TABLE `" + getPrefix() + "jobs_temp` RENAME TO `" + getPrefix() + "jobs`;");
	}

	boolean convertArchive = true;
	PreparedStatement tempArchivePst = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	ResultSet tempArchiveRes = tempArchivePst.executeQuery();

	boolean noArchivedata = true;
	try {
	    while (tempArchiveRes.next()) {
		noArchivedata = false;
		tempArchiveRes.getByte("player_uuid");
		break;
	    }
	} catch (Exception e) {
	    convertArchive = false;
	} finally {
	    tempArchiveRes.close();
	    tempArchivePst.close();
	}
	if (noArchivedata) {
	    dropDataBase("archive");
	    createDefaultArchiveBase();
	    convertArchive = false;
	}

	if (convertArchive) {
	    // Converting archive players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive_temp` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pst11 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	    ResultSet rs1 = pst11.executeQuery();
	    PreparedStatement insert1 = null;

	    conn.setAutoCommit(false);

	    while (rs1.next()) {
		String uuid = UUIDUtil.fromBytes(rs1.getBytes("player_uuid")).toString();
		if (uuid != null) {
		    insert1 = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "archive_temp` (`player_uuid`, `username`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?, ?);");
		    insert1.setString(1, uuid);
		    insert1.setString(2, rs1.getString("username"));
		    insert1.setString(3, rs1.getString("job"));
		    insert1.setInt(4, rs1.getInt("experience"));
		    insert1.setInt(5, rs1.getInt("level"));
		    insert1.addBatch();
		}
	    }
	    if (insert1 != null)
		insert1.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    rs1.close();
	    pst11.close();
	    if (insert1 != null)
		insert1.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "archive`;");
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "archive_temp` RENAME TO `" + getPrefix() + "archive`;");
	    } catch (Exception e) {
	    }
	}

	boolean convertLog = true;
	PreparedStatement tempLogPst = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	ResultSet tempLogRes = tempLogPst.executeQuery();

	boolean nodata = true;
	try {
	    while (tempLogRes.next()) {
		nodata = false;
		tempLogRes.getByte("player_uuid");
		break;
	    }
	} catch (Exception e) {
	    convertLog = false;
	} finally {
	    tempLogRes.close();
	    tempLogPst.close();
	}
	if (nodata) {
	    dropDataBase("log");
	    createDefaultLogBase();
	    convertLog = false;
	}

	if (convertLog) {
	    Bukkit.getConsoleSender().sendMessage("Converting log database");
	    // Converting log players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log_temp` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pst111 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	    ResultSet rs11 = pst111.executeQuery();
	    PreparedStatement insert11 = null;

	    conn.setAutoCommit(false);

	    while (rs11.next()) {
		String uuid = UUIDUtil.fromBytes(rs11.getBytes("player_uuid")).toString();
		if (uuid != null) {
		    insert11 = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "log_temp` (`player_uuid`, `username`, `time`, `action`, `itemname`, `count`, `money`, `exp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
		    insert11.setString(1, uuid);
		    insert11.setString(2, rs11.getString("username"));
		    insert11.setLong(3, rs11.getLong("time"));
		    insert11.setString(4, rs11.getString("action"));
		    insert11.setString(5, rs11.getString("itemname"));
		    insert11.setInt(6, rs11.getInt("count"));
		    insert11.setDouble(7, rs11.getDouble("money"));
		    insert11.setDouble(8, rs11.getDouble("exp"));
		    insert11.addBatch();
		}
	    }
	    if (insert11 != null)
		insert11.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    pst111.close();
	    rs11.close();
	    if (insert11 != null)
		insert11.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    protected synchronized void checkUpdate8() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	ResultSet res = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    prest.setString(1, database);
	    prest.setString(2, getPrefix() + "explore");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    if (rows == 0)
		try {
		    executeSQL("CREATE TABLE `" + getPrefix()
			+ "explore` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `worldname` varchar(64), `chunkX` int, `chunkZ` int, `playerName` varchar(32));");
		} catch (SQLException e) {
		}
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate9() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}

	PreparedStatement tempPrest = null;
	ResultSet res = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    tempPrest = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;");
	    tempPrest.setString(1, database);
	    tempPrest.setString(2, getPrefix() + "users");
	    res = tempPrest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(tempPrest);
	}
	// Create new points table
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "points` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `totalpoints` double, `currentpoints` double);");
	} catch (SQLException e) {
	}

	// dropping 2 columns
	PreparedStatement prestLogTemp = null;
	ResultSet rsLogTemp = null;
	boolean next = false;
	try {
	    prestLogTemp = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	    rsLogTemp = prestLogTemp.executeQuery();
	    while (rsLogTemp.next()) {
		next = true;
		rsLogTemp.getInt("userid");
		rsLogTemp.getLong("time");
		rsLogTemp.getString("action");
		rsLogTemp.getString("itemname");
		rsLogTemp.getInt("count");
		rsLogTemp.getDouble("money");
		rsLogTemp.getDouble("exp");
		break;
	    }
	} catch (Exception ex) {
	} finally {
	    close(rsLogTemp);
	    close(prestLogTemp);
	}

	if (!next) {
	    dropDataBase("log");
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	    } catch (Exception e) {
	    }
	}

	if (rows == 0) {
	    HashMap<String, String> tempMap = new HashMap<String, String>();
	    PreparedStatement prestJobs = null;
	    ResultSet res1 = null;
	    try {
		prestJobs = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "jobs;");
		res1 = prestJobs.executeQuery();
		while (res1.next()) {
		    tempMap.put(res1.getString("player_uuid"), res1.getString("username"));
		}
	    } catch (Exception e) {
	    } finally {
		close(res1);
		close(prestJobs);
	    }

	    PreparedStatement prestArchive = null;
	    ResultSet res2 = null;
	    try {
		prestArchive = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "archive;");
		res2 = prestArchive.executeQuery();
		while (res2.next()) {
		    tempMap.put(res2.getString("player_uuid"), res2.getString("username"));
		}
	    } catch (Exception e) {
	    } finally {
		close(res2);
		close(prestArchive);
	    }

	    PreparedStatement prestLog = null;
	    ResultSet res3 = null;
	    try {
		prestLog = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "log;");
		res3 = prestLog.executeQuery();
		while (res3.next()) {
		    tempMap.put(res3.getString("player_uuid"), res3.getString("username"));
		}
	    } catch (Exception e) {
	    } finally {
		close(res3);
		close(prestLog);
	    }

	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "users` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20));");
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    PreparedStatement prestUsers = null;
	    try {
		prestUsers = conn.prepareStatement("INSERT INTO `" + getPrefix() + "users` (`player_uuid`, `username`) VALUES (?, ?);");
		conn.setAutoCommit(false);
		for (Entry<String, String> users : tempMap.entrySet()) {
		    prestUsers.setString(1, users.getKey());
		    prestUsers.setString(2, users.getValue());
		    prestUsers.addBatch();
		}
		prestUsers.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		close(prestUsers);
	    }

	    HashMap<String, PlayerInfo> tempPlayerMap = new HashMap<String, PlayerInfo>();

	    PreparedStatement prestUsersT = null;
	    ResultSet res4 = null;
	    try {
		prestUsersT = conn.prepareStatement("SELECT * FROM " + getPrefix() + "users;");
		res4 = prestUsersT.executeQuery();
		while (res4.next()) {
		    tempPlayerMap.put(res4.getString("player_uuid"), new PlayerInfo(res4.getString("username"), res4.getInt("id"), UUID.fromString(res4.getString("player_uuid")), System.currentTimeMillis()));
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		close(res4);
		close(prestUsersT);
	    }

	    // Modifying jobs main table
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` ADD COLUMN `userid` int;");
	    } catch (Exception e) {
	    }
	    PreparedStatement prestJobsT = null;
	    try {
		prestJobsT = conn.prepareStatement("UPDATE `" + getPrefix() + "jobs` SET `userid` = ? WHERE `player_uuid` = ?;");
		conn.setAutoCommit(false);
		for (Entry<String, PlayerInfo> users : tempPlayerMap.entrySet()) {
		    prestJobsT.setInt(1, users.getValue().getID());
		    prestJobsT.setString(2, users.getKey());
		    prestJobsT.addBatch();
		}
		prestJobsT.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		close(prestJobsT);
	    }

	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` DROP COLUMN `player_uuid`, DROP COLUMN `username`;");
	    } catch (Exception e) {
	    }
	    // Modifying jobs archive table
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "archive` ADD COLUMN `userid` int;");
	    } catch (Exception e) {
	    }

	    PreparedStatement prestArchiveT = null;
	    try {
		prestArchiveT = conn.prepareStatement("UPDATE `" + getPrefix() + "archive` SET `userid` = ? WHERE `player_uuid` = ?;");
		conn.setAutoCommit(false);
		for (Entry<String, PlayerInfo> users : tempPlayerMap.entrySet()) {
		    prestArchiveT.setInt(1, users.getValue().getID());
		    prestArchiveT.setString(2, users.getKey());
		    prestArchiveT.addBatch();
		}
		prestArchiveT.executeBatch();
		conn.commit();
		conn.setAutoCommit(true);
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		close(prestArchiveT);
	    }

	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "archive` DROP COLUMN `player_uuid`, DROP COLUMN `username`;");
	    } catch (Exception e) {
	    }
	}
    }

    @Override
    protected synchronized void checkUpdate10() {
	createDefaultBlockProtection();
    }

    @Override
    protected synchronized void checkUpdate11() {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}

	try {
	    executeSQL("ALTER TABLE `" + getPrefix() + "users` ADD COLUMN `seen` bigint;");
	} catch (Exception e) {
	    return;
	} finally {
	}

	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("UPDATE `" + getPrefix() + "users` SET `seen` = ?;");
	    prest.setLong(1, System.currentTimeMillis());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    private boolean createDefaultExploreBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "explore` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `worldname` varchar(64), `chunkX` int, `chunkZ` int, `playerName` varchar(32));");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultPointsBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "points` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `totalpoints` double, `currentpoints` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    @Override
    protected boolean createDefaultLogBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "log` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    @Override
    protected boolean createDefaultArchiveBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "archive` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultJobsBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "jobs` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultUsersBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "users` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `seen` bigint);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultBlockProtection() {
	try {
	    executeSQL("CREATE TABLE IF NOT EXISTS `" + getPrefix()
		+ "blocks` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `world` varchar(36) NOT NULL, `x` int, `y` int, `z` int, `recorded` bigint, `resets` bigint);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultLimitBase() {
	try {
	    executeSQL("CREATE TABLE IF NOT EXISTS `" + getPrefix()
		+ "limits` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `userid` int, `type` varchar(36), `collected` double, `started` bigint);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    @Override
    protected boolean dropDataBase(String name) {
	try {
	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + name + "`;");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }
}
