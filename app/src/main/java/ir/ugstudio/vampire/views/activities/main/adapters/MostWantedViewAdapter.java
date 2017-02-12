package ir.ugstudio.vampire.views.activities.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.managers.AvatarManager;
import ir.ugstudio.vampire.models.nearest.Target;

public class MostWantedViewAdapter extends RecyclerView.Adapter<MostWantedViewHolder> {
    private List<Target> items;
    private Context context;

    public MostWantedViewAdapter(List<Target> items) {
        this.items = items;
    }

    @Override
    public MostWantedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.template_most_wanted, parent, false);
        return new MostWantedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MostWantedViewHolder holder, int position) {
        holder.username.setText(items.get(position).getTarget().getUsername());
        Picasso.with(context).load(AvatarManager.getResourceId(context, items.get(position).getTarget().getAvatar()))
                .into(holder.avatar);
        holder.coin.setText(String.valueOf(items.get(position).getTarget().getCoin()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
