package ir.ugstudio.vampire.managers;

import android.content.Context;

import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;

/**
 * Created by mehdiii on 10/8/16.
 */

public class UserManager {
    public static String readToken(Context context) {
        String userJson = VampirePreferenceManager.readString(context, Consts.USER_JSON, null);
        if(userJson == null || userJson.isEmpty()) {
            return null;
        }

        try {
            return ((User) User.deserialize(userJson, User.class)).getToken();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
