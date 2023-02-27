package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Middleware {

    private Pattern regex = Pattern.compile("");
    private MiddlewarePosition position = MiddlewarePosition.BEFORE;
    private final ContextRunner runner;

    public Middleware(ContextRunner runner) {
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

    public void processRequest(MiddlewarePosition position, @NotNull Context context) {
        if((regex.matcher(context.request.url).matches()||regex.toString().equals("")) && (this.position == position || this.position == MiddlewarePosition.BOTH))
            runner.run(context);
    }
}
