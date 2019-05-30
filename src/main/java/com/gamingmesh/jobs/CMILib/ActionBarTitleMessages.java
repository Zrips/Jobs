package com.gamingmesh.jobs.CMILib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;

public class ActionBarTitleMessages {
    private static Object packet;
    private static Method getHandle;
    private static Method sendPacket;
    private static Field playerConnection;
    private static Class<?> nmsChatSerializer;
    private static Class<?> nmsIChatBaseComponent;
    private static Class<?> packetType;

    private static Constructor<?> nmsPacketPlayOutTitle;
    private static Class<?> enumTitleAction;
    private static Method fromString;
    private static boolean simpleTitleMessages = false;

    private static Class<?> ChatMessageclz;
    private static Class<?> sub;
    private static Object[] consts;

    static {
	if (Version.getCurrent().isHigher(Version.v1_7_R4)) {
	    try {
		packetType = Class.forName(getPacketPlayOutChat());
		Class<?> typeCraftPlayer = Class.forName(getCraftPlayerClasspath());
		Class<?> typeNMSPlayer = Class.forName(getNMSPlayerClasspath());
		Class<?> typePlayerConnection = Class.forName(getPlayerConnectionClasspath());
		nmsChatSerializer = Class.forName(getChatSerializerClasspath());
		nmsIChatBaseComponent = Class.forName(getIChatBaseComponentClasspath());
		getHandle = typeCraftPlayer.getMethod("getHandle");
		playerConnection = typeNMSPlayer.getField("playerConnection");
		sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName(getPacketClasspath()));

		if (Version.isCurrentHigher(Version.v1_11_R1)) {
		    ChatMessageclz = Class.forName(getChatMessageTypeClasspath());
		    consts = ChatMessageclz.getEnumConstants();
		    sub = consts[2].getClass();
		}

	    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
		Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	    }
	    // Title
	    try {
		Class<?> typePacketPlayOutTitle = Class.forName(getPacketPlayOutTitleClasspath());
		enumTitleAction = Class.forName(getEnumTitleActionClasspath());
		nmsPacketPlayOutTitle = typePacketPlayOutTitle.getConstructor(enumTitleAction, nmsIChatBaseComponent);
		fromString = Class.forName(getClassMessageClasspath()).getMethod("fromString", String.class);
	    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
		simpleTitleMessages = true;
	    }
	}
    }

    public void send(CommandSender receivingPacket, String msg) {
	if (receivingPacket instanceof Player)
	    send((Player) receivingPacket, msg);
	else
	    receivingPacket.sendMessage(msg);
    }

    public void send(Player receivingPacket, String msg) {
	if (receivingPacket == null)
	    return;
	if (!receivingPacket.isOnline())
	    return;
	if (msg == null)
	    return;
	try {
	    if (!Version.getCurrent().isHigher(Version.v1_7_R4) || nmsChatSerializer == null) {
		receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		return;
	    }

	    Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
	    if (Version.isCurrentHigher(Version.v1_11_R1))
		packet = packetType.getConstructor(nmsIChatBaseComponent, sub).newInstance(serialized, consts[2]);
	    else if (Version.isCurrentHigher(Version.v1_7_R4)) {
		packet = packetType.getConstructor(nmsIChatBaseComponent, byte.class).newInstance(serialized, (byte) 2);
	    } else {
		packet = packetType.getConstructor(nmsIChatBaseComponent, int.class).newInstance(serialized, 2);
	    }
	    Object player = getHandle.invoke(receivingPacket);
	    Object connection = playerConnection.get(player);
	    sendPacket.invoke(connection, packet);
	} catch (Throwable t) {
//	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}

	try {
	    Object player = getHandle.invoke(receivingPacket);
	    Object connection = playerConnection.get(player);
	    sendPacket.invoke(connection, packet);
	} catch (Throwable t) {
//	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}
    }

    public static void sendTitle(final Player receivingPacket, final Object title, final Object subtitle) {
	sendTitle(receivingPacket, title, subtitle, 0, 20, 20);
    }

    public static void sendTitle(final Player receivingPacket, final Object title, final Object subtitle, final int fadeIn, final int keep, final int fadeOut) {
	Bukkit.getScheduler().runTaskAsynchronously(Jobs.getInstance(), () -> {
	    String t = title == null ? null : CMIChatColor.translateAlternateColorCodes((String) title);
	    String s = subtitle == null ? null : CMIChatColor.translateAlternateColorCodes((String) subtitle);

	    if (simpleTitleMessages) {
		receivingPacket.sendMessage(t);
		receivingPacket.sendMessage(s);
		return;
	    }
	    try {
		switch (Version.getCurrent()) {
		case v1_9_R1:
		case v1_9_R2:
		case v1_10_R1:
		case v1_11_R1:
		    receivingPacket.sendTitle(t, s);
		    break;
		case v1_12_R1:
		case v1_13_R1:
		case v1_13_R2:
		case v1_14_R1:
		case v1_14_R2:
		case v1_15_R1:
		case v1_15_R2:
		    receivingPacket.sendTitle(t, s, fadeIn, keep, fadeOut);
		    break;
		case v1_7_R1:
		case v1_7_R2:
		case v1_7_R3:
		case v1_7_R4:
		case v1_8_R1:
		case v1_8_R2:
		case v1_8_R3:
		    if (title != null) {
			Object packetTitle = nmsPacketPlayOutTitle.newInstance(enumTitleAction.getField("TITLE").get(null),
			    ((Object[]) fromString.invoke(null, t))[0]);
			sendPacket(receivingPacket, packetTitle);
		    }
		    if (subtitle != null) {
			if (title == null) {
			    Object packetTitle = nmsPacketPlayOutTitle.newInstance(enumTitleAction.getField("TITLE").get(null), ((Object[]) fromString.invoke(null, ""))[0]);
			    sendPacket(receivingPacket, packetTitle);
			}
			Object packetSubtitle = nmsPacketPlayOutTitle.newInstance(enumTitleAction.getField("SUBTITLE").get(null),
			    ((Object[]) fromString.invoke(null, s))[0]);
			sendPacket(receivingPacket, packetSubtitle);
		    }

		    break;
		default:
		    break;
		}

	    } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException ex) {
		simpleTitleMessages = true;
		Bukkit.getLogger().log(Level.SEVERE, "Your server can't fully support title messages. They will be shown in chat instead.");
	    }
	    return;
	});
    }

    private static void sendPacket(Player player, Object packet) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	Object handle = getHandle.invoke(player);
	Object connection = playerConnection.get(handle);
	sendPacket.invoke(connection, packet);
    }

    private static String getCraftPlayerClasspath() {
	return "org.bukkit.craftbukkit." + Version.getCurrent() + ".entity.CraftPlayer";
    }

    private static String getPlayerConnectionClasspath() {
	return "net.minecraft.server." + Version.getCurrent() + ".PlayerConnection";
    }

    private static String getNMSPlayerClasspath() {
	return "net.minecraft.server." + Version.getCurrent() + ".EntityPlayer";
    }

    private static String getPacketClasspath() {
	return "net.minecraft.server." + Version.getCurrent() + ".Packet";
    }

    private static String getIChatBaseComponentClasspath() {
	return "net.minecraft.server." + Version.getCurrent() + ".IChatBaseComponent";
    }

    private static String getChatSerializerClasspath() {
	if (!Version.isCurrentHigher(Version.v1_8_R2))
	    return "net.minecraft.server." + Version.getCurrent() + ".ChatSerializer";
	return "net.minecraft.server." + Version.getCurrent() + ".IChatBaseComponent$ChatSerializer";// 1_8_R2 moved to IChatBaseComponent
    }

    private static String getPacketPlayOutChat() {
	return "net.minecraft.server." + Version.getCurrent() + ".PacketPlayOutChat";
    }

    private static String getPacketPlayOutTitleClasspath() {
	return "net.minecraft.server." + Version.getCurrent() + ".PacketPlayOutTitle";
    }

    private static String getEnumTitleActionClasspath() {
	return getPacketPlayOutTitleClasspath() + "$EnumTitleAction";
    }

    private static String getClassMessageClasspath() {
	return "org.bukkit.craftbukkit." + Version.getCurrent() + ".util.CraftChatMessage";
    }

    private static String getChatMessageTypeClasspath() {
	return "net.minecraft.server." + Version.getCurrent() + ".ChatMessageType";
    }
}
