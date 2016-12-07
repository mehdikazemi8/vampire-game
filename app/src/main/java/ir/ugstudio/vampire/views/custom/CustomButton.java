package ir.ugstudio.vampire.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import ir.ugstudio.vampire.utils.FontHelper;

public class CustomButton extends Button {
    public CustomButton(Context context) {
        super(context);
        fixFont();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        fixFont();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        fixFont();
    }

    private void fixFont() {
        setTypeface(FontHelper.getDefaultTypeface(getContext()));
    }
}
