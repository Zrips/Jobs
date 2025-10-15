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

import org.jetbrains.annotations.Nullable;

import com.gamingmesh.jobs.Jobs;

public class JobsQuestTop {

    private static final int CACHE_LIMIT = 150;
    private static final long CACHE_COOLDOWN_MS = 5000;

    private static JobsQuestTop globalTop = new JobsQuestTop();

    public static void updateGlobalTop(UUID uuid, int doneQuests) {
        CompletableFuture.runAsync(() -> {
            if (doneQuests == 0) {
                globalTop.remove(uuid);
            } else
                globalTop.update(uuid, doneQuests);
        });
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

    public static @Nullable Integer getGlobalCount(UUID uuid) {
        return globalTop.getCount(uuid);
    }

    private final NavigableMap<Integer, Set<UUID>> rankingMap = new TreeMap<>(Comparator.reverseOrder());
    private final Map<UUID, Integer> uuidToStats = new HashMap<>();
    private List<UUID> topCache = new ArrayList<>();

    private boolean cacheDirty = true;
    private long lastCacheUpdate = 0L;
    private volatile boolean cacheRebuilding = false;

    public synchronized void updateAsync(UUID uuid, int level) {
        CompletableFuture.runAsync(() -> {
            update(uuid, level);
        });
    }

    public synchronized void update(UUID uuid, int level) {
        synchronized (this) {
            if (uuidToStats.containsKey(uuid)) {
                Integer prev = uuidToStats.get(uuid);
                removeFromMap(uuid, prev);
            }

            // Only add if level > 1 to avoid empty entries
            if (level > 1) {
                rankingMap
                    .computeIfAbsent(level, l -> new HashSet<>())
                    .add(uuid);

                uuidToStats.put(uuid, level);
            }

            cacheDirty = true;

            if (Jobs.fullyLoaded)
                rebuildCacheAsync();
        }
    }

    public synchronized void removeAsync(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            remove(uuid);
        });
    }

    public @Nullable Integer getCount(UUID uuid) {
        return uuidToStats.get(uuid);
    }

    public synchronized void remove(UUID uuid) {
        synchronized (this) {
            Integer count = uuidToStats.remove(uuid);
            if (count != null) {
                removeFromMap(uuid, count);
                cacheDirty = true;

                rebuildCacheAsync();
            }
        }
    }

    private void removeFromMap(UUID uuid, int level) {
        Set<UUID> uuids = rankingMap.get(level);
        if (uuids == null)
            return;

        uuids.remove(uuid);
        if (uuids.isEmpty())
            rankingMap.remove(level);
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
        for (Entry<Integer, Set<UUID>> levelEntry : rankingMap.entrySet()) {
            for (UUID uuid : levelEntry.getValue()) {
                topCache.add(uuid);
                if (topCache.size() >= CACHE_LIMIT)
                    return;
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
