/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * Copyright (C) 2017-2018 Mario Danic <mario@lovelyhq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextcloud.talk.adapters.messages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.emoji.widget.EmojiTextView;

import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.flexbox.FlexboxLayout;
import com.nextcloud.talk.R;
import com.nextcloud.talk.application.NextcloudTalkApplication;
import com.nextcloud.talk.models.json.chat.ChatMessage;
import com.nextcloud.talk.utils.DisplayUtils;
import com.nextcloud.talk.utils.TextMatchers;
import com.nextcloud.talk.utils.database.user.UserUtils;
import com.stfalcon.chatkit.messages.MessageHolders;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@AutoInjector(NextcloudTalkApplication.class)
public class MagicOutcomingTextMessageViewHolder extends MessageHolders.OutcomingTextMessageViewHolder<ChatMessage> {
    @BindView(R.id.messageText)
    EmojiTextView messageText;

    @BindView(R.id.messageTime)
    TextView messageTimeView;

    @Inject
    UserUtils userUtils;

    @Inject
    Context context;

    private View itemView;

    public MagicOutcomingTextMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);

        this.itemView = itemView;
    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);

        HashMap<String, HashMap<String, String>> messageParameters = message.getMessageParameters();

        Spannable messageString = new SpannableString(message.getText());

        itemView.setSelected(false);
        messageTimeView.setTextColor(context.getResources().getColor(R.color.white60));

        FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) messageTimeView.getLayoutParams();
        layoutParams.setWrapBefore(false);

        float textSize = context.getResources().getDimension(R.dimen.chat_text_size);

        if (messageParameters != null && messageParameters.size() > 0) {
            for (String key : messageParameters.keySet()) {
                Map<String, String> individualHashMap = message.getMessageParameters().get(key);
                if (individualHashMap != null) {
                    if (individualHashMap.get("type").equals("user") || individualHashMap.get("type").equals("guest") || individualHashMap.get("type").equals("call")) {
                        messageString =
                                DisplayUtils.searchAndReplaceWithMentionSpan(messageText.getContext(),
                                        messageString,
                                        individualHashMap.get("id"),
                                        individualHashMap.get("name"),
                                        individualHashMap.get("type"),
                                        userUtils.getUserById(message.getActiveUser().getUserId()),
                                        R.xml.chip_others);
                    } else if (individualHashMap.get("type").equals("file")) {
                        itemView.setOnClickListener(v -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(individualHashMap.get("link")));
                            context.startActivity(browserIntent);
                        });

                    }
                }
            }

        } else if (TextMatchers.isMessageWithSingleEmoticonOnly(message.getText())) {
            textSize = (float) (textSize * 2.5);
            layoutParams.setWrapBefore(true);
            messageTimeView.setTextColor(context.getResources().getColor(R.color.warm_grey_four));
            itemView.setSelected(true);
        }

        Resources resources = NextcloudTalkApplication.Companion.getSharedApplication().getResources();
        if (message.isGrouped) {
            Drawable bubbleDrawable =
                    DisplayUtils.getMessageSelector(resources.getColor(R.color.bg_message_list_outcoming_bubble),
                    resources.getColor(R.color.transparent),
                    resources.getColor(R.color.bg_message_list_outcoming_bubble), R.drawable.shape_grouped_outcoming_message);
            ViewCompat.setBackground(bubble, bubbleDrawable);
        } else {
            Drawable bubbleDrawable = DisplayUtils.getMessageSelector(resources.getColor(R.color.bg_message_list_outcoming_bubble),
                    resources.getColor(R.color.transparent),
                    resources.getColor(R.color.bg_message_list_outcoming_bubble), R.drawable.shape_outcoming_message);
            ViewCompat.setBackground(bubble, bubbleDrawable);
        }

        messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageTimeView.setLayoutParams(layoutParams);
        messageText.setText(messageString);
    }
}
