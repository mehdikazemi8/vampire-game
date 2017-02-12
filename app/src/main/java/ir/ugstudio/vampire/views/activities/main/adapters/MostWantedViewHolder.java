package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class MostWantedViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.username)
    CustomTextView username;
    @BindView(R.id.coin)
    CustomTextView coin;

    public MostWantedViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
