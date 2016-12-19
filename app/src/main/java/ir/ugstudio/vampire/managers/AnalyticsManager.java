package ir.ugstudio.vampire.managers;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import ir.ugstudio.vampire.VampireApp;

public class AnalyticsManager {
    public static String VIEW_SCREEN = "VIEW_SCREEN";
    public static String SHARE_APP = "SHARE_APP";
    public static String RATE_APP = "RATE_APP";
    public static String FAB_TAPPED = "FAB_TAPPED";
    public static String ATTACK_DIALOG = "ATTACK_DIALOG";

    public static void setViewScreen(String screenName) {
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, screenName);
        VampireApp.firebaseAnalytics.logEvent(VIEW_SCREEN, payload);
    }

    public static void logEvent(String event, String value) {
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, value);
        VampireApp.firebaseAnalytics.logEvent(event, payload);
    }
}
