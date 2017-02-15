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

package com.gamingmesh.jobs;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.gamingmesh.jobs.economy.BlackholeEconomy;
import com.gamingmesh.jobs.economy.VaultEconomy;
import com.gamingmesh.jobs.economy.IConomy6Adapter;

public class HookEconomyTask implements Runnable {
    private Jobs plugin;

    public HookEconomyTask(Jobs plugin) {
	this.plugin = plugin;
    }

    @Override
    public void run() {

	if (setVault())
	    return;

	if (setIConomy())
	    return;

	// no Economy found
	Jobs.setEconomy(this.plugin, new BlackholeEconomy());
	Bukkit.getServer().getLogger().severe("==================== " + this.plugin.getDescription().getName() + " ====================");
	Bukkit.getServer().getLogger().severe("Vault or Iconomy is required by this plugin for economy support!");
	Bukkit.getServer().getLogger().severe("Please install them first!");
	Bukkit.getServer().getLogger().severe("You can find the latest versions here:");
	Bukkit.getServer().getLogger().severe("http://dev.bukkit.org/bukkit-plugins/vault/");
	Bukkit.getServer().getLogger().severe("https://dev.bukkit.org/bukkit-plugins/iconomy-7/");
	Bukkit.getServer().getLogger().severe("==============================================");
    }
    
    private boolean setVault() {
	Plugin eco = Bukkit.getServer().getPluginManager().getPlugin("Vault");
	if (eco == null)
	    return false;

	RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
	if (provider == null)
	    return false;

	Economy economy = provider.getProvider();
	if (economy == null)
	    return false;

	Jobs.setEconomy(this.plugin, new VaultEconomy(economy));
	Jobs.consoleMsg("&e[" + this.plugin.getDescription().getName() + "] Successfully linked with Vault.");
	return true;
    }

    private boolean setIConomy() {
	Plugin p = Bukkit.getServer().getPluginManager().getPlugin("iConomy");
	if (p == null)
	    return false;

	try {
	    Jobs.setEconomy(this.plugin, new IConomy6Adapter((com.iCo6.iConomy) p));
	} catch (Exception e) {
	    Jobs.consoleMsg("&e[" + this.plugin.getDescription().getName() + "] UNKNOWN iConomy version.");
	    return false;
	}

	Jobs.consoleMsg("&e[" + this.plugin.getDescription().getName() + "] Successfully linked with iConomy! Version: " + p.getDescription().getVersion());
	return true;

    }

}
