package ir.ugstudio.vampire.views.activities.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.models.Tower;

public class TowerInfoAdapter extends RecyclerView.Adapter<TowerInfoViewHolder> {
    private List<Tower> items;
    private Context context;

    public TowerInfoAdapter(List<Tower> items) {
        this.items = items;
    }

    @Override
    public TowerInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.template_tower_info, parent, false);
        return new TowerInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TowerInfoViewHolder holder, int position) {
        holder.towerOwnersCount.setText(String.valueOf(items.get(position).getOwners().size()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
