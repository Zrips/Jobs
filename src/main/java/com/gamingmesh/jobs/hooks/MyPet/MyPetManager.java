package com.gamingmesh.jobs.hooks.MyPet;

import java.util.UUID;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import de.Keyle.MyPet.api.repository.PlayerManager;

public class MyPetManager {

    PlayerManager mp;
    de.Keyle.MyPet.api.repository.MyPetManager mppm;
    private boolean enabled = false;

    public MyPetManager() {
	mp = MyPetApi.getPlayerManager();
	mppm = MyPetApi.getMyPetManager();
	enabled = true;
    }

    public boolean isMyPet(Object ent) {
	if (!enabled || !(ent instanceof MyPetBukkitEntity))
	    return false;

	return true;
    }

    public UUID getOwnerOfPet(Object ent) {
	if (!enabled || !(ent instanceof MyPetBukkitEntity))
	    return null;

	MyPet myPet = ((MyPetBukkitEntity) ent).getMyPet();

	try {
	    return myPet.getOwner().getPlayer().getUniqueId();
	} catch (Exception e) {
	    return null;
	}
    }
}
