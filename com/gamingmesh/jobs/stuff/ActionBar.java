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
	private static int cleanVersion = 182;
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
					cleanVersion = 170;
				if (version.contains("v1_6"))
					cleanVersion = 160;
				if (version.contains("v1_5"))
					cleanVersion = 150;
				if (version.contains("v1_4"))
					cleanVersion = 140;
				if (version.contains("v1_8_R1"))
					cleanVersion = 181;
				if (version.contains("v1_8_R2"))
					cleanVersion = 182;
				if (version.contains("v1_8_R3"))
					cleanVersion = 183;
			}

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
		
		if (playername != null && Jobs.actionbartoggle.size() > 0)
			if (Jobs.actionbartoggle.containsKey(playername)) {
				Boolean show = Jobs.actionbartoggle.get(playername);
				Player abp = (Player) payment.getOfflinePlayer();
				if (abp != null && show) {
					String Message = Language.getMessage("command.toggle.output.paid");
					Message = Message.replace("[amount]", String.valueOf((((int) (payment.getAmount() * 100)) / 100.0)));
					Message = Message.replace("[exp]", String.valueOf((((int) (payment.getExp() * 100)) / 100.0)));
					ActionBar.send(abp, ChatColor.GREEN + Message);
				} else {
					Jobs.actionbartoggle.remove(playername);
				}
			}
	}

	public static void send(Player receivingPacket, String msg) {
		try {
			if (msg == null || nmsChatSerializer == null)
				return;

			if (cleanVersion < 180) {
				receivingPacket.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				return;
			}

			Object serialized = nmsChatSerializer.getMethod("a", String.class).invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', msg) + "\"}");
			if (cleanVersion > 180) {
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

		if (cleanVersion < 182) {
			return "net.minecraft.server." + version + ".ChatSerializer";
		} else {
			return "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer"; // 1_8_R2 moved to IChatBaseComponent
		}
	}

	private static String getPacketPlayOutChat() {
		return "net.minecraft.server." + version + ".PacketPlayOutChat";
	}
}