package com.gamingmesh.jobs.stuff;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.gamingmesh.jobs.i18n.Language;

/**
*
* @author hamzaxx
*/
public class ActionBar {
    private static int cleanVersion = 1820;
    private static String version = "";
    private static Object packet;
    private static Method getHandle;
    private static Method sendPacket;
    private static Field playerConnection;
    private static Class<?> nmsChatSerializer;
    private static Class<?> nmsIChatBaseComponent;
    private static Class<?> packetType;

    static {
	try {
	    version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	    // Translating version to integer for simpler use
	    try {
		cleanVersion = Integer.parseInt(version.replace("v", "").replace("V", "").replace("_", "").replace("r", "").replace("R", ""));
	    } catch (NumberFormatException e) {
		// Fail save if it for some reason can't translate version to integer
		if (version.contains("v1_7"))
		    cleanVersion = 1700;
		if (version.contains("v1_6"))
		    cleanVersion = 1600;
		if (version.contains("v1_5"))
		    cleanVersion = 1500;
		if (version.contains("v1_4"))
		    cleanVersion = 1400;
		if (version.contains("v1_8_R1"))
		    cleanVersion = 1810;
		if (version.contains("v1_8_R2"))
		    cleanVersion = 1820;
		if (version.contains("v1_8_R3"))
		    cleanVersion = 1830;
	    }

	    if (cleanVersion < 1000)
		cleanVersion = cleanVersion * 10;

	    packetType = Class.forName(getPacketPlayOutChat());
	    Class<?> typeCraftPlayer = Class.forName(getCraftPlayerClasspath());
	    Class<?> typeNMSPlayer = Class.forName(getNMSPlayerClasspath());
	    Class<?> typePlayerConnection = Class.forName(getPlayerConnectionClasspath());
	    nmsChatSerializer = Class.forName(getChatSerializerClasspath());
	    nmsIChatBaseComponent = Class.forName(getIChatBaseComponentClasspath());
	    getHandle = typeCraftPlayer.getMethod("getHandle");
	    playerConnection = typeNMSPlayer.getField("playerConnection");
	    sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName(getPacketClasspath()));

	} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}
    }

    public static void ShowActionBar(BufferedPayment payment) {
	String playername = payment.getOfflinePlayer().getName();
	if (!Jobs.actionbartoggle.containsKey(playername) && ConfigManager.getJobsConfiguration().JobsToggleEnabled)
	    Jobs.actionbartoggle.put(playername, true);

	if (playername == null)
	    return;

	if (!Jobs.actionbartoggle.containsKey(playername))
	    return;

	Boolean show = Jobs.actionbartoggle.get(playername);
	Player abp = (Player) payment.getOfflinePlayer();
	if (abp != null && show) {
	    String Message = Language.getMessage("command.toggle.output.paid");
	    Message = Message.replace("[amount]", String.format("%.2f", payment.getAmount()));
	    Message = Message.replace("[exp]", String.format("%.2f", payment.getExp()));
	    ActionBar.send(abp, ChatColor.GREEN + Message);
	}

    }

    public static void send(Player receivingPacket, String msg) {
	try {
	    if (msg == null || nmsChatSerializer == null)
		return;

	    if (cleanVersion < 1800) {
		receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		return;
	    }

	    Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
	    if (cleanVersion > 1800) {
		packet = packetType.getConstructor(nmsIChatBaseComponent, byte.class).newInstance(serialized, (byte) 2);
	    } else {
		packet = packetType.getConstructor(nmsIChatBaseComponent, int.class).newInstance(serialized, 2);
	    }
	    Object player = getHandle.invoke(receivingPacket);
	    Object connection = playerConnection.get(player);
	    sendPacket.invoke(connection, packet);
	} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}

	try {
	    Object player = getHandle.invoke(receivingPacket);
	    Object connection = playerConnection.get(player);
	    sendPacket.invoke(connection, packet);
	} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}
    }

    private static String getCraftPlayerClasspath() {
	return "org.bukkit.craftbukkit." + version + ".entity.CraftPlayer";
    }

    private static String getPlayerConnectionClasspath() {
	return "net.minecraft.server." + version + ".PlayerConnection";
    }

    private static String getNMSPlayerClasspath() {
	return "net.minecraft.server." + version + ".EntityPlayer";
    }

    private static String getPacketClasspath() {
	return "net.minecraft.server." + version + ".Packet";
    }

    private static String getIChatBaseComponentClasspath() {
	return "net.minecraft.server." + version + ".IChatBaseComponent";
    }

    private static String getChatSerializerClasspath() {

	if (cleanVersion < 1820) {
	    return "net.minecraft.server." + version + ".ChatSerializer";
	} else {
	    return "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer";// 1_8_R2 moved to IChatBaseComponent
	}
    }

    private static String getPacketPlayOutChat() {
	return "net.minecraft.server." + version + ".PacketPlayOutChat";
    }
}