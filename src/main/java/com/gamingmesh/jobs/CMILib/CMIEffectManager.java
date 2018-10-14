/**
 * Copyright (C) 2017 Zrips
 */
package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Effect.Type;
import org.bukkit.Material;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.CMILib.VersionChecker.Version;

public class CMIEffectManager {

    public enum CMIParticleType {
	SOUND, VISUAL, PARTICLE, NONE;
    }

    public enum CMIParticleDataType {
	Void, DustOptions, ItemStack, BlockData, MaterialData;
    }

    public enum CMIParticle {
	CLICK2("null", 0, CMIParticleType.SOUND, null),
	CLICK1("null", 1, CMIParticleType.SOUND, null),
	BOW_FIRE("null", 2, CMIParticleType.SOUND, null),
	DOOR_TOGGLE("null", 3, CMIParticleType.SOUND, null),
	IRON_DOOR_TOGGLE("null", 4, CMIParticleType.SOUND, null),
	TRAPDOOR_TOGGLE("null", 5, CMIParticleType.SOUND, null),
	IRON_TRAPDOOR_TOGGLE("null", 6, CMIParticleType.SOUND, null),
	FENCE_GATE_TOGGLE("null", 7, CMIParticleType.SOUND, null),
	DOOR_CLOSE("null", 8, CMIParticleType.SOUND, null),
	IRON_DOOR_CLOSE("null", 9, CMIParticleType.SOUND, null),
	TRAPDOOR_CLOSE("null", 10, CMIParticleType.SOUND, null),
	IRON_TRAPDOOR_CLOSE("null", 11, CMIParticleType.SOUND, null),
	FENCE_GATE_CLOSE("null", 12, CMIParticleType.SOUND, null),
	EXTINGUISH("null", 13, CMIParticleType.SOUND, null),
	RECORD_PLAY("null", 14, CMIParticleType.SOUND, null),
	GHAST_SHRIEK("null", 15, CMIParticleType.SOUND, null),
	GHAST_SHOOT("null", 16, CMIParticleType.SOUND, null),
	BLAZE_SHOOT("null", 17, CMIParticleType.SOUND, null),
	ZOMBIE_CHEW_WOODEN_DOOR("null", 18, CMIParticleType.SOUND, null),
	ZOMBIE_CHEW_IRON_DOOR("null", 19, CMIParticleType.SOUND, null),
	ZOMBIE_DESTROY_DOOR("null", 20, CMIParticleType.SOUND, null),
	SMOKE("null", 21, CMIParticleType.VISUAL, null),
	STEP_SOUND("null", 22, CMIParticleType.SOUND, null),
	POTION_BREAK("null", 23, CMIParticleType.VISUAL, null),
	ENDER_SIGNAL("null", 24, CMIParticleType.VISUAL, null),
	MOBSPAWNER_FLAMES("null", 25, CMIParticleType.VISUAL, null),
	BREWING_STAND_BREW("null", 26, CMIParticleType.SOUND, null),
	CHORUS_FLOWER_GROW("null", 27, CMIParticleType.SOUND, null),
	CHORUS_FLOWER_DEATH("null", 28, CMIParticleType.SOUND, null),
	PORTAL_TRAVEL("null", 29, CMIParticleType.SOUND, null),
	ENDEREYE_LAUNCH("null", 30, CMIParticleType.SOUND, null),
	FIREWORK_SHOOT("null", 31, CMIParticleType.SOUND, null),
	VILLAGER_PLANT_GROW("null", 32, CMIParticleType.VISUAL, null),
	DRAGON_BREATH("null", 33, CMIParticleType.VISUAL, null),
	ANVIL_BREAK("null", 34, CMIParticleType.SOUND, null),
	ANVIL_USE("null", 35, CMIParticleType.SOUND, null),
	ANVIL_LAND("null", 36, CMIParticleType.SOUND, null),
	ENDERDRAGON_SHOOT("null", 37, CMIParticleType.SOUND, null),
	WITHER_BREAK_BLOCK("null", 38, CMIParticleType.SOUND, null),
	WITHER_SHOOT("null", 39, CMIParticleType.SOUND, null),
	ZOMBIE_INFECT("null", 40, CMIParticleType.SOUND, null),
	ZOMBIE_CONVERTED_VILLAGER("null", 41, CMIParticleType.SOUND, null),
	BAT_TAKEOFF("null", 42, CMIParticleType.SOUND, null),
	END_GATEWAY_SPAWN("null", 43, CMIParticleType.VISUAL, null),
	ENDERDRAGON_GROWL("null", 44, CMIParticleType.SOUND, null),
	FIREWORKS_SPARK("fireworksSpark", 45, CMIParticleType.PARTICLE, CMIMaterial.FIRE_CHARGE.getMaterial()),
	CRIT("crit", 46, CMIParticleType.PARTICLE, Material.IRON_SWORD),
	MAGIC_CRIT("CRIT_MAGIC", 47, CMIParticleType.PARTICLE, Material.POTION),
	POTION_SWIRL("mobSpell", "SPELL_MOB", 48, CMIParticleType.PARTICLE, Material.BLAZE_ROD),
	POTION_SWIRL_TRANSPARENT("mobSpellAmbient", "SPELL_MOB_AMBIENT", 49, CMIParticleType.PARTICLE, Material.BLAZE_POWDER),
	SPELL("spell", 50, CMIParticleType.PARTICLE, Material.MILK_BUCKET),
	INSTANT_SPELL("instantSpell", "SPELL_INSTANT", 51, CMIParticleType.PARTICLE, Material.GLASS_BOTTLE),
	WITCH_MAGIC("witchMagic", "SPELL_WITCH", 52, CMIParticleType.PARTICLE, Material.SPIDER_EYE),
	NOTE("note", 53, CMIParticleType.PARTICLE, Material.NOTE_BLOCK),
	PORTAL("portal", 54, CMIParticleType.PARTICLE, Material.OBSIDIAN),
	FLYING_GLYPH("enchantmenttable", 55, CMIParticleType.PARTICLE, CMIMaterial.ENCHANTING_TABLE.getMaterial()),
	FLAME("flame", 56, CMIParticleType.PARTICLE, CMIMaterial.FIRE_CHARGE.getMaterial()),
	LAVA_POP("lava", 57, CMIParticleType.PARTICLE, Material.FLINT_AND_STEEL),
	FOOTSTEP("footstep", 58, CMIParticleType.PARTICLE, Material.IRON_BOOTS),
	SPLASH("splash", "water splash", 59, CMIParticleType.PARTICLE, Material.STICK),
	PARTICLE_SMOKE("smoke", "SMOKE_NORMAL", 60, CMIParticleType.PARTICLE, Material.ANVIL),
	EXPLOSION_HUGE("hugeexplosion", 61, CMIParticleType.PARTICLE, Material.FURNACE),
	EXPLOSION_LARGE("largeexplode", 62, CMIParticleType.PARTICLE, Material.FURNACE),
	EXPLOSION("explode", "EXPLOSION_NORMAL", 63, CMIParticleType.PARTICLE, Material.TNT),
	VOID_FOG("depthsuspend", "SUSPENDED_DEPTH", 64, CMIParticleType.PARTICLE, CMIMaterial.SALMON.getMaterial()),
	SMALL_SMOKE("townaura", 65, CMIParticleType.PARTICLE, CMIMaterial.MYCELIUM.getMaterial()),
	CLOUD("cloud", 66, CMIParticleType.PARTICLE, CMIMaterial.COBWEB.getMaterial()),
	COLOURED_DUST("reddust", "redstone", 67, CMIParticleType.PARTICLE, Material.REDSTONE, CMIParticleDataType.DustOptions),
	SNOWBALL_BREAK("snowballpoof", "SNOWBALL", 68, CMIParticleType.PARTICLE, CMIMaterial.SNOWBALL.getMaterial()),
	WATERDRIP("dripWater", "WATER_DROP", 69, CMIParticleType.PARTICLE, Material.WATER_BUCKET),
	LAVADRIP("dripLava", 70, CMIParticleType.PARTICLE, Material.LAVA_BUCKET),
	SNOW_SHOVEL("snowshovel", 71, CMIParticleType.PARTICLE, CMIMaterial.DIAMOND_SHOVEL.getMaterial()),
	SLIME("slime", 72, CMIParticleType.PARTICLE, Material.SLIME_BALL),
	HEART("heart", 73, CMIParticleType.PARTICLE, CMIMaterial.ROSE_RED.getMaterial()),
	VILLAGER_THUNDERCLOUD("angryVillager", "VILLAGER_ANGRY", 74, CMIParticleType.PARTICLE, Material.EMERALD),
	HAPPY_VILLAGER("VILLAGER_HAPPY", 75, CMIParticleType.PARTICLE, Material.BOOK),
	LARGE_SMOKE("largesmoke", "SMOKE_LARGE", 76, CMIParticleType.PARTICLE, Material.FURNACE),
	ITEM_BREAK("iconcrack", 77, CMIParticleType.NONE, Material.DIAMOND_BOOTS),
//	TILE_BREAK("blockcrack", 78, CMIParticleType.PARTICLE, CMIMaterial.MELON.getMaterial(), CMIParticleDataType.MaterialData),
//	TILE_DUST("blockdust", 79, CMIParticleType.PARTICLE, CMIMaterial.MELON.getMaterial(), CMIParticleDataType.MaterialData),

	// 1.13

	WATER_BUBBLE("WATER_BUBBLE", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	WATER_WAKE("WATER_WAKE", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	SUSPENDED("SUSPENDED", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	BARRIER("BARRIER", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
//	ITEM_CRACK("ITEM_CRACK", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.ItemStack),
	MOB_APPEARANCE("MOB_APPEARANCE", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	END_ROD("END_ROD", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	DAMAGE_INDICATOR("DAMAGE_INDICATOR", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	SWEEP_ATTACK("SWEEP_ATTACK", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
//	FALLING_DUST("FALLING_DUST", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.BlockData),
	TOTEM("TOTEM", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	SPIT("SPIT", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	SQUID_INK("SQUID_INK", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	BUBBLE_POP("BUBBLE_POP", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	CURRENT_DOWN("CURRENT_DOWN", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	BUBBLE_COLUMN_UP("BUBBLE_COLUMN_UP", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	NAUTILUS("NAUTILUS", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void),
	DOLPHIN("DOLPHIN", -1, CMIParticleType.PARTICLE, Material.STONE, CMIParticleDataType.Void);

	private String name;
	private String secondaryName = "";
	private int id;
	private CMIParticleType type;
	private Material icon;
	private Object particle;
	private Effect effect;
	private Object EnumParticle;
	private int[] extra;
	@SuppressWarnings("unused")
	private CMIParticleDataType dataType = CMIParticleDataType.Void;

	CMIParticle(String name, int id, CMIParticleType type) {
	    this(name, null, id, type, null);
	}

	CMIParticle(String name, int id, CMIParticleType type, Material icon) {
	    this(name, null, id, type, icon);
	}

	CMIParticle(String name, String secondaryName, int id, CMIParticleType type, Material icon) {
	    this(name, secondaryName, id, type, icon, CMIParticleDataType.Void);
	}

	CMIParticle(String name, int id, CMIParticleType type, Material icon, CMIParticleDataType dataType) {
	    this(name, null, id, type, icon, dataType);
	}

	CMIParticle(String name, String secondaryName, int id, CMIParticleType type, Material icon, CMIParticleDataType dataType) {
	    this.name = name;
	    this.secondaryName = secondaryName;
	    this.id = id;
	    this.type = type;
	    this.icon = icon;
	    this.dataType = dataType;
	}

	public String getName() {
	    return name;
	}

	public int getId() {
	    return id;
	}

	public CMIParticleType getType() {
	    return type;
	}

	public boolean isParticle() {
	    return type == CMIParticleType.PARTICLE;
	}

	public boolean isColored() {
	    return this.equals(COLOURED_DUST) || this.equals(NOTE);
	}

	public static boolean isParticle(Effect effect) {
	    if (effect == null)
		return false;
	    CMIParticle cmiEffect = getCMIParticle(effect.toString());
	    if (cmiEffect == null)
		return false;
	    return cmiEffect.isParticle();
	}

	public static Material getSafeIcon(Effect effect) {
	    CMIParticle cmiEffect = getCMIParticle(effect.toString());
	    if (cmiEffect == null)
		return Material.STONE;
	    return cmiEffect.getIcon() == null ? Material.STONE : cmiEffect.getIcon();
	}

	public Material getSafeIcon() {
	    return getIcon() == null ? Material.STONE : getIcon();
	}

	public static CMIParticle getCMIParticle(String name) {
	    CMIParticle cmiEffect = null;
	    if (name == null)
		return null;
	    name = name.replace("_", "").toLowerCase();
	    for (CMIParticle one : CMIParticle.values()) {
		if (one.getName() != null && one.getName().equalsIgnoreCase(name)) {
		    cmiEffect = one;
		    break;
		}
		if (!one.getSecondaryName().isEmpty() && one.getSecondaryName().replace("_", "").equalsIgnoreCase(name)) {
		    cmiEffect = one;
		    break;
		}
		if (one.name().replace("_", "").equalsIgnoreCase(name)) {
		    cmiEffect = one;
		    break;
		}
	    }
	    if (cmiEffect != null && Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R1) && cmiEffect.getParticle() == null)
		return null;
	    if (Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_13_R1) && cmiEffect != null && cmiEffect.getEffect() == null)
		return null;
	    return cmiEffect;
	}

//	public static Effect getEffect(String name) {
//	    CMIParticle cmiEffect = getCMIParticle(name);
////	    Bukkit.getConsoleSender().sendMessage("1 "+name);
////	    Bukkit.getConsoleSender().sendMessage("2 "+cmiEffect);
//
//	    if (cmiEffect != null) {
//		if (!cmiEffect.getType().equals(CMIParticleType.PARTICLE))
//		    return null;
//		for (Effect one : Effect.values()) {
//		    if (one.toString().equalsIgnoreCase(cmiEffect.name()))
//			return one;
//		    if (one.toString().equalsIgnoreCase(cmiEffect.getName()))
//			return one;
//		}
//	    } else {
//		for (Effect one : Effect.values()) {
//		    if (one.toString().replace("_", "").equalsIgnoreCase(name)) {
//			try {
//			    if (one.getType() != Type.VISUAL)
//				return null;
//			} catch (Exception | NoSuchMethodError e) {
//			    return null;
//			}
//			return one;
//		    }
//		}
//	    }
//	    return null;
//	}

	public Effect getEffect() {
	    if (effect != null)
		return effect;
	    if (!isParticle())
		return null;
	    for (Effect one : Effect.values()) {
		if (one.toString().replace("_", "").equalsIgnoreCase(name().replace("_", ""))) {
		    effect = one;
		    return one;
		}
		if (one.toString().replace("_", "").equalsIgnoreCase(getName())) {
		    effect = one;
		    return one;
		}
	    }

	    for (Effect one : Effect.values()) {
		if (one.toString().replace("_", "").equalsIgnoreCase(name.replace("_", ""))) {
		    try {
			if (one.getType() != Type.VISUAL)
			    return null;
		    } catch (Exception | NoSuchMethodError e) {
			return null;
		    }
		    effect = one;
		    return one;
		}
	    }
	    return null;
	}

	public Material getIcon() {
	    return icon;
	}

	public static List<CMIParticle> getParticleList() {
	    List<CMIParticle> ls = new ArrayList<>();
	    for (CMIParticle one : CMIParticle.values()) {
		if (!one.isParticle())
		    continue;
		if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R1) && one.getParticle() == null)
		    continue;
		if (Jobs.getVersionCheckManager().getVersion().isLower(Version.v1_13_R1) && one.getEffect() == null)
		    continue;
		ls.add(one);
	    }
	    return ls;
	}

	public CMIParticle getNextPartcileEffect() {

	    List<CMIParticle> ls = getParticleList();
	    for (int i = 0; i < ls.size(); i++) {
		CMIParticle next = ls.get(i);
		if (next == null)
		    continue;

		if (!next.isParticle())
		    continue;
		if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R1) && next.getParticle() == null)
		    continue;

		if (next.equals(this)) {
		    if (i == ls.size() - 1)
			return ls.get(0);
		    return ls.get(i + 1);
		}
	    }
	    return this;
	}

	public CMIParticle getPrevParticleEffect() {
	    List<CMIParticle> ls = getParticleList();
	    for (int i = 0; i < ls.size(); i++) {
		CMIParticle next = ls.get(i);

		if (next == null)
		    continue;

		if (Jobs.getVersionCheckManager().getVersion().isEqualOrHigher(Version.v1_9_R1) && next.getParticle() == null)
		    continue;

		if (!next.isParticle())
		    continue;
		if (next.equals(this)) {
		    if (i == 0)
			return ls.get(ls.size() - 1);
		    return ls.get(i - 1);
		}
	    }
	    return this;
	}

	public String getSecondaryName() {
	    return secondaryName == null ? "" : secondaryName;
	}

	public void setSecondaryName(String secondaryName) {
	    this.secondaryName = secondaryName;
	}

	public org.bukkit.Particle getParticle() {
	    if (Jobs.getVersionCheckManager().getVersion().isEqualOrLower(Version.v1_8_R3))
		return null;
	    if (particle == null) {
		String n = this.toString().replace("_", "").toLowerCase();
		for (org.bukkit.Particle one : org.bukkit.Particle.values()) {
		    String name = one.toString().toLowerCase().replace("_", "");
		    if (name.equalsIgnoreCase(n)) {
			particle = one;
			break;
		    }
		}
	    }
	    if (particle == null) {
		String n = name().replace("_", "").toLowerCase();
		for (org.bukkit.Particle one : org.bukkit.Particle.values()) {
		    String name = one.toString().toLowerCase().replace("_", "");
		    if (name.equalsIgnoreCase(n)) {
			particle = one;
			break;
		    }
		}
	    }
	    if (particle == null) {
		String n = getName().replace("_", "").toLowerCase();
		for (org.bukkit.Particle one : org.bukkit.Particle.values()) {
		    String name = one.toString().toLowerCase().replace("_", "");
		    if (name.equalsIgnoreCase(n)) {
			particle = one;
			break;
		    }
		}
	    }
	    if (particle == null) {
		String n = getSecondaryName().replace("_", "").toLowerCase();
		if (!n.isEmpty()) {
		    for (org.bukkit.Particle one : org.bukkit.Particle.values()) {
			String name = one.toString().toLowerCase().replace("_", "");
			if (name.equalsIgnoreCase(n)) {
			    particle = one;
			    break;
			}
		    }

		    if (particle == null)
			for (org.bukkit.Particle one : org.bukkit.Particle.values()) {
			    String name = one.toString().toLowerCase().replace("_", "");
			    if (name.contains(n)) {
				particle = one;
				break;
			    }
			}
		}
	    }
	    return particle == null ? null : (org.bukkit.Particle) particle;
	}

	public Object getEnumParticle() {
	    return EnumParticle;
	}

	public void setEnumParticle(Object EnumParticle) {
	    this.EnumParticle = EnumParticle;
	}

	public int[] getExtra() {
	    return extra;
	}

	public void setExtra(int[] extra) {
	    this.extra = extra;
	}
    }
}
