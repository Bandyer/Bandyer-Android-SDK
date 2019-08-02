package com.bandyer.demo_android_sdk.adapter_items;

import android.view.View;

import androidx.annotation.NonNull;

import com.bandyer.demo_android_sdk.R;
import com.google.android.material.chip.Chip;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;

/**
 * A simple RecyclerView item used to display the user userAlias as a Chip in the list.
 */
public class SelectedUserItem extends AbstractItem<SelectedUserItem, SelectedUserItem.ViewHolder> {

    public final String userAlias;
    public final int position;

    public SelectedUserItem(String userAlias, int position) {
        this.userAlias = userAlias;
        this.position = position;
    }

    @Override
    public int getType() {
        return R.id.user_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.selected_user_item;
    }

    @NonNull
    @Override
    public SelectedUserItem.ViewHolder getViewHolder(@NonNull View v) {
        return new SelectedUserItem.ViewHolder(v);
    }

    protected static class ViewHolder extends ButterKnifeViewHolder<SelectedUserItem> {

        @BindView(R.id.userAlias)
        Chip userAlias;

        ViewHolder(View view) {
            super(view);
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
        }

        @Override
        public void bindView(@NonNull SelectedUserItem item, @NonNull List<Object> payloads) {
            userAlias.setText(item.userAlias);
            userAlias.setOnCloseIconClickListener(v -> userAlias.performClick());
        }

        @Override
        public void unbindView(@NonNull SelectedUserItem item) {
            userAlias.setText(null);
        }
    }
}
