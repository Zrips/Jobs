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
    private static int resource = 4216;

    public VersionChecker(Jobs plugin) {
	this.plugin = plugin;
    }

    public Version getVersion() {
	return Version.getCurrent();
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

    public enum Version {
	v1_7_R1,
	v1_7_R2,
	v1_7_R3,
	v1_7_R4,
	v1_8_R1,
	v1_8_R2,
	v1_8_R3,
	v1_9_R1,
	v1_9_R2,
	v1_10_R1,
	v1_11_R1,
	v1_12_R1,
	v1_13_R1,
	v1_13_R2,
	v1_14_R1,
	v1_14_R2,
	v1_15_R1,
	v1_15_R2,
	v1_16_R1,
	v1_16_R2,
	v1_17_R1,
	v1_17_R2;

	private Integer value;
	private String shortVersion;
	private static Version current = null;

	Version() {
	    try {
		this.value = Integer.valueOf(this.name().replaceAll("[^\\d.]", ""));
	    } catch (Exception e) {
	    }
	    shortVersion = this.name().substring(0, this.name().length() - 3);
	}

	public Integer getValue() {
	    return value;
	}

	public String getShortVersion() {
	    return shortVersion;
	}

	public static Version getCurrent() {
	    if (current != null)
		return current;
	    String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
	    String vv = v[v.length - 1];
	    for (Version one : values()) {
		if (one.name().equalsIgnoreCase(vv)) {
		    current = one;
		    break;
		}
	    }
	    return current;
	}

	public boolean isLower(Version version) {
	    return getValue() < version.getValue();
	}

	public boolean isHigher(Version version) {
	    return getValue() > version.getValue();
	}

	public boolean isEqualOrLower(Version version) {
	    return getValue() <= version.getValue();
	}

	public boolean isEqualOrHigher(Version version) {
	    return getValue() >= version.getValue();
	}

	public static boolean isCurrentEqualOrHigher(Version v) {
	    return getCurrent().getValue() >= v.getValue();
	}

	public static boolean isCurrentHigher(Version v) {
	    return getCurrent().getValue() > v.getValue();
	}

	public static boolean isCurrentLower(Version v) {
	    return getCurrent().getValue() < v.getValue();
	}

	public static boolean isCurrentEqualOrLower(Version v) {
	    return getCurrent().getValue() <= v.getValue();
	}
    }

    public void VersionCheck(final Player player) {
	if (!Jobs.getGCManager().isShowNewVersion())
	    return;

	Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	    String newVersion = getNewVersion();
	    if (newVersion == null)
		return;

	    String currentVersion = plugin.getDescription().getVersion();
	    if (Integer.parseInt(newVersion.replace(".", "")) <= Integer.parseInt(currentVersion.replace(".", "")))
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
	    URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resource).openConnection();
	    String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	    if (version.length() <= 8)
		return version;
	} catch (Throwable t) {
	    Jobs.consoleMsg("&cFailed to check for " + plugin.getDescription().getName() + " update on spigot web page.");
	}
	return null;
    }

}
