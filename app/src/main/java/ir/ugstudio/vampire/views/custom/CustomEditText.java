package ir.ugstudio.vampire.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import ir.ugstudio.vampire.utils.FontHelper;

public class CustomEditText extends EditText {
    public CustomEditText(Context context) {
        super(context);
        fixFont();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        fixFont();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        fixFont();
    }

    private void fixFont() {
        setTypeface(FontHelper.getDefaultTypeface(getContext()));
    }
}
