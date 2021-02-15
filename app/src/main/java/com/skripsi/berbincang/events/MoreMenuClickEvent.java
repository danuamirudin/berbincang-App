

package com.skripsi.berbincang.events;

import com.skripsi.berbincang.models.json.conversations.Conversation;
import lombok.Data;

@Data
public class MoreMenuClickEvent {
    private final Conversation conversation;

    public MoreMenuClickEvent(Conversation conversation) {
        this.conversation = conversation;
    }
}
