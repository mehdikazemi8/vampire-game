package ir.ugstudio.vampire.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ir.ugstudio.vampire.R;
import okhttp3.ResponseBody;

public class Utility {
    public static void makeToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        FontHelper.setKoodakFor(context, (TextView) toast.getView().findViewById(android.R.id.message));
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize}
        );
        int height = (int) styledAttributes.getDimension(0, 0) + 5;
        toast.setGravity(Gravity.TOP, 0, height * 2);
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
