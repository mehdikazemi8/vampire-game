package ir.ugstudio.vampire.views.custom.avatar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ir.ugstudio.vampire.R;

class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private int[] mResources = {
            R.drawable.hunt0,
            R.drawable.hunt1,
            R.drawable.hunt2,
            R.drawable.hunt3,
            R.drawable.hunt4,
            R.drawable.hunt5,
            R.drawable.hunt6,
            R.drawable.hunt7,
            R.drawable.vamp0,
            R.drawable.vamp1,
            R.drawable.vamp2,
            R.drawable.vamp3,
            R.drawable.vamp4,
            R.drawable.vamp5,
            R.drawable.vamp6,
            R.drawable.vamp7,
    };

    public CustomPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageResource(mResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}