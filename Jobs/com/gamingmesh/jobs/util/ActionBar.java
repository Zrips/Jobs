package com.gamingmesh.jobs.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
*
* @author hamzaxx
*/
public class ActionBar {
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

	public static void send(Player receivingPacket, String msg) {
		try {
			if (msg == null)
				return;
			Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
			if (!version.contains("1_7")) {
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
		if(version.equals("v1_8_R1") || version.contains("1_7")){
    		return "net.minecraft.server." + version + ".ChatSerializer";
    	} else {
    		return "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer"; // 1_8_R2 moved to IChatBaseComponent
    	} 
	}

	private static String getPacketPlayOutChat() {
		return "net.minecraft.server." + version + ".PacketPlayOutChat";
	}
}