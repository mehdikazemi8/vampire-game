package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.models.TowerMessage;

public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<TowerMessage> items;

    public MessageViewAdapter(List<TowerMessage> items) {
        this.items = items;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.template_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.username.setText(items.get(position).getUsername());
        holder.message.setText(String.valueOf(items.get(position).getMessage()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
