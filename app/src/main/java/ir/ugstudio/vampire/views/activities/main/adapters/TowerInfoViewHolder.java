package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class TowerInfoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tower_owners_count)
    CustomTextView towerOwnersCount;

    public TowerInfoViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
