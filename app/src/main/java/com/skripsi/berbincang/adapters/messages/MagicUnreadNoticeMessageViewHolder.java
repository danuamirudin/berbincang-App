
package com.skripsi.berbincang.adapters.messages;

import android.view.View;

import com.skripsi.berbincang.models.json.chat.ChatMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

public class MagicUnreadNoticeMessageViewHolder extends MessageHolders.SystemMessageViewHolder<ChatMessage> {

    public MagicUnreadNoticeMessageViewHolder(View itemView) {
        super(itemView);
    }

    public MagicUnreadNoticeMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void viewDetached() {
        messagesListAdapter.deleteById("-1");
    }

    @Override
    public void viewAttached() {
    }

    @Override
    public void viewRecycled() {

    }
}
