package ir.ugstudio.vampire.utils;

import android.content.Context;
import android.content.res.TypedArray;

import ir.ugstudio.vampire.R;

public class StoreIconSelector {
    static int[] reals = null;
    static int[] virtuals = null;

    private static void loadReals(Context context, String role) {
        TypedArray realsTypedArray = null;
        if (role.equals("hunter"))
            realsTypedArray = context.getResources().obtainTypedArray(R.array.real_hunter_store_icons);
        else
            realsTypedArray = context.getResources().obtainTypedArray(R.array.real_vampire_store_icons);

        int length = realsTypedArray.length();
        reals = new int[length];
        for (int i = 0; i < length; i++)
            reals[i] = realsTypedArray.getResourceId(i, 0);
        realsTypedArray.recycle();
    }

    private static void loadVirtuals(Context context, String role) {
        TypedArray virtualsTypedArray = null;
        if (role.equals("hunter"))
            virtualsTypedArray = context.getResources().obtainTypedArray(R.array.virtual_hunter_store_icons);
        else
            virtualsTypedArray = context.getResources().obtainTypedArray(R.array.virtual_vampire_store_icons);

        int length = virtualsTypedArray.length();
        virtuals = new int[length];
        for (int i = 0; i < length; i++)
            virtuals[i] = virtualsTypedArray.getResourceId(i, 0);
        virtualsTypedArray.recycle();
    }

    public static int getRealStoreItem(Context context, int imageType, String role) {
        if (reals == null) {
            loadReals(context, role);
        }

        if (reals.length >= imageType && imageType > 0)
            return reals[imageType - 1];
        else
            return reals[reals.length - 1];
    }

    public static int getVirtualStoreItem(Context context, int imageType, String role) {
        if (virtuals == null) {
            loadVirtuals(context, role);
        }

        if (virtuals.length >= imageType && imageType > 0)
            return virtuals[imageType - 1];
        else
            return virtuals[virtuals.length - 1];
    }
}
