package com.gamingmesh.jobs.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class JobsDriver
    implements Driver {
    private Driver driver;

    public JobsDriver(Driver driver) {
	this.driver = driver;
    }

    @Override
    public Connection connect(String url, Properties info)
	throws SQLException {
	return this.driver.connect(url, info);
    }

    @Override
    public boolean acceptsURL(String url)
	throws SQLException {
	return this.driver.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
	throws SQLException {
	return this.driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
	return this.driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
	return this.driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
	return this.driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
	return this.driver.getParentLogger();
    }
}
