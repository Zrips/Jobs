package com.gamingmesh.jobs.dao;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;

import net.Zrips.CMILib.Messages.CMIMessages;

public class JobsSQLite extends JobsDAO {

    public void initialize() {
        setUp();
    }

    public JobsSQLite initialize(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Could not create SQLite directory: " + dir.getAbsolutePath());
        }
        setUp();
        return this;
    }

    JobsSQLite(Jobs plugin, File file) {
        super(plugin, "org.sqlite.JDBC", "jdbc:sqlite:" + new File(file, "jobs.sqlite.db").getPath(), null, null, "");
        if (!file.exists() && !file.mkdirs()) {
            throw new IllegalStateException("Could not create SQLite directory: " + file.getAbsolutePath());
        }
        setDbType(DataBaseType.SqLite);
    }

    @Override
    protected void checkUpdate() throws SQLException {
        JobsConnection conn = getConnection();
        if (conn == null) {
            throw new SQLException("Could not connect to SQLite database.");
        }

        String query = "CREATE TABLE IF NOT EXISTS `" + getPrefix()
                + "users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `player_uuid` varchar(36) NOT NULL, `username` varchar(20), `data` text);";

        executeSQL(query);
    }

    @Override
    public Statement prepareStatement(String query) throws SQLException {
        JobsConnection conn = getConnection();
        
        if (conn == null) 
            throw new SQLException("SQLite connection is not available.");        

        try {
            return conn.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean createTable(String query) {
        Statement statement = null;
        try {
            if (query == null || query.isEmpty()) {
                CMIMessages.consoleMessage("&cCould not create table: query is empty or null.");
                return false;
            }

            statement = getConnection().createStatement();
            statement.execute(query);
            return true;
        } catch (SQLException e) {
            CMIMessages.consoleMessage("&cCould not create table, SQLException: " + e.getMessage());
            return false;
        } finally {
            close(statement);
        }
    }

    @Override
    public boolean isTable(String table) {
        try {
            ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null);
            if (tables.next()) {
                tables.close();
                return true;
            }
            tables.close();
            return false;
        } catch (SQLException e) {
            CMIMessages.consoleMessage("&cCould not check if table \"" + table + "\" exists, SQLException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isCollumn(String table, String collumn) {
        try {
            ResultSet tables = getConnection().getMetaData().getColumns(null, null, table, collumn);
            if (tables.next()) {
                tables.close();
                return true;
            }
            tables.close();
            return false;
        } catch (SQLException e) {
            CMIMessages.consoleMessage("&cCould not check if table \"" + table + "\" exists, SQLException: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addCollumn(String table, String collumn, String type) {
        Statement statement;
        try {
            statement = getConnection().createStatement();
        } catch (SQLException e) {
            CMIMessages.consoleMessage("&cCould not add new collumn, SQLException: " + e.getMessage());
            return false;
        }
        try {
            statement.execute("ALTER TABLE `" + table + "` ADD `" + collumn + "` " + type);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
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
                CMIMessages.consoleMessage("&cTable \"" + table + "\" does not exist.");
                return false;
            }
            statement = getConnection().createStatement();
            statement.executeUpdate("DELETE FROM `" + table + "`;");
            return true;
        } catch (SQLException e) {
            if (!(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) &&
                    !e.toString().contains("not return ResultSet"))
                CMIMessages.consoleMessage("&cError in wipeTable() query: " + e);
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
                CMIMessages.consoleMessage("&cTable \"" + table + "\" does not exist.");
                return false;
            }
            statement = getConnection().createStatement();
            statement.execute("DROP TABLE IF EXISTS `" + table + "`;");
            return true;
        } catch (SQLException e) {
            if (!(e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) &&
                    !e.toString().contains("not return ResultSet"))
                CMIMessages.consoleMessage("&cError in dropTable() query: " + e);
            return false;
        } finally {
            close(statement);
        }
    }
}
