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

public class HookEconomyTask implements Runnable {
    private JobsPlugin plugin;
    public HookEconomyTask(JobsPlugin plugin) {
        this.plugin = plugin;
    }
    public void run() {
        Plugin test = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        if (test != null) {
            RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) {
                Economy economy = provider.getProvider();
                
                if (economy != null && economy.isEnabled()) {
                    Jobs.setEconomy(plugin, new VaultEconomy(economy));
                    Jobs.getPluginLogger().info("["+plugin.getDescription().getName()+"] Successfully linked with Vault.");
                    return;
                }
            }
        }
        
        // no Vault found
        Jobs.setEconomy(plugin, new BlackholeEconomy());
        Bukkit.getServer().getLogger().severe("==================== "+plugin.getDescription().getName()+" ====================");
        Bukkit.getServer().getLogger().severe("Vault is required by this plugin for economy support!");
        Bukkit.getServer().getLogger().severe("Please install Vault first!");
        Bukkit.getServer().getLogger().severe("You can find the latest version here:");
        Bukkit.getServer().getLogger().severe("http://dev.bukkit.org/server-mods/vault/");
        Bukkit.getServer().getLogger().severe("==============================================");
    }

}
