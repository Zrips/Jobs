package com.gamingmesh.jobs.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JobsConnection {
    private Connection conn;

    public JobsConnection(Connection conn) {
	this.conn = conn;
    }

    public Connection getConnection() {
	return conn;
    }

    public synchronized boolean isClosed() {
	try {
	    return conn.isClosed();
	} catch (SQLException e) {
	    // Assume it's closed
	    return true;
	}
    }

    public synchronized boolean isValid(int timeout) throws SQLException {
	try {
	    return conn.isValid(timeout);
	} catch (AbstractMethodError e) {
	}

	return true;
    }

    public synchronized void closeConnection() throws SQLException {
	conn.close();
    }

    public synchronized Statement createStatement() throws SQLException {
	return conn.createStatement();
    }

    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException {
	return conn.prepareStatement(sql);
    }

    public synchronized PreparedStatement prepareStatement(String sql, int returnGeneratedKeys) throws SQLException {
	return conn.prepareStatement(sql, returnGeneratedKeys);
    }

    public synchronized void setAutoCommit(Boolean mode) throws SQLException {
	conn.setAutoCommit(mode);
    }

    public synchronized void commit() throws SQLException {
	if (conn.getAutoCommit()) {
	    conn.setAutoCommit(false);
	    conn.commit();
	    conn.setAutoCommit(true);
	} else {
	    conn.commit();
	}
    }

    public synchronized DatabaseMetaData getMetaData() throws SQLException {
	return conn.getMetaData();
    }

    public synchronized void setClientInfo(String path, String value) throws SQLException {
	conn.setClientInfo(path, value);
    }
}
