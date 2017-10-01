package com.gamingmesh.jobs.dao;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

public class JobsSQLite extends JobsDAO {
    private Jobs plugin;

    public void initialize() {
	try {
	    this.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public JobsSQLite initialize(Jobs plugin, File dir) {
	this.plugin = plugin;
	if (!dir.exists())
	    dir.mkdirs();
	try {
	    this.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return this;
    }

    JobsSQLite(Jobs plugin, File file) {
	super(plugin, "org.sqlite.JDBC", "jdbc:sqlite:" + new File(file, "jobs.sqlite.db").getPath(), null, null, "");
	if (!file.exists())
	    file.mkdirs();
	this.setDbType(DataBaseType.SqLite);
    }

    @Override
    protected synchronized void setupConfig() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.consoleMsg("&cCould not run database updates!  Could not connect to MySQL!");
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
		executeSQL("CREATE TABLE `" + getPrefix() + "config` (`key` varchar(50) NOT NULL PRIMARY KEY, `value` int NOT NULL);");
		insert = conn.prepareStatement("INSERT INTO `" + getPrefix() + "config` (`key`, `value`) VALUES (?, ?);");
		insert.setString(1, "version");
		insert.setInt(2, 1);
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
	    Jobs.consoleMsg("&cCould not run database updates!  Could not connect to MySQL!");
	    return;
	}
	createDefaultUsersBase();
    }

    private boolean createDefaultUsersBase() {
	try {
	    executeSQL("CREATE TABLE `" + getPrefix()
		+ "users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `data` text);");
	} catch (SQLException e) {
	    return false;
	}
	return true;
    }

    @Override
    public Statement prepareStatement(String query) throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return null;
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement(query);
	} catch (SQLException | NumberFormatException e) {
	    e.printStackTrace();
	}
	return prest;
    }

    @SuppressWarnings("resource")
    @Override
    public boolean createTable(String query) {
	Statement statement = null;
	try {
	    if (query == null || query.equals("")) {
		Jobs.consoleMsg("&cCould not create table: query is empty or null.");
		return false;
	    }

	    statement = getConnection().createStatement();
	    statement.execute(query);
	    statement.close();
	    return true;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not create table, SQLException: " + e.getMessage());
	    close(statement);
	    return false;
	}
    }

    @Override
    public boolean isTable(String table) {
	DatabaseMetaData md = null;
	try {
	    md = this.getConnection().getMetaData();
	    ResultSet tables = md.getTables(null, null, table, null);
	    if (tables.next()) {
		tables.close();
		return true;
	    }
	    tables.close();
	    return false;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not check if table \"" + table + "\" exists, SQLException: " + e.getMessage());
	    return false;
	}
    }

    @Override
    public boolean isCollumn(String table, String collumn) {
	DatabaseMetaData md = null;
	try {
	    md = this.getConnection().getMetaData();
	    ResultSet tables = md.getColumns(null, null, table, collumn);
	    if (tables.next()) {
		tables.close();
		return true;
	    }
	    tables.close();
	    return false;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not check if table \"" + table + "\" exists, SQLException: " + e.getMessage());
	    return false;
	}
    }

    @SuppressWarnings("resource")
    @Override
    public boolean addCollumn(String table, String collumn, String type) {
	Statement statement;
	try {
	    statement = getConnection().createStatement();
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not add new collumn, SQLException: " + e.getMessage());
	    return false;
	}
	try {
	    statement.executeQuery("ALTER TABLE `" + table + "` ADD `" + collumn + "` " + type);
	    statement.close();
	    return true;
	} catch (SQLException e) {
	    close(statement);
	    return false;
	}
    }

    @SuppressWarnings("resource")
    @Override
    public boolean truncate(String table) {
	Statement statement = null;
	String query = null;
	try {
	    if (!this.isTable(table)) {
		Jobs.consoleMsg("&cTable \"" + table + "\" does not exist.");
		return false;
	    }
	    statement = getConnection().createStatement();
	    query = "DELETE FROM `" + table + "`;";
	    statement.executeQuery(query);
	    statement.close();
	    return true;
	} catch (SQLException e) {
	    if (!(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) &&
		!e.toString().contains("not return ResultSet"))
		Jobs.consoleMsg("&cError in wipeTable() query: " + e);
	    close(statement);
	    return false;
	}
    }

    @Override
    public boolean drop(String table) {
	Statement statement = null;
	String query = null;
	try {
	    if (!this.isTable(table)) {
		Jobs.consoleMsg("&cTable \"" + table + "\" does not exist.");
		return false;
	    }
	    statement = getConnection().createStatement();
	    query = "DROP TABLE IF EXISTS `" + table + "`;";
	    statement.executeQuery(query);
	    statement.close();
	    return true;
	} catch (SQLException e) {
	    if (!(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) &&
		!e.toString().contains("not return ResultSet"))
		Jobs.consoleMsg("&cError in dropTable() query: " + e);
	    close(statement);
	    return false;
	} finally {
	    close(statement);
	}
    }
}
