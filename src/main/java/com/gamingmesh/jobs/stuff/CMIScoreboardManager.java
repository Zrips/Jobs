package com.gamingmesh.jobs.stuff;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.CMIChatColor;
import com.gamingmesh.jobs.CMILib.CMIReflections;
import com.gamingmesh.jobs.CMILib.Version;
import com.gamingmesh.jobs.container.ScoreboardInfo;

public class CMIScoreboardManager {

    private ConcurrentHashMap<UUID, ScoreboardInfo> timerMap = new ConcurrentHashMap<>();

    private void runScheduler() {
	Iterator<Entry<UUID, ScoreboardInfo>> meinMapIter = timerMap.entrySet().iterator();
	while (meinMapIter.hasNext()) {
	    Entry<UUID, ScoreboardInfo> map = meinMapIter.next();

	    if (System.currentTimeMillis() > map.getValue().getTime() + (Jobs.getGCManager().ToplistInScoreboardInterval * 1000)) {
		Player player = Bukkit.getPlayer(map.getKey());
		if (player != null) {
		    removeScoreBoard(player);
		    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

		    if (map.getValue().getObj() != null) {
			try {
			    Objective obj = player.getScoreboard().getObjective(map.getValue().getObj().getName());
			    if (obj != null)
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			} catch (IllegalStateException e) {
			}
		    }
		}

		timerMap.remove(map.getKey());
	    }
	}

	if (timerMap.size() > 0)
	    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Jobs.getInstance(), this::runScheduler, 20L);
    }

    public void addNew(Player player) {
	timerMap.put(player.getUniqueId(), new ScoreboardInfo(player.getScoreboard(), DisplaySlot.SIDEBAR));
	runScheduler();
    }

    private final String objName = "CMIScoreboard";

    public void removeScoreBoard(Player player) {
	try {
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Object pp1 = getNMSClass("PacketPlayOutScoreboardObjective").getConstructor().newInstance();
		setField(pp1, "a", player.getName());
		setField(pp1, "d", 1);
		sendPacket(player, pp1);
	    } else {
		Object boards = getNMSClass("Scoreboard").getConstructor().newInstance();

		Object obj = boards.getClass().getMethod("registerObjective", String.class,
			getNMSClass("IScoreboardCriteria")).invoke(boards, objName,
				getNMSClass("ScoreboardBaseCriteria").getConstructor(String.class).newInstance("JobsDummy"));
		sendPacket(player, getNMSClass("PacketPlayOutScoreboardObjective").getConstructor(obj.getClass(), int.class).newInstance(obj, 1));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void setScoreBoard(Player player, String displayName, List<String> lines) {
	removeScoreBoard(player);

	try {
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Object pp1 = getNMSClass("PacketPlayOutScoreboardObjective").getConstructor().newInstance();
		setField(pp1, "a", player.getName());
		setField(pp1, "d", 0);
		setField(pp1, "b", getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(CMIChatColor.translate(displayName)));
		setField(pp1, "c", getNMSClass("IScoreboardCriteria$EnumScoreboardHealthDisplay").getEnumConstants()[1]);
		sendPacket(player, pp1);

		Object d0 = getNMSClass("PacketPlayOutScoreboardDisplayObjective").getConstructor().newInstance();
		setField(d0, "a", 1);
		setField(d0, "b", player.getName());
		sendPacket(player, d0);

		for (int i = 0; i < 15; i++) {
		    if (i >= lines.size())
			break;
		    Object PacketPlayOutScoreboardScore = getNMSClass("PacketPlayOutScoreboardScore").getConstructor().newInstance();

		    setField(PacketPlayOutScoreboardScore, "a", CMIChatColor.translate(lines.get(i)));
		    setField(PacketPlayOutScoreboardScore, "b", player.getName());
		    setField(PacketPlayOutScoreboardScore, "c", 15 - i);
		    setField(PacketPlayOutScoreboardScore, "d", getNMSClass("ScoreboardServer$Action").getEnumConstants()[0]);
		    sendPacket(player, PacketPlayOutScoreboardScore);
		}
	    } else {
		Object boards = getNMSClass("Scoreboard").getConstructor().newInstance();
		Object obj = boards.getClass().getMethod("registerObjective", String.class, getNMSClass("IScoreboardCriteria"))
			.invoke(boards, objName, getNMSClass("ScoreboardBaseCriteria").getConstructor(String.class).newInstance("JobsDummy"));

		obj.getClass().getMethod("setDisplayName", String.class).invoke(obj, CMIChatColor.translate(displayName));

		sendPacket(player, getNMSClass("PacketPlayOutScoreboardObjective").getConstructor(obj.getClass(), int.class).newInstance(obj, 1));
		sendPacket(player, getNMSClass("PacketPlayOutScoreboardObjective").getConstructor(obj.getClass(), int.class).newInstance(obj, 0));

		sendPacket(player, getNMSClass("PacketPlayOutScoreboardDisplayObjective").getConstructor(int.class,
			getNMSClass("ScoreboardObjective")).newInstance(1, obj));

		for (int i = 0; i < 15; i++) {
		    if (i >= lines.size())
			break;

		    Object packet2 = getNMSClass("ScoreboardScore").getConstructor(getNMSClass("Scoreboard"),
		        getNMSClass("ScoreboardObjective"), String.class).newInstance(boards, obj, CMIChatColor.translate(lines.get(i)));
		    packet2.getClass().getMethod("setScore", int.class).invoke(packet2, 15 - i);

		    sendPacket(player, getNMSClass("PacketPlayOutScoreboardScore").getConstructor(getNMSClass("ScoreboardScore")).newInstance(packet2));
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void setField(Object edit, String fieldName, Object value) {
	try {
	    Field field = edit.getClass().getDeclaredField(fieldName);
	    field.setAccessible(true);
	    field.set(edit, value);
	} catch (NoSuchFieldException | IllegalAccessException e) {
	    e.printStackTrace();
	}
    }

    private static void sendPacket(Player player, Object packet) {
	try {
	    getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet")).invoke(getConnection(player), packet);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
	return CMIReflections.getMinecraftClass(nmsClassString);
    }

    private static Object getConnection(Player player) throws Exception {
	Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
	return nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
    }
}
