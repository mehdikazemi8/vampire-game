package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class VampirePreferenceManager {
    public static String PREFERENCE_NAME = "preference_vampire";

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static boolean writeString(Context context, String key, String data) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return false;
        preferences.edit().putString(key, data).apply();
        return true;
    }

    public static boolean writeLong(Context context, String key, long data) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return false;
        preferences.edit().putLong(key, data).apply();
        return true;
    }

    public static boolean writeInt(Context context, String key, int data) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return false;
        preferences.edit().putInt(key, data).apply();
        return true;
    }

    public static boolean writeBoolean(Context context, String key, boolean data) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return false;
        preferences.edit().putBoolean(key, data).apply();
        return true;
    }


    public static String readString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return null;
        return preferences.getString(key, defaultValue);
    }

    public static int readInteger(Context context, String key, int defaultValue) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return defaultValue;
        return preferences.getInt(key, defaultValue);
    }

    public static long readLong(Context context, String key, long defaultValue) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return defaultValue;
        return preferences.getLong(key, defaultValue);
    }

    public static boolean readBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return defaultValue;
        return preferences.getBoolean(key, defaultValue);
    }

    public static boolean remove(Context context, String key) {
        SharedPreferences preferences = getPreference(context);
        if (preferences == null)
            return false;
        preferences.edit().remove(key).apply();
        return true;
    }

    public static boolean hasKey(Context context, String key) {
        SharedPreferences preferences = getPreference(context);
        return preferences != null && preferences.contains(key);
    }

    public static void clearAll(Context context) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}