package ir.ugstudio.vampire.utils.introduction;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

public class IntroductionMessagesHandler {
    public static List<String> getMessages(Context context, BaseIntroductionMessages object) {
        return Arrays.asList(context.getResources().getStringArray(object.getResourceId()));
    }
}
