package ir.ugstudio.vampire.managers;

import android.location.Location;

import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.models.User;
import ir.ugstudio.vampire.utils.Consts;

public class CacheHandler {
    public static Location getLastLocation() {
        return CacheManager.get(Consts.CACHE_LAST_LOCATION);
    }

    public static void setLastLocation(Location location) {
        CacheManager.set(Consts.CACHE_LAST_LOCATION, location);
    }

    public static void setQuotes(QuotesResponse quotes) {
        CacheManager.set(Consts.CACHE_QUOTES, quotes);
    }

    public static QuotesResponse getQuotes() {
        return CacheManager.get(Consts.CACHE_QUOTES);
    }

    public static User getUser() {
        return CacheManager.get(Consts.CACHE_USER);
    }

    public static void setUser(User user) {
        CacheManager.set(Consts.CACHE_USER, user);
    }

    public static boolean amIOwnerOfThisTower(String towerId) {
        return getUser().getTowers().contains(towerId);
    }
}
