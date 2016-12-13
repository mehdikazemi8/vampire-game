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
        // todo, handle vampires
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.hunters);
        int length = imgs.length();
        avatars = new int[length];
        for (int i = 0; i < length; i++)
            avatars[i] = imgs.getResourceId(i, 0);
        imgs.recycle();
    }

    public static int getResourceId(Context context, int position) {
        if (avatars == null)
            load(context);

        // // TODO: 12/5/16
        if (0 == 0)
            return avatars[0];

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

    public static int getAvatarIndex(String playerType, int position) {
        if(playerType == null || playerType.isEmpty()) {
            return -1;
        }

        if(playerType.equals("vampire")) {
            return position + 1000;
        } else if(playerType.equals("hunter")) {
            return position + 2000;
        }

        return -2;
    }

}
