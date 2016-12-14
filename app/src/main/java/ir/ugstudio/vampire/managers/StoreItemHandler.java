package ir.ugstudio.vampire.managers;

import android.content.Context;

import ir.ugstudio.vampire.models.StoreItemVirtual;

public class StoreItemHandler {
    public static int getCost(Context context, String itemId) {
        for (StoreItemVirtual item : SharedPrefHandler.readStoreItems(context).getVirtuals()) {
            if (item.getItemId().equals(itemId)) {
                return item.getPrice();
            }
        }
        return 0;
    }
}
