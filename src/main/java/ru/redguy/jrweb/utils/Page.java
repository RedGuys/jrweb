package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Page {
    private final Pattern regex;
    private final ContextRunner runner;

    public Page(String regex, ContextRunner runner) {
        this.regex = Pattern.compile(regex);
        this.runner = runner;
    }

    public void processRequest(@NotNull Context context) {
        if(regex.matcher(context.request.url).matches()) {
            runner.run(context);
            context.processed = true;
        }
    }
}
