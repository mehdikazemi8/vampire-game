package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ir.ugstudio.vampire.R;

public class AvatarManager {
    static int[] avatars = null;

    public static Bitmap getBitmap(Context context, int position) {
        return BitmapFactory.decodeResource(context.getResources(), getResourceId(context, position));
    }

    private static void load(Context context) {
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.avatars);
        int length = imgs.length();
        avatars = new int[length];
        for (int i = 0; i < length; i++)
            avatars[i] = imgs.getResourceId(i, 0);
        imgs.recycle();
    }

    public static int getResourceId(Context context, int position) {
        if (avatars == null)
            load(context);

        if (avatars.length >= position && position > 0)
            return avatars[position - 1];
        else
            return avatars[avatars.length - 1];
    }

    public static int getCount(Context context) {
        if (avatars == null)
            load(context);
        return avatars.length;
    }
}
