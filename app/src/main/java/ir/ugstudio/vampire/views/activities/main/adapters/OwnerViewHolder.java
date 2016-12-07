package ir.ugstudio.vampire.views.activities.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class OwnerViewHolder extends RecyclerView.ViewHolder {
    public ImageView avatar;
    public CustomTextView username;

    public OwnerViewHolder(View view) {
        super(view);

        avatar = (ImageView) view.findViewById(R.id.avatar);
        username = (CustomTextView) view.findViewById(R.id.username);
    }
}
