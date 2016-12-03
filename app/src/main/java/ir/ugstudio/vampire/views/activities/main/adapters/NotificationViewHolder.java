package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ugstudio.vampire.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public TextView created;
    public TextView message;
    public TextView username;
    public TextView lostCoin;
    public ImageView avatar;

    public NotificationViewHolder(View view) {
        super(view);

        created = (TextView) view.findViewById(R.id.created);
        message = (TextView) view.findViewById(R.id.message);
        username = (TextView) view.findViewById(R.id.username);
        lostCoin = (TextView) view.findViewById(R.id.lost_coin);
        avatar = (ImageView) view.findViewById(R.id.avatar);
    }
}
