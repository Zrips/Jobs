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

import org.bukkit.plugin.RegisteredServiceProvider;

import com.gamingmesh.jobs.economy.BlackholeEconomy;
import com.gamingmesh.jobs.economy.VaultEconomy;

public class HookEconomyTask implements Runnable {

    private final Jobs plugin;

    public HookEconomyTask(Jobs plugin) {
	this.plugin = plugin;
    }

    @Override
    public void run() {
	if (setVault())
	    return;

	// no Economy found
	Jobs.setEconomy(new BlackholeEconomy());
	Jobs.getPluginLogger().severe("==================== " + plugin.getDescription().getName() + " ====================");
	Jobs.getPluginLogger().severe("Vault is required by this plugin for economy support!");
	Jobs.getPluginLogger().severe("Please install them first!");
	Jobs.getPluginLogger().severe("You can find the latest versions here:");
	Jobs.getPluginLogger().severe("https://www.spigotmc.org/resources/34315/");
	Jobs.getPluginLogger().severe("==============================================");
    }

    private boolean setVault() {
	if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault"))
	    return false;

	RegisteredServiceProvider<Economy> provider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
	if (provider == null)
	    return false;

	Jobs.setEconomy(new VaultEconomy(provider.getProvider()));
	Jobs.consoleMsg("&e[" + plugin.getDescription().getName() + "] Successfully linked with Vault.");
	return true;
    }

}
