package com.gamingmesh.jobs.stuff;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.gamingmesh.jobs.stuff.VersionChecker.Version;

/**
*
* @author hamzaxx
*/
public class ActionBar {
    private Version version = Version.v1_11_R1;
    private Object packet;
    private Method getHandle;
    private Method sendPacket;
    private Field playerConnection;
    private Class<?> nmsChatSerializer;
    private Class<?> nmsIChatBaseComponent;
    private Class<?> packetType;

    private Class<?> ChatMessageclz;
    private Class<?> sub;
    private Object[] consts;

    public ActionBar() {
	try {
	    version = Jobs.getVersionCheckManager().getVersion();
	    packetType = Class.forName(getPacketPlayOutChat());
	    Class<?> typeCraftPlayer = Class.forName(getCraftPlayerClasspath());
	    Class<?> typeNMSPlayer = Class.forName(getNMSPlayerClasspath());
	    Class<?> typePlayerConnection = Class.forName(getPlayerConnectionClasspath());
	    nmsChatSerializer = Class.forName(getChatSerializerClasspath());
	    nmsIChatBaseComponent = Class.forName(getIChatBaseComponentClasspath());
	    getHandle = typeCraftPlayer.getMethod("getHandle");
	    playerConnection = typeNMSPlayer.getField("playerConnection");
	    sendPacket = typePlayerConnection.getMethod("sendPacket", Class.forName(getPacketClasspath()));

	    if (Jobs.getVersionCheckManager().getVersion().isHigher(Version.v1_11_R1)) {
		ChatMessageclz = Class.forName(getChatMessageTypeClasspath());
		consts = ChatMessageclz.getEnumConstants();
		sub = consts[2].getClass();
	    }

	} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}

    }

    public void ShowActionBar(BufferedPayment payment) {

	if (!payment.getOfflinePlayer().isOnline())
	    return;

	String playername = payment.getOfflinePlayer().getName();
	if (!Jobs.getActionbarToggleList().containsKey(playername) && Jobs.getGCManager().ActionBarsMessageByDefault)
	    Jobs.getActionbarToggleList().put(playername, true);

	if (playername == null)
	    return;

	if (!Jobs.getActionbarToggleList().containsKey(playername))
	    return;

	Boolean show = Jobs.getActionbarToggleList().get(playername);
	Player abp = Bukkit.getPlayer(payment.getOfflinePlayer().getUniqueId());

	if (abp != null && show) {
	    String Message = Jobs.getLanguage().getMessage("command.toggle.output.paid.main");
	    if (payment.getAmount() != 0D)
		Message = Message + " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.money", "[amount]", String.format(Jobs.getGCManager().getDecimalPlacesMoney(), payment
		    .getAmount()));
	    if (payment.getPoints() != 0D)
		Message = Message + " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.points", "[points]", String.format(Jobs.getGCManager().getDecimalPlacesPoints(), payment.getPoints()));
	    if (payment.getExp() != 0D)
		Message = Message + " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.exp", "[exp]", String.format(Jobs.getGCManager().getDecimalPlacesExp(), payment.getExp()));
	    send(abp, ChatColor.GREEN + Message);
	}
    }

    public void send(CommandSender receivingPacket, String msg) {
	try {
	    if (msg == null || nmsChatSerializer == null || msg.isEmpty())
		return;

	    if (receivingPacket == null)
		return;

	    if (version.isLower(Version.v1_8_R1) || !(receivingPacket instanceof Player)) {
		receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		return;
	    }

	    Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
	    if (Jobs.getVersionCheckManager().getVersion().isHigher(Version.v1_11_R1))
		packet = packetType.getConstructor(nmsIChatBaseComponent, sub).newInstance(serialized, consts[2]);
	    else if (version.isHigher(Version.v1_7_R4)) {
		packet = packetType.getConstructor(nmsIChatBaseComponent, byte.class).newInstance(serialized, (byte) 2);
	    } else {
		packet = packetType.getConstructor(nmsIChatBaseComponent, int.class).newInstance(serialized, 2);
	    }
	    Object player = getHandle.invoke(receivingPacket);
	    Object connection = playerConnection.get(player);
	    sendPacket.invoke(connection, packet);
	} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException ex) {
	    Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
	}
    }

    private String getCraftPlayerClasspath() {
	return "org.bukkit.craftbukkit." + version.name() + ".entity.CraftPlayer";
    }

    private String getPlayerConnectionClasspath() {
	return "net.minecraft.server." + version.name() + ".PlayerConnection";
    }

    private String getNMSPlayerClasspath() {
	return "net.minecraft.server." + version.name() + ".EntityPlayer";
    }

    private String getPacketClasspath() {
	return "net.minecraft.server." + version.name() + ".Packet";
    }

    private String getIChatBaseComponentClasspath() {
	return "net.minecraft.server." + version.name() + ".IChatBaseComponent";
    }

    private String getChatSerializerClasspath() {
	if (!Jobs.getVersionCheckManager().getVersion().isHigher(Version.v1_8_R2))
	    return "net.minecraft.server." + version.name() + ".ChatSerializer";
	return "net.minecraft.server." + version.name() + ".IChatBaseComponent$ChatSerializer";// 1_8_R2 moved to IChatBaseComponent
    }

    private String getPacketPlayOutChat() {
	return "net.minecraft.server." + version.name() + ".PacketPlayOutChat";
    }

    private String getChatMessageTypeClasspath() {
	return "net.minecraft.server." + version.name() + ".ChatMessageType";
    }
}