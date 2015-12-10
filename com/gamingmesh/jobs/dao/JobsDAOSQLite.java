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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.stuff.ChatColor;
import com.gamingmesh.jobs.stuff.OfflinePlayerList;
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

		    OfflinePlayer offPlayer = OfflinePlayerList.getPlayer(names);
		    if (offPlayer == null)
			continue;

		    pst2.setBytes(1, UUIDUtil.toBytes(offPlayer.getUniqueId()));
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

	executeSQL("CREATE TABLE `" + getPrefix()
	    + "log_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` binary(16) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");

	executeSQL("INSERT INTO `" + getPrefix() + "log_temp` SELECT `id`, `player_uuid`, `username`, `time`, `action`, `itemname`, `count`, `money`, `exp` FROM `"
	    + getPrefix() + "log`;");

	executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");

    }

    @Override
    protected synchronized void checkUpdate7() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.getPluginLogger().severe("Could not run database updates!  Could not connect to MySQL!");
	    return;
	}

	Jobs.getPluginLogger().info("Converting byte uuids to string.  This could take a long time!!!");

	executeSQL("CREATE TABLE `" + getPrefix()
	    + "jobs_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL,`username` varchar(20), `job` varchar(20), `experience` int, `level` int);");

	PreparedStatement pst1 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "jobs`;");
	ResultSet rs = pst1.executeQuery();
	PreparedStatement insert = null;
	while (rs.next()) {

	    String uuid = UUIDUtil.fromBytes(rs.getBytes("player_uuid")).toString();

	    if (uuid != null) {
		insert = conn.prepareStatement("INSERT INTO `" + getPrefix()
		    + "jobs_temp` (`player_uuid`, `username`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?, ?);");
		insert.setString(1, uuid);
		insert.setString(2, rs.getString("username"));
		insert.setString(3, rs.getString("job"));
		insert.setInt(4, rs.getInt("experience"));
		insert.setInt(5, rs.getInt("level"));
		insert.execute();
	    }
	}
	rs.close();
	if (insert != null)
	    insert.close();

	executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "jobs`;");
	executeSQL("ALTER TABLE `" + getPrefix() + "jobs_temp` RENAME TO `" + getPrefix() + "jobs`;");

	// Converting archive players byte uuid into string
	executeSQL("CREATE TABLE `" + getPrefix()
	    + "archive_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `job` varchar(20), `experience` int, `level` int);");

	PreparedStatement pst11 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "archive`;");
	ResultSet rs1 = pst11.executeQuery();
	PreparedStatement insert1 = null;
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
		insert1.execute();
	    }
	}
	rs1.close();
	if (insert1 != null)
	    insert1.close();

	executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "archive`;");
	executeSQL("ALTER TABLE `" + getPrefix() + "archive_temp` RENAME TO `" + getPrefix() + "archive`;");

	// Converting log players byte uuid into string
	executeSQL("CREATE TABLE `" + getPrefix()
	    + "log_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `time` bigint, `action` varchar(20), `itemname` varchar(60), `count` int, `money` double, `exp` double);");

	PreparedStatement pst111 = conn.prepareStatement("SELECT * FROM `" + getPrefix() + "log`;");
	ResultSet rs11 = pst111.executeQuery();
	PreparedStatement insert11 = null;
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
		insert11.execute();
	    }
	}
	rs11.close();
	if (insert11 != null)
	    insert11.close();

	executeSQL("DROP TABLE IF EXISTS `" + getPrefix() + "log`;");
	executeSQL("ALTER TABLE `" + getPrefix() + "log_temp` RENAME TO `" + getPrefix() + "log`;");
    }

}
