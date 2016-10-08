package ir.ugstudio.vampire.models;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by mehdiii on 10/8/16.
 */

public class BaseModel {
    public static <T> T deserialize(String json, Type type) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
