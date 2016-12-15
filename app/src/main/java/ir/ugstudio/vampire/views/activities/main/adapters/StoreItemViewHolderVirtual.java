package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.models.StoreItemVirtual;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class StoreItemViewHolderVirtual extends RecyclerView.ViewHolder {

    public ImageView icon;
    public CustomTextView title;
    public CustomTextView price;

    public StoreItemViewHolderVirtual(View view) {
        super(view);

        icon = (ImageView) view.findViewById(R.id.item_icon);
        title = (CustomTextView) view.findViewById(R.id.item_title);
        price = (CustomTextView) view.findViewById(R.id.item_price);
    }

    public void bind(final StoreItemVirtual item, final OnVirtualStoreItemClickListener listener) {
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(item);
            }
        });
    }
}
