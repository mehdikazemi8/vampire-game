package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.util.Log;

import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;

public class UserHandler {
    public static String readToken(Context context) {
        // todo, is null when logging out
        String userJson = SharedPrefManager.readString(context, Consts.USER_JSON, null);
        Log.d("TAG", "UserHandler " + userJson);
        if (userJson == null || userJson.isEmpty()) {
            return null;
        }

        try {
            return ((User) User.deserialize(userJson, User.class)).getToken();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void clearUser(Context context) {
        SharedPrefManager.writeString(context, Consts.USER_JSON, null);
    }

    public static void writeUser(Context context, User user) {
        SharedPrefManager.writeString(context, Consts.USER_JSON, user.serialize());
    }
}
