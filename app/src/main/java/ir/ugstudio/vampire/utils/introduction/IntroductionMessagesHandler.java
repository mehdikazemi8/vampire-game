package ir.ugstudio.vampire.utils.introduction;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import ir.ugstudio.vampire.managers.SharedPrefManager;

public class IntroductionMessagesHandler {
    public static List<String> getMessages(Context context, BaseIntroductionMessages object) {
        return Arrays.asList(context.getResources().getStringArray(object.getResourceId()));
    }

    public static boolean showMessage(Context context, BaseIntroductionMessages object) {
        if (SharedPrefManager.hasKey(context, object.getClass().getCanonicalName()))
            return false;

        SharedPrefManager.writeBoolean(context, object.getClass().getCanonicalName(), true);
        return true;
    }
}
