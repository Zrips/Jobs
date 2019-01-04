package com.gamingmesh.jobs.stuff;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;
import com.gamingmesh.jobs.container.ScoreboardInfo;

public class CMIScoreboardManager {

    private ConcurrentHashMap<UUID, ScoreboardInfo> timerMap = new ConcurrentHashMap<>();
    private Jobs plugin;

    public CMIScoreboardManager(Jobs plugin) {
	this.plugin = plugin;
    }

    private void RunScheduler() {
	Iterator<Entry<UUID, ScoreboardInfo>> MeinMapIter = timerMap.entrySet().iterator();
	while (MeinMapIter.hasNext()) {
	    Entry<UUID, ScoreboardInfo> Map = MeinMapIter.next();

	    if (System.currentTimeMillis() > Map.getValue().getTime() + (Jobs.getGCManager().ToplistInScoreboardInterval * 1000)) {
		Player player = Bukkit.getPlayer(Map.getKey());
		if (player != null) {
		    removeScoreBoard(player);
		    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		    if (Map.getValue().getObj() != null) {
			Objective obj = player.getScoreboard().getObjective(Map.getValue().getObj().getName());
			if (obj != null)
			    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		    }
		}
		timerMap.remove(Map.getKey());
	    }
	}

	if (timerMap.size() > 0)
	    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		@Override
		public void run() {
		    RunScheduler();
		    return;
		}
	    }, 20L);
	return;
    }

    public void addNew(Player player) {
	Scoreboard scoreBoard = player.getScoreboard();
	timerMap.put(player.getUniqueId(), new ScoreboardInfo(scoreBoard, DisplaySlot.SIDEBAR));
	RunScheduler();
    }

    private final String objName = "CMIScoreboard";

    public void removeScoreBoard(Player player) {
	try {

	    Class<?> boardClass = getNMSClass("Scoreboard");
	    Object boards = boardClass.getConstructor().newInstance();

	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Class<?> p0 = getNMSClass("PacketPlayOutScoreboardObjective");
		Constructor<?> p00 = p0.getConstructor();
		Object pp1 = p00.newInstance();
		setField(pp1, "a", player.getName());
		setField(pp1, "d", 1);
		sendPacket(player, pp1);
	    } else {
		Method m = boards.getClass().getMethod("registerObjective", String.class, getNMSClass("IScoreboardCriteria"));

		Class<?> IScoreboardCriterias = getNMSClass("ScoreboardBaseCriteria");
		Constructor<?> IScoreboardCriteriasConst = IScoreboardCriterias.getConstructor(String.class);
		Object IScoreboardCriteria = IScoreboardCriteriasConst.newInstance("JobsDummy");

		Object obj = m.invoke(boards, objName, IScoreboardCriteria);
		Class<?> p1 = getNMSClass("PacketPlayOutScoreboardObjective");
		Constructor<?> p11 = p1.getConstructor(obj.getClass(), int.class);
		Object pp1 = p11.newInstance(obj, 1);
		sendPacket(player, pp1);
	    }
	} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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

    public void setScoreBoard(Player player, String displayName, List<String> lines) {
	removeScoreBoard(player);
	try {
	    Class<?> boardClass = getNMSClass("Scoreboard");
	    Object boards = boardClass.getConstructor().newInstance();
	    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
		Class<?> enums = getNMSClass("IScoreboardCriteria$EnumScoreboardHealthDisplay");

		Class<?> p0 = getNMSClass("PacketPlayOutScoreboardObjective");
		Constructor<?> p00 = p0.getConstructor();
		Object pp1 = p00.newInstance();
		setField(pp1, "a", player.getName());
		setField(pp1, "d", 0);
		Object chatComponentText = getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', displayName));
		setField(pp1, "b", chatComponentText);
		setField(pp1, "c", enums.getEnumConstants()[1]);
		sendPacket(player, pp1);

		Object d0 = getNMSClass("PacketPlayOutScoreboardDisplayObjective").getConstructor().newInstance();
		setField(d0, "a", 1);
		setField(d0, "b", player.getName());
		sendPacket(player, d0);

		for (int i = 0; i < 15; i++) {
		    if (i >= lines.size())
			break;
		    String ln = ChatColor.translateAlternateColorCodes('&', lines.get(i));
		    Class<?> PacketPlayOutScoreboardScoreClass = getNMSClass("PacketPlayOutScoreboardScore");
		    Constructor<?> PacketPlayOutScoreboardScoreConstructor = PacketPlayOutScoreboardScoreClass.getConstructor();
		    Object PacketPlayOutScoreboardScore = PacketPlayOutScoreboardScoreConstructor.newInstance();
		    Class<?> aenums = getNMSClass("ScoreboardServer$Action");

		    setField(PacketPlayOutScoreboardScore, "a", ln);
		    setField(PacketPlayOutScoreboardScore, "b", player.getName());
		    setField(PacketPlayOutScoreboardScore, "c", 15 - i);
		    setField(PacketPlayOutScoreboardScore, "d", aenums.getEnumConstants()[0]);
		    sendPacket(player, PacketPlayOutScoreboardScore);
		}
	    } else {

		Method m = boards.getClass().getMethod("registerObjective", String.class, getNMSClass("IScoreboardCriteria"));

		Class<?> IScoreboardCriterias = getNMSClass("ScoreboardBaseCriteria");
		Constructor<?> IScoreboardCriteriasConst = IScoreboardCriterias.getConstructor(String.class);
		Object IScoreboardCriteria = IScoreboardCriteriasConst.newInstance("JobsDummy");

		Object obj = m.invoke(boards, objName, IScoreboardCriteria);

		Method mm = obj.getClass().getMethod("setDisplayName", String.class);
		mm.invoke(obj, ChatColor.translateAlternateColorCodes('&', displayName));

		Class<?> p1 = getNMSClass("PacketPlayOutScoreboardObjective");
		Constructor<?> p11 = p1.getConstructor(obj.getClass(), int.class);
		Object pp1 = p11.newInstance(obj, 1);
		sendPacket(player, pp1);

		Class<?> p2 = getNMSClass("PacketPlayOutScoreboardObjective");
		Constructor<?> p12 = p2.getConstructor(obj.getClass(), int.class);
		Object pp2 = p12.newInstance(obj, 0);
		sendPacket(player, pp2);

		Class<?> packetClass = getNMSClass("PacketPlayOutScoreboardDisplayObjective");
		Constructor<?> packetConstructor = packetClass.getConstructor(int.class, getNMSClass("ScoreboardObjective"));
		Object packet = packetConstructor.newInstance(1, obj);
		sendPacket(player, packet);

		for (int i = 0; i < 15; i++) {
		    if (i >= lines.size())
			break;

		    String ln = ChatColor.translateAlternateColorCodes('&', lines.get(i));
		    Class<?> ScoreboardScoreClass = getNMSClass("ScoreboardScore");
		    Constructor<?> packetConstructor2 = ScoreboardScoreClass.getConstructor(getNMSClass("Scoreboard"), getNMSClass("ScoreboardObjective"), String.class);
		    Object packet2 = packetConstructor2.newInstance(boards, obj, ln);
		    Method mc = packet2.getClass().getMethod("setScore", int.class);
		    mc.invoke(packet2, 15 - i);

		    Class<?> PacketPlayOutScoreboardScoreClass = getNMSClass("PacketPlayOutScoreboardScore");
		    Constructor<?> PacketPlayOutScoreboardScoreConstructor = PacketPlayOutScoreboardScoreClass.getConstructor(getNMSClass("ScoreboardScore"));
		    Object PacketPlayOutScoreboardScore = PacketPlayOutScoreboardScoreConstructor.newInstance(packet2);

		    sendPacket(player, PacketPlayOutScoreboardScore);

		}
	    }
	} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	    e.printStackTrace();
	}
    }

    private static void sendPacket(Player player, Object packet) {
	Method sendPacket;
	try {
	    sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
	    sendPacket.invoke(getConnection(player), packet);
	} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
	    e.printStackTrace();
	}
    }

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
	return Class.forName("net.minecraft.server." + Jobs.getVersionCheckManager().getVersion() + "." + nmsClassString);
    }

    private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	Method getHandle = player.getClass().getMethod("getHandle");
	Object nmsPlayer = getHandle.invoke(player);
	Field conField = nmsPlayer.getClass().getField("playerConnection");
	Object con = conField.get(nmsPlayer);
	return con;
    }
}
