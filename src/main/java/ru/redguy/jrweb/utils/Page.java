package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.bodyparsers.BodyParser;

import java.io.IOException;
import java.util.regex.Pattern;

public abstract class Page {
    private Method method = null;
    private final Pattern regex;

    public Page(String regex) {
        this.regex = Pattern.compile(regex);
    }

    public Page(Method method, String regex) {
        this.method = method;
        this.regex = Pattern.compile(regex);
    }

    public void processRequest(String path,@NotNull Context context) throws IOException {
        if(regex.matcher(path).matches()&& (method == null || method.equals(context.request.method))) {
            if(context.request.headers.has(Headers.Common.CONTENT_TYPE)) {
                if (BodyParser.bodyParsers.containsKey(context.request.headers.getFirst(Headers.Common.CONTENT_TYPE).getValue())) {
                    BodyParser.bodyParsers.get(context.request.headers.getFirst(Headers.Common.CONTENT_TYPE).getValue()).parse(context);
                }
            }
            run(context);
            context.processed = true;
        }
    }
    
    public abstract void run(Context context) throws IOException;

    public Pattern getRegex() {
        return regex;
    }
}
