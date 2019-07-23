package com.gamingmesh.jobs.MyPet;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gamingmesh.jobs.Jobs;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import de.Keyle.MyPet.api.repository.PlayerManager;

public class MyPetManager {

    PlayerManager mp;
    de.Keyle.MyPet.api.repository.MyPetManager mppm;
    private boolean enabled = false;

    public MyPetManager() {
	setup();
    }

    public boolean isMyPet(Object ent) {
	if (!enabled || ent == null)
	    return false;
	if (!(ent instanceof MyPetBukkitEntity))
	    return false;

	return true;
    }

    public UUID getOwnerOfPet(Object ent) {
	if (!enabled || ent == null)
	    return null;
	if (!(ent instanceof MyPetBukkitEntity))
	    return null;

	MyPet myPet = ((MyPetBukkitEntity) ent).getMyPet();

	try {
	    UUID uuid = myPet.getOwner().getPlayer().getUniqueId();
	    return uuid;
	} catch (Exception e) {
	    return null;
	}
    }

    public void setup() {
	Plugin pl = Bukkit.getPluginManager().getPlugin("MyPet");
	if (pl == null || !Bukkit.getPluginManager().isPluginEnabled("MyPet"))
	    return;

	mp = MyPetApi.getPlayerManager();
	mppm = MyPetApi.getMyPetManager();
	enabled = true;
	Jobs.consoleMsg("&e[Jobs] &6MyPet detected");
    }

}
