package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gamingmesh.jobs.Jobs;

public class JobsTop {

    private static final int CACHE_LIMIT = 150;
    private static final long CACHE_COOLDOWN_MS = 5000;

    private static JobsTop globalTop = new JobsTop();

    public static void updateGlobalTop(UUID uuid, List<JobProgression> progress) {
        CompletableFuture.runAsync(() -> {
            int level = 0;
            double experience = 0;

            synchronized (progress) {
                for (JobProgression prog : progress) {
                    if (prog.getLevel() == 1 && prog.getExperience() == 0)
                        continue;
                    level += prog.getLevel();
                    experience += prog.getExperience();
                }
            }

            if (level == 0 && experience == 0) {
                globalTop.remove(uuid);
            } else
                globalTop.update(uuid, level, experience);
        });
    }

    public static void updateGlobalTop(UUID uuid, int level, double experience) {
        globalTop.update(uuid, level, experience);
    }

    public static void removeFromGlobalTop(UUID uuid) {
        globalTop.remove(uuid);
    }

    public static @Nullable UUID getGlobalTop(int index) {
        return globalTop.getByPosition(index);
    }

    public static List<UUID> getGlobalTopList(int limit) {
        return globalTop.getTop(limit);
    }

    public static @Nullable topStats getGlobalStats(UUID uuid) {
        return globalTop.getStats(uuid);
    }

    public static void updateTops(@NotNull Job job, @NotNull JobsPlayer jPlayer, int level, double experience) {
        if (jPlayer == null)
            return;
        job.updateTop(jPlayer.getUniqueId(), level, experience);
        JobsTop.updateGlobalTop(jPlayer.getUniqueId(), jPlayer.getJobProgression());
    }

    private final NavigableMap<Integer, NavigableMap<Double, Set<UUID>>> rankingMap = new TreeMap<>(Comparator.reverseOrder());
    private final Map<UUID, topStats> uuidToStats = new HashMap<>();
    private List<UUID> topCache = new ArrayList<>();

    private boolean cacheDirty = true;
    private long lastCacheUpdate = 0L;
    private volatile boolean cacheRebuilding = false;

    public static class topStats {
        int level;
        double experience;

        topStats(int level, double experience) {
            this.level = level;
            this.experience = experience;
        }

        public int getLevel() {
            return level;
        }

        public double getExperience() {
            return experience;
        }
    }

    public synchronized void updateAsync(@NotNull UUID uuid, int level, double experience) {
        CompletableFuture.runAsync(() -> {
            update(uuid, level, experience);
        });
    }

    public synchronized void update(@NotNull UUID uuid, int level, double experience) {
        if (uuid == null)
            return;
        synchronized (this) {
            if (uuidToStats.containsKey(uuid)) {
                topStats prev = uuidToStats.get(uuid);
                removeFromMap(uuid, prev.level, prev.experience);
            }

            // Only add if level > 1 or experience > 0 to avoid empty entries
            if (level > 1 || experience > 0) {
                rankingMap
                    .computeIfAbsent(level, l -> new TreeMap<>(Comparator.reverseOrder()))
                    .computeIfAbsent(experience, e -> new HashSet<>())
                    .add(uuid);

                uuidToStats.put(uuid, new topStats(level, experience));
            }

            cacheDirty = true;

            if (Jobs.fullyLoaded)
                rebuildCacheAsync();
        }
    }

    public synchronized void removeAsync(@NotNull UUID uuid) {
        CompletableFuture.runAsync(() -> {
            remove(uuid);
        });
    }

    public @Nullable topStats getStats(@NotNull UUID uuid) {
        if (uuid == null)
            return null;
        return uuidToStats.get(uuid);
    }

    public synchronized void remove(UUID uuid) {
        synchronized (this) {
            topStats stats = uuidToStats.remove(uuid);
            if (stats != null) {
                removeFromMap(uuid, stats.level, stats.experience);
                cacheDirty = true;

                rebuildCacheAsync();
            }
        }
    }

    private void removeFromMap(UUID uuid, int level, double experience) {
        NavigableMap<Double, Set<UUID>> expMap = rankingMap.get(level);
        if (expMap == null)
            return;

        Set<UUID> uuids = expMap.get(experience);
        if (uuids != null) {
            uuids.remove(uuid);
            if (uuids.isEmpty())
                expMap.remove(experience);
        }

        if (expMap.isEmpty()) {
            rankingMap.remove(level);
        }
    }

    private void rebuildCacheAsync() {
        CompletableFuture.runAsync(this::rebuildCache);
    }

    private void rebuildCache() {
        long now = System.currentTimeMillis();
        if (!cacheDirty || now - lastCacheUpdate < CACHE_COOLDOWN_MS || cacheRebuilding)
            return;
        cacheRebuilding = true;
        synchronized (this) {
            cycle();
            lastCacheUpdate = now;
            cacheDirty = false;
            cacheRebuilding = false;
        }
    }

    private void cycle() {
        topCache = new ArrayList<>(CACHE_LIMIT);
        for (Entry<Integer, NavigableMap<Double, Set<UUID>>> levelEntry : rankingMap.entrySet()) {
            for (Entry<Double, Set<UUID>> expEntry : levelEntry.getValue().entrySet()) {
                for (UUID uuid : expEntry.getValue()) {
                    topCache.add(uuid);
                    if (topCache.size() >= CACHE_LIMIT)
                        return;
                }
            }
        }
    }

    public synchronized List<UUID> getTop(int limit) {
        if (topCache.isEmpty())
            rebuildCache();
        else
            rebuildCacheAsync();
        if (limit < 1)
            return new ArrayList<>(topCache);

        return new ArrayList<>(topCache.subList(0, Math.min(limit, topCache.size())));
    }

    public synchronized @Nullable UUID getByPosition(int index) {
        if (topCache.isEmpty())
            rebuildCache();
        else
            rebuildCacheAsync();
        return (index >= 0 && index < topCache.size()) ? topCache.get(index) : null;
    }
}
