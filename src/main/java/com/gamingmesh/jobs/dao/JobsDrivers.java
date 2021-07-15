/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class JobsDrivers implements Driver {
    private Driver driver;

    public JobsDrivers(Driver driver) {
	this.driver = driver;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
	return driver.connect(url, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
	return driver.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
	return driver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
	return driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
	return driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
	return driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
	return driver.getParentLogger();
    }
}
