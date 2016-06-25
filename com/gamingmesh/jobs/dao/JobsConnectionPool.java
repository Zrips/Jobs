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
import java.sql.DriverManager;
import java.sql.SQLException;

import com.gamingmesh.jobs.Jobs;

public class JobsConnectionPool {
    private JobsConnection connection;
    private String url;
    private String username;
    private String password;
    public JobsConnectionPool(String driverName, String url, String username, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
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
            @SuppressWarnings("resource")
	    Connection conn = DriverManager.getConnection(url, username, password);
            connection = new JobsConnection(conn);
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
