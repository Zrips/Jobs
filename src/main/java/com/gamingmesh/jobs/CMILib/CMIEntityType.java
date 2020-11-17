package com.gamingmesh.jobs.CMILib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public enum CMIEntityType {

    DROPPED_ITEM(1, "Item"),
    EXPERIENCE_ORB(2, "Experience Orb"),
    AREA_EFFECT_CLOUD(3, "Area Effect Cloud"),
    ELDER_GUARDIAN(4, "Elder Guardian", Arrays.asList(
	"MWM3OTc0ODJhMTRiZmNiODc3MjU3Y2IyY2ZmMWI2ZTZhOGI4NDEzMzM2ZmZiNGMyOWE2MTM5Mjc4YjQzNmIifX19")),
    WITHER_SKELETON(5, "Wither Skeleton", Arrays.asList(
	"Nzk1M2I2YzY4NDQ4ZTdlNmI2YmY4ZmIyNzNkNzIwM2FjZDhlMWJlMTllODE0ODFlYWQ1MWY0NWRlNTlhOCJ9fX0=",
	"NDk2YmM4ZWJkNGUxM2Y0OTZkOGQ3NGM1NjVkZDU2ZTk5YTRhZjJlMmVhN2EyN2E5NmMxYWJkMjg0MTg0YiJ9fX0=",
	"ZjVlYzk2NDY0NWE4ZWZhYzc2YmUyZjE2MGQ3Yzk5NTYzNjJmMzJiNjUxNzM5MGM1OWMzMDg1MDM0ZjA1MGNmZiJ9fX0=")),
    STRAY(6, "Stray", Arrays.asList(
	"NzhkZGY3NmU1NTVkZDVjNGFhOGEwYTVmYzU4NDUyMGNkNjNkNDg5YzI1M2RlOTY5ZjdmMjJmODVhOWEyZDU2In19fQ==")),
    EGG(7, "Thrown Egg"),
    LEASH_HITCH(8, "Leash Knot"),
    PAINTING(9, "Painting"),
    ARROW(10, "Arrow"),
    SNOWBALL(11, "Snowball"),
    FIREBALL(12, "Fireball"),
    SMALL_FIREBALL(13, "Small Fireball"),
    ENDER_PEARL(14, "Thrown Ender Pearl"),
    ENDER_SIGNAL(15, "End Signal"),
    SPLASH_POTION(16, "Splash Potion"),
    THROWN_EXP_BOTTLE(17, "Thrown Bottle o' Enchanting"),
    ITEM_FRAME(18, "Item Frame"),
    WITHER_SKULL(19, "Wither Skull"),
    PRIMED_TNT(20, "Primed TNT"),
    FALLING_BLOCK(21, "Falling Block"),
    FIREWORK(22, "Firework Rocket"),
    HUSK(23, "Husk", Arrays.asList(
	"Nzc3MDY4MWQxYTI1NWZiNGY3NTQ3OTNhYTA1NWIyMjA0NDFjZGFiOWUxMTQxZGZhNTIzN2I0OTkzMWQ5YjkxYyJ9fX0=")),
    SPECTRAL_ARROW(24, "Spectral Arrow"),
    SHULKER_BULLET(25, "Shulker Bullet"),
    DRAGON_FIREBALL(26, "Dragon Fireball"),
    ZOMBIE_VILLAGER(27, "Zombie Villager", Arrays.asList(
	"NDRmMDhlYmQ0ZTI1Y2RhM2FkZTQ1Yjg2MzM3OGFkMzc3ZjE4YzUxMGRiNGQyOGU4MmJiMjQ0NTE0MzliMzczNCJ9fX0=",
	"OTYxZjE5ZmZkOGFlNDI1NzkyYzRiMTgxNzU2YWRmZjhkNDgxNzRhZWVmNThhMGYzMjdhMjhjNzQyYzcyNDQyIn19fQ==",
	"NTI4YzJiYWQ1Mzg5Y2IzNTkyYjU2NWIzYzQ3ZWNjMTg5ZTA1NDJhODc4MzUwMjhkNjE0OGJiZTMzNDU2NDUifX19",
	"YTE2MTU1ZmNmMzY2Y2Y0ZTA2Y2U1ZGZmYzQ4Y2E1NGU4ZWE0OGRmZTUyNTM1OGI2MTJkYzQ0ZmQ0MzIifX19",
	"Y2ZmMDQ4MmZkMzJmYWIyY2U5ZjVmYTJlMmQ5YjRkYzc1NjFkYTQyMjE1MmM5OWZjODA0YjkxMzljYWYyNTZiIn19fQ==",
	"MzdlODM4Y2NjMjY3NzZhMjE3YzY3ODM4NmY2YTY1NzkxZmU4Y2RhYjhjZTljYTRhYzZiMjgzOTdhNGQ4MWMyMiJ9fX0=")),
    SKELETON_HORSE(28, "Skeleton Horse", Arrays.asList(
	"NDdlZmZjZTM1MTMyYzg2ZmY3MmJjYWU3N2RmYmIxZDIyNTg3ZTk0ZGYzY2JjMjU3MGVkMTdjZjg5NzNhIn19fQ==")),
    ZOMBIE_HORSE(29, "Zombie Horse", Arrays.asList(
	"ZDIyOTUwZjJkM2VmZGRiMThkZTg2ZjhmNTVhYzUxOGRjZTczZjEyYTZlMGY4NjM2ZDU1MWQ4ZWI0ODBjZWVjIn19fQ==")),
    ARMOR_STAND(30, "Armor Stand"),
    DONKEY(31, "Donkey", Arrays.asList(
	"ZGZiNmMzYzA1MmNmNzg3ZDIzNmEyOTE1ZjgwNzJiNzdjNTQ3NDk3NzE1ZDFkMmY4Y2JjOWQyNDFkODhhIn19fQ==")),
    MULE(32, "Mule", Arrays.asList(
	"YTA0ODZhNzQyZTdkZGEwYmFlNjFjZTJmNTVmYTEzNTI3ZjFjM2IzMzRjNTdjMDM0YmI0Y2YxMzJmYjVmNWYifX19")),
    EVOKER_FANGS(33, "Evoker Fangs"),
    EVOKER(34, "Evoker", Arrays.asList(
	"YTAwZDNmZmYxNmMyZGNhNTliOWM1OGYwOTY1MjVjODY5NzExNjZkYmFlMTMzYjFiMDUwZTVlZTcxNjQ0MyJ9fX0=")),
    VEX(35, "Vex", Arrays.asList(
	"NWU3MzMwYzdkNWNkOGEwYTU1YWI5ZTk1MzIxNTM1YWM3YWUzMGZlODM3YzM3ZWE5ZTUzYmVhN2JhMmRlODZiIn19fQ==")),
    VINDICATOR(36, "Vindicator", Arrays.asList(
	"YTAwZDNmZmYxNmMyZGNhNTliOWM1OGYwOTY1MjVjODY5NzExNjZkYmFlMTMzYjFiMDUwZTVlZTcxNjQ0MyJ9fX0=")),
    ILLUSIONER(37, "Illusioner", Arrays.asList(
	"NTEyNTEyZTdkMDE2YTIzNDNhN2JmZjFhNGNkMTUzNTdhYjg1MTU3OWYxMzg5YmQ0ZTNhMjRjYmViODhiIn19fQ==",
	"MmYyODgyZGQwOTcyM2U0N2MwYWI5NjYzZWFiMDgzZDZhNTk2OTI3MzcwNjExMGM4MjkxMGU2MWJmOGE4ZjA3ZSJ9fX0=")),
    MINECART_COMMAND(40, "Minecart with Command Block"),
    BOAT(41, "Boat"),
    MINECART(42, "Minecart"),
    MINECART_CHEST(43, "Minecart with Chest"),
    MINECART_FURNACE(44, "Minecart with Furnace"),
    MINECART_TNT(45, "Minecart with TNT"),
    MINECART_HOPPER(46, "Minecart with Hopper"),
    MINECART_MOB_SPAWNER(47, "Minecart with Spawner"),
    CREEPER(50, "Creeper", Arrays.asList(
	"ZjQyNTQ4MzhjMzNlYTIyN2ZmY2EyMjNkZGRhYWJmZTBiMDIxNWY3MGRhNjQ5ZTk0NDQ3N2Y0NDM3MGNhNjk1MiJ9fX0=",
	"YTNmMTcyZDI5Y2Y5NGJjODk1NjA4YjdhNWRjMmFmMGRlNDljNzg4ZDViZWNiMTYwNWYxZjUzNDg4YTAxNzBiOCJ9fX0=")),
    SKELETON(51, "Skeleton", Arrays.asList(
	"MzAxMjY4ZTljNDkyZGExZjBkODgyNzFjYjQ5MmE0YjMwMjM5NWY1MTVhN2JiZjc3ZjRhMjBiOTVmYzAyZWIyIn19fQ==",
	"Yjk1MDc4ZDNiM2IxNzAxZDQ1NzI5ZDNhMTQyMjQ2N2IyOWRiYjJlMWE5MTI4MTMzYTJmMTYzZWJlODVkMmRiOSJ9fX0=")),
    SPIDER(52, "Spider", Arrays.asList(
	"Y2Q1NDE1NDFkYWFmZjUwODk2Y2QyNThiZGJkZDRjZjgwYzNiYTgxNjczNTcyNjA3OGJmZTM5MzkyN2U1N2YxIn19fQ==")),
    GIANT(53, "Giant"),
    ZOMBIE(54, "Zombie", Arrays.asList(
	"NTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ==",
	"MzExZGQ5MWVlNGQzMWRkZDU5MWQyODMyZWExZWMwODBmMmVkZWQzM2FiODllZTFkYjhiMDRiMjZhNjhhIn19fQ==")),
    SLIME(55, "Slime", Arrays.asList(
	"MTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ==")),
    GHAST(56, "Ghast", Arrays.asList(
	"OGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19")),
    PIG_ZOMBIE(57, "Zombie Pigman", Arrays.asList(
	"NzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ==")),
    ENDERMAN(58, "Enderman", Arrays.asList(
	"N2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=")),
    CAVE_SPIDER(59, "Cave Spider", Arrays.asList(
	"NDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19")),
    SILVERFISH(60, "Silverfish", Arrays.asList(
	"ZGE5MWRhYjgzOTFhZjVmZGE1NGFjZDJjMGIxOGZiZDgxOWI4NjVlMWE4ZjFkNjIzODEzZmE3NjFlOTI0NTQwIn19fQ==")),
    BLAZE(61, "Blaze", Arrays.asList(
	"Yjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ==")),
    MAGMA_CUBE(62, "Magma Cube", Arrays.asList(
	"Mzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0=")),
    ENDER_DRAGON(63, "Ender Dragon"),
    WITHER(64, "Wither", Arrays.asList(
	"ZGRhZmIyM2VmYzU3ZjI1MTg3OGU1MzI4ZDExY2IwZWVmODdiNzljODdiMjU0YTdlYzcyMjk2ZjkzNjNlZjdjIn19fQ==",
	"M2U0ZjQ5NTM1YTI3NmFhY2M0ZGM4NDEzM2JmZTgxYmU1ZjJhNDc5OWE0YzA0ZDlhNGRkYjcyZDgxOWVjMmIyYiJ9fX0=",
	"OTY0ZTFjM2UzMTVjOGQ4ZmZmYzM3OTg1YjY2ODFjNWJkMTZhNmY5N2ZmZDA3MTk5ZThhMDVlZmJlZjEwMzc5MyJ9fX0=",
	"Y2RmNzRlMzIzZWQ0MTQzNjk2NWY1YzU3ZGRmMjgxNWQ1MzMyZmU5OTllNjhmYmI5ZDZjZjVjOGJkNDEzOWYifX19",
	"YTQzNTE2NGMwNWNlYTI5OWEzZjAxNmJiYmVkMDU3MDZlYmI3MjBkYWM5MTJjZTQzNTFjMjI5NjYyNmFlY2Q5YSJ9fX0=")),
    BAT(65, "Bat", Arrays.asList(
	"NGNmMWIzYjNmNTM5ZDJmNjNjMTcyZTk0Y2FjZmFhMzkxZThiMzg1Y2RkNjMzZjNiOTkxYzc0ZTQ0YjI4In19fQ==")),
    WITCH(66, "Witch", Arrays.asList(
	"ODllOGI1ZjE1YTliMjlhMWUzODljOTUyMThmZDM3OTVmMzI4NzJlNWFlZTk0NjRhNzY0OTVjNTI3ZDIyNDUifX19")),
    ENDERMITE(67, "Endermite", Arrays.asList(
	"ODRhYWZmYTRjMDllMmVhZmI4NWQzNTIyMTIyZGIwYWE0NTg3NGJlYTRlM2Y1ZTc1NjZiNGQxNjZjN2RmOCJ9fX0=")),
    GUARDIAN(68, "Guardian", Arrays.asList(
	"ZGZiNjc1Y2I1YTdlM2ZkMjVlMjlkYTgyNThmMjRmYzAyMGIzZmE5NTAzNjJiOGJjOGViMjUyZTU2ZTc0In19fQ==")),
    SHULKER(69, "Shulker", Arrays.asList(
	"MWU3MzgzMmUyNzJmODg0NGM0NzY4NDZiYzQyNGEzNDMyZmI2OThjNThlNmVmMmE5ODcxYzdkMjlhZWVhNyJ9fX0=")),
    PIG(90, "Pig", Arrays.asList(
	"NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0=",
	"YzNhYmQ0NGFlNjdkOWM5MjU0ZDE3N2U5NjU4ZGE4NDg0MzM4OWQ1ZTFmZmQyYWYxZmI1MTIxN2M3NWMyOTgifX19")),
    SHEEP(91, "Sheep", Arrays.asList(
	"MjZhNDExMmRmMWU0YmNlMmE1ZTI4NDE3ZjNhYWZmNzljZDY2ZTg4NWMzNzI0NTU0MTAyY2VmOGViOCJ9fX0=",
	"ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19",
	"Y2UxYWM2ODM5OTNiZTM1NTEyZTFiZTMxZDFmNGY5OGU1ODNlZGIxNjU4YTllMjExOTJjOWIyM2I1Y2NjZGMzIn19fQ==",
	"ODM5YWY0NzdlYjYyNzgxNWY3MjNhNTY2MjU1NmVjOWRmY2JhYjVkNDk0ZDMzOGJkMjE0MjMyZjIzZTQ0NiJ9fX0=",
	"YWU1Mjg2N2FmZWYzOGJiMTRhMjZkMTQyNmM4YzBmMTE2YWQzNDc2MWFjZDkyZTdhYWUyYzgxOWEwZDU1Yjg1In19fQ==",
	"MmFjNzRhMmI5YjkxNDUyZTU2ZmExZGRhNWRiODEwNzc4NTZlNDlmMjdjNmUyZGUxZTg0MWU1Yzk1YTZmYzVhYiJ9fX0=",
	"ZjA5ODM5N2EyNzBiNGMzZDJiMWU1NzRiOGNmZDNjYzRlYTM0MDkwNjZjZWZlMzFlYTk5MzYzM2M5ZDU3NiJ9fX0=",
	"MTgzNjU2NWM3ODk3ZDQ5YTcxYmMxODk4NmQxZWE2NTYxMzIxYTBiYmY3MTFkNDFhNTZjZTNiYjJjMjE3ZTdhIn19fQ==",
	"OTJhMjQ0OGY1OGE0OTEzMzI0MzRlODVjNDVkNzg2ZDg3NDM5N2U4MzBhM2E3ODk0ZTZkOTI2OTljNDJiMzAifX19",
	"MWM4YTk3YTM4ODU2NTE0YTE2NDEzZTJjOTk1MjEyODlmNGM1MzYzZGM4MmZjOWIyODM0Y2ZlZGFjNzhiODkifX19",
	"YTAxNzIxNWM3ZjhkYjgyMDQwYWEyYzQ3Mjk4YjY2NTQxYzJlYjVmN2Y5MzA0MGE1ZDIzZDg4ZjA2ODdkNGIzNCJ9fX0=",
	"NDI4N2ViNTAxMzkxZjI3NTM4OWYxNjZlYzlmZWJlYTc1ZWM0YWU5NTFiODhiMzhjYWU4N2RmN2UyNGY0YyJ9fX0=",
	"NDZmNmM3ZTdmZDUxNGNlMGFjYzY4NTkzMjI5ZTQwZmNjNDM1MmI4NDE2NDZlNGYwZWJjY2NiMGNlMjNkMTYifX19",
	"YTU1YWQ2ZTVkYjU2OTJkODdmNTE1MTFmNGUwOWIzOWZmOWNjYjNkZTdiNDgxOWE3Mzc4ZmNlODU1M2I4In19fQ==",
	"ZDllYzIyODE4ZDFmYmZjODE2N2ZiZTM2NzI4YjI4MjQwZTM0ZTE2NDY5YTI5MjlkMDNmZGY1MTFiZjJjYTEifX19",
	"MzI2NTIwODNmMjhlZDFiNjFmOWI5NjVkZjFhYmYwMTBmMjM0NjgxYzIxNDM1OTUxYzY3ZDg4MzY0NzQ5ODIyIn19fQ==")),
    COW(92, "Cow", Arrays.asList(
	"NWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19",
	"YzVhOWNkNThkNGM2N2JjY2M4ZmIxZjVmNzU2YTJkMzgxYzlmZmFjMjkyNGI3ZjRjYjcxYWE5ZmExM2ZiNWMifX19")),
    CHICKEN(93, "Chicken", Arrays.asList(
	"MTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ==")),
    SQUID(94, "Squid", Arrays.asList(
	"MDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ==")),
    WOLF(95, "Wolf", Arrays.asList(
	"ZTk1Y2JiNGY3NWVhODc2MTdmMmY3MTNjNmQ0OWRhYzMyMDliYTFiZDRiOTM2OTY1NGIxNDU5ZWExNTMxNyJ9fX0=")),
    MUSHROOM_COW(96, "Mushroom Cow", "Mooshroom", Arrays.asList(
	"ZDBiYzYxYjk3NTdhN2I4M2UwM2NkMjUwN2EyMTU3OTEzYzJjZjAxNmU3YzA5NmE0ZDZjZjFmZTFiOGRiIn19fQ==")),
    SNOWMAN(97, "Snowman", Arrays.asList(
	"MWZkZmQxZjc1MzhjMDQwMjU4YmU3YTkxNDQ2ZGE4OWVkODQ1Y2M1ZWY3MjhlYjVlNjkwNTQzMzc4ZmNmNCJ9fX0=")),
    OCELOT(98, "Ocelot", "Cat", Arrays.asList(
	"YWI4ODFjMzliM2FmZGNjNzlmOTFmZTVkZTNjZGQwMTViYzMzNTM4NDNmNTkxZjRkMjNjZDMwMjdkZTRlNiJ9fX0=",
	"YTc1NWU3ZGYwNGQxOGIzMWQ2M2MxN2Y0YTdiNGM3MzkyNGJkNjI2NWRhNjllMTEzZWRkZDk3NTE2ZmM3In19fQ==",
	"ZjJhNjYyZjJhZTdkZWJlZTY1MjkyYzJiZjQyZmJiMDliOTdiMmZmYmRiMjcwNTIwYzJkYjk2ZTUxZDg5NDUifX19",
	"NTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19")),
    IRON_GOLEM(99, "Iron Golem", Arrays.asList(
	"ODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19")),
    HORSE(100, "Horse", Arrays.asList(
	"NjE5MDI4OTgzMDg3MzBjNDc0NzI5OWNiNWE1ZGE5YzI1ODM4YjFkMDU5ZmU0NmZjMzY4OTZmZWU2NjI3MjkifX19")),
    RABBIT(101, "Rabbit", Arrays.asList(
	"Y2I4Y2ZmNGIxNWI4Y2EzN2UyNTc1MGYzNDU3MThmMjg5Y2IyMmM1YjNhZDIyNjI3YTcxMjIzZmFjY2MifX19",
	"NzJjNTgxMTZhMTQ3ZDFhOWEyNjI2OTIyNGE4YmUxODRmZThlNWYzZjNkZjliNjE3NTEzNjlhZDg3MzgyZWM5In19fQ==",
	"Yzk3N2EzMjY2YmYzYjllYWYxN2U1YTAyZWE1ZmJiNDY4MDExNTk4NjNkZDI4OGI5M2U2YzEyYzljYiJ9fX0=")),
    POLAR_BEAR(102, "Polar Bear", Arrays.asList(
	"ZDQ2ZDIzZjA0ODQ2MzY5ZmEyYTM3MDJjMTBmNzU5MTAxYWY3YmZlODQxOTk2NjQyOTUzM2NkODFhMTFkMmIifX19")),
    LLAMA(103, "Llama", Arrays.asList(
	"ODAyNzdlNmIzZDlmNzgxOWVmYzdkYTRiNDI3NDVmN2FiOWE2M2JhOGYzNmQ2Yjg0YTdhMjUwYzZkMWEzNThlYiJ9fX0=",
	"Y2YyNGU1NmZkOWZmZDcxMzNkYTZkMWYzZTJmNDU1OTUyYjFkYTQ2MjY4NmY3NTNjNTk3ZWU4MjI5OWEifX19",
	"YzJiMWVjZmY3N2ZmZTNiNTAzYzMwYTU0OGViMjNhMWEwOGZhMjZmZDY3Y2RmZjM4OTg1NWQ3NDkyMTM2OCJ9fX0=",
	"NGQ2N2ZkNGJmZjI5MzI2OWNiOTA4OTc0ZGNhODNjMzM0ODVlNDM1ZWQ1YThlMWRiZDY1MjFjNjE2ODcxNDAifX19")),
    LLAMA_SPIT(104, "Llama Spit"),
    PARROT(105, "Parrot", Arrays.asList(
	"YTRiYThkNjZmZWNiMTk5MmU5NGI4Njg3ZDZhYjRhNTMyMGFiNzU5NGFjMTk0YTI2MTVlZDRkZjgxOGVkYmMzIn19fQ==",
	"Yjc4ZTFjNWY0OGE3ZTEyYjI2Mjg1MzU3MWVmMWY1OTdhOTJlZjU4ZGE4ZmFhZmUwN2JiN2MwZTY5ZTkzIn19fQ==",
	"YWI5YTM2YzU1ODlmM2EyZTU5YzFjYWE5YjNiODhmYWRhNzY3MzJiZGI0YTc5MjYzODhhOGMwODhiYmJjYiJ9fX0=",
	"M2Q2ZjRhMjFlMGQ2MmFmODI0Zjg3MDhhYzYzNDEwZjFhMDFiYmI0MWQ3ZjRhNzAyZDk0NjljNjExMzIyMiJ9fX0=",
	"MmI5NGYyMzZjNGE2NDJlYjJiY2RjMzU4OWI5YzNjNGEwYjViZDVkZjljZDVkNjhmMzdmOGM4M2Y4ZTNmMSJ9fX0=",
	"ZGFjNjcwM2RlZDQ2ZDkzOWE2MjBmZTIyYzQzZTE4Njc0ZTEzZDIzYzk3NDRiZTAzNmIzNDgzYzFkMWNkZCJ9fX0=",
	"ZjBiZmE4NTBmNWRlNGIyOTgxY2NlNzhmNTJmYzJjYzdjZDdiNWM2MmNhZWZlZGRlYjljZjMxMWU4M2Q5MDk3In19fQ==",
	"ZjhhODJjOGI3NWRkMWMyY2U4MTMzYzBiYTkzOWI4YzUyZTQ3ZDNlYzM3NDk1MGY0N2RkZGJiZTM0NWUyMCJ9fX0=",
	"YWNhNTgwYjA1MWM2M2JlMjlkYTU0NWE5YWE3ZmY3ZTEzNmRmNzdhODFjNjdkYzFlZTllNjE3MGMxNGZiMzEwIn19fQ==")),
    VILLAGER(120, "Villager", Arrays.asList(
	"ODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19")),
    ENDER_CRYSTAL(200, "End Crystal"),
    TURTLE(901, "Turtle", Arrays.asList(
	"MGE0MDUwZTdhYWNjNDUzOTIwMjY1OGZkYzMzOWRkMTgyZDdlMzIyZjlmYmNjNGQ1Zjk5YjU3MThhIn19fQ==")),
    PHANTOM(902, "Phantom", Arrays.asList(
	"NDExZDI1YmNkYWJhZmFkNWZkNmUwMTBjNWIxY2Y3YTAwYzljY2E0MGM1YTQ2NzQ3ZjcwNmRjOWNiM2EifX19",
	"YWQyZmE1NjE4NDQ3NzYyZTI2MTExZTA2MGRjNTkzZWE2MjJkNWRkZmMzODVkN2U0MjUzMmU0NjMyN2Y4MDdjMCJ9fX0=")),
    TRIDENT(903, "Trident"),
    COD(904, "Cod", Arrays.asList(
	"NmY5OWI1ODBkNDVhNzg0ZTdhOTY0ZTdkM2IxZjk3Y2VjZTc0OTExMTczYmQyMWMxZDdjNTZhY2RjMzg1ZWQ1In19fQ==")),
    SALMON(905, "Salmon", Arrays.asList(
	"YWRmYzU3ZDA5MDU5ZTQ3OTlmYTkyYzE1ZTI4NTEyYmNmYWExMzE1NTc3ZmUzYTI3YWVkMzg5ZTRmNzUyMjg5YSJ9fX0=")),
    PUFFERFISH(906, "Pufferfish", Arrays.asList(
	"YTk1NTkzODg5OTNmZTc4MmY2N2JkNThkOThjNGRmNTZiY2Q0MzBlZGNiMmY2NmVmNTc3N2E3M2MyN2RlMyJ9fX0=")),
    TROPICAL_FISH(907, "Tropical Fish", Arrays.asList(
	"MzZkMTQ5ZTRkNDk5OTI5NjcyZTI3Njg5NDllNjQ3Nzk1OWMyMWU2NTI1NDYxM2IzMjdiNTM4ZGYxZTRkZiJ9fX0=")),
    DROWNED(908, "Drowned", Arrays.asList(
	"YzNmN2NjZjYxZGJjM2Y5ZmU5YTYzMzNjZGUwYzBlMTQzOTllYjJlZWE3MWQzNGNmMjIzYjNhY2UyMjA1MSJ9fX0=",
	"MWY4YmFhNDhiOGY1MTE5OTBlNDdkYjk2ODMyNGMxNTJiZDExNjc3MzFkZGYwMzQ1MzAwNDQ3MzVhNmJkMmVkNCJ9fX0=",
	"YzFhNzMyNTI0MDFhNmU5NDZmNjFkYmFjMGUwMjdkMTgzZTBhY2U1ODc1MmZhMTVhNjRkMjQ0OWZjZjUwODdiNyJ9fX0=",
	"Yzg0ZGY3OWM0OTEwNGIxOThjZGFkNmQ5OWZkMGQwYmNmMTUzMWM5MmQ0YWI2MjY5ZTQwYjdkM2NiYmI4ZTk4YyJ9fX0=",
	"ZmIxNTMxYzA0ZTI1ZDdmYTY0NTc2OTgyNjg0OTFjYjg5NmQzMzAyZDI2ODg0ZmNmZGYxYTBiMmY5MmQ3N2M4ZiJ9fX0=",
	"NTZkYWY1MGVhZjc2YzNhNmQ1YWQzOWM5NjZmMjk4NzdiOTFkOTUwZGQxZTM3MTIyZTljODE5NTg1Yzg5ZDkyZSJ9fX0=")),
    DOLPHIN(909, "Dolphin", Arrays.asList(
	"OGU5Njg4Yjk1MGQ4ODBiNTViN2FhMmNmY2Q3NmU1YTBmYTk0YWFjNmQxNmY3OGU4MzNmNzQ0M2VhMjlmZWQzIn19fQ==")),
    LINGERING_POTION(910, "Lingering Potion"),
    FISHING_HOOK(911, "Fishing Hook"),
    LIGHTNING(912, "Lightning Bolt"),
    WEATHER(913, "Weather"),
    PLAYER(914, "Player"),
    COMPLEX_PART(915, "Complex Part"),
    TIPPED_ARROW(916, "Tipped Arrow"),

    PANDA(917, "Panda", Arrays.asList(
	"ZDE4OGM5ODBhYWNmYTk0Y2YzMzA4ODUxMmIxYjk1MTdiYTgyNmIxNTRkNGNhZmMyNjJhZmY2OTc3YmU4YSJ9fX0=")),
    PILLAGER(918, "Pillager", Arrays.asList(
	"NGFlZTZiYjM3Y2JmYzkyYjBkODZkYjVhZGE0NzkwYzY0ZmY0NDY4ZDY4Yjg0OTQyZmRlMDQ0MDVlOGVmNTMzMyJ9fX0=")),
    RAVAGER(919, "Ravager", Arrays.asList(
	"MWNiOWYxMzlmOTQ4OWQ4NmU0MTBhMDZkOGNiYzY3MGM4MDI4MTM3NTA4ZTNlNGJlZjYxMmZlMzJlZGQ2MDE5MyJ9fX0=",
	"M2I2MjUwMWNkMWI4N2IzN2Y2MjgwMTgyMTBlYzU0MDBjYjY1YTRkMWFhYjc0ZTZhM2Y3ZjYyYWE4NWRiOTdlZSJ9fX0=")),
    TRADER_LLAMA(920, "Trader Llama", Arrays.asList(
	"ODQyNDc4MGIzYzVjNTM1MWNmNDlmYjViZjQxZmNiMjg5NDkxZGY2YzQzMDY4M2M4NGQ3ODQ2MTg4ZGI0Zjg0ZCJ9fX0=",
	"NzA4N2E1NTZkNGZmYTk1ZWNkMjg0NGYzNTBkYzQzZTI1NGU1ZDUzNWZhNTk2ZjU0MGQ3ZTc3ZmE2N2RmNDY5NiJ9fX0=",
	"YmU0ZDhhMGJjMTVmMjM5OTIxZWZkOGJlMzQ4MGJhNzdhOThlZTdkOWNlMDA3MjhjMGQ3MzNmMGEyZDYxNGQxNiJ9fX0=")),
    WANDERING_TRADER(921, "Wandering Trader", Arrays.asList(
	"NWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=")),
    FOX(922, "Fox", Arrays.asList(
	"YjZmZWI3NjFiMmY1OWZhYmU1Y2MzY2M4MmE5MzRiNTM0ZWE5OWVkYjkxMzJjY2RhOWY0ODRiZDU5ODZkNyJ9fX0=",
	"MjRhMDM0NzQzNjQzNGViMTNkNTM3YjllYjZiNDViNmVmNGM1YTc4Zjg2ZTkxODYzZWY2MWQyYjhhNTNiODIifX19",
	"MTZkYjdkNTA3Mzg5YTE0YmJlYzM5ZGU2OTIyMTY1YjMyZDQzNjU3YmNiNmFhZjRiNTE4MjgyNWIyMmI0In19fQ==")),
    CAT(923, "Cat", Arrays.asList(
	"N2M5Yjc0MDllN2I1MzgzYzE5YjM2MmIyYTBjYjQzZDUwOTNiMTNlMmIyMzRlOGExODkxNTYzZTU1ZWFlOWQ2OCJ9fX0=",
	"NTg4MDNmMDI3MGY4Y2RmNGUwZmU5NzMyZDQ5NjdjY2NjMGEyZjRmY2QxMThjZDE1MDAwOTc5YjE4ODg1MTQ0ZiJ9fX0=")),
    BEE(924, "Bee", Arrays.asList(
	"OTQ3MzIyZjgzMWUzYzE2OGNmYmQzZTI4ZmU5MjUxNDRiMjYxZTc5ZWIzOWM3NzEzNDlmYWM1NWE4MTI2NDczIn19fQ==",
	"OTlkYzNmMDBlY2FiMjI0OWJiNmExNmM4YzUxMTVjZWI5ZjIzMjA1YTBkNTVjYzBlOWJhYmQyNTYyZjc5NTljNCJ9fX0==",
	"ZTZiNzRlMDUyYjc0Mjg4Nzk5YmE2ZDlmMzVjNWQwMjIxY2Y4YjA0MzMxNTQ3ZWMyZjY4ZDczNTk3YWUyYzliIn19fQ==",
	"YmIxNzc3NDY2MjUxMmQ3ODdlZjc3YjFhNDZhMDRlMmM2ZmQ2Nzc5NGJmN2Y3Nzk1NjZlYjIxYzgxNDNhYWQ5ZSJ9fX0=")),
    
    ZOMBIFIED_PIGLIN(925, "Zombified Piglin", Arrays.asList("N2VhYmFlY2M1ZmFlNWE4YTQ5Yzg4NjNmZjQ4MzFhYWEyODQxOThmMWEyMzk4ODkwYzc2NWUwYThkZTE4ZGE4YyJ9fX0=")),
    HOGLIN(926, "Hoglin", Arrays.asList("OWJiOWJjMGYwMWRiZDc2MmEwOGQ5ZTc3YzA4MDY5ZWQ3Yzk1MzY0YWEzMGNhMTA3MjIwODU2MWI3MzBlOGQ3NSJ9fX0=")),
    PIGLIN(927, "Piglin", Arrays.asList("OWYxODEwN2QyNzVmMWNiM2E5Zjk3M2U1OTI4ZDU4NzlmYTQwMzI4ZmYzMjU4MDU0ZGI2ZGQzZTdjMGNhNjMzMCJ9fX0=")),
    STRIDER(928, "Strider", Arrays.asList("MThhOWFkZjc4MGVjN2RkNDYyNWM5YzA3NzkwNTJlNmExNWE0NTE4NjY2MjM1MTFlNGM4MmU5NjU1NzE0YjNjMSJ9fX0=")),
    ZOGLIN(929, "Zoglin", Arrays.asList("ZTY3ZTE4NjAyZTAzMDM1YWQ2ODk2N2NlMDkwMjM1ZDg5OTY2NjNmYjllYTQ3NTc4ZDNhN2ViYmM0MmE1Y2NmOSJ9fX0=")),
    
    // 1.16.2
    PIGLIN_BRUTE(930, "Piglin Brute", Arrays.asList("M2UzMDBlOTAyNzM0OWM0OTA3NDk3NDM4YmFjMjllM2E0Yzg3YTg0OGM1MGIzNGMyMTI0MjcyN2I1N2Y0ZTFjZiJ9fX0=")),

    // if possible we can remove this string for each texture to save up some space
    // eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv
    UNKNOWN(999, "Unknown");

    private int id;
    private String name;
    private String secondaryName;
    EntityType type = null;
    public static HashMap<String, ItemStack> cache = new HashMap<>();
    static HashMap<String, CMIEntityType> byName = new HashMap<>();

    static {
	for (CMIEntityType one : CMIEntityType.values()) {
	    byName.put(one.toString().replace("_", "").toLowerCase(), one);
	    byName.put(one.getName().replace("_", "").replace(" ", "").toLowerCase(), one);
	    byName.put(String.valueOf(one.getId()), one);
	    if (one.secondaryName != null)
		byName.put(one.secondaryName.replace("_", "").replace(" ", "").toLowerCase(), one);
	}
    }

    CMIEntityType(int id, String name, List<String> headTextures) {
	this(id, name, null, headTextures);
    }

    CMIEntityType(int id, String name) {
	this(id, name, null, new ArrayList<String>());
    }

    CMIEntityType(int id, String name, String secondaryName) {
	this(id, name, secondaryName, new ArrayList<String>());
    }

    CMIEntityType(int id, String name, String secondaryName, List<String> headTextures) {
	this.id = id;
	this.name = name;
	this.secondaryName = secondaryName;
    }

    public int getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public static CMIEntityType getById(int id) {
	CMIEntityType ttype = getByName(String.valueOf(id));
	return ttype == null ? CMIEntityType.PIG : ttype;
    }

    public static CMIEntityType getByType(EntityType entity) {
	return getByName(entity.toString());
    }

    public static CMIEntityType getByItem(ItemStack item) {
	if (item == null)
	    return null;

	if (CMIMaterial.isMonsterEgg(item.getType())) {
	    String name = item.getType().toString().replace("_SPAWN_EGG", "");
	    return getByName(name);
	}

	if (CMIMaterial.SPAWNER.equals(item.getType())) {
	    if (item.getItemMeta() instanceof BlockStateMeta) {
		BlockStateMeta bsm = (BlockStateMeta) item.getItemMeta();
		if (bsm.getBlockState() instanceof CreatureSpawner) {
		    CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
		    return CMIEntityType.getByType(bs.getSpawnedType());
		}
	    }
	}
	return null;
    }

    public static CMIEntityType getByName(String name) {
	return byName.get(name.toLowerCase().replace("_", ""));
    }

    public EntityType getType() {
	if (type != null)
	    return type;
	for (EntityType one : EntityType.values()) {
	    if (one.toString().equalsIgnoreCase(this.name())) {
		type = one;
		break;
	    }
	}
	return type;
    }

    public boolean isAlive() {
	return getType() != null && getType().isAlive();
    }

    public boolean isSpawnable() {
	return getType() != null && getType().isSpawnable();
    }

    public static String getRealNameByType(EntityType type) {
	if (type == null)
	    return null;
	CMIEntityType ctype = CMIEntityType.getByType(type);
	if (ctype != null)
	    return ctype.getName();
	String name = type.name();

	name = name.toLowerCase().replace('_', ' ');
	name = name.substring(0, 1).toUpperCase() + name.substring(1);
	return name;
    }

    public CMIMaterial getSpawnEggMaterial() {
	CMIMaterial m = CMIMaterial.get((this.equals(CMIEntityType.MUSHROOM_COW) ? "Mooshroom".toLowerCase() : this.toString().toLowerCase()) + "_spawn_egg");
	return m != null && m.isMonsterEgg() ? m : null;
    }
}