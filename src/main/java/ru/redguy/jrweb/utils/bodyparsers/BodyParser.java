package ru.redguy.jrweb.utils.bodyparsers;

import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.Body;
import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.optional.GsonUtil;

import java.util.HashMap;
import java.util.function.Function;

public abstract class BodyParser {

    public static HashMap<String, Function<Context,Body>> bodyParsers = new HashMap<>();

    static {
        bodyParsers.put("application/x-www-form-urlencoded", URLEncodedBody::new);
        bodyParsers.put("default", BytesBody::new);
        if(GsonUtil.isSupported()) {
            bodyParsers.put("application/json", JsonBody::new);
        }
    }

    public static Body parse(Context context) {
        if (context.request.headers.has(Headers.Common.CONTENT_TYPE)) {
            String type = context.request.headers.getFirst(Headers.Common.CONTENT_TYPE).getValue();
            if (bodyParsers.containsKey(type)) {
                return bodyParsers.get(type).apply(context);
            }
        }
        return bodyParsers.get("default").apply(context);
    }
}
