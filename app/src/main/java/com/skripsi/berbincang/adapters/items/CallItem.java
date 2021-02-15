

package com.skripsi.berbincang.adapters.items;

import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.emoji.widget.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.events.MoreMenuClickEvent;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.conversations.Conversation;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.DisplayUtils;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.utils.FlexibleUtils;
import eu.davidea.viewholders.FlexibleViewHolder;
import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.regex.Pattern;

public class CallItem extends AbstractFlexibleItem<CallItem.RoomItemViewHolder> implements IFilterable<String> {

    private Conversation conversation;
    private UserEntity userEntity;

    public CallItem(Conversation conversation, UserEntity userEntity) {
        this.conversation = conversation;
        this.userEntity = userEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CallItem) {
            CallItem inItem = (CallItem) o;
            return conversation.equals(inItem.getModel());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return conversation.hashCode();
    }

    /**
     * @return the model object
     */

    public Conversation getModel() {
        return conversation;
    }

    /**
     * Filter is applied to the model fields.
     */

    @Override
    public int getLayoutRes() {
        return R.layout.rv_item_conversation;
    }

    @Override
    public RoomItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new RoomItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(final FlexibleAdapter adapter, RoomItemViewHolder holder, int position, List payloads) {
        if (adapter.hasFilter()) {
            FlexibleUtils.highlightText(holder.roomDisplayName, conversation.getDisplayName(),
                    String.valueOf(adapter.getFilter(String.class)), NextcloudTalkApplication.Companion.getSharedApplication()
                            .getResources().getColor(R.color.colorPrimary));
        } else {
            holder.roomDisplayName.setText(conversation.getDisplayName());
        }

        if (conversation.getLastPing() == 0) {
            holder.roomLastPing.setText(R.string.nc_never);
        } else {
            holder.roomLastPing.setText(DateUtils.getRelativeTimeSpanString(conversation.getLastPing() * 1000L,
                    System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));
        }

        if (conversation.hasPassword) {
            holder.passwordProtectedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.passwordProtectedImageView.setVisibility(View.GONE);
        }

        Resources resources = NextcloudTalkApplication.Companion.getSharedApplication().getResources();
        switch (conversation.getType()) {
            case ROOM_TYPE_ONE_TO_ONE_CALL:
                holder.avatarImageView.setVisibility(View.VISIBLE);

                holder.moreMenuButton.setContentDescription(String.format(resources.getString(R.string
                        .nc_description_more_menu_one_to_one), conversation.getDisplayName()));

                if (!TextUtils.isEmpty(conversation.getName())) {
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                            .setOldController(holder.avatarImageView.getController())
                            .setAutoPlayAnimations(true)
                            .setImageRequest(DisplayUtils.getImageRequestForUrl(ApiUtils.getUrlForAvatarWithName(userEntity.getBaseUrl(),
                                    conversation.getName(),
                                    R.dimen.avatar_size), null))
                            .build();
                    holder.avatarImageView.setController(draweeController);
                } else {
                    holder.avatarImageView.setVisibility(View.GONE);
                }
                break;
            case ROOM_GROUP_CALL:
                holder.moreMenuButton.setContentDescription(String.format(resources.getString(R.string
                        .nc_description_more_menu_group), conversation.getDisplayName()));
                holder.avatarImageView.setActualImageResource(R.drawable.ic_people_group_white_24px);
                holder.avatarImageView.setVisibility(View.VISIBLE);
                break;
            case ROOM_PUBLIC_CALL:
                holder.moreMenuButton.setContentDescription(String.format(resources.getString(R.string
                        .nc_description_more_menu_public), conversation.getDisplayName()));
                holder.avatarImageView.setActualImageResource(R.drawable.ic_link_white_24px);
                holder.avatarImageView.setVisibility(View.VISIBLE);
                break;
            default:
                holder.avatarImageView.setVisibility(View.GONE);

        }

        holder.moreMenuButton.setOnClickListener(view -> EventBus.getDefault().post(new MoreMenuClickEvent(conversation)));
    }

    @Override
    public boolean filter(String constraint) {
        return conversation.getDisplayName() != null &&
                Pattern.compile(constraint, Pattern.CASE_INSENSITIVE | Pattern.LITERAL).matcher(conversation.getDisplayName().trim()).find();
    }

    static class RoomItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.name_text)
        public EmojiTextView roomDisplayName;
        @BindView(R.id.secondary_text)
        public EmojiTextView roomLastPing;
        @BindView(R.id.avatar_image)
        public SimpleDraweeView avatarImageView;
        @BindView(R.id.more_menu)
        public ImageButton moreMenuButton;
        @BindView(R.id.password_protected_image_view)
        ImageView passwordProtectedImageView;

        RoomItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
