package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.util.Log;

import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.utils.Consts;

public class SharedPrefHandler {
    public static StoreItems readStoreItems(Context context) {
        String itemsJson = SharedPrefManager.readString(context, Consts.SP_STORE_ITEMS, null);
        if (itemsJson == null || itemsJson.isEmpty()) {
            Log.d("TAG", "error storeItems is empty");
            return null;
        }

        try {
            return ((StoreItems) StoreItems.deserialize(itemsJson, StoreItems.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void writeStoreItems(Context context, StoreItems items) {
        SharedPrefManager.writeString(context, Consts.SP_STORE_ITEMS, items.serialize());
        Log.d("TAG", "storeItems not empty and saved now");
    }
}

