package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.config.RestrictedAreaManager;
import com.gamingmesh.jobs.container.CuboidArea;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.RestrictedArea;
import com.gamingmesh.jobs.hooks.JobsHook;
import com.gamingmesh.jobs.i18n.Language;

import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class area implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            CMIMessages.sendMessage(sender, LC.info_Ingame);
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add")) {
                if (!Jobs.hasPermission(player, "jobs.area.add", true))
                    return true;

                double bonus = 0D;
                try {
                    bonus = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    return false;
                }

                boolean wg = false;

                String name = args[1];
                if (name.startsWith("wg:")) {
                    wg = true;
                    name = name.substring("wg:".length(), name.length());
                }

                RestrictedAreaManager ra = Jobs.getRestrictedAreaManager();

                if (ra.isExist(name)) {
                    Language.sendMessage(sender, "command.area.output.exist");
                    return true;
                }

                if (!wg && !Jobs.getSelectionManager().hasPlacedBoth(player)) {
                    Language.sendMessage(sender, "command.area.output.select",
                        "%tool%", CMIMaterial.get(Jobs.getGCManager().getSelectionTool()).getName());
                    return true;
                }
                if (wg && JobsHook.WorldGuard.isEnabled()) {
                    com.sk89q.worldguard.protection.regions.ProtectedRegion protectedRegion = JobsHook.getWorldGuardManager().getProtectedRegionByName(name);

                    if (protectedRegion == null) {
                        Language.sendMessage(sender, "command.area.output.wgDontExist");
                        return true;
                    }
                    name = protectedRegion.getId();
                }

                if (!wg) {
                    RestrictedArea restrictedArea = new RestrictedArea(name, Jobs.getSelectionManager().getSelectionCuboid(player), bonus);
                    restrictedArea.setEnabled(true);
                    ra.addNew(restrictedArea, true);
                } else {
                    RestrictedArea restrictedArea = new RestrictedArea(name, name, bonus);
                    restrictedArea.setEnabled(true);
                    ra.addNew(restrictedArea, true);
                }
                Language.sendMessage(sender, "command.area.output.addedNew", "%bonus%", bonus);
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (!Jobs.hasPermission(player, "jobs.area.remove", true))
                    return true;

                RestrictedAreaManager ra = Jobs.getRestrictedAreaManager();
                String name = args[1];

                if (!ra.isExist(name)) {
                    Language.sendMessage(sender, "command.area.output.dontExist");
                    return true;
                }

                ra.remove(name);
                Language.sendMessage(sender, "command.area.output.removed", "%name%", name);
                return true;
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {

            Set<RestrictedArea> areas = Jobs.getRestrictedAreaManager().getByLocation(player.getLocation());

            StringBuilder msg = new StringBuilder();

            for (RestrictedArea area : areas) {
                if (!msg.toString().isEmpty())
                    msg.append(LC.info_ListSpliter.getLocale());
                msg.append(area.getName());
            }

            if (msg.toString().isEmpty()) {
                Language.sendMessage(sender, "command.area.output.noAreasByLoc");
                return true;
            }
            Language.sendMessage(sender, "command.area.output.areaList", "%list%", msg);
            return true;
        }

        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("list")) {

            java.util.Map<String, RestrictedArea> areas = Jobs.getRestrictedAreaManager().getRestrictedAreas();
            if (areas.isEmpty()) {
                sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.noAreas"));
                return true;
            }

            sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
            int i = 0;
            for (Entry<String, RestrictedArea> area : areas.entrySet()) {
                i++;
                CuboidArea cuboid = area.getValue().getCuboidArea();
                HashMap<CurrencyType, Double> multi = area.getValue().getMultipliers();
                if (area.getValue().getWgName() == null) {
                    Language.sendMessage(sender, "command.area.output.lists",
                        "%number%", i,
                        "%areaname%", area.getValue().getName(),
                        "%worldname%", cuboid.getWorldName(),
                        "%x1%", cuboid.getLowPoint().getBlockX(),
                        "%y1%", cuboid.getLowPoint().getBlockY(),
                        "%z1%", cuboid.getLowPoint().getBlockZ(),
                        "%x2%", cuboid.getHighPoint().getBlockX(),
                        "%y2%", cuboid.getHighPoint().getBlockY(),
                        "%z2%", cuboid.getHighPoint().getBlockZ(),
                        "%money%", multi.get(CurrencyType.MONEY),
                        "%points%", multi.get(CurrencyType.POINTS),
                        "%exp%", multi.get(CurrencyType.EXP));
                } else {
                    Language.sendMessage(sender, "command.area.output.wgLists",
                        "%number%", i,
                        "%areaname%", area.getValue().getName(),
                        "%money%", multi.get(CurrencyType.MONEY),
                        "%points%", multi.get(CurrencyType.POINTS),
                        "%exp%", multi.get(CurrencyType.EXP));
                }
            }
            Language.sendMessage(sender, "general.info.separator");
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("add")) {
                Language.sendMessage(sender, "command.area.help.addUsage");
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                Language.sendMessage(sender, "command.area.help.removeUsage");
                return true;
            }
        }

        return false;
    }

}
