package com.gamingmesh.jobs.economy;

import org.bukkit.OfflinePlayer;

import com.iCo6.iConomy;
import com.iCo6.system.Accounts;

public class IConomy6Adapter implements Economy {

    iConomy icon;

    public IConomy6Adapter(iConomy iconomy) {
	icon = iconomy;
    }

    public double getBalance(String playerName) {
	checkExist(playerName);
	return new Accounts().get(playerName).getHoldings().getBalance();
    }

    @Override
    public boolean hasMoney(OfflinePlayer offlinePlayer, double amount) {
	return hasMoney(offlinePlayer.getName(), amount);
    }

    @Override
    public boolean hasMoney(String PlayerName, double amount) {
	checkExist(PlayerName);
	double holdings = this.getBalance(PlayerName);
	if (holdings >= amount) {
	    return true;
	}
	return false;
    }

    @Override
    public boolean depositPlayer(String PlayerName, double amount) {
	checkExist(PlayerName);
	new Accounts().get(PlayerName).getHoldings().add(amount);
	return true;
    }

    @Override
    public boolean depositPlayer(OfflinePlayer offlinePlayer, double amount) {
	return depositPlayer(offlinePlayer.getName(), amount);
    }

    @Override
    public boolean withdrawPlayer(String playerName, double amount) {
	checkExist(playerName);
	if (this.hasMoney(playerName, amount)) {
	    new Accounts().get(playerName).getHoldings().subtract(amount);
	    return true;
	}
	return false;
    }

    @Override
    public boolean withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
	return withdrawPlayer(offlinePlayer.getName(), amount);
    }

    @Override
    public String format(double money) {
	return iConomy.format(money);
    }

    private static void checkExist(String playerName) {
	Accounts acc = new Accounts();
	if (!acc.exists(playerName)) {
	    acc.create(playerName);
	}
    }

}
