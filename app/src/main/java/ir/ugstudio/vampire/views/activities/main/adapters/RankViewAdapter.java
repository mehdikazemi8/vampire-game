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
import ir.ugstudio.vampire.models.User;

public class RankViewAdapter extends RecyclerView.Adapter<RankViewHolder> {

    private Context context;
    private List<User> items;

    public RankViewAdapter(Context context, List<User> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.template_rank, parent, false);
        context = parent.getContext();
        return new RankViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RankViewHolder holder, int position) {
        holder.username.setText(items.get(position).getUsername());
        holder.rank.setText(String.valueOf(items.get(position).getRank()));
        holder.score.setText(String.valueOf(items.get(position).getScore()));

        Picasso.with(context).load(
                AvatarManager.getResourceIdMono(context, items.get(position).getAvatar())
        ).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<User> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}


