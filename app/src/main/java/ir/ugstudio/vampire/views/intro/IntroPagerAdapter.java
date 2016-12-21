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
import ir.ugstudio.vampire.views.custom.CustomTextView;

public class IntroPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private int[] introPictures = {
            R.drawable.intro01,
            R.drawable.intro02,
            R.drawable.intro03,
            R.drawable.intro04,
            R.drawable.intro05,
            R.drawable.intro06,
            R.drawable.intro07,
            R.drawable.intro08
    };

    private int[] introText = {
            R.string.intro_text_1,
            R.string.intro_text_2,
            R.string.intro_text_3,
            R.string.intro_text_4,
            R.string.intro_text_5,
            R.string.intro_text_6,
            R.string.intro_text_7,
            R.string.intro_text_8
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
        View itemView = layoutInflater.inflate(R.layout.intro_pager_item, container, false);
        ImageView avatar = (ImageView) itemView.findViewById(R.id.intro_image);
        Picasso.with(context).load(getResource(position)).into(avatar);

        CustomTextView text = (CustomTextView) itemView.findViewById(R.id.text);
        text.setText(introText[position]);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
