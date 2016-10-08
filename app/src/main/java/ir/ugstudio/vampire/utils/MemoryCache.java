package ir.ugstudio.vampire.utils;

import java.util.HashMap;
import java.util.Map;

public class MemoryCache {

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
