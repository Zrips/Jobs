package com.gamingmesh.jobs.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class JobsBlockOwnershipRegisterEvent extends BaseEvent implements Cancellable{

    private boolean cancelled = false;
    private final Player player;
    private final Block block;

    public JobsBlockOwnershipRegisterEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Returns the player who try to register a block ownership.
     *
     * @return {@link Player}
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the block which the player tried to register.
     *
     * @return {@link Block}
     */
    public Block getBlock() {
        return block;
    }
}
