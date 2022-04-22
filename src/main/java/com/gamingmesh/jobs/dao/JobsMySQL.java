package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

public class JobsMySQL extends JobsDAO {

    private static String path = "";

    static {
	try {
	    Class.forName("com.mysql.cj.jdbc.Driver");
	    path = "com.mysql.cj.jdbc.Driver";
	} catch (Throwable e) {
	    path = "com.mysql.jdbc.Driver";
	}
    }

    private String database;

    JobsMySQL(Jobs plugin, String hostname, String database, String username, String password, String prefix, boolean certificate, boolean ssl, boolean autoReconnect,
	String characterEncoding, String encoding) {
	super(plugin, path, "jdbc:mysql://" + hostname + "/" + database
	    + "?maxReconnects=1&characterEncoding=" + characterEncoding + "&encoding="
	    + encoding + "&useUnicode=true&autoReconnect=" + autoReconnect + "&useSSL=" + ssl
	    + "&verifyServerCertificate=" + certificate, username, password, prefix);
	setDbType(DataBaseType.MySQL);
    }

    public void initialize() {
	setUp();
    }

    public JobsMySQL initialize(Jobs plugin, String hostname, String database, String username, String password, String prefix, boolean certificate, boolean ssl, boolean autoReconnect,
	String characterEncoding, String encoding) {
	JobsMySQL dao = new JobsMySQL(plugin, hostname, database, username, password, prefix, certificate, ssl, autoReconnect,
	    characterEncoding, encoding);
	this.database = database;
	dao.setUp();
	return dao;
    }

    @Override
    protected void checkUpdate() throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null) {
	    Jobs.consoleMsg("&cCould not run database updates! Could not connect to MySQL!");
	    return;
	}

	executeSQL("CREATE TABLE `" + getPrefix()
	    + "users` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `seen` bigint);");
    }

    @Override
    public Statement prepareStatement(String query) throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return null;

	try {
	    return conn.prepareStatement(query);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public boolean createTable(String query) {
	if (query == null || query.isEmpty()) {
	    Jobs.consoleMsg("&cCould not create table: query is empty or null.");
	    return false;
	}
	JobsConnection conn = getConnection();
	if (conn == null)
	    return false;
	Statement statement = null;
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
	JobsConnection conn = getConnection();
	if (conn == null)
	    return false;

	try {
	    ResultSet tables = conn.getMetaData().getTables(database, null, table, new String[] {"TABLE"});
	    if (tables.next()) {
		tables.close();
		return true;
	    }
	    tables.close();
	    return false;
	} catch (SQLException e) {
	    Jobs.consoleMsg("Not a table |" + "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + table + "';" + "|");
	}

	PreparedStatement insert = null;
	ResultSet res = null;
	try {
	    insert = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + table + "';");
	    res = insert.executeQuery();
	    if (res.next()) {
		close(res);
		close(insert);
		return true;
	    }
	} catch (SQLException e) {
	    Jobs.consoleMsg("Not a table |" + "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + table + "';" + "|");
	} finally {
	    close(res);
	    close(insert);
	}

	return false;
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
	    Jobs.consoleMsg("Not a collumn |" + "SELECT " + collumn + " FROM " + table + "|");
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
	    Jobs.consoleMsg("Creating collumn |" + "ALTER TABLE `" + table + "` ADD COLUMN `" + collumn + "` " + type + ";" + "|");
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
	    if (!isTable(table)) {
		Jobs.consoleMsg("&cTable \"" + table + "\" does not exist.");
		return false;
	    }
	    statement = getConnection().createStatement();
	    statement.executeUpdate("DELETE FROM " + table + ";");
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
	try {
	    if (!isTable(table)) {
		Jobs.consoleMsg("&cTable \"" + table + "\" does not exist.");
		return false;
	    }
	    statement = getConnection().createStatement();
	    statement.executeUpdate("DROP TABLE IF EXISTS `" + table + "`;");
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
