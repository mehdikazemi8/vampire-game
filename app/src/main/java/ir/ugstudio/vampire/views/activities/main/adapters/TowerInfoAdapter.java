package ir.ugstudio.vampire.views.activities.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.events.OpenTowerWallFragment;
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
    public void onBindViewHolder(TowerInfoViewHolder holder, final int position) {
        holder.towerOwnersCount.setText(String.valueOf(items.get(position).getOwners().size()));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "rootView");
                EventBus.getDefault().post(new OpenTowerWallFragment(items.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
