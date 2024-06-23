package ru.redguy.jrweb.utils.bodyparsers;

import com.google.gson.JsonElement;
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
}
