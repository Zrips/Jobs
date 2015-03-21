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

import com.gamingmesh.jobs.Jobs;

public class JobsDAOH2 extends JobsDAO {
	private JobsDAOH2() {
		super("org.h2.Driver", "jdbc:h2:" + new File(Jobs.getDataFolder(), "jobs").getPath(), "sa", "sa", "");
	}

	public static void convertToSQLite() throws SQLException {
		Jobs.getPluginLogger().info("Converting H2 to SQLite.  This could take a long time!");

		JobsDAOH2 h2dao = new JobsDAOH2();
		JobsConnection h2Conn = h2dao.getConnection();

		JobsDAOH2SQLiteImporter sqliteDao = JobsDAOH2SQLiteImporter.initialize();
		JobsConnection sqliteConn = sqliteDao.getConnection();

		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;

		try {
			sqliteDao.executeSQL("CREATE TABLE `" + sqliteDao.getPrefix() + "jobs` (`username` varchar(20), `job` varchar(20), `experience` int, `level` int);");
			pst1 = h2Conn.prepareStatement("SELECT `username`, `job`, `experience`, `level` FROM `" + h2dao.getPrefix() + "jobs`");
			pst2 = sqliteConn.prepareStatement("INSERT INTO `" + sqliteDao.getPrefix() + "jobs` (`username`, `job`, `experience`, `level`) VALUES (?, ?, ?, ?);");
			ResultSet rs = pst1.executeQuery();
			while (rs.next()) {
				pst2.setString(1, rs.getString(1));
				pst2.setString(2, rs.getString(2));
				pst2.setInt(3, rs.getInt(3));
				pst2.setInt(4, rs.getInt(4));
				pst2.execute();
			}
			Jobs.getPluginLogger().info("Conversion from H2 to SQLite complete!");
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
			h2Conn.closeConnection();
			sqliteConn.closeConnection();
		}
	}

	@Override
	protected synchronized void setupConfig() throws SQLException {
	}

	@Override
	protected void checkUpdate1() throws SQLException {
	}

	public static class JobsDAOH2SQLiteImporter extends JobsDAO {
		public static JobsDAOH2SQLiteImporter initialize() {
			JobsDAOH2SQLiteImporter dao = new JobsDAOH2SQLiteImporter();
			File dir = Jobs.getDataFolder();
			if (!dir.exists())
				dir.mkdirs();
			return dao;
		}

		private JobsDAOH2SQLiteImporter() {
			super("org.sqlite.JDBC", "jdbc:sqlite:" + new File(Jobs.getDataFolder(), "jobs.sqlite.db").getPath(), null, null, "");
		}

		@Override
		protected void setupConfig() throws SQLException {
		}

		@Override
		protected void checkUpdate1() throws SQLException {
		}

		@Override
		protected void checkUpdate2() throws SQLException {
		}

		@Override
		protected void checkUpdate4() throws SQLException {
		}
	}

	@Override
	protected void checkUpdate2() throws SQLException {
	}
	
	@Override
	protected void checkUpdate4() throws SQLException {
	}
}
