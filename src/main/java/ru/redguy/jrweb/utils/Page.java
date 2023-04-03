package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

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
            run(context);
            context.processed = true;
        }
    }
    
    public abstract void run(Context context) throws IOException;

    public Pattern getRegex() {
        return regex;
    }
}
