package com.gamingmesh.jobs.config;

import java.io.File;
import java.util.Arrays;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.DisplayMethod;

import net.Zrips.CMILib.FileHandler.ConfigReader;

public class ExampleJob {

    public static void updateExampleFile() {
        ConfigReader cfg = null;
        try {
            cfg = new ConfigReader(new File(Jobs.getFolder(), "jobs" + File.separator + ConfigManager.EXAMPLEJOBNAME.toUpperCase() + ".yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cfg == null)
            return;

        if (!cfg.getFile().isFile())
            return;

        cfg.header(Arrays.asList("Jobs configuration.", "",
            "This Job will be ignored so there is no need to remove it. Keep it as reference as it will contain all possible options.",
            "",
            "Stores information about each job",
            "",
            "NOTE: When having multiple jobs, both jobs will give the income payout to the player",
            "even if they give the pay for one action (make the configurations with this in mind)",
            "and each job will get the respective experience.",
            "",
            "e.g If player has 2 jobs where job1 gives 10 income and experience for killing a player ",
            "and job2 gives 5 income and experience for killing a player. When the user kills a player",
            "they will get 15 income and job1 will gain 10 experience and job2 will gain 5 experience."));

        String pt = "exampleJob";
        cfg.addComment(pt, "Must be one word",
            "This job will be ignored as this is just example of all possible actions.");
        cfg.addComment(pt + ".fullname", "full name of the job (displayed when browsing a job, used when joining and leaving",
            "also can be used as a prefix for the user's name if the option is enabled.",
            "Shown as a prefix only when the user has 1 job.",
            "",
            "NOTE: Must be 1 word");
        cfg.get(pt + ".fullname", "Woodcutter");

        cfg.addComment(pt + ".displayName", "Jobs display name used only for visualization in specific parts. Can contain spaces and color codes");
        cfg.get(pt + ".displayName", "&2--{#cancan}Woodcutter&2--");

        cfg.addComment(pt + ".shortname", "Shortened version of the name of the job. Used as a prefix when the user has more than 1 job.");
        cfg.get(pt + ".shortname", "W");
        cfg.get(pt + ".description", "Earns money felling and planting trees");

        cfg.addComment(pt + ".FullDescription", "Full description of job to be shown in job browse command");
        cfg.get(pt + ".FullDescription", Arrays.asList("&2Get money for:", "  &7Planting trees", "  &7Cutting down trees", "  &7Killing players"));

        cfg.addComment(pt + ".ChatColour",
            "The colour of the name, for a full list of supported colours, go to the message config. Hex color codes are supported as of 1.16 minecraft version. Example: {#6600cc} or {#Brown}");
        cfg.get(pt + ".ChatColour", "GREEN");

        cfg.addComment(pt + ".BossBarColour", "[OPTIONAL] The colour of the boss bar: GREEN, BLUE, RED, WHITE, YELLOW, PINK, PURPLE.");
        cfg.get(pt + ".BossBarColour", "WHITE");

        cfg.addComment(pt + ".chat-display", "Option to let you choose what kind of prefix this job adds to your name.", "Options are: ");
        for (DisplayMethod one : DisplayMethod.values()) {
            cfg.appendComment(pt + ".chat-display", one.getName() + " - " + one.getDesc());
        }
        cfg.get(pt + ".chat-display", "full");

        cfg.addComment(pt + ".max-level", "[OPTIONAL] - the maximum level of this class");
        cfg.get(pt + ".max-level", 10);

        cfg.addComment(pt + ".vip-max-level", "[OPTIONAL] - the maximum level of this class with specific permission",
            "use jobs.[jobsname].vipmaxlevel, in this case it will be jobs.exampleJob.vipmaxlevel");
        cfg.get(pt + ".vip-max-level", 20);

        cfg.addComment(pt + ".slots", "[OPTIONAL] - the maximum number of users on the server that can have this job at any one time (includes offline players).");
        cfg.get(pt + ".slots", 1);

        cfg.addComment(pt + ".softIncomeLimit", "[OPTIONAL] Soft limits will allow to stop income/exp/point payment increase at some particular level but allow further general leveling.",
            "In example if player is level 70, he will get paid as he would be at level 50, exp gain will be as he would be at lvl 40 and point gain will be as at level 60",
            "This only applies after players level is higher than provided particular limit.");

        cfg.get(pt + ".softIncomeLimit", 50);
        cfg.get(pt + ".softExpLimit", 40);
        cfg.get(pt + ".softPointsLimit", 60);

        cfg.addComment(pt + ".leveling-progression-equation", "Equation used for calculating how much experience is needed to go to the next level.",
            "Available parameters:",
            "  numjobs - the number of jobs the player has",
            "  maxjobs - the number of jobs the player have max",
            "  joblevel - the level the player has attained in the job.",
            " NOTE: Please take care of the brackets when modifying this equation.");
        cfg.get(pt + ".leveling-progression-equation", "10*(joblevel)+(joblevel*joblevel*4)");

        cfg.addComment(pt + ".income-progression-equation", "Equation used for calculating how much income is given per action for the job level.",
            "Available parameters:",
            "  numjobs - the number of jobs the player has",
            "  maxjobs - the number of jobs the player have max",
            "  baseincome - the income for the action at level 1 (as set in the configuration).",
            "  joblevel - the level the player has attained in the job.",
            "NOTE: Please take care of the brackets when modifying this equation.");
        cfg.get(pt + ".income-progression-equation", "baseincome+(baseincome*(joblevel-1)*0.01)-((baseincome+(joblevel-1)*0.01) * ((numjobs-1)*0.05))");

        cfg.addComment(pt + ".points-progression-equation", "Equation used for calculating how much points is given per action for the job level.",
            "Available parameters:",
            "  numjobs - the number of jobs the player has",
            "  maxjobs - the number of jobs the player have max",
            "  basepoints - the points for the action at level 1 (as set in the configuration).",
            "  joblevel - the level the player has attained in the job.",
            "NOTE: Please take care of the brackets when modifying this equation.");
        cfg.get(pt + ".points-progression-equation", "basepoints+(basepoints*(joblevel-1)*0.01)-((basepoints+(joblevel-1)*0.01) * ((numjobs-1)*0.05))");

        cfg.addComment(pt + ".experience-progression-equation", "Equation used for calculating how much experience is given per action for the job level.",
            "Available parameters:",
            "  numjobs - the number of jobs the player has",
            "  maxjobs - the number of jobs the player have max",
            "  baseexperience - the experience for the action at level 1 (as set in the configuration).",
            "  joblevel - the level the player has attained in the job.",
            "NOTE: Please take care of the brackets when modifying this equation.");
        cfg.get(pt + ".experience-progression-equation", "basepoints+(basepoints*(joblevel-1)*0.01)-((basepoints+(joblevel-1)*0.01) * ((numjobs-1)*0.05))");

        cfg.addComment(pt + ".rejoinCooldown", "Defines how often in seconds player can rejoin this job. Can be bypassed with jobs.rejoinbypass");
        cfg.get(pt + ".rejoinCooldown", 10);

        cfg.addComment(pt + ".Gui", "GUI icon information when using GUI function", "More information on usage at https://www.zrips.net/cmi/commands/icwol/");
        cfg.get(pt + ".Gui.ItemStack", "oaklog;DURABILITY:1;hideenchants");
        cfg.addComment(pt + ".Gui.slot", "Slot number to show the item in the specified row");
        cfg.get(pt + ".Gui.slot", 5);

        cfg.addComment(pt + ".maxDailyQuests",
            "Defines maximum amount of daily quests player can have from THIS job",
            "This will not have effect on overall quest amount player will have");
        cfg.get(pt + ".maxDailyQuests", 3);

        cfg.addComment(pt + ".Quests", "Daily quests",
            "Each job can have as many daily quests as you want",
            "Players will have access to quests from jobs he is currently working at");

        String questPt = pt + ".Quests.first";
        cfg.addComment(questPt, "Quest identification. Can be any ONE word or number or both of them. This doesn't have any real meaning but it can't repeat.");
        cfg.addComment(questPt + ".Name", "Quest name used for quests list, don't forget to enclose it with \" \"");
        cfg.get(questPt + ".Name", "Break Oak wood");
        cfg.addComment(questPt + ".Objectives", "This should be in a format as [actionType];[actionTarget];[amount]",
            "[actionType] can be any valid job action. Look lower for all possible action types",
            "[actionTarget] can be material name, block type, entity name and so on. This is defined in same way as any generic payable job action",
            "[amount] is how many times player should perform this action to complete quest");
        cfg.get(questPt + ".Objectives", "Break;oak_log;300");

        cfg.addComment(questPt + ".RewardCommands", "Command list to be performed after quest is finished.",
            "Use [playerName] to insert players name who finished that quest");
        cfg.get(questPt + ".RewardCommands", Arrays.asList("money give [playerName] 500", "msg [playerName] Completed quest!"));

        cfg.addComment(questPt + ".RewardAmount", "Reward amount to be given to player after quest is finished");
        cfg.get(questPt + ".RewardAmount", 0);

        cfg.addComment(questPt + ".RewardDesc", "Quest description to be used to explain quest requirements or rewards for player");
        cfg.get(questPt + ".RewardDesc", Arrays.asList("Break 300 Oak wood", "Get 500 bucks for this"));

        cfg.addComment(questPt + ".RestrictedAreas", "Restricted areas where player cant progress his quest");
        cfg.get(questPt + ".RestrictedAreas", Arrays.asList("Arenas", "myarena"));

        cfg.addComment(questPt + ".Chance", "Defines chance in getting this quest.",
            "If you have set 10 quests and player can have only 2, then quests with biggest chance will be picked most likely",
            "This will allow to have some rare quests with legendary rewards");
        cfg.get(questPt + ".Chance", 40);

        cfg.addComment(questPt + ".fromLevel", "Defines from which level you want to give option to get this quest",
            "You can use both limitations to have limited quests for particular job level ranges");
        cfg.get(questPt + ".fromLevel", 3);

        cfg.addComment(questPt + ".toLevel", "Defines to which job level you want to give out this quest.",
            "Keep in mind that player will keep quest even if he is over level limit if he got new one while being under",
            "In example: player with level 2 takes quests and levels up to level 5, he still can finish this quest and after next quest reset (check general config file)",
            "he will no longer have option to get this quest");
        cfg.get(questPt + ".toLevel", 5);

        cfg.addComment(pt + ".Break",
            "########################################################################",
            "Section used to configure what items the job gets paid for, how much",
            "they get paid and how much experience they gain.",
            "",
            "For break and place, the block material name is used.",
            "e.g ACACIA_LOG, DARK_OAK_FENCE, BIRCH_DOOR",
            "",
            "To get a list of all available block types, check the",
            "bukkit JavaDocs for a complete list of block types",
            "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html",
            "",
            "For kill tags (Kill and custom-kill), the name is the name of the mob.",
            "To get a list of all available entity types, check the",
            "bukkit JavaDocs for a complete list of entity types",
            "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html",
            "",
            "For custom-kill, it is the name of the job (case sensitive).",
            "",
            "NOTE: If a job has both the pay for killing a player and for killing a specific class, they will get both payments.",
            "#######################################################################",
            "payment for breaking a block");

        cfg.addComment(pt + ".Break.oak_log", "block name/id (with optional sub-type)");
        cfg.addComment(pt + ".Break.oak_log.income", "base income, can be not used if using point system");
        cfg.get(pt + ".Break.oak_log.income", 5D);
        cfg.addComment(pt + ".Break.oak_log.points", "base points, can be not used if using income system");
        cfg.get(pt + ".Break.oak_log.points", 5D);
        cfg.addComment(pt + ".Break.oak_log.experience", "base experience");
        cfg.get(pt + ".Break.oak_log.experience", 5D);
        cfg.addComment(pt + ".Break.oak_log.from-level", "(OPTIONAL) from which level of this job player can get money for this action",
            "if not given, then player will always get money for this action",
            "this can be used for any action");
        cfg.get(pt + ".Break.oak_log.from-level", 1);
        cfg.addComment(pt + ".Break.oak_log.until-level", "(OPTIONAL) until which level player can get money for this action.",
            "if not given, then there is no limit",
            "this can be used for any action");
        cfg.get(pt + ".Break.oak_log.until-level", 30);
        cfg.addComment(pt + ".Break.oak_log.softIncomeLimit", "(OPTIONAL) Soft limits will allow to stop income/exp/point payment increase at some particular level but allow further general leveling.",
            "In example if player is level 70, he will get paid as he would be at level 50, exp gain will be as he would be at lvl 40 and point gain will be as at level 60",
            "This only applies after players level is higher than provided particular limit.");
        cfg.get(pt + ".Break.oak_log.softIncomeLimit", 50);
        cfg.get(pt + ".Break.oak_log.softExpLimit", 40);
        cfg.get(pt + ".Break.oak_log.softPointsLimit", 60);

        cfg.addComment(pt + ".Break.gravel.income", "you can use minuses to take away money if the player break this block");
        cfg.get(pt + ".Break.gravel.income", -1D);

        cfg.addComment(pt + ".Collect", "Payment for collecting things from sweet berry bush, composter or honey. Keep in mind that you need to define item you get it and not block you click on.");
        generate(cfg, pt + ".Collect.sweet_berries");
        generate(cfg, pt + ".Collect.bonemeal");
        generate(cfg, pt + ".Collect.honeycomb");
        generate(cfg, pt + ".Collect.honey_bottle");
        generate(cfg, pt + ".Collect.glow_berries");

        cfg.addComment(pt + ".Bucket", "Payment for catching entities in a bucket. Define material of a bucket and not entity you are catching.");
        generate(cfg, pt + ".Bucket.PUFFERFISH_BUCKET");
        generate(cfg, pt + ".Bucket.SALMON_BUCKET");
        generate(cfg, pt + ".Bucket.COD_BUCKET");
        generate(cfg, pt + ".Bucket.TROPICAL_FISH_BUCKET");
        generate(cfg, pt + ".Bucket.AXOLOTL_BUCKET");
        generate(cfg, pt + ".Bucket.TADPOLE_BUCKET");
        
        cfg.addComment(pt + ".Bake", "Payment for cooking raw foods in camp fire");
        generate(cfg, pt + ".Bake.beef");
        generate(cfg, pt + ".Bake.porkchop");

        cfg.addComment(pt + ".StripLogs", "Payment for stripping wood logs, only for 1.13+ servers");
        generate(cfg, pt + ".StripLogs.stripped_acacia_log");
        generate(cfg, pt + ".StripLogs.stripped_oak_log");

        
        cfg.addComment(pt + ".Wax", "Payment for waxing blocks, only for 1.17+ servers");
        generate(cfg, pt + ".Wax.COPPER_BLOCK", 4, 4);
        generate(cfg, pt + ".Wax.EXPOSED_COPPER", 4.5, 4.5);
        generate(cfg, pt + ".Wax.WEATHERED_COPPER", 5, 5);
        generate(cfg, pt + ".Wax.OXIDIZED_COPPER", 6, 6);
        generate(cfg, pt + ".Wax.CUT_COPPER", 5, 5);
        generate(cfg, pt + ".Wax.EXPOSED_CUT_COPPER", 5.5, 5.5);
        generate(cfg, pt + ".Wax.WEATHERED_CUT_COPPER", 6, 6);
        generate(cfg, pt + ".Wax.OXIDIZED_CUT_COPPER", 7, 7);
        generate(cfg, pt + ".Wax.CUT_COPPER_STAIRS", 5.5, 5.5);
        generate(cfg, pt + ".Wax.EXPOSED_CUT_COPPER_STAIRS", 6, 6);
        generate(cfg, pt + ".Wax.WEATHERED_CUT_COPPER_STAIRS", 6.5, 6.5);
        generate(cfg, pt + ".Wax.OXIDIZED_CUT_COPPER_STAIRS", 7.5, 7.5);
        generate(cfg, pt + ".Wax.CUT_COPPER_SLAB", 5, 5);
        generate(cfg, pt + ".Wax.EXPOSED_CUT_COPPER_SLAB", 5.5, 5.5);
        generate(cfg, pt + ".Wax.WEATHERED_CUT_COPPER_SLAB", 6, 6);
        generate(cfg, pt + ".Wax.OXIDIZED_CUT_COPPER_SLAB", 7, 7);
        generate(cfg, pt + ".Wax.CHISELED_COPPER", 6, 6);
        generate(cfg, pt + ".Wax.EXPOSED_CHISELED_COPPER", 6.5, 6.5);
        generate(cfg, pt + ".Wax.WEATHERED_CHISELED_COPPER", 7, 7);
        generate(cfg, pt + ".Wax.OXIDIZED_CHISELED_COPPER", 8, 8);
        
        cfg.addComment(pt + ".Scrape", "Payment for scrapping copper blocks, only for 1.17+ servers");
        generate(cfg, pt + ".Scrape.EXPOSED_COPPER", 4.5, 4.5);
        generate(cfg, pt + ".Scrape.WEATHERED_COPPER", 5, 5);
        generate(cfg, pt + ".Scrape.OXIDIZED_COPPER", 6, 6);
        generate(cfg, pt + ".Scrape.EXPOSED_CUT_COPPER", 5.5, 5.5);
        generate(cfg, pt + ".Scrape.WEATHERED_CUT_COPPER", 6, 6);
        generate(cfg, pt + ".Scrape.OXIDIZED_CUT_COPPER", 7, 7);
        generate(cfg, pt + ".Scrape.EXPOSED_CUT_COPPER_STAIRS", 6, 6);
        generate(cfg, pt + ".Scrape.WEATHERED_CUT_COPPER_STAIRS", 6.5, 6.5);
        generate(cfg, pt + ".Scrape.OXIDIZED_CUT_COPPER_STAIRS", 7.5, 7.5);
        generate(cfg, pt + ".Scrape.EXPOSED_CUT_COPPER_SLAB", 5.5, 5.5);
        generate(cfg, pt + ".Scrape.WEATHERED_CUT_COPPER_SLAB", 6, 6);
        generate(cfg, pt + ".Scrape.OXIDIZED_CUT_COPPER_SLAB", 7, 7);
        generate(cfg, pt + ".Scrape.EXPOSED_CHISELED_COPPER", 6.5, 6.5);
        generate(cfg, pt + ".Scrape.WEATHERED_CHISELED_COPPER", 7, 7);
        generate(cfg, pt + ".Scrape.OXIDIZED_CHISELED_COPPER", 8, 8);

        cfg.addComment(pt + ".TNTBreak", "Payment for breaking a block with tnt");
        generate(cfg, pt + ".TNTBreak.oaklog");

        cfg.addComment(pt + ".Place", "Payment for placing a block");
        cfg.addComment(pt + ".Place.materials", "You can use list of materials to simplify adding each materials one by one", "Remember that you should separate the income, points and exp with ';'");
        cfg.get(pt + ".Place.materials", Arrays.asList("sapling;1.0;1.0;1.0", "wood;2.0;1.0", "stone;0.1"));

        cfg.addComment(pt + ".VTrade", "Payment for breaking a block with tnt");
        generate(cfg, pt + ".VTrade.emerald");
        cfg.addComment(pt + ".VTrade.enchanted_book-12", "you can add enchanted book with sub-id");
        generate(cfg, pt + ".VTrade.enchanted_book-12");

        cfg.addComment(pt + ".Kill", "Payment for killing any type of living entity");
        generate(cfg, pt + ".Kill.Player");

        cfg.addComment(pt + ".MMKill", "Payment for killing a MythicMob");
        generate(cfg, pt + ".MMKill.CustomNameHere");

        cfg.addComment(pt + ".custom-kill", "Killing player with certain job");
        generate(cfg, pt + ".custom-kill.Woodcutter");

        cfg.addComment(pt + ".Tame", "Taming animals");
        generate(cfg, pt + ".Tame.Wolf");

        cfg.addComment(pt + ".Breed", "Breeding animals");
        generate(cfg, pt + ".Breed.Wolf");

        cfg.addComment(pt + ".Eat", "Eating food");
        generate(cfg, pt + ".Eat.cooked_rabbit");
        generate(cfg, pt + ".Eat.baked_potato");

        cfg.addComment(pt + ".Milk", "Milking cows");
        generate(cfg, pt + ".Milk.Cow");
        generate(cfg, pt + ".Milk.MushroomCow");

        cfg.addComment(pt + ".Shear", "Shear sheeps by its color", "You can use 'color-all' identifier to specify all known colors.");
        generate(cfg, pt + ".Shear.Black");
        generate(cfg, pt + ".Shear.Blue");
        generate(cfg, pt + ".Shear.Brown");
        generate(cfg, pt + ".Shear.Cyan");
        generate(cfg, pt + ".Shear.Gray");
        generate(cfg, pt + ".Shear.Green");
        generate(cfg, pt + ".Shear.Light_Blue");
        generate(cfg, pt + ".Shear.Lime");
        generate(cfg, pt + ".Shear.Magenta");
        generate(cfg, pt + ".Shear.Orange");
        generate(cfg, pt + ".Shear.Pink");
        generate(cfg, pt + ".Shear.Purple");
        generate(cfg, pt + ".Shear.Red");
        generate(cfg, pt + ".Shear.Light_Gray");
        generate(cfg, pt + ".Shear.White");
        generate(cfg, pt + ".Shear.Yellow");

        cfg.addComment(pt + ".Dye", "dyeing armor");
        generate(cfg, pt + ".Dye.leather_boots");
        generate(cfg, pt + ".Dye.leather_chestplate");
        generate(cfg, pt + ".Dye.leather_helmet");
        generate(cfg, pt + ".Dye.leather_leggings");

        cfg.addComment(pt + ".Fish", "Catching fish");
        generate(cfg, pt + ".Fish.raw_fish");
        cfg.addComment(pt + ".Fish.legacy_raw_fish", "If you are using below version 1.13");
        generate(cfg, pt + ".Fish.legacy_raw_fish");

        cfg.addComment(pt + ".PyroFishingPro", "Catching CUSTOM fish of the PyroFishingPro plugin");
        generate(cfg, pt + ".PyroFishingPro.CustomTier");

        cfg.addComment(pt + ".CustomFishing", "Catching CUSTOM fish of the CustomFishing plugin");
        generate(cfg, pt + ".CustomFishing.CustomFishId");

        cfg.addComment(pt + ".Repair", "Repairing items");
        generate(cfg, pt + ".Repair.wood_sword");
        generate(cfg, pt + ".Repair.iron_sword");

        cfg.addComment(pt + ".Craft", "Crafting items");
        generate(cfg, pt + ".Craft.wood_sword");
        generate(cfg, pt + ".Craft.leather_boots");
        cfg.addComment(pt + ".Craft.!Healing Bandage", "Add ! at front when you want to pay for crafted items with special names. Always use double quotation marks, same as example");
        generate(cfg, pt + ".Craft.!Healing Bandage");
        cfg.addComment(pt + ".Craft.tipped_arrow:slowness", "If you add ':' after the tipped_arrow then you can use effect names like in example");
        generate(cfg, pt + ".Craft.tipped_arrow:slowness");

        cfg.addComment(pt + ".Smelt", "Smelting ores in any type of furnaces");
        generate(cfg, pt + ".Smelt.iron_ingot");
        generate(cfg, pt + ".Smelt.gold_ingot");

        cfg.addComment(pt + ".Enchant", "Smelting ores in any type of furnaces");
        generate(cfg, pt + ".Enchant.wood_sword");
        generate(cfg, pt + ".Enchant.leather_boots");
        cfg.addComment(pt + ".Enchant.DIG_SPEED-1", "Or/and you can give money for each enchantment they got");
        generate(cfg, pt + ".Enchant.DIG_SPEED-1");
        generate(cfg, pt + ".Enchant.dig_speed-2");

        cfg.addComment(pt + ".Brew", "Brewing miscellaneous items");
        generate(cfg, pt + ".Brew.nether_stalk");
        generate(cfg, pt + ".Brew.redstone");

        cfg.addComment(pt + ".Brush", "Brushing blocks and getting items from them");
        generate(cfg, pt + ".Brush.suspicious_sand");
        generate(cfg, pt + ".Brush.suspicious_gravel");
        generate(cfg, pt + ".Brush.coal");

        cfg.addComment(pt + ".Explore", "Explore options. Each number represents players number in exploring that chunk",
            "1 means that player is first in this chunk, 2 is second and so on",
            "so you can give money not only for first player who discovers that chunk");
        generate(cfg, pt + ".Explore.1");
        generate(cfg, pt + ".Explore.2");
        generate(cfg, pt + ".Explore.3");
        generate(cfg, pt + ".Explore.4");
        generate(cfg, pt + ".Explore.5");

        cfg.addComment(pt + ".permissions", "permissions granted for joining to a job");
        cfg.addComment(pt + ".permissions.firstNode", "example node", "Any name can be accepted");
        cfg.addComment(pt + ".permissions.firstNode.value", "true to give, false to revoke");
        cfg.get(pt + ".permissions.firstNode.value", true);
        cfg.addComment(pt + ".permissions.firstNode.permission", "The permission node");
        cfg.get(pt + ".permissions.firstNode.permission", "atest.node");
        cfg.addComment(pt + ".permissions.firstNode.level", "minimum level needed to grant permission. Use 0 for all levels");
        cfg.get(pt + ".permissions.firstNode.level", 0);
        cfg.get(pt + ".permissions.secNode.value", true);
        cfg.get(pt + ".permissions.secNode.permission", "atest.node2");
        cfg.addComment(pt + ".permissions.secNode.level", "Permission granted when reaching level 10");
        cfg.get(pt + ".permissions.secNode.level", 10);

        cfg.addComment(pt + ".conditions", "Permissions granted when particular conditions are met");
        cfg.addComment(pt + ".conditions.first", "Condition name, irrelevant, you can write anything in here");
        cfg.addComment(pt + ".conditions.first.requires", "j marks that player should have particular jobs level and higher");
        cfg.get(pt + ".conditions.first.requires", Arrays.asList("j:Miner-50", "j:Digger-50", "p:essentials.notnoob"));
        cfg.addComment(pt + ".conditions.first.perform", "p marks permission, player will get if given true value, if used false, permission will be taken");
        cfg.get(pt + ".conditions.first.perform", Arrays.asList("p:essentials.fly-true"));

        cfg.addComment(pt + ".commands", "Commands executed when player reached level");
        cfg.addComment(pt + ".commands.fly", "command name, just to have better idea what this do");
        cfg.addComment(pt + ".commands.fly.command", "Command its self, this will be executed from console, so all commands should work",
            "Possible variables are: [player] [jobname] [oldlevel] [newlevel]");
        cfg.get(pt + ".commands.fly.command", "lp user [player] permission set essentials.fly");
        cfg.addComment(pt + ".commands.fly.levelFrom", "When to execute this command first time", "Set to 0 if you want to detect all the levels");
        cfg.get(pt + ".commands.fly.levelFrom", 100);
        cfg.addComment(pt + ".commands.fly.levelUntil", "Until when to do this command", "This can be set to same level as levelFrom, so this command will be executed only once",
            "Set to 0 if you want to detect all the levels");
        cfg.get(pt + ".commands.fly.levelUntil", 100);
        cfg.get(pt + ".commands.kit.command", Arrays.asList("lp user [player] permission set essentials.kits.woodcutter", "msg [player] Now you can use woodcutter kit!"));
        cfg.get(pt + ".commands.kit.levelFrom", 150);
        cfg.get(pt + ".commands.kit.levelUntil", 150);

        cfg.addComment(pt + ".commands-on-max-level", "Perform specific commands when a player reaches the max level of this job.",
            "Players can have vip max level and this will be performed when they reach the max vip level.",
            "You can use 'player:' or 'console:' prefix tag to perform for specific senders.");
        cfg.get(pt + ".commands-on-max-level", Arrays.asList("msg [playerName] Max level of [job] reached!", "player:jobs stats"));

        cfg.addComment(pt + ".reverse-world-blacklist-functionality", "Turns the 'world-blacklist' list into a whitelist. This essentially means the job only works in the specified worlds.");
        cfg.get(pt + ".reverse-world-blacklist-functionality", false);

        cfg.addComment(pt + ".world-blacklist", "World list in which this job will not work. World name should be exact");
        cfg.get(pt + ".world-blacklist", Arrays.asList("plotworld", "teamworld"));

        cfg.addComment(pt + ".ignore-jobs-max", "Allow a player to '/jobs join' this job even if they have the max jobs permission reached.");
        cfg.get(pt + ".ignore-jobs-max", false);

        cfg.get(pt + ".cmd-on-join", Arrays.asList("msg [name] Thx for joining this job!", "msg [name] Now start working and get money from [jobname] job!"));
        cfg.get(pt + ".cmd-on-leave", Arrays.asList("msg [name] You have left this awesome [jobname] job", "msg [name] See you soon!"));

        cfg.addComment(pt + ".limitedItems", "Limit item use to jobs level");
        cfg.addComment(pt + ".limitedItems.firstOne", "Just name, don't have any impact");

        cfg.addComment(pt + ".limitedItems.firstOne.ItemStack", "Tool/Weapon data. More information on usage www.zrips.net/cmi/commands/icwol/");
        cfg.get(pt + ".limitedItems.firstOne.ItemStack", "DIAMOND_PICKAXE;n{&8Miner_Pickaxe};l{&eBobs_pick\\n&710%_bonus_XP};DAMAGE_ALL:1,FIRE_ASPECT:1");
        cfg.addComment(pt + ".limitedItems.firstOne.level", "Level of this job player can start using this item");
        cfg.get(pt + ".limitedItems.firstOne.level", 5);

        cfg.save();
    }

    private static void generate(ConfigReader cfg, String pt) {
        generate(cfg, pt, 1, 1);
    }

    private static void generate(ConfigReader cfg, String pt, double income, double experience) {
        cfg.get(pt + ".income", income);
        cfg.get(pt + ".experience", experience);
    }
}
