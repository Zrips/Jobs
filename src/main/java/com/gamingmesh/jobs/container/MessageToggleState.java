package com.gamingmesh.jobs.container;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Locale.LC;

public enum MessageToggleState {
    Off, Rapid, Batched;

    public static MessageToggleState getByName(String name) {
        for (MessageToggleState state : MessageToggleState.values()) {
            if (state.name().equalsIgnoreCase(name))
                return state;
        }
        return MessageToggleState.Rapid;
    }

    public static MessageToggleState getFromID(int id) {
        return MessageToggleState.values()[CMINumber.clamp(id, 0, MessageToggleState.values().length - 1)];

    }

    public static String toCommaSeparatedString() {
        StringBuilder str = new StringBuilder();
        for (MessageToggleState state : MessageToggleState.values()) {
            if (!str.toString().isEmpty())
                str.append(LC.info_ListSpliter.getLocale());
            str.append(state.name());
        }
        return str.toString();
    }

    public MessageToggleState getNext() {
        int index = this.ordinal();
        index++;
        index = index >= MessageToggleState.values().length ? 0 : index;
        return MessageToggleState.values()[index];
    }
}
