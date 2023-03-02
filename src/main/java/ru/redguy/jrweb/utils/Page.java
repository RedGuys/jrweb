package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Pattern;

public class Page {
    private Method method = null;
    private final Pattern regex;
    private final ContextRunner runner;

    public Page(String regex, ContextRunner runner) {
        this.regex = Pattern.compile(regex);
        this.runner = runner;
    }

    public Page(Method method, String regex, ContextRunner runner) {
        this.method = method;
        this.regex = Pattern.compile(regex);
        this.runner = runner;
    }

    public void processRequest(String path,@NotNull Context context) throws IOException {
        if(regex.matcher(path).matches()&& (method == null || method.equals(context.request.method))) {
            runner.run(context);
            context.processed = true;
        }
    }

    public Pattern getRegex() {
        return regex;
    }
}
