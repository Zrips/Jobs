package com.gamingmesh.jobs.container;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Locale.LC;

public enum MessageToggleType {
    ActionBar, BossBar, ChatText;

    public static MessageToggleType getByName(String name) {
        for (MessageToggleType state : MessageToggleType.values()) {
            if (state.name().equalsIgnoreCase(name))
                return state;
        }
        return null;
    }

    public static MessageToggleType getFromID(int id) {
        return MessageToggleType.values()[CMINumber.clamp(id, 0, MessageToggleType.values().length - 1)];
    }

    public static String toCommaSeparatedString() {
        StringBuilder str = new StringBuilder();
        for (MessageToggleType state : MessageToggleType.values()) {
            if (!str.toString().isEmpty())
                str.append(LC.info_ListSpliter.getLocale());
            str.append(state.name());
        }
        return str.toString();
    }

    public MessageToggleType getNext() {
        int index = this.ordinal();
        index++;
        index = index >= MessageToggleType.values().length ? 0 : index;
        return MessageToggleType.values()[index];
    }
}
