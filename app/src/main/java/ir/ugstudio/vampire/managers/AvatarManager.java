package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ir.ugstudio.vampire.R;

public class AvatarManager {
    private static int[] vampires = null;
    private static int[] hunters = null;
    private static int[] vampiresMono = null;
    private static int[] huntersMono = null;

    public static Bitmap getBitmap(Context context, int position) {
        return BitmapFactory.decodeResource(context.getResources(), getResourceId(context, position));
    }

    private static int[] load(Context context, int resId) {
        TypedArray typedArray = context.getResources().obtainTypedArray(resId);
        int length = typedArray.length();
        int[] resultArray = new int[length];
        for (int i = 0; i < length; i++)
            resultArray[i] = typedArray.getResourceId(i, 0);
        typedArray.recycle();
        return resultArray;
    }

    private static void load(Context context) {
        vampires = load(context, R.array.vampires);
        vampiresMono = load(context, R.array.vampires_mono);

        hunters = load(context, R.array.hunters);
        huntersMono = load(context, R.array.hunters_mono);
    }

    public static int getResourceId(Context context, int position) {
        if (vampires == null)
            load(context);

        if (position < 2000) {
            position -= 1000;
            if (0 <= position && position < vampires.length)
                return vampires[position];
            else
                return vampires[vampires.length - 1];
        } else {
            position -= 2000;
            if (0 <= position && position < hunters.length)
                return hunters[position];
            else
                return hunters[hunters.length - 1];
        }
    }

    public static int getResourceIdMono(Context context, int position) {
        if (vampiresMono == null)
            load(context);

        if (position < 2000) {
            position -= 1000;
            if (0 <= position && position < vampiresMono.length)
                return vampiresMono[position];
            else
                return vampiresMono[vampiresMono.length - 1];
        } else {
            position -= 2000;
            if (0 <= position && position < huntersMono.length)
                return huntersMono[position];
            else
                return huntersMono[huntersMono.length - 1];
        }
    }

    public static int getAvatarIndexInThousand(String playerType, int avatarIndex) {
        if (playerType == null || playerType.isEmpty()) {
            return -1;
        }

        if (playerType.equals("vampire")) {
            return avatarIndex + 1000;
        } else if (playerType.equals("hunter")) {
            return avatarIndex + 2000;
        }

        return -2;
    }

}
