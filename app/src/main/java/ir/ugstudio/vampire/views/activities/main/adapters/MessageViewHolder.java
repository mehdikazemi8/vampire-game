package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ir.ugstudio.vampire.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView message;
    public TextView username;

    public MessageViewHolder(View view) {
        super(view);

        username = (TextView) view.findViewById(R.id.username);
        message = (TextView) view.findViewById(R.id.message);
    }
}
