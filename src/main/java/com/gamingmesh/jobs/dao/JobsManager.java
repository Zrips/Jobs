package com.gamingmesh.jobs.dao;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.LocaleReader;

public class JobsManager {
    private JobsDAO dao;
    private Jobs plugin;
    private DataBaseType DbType = DataBaseType.SqLite;

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
	switch (DbType) {
	case SqLite:
	    DbType = DataBaseType.SqLite;
	    dao = startSqlite();
	    dao.setDbType(DbType);
	    break;
	case MySQL:
	    DbType = DataBaseType.MySQL;
	    dao = startMysql();
	    dao.setDbType(DbType);
	    break;
	}

	File f = new File(Jobs.getFolder(), "generalConfig.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

	config.set("storage.method", DbType.toString());
	try {
	    config.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	Jobs.setDAO(dao);
    }

    public void start(LocaleReader c) {
	c.getW().addComment("storage.method", "storage method, can be MySQL or sqlite");
	String storageMethod = c.get("storage.method", "sqlite");
	c.getW().addComment("mysql-username", "Requires Mysql.");
	c.get("mysql-username", "root");
	c.get("mysql-password", "");
	c.get("mysql-hostname", "localhost:3306");
	c.get("mysql-database", "minecraft");
	c.get("mysql-table-prefix", "jobs_");
	c.get("verify-server-certificate", false);
	c.get("use-ssl", false);
	c.get("auto-reconnect", false);

	if (storageMethod.equalsIgnoreCase("mysql")) {
	    DbType = DataBaseType.MySQL;
	    dao = startMysql();
	} else if (storageMethod.equalsIgnoreCase("sqlite")) {
	    DbType = DataBaseType.SqLite;
	    dao = startSqlite();
	} else {
	    Jobs.consoleMsg("&cInvalid storage method! Changing method to sqlite!");
	    c.getC().set("storage.method", "sqlite");
	    DbType = DataBaseType.SqLite;
	    dao = startSqlite();
	}
	Jobs.setDAO(dao);
    }

    private synchronized JobsMySQL startMysql() {
	File f = new File(Jobs.getFolder(), "generalConfig.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	String legacyUrl = config.getString("mysql-url");
	if (legacyUrl != null) {
	    String jdbcString = "jdbc:mysql://";
	    if (legacyUrl.toLowerCase().startsWith(jdbcString)) {
		legacyUrl = legacyUrl.substring(jdbcString.length());
		String[] parts = legacyUrl.split("/");
		if (parts.length >= 2) {
		    config.set("mysql-hostname", parts[0]);
		    config.set("mysql-database", parts[1]);
		}
	    }
	}
	String username = config.getString("mysql-username");
	if (username == null) {
	    Jobs.getPluginLogger().severe("mysql-username property invalid or missing");
	}
	String password = config.getString("mysql-password");
	String hostname = config.getString("mysql-hostname");
	String database = config.getString("mysql-database");
	String prefix = config.getString("mysql-table-prefix");

	boolean certificate = config.getBoolean("verify-server-certificate", false);
	boolean ssl = config.getBoolean("use-ssl", false);
	boolean autoReconnect = config.getBoolean("auto-reconnect");
	if (plugin.isEnabled()) {
	    JobsMySQL data = new JobsMySQL(plugin, hostname, database, username, password, prefix, certificate, ssl, autoReconnect);
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
	return DbType;
    }

}
