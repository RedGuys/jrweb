package ru.redguy.jrweb.utils.bodyparsers;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.Headers;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class URLEncodedBodyParser extends BodyParser {

    public URLEncodedBodyParser() {
        super("application/x-www-form-urlencoded");
    }

    @Override
    public void parse(@NotNull Context context) {
        try {
            if(!context.request.headers.has(Headers.Common.CONTENT_LENGTH))
                return;
            String[] params = context.reader.readString(Integer.parseInt(context.request.headers.getFirst(Headers.Common.CONTENT_LENGTH).getValue())).split("&");
            HashMap<String, Object> parameters = new HashMap<>();
            for (String param : params) {
                String[] keyValue = param.split("=");
                if(keyValue.length == 2)
                    parameters.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
                else
                    parameters.put(URLDecoder.decode(keyValue[0], "UTF-8"), "");

            }
            context.request.params = parameters;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
