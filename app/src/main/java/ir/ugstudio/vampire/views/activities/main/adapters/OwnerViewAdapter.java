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

public class OwnerViewAdapter extends RecyclerView.Adapter<OwnerViewHolder> {
    private List<User> items;
    private Context context;

    public OwnerViewAdapter(List<User> items) {
        this.items = items;
    }

    @Override
    public OwnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_owner, parent, false);
        context = parent.getContext();
        return new OwnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OwnerViewHolder holder, int position) {
        Picasso.with(context).load(AvatarManager.getResourceId(context, items.get(position).getAvatar()))
                .into(holder.avatar);
        holder.username.setText(items.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
