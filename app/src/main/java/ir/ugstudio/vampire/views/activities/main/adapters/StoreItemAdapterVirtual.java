package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.models.StoreItemVirtual;

public class StoreItemAdapterVirtual extends RecyclerView.Adapter<StoreItemViewHolderVirtual> {
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
        return new StoreItemViewHolderVirtual(view);
    }

    @Override
    public void onBindViewHolder(StoreItemViewHolderVirtual holder, int position) {
        holder.bind(items.get(position), listener);
        holder.price.setText(String.valueOf(items.get(position).getPrice()));
        holder.title.setText(items.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
