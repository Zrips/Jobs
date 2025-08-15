# Jobs Reborn

![Image of Jobs](https://www.spigotmc.org/data/resource_icons/4/4216.jpg?1424463769)

-------

[![GitHub license](https://img.shields.io/badge/license-Apache-brightgreen.svg)](https://github.com/Zrips/Jobs/blob/master/LICENSE-Apache.txt) [![GitHub license](https://img.shields.io/badge/license-GNU--GPL-brightgreen.svg)](https://github.com/Zrips/Jobs/blob/master/LICENSE-GNU-GPL.txt) [![GitHub issues](https://img.shields.io/github/issues/Zrips/Jobs.svg)](https://github.com/Zrips/Jobs/issues) [![GitHub pulls](https://img.shields.io/github/issues-pr/Zrips/Jobs.svg)](https://github.com/Zrips/Jobs/pulls)

-------

## Introduction

Welcome to the Minecraft plugin that makes the gaming experience for your players even more exciting! 

With this plugin, a player can pick their favorite job(s), such as building, mining, fishing, and crafting. By just playing they can earn rewards like job points, job experience that helps them level up, and ingame money (requires economy engine). 

Whether they're a seasoned player or just starting out, they'll love the class-based professions and the satisfaction of leveling up as they master their chosen job(s). 

Get ready for a whole new world of fun and rewards! This plugin works great together with the premium plugin CMI.

## Required Dependancy

To ensure the best experience with the Zrips made plugins, including Jobs Reborn, it's important to have the CMILib library installed. 

We've made it easy for you by providing the latest .jar file. Simply make sure to include this library when you first install Jobs (or when you upgrade to a newer version), and you'll be on your way to enjoying all the fun and benefits our plugins have to offer.

## Releases / Downloads

We highly recommend keeping the Jobs Reborn .jar file, as well as all Zrips plugins, up-to-date. You can easily download the latest releases from the following links:

- CMILib Library: https://www.zrips.net/cmilib/
- Jobs-Reborn Plugin: https://www.spigotmc.org/resources/4216/
- For the tech-savvy, you can also access the latest Jobs source code on Github and compile it yourself.
- The old Bukkit Platform: https://dev.bukkit.org/projects/jobs-reborn

## Minecraft Server Engines

Jobs is designed for and officially supported on the following server engines:

- Spigot: https://www.spigotmc.org/
- Paper: https://papermc.io/

While other server engines are not officially supported, direct forks of Spigot and Paper _may_ still be compatible. Here are a few examples:

- PurPur
- Tuinity

Please note that these unsupported engines are used 'as is' and may not offer the same level of support or performance as the officially supported engines.

## Installation Instructions

To install the Jobs-Reborn plugin along with the CMILib library, follow these steps:

- Backup your server by running the /stop command.
- Download the latest CMILib .jar file and place it in the ~/plugins/ directory.
- Also include the Jobs-Reborn .jar file in the ~/plugins/ directory.
- Start your server back up.
- After the initial run, if you see the message 'Done!' with no issues, the plugin will have generated the necessary language and configuration files.
- Stop and restart the server one more time to review the plugin configurations. You can customize jobs, economy, chat settings, and more by following the guidance on the zrips.net > Jobs pages.

## Basic Permissions for Players

The Jobs-Reborn plugin requires some configuration in order for players to use the plugin, this includes the ability to use it, to browse and pick jobs, and to only get paid out in certain worlds. Below is an example of some basic permissions you can consider using to achieve that. (This requires a permission manager)

```
jobs.command.browse
jobs.command.info
jobs.command.join
jobs.command.leave
jobs.command.stats
jobs.command.top
jobs.join.*
jobs.max.3
jobs.use
jobs.world.world_overworld
jobs.world.world_theend
jobs.world.world_thenether
```

For example, using LuckPerms you could update the default group as such:
```
lp group default permission set jobs.use true
lp group default permission set jobs.max.1 true
lp group donator permission set jobs.max.2 true
```

## Bugs and Suggestions

Feel free to clone and submit a pull request with bug fixes or code suggestions. 

If you're not familiar with the process, you can also report bugs and make suggestions as an issue (if one doesn't already exist) under the [Issues tab](https://github.com/Zrips/Jobs/issues) on Github.

## Support

You can request support from other Community Members on the [Zrips Community Discord](https://discord.gg/dDMamN4). (Pick the right roles, to unlock and show the plugins' #help channels.)

Please do check [the zrips.net/jobs website](https://www.zrips.net/jobs/) out first, before asking questions.

## Jobs API

- Information: https://www.zrips.net/jobs/api/

You can manually add the .jar file to your build path or you can use Jitpack if you use Maven or Gradle:

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Zrips</groupId>
        <artifactId>Jobs</artifactId>
        <version>v5.2.6.2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle

```gradle
repositories {
    maven { 
        url 'https://jitpack.io' 
    }
}
dependencies {
    implementation 'com.github.Zrips:Jobs:v5.2.6.2'
}
```

### API Events

For API events: https://github.com/Zrips/Jobs/wiki/API

## Credits

- _Original author and manager of this was phrstbrn until [v2.12.0](https://dev.bukkit.org/projects/jobs/files/808311) version._
- Current development: [Zrips](https://www.spigotmc.org/resources/authors/zrips.24572/)
- Contributions: https://github.com/Zrips/Jobs/graphs/contributors

