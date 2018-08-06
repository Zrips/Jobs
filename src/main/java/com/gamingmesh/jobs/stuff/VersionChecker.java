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

    private static Version version = Version.v1_11_R1;

    public Version getVersion() {
	return version;
    }

    public enum Version {
	v1_7_R1(171, "v1_7"),
	v1_7_R2(172, "v1_7"),
	v1_7_R3(173, "v1_7"),
	v1_7_R4(174, "v1_7"),
	v1_8_R1(181, "v1_8"),
	v1_8_R2(182, "v1_8"),
	v1_8_R3(183, "v1_8"),
	v1_9_R1(191, "v1_9"),
	v1_9_R2(192, "v1_9"),
	v1_10_R1(1101, "v1_10"),
	v1_11_R1(1111, "v1_11"),
	v1_11_R2(1112, "v1_11"),
	v1_12_R1(1121, "v1_12"),
	v1_12_R2(1122, "v1_12"),
	v1_13_R1(1131, "v1_13"),
	v1_13_R2(1132, "v1_13"),
	v1_14_R1(1141, "v1_14"),
	v1_14_R2(1142, "v1_14"),
	v1_15_R1(1151, "v1_15"),
	v1_15_R2(1152, "v1_15");

	private Integer value;
	private String shortVersion;

	Version(Integer value, String ShortVersion) {
	    this.value = value;
	    shortVersion = ShortVersion;
	}

	public Integer getValue() {
	    return value;
	}

	public String getShortVersion() {
	    return shortVersion;
	}

	public static Version getCurrent() {
	    String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
	    String vv = v[v.length - 1];
	    for (Version one : values()) {
		if (one.name().equalsIgnoreCase(vv))
		    return one;
	    }
	    return null;
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

	public static boolean isCurrentEqualOrHigher(Version version) {
	    return version.getValue() >= version.getValue();
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
