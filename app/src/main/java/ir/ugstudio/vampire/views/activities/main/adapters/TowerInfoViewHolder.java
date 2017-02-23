package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class TowerInfoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tower_owners_count)
    CustomTextView towerOwnersCount;
    @BindView(R.id.tower_info_owner_type)
    ImageView ownerType;
    @BindView(R.id.tower_info_coin)
    CustomTextView coin;
    @BindView(R.id.tower_info_unread_message_count)
    CustomTextView unreadMessageCount;

    @BindView(R.id.template_tower_info_root_view)
    LinearLayout rootView;

    public TowerInfoViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
