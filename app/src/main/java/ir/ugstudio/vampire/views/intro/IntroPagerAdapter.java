package ir.ugstudio.vampire.views.intro;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import ir.ugstudio.vampire.R;

public class IntroPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private int[] introPictures = {
            R.drawable.hunt2000,
            R.drawable.hunt2001,
            R.drawable.hunt2002,
            R.drawable.hunt2003,
            R.drawable.hunt2004,
            R.drawable.hunt2005,
            R.drawable.hunt2006,
            R.drawable.hunt2007,
    };

    public IntroPagerAdapter(Context context) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return introPictures.length;
    }

    private int getResource(int position) {
        return introPictures[position];
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
