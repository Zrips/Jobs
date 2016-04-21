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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.UUIDUtil;

public class JobsDAOSQLite extends JobsDAO {
    public static JobsDAOSQLite initialize() {
	JobsDAOSQLite dao = new JobsDAOSQLite();
	File dir = Jobs.getDataFolder();
	if (!dir.exists())
	    dir.mkdirs();
	try {
	    dao.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return dao;
    }

    private JobsDAOSQLite() {
	super("org.sqlite.JDBC", "jdbc:sqlite:" + new File(Jobs.getDataFolder(), "jobs.sqlite.db").getPath(), null, null, "");
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
	try {
	    // Check for config table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "config");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
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
		if (insert != null) {
		    try {
			insert.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	}
    }

    @Override
    protected synchronized void checkUpdate1() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "jobs");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	PreparedStatement pst1 = null;
	PreparedStatement pst2 = null;
	try {
	    if (rows > 0) {
		Jobs.getPluginLogger().info("Converting existing usernames to Mojang UUIDs.  This could take a long time!!!");
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` RENAME TO `" + getPrefix() + "jobs_old`;");
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs_old` ADD COLUMN `player_uuid` binary(16) DEFAULT NULL;");
	    }

	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "jobs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` binary(16) NOT NULL, `job` varchar(20), `experience` int, `level` int);");

	    if (rows > 0) {
		pst1 = conn.prepareStatement("SELECT DISTINCT `username` FROM `" + getPrefix() + "jobs_old` WHERE `player_uuid` IS NULL;");
		ResultSet rs = pst1.executeQuery();
		ArrayList<String> usernames = new ArrayList<String>();
		while (rs.next()) {
		    usernames.add(rs.getString(1));
		}
		pst2 = conn.prepareStatement("UPDATE `" + getPrefix() + "jobs_old` SET `player_uuid` = ? WHERE `username` = ?;");
		int i = 0;
		int y = 0;
		for (String names : usernames) {
		    i++;
		    y++;
		    if (i >= 50) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "" + y + " of " + usernames.size());
			i = 0;
		    }

		    Entry<String, PlayerInfo> info = Jobs.getPlayerManager().getPlayerInfoByName(names);
		    if (info == null)
			continue;

		    pst2.setBytes(1, UUIDUtil.toBytes(UUID.fromString(info.getKey())));
		    pst2.setString(2, names);
		    pst2.execute();
		}
		executeSQL("INSERT INTO `" + getPrefix() + "jobs` (`player_uuid`, `job`, `experience`, `level`) SELECT `player_uuid`, `job`, `experience`, `level` FROM `"
		    + getPrefix() + "jobs_old`;");
	    }
	} finally {
	    if (pst1 != null) {
		try {
		    pst1.close();
		} catch (SQLException e) {
		}
	    }
	    if (pst2 != null) {
		try {
		    pst2.close();
		} catch (SQLException e) {
		}
	    }
	}

	if (rows > 0) {
	    executeSQL("DROP TABLE `" + getPrefix() + "jobs_old`;");

	    Jobs.getPluginLogger().info("Mojang UUID conversion complete!");
	}
	checkUpdate2();
    }

    @Override
    protected synchronized void checkUpdate2() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "jobs");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
	}

	try {
	    if (rows > 0) {
		executeSQL("ALTER TABLE `" + getPrefix() + "jobs` ADD COLUMN `username` varchar(20);");
	    }

	} finally {
	}
	checkUpdate4();
    }

    @Override
    protected synchronized void checkUpdate4() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}
	PreparedStatement prest = null;
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "archive");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
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
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "log");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
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
	    createDefaultJobsBase();
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
	    createDefaultArchiveBase();
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
	    createDefaultLogBase();
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
	int rows = 0;
	try {
	    // Check for jobs table
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prest.setString(1, getPrefix() + "explore");
	    ResultSet res = prest.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prest != null) {
		try {
		    prest.close();
		} catch (SQLException e) {
		}
	    }
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
	int rows = 0;
	try {
	    // Check for jobs table
	    prestTemp = conn.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE name = ?;");
	    prestTemp.setString(1, getPrefix() + "users");
	    ResultSet res = prestTemp.executeQuery();
	    if (res.next()) {
		rows = res.getInt(1);
	    }
	} finally {
	    if (prestTemp != null) {
		try {
		    prestTemp.close();
		} catch (SQLException e) {
		}
	    }
	}

	if (rows == 0) {
	    HashMap<String, String> tempMap = new HashMap<String, String>();
	    PreparedStatement prestJobs = null;
	    try {
		prestJobs = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "jobs;");
		ResultSet res = prestJobs.executeQuery();
		while (res.next()) {
		    tempMap.put(res.getString("player_uuid"), res.getString("username"));
		}
	    } finally {
		if (prestJobs != null) {
		    try {
			prestJobs.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	    PreparedStatement prestArchive = null;
	    try {
		prestArchive = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "archive;");
		ResultSet res = prestArchive.executeQuery();
		while (res.next()) {
		    tempMap.put(res.getString("player_uuid"), res.getString("username"));
		}
		if (res != null)
		    res.close();
	    } finally {
		if (prestArchive != null) {
		    try {
			prestArchive.close();
		    } catch (SQLException e) {
		    }
		}
	    }
	    PreparedStatement prestLog = null;
	    try {
		prestLog = conn.prepareStatement("SELECT DISTINCT(player_uuid),username FROM " + getPrefix() + "log;");
		ResultSet res = prestLog.executeQuery();
		while (res.next()) {
		    tempMap.put(res.getString("player_uuid"), res.getString("username"));
		}
		if (res != null)
		    res.close();
	    } finally {
		if (prestLog != null) {
		    try {
			prestLog.close();
		    } catch (SQLException e) {
		    }
		}
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
		if (prestUsers != null) {
		    try {
			prestUsers.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    HashMap<String, PlayerInfo> tempPlayerMap = new HashMap<String, PlayerInfo>();

	    PreparedStatement prestUsers2 = null;
	    try {
		prestUsers2 = conn.prepareStatement("SELECT * FROM " + getPrefix() + "users;");
		ResultSet res = prestUsers2.executeQuery();
		while (res.next()) {
		    tempPlayerMap.put(res.getString("player_uuid"), new PlayerInfo(res.getString("username"), res.getInt("id")));
		}
		if (res != null)
		    res.close();
	    } finally {
		if (prestUsers2 != null) {
		    try {
			prestUsers2.close();
		    } catch (SQLException e) {
		    }
		}
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
	    } finally {
		if (prestJobsT != null) {
		    try {
			prestJobsT.close();
		    } catch (SQLException e) {
		    }
		}
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
		if (prestArchiveT != null) {
		    try {
			prestArchiveT.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    // dropping 2 columns
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "archive_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `job` varchar(20), `experience` int, `level` int);");
	    } catch (Exception e) {
	    }

	    PreparedStatement pstArchive = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	    ResultSet rsArchive = pstArchive.executeQuery();
	    PreparedStatement insertArchive = null;
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
	    if (rsArchive != null)
		rsArchive.close();
	    if (insertArchive != null)
		insertArchive.close();
	    if (pstArchive != null)
		pstArchive.close();
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
		if (prestPreLog != null) {
		    try {
			prestPreLog.close();
		    } catch (SQLException e) {
		    }
		}
	    }

	    // dropping 2 columns
	    try {
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "log_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	    } catch (Exception e) {
	    }

	    PreparedStatement prestLogT = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	    ResultSet rsLog = prestLogT.executeQuery();
	    PreparedStatement insertLog = null;
	    while (rsLog.next()) {
		String uuid = UUIDUtil.fromBytes(rsLog.getBytes("player_uuid")).toString();
		if (uuid != null) {
		    insertLog = conn.prepareStatement("INSERT INTO `" + getPrefix()
			+ "log_temp` (`userid`, `time`, `action`, `itemname`, `count`, `money`, `exp`) VALUES (?, ?, ?, ?, ?, ?, ?);");
		    insertLog.setInt(1, rsLog.getInt("userid"));
		    insertLog.setLong(2, rsLog.getLong("time"));
		    insertLog.setString(3, rsLog.getString("action"));
		    insertLog.setString(4, rsLog.getString("itemname"));
		    insertLog.setInt(5, rsLog.getInt("count"));
		    insertLog.setDouble(6, rsLog.getDouble("money"));
		    insertLog.setDouble(7, rsLog.getDouble("exp"));
		    insertLog.execute();
		}
	    }
	    rsLog.close();
	    if (insertLog != null)
		insertLog.close();

	    try {
		executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
		executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");

		// Create new points table
		executeSQL("CREATE TABLE `" + getPrefix()
		    + "points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `totalpoints` double, `currentpoints` double);");

	    } catch (Exception e) {
	    }
	}
    }

    private boolean createDefaultLogBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userid` int, `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    private boolean createDefaultArchiveBase() {
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

    private boolean dropDataBase(String name) {
	try {
	    executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + name + "`;");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }
}
