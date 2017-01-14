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

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.stuff.UUIDUtil;

public class JobsDAOSQLite extends JobsDAO {
    public static JobsDAOSQLite initialize(Jobs plugin) {
	JobsDAOSQLite dao = new JobsDAOSQLite(plugin);
	File dir = Jobs.getFolder();
	if (!dir.exists())
	    dir.mkdirs();
	try {
	    dao.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return dao;
    }

    private JobsDAOSQLite(Jobs plugin) {
	super(plugin, "org.sqlite.JDBC", "jdbc:sqlite:" + new File(Jobs.getFolder(), "jobs.sqlite.db").getPath(), null, null, "");
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
	ResultSet res = null;
	int rows = 0;
	try {
	    // Check for config table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "config");
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
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "jobs");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    if (rows > 0) {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` ADD COLUMN `username` varchar(20);");
	    }

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
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "archive");
	    res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    if (rows == 0) {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
	    }

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
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "log");
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
		    + "log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
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
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "log_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} catch (Exception e) {
	}
	try {
	    executeSQL("INSERT INTO `" + getPrefix() + "log_temp` SELECT `id`, `player_uuid`, `username`, `time`, `action`, `itemname`, `count`, `money`, `exp` FROM `"
		+ getPrefix() + "log`;");
	} catch (Exception e) {
	}
	executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	try {
	    executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");
	} catch (Exception e) {
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
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "jobs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	    } catch (SQLException e) {
	    }
	    convertJobs = false;
	}

	if (convertJobs) {

	    Jobs.getPluginLogger().info("Converting byte uuids to string.  This could take a long time!!!");
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "jobs_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL,`username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
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

	    rs.close();
	    pst1.close();
	    if (insert != null)
		insert.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "jobs`;");
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs_temp` RENAME TO `" + getPrefix() + "jobs`;");
	    } catch (Exception e) {
	    }
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
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	    } catch (SQLException e) {
	    }
	    convertArchive = false;
	}

	if (convertArchive) {
	    // Converting archive players byte uuid into string
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
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

	// Converting log players byte uuid into string
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
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	    } catch (SQLException e) {
	    }
	    convertLog = false;
	}

	if (convertLog) {
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
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

	    rs11.close();
	    pst111.close();
	    if (insert11 != null)
		insert11.close();

	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	    try {
		executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");
	    } catch (Exception e) {
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
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "explore");
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
		    + "explore` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `worldname` varchar(64), `chunkX` int, `chunkZ` int, `playerName` varchar(32));");
	} catch (Exception e) {
	} finally {
	}
    }

    @Override
    protected synchronized void checkUpdate9() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to SQLite!");
	    return;
	}
	PreparedStatement prestTemp = null;
	ResultSet res1 = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prestTemp = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prestTemp.setString(1, getPrefix() + "users");
	    res1 = prestTemp.executeQuery();
	    if (res1.next()) {
		rows = res1.getInt(1);
	    }
	} finally {
	    close(res1);
	    close(prestTemp);
	}

	// Create new points table
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `totalpoints` double, `currentpoints` double);");
	} catch (Exception e) {
	}

	// checking log table, recreating if old version present
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
	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	if (rows != 0)
	    return;

	HashMap<String, String> tempMap = new HashMap<String, String>();
	PreparedStatement prestJobs = null;
	ResultSet res2 = null;
	try {
	    prestJobs = conn.prepareStatement("SELECT * FROM " + getPrefix() + "jobs;");
	    res2 = prestJobs.executeQuery();
	    while (res2.next()) {
		tempMap.put(res2.getString("player_uuid"), res2.getString("username"));
	    }
	} finally {
	    close(res2);
	    close(prestJobs);
	}

	PreparedStatement prestArchive = null;
	ResultSet res3 = null;
	try {
	    prestArchive = conn.prepareStatement("SELECT * FROM " + getPrefix() + "archive;");
	    res3 = prestArchive.executeQuery();
	    while (res3.next()) {
		tempMap.put(res3.getString("player_uuid"), res3.getString("username"));
	    }
	} finally {
	    close(res3);
	    close(prestArchive);
	}

	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20));");
	} catch (Exception e) {
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
	} finally {
	    close(prestUsers);
	}

	HashMap<String, PlayerInfo> tempPlayerMap = new HashMap<String, PlayerInfo>();

	PreparedStatement prestUsers2 = null;
	ResultSet res4 = null;
	try {
	    prestUsers2 = conn.prepareStatement("SELECT * FROM " + getPrefix() + "users;");
	    res4 = prestUsers2.executeQuery();
	    while (res4.next()) {
		tempPlayerMap.put(res4.getString("player_uuid"), new PlayerInfo(res4.getString("username"), res4.getInt("id"), UUID.fromString(res4.getString("player_uuid")), System.currentTimeMillis()));
	    }
	} finally {
	    close(res4);
	    close(prestUsers2);
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
	} finally {
	    close(prestJobsT);
	}

	// dropping 2 columns
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "jobs_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (Exception e) {
	}

	PreparedStatement pst111 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "jobs`;");
	ResultSet rs11 = pst111.executeQuery();
	PreparedStatement insert11 = null;
	while (rs11.next()) {
	    String uuid = UUIDUtil.fromBytes(rs11.getBytes("player_uuid")).toString();
	    if (uuid != null) {
		insert11 = conn.prepareStatement("INSERT INTO `" + getPrefix() + "jobs_temp` (`userid`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?);");
		insert11.setInt(1, rs11.getInt("userid"));
		insert11.setString(2, rs11.getString("job"));
		insert11.setInt(3, rs11.getInt("experience"));
		insert11.setInt(4, rs11.getInt("level"));
		insert11.execute();
	    }
	}
	rs11.close();
	pst111.close();
	if (insert11 != null)
	    insert11.close();
	try {
	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "jobs`;");
	    executeSQL("ALTER TABLE `" + getPrefix() + "jobs_temp` RENAME TO `" + getPrefix() + "jobs`;");
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
	} finally {
	    close(prestArchiveT);
	}

	// dropping 2 columns
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "archive_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (Exception e) {
	}

	PreparedStatement pstArchive = null;
	ResultSet rsArchive = null;
	PreparedStatement insertArchive = null;
	try {
	    pstArchive = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	    rsArchive = pstArchive.executeQuery();
	    while (rsArchive.next()) {
		String uuid = UUIDUtil.fromBytes(rsArchive.getBytes("player_uuid")).toString();
		if (uuid != null) {
		    insertArchive = conn.prepareStatement("INSERT INTO `" + getPrefix() + "archive_temp` (`userid`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?);");
		    insertArchive.setInt(1, rsArchive.getInt("userid"));
		    insertArchive.setString(2, rsArchive.getString("job"));
		    insertArchive.setInt(3, rsArchive.getInt("experience"));
		    insertArchive.setInt(4, rsArchive.getInt("level"));
		    insertArchive.execute();
		}
	    }
	} catch (Exception e) {
	} finally {
	    if (rsArchive != null)
		rsArchive.close();
	    if (insertArchive != null)
		insertArchive.close();
	    if (pstArchive != null)
		pstArchive.close();
	}

	executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "archive`;");
	try {
	    executeSQL("ALTER TABLE `" + getPrefix() + "archive_temp` RENAME TO `" + getPrefix() + "archive`;");
	} catch (Exception e) {
	}
	// Modifying jobs log table
	PreparedStatement prestPreLog = null;
	try {
	    executeSQL("ALTER TABLE `" + getPrefix() + "log` ADD COLUMN `userid` int;");
	    prestPreLog = conn.prepareStatement("UPDATE `" + getPrefix() + "log` SET `userid` = ? WHERE `player_uuid` = ?;");
	    conn.setAutoCommit(false);
	    for (Entry<String, PlayerInfo> users : tempPlayerMap.entrySet()) {
		prestPreLog.setInt(1, users.getValue().getID());
		prestPreLog.setString(2, users.getKey());
		prestPreLog.addBatch();
	    }
	    prestPreLog.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);
	} finally {
	    close(prestPreLog);
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
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to SQLITE!");
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
		+ "explore` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `worldname` varchar(64), `chunkX` int, `chunkZ` int, `playerName` varchar(32));");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultPointsBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `totalpoints` double, `currentpoints` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultUsersBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `seen` bigint);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    @Override
    protected boolean createDefaultLogBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    @Override
    protected boolean createDefaultArchiveBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "archive` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultJobsBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "jobs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultBlockProtection() {
	try {
	    executeSQL("CREATE TABLE IF NOT EXISTS `" + getPrefix()
		+ "blocks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `world` varchar(36) NOT NULL, `x` int, `y` int, `z` int, `recorded` bigint, `resets` bigint);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultLimitBase() {
	try {
	    executeSQL("CREATE TABLE IF NOT EXISTS `" + getPrefix()
		+ "limits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `type` varchar(36), `collected` double, `started` bigint);");
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
