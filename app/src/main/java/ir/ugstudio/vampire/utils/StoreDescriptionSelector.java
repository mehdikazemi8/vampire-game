package ir.ugstudio.vampire.utils;

import android.content.Context;

import ir.ugstudio.vampire.R;

public class StoreDescriptionSelector {
    public static String getDescription(Context context, String itemId) {
        switch (itemId) {
            case "heal":
                return context.getString(R.string.virtual_desc_heal);

            case "sight_range":
                return context.getString(R.string.virtual_desc_sight_range);

            case "attack_range":
                return context.getString(R.string.virtual_desc_attack_range);

            case "invisible":
                return context.getString(R.string.virtual_desc_invisible);

            case Consts.NEAREST_SHEEP:
                return String.format(context.getString(R.string.desc_nearest_sheep), 50);

            case Consts.NEAREST_TOWER:
                return String.format(context.getString(R.string.desc_nearest_tower), 100);

            case Consts.NEAREST_PLAYER:
                return String.format(context.getString(R.string.desc_nearest_player), 150);
        }
        return "";
    }
}
