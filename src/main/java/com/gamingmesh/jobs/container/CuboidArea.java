package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Container.CuboidArea.ChunkRef;

public class CuboidArea {
    protected Vector highPoints;
    protected Vector lowPoints;
    protected String worldName;
    protected World world;

    protected CuboidArea() {
    }

    public CuboidArea(Location startLoc, Location endLoc) {
        modifyVectors(startLoc.toVector(), endLoc.toVector());
        this.worldName = startLoc.getWorld().getName();
    }

    public CuboidArea(String worldName, Vector startLoc, Vector endLoc) {
        modifyVectors(startLoc, endLoc);
        this.worldName = worldName;
    }

    private void modifyVectors(Vector startLoc, Vector endLoc) {

        int highx = Math.max(startLoc.getBlockX(), endLoc.getBlockX());
        int lowx = Math.min(startLoc.getBlockX(), endLoc.getBlockX());

        int highy = Math.max(startLoc.getBlockY(), endLoc.getBlockY());
        int lowy = Math.min(startLoc.getBlockY(), endLoc.getBlockY());

        int highz = Math.max(startLoc.getBlockZ(), endLoc.getBlockZ());
        int lowz = Math.min(startLoc.getBlockZ(), endLoc.getBlockZ());

        this.highPoints = new Vector(highx, highy, highz);
        this.lowPoints = new Vector(lowx, lowy, lowz);
    }

    public long getSize() {
        int xsize = (getHighPoint().getBlockX() - getLowPoint().getBlockX()) + 1;
        int zsize = (getHighPoint().getBlockZ() - getLowPoint().getBlockZ()) + 1;
        int ysize = (getHighPoint().getBlockY() - getLowPoint().getBlockY()) + 1;
        return (long) xsize * ysize * zsize;
    }

    public int getXSize() {
        return (getHighPoint().getBlockX() - getLowPoint().getBlockX()) + 1;
    }

    public int getYSize() {
        return (getHighPoint().getBlockY() - getLowPoint().getBlockY()) + 1;
    }

    public int getZSize() {
        return (getHighPoint().getBlockZ() - getLowPoint().getBlockZ()) + 1;
    }

    public Location getHighLoc() {
        return getHighPoint().toLocation(getWorld());
    }

    public Location getLowLoc() {
        return getLowPoint().toLocation(getWorld());
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        if (world != null)
            return world;
        world = CMIWorld.getWorld(worldName);
        return world;
    }

    public List<ChunkRef> getChunks() {
        List<ChunkRef> chunks = new ArrayList<>();

        int lowX = ChunkRef.getChunkCoord(this.getLowPoint().getBlockX());
        int lowZ = ChunkRef.getChunkCoord(this.getLowPoint().getBlockZ());
        int highX = ChunkRef.getChunkCoord(this.getHighPoint().getBlockX());
        int highZ = ChunkRef.getChunkCoord(this.getHighPoint().getBlockZ());

        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                chunks.add(new ChunkRef(x, z));
            }
        }
        return chunks;
    }

    public Vector getLowPoint() {
        return lowPoints;
    }

    public Vector getHighPoint() {
        return highPoints;
    }
}
