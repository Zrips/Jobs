package com.gamingmesh.jobs.CMILib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionBarManager {
    private static Object packet;
    private static Method getHandle;
    private static Method sendPacket;
    private static Field playerConnection;
    private static Class<?> nmsChatSerializer;
    private static Class<?> nmsIChatBaseComponent;
    private static Class<?> packetType;

    private static Class<?> ChatMessageclz;
    private static Class<?> sub;
    private static Object[] consts;

    static {
	if (Version.isCurrentHigher(Version.v1_7_R4)) {
	    Version version = Version.getCurrent();
	    try {
		packetType = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
		Class<?> typeCraftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
		Class<?> typeNMSPlayer = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
		Class<?> typePlayerConnection = Class.forName("net.minecraft.server." + version + ".PlayerConnection");
		nmsChatSerializer = Class.forName(getChatSerializerClasspath());
		nmsIChatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
		getHandle = typeCraftPlayer.getMethod("getHandle");
		playerConnection = typeNMSPlayer.getField("playerConnection");
		sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"));
		if (Version.isCurrentHigher(Version.v1_11_R1)) {
		    ChatMessageclz = Class.forName("net.minecraft.server." + version + ".ChatMessageType");
		    consts = ChatMessageclz.getEnumConstants();
		    sub = consts[2].getClass();
		}
	    } catch (Exception ex) {
		Bukkit.getConsoleSender().sendMessage(ex.toString());
	    }
	}
    }

    public static void send(CommandSender receivingPacket, String msg) {
	if (receivingPacket instanceof Player)
	    send((Player) receivingPacket, msg);
	else
	    receivingPacket.sendMessage(msg);
    }

    public static void send(Player receivingPacket, String msg) {
	if (receivingPacket == null || !receivingPacket.isOnline() || msg == null)
	    return;

	try {
	    if (!Version.getCurrent().isHigher(Version.v1_7_R4) || nmsChatSerializer == null) {
		receivingPacket.sendMessage(CMIChatColor.translate(msg));
		return;
	    }

	    RawMessage rm = new RawMessage().addText(msg);
	    Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null, CMIChatColor.translate(rm.getRaw()));
	    if (Version.isCurrentHigher(Version.v1_15_R1))
		packet = packetType.getConstructor(nmsIChatBaseComponent, sub, UUID.class).newInstance(serialized, consts[2], receivingPacket.getUniqueId());
	    else if (Version.isCurrentHigher(Version.v1_11_R1)) {
		packet = packetType.getConstructor(nmsIChatBaseComponent, sub).newInstance(serialized, consts[2]);
	    } else if (Version.isCurrentHigher(Version.v1_7_R4)) {
		packet = packetType.getConstructor(nmsIChatBaseComponent, byte.class).newInstance(serialized, (byte) 2);
	    } else {
		packet = packetType.getConstructor(nmsIChatBaseComponent, int.class).newInstance(serialized, 2);
	    }
	    Object player = getHandle.invoke(receivingPacket);
	    Object connection = playerConnection.get(player);

	    sendPacket.invoke(connection, packet);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    private static String getChatSerializerClasspath() {
	if (!Version.isCurrentHigher(Version.v1_8_R2))
	    return "net.minecraft.server." + Version.getCurrent() + ".ChatSerializer";
	return "net.minecraft.server." + Version.getCurrent() + ".IChatBaseComponent$ChatSerializer";// 1_8_R2 moved to IChatBaseComponent
    }
}
