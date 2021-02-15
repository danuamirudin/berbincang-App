
package com.skripsi.berbincang.adapters.items;

import android.annotation.SuppressLint;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.DisplayUtils;

import java.util.List;
import java.util.regex.Pattern;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.flexibleadapter.utils.FlexibleUtils;

public class MentionAutocompleteItem extends AbstractFlexibleItem<UserItem.UserItemViewHolder>
        implements IFilterable<String> {

    private String objectId;
    private String displayName;
    private String source;
    private UserEntity currentUser;

    public MentionAutocompleteItem(String objectId, String displayName, String source, UserEntity currentUser) {
        this.objectId = objectId;
        this.displayName = displayName;
        this.source = source;
        this.currentUser = currentUser;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MentionAutocompleteItem) {
            MentionAutocompleteItem inItem = (MentionAutocompleteItem) o;
            return (objectId.equals(inItem.objectId) && displayName.equals(inItem.displayName));
        }

        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_item_mention;
    }

    @Override
    public UserItem.UserItemViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new UserItem.UserItemViewHolder(view, adapter);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, UserItem.UserItemViewHolder holder, int position, List<Object> payloads) {

        if (adapter.hasFilter()) {
            FlexibleUtils.highlightText(holder.contactDisplayName, displayName,
                    String.valueOf(adapter.getFilter(String.class)), NextcloudTalkApplication.Companion.getSharedApplication()
                            .getResources().getColor(R.color.colorPrimary));
            if (holder.contactMentionId != null) {
                FlexibleUtils.highlightText(holder.contactMentionId, "@" + objectId,
                        String.valueOf(adapter.getFilter(String.class)), NextcloudTalkApplication.Companion.getSharedApplication()
                                .getResources().getColor(R.color.colorPrimary));
            }
        } else {
            holder.contactDisplayName.setText(displayName);
            if (holder.contactMentionId != null) {
                holder.contactMentionId.setText("@" + objectId);
            }
        }

        if (source.equals("calls")) {
            holder.simpleDraweeView.getHierarchy().setPlaceholderImage(DisplayUtils.getRoundedBitmapDrawableFromVectorDrawableResource(NextcloudTalkApplication.Companion.getSharedApplication().getResources(), R.drawable.ic_people_group_white_24px));
        } else {
            String avatarId = objectId;
            String avatarUrl = ApiUtils.getUrlForAvatarWithName(currentUser.getBaseUrl(),
                    avatarId, R.dimen.avatar_size_big);

            if (source.equals("guests")) {
                avatarId = displayName;
                avatarUrl = ApiUtils.getUrlForAvatarWithNameForGuests(currentUser.getBaseUrl(), avatarId, R.dimen.avatar_size_big);
            }

            holder.simpleDraweeView.setController(null);
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.simpleDraweeView.getController())
                    .setAutoPlayAnimations(true)
                    .setImageRequest(DisplayUtils.getImageRequestForUrl(avatarUrl, null))
                    .build();
            holder.simpleDraweeView.setController(draweeController);
        }
    }

    @Override
    public boolean filter(String constraint) {
        return objectId != null && Pattern.compile(constraint,
                Pattern.CASE_INSENSITIVE | Pattern.LITERAL).matcher(objectId).find()
                || displayName != null && Pattern.compile(constraint, Pattern.CASE_INSENSITIVE | Pattern.LITERAL).matcher(displayName).find();
    }
}
