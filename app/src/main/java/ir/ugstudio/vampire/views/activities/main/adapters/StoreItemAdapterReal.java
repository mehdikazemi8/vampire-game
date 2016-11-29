package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.models.StoreItemReal;

public class StoreItemAdapterReal extends RecyclerView.Adapter<StoreItemViewHolderReal> {

    private final OnRealStoreItemClickListener listener;
    private List<StoreItemReal> items;

    public StoreItemAdapterReal(List<StoreItemReal> items, OnRealStoreItemClickListener listener) {
        this.listener = listener;
        this.items = items;
    }

    @Override
    public StoreItemViewHolderReal onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.template_store_item, parent, false
        );
        return new StoreItemViewHolderReal(view);
    }

    @Override
    public void onBindViewHolder(StoreItemViewHolderReal holder, int position) {
        Log.d("TAG", "onBindViewHolder " + position);

        holder.bind(items.get(position), listener);

        if (items.get(position).getImageType() == 1)
            holder.icon.setBackgroundResource(R.drawable.v_avatar_01);
        else
            holder.icon.setBackgroundResource(R.drawable.v_avatar_02);

        holder.title.setText(items.get(position).getTitle());
        holder.price.setText(String.valueOf(items.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
