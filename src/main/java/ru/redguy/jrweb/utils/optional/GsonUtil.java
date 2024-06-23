package ru.redguy.jrweb.utils.optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonUtil {

    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    public static void registerTypeAdapter(Class<?> type, Object typeAdapter) {
        gson = gson.newBuilder().registerTypeAdapter(type, typeAdapter).create();
    }

    public static JsonElement parse(String json) {
        return JsonParser.parseString(json.trim());
    }

    public static boolean isSupported() {
        try {
            Class.forName("com.google.gson.Gson");
            Class.forName("com.google.gson.JsonElement");
            Class.forName("com.google.gson.JsonParser");
            return true;
        } catch (ClassNotFoundException | UnsatisfiedLinkError e) {
            return false;
        }
    }
}
