package ir.ugstudio.vampire.views.activities.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.CacheHandler;
import ir.ugstudio.vampire.models.StoreItemReal;
import ir.ugstudio.vampire.utils.StoreIconSelector;

public class StoreItemAdapterReal extends RecyclerView.Adapter<StoreItemViewHolderReal> {

    private final OnRealStoreItemClickListener listener;
    private List<StoreItemReal> items;
    private Context context;

    public StoreItemAdapterReal(List<StoreItemReal> items, OnRealStoreItemClickListener listener) {
        this.listener = listener;
        this.items = items;
    }

    @Override
    public StoreItemViewHolderReal onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.template_store_item, parent, false
        );
        context = parent.getContext();
        return new StoreItemViewHolderReal(view);
    }

    @Override
    public void onBindViewHolder(StoreItemViewHolderReal holder, int position) {
        Log.d("TAG", "onBindViewHolder " + position + " " + items.get(position).getImageType());

        holder.bind(items.get(position), listener);

        Picasso.with(context).load(StoreIconSelector.getRealStoreItem(context, items.get(position).getImageType(),
                CacheHandler.getUser().getRole())).into(holder.icon);
        holder.title.setText(items.get(position).getTitle());

        holder.price.setText(String.format(context.getString(R.string.real_store_item_price), items.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
