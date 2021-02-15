

package com.skripsi.berbincang.adapters.items;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

public class NotificationSoundItem extends AbstractFlexibleItem<NotificationSoundItem.NotificationSoundItemViewHolder> {

    private String notificationSoundName;
    private String notificationSoundUri;

    public NotificationSoundItem(String notificationSoundName, String notificationSoundUri) {
        this.notificationSoundName = notificationSoundName;
        this.notificationSoundUri = notificationSoundUri;
    }

    public String getNotificationSoundUri() {
        return notificationSoundUri;
    }

    public String getNotificationSoundName() {
        return notificationSoundName;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_item_notification_sound;
    }

    @Override
    public NotificationSoundItemViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new NotificationSoundItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, NotificationSoundItemViewHolder holder, int position, List<Object> payloads) {
        holder.notificationName.setText(notificationSoundName);

        if (adapter.isSelected(position)) {
            holder.checkedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.checkedImageView.setVisibility(View.GONE);
        }

        Resources resources = NextcloudTalkApplication.Companion.getSharedApplication().getResources();
        holder.simpleDraweeView.getHierarchy().setBackgroundImage(new ColorDrawable(resources.getColor(R.color.colorPrimary)));
        if (position == 0) {
            holder.simpleDraweeView.getHierarchy().setImage(resources.getDrawable(R.drawable.ic_stop_white_24dp), 100,
                    true);
        } else {
            holder.simpleDraweeView.getHierarchy().setImage(resources.getDrawable(R.drawable.ic_play_circle_outline_white_24dp), 100,
                    true);
        }
    }

    static class NotificationSoundItemViewHolder extends FlexibleViewHolder {
        @BindView(R.id.notificationNameTextView)
        public TextView notificationName;
        @BindView(R.id.simpleDraweeView)
        SimpleDraweeView simpleDraweeView;
        @BindView(R.id.checkedImageView)
        ImageView checkedImageView;

        /**
         * Default constructor.
         */
        NotificationSoundItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }


}
