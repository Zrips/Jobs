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

package com.gamingmesh.jobs.economy;

import org.bukkit.OfflinePlayer;

@SuppressWarnings("deprecation")
public class VaultEconomy implements Economy {
    private net.milkbowl.vault.economy.Economy vault;

    public VaultEconomy(net.milkbowl.vault.economy.Economy vault) {
        this.vault = vault;
    }

    @Override
    public boolean depositPlayer(OfflinePlayer offlinePlayer, double money) {
        return vault.depositPlayer(offlinePlayer, money).transactionSuccess();
    }

    @Override
    public boolean depositPlayer(String PlayerName, double money) {
        return vault.depositPlayer(PlayerName, money).transactionSuccess();
    }

    @Override
    public boolean withdrawPlayer(OfflinePlayer offlinePlayer, double money) {
        return vault.withdrawPlayer(offlinePlayer, money).transactionSuccess();
    }

    @Override
    public boolean withdrawPlayer(String PlayerName, double money) {
        return vault.withdrawPlayer(PlayerName, money).transactionSuccess();
    }

    @Override
    public boolean hasMoney(OfflinePlayer offlinePlayer, double money) {
        return offlinePlayer.getName() != null && vault.has(offlinePlayer, money);
    }

    @Override
    public boolean hasMoney(String PlayerName, double money) {
        return vault.has(PlayerName, money);
    }

    @Override
    public String format(double money) {
        return vault.format(money);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return vault.getBalance(offlinePlayer);
    }

    @Override
    public double getBalance(String PlayerName) {
        return vault.getBalance(PlayerName);
    }

    @Override
    public String getFormatedBalance(OfflinePlayer offlinePlayer) {
        return format(getBalance(offlinePlayer));
    }

}
