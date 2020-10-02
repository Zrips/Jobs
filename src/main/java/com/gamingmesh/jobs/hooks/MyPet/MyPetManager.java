package com.gamingmesh.jobs.hooks.MyPet;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.repository.PlayerManager;

public class MyPetManager {

    private final PlayerManager mp = MyPetApi.getPlayerManager();

    public boolean isMyPet(Entity entity, Player owner) {
	if (owner == null) {
	    return entity instanceof MyPetBukkitEntity;
	}

	if (!mp.isMyPetPlayer(owner)) {
	    return false;
	}

	MyPetPlayer myPetPlayer = mp.getMyPetPlayer(owner);
	return myPetPlayer.hasMyPet() && myPetPlayer.getMyPet().getEntity().isPresent() &&
		myPetPlayer.getMyPet().getEntity().get().getType() == entity.getType();
    }

    public UUID getOwnerOfPet(Entity ent) {
	if (!(ent instanceof MyPetBukkitEntity))
	    return null;

	MyPet myPet = ((MyPetBukkitEntity) ent).getMyPet();

	try {
	    return myPet.getOwner().getPlayer().getUniqueId();
	} catch (Exception e) {
	    return null;
	}
    }
}
