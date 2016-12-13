package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ir.ugstudio.vampire.R;

public class AvatarManager {
    static int[] vampires = null;
    static int[] hunters = null;

    public static Bitmap getBitmap(Context context, int position) {
        return BitmapFactory.decodeResource(context.getResources(), getResourceId(context, position));
    }

    private static void load(Context context) {
        TypedArray vampiresTypedArray = context.getResources().obtainTypedArray(R.array.vampires);
        int lengthVampires = vampiresTypedArray.length();
        vampires = new int[lengthVampires];
        for (int i = 0; i < lengthVampires; i++)
            vampires[i] = vampiresTypedArray.getResourceId(i, 0);
        vampiresTypedArray.recycle();

        TypedArray huntersTypedArray = context.getResources().obtainTypedArray(R.array.hunters);
        int length = huntersTypedArray.length();
        hunters = new int[length];
        for (int i = 0; i < length; i++)
            hunters[i] = huntersTypedArray.getResourceId(i, 0);
        huntersTypedArray.recycle();
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
