
package com.skripsi.berbincang.adapters.items;


import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.utils.DisplayUtils;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

public class MenuItem extends AbstractFlexibleItem<MenuItem.MenuItemViewHolder> {
    private String title;
    private Drawable icon;
    private int tag;
    private int padding;

    public MenuItem(String title, int tag, Drawable icon) {
        this.title = title;
        this.tag = tag;
        this.icon = icon;
        padding = (int) DisplayUtils.convertDpToPixel(16,
                NextcloudTalkApplication.Companion.getSharedApplication().getApplicationContext());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MenuItem) {
            MenuItem inItem = (MenuItem) o;
            return tag == inItem.tag;
        }
        return false;
    }

    public int getTag() {
        return tag;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_item_menu;
    }

    @Override
    public MenuItem.MenuItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new MenuItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MenuItem.MenuItemViewHolder holder, int position, List payloads) {
        if (position == 0) {
            Spannable spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(NextcloudTalkApplication.Companion.getSharedApplication()
                            .getResources().getColor(R.color.grey_600)), 0,
                    spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.menuTitle.setText(spannableString);
        } else {
            holder.menuTitle.setText(title);
            holder.menuTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            holder.menuTitle.setCompoundDrawablePadding(padding);
        }
    }

    static class MenuItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.menu_text)
        public TextView menuTitle;

        /**
         * Default constructor.
         */
        MenuItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
