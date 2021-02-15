

package com.skripsi.berbincang.adapters.items;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

public class AppItem extends AbstractFlexibleItem<AppItem.AppItemViewHolder> {
    private String title;
    private String packageName;
    private String name;
    @Nullable
    private Drawable drawable;

    public AppItem(String title, String packageName, String name, @Nullable Drawable drawable) {
        this.title = title;
        this.packageName = packageName;
        this.name = name;
        this.drawable = drawable;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppItem) {
            AppItem inItem = (AppItem) o;
            return title.equals(inItem.getTitle()) && packageName.equals(inItem.getPackageName()) && name.equals(inItem
                    .getName());
        }

        return false;
    }

    public String getTitle() {
        return title;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_item_app;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, AppItemViewHolder holder, int position, List<Object> payloads) {
        if (drawable != null) {
            holder.iconImageView.setVisibility(View.VISIBLE);
            holder.iconImageView.setImageDrawable(drawable);
        } else {
            holder.iconImageView.setVisibility(View.GONE);
        }

        if (position == 0) {
            Spannable spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(NextcloudTalkApplication.Companion.getSharedApplication()
                            .getResources().getColor(R.color.grey_600)), 0,
                    spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.appTitleTextView.setText(spannableString);
        } else {
            holder.appTitleTextView.setText(title);
        }
    }

    @Override
    public AppItem.AppItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new AppItemViewHolder(view, adapter);
    }

    static class AppItemViewHolder extends FlexibleViewHolder {
        @BindView(R.id.icon_image_view)
        public ImageView iconImageView;
        @BindView(R.id.app_title_text_view)
        public TextView appTitleTextView;

        /**
         * Default constructor.
         */
        AppItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }

}
