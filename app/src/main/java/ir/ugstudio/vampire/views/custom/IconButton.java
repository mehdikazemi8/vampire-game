package ir.ugstudio.vampire.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import ir.ugstudio.vampire.utils.FontHelper;

public class IconButton extends Button {
    public IconButton(Context context) {
        super(context);
        fixFont();
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        fixFont();
    }

    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        fixFont();
    }

    private void fixFont() {
        setTypeface(FontHelper.getIcons(getContext()));
    }
}
