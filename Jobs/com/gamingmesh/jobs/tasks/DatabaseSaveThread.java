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

package com.gamingmesh.jobs.tasks;

import com.gamingmesh.jobs.Jobs;

public class DatabaseSaveThread extends Thread {
    
    private volatile boolean running = true;
    private int sleep;
    
    public DatabaseSaveThread(int duration) {
        super("Jobs-DatabaseSaveTask");
        this.sleep = duration * 60000;
    }

    @Override
    public void run() {
        Jobs.getPluginLogger().info("Started database save task");
        while (running) {
            try {
                sleep(sleep);
            } catch (InterruptedException e) {
                this.running = false;
                continue;
            }
            try {
                Jobs.getPlayerManager().saveAll();
            } catch (Throwable t) {
                t.printStackTrace();
                Jobs.getPluginLogger().severe("Exception in DatabaseSaveTask, stopping auto save!");
                running = false;
            }
        }
        Jobs.getPluginLogger().info("Database save task shutdown");
        
    }
    
    public void shutdown() {
        this.running = false;
        interrupt();
    }
}
