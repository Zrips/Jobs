package com.gamingmesh.jobs.dao;

import java.io.IOException;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.LoadStatus;

import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Logs.CMIDebug;

public class JobsManager {
    private JobsDAO dao;
    private Jobs plugin;
    private DataBaseType dbType = DataBaseType.SqLite;

    public enum DataBaseType {
	MySQL, SqLite
    }

    public JobsManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public JobsDAO getDB() {
	return dao;
    }

    public void switchDataBase() {
	if (dao != null)
	    dao.closeConnections();

	// Picking opposite database then it is currently
	switch (dbType) {
	case MySQL:
	    // If it MySQL lets change to SqLite
	    dbType = DataBaseType.SqLite;
	    dao = startSqlite();

	    if (dao != null)
		dao.setDbType(dbType);
	    break;
	case SqLite:
	    // If it SqLite lets change to MySQL
	    dbType = DataBaseType.MySQL;
	    dao = startMysql();

	    if (dao != null)
		dao.setDbType(dbType);
	    break;
	default:
	    break;
	}

	ConfigReader config = Jobs.getGCManager().getConfig();

	config.set("storage.method", dbType.toString().toLowerCase());
	try {
	    config.save(config.getFile());
	} catch (IOException e) {
	    e.printStackTrace();
	}

	Jobs.setDAO(dao);
    }

    private String username = "root", password = "", hostname = "localhost:3306", database = "minecraft", prefix = "jobs_",
	characterEncoding = "utf8", encoding = "UTF-8";
    private boolean certificate = false, ssl = false, autoReconnect = false;

    public void start() {

	if (Jobs.getJobsDAO() != null) {
	    Jobs.consoleMsg("&eClosing existing database connection...");
	    Jobs.getJobsDAO().closeConnections();
	    Jobs.consoleMsg("&eClosed");
	}

	ConfigReader c = Jobs.getGCManager().getConfig();

	c.addComment("storage.method", "storage method, can be MySQL or sqlite");
	String storageMethod = c.get("storage.method", "sqlite");
	c.addComment("mysql", "Requires Mysql");

	username = c.get("mysql.username", c.getC().getString("mysql-username", "root"));
	password = c.get("mysql.password", c.getC().getString("mysql-password", ""));
	hostname = c.get("mysql.hostname", c.getC().getString("mysql-hostname", "localhost:3306"));
	database = c.get("mysql.database", c.getC().getString("mysql-database", "minecraft"));
	prefix = c.get("mysql.table-prefix", c.getC().getString("mysql-table-prefix", "jobs_"));
	certificate = c.get("mysql.verify-server-certificate", c.getC().getBoolean("verify-server-certificate"));
	ssl = c.get("mysql.use-ssl", c.getC().getBoolean("use-ssl"));
	autoReconnect = c.get("mysql.auto-reconnect", c.getC().getBoolean("auto-reconnect", true));
	characterEncoding = c.get("mysql.characterEncoding", "utf8");
	encoding = c.get("mysql.encoding", "UTF-8");

	if (storageMethod.equalsIgnoreCase("mysql")) {
	    dbType = DataBaseType.MySQL;
	    dao = startMysql();
	    if (dao == null || dao.getConnection() == null) {
		Jobs.status = LoadStatus.MYSQLFailure;
	    }
	} else {
	    if (!storageMethod.equalsIgnoreCase("sqlite")) {
		Jobs.consoleMsg("&cInvalid storage method! Changing method to sqlite!");
		c.set("storage.method", "sqlite");
	    }

	    dbType = DataBaseType.SqLite;
	    dao = startSqlite();

	    if (dao.getConnection() == null) {
		Jobs.status = LoadStatus.SQLITEFailure;
	    }
	}

	Jobs.setDAO(dao);
    }

    private synchronized JobsMySQL startMysql() {
	ConfigReader c = Jobs.getGCManager().getConfig();
	String legacyUrl = c.getC().getString("mysql.url");

	if (legacyUrl != null) {
	    String jdbcString = "jdbc:mysql://";

	    if (legacyUrl.toLowerCase().startsWith(jdbcString)) {
		legacyUrl = legacyUrl.substring(jdbcString.length());

		String[] parts = legacyUrl.split("/", 2);

		if (parts.length >= 2) {
		    hostname = c.get("mysql.hostname", parts[0]);
		    database = c.get("mysql.database", parts[1]);
		}
	    }
	}

	if (username == null) {
	    username = "root";
	}

	if (plugin.isEnabled()) {
	    JobsMySQL data = new JobsMySQL(plugin, hostname, database, username, password, prefix, certificate, ssl, autoReconnect,
		characterEncoding, encoding);
	    data.initialize();
	    return data;
	}

	return null;
    }

    private synchronized JobsSQLite startSqlite() {
	JobsSQLite data = new JobsSQLite(plugin, Jobs.getFolder());
	data.initialize();
	return data;
    }

    public DataBaseType getDbType() {
	return dbType;
    }

}
