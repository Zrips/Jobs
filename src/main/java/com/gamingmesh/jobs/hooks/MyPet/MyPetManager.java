package com.gamingmesh.jobs.hooks.MyPet;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.entity.Pet;
import net.Zrips.CMILib.Messages.CMIMessages;

public class MyPetManager {

    private final Api api;

    public MyPetManager() {
        Api api = null;
        try {
            MyPetApi.class.getMethod("getPetManager");
            api = new ModernApi();
        } catch (NoSuchMethodException e) {
            api = LegacyApi.create();
        }
        this.api = api;
    }

    public boolean isMyPet(Entity entity, Player owner) {
        UUID petOwner = getOwner(entity);
        return petOwner != null && (owner == null || petOwner.equals(owner.getUniqueId()));
    }

    public UUID getOwnerOfPet(Entity entity) {
        return getOwner(entity);
    }

    private UUID getOwner(Entity entity) {
        return api == null ? null : api.getOwner(entity);
    }

    private interface Api {
        UUID getOwner(Entity entity);
    }

    private static final class ModernApi implements Api {
        @Override
        public UUID getOwner(Entity entity) {
            Pet pet = MyPetApi.getPetManager().getPetFromEntity(entity);
            return pet == null || pet.getOwner() == null ? null : pet.getOwner().getUniqueId();
        }
    }

    private static final class LegacyApi implements Api {

        private final Class<?> petEntityClass;
        private final Method getMyPet;
        private final Method getOwner;
        private final Method getPlayer;

        private LegacyApi(Class<?> petEntityClass, Method getMyPet, Method getOwner, Method getPlayer) {
            this.petEntityClass = petEntityClass;
            this.getMyPet = getMyPet;
            this.getOwner = getOwner;
            this.getPlayer = getPlayer;
        }

        private static LegacyApi create() {
            try {
                ClassLoader classLoader = MyPetApi.class.getClassLoader();
                Class<?> petEntityClass = Class.forName("de.Keyle.MyPet.api.entity.MyPetBukkitEntity", false, classLoader);
                Class<?> myPetClass = Class.forName("de.Keyle.MyPet.api.entity.MyPet", false, classLoader);
                Class<?> myPetPlayerClass = Class.forName("de.Keyle.MyPet.api.player.MyPetPlayer", false, classLoader);
                return new LegacyApi(petEntityClass, petEntityClass.getMethod("getMyPet"), myPetClass.getMethod("getOwner"), myPetPlayerClass.getMethod("getPlayer"));
            } catch (ReflectiveOperationException e) {
                CMIMessages.consoleMessage("Jobs could not initialize its MyPet 3 integration: " + e.getMessage());
                return null;
            }
        }

        @Override
        public UUID getOwner(Entity entity) {
            if (!petEntityClass.isInstance(entity)) {
                return null;
            }

            try {
                Object pet = getMyPet.invoke(entity);
                if (pet == null)
                    return null;

                Object owner = getOwner.invoke(pet);
                if (owner == null)
                    return null;

                Object player = getPlayer.invoke(owner);

                return player instanceof Player ? ((Player) player).getUniqueId() : null;

            } catch (ReflectiveOperationException | ClassCastException e) {
                return null;
            }
        }
    }
}
