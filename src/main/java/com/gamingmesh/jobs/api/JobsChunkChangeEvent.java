package com.gamingmesh.jobs.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

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

    public Player getPlayer() {
	return player;
    }

    public Chunk getOldChunk() {
	return oldChunk;
    }

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