package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Util.CMIVersionChecker;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.milkbowl.vault.economy.Economy;

public class version implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {

        CMIScheduler.runTaskAsynchronously(plugin, () -> {
            final String version = plugin.getDescription().getVersion();
            final String official = Jobs.getVersionCheckManager().getNewVersion();
            String ofCMIlibVersion = CMIVersionChecker.getOfficialVersion(87610, "CMILib");

            final String server = Bukkit.getBukkitVersion();
            String[] split = Bukkit.getVersion().split("-");
            final String serverType = split.length > 1 ? split[1] : split[0];
            String build = null;

            try {
                if (serverType.equalsIgnoreCase("Paper"))
                    build = Bukkit.getVersion().split("-")[2].split(" ")[0];
            } catch (Exception e) {

            }

            final String buildVersion = build == null ? "" : "(" + build + ")";

            String injector = "";
            if (plugin.getServer().getPluginManager().getPlugin("CMIEInjector") != null)
                injector = "(CMIEInjector)";

            Plugin v = plugin.getServer().getPluginManager().getPlugin("Vault");
            String preVault = "Unknown0";
            String vProvider = null;
            if (v != null) {
                preVault = v.getDescription().getVersion();
                preVault = (preVault + (v.getDescription().getDescription().contains("CMIEconomy") ? "(+)" : injector)).replace("${env.TRAVIS_BUILD_NUMBER}", "");

                RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp != null) {
                    vProvider = rsp.getProvider().getName();
                }
            }

            final String vault = preVault;
            final String vaultProvider = vProvider;

            CMIScheduler.runTask(plugin, () -> {

                Plugin CMILib = Bukkit.getPluginManager().getPlugin("CMILib");
                String CMILibversion = null;
                if (CMILib != null) {
                    CMILibversion = CMILib.getDescription().getVersion();
                }

                CMIMessages.sendMessage(sender, LC.info_Spliter);

                RawMessage rm = new RawMessage();
                rm.addText(Jobs.getLanguage().getMessage("command.version.output.jobsVersion", "[version]", version)
                    + Jobs.getLanguage().getMessage("command.version.output.dbType", "[db]", Jobs.getDBManager().getDbType().toString()));

                if (!version.equalsIgnoreCase(official))
                    rm.addText(Jobs.getLanguage().getMessage("command.version.output.jobsVersionNew", "[newVersion]", official)).addUrl("https://www.spigotmc.org/resources/4216/updates").addHover(
                        "&r&2New version available");
                rm.show(sender);

                if (CMILibversion != null) {
                    rm = new RawMessage();
                    rm.addText(Jobs.getLanguage().getMessage("command.version.output.CMILib", "[version]", CMILibversion));

                    if (ofCMIlibVersion != null && Version.convertVersion(CMILibversion) < Version.convertVersion(ofCMIlibVersion)) {
                        rm.addText(Jobs.getLanguage().getMessage("command.version.output.cmilVersionNew", "[newVersion]", ofCMIlibVersion));
                        rm.addUrl("https://www.spigotmc.org/resources/" + 87610 + "/updates").addHover("&r&2New version available");
                    }
                    rm.show(sender);
                }

                CMIMessages.sendMessage(sender, Jobs.getLanguage().getMessage("command.version.output.newServer", "[version]", serverType + buildVersion + " " + server));

                CMIMessages.sendMessage(sender, (vaultProvider != null ? Jobs.getLanguage().getMessage("command.version.output.Economy", "[provider]", vaultProvider) : "") +
                    (vault != null ? Jobs.getLanguage().getMessage("command.version.output.newVault", "[version]", vault) : ""));

                LC.info_Spliter.sendMessage(sender);
            });
        });

        return true;
    }
}
