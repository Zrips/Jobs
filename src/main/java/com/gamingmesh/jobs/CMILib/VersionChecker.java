package com.gamingmesh.jobs.CMILib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;

public class VersionChecker {

    private Jobs plugin;

    public VersionChecker(Jobs plugin) {
	this.plugin = plugin;
    }

    public Integer convertVersion(String v) {
	v = v.replaceAll("[^\\d.]", "");
	Integer version = 0;
	if (v.contains(".")) {
	    String lVersion = "";
	    for (String one : v.split("\\.")) {
		String s = one;
		if (s.length() == 1)
		    s = "0" + s;
		lVersion += s;
	    }

	    version = Integer.parseInt(lVersion);
	} else {
	    version = Integer.parseInt(v);
	}
	return version;
    }

    public void VersionCheck(final Player player) {
	if (!Jobs.getGCManager().isShowNewVersion())
	    return;

	Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	    String newVersion = getNewVersion();
	    if (newVersion == null)
		return;

	    int currentVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
	    if (Integer.parseInt(newVersion.replace(".", "")) <= currentVersion || currentVersion >=
			Integer.parseInt(newVersion.replace(".", "")))
		return;

	    List<String> msg = Arrays.asList(
		ChatColor.GREEN + "*********************** " + plugin.getDescription().getName() + " **************************",
		ChatColor.GREEN + "* " + newVersion + " is now available! Your version: " + currentVersion,
		ChatColor.GREEN + "* " + ChatColor.DARK_GREEN + plugin.getDescription().getWebsite(),
		ChatColor.GREEN + "************************************************************");
		for (String one : msg)
		    if (player != null)
			player.sendMessage(one);
		    else
			Jobs.consoleMsg(one);
	});
    }

    public String getNewVersion() {
	try {
	    URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=4216").openConnection();
	    String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	    if (version.length() <= 8)
		return version;
	} catch (Throwable t) {
	    Jobs.consoleMsg("&cFailed to check for " + plugin.getDescription().getName() + " update on spigot web page.");
	}
	return null;
    }

}
