package ir.ugstudio.vampire.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontHelper {
    private static final String FONT_APP_NAME = "roya_bold.ttf";
    //    private static final String FONT_APP_NAME = "iran_sans_light.ttf";
    //    private static final String FONT_AVATAR_NAME = "avatars.ttf";
    private static final String FONT_MENU_NAME = "icons.ttf";

    private static Typeface koodak = null;
    private static Typeface avatars = null;
    private static Typeface menu = null;

    public static Typeface getKoodak(Context context) {
        if (koodak == null)
            koodak = Typeface.createFromAsset(context.getAssets(), FONT_APP_NAME);
        return koodak;
    }

    public static Typeface getDefaultTypeface(Context context) {
        if (koodak == null)
            koodak = Typeface.createFromAsset(context.getAssets(), FONT_APP_NAME);
        return koodak;
    }

    public static void setKoodakFor(Context context, TextView... views) {
        Typeface koodak = getKoodak(context);
        for (TextView view : views) {
            view.setTypeface(koodak);
        }
    }

//    public static Typeface getAvatars(Context context) {
//        if (avatars == null)
//            avatars = Typeface.createFromAsset(context.getAssets(), FONT_AVATAR_NAME);
//        return avatars;
//    }

    public static Typeface getIcons(Context context) {
        if (menu == null)
            menu = Typeface.createFromAsset(context.getAssets(), FONT_MENU_NAME);
        return menu;
    }
}
