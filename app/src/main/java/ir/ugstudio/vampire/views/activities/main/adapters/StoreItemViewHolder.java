package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ugstudio.vampire.R;

public class StoreItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView title;
    public TextView price;

    public StoreItemViewHolder(View view) {
        super(view);

        icon = (ImageView) view.findViewById(R.id.item_icon);
        title = (TextView) view.findViewById(R.id.item_title);
        price = (TextView) view.findViewById(R.id.item_price);
    }
}
