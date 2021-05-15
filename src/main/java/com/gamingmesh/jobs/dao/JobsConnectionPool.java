package com.gamingmesh.jobs.dao;

import java.sql.DriverManager;
import java.sql.SQLException;

public class JobsConnectionPool {

    private JobsConnection connection;
    private String url;
    private String username;
    private String password;

    public JobsConnectionPool(String url, String username, String password) {
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
	    try {
		connection = new JobsConnection(DriverManager.getConnection(url, username, password));
	    } catch (SQLException e) {
	    }
	}

	return connection;
    }

    public void closeConnection() {
	if (connection != null) {
	    try {
		connection.closeConnection();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }
}
