package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.i18n.Language;

public class resetexploreregion implements Cmd {

    private static String WORLD = "world";
    private static String REGEX = "^[0-9a-zA-Z_-]+$";

    @Override
    public Boolean perform(Jobs plugin, CommandSender sender, String[] args) {
        if (args.length != 2 || !WORLD.equals(args[0])) {
            return false;
        }

        if (!Jobs.getGCManager().resetExploringData) {
            Language.sendMessage(sender, "command.resetexploreregion.output.notenabled");
            return true;
        }

        final String worldName = args[1];
        if (!worldName.matches(REGEX)) {
            Language.sendMessage(sender, "command.resetexploreregion.output.invalidname");
            return true;
        }

        Jobs.getExploreManager().resetRegion(worldName);
        Language.sendMessage(sender, "command.resetexploreregion.output.reseted", "%worldname%", worldName);
        return true;
    }
}
