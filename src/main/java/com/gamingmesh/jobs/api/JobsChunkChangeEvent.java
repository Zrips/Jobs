package com.gamingmesh.jobs.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Fired when there is a new chunk explored by player moving.
 * <p>
 * <b>This is same behaviour when using {@link org.bukkit.event.player.PlayerMoveEvent}
 */
public final class JobsChunkChangeEvent extends BaseEvent implements Cancellable {

    private Player player;
    private Chunk oldChunk;
    private Chunk newChunk;
    private boolean cancelled = false;

    public JobsChunkChangeEvent(Player player, Chunk oldChunk, Chunk newChunk) {
	this.player = player;
	this.oldChunk = oldChunk;
	this.newChunk = newChunk;
    }

    /**
     * Gets the player who explored a new chunk.
     * 
     * @return {@link Player}
     */
    public Player getPlayer() {
	return player;
    }

    /**
     * Returns the old explored chunk.
     * 
     * @return {@link Chunk}
     */
    public Chunk getOldChunk() {
	return oldChunk;
    }

    /**
     * Returns the new explored chunk.
     * 
     * @return {@link Chunk}
     */
    public Chunk getNewChunk() {
	return newChunk;
    }

    @Override
    public boolean isCancelled() {
	return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
	this.cancelled = cancelled;
    }
}