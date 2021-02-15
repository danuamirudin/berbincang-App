

package com.skripsi.berbincang.adapters.messages;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.json.chat.ChatMessage;
import com.skripsi.berbincang.utils.DisplayUtils;
import com.skripsi.berbincang.utils.preferences.AppPreferences;
import com.stfalcon.chatkit.messages.MessageHolders;

import java.util.Map;
import javax.inject.Inject;
import autodagger.AutoInjector;

@AutoInjector(NextcloudTalkApplication.class)
public class MagicSystemMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<ChatMessage> {

    @Inject
    AppPreferences appPreferences;

    @Inject
    Context context;

    public MagicSystemMessageViewHolder(View itemView) {
        super(itemView);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);

        Resources resources = itemView.getResources();
        int normalColor = resources.getColor(R.color.bg_message_list_incoming_bubble);
        int pressedColor;
        int mentionColor;


        pressedColor = normalColor;
        mentionColor = resources.getColor(R.color.nc_author_text);

        Drawable bubbleDrawable = DisplayUtils.getMessageSelector(normalColor,
                                resources.getColor(R.color.transparent), pressedColor,
                                R.drawable.shape_grouped_incoming_message);
        ViewCompat.setBackground(bubble, bubbleDrawable);

        Spannable messageString = new SpannableString(message.getText());

        if (message.getMessageParameters() != null && message.getMessageParameters().size() > 0) {
            for (String key : message.getMessageParameters().keySet()) {
                Map<String, String> individualHashMap = message.getMessageParameters().get(key);
                if (individualHashMap != null && (individualHashMap.get("type").equals("user") || individualHashMap.get("type").equals("guest") || individualHashMap.get("type").equals("call"))) {
                    messageString = DisplayUtils.searchAndColor(messageString, "@" + individualHashMap.get("name"), mentionColor);
                }
            }
        }

        text.setText(messageString);
    }
}
