package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class RankViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout container;
    public CustomTextView username;
    public CustomTextView rank;
    public CustomTextView score;
    public ImageView avatar;

    public RankViewHolder(View view) {
        super(view);

        container = (LinearLayout) view.findViewById(R.id.container);
        username = (CustomTextView) view.findViewById(R.id.username);
        rank = (CustomTextView) view.findViewById(R.id.rank);
        score = (CustomTextView) view.findViewById(R.id.score);
        avatar = (ImageView) view.findViewById(R.id.avatar);
    }
}
