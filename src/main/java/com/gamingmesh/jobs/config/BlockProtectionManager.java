package com.gamingmesh.jobs.config;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIMaterial;
import com.gamingmesh.jobs.container.BlockProtection;
import com.gamingmesh.jobs.container.DBAction;

public class BlockProtectionManager {

    private HashMap<World, HashMap<String, HashMap<String, HashMap<String, BlockProtection>>>> map = new HashMap<>();
    private ConcurrentHashMap<World, ConcurrentHashMap<String, BlockProtection>> tempCache = new ConcurrentHashMap<>();

    public HashMap<World, HashMap<String, HashMap<String, HashMap<String, BlockProtection>>>> getMap() {
	return this.map;
    }

    public int getSize() {
	int i = 0;
	for (Entry<World, HashMap<String, HashMap<String, HashMap<String, BlockProtection>>>> worlds : map.entrySet()) {
	    for (Entry<String, HashMap<String, HashMap<String, BlockProtection>>> regions : worlds.getValue().entrySet()) {
		for (Entry<String, HashMap<String, BlockProtection>> chunks : regions.getValue().entrySet()) {
		    i += chunks.getValue().size();
		}
	    }
	}
	return i;
    }

    public void add(Block block, Integer cd) {
	add(block, cd, true);
    }

    public void add(Block block, Integer cd, boolean paid) {
	add(block.getLocation(), cd, paid);
    }

    public void add(Location loc, Integer cd) {
	add(loc, cd, true);
    }

    public void add(Location loc, Integer cd, boolean paid) {
	if (cd == null)
	    return;
	if (cd != -1)
	    addP(loc, System.currentTimeMillis() + (cd * 1000), paid, true);
	else
	    addP(loc, -1L, paid, true);
    }

    public BlockProtection addP(Location loc, Long time, boolean paid, boolean cache) {
	String v = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();

	HashMap<String, HashMap<String, HashMap<String, BlockProtection>>> regions = map.getOrDefault(loc.getWorld(), new HashMap<>());

	String region = locToRegion(loc);
	HashMap<String, HashMap<String, BlockProtection>> chunks = regions.getOrDefault(region, new HashMap<>());

	String chunk = locToChunk(loc);
	HashMap<String, BlockProtection> Bpm = chunks.getOrDefault(chunk, new HashMap<>());
	BlockProtection Bp = Bpm.get(v);

	if (Bp == null)
	    Bp = new BlockProtection(DBAction.INSERT, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	else
	    Bp.setAction(DBAction.UPDATE);

	Bp.setPaid(paid);
	Bp.setTime(time);
	Bpm.put(v, Bp);
	chunks.put(chunk, Bpm);
	regions.put(region, chunks);
	map.put(loc.getWorld(), regions);
	if (cache)
	    addToCache(loc, Bp);
	return Bp;
    }

    private void addToCache(Location loc, BlockProtection Bp) {
	if (!Jobs.getGCManager().useBlockProtection)
	    return;
	String v = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	ConcurrentHashMap<String, BlockProtection> locations = tempCache.get(loc.getWorld());
	if (locations == null) {
	    locations = new ConcurrentHashMap<String, BlockProtection>();
	    tempCache.put(loc.getWorld(), locations);
	}

	locations.put(v, Bp);

//	if (locations.size() > 10) {
//	    Jobs.getJobsDAO().saveBlockProtection(loc.getWorld().getName(), new HashMap<String, BlockProtection>(locations));
//	    locations.clear();
//	}
    }

    public void saveCache() {
	if (!Jobs.getGCManager().useBlockProtection)
	    return;
	for (Entry<World, ConcurrentHashMap<String, BlockProtection>> one : tempCache.entrySet()) {
	    Jobs.getJobsDAO().saveBlockProtection(one.getKey().getName(), one.getValue());
	}
	tempCache.clear();
    }

    public BlockProtection remove(Block block) {
	return remove(block.getLocation());
    }

    public BlockProtection remove(Location loc) {
	HashMap<String, HashMap<String, HashMap<String, BlockProtection>>> world = map.get(loc.getWorld());
	if (world == null)
	    return null;
	HashMap<String, HashMap<String, BlockProtection>> region = world.get(locToRegion(loc));
	if (region == null)
	    return null;
	HashMap<String, BlockProtection> chunk = region.get(locToChunk(loc));
	if (chunk == null)
	    return null;
	String v = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	BlockProtection bp = chunk.get(v);
	if (bp != null)
	    bp.setAction(DBAction.DELETE);
	return bp;
    }

    public Long getTime(Block block) {
	return getTime(block.getLocation());
    }

    public Long getTime(Location loc) {
	BlockProtection Bp = getBp(loc);
	if (Bp == null)
	    return null;
	return Bp.getTime();
    }

    public BlockProtection getBp(Location loc) {
	HashMap<String, HashMap<String, HashMap<String, BlockProtection>>> world = map.get(loc.getWorld());
	if (world == null)
	    return null;
	HashMap<String, HashMap<String, BlockProtection>> region = world.get(locToRegion(loc));
	if (region == null)
	    return null;
	HashMap<String, BlockProtection> chunk = region.get(locToChunk(loc));
	if (chunk == null)
	    return null;
	String v = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	BlockProtection Bp = chunk.get(v);
	return Bp;
    }

    private static String locToChunk(Location loc) {
	int x = (int) Math.floor(loc.getBlockX() / 16);
	int z = (int) Math.floor(loc.getBlockZ() / 16);
	return x + ":" + z;
    }

    private static String locToRegion(Location loc) {
	int x = (int) Math.floor(loc.getBlockX() / 16);
	int z = (int) Math.floor(loc.getBlockZ() / 16);
	String reg = (int) Math.floor(x / 32) + ":" + (int) Math.floor(z / 32);
	return reg;
    }

    public Integer getBlockDelayTime(Block block) {
	Integer time = Jobs.getRestrictedBlockManager().restrictedBlocksTimer.get(CMIMaterial.get(block));
	if (time == null && Jobs.getGCManager().useGlobalTimer) {
	    time = Jobs.getGCManager().globalblocktimer;
	}
	return time;
    }

    public boolean isInBp(Block block) {
	return Jobs.getRestrictedBlockManager().restrictedBlocksTimer.get(CMIMaterial.get(block)) != null;
    }
}
