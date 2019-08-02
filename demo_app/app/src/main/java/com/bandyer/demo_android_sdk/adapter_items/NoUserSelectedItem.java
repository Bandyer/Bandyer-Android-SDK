package com.bandyer.demo_android_sdk.adapter_items;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bandyer.demo_android_sdk.R;
import com.bandyer.demo_android_sdk.utils.Utils;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;

import butterknife.BindView;

/**
 * A simple RecyclerView item used to display no selections on users' list.
 */
public class NoUserSelectedItem extends AbstractItem<NoUserSelectedItem, NoUserSelectedItem.ViewHolder> {

    public static long NO_USER_SELECTED_ITEM_IDENTIFIER = Long.MAX_VALUE;

    public NoUserSelectedItem() {
        super();
        withIdentifier(NO_USER_SELECTED_ITEM_IDENTIFIER);
    }

    @Override
    public int getType() {
        return R.id.no_user_selecte_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.no_users_selected_item;
    }

    @NonNull
    @Override
    public NoUserSelectedItem.ViewHolder getViewHolder(@NonNull View v) {
        return new NoUserSelectedItem.ViewHolder(v);
    }

    protected static class ViewHolder extends ButterKnifeViewHolder<NoUserSelectedItem> {

        @BindView(R.id.no_user_selecte_item)
        TextView noUserselectedItem;

        ViewHolder(View view) {
            super(view);
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
            view.setClickable(false);
        }

        @Override
        public void bindView(@NonNull NoUserSelectedItem item, @NonNull List<Object> payloads) {
            noUserselectedItem.setText(noUserselectedItem.getContext().getString(R.string.no_users_selected));
        }

        @Override
        public void unbindView(@NonNull NoUserSelectedItem item) { }
    }
}
