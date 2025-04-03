package com.gamingmesh.jobs.stuff;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;

import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public final class VersionChecker {

    private Jobs plugin;

    public VersionChecker(Jobs plugin) {
        this.plugin = plugin;
    }

    public void VersionCheck(final Player player) {
        if (!Jobs.getGCManager().isShowNewVersion())
            return;

        CMIScheduler.runTaskAsynchronously(plugin, () -> {
            String newVersion = getNewVersion();
            if (newVersion == null)
                return;

            int currentVersion = Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
            int newVer = Integer.parseInt(newVersion.replace(".", ""));

            if (newVer <= currentVersion || currentVersion >= newVer)
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
                    CMIMessages.consoleMessage(one);
        });
    }

    public String getNewVersion() {
        try {
            URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=4216").openConnection();
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 8)
                return version;
        } catch (Throwable t) {
            CMIMessages.consoleMessage("&cFailed to check for " + plugin.getDescription().getName() + " update on spigot web page.");
        }
        return null;
    }

}
