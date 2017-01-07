package ir.ugstudio.vampire.views.custom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

import ir.ugstudio.vampire.R;
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
        setTextColor(ContextCompat.getColor(getContext(), R.color.zereshki));
        setPadding(20, 0, 20, 0);
    }
}
