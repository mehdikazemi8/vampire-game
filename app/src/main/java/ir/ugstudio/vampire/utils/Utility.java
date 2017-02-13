package ir.ugstudio.vampire.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import ir.ugstudio.vampire.views.custom.CustomTextView;
import okhttp3.ResponseBody;

public class Utility {
    public static void makeToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);

        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize}
        );
        int height = (int) styledAttributes.getDimension(0, 0) + 5;
        toast.setGravity(Gravity.BOTTOM, 0, height * 2);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_toast, null);
        toast.setView(view);
        CustomTextView textView = (CustomTextView) view.findViewById(R.id.message);
        textView.setText(message);

        toast.show();
    }

    public static String extractResult(ResponseBody response) {
        String result = "IOEXception";
        try {
            result = response.string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }
}
