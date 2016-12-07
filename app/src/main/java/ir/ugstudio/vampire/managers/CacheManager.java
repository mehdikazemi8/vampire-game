package ir.ugstudio.vampire.managers;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static Map<String, Object> map = new HashMap<String, Object>();

    public static void set(String key, Object value) {
        map.put(key, value);
    }

    public static <T> T get(String key) {
        if (!map.containsKey(key))
            return null;
        else
            return (T) map.get(key);
    }
}
