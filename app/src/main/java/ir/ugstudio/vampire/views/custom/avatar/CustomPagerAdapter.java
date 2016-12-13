package ir.ugstudio.vampire.views.custom.avatar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import ir.ugstudio.vampire.R;

public class CustomPagerAdapter extends PagerAdapter {

    private final int EMPTY = 0;
    private final int VAMPIRE = 1;
    private final int HUNTER = 2;
    private LayoutInflater layoutInflater;
    private Context context;
    private int type = -1;

    private int[] huntersArray = {
            R.drawable.hunt2000,
            R.drawable.hunt2001,
            R.drawable.hunt2002,
            R.drawable.hunt2003,
            R.drawable.hunt2004,
            R.drawable.hunt2005,
            R.drawable.hunt2006,
            R.drawable.hunt2007,
    };

    private int[] vampiresArray = {
            R.drawable.vamp1000,
            R.drawable.vamp1001,
            R.drawable.vamp1002,
            R.drawable.vamp1003,
            R.drawable.vamp1004,
            R.drawable.vamp1005,
            R.drawable.vamp1006,
            R.drawable.vamp1007,
    };

    private int[] emptyArray = {
            R.drawable.icon,
            R.drawable.icon,
            R.drawable.icon,
            R.drawable.icon,
            R.drawable.icon,
            R.drawable.icon,
            R.drawable.icon,
            R.drawable.icon
    };

    public CustomPagerAdapter(Context context, int type) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        switch (this.type) {
            case VAMPIRE:
                return vampiresArray.length;
            case HUNTER:
                return huntersArray.length;
            case EMPTY:
                return emptyArray.length;
            default:
                return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    private int getResource(int position) {
        switch (this.type) {
            case VAMPIRE:
                return vampiresArray[position];
            case HUNTER:
                return huntersArray[position];
            case EMPTY:
                return emptyArray[position];
            default:
                return 0;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView avatar = (ImageView) itemView.findViewById(R.id.avatar);
        Picasso.with(context).load(getResource(position)).into(avatar);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}