package com.gamingmesh.jobs.stuff;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;

public class VersionChecker {
    Jobs plugin;
    private int resource = 4216;

    public VersionChecker(Jobs plugin) {
	this.plugin = plugin;
	version = getCurrent();
    }

    private Version version = Version.v1_11_R1;

    public Version getVersion() {
	return version;
    }

    public enum Version {
	v1_7_R1(),
	v1_7_R2(),
	v1_7_R3(),
	v1_7_R4(),
	v1_8_R1(),
	v1_8_R2(),
	v1_8_R3(),
	v1_9_R1(),
	v1_9_R2(),
	v1_10_R1(),
	v1_11_R1(),
	v1_11_R2(),
	v1_11_R3(),
	v1_12_R1(),
	v1_12_R2(),
	v1_12_R3(),
	v1_13_R1(),
	v1_13_R2(),
	v1_13_R3();

	private Integer value = null;
	private String shortVersion = null;

	public Integer getValue() {
	    if (value == null)
		try {
		    value = Integer.valueOf(this.name().replaceAll("[^\\d.]", ""));
		} catch (Exception e) {
		}
	    return this.value;
	}

	public String getShortVersion() {
	    if (shortVersion == null)
		shortVersion = this.name().split("_R")[0];
	    return shortVersion;
	}

	public boolean isHigher(Version version) {
	    return getValue() > version.getValue();
	}

	public boolean isLower(Version version) {
	    return getValue() < version.getValue();
	}
    }

    public static Version getCurrent() {
	String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
	String vv = v[v.length - 1];
	for (Version one : Version.values()) {
	    if (one.name().equalsIgnoreCase(vv)) {
		return one;
	    }
	}
	return null;
    }

    public boolean isLower(Version version) {
	return this.version.getValue() < version.getValue();
    }

    public boolean isLowerEquals(Version version) {
	return this.version.getValue() <= version.getValue();
    }

    public boolean isHigher(Version version) {
	return this.version.getValue() > version.getValue();
    }

    public boolean isHigherEquals(Version version) {
	return this.version.getValue() >= version.getValue();
    }

    public void VersionCheck(final Player player) {
	if (!Jobs.getGCManager().isShowNewVersion())
	    return;

	Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	    @Override
	    public void run() {
		String currentVersion = plugin.getDescription().getVersion();
		String newVersion = getNewVersion();
		if (newVersion == null || newVersion.equalsIgnoreCase(currentVersion))
		    return;
		List<String> msg = Arrays.asList(
		    ChatColor.GREEN + "*********************** " + plugin.getDescription().getName() + " **************************",
		    ChatColor.GREEN + "* " + newVersion + " is now available! Your version: " + currentVersion,
		    ChatColor.GREEN + "* " + ChatColor.DARK_GREEN + plugin.getDescription().getWebsite(),
		    ChatColor.GREEN + "************************************************************");
		for (String one : msg)
		    if (player != null)
			player.sendMessage(one);
	    }
	});
    }

    public String getNewVersion() {
	try {
	    HttpURLConnection con = (HttpURLConnection) new URL("https://www.spigotmc.org/api/general.php").openConnection();
	    con.setDoOutput(true);
	    con.setRequestMethod("POST");
	    con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + resource).getBytes("UTF-8"));
	    String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	    if (version.length() <= 9)
		return version;
	} catch (Exception ex) {
	    plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Failed to check for " + plugin.getDescription().getName() + " update on spigot web page.");
	}
	return null;
    }

}
