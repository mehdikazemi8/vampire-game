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
import ir.ugstudio.vampire.models.TowerMessage;

public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<TowerMessage> items;
    private Context context;

    public MessageViewAdapter(List<TowerMessage> items) {
        this.items = items;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.template_message, parent, false);
        context = parent.getContext();
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.username.setText(items.get(position).getUsername());
        holder.message.setText(String.valueOf(items.get(position).getMessage()));

        Picasso.with(context).load(AvatarManager.getResourceId(context, items.get(position).getAvatar()))
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
