package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public CustomTextView created;
    public CustomTextView message;
    public CustomTextView username;
    public CustomTextView lostCoin;
    public ImageView avatar;

    public NotificationViewHolder(View view) {
        super(view);

        created = (CustomTextView) view.findViewById(R.id.created);
        message = (CustomTextView) view.findViewById(R.id.message);
        username = (CustomTextView) view.findViewById(R.id.username);
        lostCoin = (CustomTextView) view.findViewById(R.id.lost_coin);
        avatar = (ImageView) view.findViewById(R.id.avatar);
    }
}
