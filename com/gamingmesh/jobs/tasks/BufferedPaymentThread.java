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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.economy.BufferedEconomy;

public class BufferedPaymentThread extends Thread {
    private volatile boolean running = true;
    private int sleep;

    public BufferedPaymentThread(int duration) {
	super("Jobs-BufferedPaymentThread");
	this.sleep = duration * 1000;
    }

    @Override
    public void run() {

	String message = ChatColor.YELLOW + "[Jobs] Started buffered payment thread.";
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	console.sendMessage(message);

	//Jobs.getPluginLogger().info("Started buffered payment thread");
	while (running) {
	    try {
		sleep(sleep);
	    } catch (InterruptedException e) {
		this.running = false;
		continue;
	    }
	    try {
		BufferedEconomy economy = Jobs.getEconomy();
		if (economy != null)
		    economy.payAll();
	    } catch (Throwable t) {
		t.printStackTrace();
		message = ChatColor.RED + "[Jobs] Exception in BufferedPaymentThread, stopping economy payments!";
		console.sendMessage(message);
		//Jobs.getPluginLogger().severe("Exception in BufferedPaymentThread, stopping economy payments!");
		running = false;
	    }
	}
	message = ChatColor.YELLOW + "[Jobs] Buffered payment thread shutdown.";
	console.sendMessage(message);
	//Jobs.getPluginLogger().info("Buffered payment thread shutdown");   
    }

    public void shutdown() {
	this.running = false;
	interrupt();
    }
}
