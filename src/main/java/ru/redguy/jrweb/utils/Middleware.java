package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;

import java.io.IOException;
import java.util.regex.Pattern;

public abstract class Middleware {

    private Method method = null;
    private Pattern regex = Pattern.compile("");
    private MiddlewarePosition position = MiddlewarePosition.BEFORE;

    public Middleware() {
    }

    public Middleware(Method method) {
        this.method = method;
    }

    public Middleware setRegex(String regex) {
        this.regex = Pattern.compile(regex);
        return this;
    }

    public Middleware setPosition(MiddlewarePosition position) {
        this.position = position;
        return this;
    }

    public Middleware setMethod(Method method) {
        this.method = method;
        return this;
    }

    public void processRequest(String path, MiddlewarePosition position, @NotNull Context context) throws IOException {
        if ((getRegex().matcher(path).matches() || getRegex().toString().equals(""))
                && (getPosition() == position || getPosition() == MiddlewarePosition.BOTH)
                && (getMethod() == null || getMethod().equals(context.request.method)))
            run(context);
    }

    public abstract void run(Context context) throws IOException;

    public Pattern getRegex() {
        return regex;
    }

    public MiddlewarePosition getPosition() {
        return position;
    }

    public Method getMethod() {
        return method;
    }
}
