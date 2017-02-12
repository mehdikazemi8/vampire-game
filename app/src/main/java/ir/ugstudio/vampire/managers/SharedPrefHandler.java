package ir.ugstudio.vampire.managers;

import android.content.Context;
import android.util.Log;

import ir.ugstudio.vampire.models.StoreItems;
import ir.ugstudio.vampire.models.nearest.Target;
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

    public static void writeMissionObject(Context context, Target object) {
        SharedPrefManager.writeString(context, Consts.SP_MISSION_OBJECT, object.serialize());
        Log.d("TAG", "writeMissionObject not empty and saved now");
    }

    public static Target readMissionObject(Context context) {
        String nearestObjectJson = SharedPrefManager.readString(context, Consts.SP_MISSION_OBJECT, null);
        if (nearestObjectJson == null || nearestObjectJson.isEmpty()) {
            Log.d("TAG", "error storeItems is empty");
            return null;
        }

        try {
            return ((Target) StoreItems.deserialize(nearestObjectJson, Target.class));
        } catch (NullPointerException e) {
            return null;
        }
    }
}

