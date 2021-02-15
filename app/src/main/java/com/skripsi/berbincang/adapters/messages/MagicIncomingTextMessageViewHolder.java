

package com.skripsi.berbincang.adapters.messages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.emoji.widget.EmojiTextView;

import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.json.chat.ChatMessage;
import com.skripsi.berbincang.utils.DisplayUtils;
import com.skripsi.berbincang.utils.TextMatchers;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import com.skripsi.berbincang.utils.preferences.AppPreferences;
import com.stfalcon.chatkit.messages.MessageHolders;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@AutoInjector(NextcloudTalkApplication.class)
public class MagicIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<ChatMessage> {

    @BindView(R.id.messageAuthor)
    EmojiTextView messageAuthor;

    @BindView(R.id.messageText)
    EmojiTextView messageText;

    @BindView(R.id.messageUserAvatar)
    SimpleDraweeView messageUserAvatarView;

    @BindView(R.id.messageTime)
    TextView messageTimeView;

    @Inject
    UserUtils userUtils;

    @Inject
    Context context;

    @Inject
    AppPreferences appPreferences;

    private View itemView;

    public MagicIncomingTextMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);

        this.itemView = itemView;
    }


    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);
        String author;

        if (!TextUtils.isEmpty(author = message.getActorDisplayName())) {
            messageAuthor.setText(author);
        } else {
            messageAuthor.setText(R.string.nc_nick_guest);
        }

        if (!message.isGrouped() && !message.isOneToOneConversation()) {
            messageUserAvatarView.setVisibility(View.VISIBLE);
            if (message.getActorType().equals("guests")) {
                // do nothing, avatar is set
            } else if (message.getActorType().equals("bots") && message.getActorId().equals("changelog")) {
                messageUserAvatarView.setController(null);
                Drawable[] layers = new Drawable[2];
                layers[0] = context.getDrawable(R.drawable.ic_launcher_background);
                layers[1] = context.getDrawable(R.drawable.ic_launcher_foreground);
                LayerDrawable layerDrawable = new LayerDrawable(layers);

                messageUserAvatarView.getHierarchy().setPlaceholderImage(DisplayUtils.getRoundedDrawable(layerDrawable));
            } else if (message.getActorType().equals("bots")) {
                messageUserAvatarView.setController(null);
                TextDrawable drawable = TextDrawable.builder().beginConfig().bold().endConfig().buildRound(">",
                                context.getResources().getColor(R.color.black));
                messageUserAvatarView.setVisibility(View.VISIBLE);
                messageUserAvatarView.getHierarchy().setPlaceholderImage(drawable);
            }
        } else {
            if (message.isOneToOneConversation()) {
                messageUserAvatarView.setVisibility(View.GONE);
            } else {
                messageUserAvatarView.setVisibility(View.INVISIBLE);
            }
            messageAuthor.setVisibility(View.GONE);
        }

        Resources resources = itemView.getResources();

        int bg_bubble_color = resources.getColor(R.color.bg_message_list_incoming_bubble);

        int bubbleResource = R.drawable.shape_incoming_message;

        if (message.isGrouped) {
            bubbleResource = R.drawable.shape_grouped_incoming_message;
        }

        Drawable bubbleDrawable = DisplayUtils.getMessageSelector(bg_bubble_color,
                resources.getColor(R.color.transparent),
                bg_bubble_color, bubbleResource);
        ViewCompat.setBackground(bubble, bubbleDrawable);

        HashMap<String, HashMap<String, String>> messageParameters = message.getMessageParameters();

        itemView.setSelected(false);
        messageTimeView.setTextColor(context.getResources().getColor(R.color.warm_grey_four));

        FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) messageTimeView.getLayoutParams();
        layoutParams.setWrapBefore(false);

        Spannable messageString = new SpannableString(message.getText());

        float textSize = context.getResources().getDimension(R.dimen.chat_text_size);

        if (messageParameters != null && messageParameters.size() > 0) {
            for (String key : messageParameters.keySet()) {
                Map<String, String> individualHashMap = message.getMessageParameters().get(key);
                if (individualHashMap != null) {
                    if (individualHashMap.get("type").equals("user") || individualHashMap.get("type").equals("guest") || individualHashMap.get("type").equals("call")) {
                        if (individualHashMap.get("id").equals(message.getActiveUser().getUserId())) {
                            messageString =
                                    DisplayUtils.searchAndReplaceWithMentionSpan(messageText.getContext(),
                                            messageString,
                                            individualHashMap.get("id"),
                                            individualHashMap.get("name"),
                                            individualHashMap.get("type"),
                                            userUtils.getUserById(message.getActiveUser().getUserId()),
                                            R.xml.chip_you);
                        } else {
                            messageString =
                                    DisplayUtils.searchAndReplaceWithMentionSpan(messageText.getContext(),
                                            messageString,
                                            individualHashMap.get("id"),
                                            individualHashMap.get("name"),
                                            individualHashMap.get("type"),
                                            userUtils.getUserById(message.getActiveUser().getUserId()),
                                            R.xml.chip_others);
                        }

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
            itemView.setSelected(true);
            messageAuthor.setVisibility(View.GONE);
        }

        messageText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageTimeView.setLayoutParams(layoutParams);
        messageText.setText(messageString);
    }
}