package ru.redguy.jrweb.utils.optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class GsonUtil {

    private static Gson gson = new Gson();
    private static final HashMap<Class<?>,Object> typeAdapters = new HashMap<>();

    public static Gson getGson() {
        return gson;
    }

    public static void registerTypeAdapter(Class<?> type, Object typeAdapter) {
        gson = gson.newBuilder().registerTypeAdapter(type, typeAdapter).create();
        typeAdapters.put(type, typeAdapter);
    }

    public static boolean isTypeAdapterRegistered(Class<?> type) {
        return typeAdapters.containsKey(type);
    }

    public static Object getTypeAdapter(Class<?> type) {
        return typeAdapters.get(type);
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
