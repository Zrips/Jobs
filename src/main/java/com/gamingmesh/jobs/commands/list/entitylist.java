package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;

import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;

public class entitylist implements Cmd {

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        
        StringBuilder msg = new StringBuilder();
        String c1 = "&e";
        String c2 = "&6";

        int i = 0;
        for (EntityType type : EntityType.values()) {
            if (!type.isAlive() || !type.isSpawnable())
                continue;

            i++;

            if (!msg.isEmpty())
                msg.append(LC.info_ListSpliter.getLocale());

            if (i > 1) {
                msg.append(c1);
                i = 0;
            } else {
                msg.append(c2);
            }

            msg.append(CMIText.firstToUpperCase(type.name()));
        }

        CMIMessages.sendMessage(sender, msg.toString());
        return true;
    }

}
