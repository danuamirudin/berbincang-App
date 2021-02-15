

package com.skripsi.berbincang.adapters.items;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.skripsi.berbincang.R;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;

public class GenericTextHeaderItem extends AbstractHeaderItem<GenericTextHeaderItem.HeaderViewHolder> {
    private static final String TAG = "GenericTextHeaderItem";

    private String title;

    public GenericTextHeaderItem(String title) {
        super();
        setHidden(false);
        setSelectable(false);
        this.title = title;
    }

    public String getModel() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GenericTextHeaderItem) {
            GenericTextHeaderItem inItem = (GenericTextHeaderItem) o;
            return title.equals(inItem.getModel());
        }
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rv_item_title_header;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, HeaderViewHolder holder, int position, List<Object> payloads) {
        if (payloads.size() > 0) {
            Log.d(TAG, "We have payloads, so ignoring!");
        } else {
            holder.titleTextView.setText(title);
        }

    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    static class HeaderViewHolder extends FlexibleViewHolder {

        @BindView(R.id.title_text_view)
        public TextView titleTextView;

        /**
         * Default constructor.
         */
        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            ButterKnife.bind(this, view);
        }
    }

}
