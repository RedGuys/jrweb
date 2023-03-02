package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Pattern;

public class Middleware {

    private Method method = null;
    private Pattern regex = Pattern.compile("");
    private MiddlewarePosition position = MiddlewarePosition.BEFORE;
    private final ContextRunner runner;

    public Middleware(ContextRunner runner) {
        this.runner = runner;
    }

    public Middleware(Method method, ContextRunner runner) {
        this.method = method;
        this.runner = runner;
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
        if ((regex.matcher(path).matches() || regex.toString().equals(""))
                && (this.position == position || this.position == MiddlewarePosition.BOTH)
                && (method == null || method.equals(context.request.method)))
            runner.run(context);
    }

    public Pattern getRegex() {
        return regex;
    }
}
