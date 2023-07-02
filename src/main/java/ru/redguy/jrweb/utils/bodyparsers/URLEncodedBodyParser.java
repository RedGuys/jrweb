package ru.redguy.jrweb.utils.bodyparsers;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;

import java.io.IOException;
import java.util.HashMap;

public class URLEncodedBodyParser extends BodyParser {

    public URLEncodedBodyParser() {
        super("application/x-www-form-urlencoded");
    }

    @Override
    public void parse(@NotNull Context context) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            while (context.request.reader.ready()) {
                char c = (char) context.request.reader.read();
                stringBuilder.append(c);
            }
            String[] params = stringBuilder.toString().split("&");
            HashMap<String, String> parameters = new HashMap<>();
            for (String param : params) {
                String[] keyValue = param.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
            context.request.params = parameters;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
