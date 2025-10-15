package com.gamingmesh.jobs.stuff;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.MessageToggleState;
import com.gamingmesh.jobs.container.MessageToggleType;

public class ToggleBarHandling {

    static Map<MessageToggleType, Map<UUID, MessageToggleState>> toggleMap = new HashMap<>();

    private static int defaultStatesAsInt = 1000;

    public static void init() {
        StringBuilder result = new StringBuilder();
        // Starts with 1 in case remaining are 0's to produce actual full digit number
        result.append("1");
        for (MessageToggleType one : MessageToggleType.values()) {
            result.append(getDefaultState(one).ordinal());
        }
        defaultStatesAsInt = Integer.parseInt(result.toString());
    }

    @Deprecated
    public static Map<UUID, MessageToggleState> getActionBarToggle() {
        return toggleMap.getOrDefault(MessageToggleType.ActionBar, new HashMap<>());
    }

    @Deprecated
    public static Map<UUID, MessageToggleState> getBossBarToggle() {
        return toggleMap.getOrDefault(MessageToggleType.BossBar, new HashMap<>());
    }

    public static MessageToggleState modify(UUID uuid, MessageToggleType type, MessageToggleState state) {
        synchronized (toggleMap) {
            return toggleMap.computeIfAbsent(type, k -> new HashMap<>()).put(uuid, state);
        }
    }

    public static MessageToggleState getActionBarState(UUID uuid) {
        synchronized (toggleMap) {
            return getState(uuid, MessageToggleType.ActionBar);
        }
    }

    public static MessageToggleState getBossBarState(UUID uuid) {
        synchronized (toggleMap) {
            return getState(uuid, MessageToggleType.BossBar);
        }
    }

    public static MessageToggleState getChatTextState(UUID uuid) {
        synchronized (toggleMap) {
            return getState(uuid, MessageToggleType.ChatText);
        }
    }

    public static MessageToggleState getState(UUID uuid, MessageToggleType type) {
        synchronized (toggleMap) {
            return toggleMap.getOrDefault(type, new HashMap<>()).getOrDefault(uuid, getDefaultState(type));
        }
    }

    private static MessageToggleState getDefaultState(MessageToggleType type) {
        switch (type) {
        default:
        case ActionBar:
            return Jobs.getGCManager().ActionBarsMessageDefault;
        case BossBar:
            return Jobs.getGCManager().BossBarsMessageDefault;
        case ChatText:
            return Jobs.getGCManager().ChatTextMessageDefault;
        }
    }

    public static @Nullable Integer getPlayerOptionsAsInt(UUID uuid) {
        StringBuilder result = new StringBuilder();
        // Starts with 1 in case remaining are 0's to produce actual full digit number
        result.append("1");
        for (MessageToggleType one : MessageToggleType.values()) {
            result.append(getState(uuid, one).ordinal());
        }

        int options = Integer.parseInt(result.toString());
        if (options == defaultStatesAsInt)
            return null;

        return options;
    }

    public static void recordPlayerOptionsFromInt(UUID uuid, @Nullable Integer options) {

        // Value should be over 10 where first number isint part of options value
        if (options == null || options < 10)
            return;

        int[] values = Integer
            .toString(options)
            // removing first number which should be 1 as a filler
            .substring(1)
            .chars()
            .map(c -> c - '0')
            .toArray();

        for (MessageToggleType type : MessageToggleType.values()) {

            if (type.ordinal() >= values.length)
                break;

            MessageToggleState state = MessageToggleState.getFromID(values[type.ordinal()]);
            // Only recording if values isint default one
            if (state != null && !getDefaultState(type).equals(state))
                toggleMap.computeIfAbsent(type, k -> new HashMap<>()).put(uuid, state);
        }
    }
}
