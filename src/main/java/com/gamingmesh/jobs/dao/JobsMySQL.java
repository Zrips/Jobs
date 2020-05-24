package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

public class JobsMySQL extends JobsDAO {

    JobsMySQL(Jobs plugin, String hostname, String database, String username, String password, String prefix, boolean certificate, boolean ssl, boolean autoReconnect) {
	super(plugin, "com.mysql.jdbc.Driver", "jdbc:mysql://" + hostname + "/" + database
	    + "?maxReconnects=1&useUnicode=true&character_set_server=utf8mb4&autoReconnect=" + autoReconnect + "&useSSL=" + ssl
	    + "&verifyServerCertificate=" + certificate, username, password, prefix);
	this.setDbType(DataBaseType.MySQL);
    }

    public void initialize() {
	try {
	    setUp();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public JobsMySQL initialize(Jobs plugin, String hostname, String database, String username, String password, String prefix, boolean certificate, boolean ssl, boolean autoReconnect) {
	JobsMySQL dao = new JobsMySQL(plugin, hostname, database, username, password, prefix, certificate, ssl, autoReconnect);
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
	    Jobs.consoleMsg("&cCould not run database updates! Could not connect to MySQL!");
	    return;
	}

	String name = getPrefix() + "config";
	if (isTable(name)) {
	    drop(name);
	}
    }

    @Override
    protected synchronized void checkUpdate() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.consoleMsg("&cCould not run database updates! Could not connect to MySQL!");
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

    @Override
    public boolean createTable(String query) {
	Statement statement = null;
	if (query == null || query.isEmpty()) {
	    Jobs.consoleMsg("&cCould not create table: query is empty or null.");
	    return false;
	}
	JobsConnection conn = getConnection();
	if (conn == null)
	    return false;
	try {
	    statement = conn.createStatement();
	    statement.execute(query);
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not create table, SQLException: " + e.getMessage());
	    return false;
	} finally {
	    close(statement);
	}
	return true;
    }

    @Override
    public boolean isTable(String table) {
	Statement statement;
	JobsConnection conn = getConnection();
	if (conn == null)
	    return false;
	try {
	    statement = conn.createStatement();
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not check if its table, SQLException: " + e.getMessage());
	    return false;
	}
	try {
	    ResultSet tables = conn.getMetaData().getTables(null, null, table, null);
	    if (tables.next()) {
		tables.close();
		return true;
	    }
	    tables.close();
	    return false;
	} catch (SQLException e) {
	    Jobs.consoleMsg("Not a table |" + "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + table + "';" + "|");
	}
	try {

	    PreparedStatement insert = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + table + "';");
	    ResultSet res = insert.executeQuery();
	    if (res.next()) {
		res.close();
		insert.close();
		return true;
	    }
	    res.close();
	    insert.close();
	    return false;
	} catch (SQLException e) {
	    Jobs.consoleMsg("Not a table |" + "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + table + "';" + "|");
	    return false;
	} finally {
	    close(statement);
	}
    }

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
	    return true;
	} catch (SQLException e) {
	    Jobs.consoleMsg("Not a culumn |" + "SELECT " + collumn + " FROM " + table + "|");
	    return false;
	} finally {
	    close(statement);
	}
    }

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
	    return true;
	} catch (SQLException e) {
	    return false;
	} finally {
	    close(statement);
	}
    }

    @Override
    public boolean truncate(String table) {
	Statement statement = null;
	try {
	    if (!this.isTable(table)) {
		Jobs.consoleMsg("&cTable \"" + table + "\" does not exist.");
		return false;
	    }
	    statement = getConnection().createStatement();
	    String query = "DELETE FROM " + table + ";";
	    statement.executeUpdate(query);
	    return true;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not wipe table, SQLException: " + e.getMessage());
	    e.printStackTrace();
	    return false;
	} finally {
	    close(statement);
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
	    statement.executeUpdate(query);
	    return true;
	} catch (SQLException e) {
	    Jobs.consoleMsg("&cCould not wipe table, SQLException: " + e.getMessage());
	    e.printStackTrace();
	    return false;
	} finally {
	    close(statement);
	}
    }
}
