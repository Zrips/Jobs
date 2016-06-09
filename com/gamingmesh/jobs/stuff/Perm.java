package com.gamingmesh.jobs.stuff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Perm {

    public static boolean hasPermission(CommandSender player, String permission) {
	if (player instanceof Player)
	    return hasPermission((Player) player, permission);
	return true;
    }

    public static boolean hasPermission(Player player, String permission) {
	if (player == null)
	    return false;
	Permission p = new Permission(permission, PermissionDefault.FALSE);
	return player.hasPermission(p);
    }
}
