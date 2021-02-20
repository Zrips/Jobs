[![GitHub license](https://img.shields.io/badge/license-Apache-brightgreen.svg)](https://github.com/Zrips/Jobs/blob/master/LICENSE-Apache.txt) [![GitHub license](https://img.shields.io/badge/license-GNU--GPL-brightgreen.svg)](https://github.com/Zrips/Jobs/blob/master/LICENSE-GNU-GPL.txt)

[![GitHub Pre-Release](https://img.shields.io/github/release-pre/Zrips/Jobs.svg)](https://github.com/Zrips/Jobs/releases) [![Github All Releases](https://img.shields.io/github/downloads/Zrips/Jobs/total.svg)](https://github.com/Zrips/Jobs/releases)
[![GitHub issues](https://img.shields.io/github/issues/Zrips/Jobs.svg)](https://github.com/Zrips/Jobs/issues) [![GitHub pulls](https://img.shields.io/github/issues-pr/Zrips/Jobs.svg)](https://github.com/Zrips/Jobs/pulls)

Jobs Plugin for the BukkitAPI
***

# Jobs
![Image of Jobs](https://www.spigotmc.org/data/resource_icons/4/4216.jpg?1424463769)

_Original author and manager of this was phrstbrn until [v2.12.0](https://dev.bukkit.org/projects/jobs/files/808311) version._

A fully configurable plugin that allows you to get paid for breaking, placing, killing, fishing, and crafting, and more. Class based professions, gain experience as you perform your job.

Links
- Bukkit: https://dev.bukkit.org/projects/jobs-reborn
- Spigot: https://www.spigotmc.org/resources/4216/

# Jobs API
You can manually add the jar file to your build path or you can use jitpack if you use maven or gradle:
## Maven
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
        <version>LATEST</version> <!-- Change the LATEST to the current version of jobs -->
        <scope>provided</scope>
    </dependency>
</dependencies>
```
## Gradle
```gradle
repositories {
    maven { 
        url 'https://jitpack.io' 
    }
}
dependencies {
    implementation 'com.github.Zrips:Jobs:LATEST' //Change the LATEST to the current version of jobs
}
```

For API events: https://github.com/Zrips/Jobs/wiki/API
