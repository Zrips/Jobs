package com.gamingmesh.jobs.config;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.BlockProtection;
import com.gamingmesh.jobs.container.DBAction;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;

public class BlockProtectionManager {

    private HashMap<World, HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>>> map =
	new HashMap<World, HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>>>();

    public Long timer = 0L;

    public HashMap<World, HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>>> getMap() {
	return this.map;
    }

    public int getSize() {
	int i = 0;
	for (Entry<World, HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>>> worlds : map.entrySet()) {
	    for (Entry<String, HashMap<String, HashMap<Vector, BlockProtection>>> regions : worlds.getValue().entrySet()) {
		for (Entry<String, HashMap<Vector, BlockProtection>> chunks : regions.getValue().entrySet()) {
		    i += chunks.getValue().size();
		}
	    }
	}
	return i;
    }

    public void add(Block block, boolean paid) {
	add(block, -1L, paid);
    }

    public void add(Block block) {
	add(block, -1L, true);
    }

    public void add(Block block, Long time, boolean paid) {
	add(block.getLocation(), time, paid);
    }

    public void add(Block block, Integer cd) {
	add(block, cd, true);
    }

    public void add(Block block, Integer cd, boolean paid) {
	if (cd == null)
	    return;
	if (cd != -1)
	    add(block, System.currentTimeMillis() + (cd * 1000), paid);
	else
	    add(block, paid);
    }

    public void add(Block block, Long time) {
	add(block.getLocation(), time, true);
    }

    public void add(Location loc, Long time) {
	add(loc, time, true);
    }

    public BlockProtection add(Location loc, Long time, boolean paid) {
	Vector v = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>> regions = map.get(loc.getWorld());
	if (regions == null)
	    regions = new HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>>();
	String region = locToRegion(loc);
	HashMap<String, HashMap<Vector, BlockProtection>> chunks = regions.get(region);
	if (chunks == null)
	    chunks = new HashMap<String, HashMap<Vector, BlockProtection>>();
	String chunk = locToChunk(loc);
	HashMap<Vector, BlockProtection> Bpm = chunks.get(chunk);
	if (Bpm == null)
	    Bpm = new HashMap<Vector, BlockProtection>();

	BlockProtection Bp = Bpm.get(v);

	if (Bp == null)
	    Bp = new BlockProtection(DBAction.INSERT);
	else
	    Bp.setAction(DBAction.UPDATE);

	Bp.setPaid(paid);
	Bp.setTime(time);
	Bpm.put(v, Bp);
	chunks.put(chunk, Bpm);
	regions.put(region, chunks);
	map.put(loc.getWorld(), regions);
	return Bp;
    }

    public BlockProtection remove(Block block) {
	return remove(block.getLocation());
    }

    public BlockProtection remove(Location loc) {
	HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>> world = map.get(loc.getWorld());
	if (world == null)
	    return null;
	HashMap<String, HashMap<Vector, BlockProtection>> region = world.get(locToRegion(loc));
	if (region == null)
	    return null;
	HashMap<Vector, BlockProtection> chunk = region.get(locToChunk(loc));
	if (chunk == null)
	    return null;
	Vector v = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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
	HashMap<String, HashMap<String, HashMap<Vector, BlockProtection>>> world = map.get(loc.getWorld());
	if (world == null)
	    return null;
	HashMap<String, HashMap<Vector, BlockProtection>> region = world.get(locToRegion(loc));
	if (region == null)
	    return null;
	HashMap<Vector, BlockProtection> chunk = region.get(locToChunk(loc));
	if (chunk == null)
	    return null;
	Vector v = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	BlockProtection Bp = chunk.get(v);
	if (Bp == null)
	    return null;
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

    @SuppressWarnings("deprecation")
    public Integer getBlockDelayTime(Block block) {
    	if(Jobs.getGCManager().useGlobalTimer){
    		return Jobs.getGCManager().globalblocktimer;
    	}else{
    		return Jobs.getRestrictedBlockManager().restrictedBlocksTimer.get(block.getTypeId());
    	}
    }

    @SuppressWarnings("deprecation")
    public boolean checkVegybreak(Block block, Player player) {
	if (!Jobs.getRestrictedBlockManager().restrictedBlocksTimer.containsKey(block.getTypeId()))
	    return false;
	if (CheckVegyTimer(block, Jobs.getRestrictedBlockManager().restrictedBlocksTimer.get(block.getTypeId()), player))
	    return true;
	return false;
    }

    public boolean CheckVegyTimer(Block block, int time, Player player) {
	long currentTime = System.currentTimeMillis();
	if (!block.hasMetadata(JobsPaymentListener.VegyMetadata))
	    return false;
	long BlockTime = block.getMetadata(JobsPaymentListener.VegyMetadata).get(0).asLong();

	if (currentTime >= BlockTime + time * 1000) {
	    return false;
	}

	int sec = Math.round((((BlockTime + time * 1000) - currentTime)) / 1000);

	Jobs.getActionBar().send(player, Jobs.getLanguage().getMessage("message.blocktimer", "[time]", sec));
	return true;
    }

}
