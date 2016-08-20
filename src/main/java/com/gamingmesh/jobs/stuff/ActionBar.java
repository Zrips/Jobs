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

/**
 *
 * @author hamzaxx
 */
public class ActionBar {
	private int cleanVersion = -1;
	private String version = "";
	private Object packet;
	private Method getHandle;
	private Method sendPacket;
	private Field playerConnection;
	private Class<?> nmsChatSerializer;
	private Class<?> nmsIChatBaseComponent;
	private Class<?> packetType;

	public int getVersion() {
		if (cleanVersion == -1)
			getInfo();
		return cleanVersion;
	}

	private void getInfo() {
		try {
			String[] v = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
			version = v[v.length - 1];
			// Translating version to integer for simpler use
			try {
				cleanVersion = Integer.parseInt(
						version.replace("v", "").replace("V", "").replace("_", "").replace("r", "").replace("R", ""));
				cleanVersion *= 10;
			} catch (NumberFormatException e) {
				// Fail safe if it for some reason can't translate version to
				// integer
				if (version.contains("v1_4"))
					cleanVersion = 1400;
				if (version.contains("v1_5"))
					cleanVersion = 1500;
				if (version.contains("v1_6"))
					cleanVersion = 1600;
				if (version.contains("v1_7"))
					cleanVersion = 1700;
				if (version.contains("v1_8_R1"))
					cleanVersion = 1810;
				if (version.contains("v1_8_R2"))
					cleanVersion = 1820;
				if (version.contains("v1_8_R3"))
					cleanVersion = 1830;
				if (version.contains("v1_9_R1"))
					cleanVersion = 1910;
				if (version.contains("v1_9_R2"))
					cleanVersion = 1920;
				if (version.contains("v1_10_R1"))
					cleanVersion = 11010;
			}

			if (cleanVersion < 1400)
				cleanVersion *= 10;

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

	public void ShowActionBar(BufferedPayment payment) {

		if (cleanVersion == -1)
			getInfo();

		if (cleanVersion == -1)
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
				Message = Message + " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.money", "[amount]",
						String.format("%.2f", payment.getAmount()));
			if (payment.getPoints() != 0D)
				Message = Message + " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.points", "[points]",
						String.format("%.2f", payment.getPoints()));
			if (payment.getExp() != 0D)
				Message = Message + " " + Jobs.getLanguage().getMessage("command.toggle.output.paid.exp", "[exp]",
						String.format("%.2f", payment.getExp()));
			send(abp, ChatColor.GREEN + Message);
		}
	}

	public void send(CommandSender receivingPacket, String msg) {
		try {
			if (msg == null || nmsChatSerializer == null)
				return;

			if (receivingPacket == null)
				return;

			if (cleanVersion < 1800 || !(receivingPacket instanceof Player)) {
				receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				return;
			}

			Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null,
					"{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
			if (cleanVersion > 1800) {
				packet = packetType.getConstructor(nmsIChatBaseComponent, byte.class).newInstance(serialized, (byte) 2);
			} else {
				packet = packetType.getConstructor(nmsIChatBaseComponent, int.class).newInstance(serialized, 2);
			}
			Object player = getHandle.invoke(receivingPacket);
			Object connection = playerConnection.get(player);
			sendPacket.invoke(connection, packet);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
				| InstantiationException | NoSuchMethodException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error {0}", ex);
		}
	}

	private String getCraftPlayerClasspath() {
		return "org.bukkit.craftbukkit." + version + ".entity.CraftPlayer";
	}

	private String getPlayerConnectionClasspath() {
		return "net.minecraft.server." + version + ".PlayerConnection";
	}

	private String getNMSPlayerClasspath() {
		return "net.minecraft.server." + version + ".EntityPlayer";
	}

	private String getPacketClasspath() {
		return "net.minecraft.server." + version + ".Packet";
	}

	private String getIChatBaseComponentClasspath() {
		return "net.minecraft.server." + version + ".IChatBaseComponent";
	}

	private String getChatSerializerClasspath() {
		if (cleanVersion < 1820)
			return "net.minecraft.server." + version + ".ChatSerializer";
		return "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer";// 1_8_R2
																						// moved
																						// to
																						// IChatBaseComponent
	}

	private String getPacketPlayOutChat() {
		return "net.minecraft.server." + version + ".PacketPlayOutChat";
	}
}