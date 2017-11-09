package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

public class JobsMySQL extends JobsDAO {
    private String database;
    private Jobs plugin;

    JobsMySQL(Jobs plugin, String hostname, String database, String username, String password, String prefix, boolean certificate, boolean ssl) {
	super(plugin, "com.mysql.jdbc.Driver", "jdbc:mysql://" + hostname + "/" + database + "?autoReconnect=true&useSSL=" + ssl + "&verifyServerCertificate=" + certificate, username, password, prefix);
	this.plugin = plugin;
	this.database = database;
	this.setDbType(DataBaseType.MySQL);
    }

    public void initialize() {
	try {
	    setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public JobsMySQL initialize(Jobs plugin, String hostname, String database, String username, String password, String prefix, boolean certificate, boolean ssl) {
	this.plugin = plugin;
	JobsMySQL dao = new JobsMySQL(plugin, hostname, database, username, password, prefix, certificate, ssl);
	try {
	    dao.setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return dao;
    }

    @Override
    protected synchronized void setupConfig() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.consoleMsg("&cCould not run database updates!  Could not connect to MySQL!");
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
		+ "users` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `seen` bigint);");
	} catch (SQLException e) {
	    e.printStackTrace();
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
	Jobs.consoleMsg(query);
	Statement statement = null;
	if (query == null || query.equals("")) {
	    Jobs.consoleMsg("&cCould not create table: query is empty or null.");
	    return false;
	}

	try {
	    statement = getConnection().createStatement();
	    statement.execute(query);
	    statement.close();
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not create table, SQLException: " + e.getMessage());
	    close(statement);
	    return false;
	} finally {
	    close(statement);
	}
	return true;
    }

    @SuppressWarnings("resource")
    @Override
    public boolean isTable(String table) {
	Statement statement;
	try {
	    statement = getConnection().createStatement();
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not check if its table, SQLException: " + e.getMessage());
	    return false;
	}
	try {
	    statement.executeQuery("SELECT * FROM `" + table+"`;");
	    statement.close();
	    return true;
	} catch (SQLException e) {
//	    Jobs.consoleMsg("Not a table |" + "SELECT * FROM " + table + "|");
	    close(statement);
	    return false;
	}
    }

    @SuppressWarnings("resource")
    @Override
    public boolean isCollumn(String table, String collumn) {
	Statement statement;
	try {
	    statement = getConnection().createStatement();
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not check if its collumn, SQLException: " + e.getMessage());
	    return false;
	}
	try {
	    statement.executeQuery("SELECT `" + collumn + "` FROM `" + table + "`;");
	    statement.close();
	    return true;
	} catch (SQLException e) {
	    e.printStackTrace();
	    Jobs.consoleMsg("Not a culumn |" + "SELECT " + collumn + " FROM " + table + "|");
	    close(statement);
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
	    Jobs.consoleMsg("Creating culumn |" + "ALTER TABLE `" + table + "` ADD COLUMN `" + collumn + "` " + type + ";" + "|");
	    statement.executeUpdate("ALTER TABLE `" + table + "` ADD COLUMN `" + collumn + "` " + type + ";");
	    statement.close();
	    return true;
	} catch (SQLException e) {
	    close(statement);
//	    e.printStackTrace();
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
	    query = "DELETE FROM " + table + ";";
	    statement.executeUpdate(query);
	    statement.close();

	    return true;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not wipe table, SQLException: " + e.getMessage());
	    close(statement);
	    e.printStackTrace();
	    return false;
	}
    }

    @SuppressWarnings("resource")
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
	    statement.executeUpdate(query);
	    statement.close();

	    return true;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not wipe table, SQLException: " + e.getMessage());
	    close(statement);
	    e.printStackTrace();
	    return false;
	}
    }
}
