package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Router {

    Pattern pattern = Pattern.compile("");

    List<Middleware> middlewares = new ArrayList<>();
    List<Page> pages = new ArrayList<>();
    List<Router> routers = new ArrayList<>();

    public Router() {
    }

    public Router(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public void processRequest(@NotNull String path, @NotNull Context context) {
        if (!pattern.matcher(context.request.url.substring(path.length())).find() && !Objects.equals(pattern.toString(), ""))
            return;

        for (Middleware middleware : middlewares) {
            middleware.processRequest(context.request.url.substring(path.length()+pattern.toString().length()), MiddlewarePosition.BEFORE, context);
        }

        if (!context.cancelled) {
            for (Router router : routers) {
                router.processRequest(path+pattern.toString(), context);
                if (context.processed)
                    break;
            }

            if (!context.processed) {
                for (Page page : pages) {
                    page.processRequest(context.request.url.substring(path.length()+pattern.toString().length()), context);
                    if (context.processed)
                        break;
                }
            }
        }

        for (Middleware middleware : middlewares) {
            middleware.processRequest(context.request.url.substring(path.length()+pattern.toString().length()), MiddlewarePosition.AFTER, context);
        }
    }

    public Router setPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public void add(Middleware middleware) {
        middlewares.add(middleware);
    }

    public void add(Page page) {
        pages.add(page);
    }

    public void add(Router router) {
        routers.add(router);
    }
}
