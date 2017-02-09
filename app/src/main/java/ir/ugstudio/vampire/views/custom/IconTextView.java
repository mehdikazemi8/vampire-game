package ir.ugstudio.vampire.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import ir.ugstudio.vampire.utils.FontHelper;

public class IconTextView extends TextView {

    public IconTextView(Context context) {
        super(context);
        fixFont();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fixFont();
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        fixFont();
    }

    private void fixFont() {
        setTypeface(FontHelper.getIcons(getContext()));
    }
}
