package com.gamingmesh.jobs.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobLimitedItems;
import com.gamingmesh.jobs.i18n.Language;
import com.gamingmesh.jobs.stuff.GiveItem;

import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;

public class give implements Cmd {

    private enum actions {
        items, limiteditems;

        public static actions getByname(String name) {
            for (actions one : actions.values()) {
                if (one.name().equalsIgnoreCase(name))
                    return one;
            }
            return null;
        }
    }

    @Override
    public Boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
        Player player = null;
        Job job = null;
        actions name = null;
        String itemName = null;

        for (String one : args) {
            if (player == null) {
                player = Bukkit.getPlayer(one);
                if (player != null)
                    continue;
            }

            if (job == null) {
                job = Jobs.getJob(one);
                if (job != null)
                    continue;
            }

            if (name == null) {
                name = actions.getByname(one);
                if (name != null)
                    continue;
            }
            itemName = one;
        }

        if (player == null && sender instanceof Player)
            player = (Player) sender;

        if (player == null) {
            Language.sendMessage(sender, "command.give.output.notonline");
            return null;
        }

        if (name == null)
            name = actions.items;

        if (itemName == null) {
            Jobs.getCommandManager().sendUsage(sender, "give");
            return true;
        }

        Player p = player;

        switch (name) {
        case items:
            JobItems jItem = ItemBoostManager.getItemByKey(itemName);

            CMIAsyncHead ahead = new CMIAsyncHead() {
                @Override
                public void afterAsyncUpdate(ItemStack item) {
                    GiveItem.giveItemForPlayer(p, ItemBoostManager.applyNBT(item, jItem.getNode()));
                }
            };

            CMIItemStack item = jItem == null ? null : jItem.getItemStack(player, ahead);

            if (jItem == null || item == null) {
                Language.sendMessage(sender, "command.give.output.noitem");
                return true;
            }
            if (!ahead.isAsyncHead())
                GiveItem.giveItemForPlayer(player, ItemBoostManager.applyNBT(item.getItemStack(), jItem.getNode()));
            break;
        case limiteditems:
            if (job == null) {
                Jobs.getCommandManager().sendUsage(sender, "give");
                return true;
            }

            JobLimitedItems jLItem = job.getLimitedItems().get(itemName.toLowerCase());

            ahead = new CMIAsyncHead() {
                @Override
                public void afterAsyncUpdate(ItemStack item) {
                    GiveItem.giveItemForPlayer(p, item);
                }
            };

            CMIItemStack limItem = jLItem == null ? null : jLItem.getItemStack(player, ahead);

            if (limItem == null) {
                Language.sendMessage(sender, "command.give.output.noitem");
                return true;
            }

            if (!ahead.isAsyncHead())
                GiveItem.giveItemForPlayer(player, limItem.getItemStack());
            break;
        default:
            break;
        }

        return true;
    }
}