package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public CustomTextView message;
    public TextView username;
    public ImageView avatar;

    public MessageViewHolder(View view) {
        super(view);

        username = (TextView) view.findViewById(R.id.username);
        message = (CustomTextView) view.findViewById(R.id.message);
        avatar = (ImageView) view.findViewById(R.id.avatar);
    }
}
