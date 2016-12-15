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
import ir.ugstudio.vampire.models.StoreItemVirtual;
import ir.ugstudio.vampire.utils.StoreIconSelector;

public class StoreItemAdapterVirtual extends RecyclerView.Adapter<StoreItemViewHolderVirtual> {
    private Context context;
    private List<StoreItemVirtual> items;
    private OnVirtualStoreItemClickListener listener;

    public StoreItemAdapterVirtual(List<StoreItemVirtual> items, OnVirtualStoreItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public StoreItemViewHolderVirtual onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.template_store_item, parent, false
        );
        context = parent.getContext();
        return new StoreItemViewHolderVirtual(view);
    }

    @Override
    public void onBindViewHolder(StoreItemViewHolderVirtual holder, int position) {
        Log.d("TAG", "onBindViewHolder virtual " + position + " " + items.get(position).getImageType());

        Picasso.with(context).load(StoreIconSelector.getVirtualStoreItem(context, items.get(position).getImageType(),
                CacheHandler.getUser().getRole())).into(holder.icon);

        holder.bind(items.get(position), listener);
        holder.title.setText(items.get(position).getTitle());

        holder.price.setText(String.format(context.getString(R.string.virtual_store_item_price), items.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
