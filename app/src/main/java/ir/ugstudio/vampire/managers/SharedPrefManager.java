package ir.ugstudio.vampire.managers;

import android.content.Context;

import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;

public class SharedPrefManager {
    public static StoreItems readStoreItems(Context context) {
        String itemsJson = VampirePreferenceManager.readString(context, Consts.SP_STORE_ITEMS, null);
        if (itemsJson == null || itemsJson.isEmpty()) {
            return null;
        }

        try {
            return ((StoreItems) StoreItems.deserialize(itemsJson, User.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void writeStoreItems(Context context, StoreItems items) {
        VampirePreferenceManager.writeString(context, Consts.USER_JSON, items.serialize());
    }
}

