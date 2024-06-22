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

    /**
     * Tries to process request
     *
     * @param path    path
     * @param context request context
     * @throws IOException execption from run implementation
     */
    public void processRequest(String path, @NotNull Context context) throws Exception {
        if (regex.matcher(path).matches() && (method == null || method.equals(context.request.method))) {
            run(context);
            context.processed = true;
        }
    }

    /**
     * Page logic implementation
     *
     * @param context request context
     * @throws Exception
     */
    public abstract void run(Context context) throws Exception;

    public Pattern getRegex() {
        return regex;
    }
}
