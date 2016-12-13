package ir.ugstudio.vampire.views.activities.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.models.Notification;

public class NotificationViewAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    private List<Notification> items;
    private Context context;

    public NotificationViewAdapter(List<Notification> items) {
        this.items = items;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_notification, parent, false);
        context = parent.getContext();
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        holder.message.setText(items.get(position).getMessage());
        holder.created.setText(items.get(position).getCreated());
        String forLostCoin = String.format(
                context.getString(R.string.template_notification_message),
                items.get(position).getLostCoin()
        );
        holder.lostCoin.setText(forLostCoin);
        holder.username.setText(items.get(position).getKiller().getUsername());
        Picasso.with(context).load(R.drawable.hunt2000).into(holder.avatar);
        }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<Notification> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
