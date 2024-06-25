package ru.redguy.jrweb.utils.bodyparsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ru.redguy.jrweb.Body;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.optional.GsonUtil;

import java.io.IOException;

public class JsonBody extends Body {

    private JsonElement jsonElement;

    public JsonBody(Context context) {
        super(context);
    }

    @Override
    protected void parse() {
        try {
            jsonElement = GsonUtil.parse(context.reader.readString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(Class<T> clazz) {
        return GsonUtil.getGson().fromJson(jsonElement, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return GsonUtil.getGson().fromJson(jsonElement.getAsJsonObject().get(key), clazz);
    }

    public JsonPrimitive get(String key) {
        return jsonElement.getAsJsonObject().getAsJsonPrimitive(key);
    }

    public boolean has(String key) {
        return jsonElement.getAsJsonObject().has(key);
    }
}
