package com.gamingmesh.jobs.dao;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.gamingmesh.jobs.Jobs;

public class JobsConnectionPool {

    private JobsConnection connection;
    private String url;
    private String username;
    private String password;

    public JobsConnectionPool(String driverName, String url, String username, String password)
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
	Driver driver = (Driver) Class.forName(driverName, true, Jobs.getJobsClassloader()).newInstance();
	JobsDrivers jDriver = new JobsDrivers(driver);
	DriverManager.registerDriver(jDriver);

	this.url = url;
	this.username = username;
	this.password = password;
    }

    public synchronized JobsConnection getConnection() throws SQLException {
	if (connection != null && (connection.isClosed() || !connection.isValid(1))) {
	    try {
		connection.closeConnection();
	    } catch (SQLException e) {}
	    connection = null;
	}

	if (connection == null) {
	    connection = new JobsConnection(DriverManager.getConnection(url, username, password));
	}

	return connection;
    }

    public synchronized void closeConnection() {
	if (connection != null) {
	    try {
		connection.closeConnection();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }
}
