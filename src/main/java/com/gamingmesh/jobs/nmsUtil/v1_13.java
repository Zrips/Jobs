package com.gamingmesh.jobs.nmsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;

import com.gamingmesh.jobs.NMS;

public class v1_13 implements NMS {

    @Override
    public List<Block> getPistonRetractBlocks(BlockPistonRetractEvent event) {
	return new ArrayList<>(event.getBlocks());
    }

    @Override
    public String getRealType(Entity entity) {
	return entity.getType().name();
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
	return player.getInventory().getItemInMainHand();
    }

    @Override
    public void setItemInMainHand(Player player, ItemStack item) {
	player.getInventory().setItemInMainHand(item);
    }

    @Override
    public double getMaxHealth(LivingEntity entity) {
	return entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
    }

    @Override
    public short getDurability(ItemStack item) {
	return (short) ((Damageable) item.getItemMeta()).getDamage();
    }

    @Override
    public void setSkullOwner(SkullMeta meta, OfflinePlayer player) {
	if (meta != null && player != null) {
	    meta.setOwningPlayer(player);
	}
    }
}
