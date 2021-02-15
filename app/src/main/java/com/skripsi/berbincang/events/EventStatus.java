

package com.skripsi.berbincang.events;

import lombok.Data;

@Data
public class EventStatus {
    private long userId;
    private EventType eventType;
    private boolean allGood;

    public EventStatus(long userId, EventType eventType, boolean allGood) {
        this.userId = userId;
        this.eventType = eventType;
        this.allGood = allGood;
    }

    public enum EventType {
        PUSH_REGISTRATION, CAPABILITIES_FETCH, SIGNALING_SETTINGS, CONVERSATION_UPDATE, PARTICIPANTS_UPDATE
    }

}
