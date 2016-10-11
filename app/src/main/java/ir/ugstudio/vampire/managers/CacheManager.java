package ir.ugstudio.vampire.managers;

import android.location.Location;
import android.util.Log;

import ir.ugstudio.vampire.models.QuotesResponse;
import ir.ugstudio.vampire.utils.Consts;
import ir.ugstudio.vampire.utils.MemoryCache;

public class CacheManager {
    public static Location getLastLocation() {
        return MemoryCache.get(Consts.CACHE_LAST_LOCATION);
    }

    public static void setLastLocation(Location location) {
        MemoryCache.set(Consts.CACHE_LAST_LOCATION, location);
    }

    public static void setQuotes(QuotesResponse quotes) {
        MemoryCache.set(Consts.CACHE_QUOTES, quotes);
    }

    public static QuotesResponse getQuotes() {
        return MemoryCache.get(Consts.CACHE_QUOTES);
    }
}
