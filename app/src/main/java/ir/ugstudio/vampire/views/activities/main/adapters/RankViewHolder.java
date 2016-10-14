package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.ugstudio.vampire.R;

public class RankViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout container;
    public TextView username;
    public TextView rank;
    public TextView score;

    public RankViewHolder(View view) {
        super(view);

        container = (LinearLayout) view.findViewById(R.id.container);
        username = (TextView) view.findViewById(R.id.username);
        rank = (TextView) view.findViewById(R.id.rank);
        score = (TextView) view.findViewById(R.id.score);
    }
}
