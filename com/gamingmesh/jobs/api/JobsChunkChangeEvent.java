package com.gamingmesh.jobs.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class JobsChunkChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Chunk oldChunk;
    private Chunk newChunk;
    private boolean cancelled;

    public JobsChunkChangeEvent(Player player, Chunk oldChunk, Chunk newChunk) {
	this.player = player;
	this.oldChunk = oldChunk;
	this.newChunk = newChunk;
    }

    public Player getPlayer() {
	return this.player;
    }

    public Chunk getOldChunk() {
	return this.oldChunk;
    }

    public Chunk getNewChunk() {
	return this.newChunk;
    }

    public boolean isCancelled() {
	return cancelled;
    }

    public void setCancelled(boolean cancel) {
	cancelled = cancel;
    }

    public HandlerList getHandlers() {
	return handlers;
    }

    public static HandlerList getHandlerList() {
	return handlers;
    }
}