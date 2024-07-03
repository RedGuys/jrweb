package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Router {

    protected Pattern pattern = Pattern.compile("");

    protected List<Middleware> middlewares = new ArrayList<>();
    protected List<Page> pages = new ArrayList<>();
    protected List<Router> routers = new ArrayList<>();

    /**
     * Constructs global router
     */
    public Router() {
    }

    /**
     * Constructs router with pattern
     * @param pattern regex for request url validation
     */
    public Router(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * Processes input request
     * @param path current path
     * @param context request context
     */
    public void processRequest(@NotNull String path, @NotNull Context context) {
        if (!pattern.matcher(context.request.url.substring(path.length())).find() && !Objects.equals(pattern.toString(), ""))
            return;

        try {
            for (Middleware middleware : middlewares) {
                middleware.processRequest(context.request.url.substring(path.length() + pattern.toString().length()), MiddlewarePosition.BEFORE, context);
            }

            if (!context.cancelled) {
                for (Router router : routers) {
                    router.processRequest(path + pattern.toString(), context);
                    if (context.processed)
                        break;
                }

                if (!context.processed) {
                    for (Page page : pages) {
                        page.processRequest(context.request.url.substring(path.length() + pattern.toString().length()), context);
                        if (context.processed)
                            break;
                    }
                }
            } else {
                context.processed = true;
            }

            for (Middleware middleware : middlewares) {
                middleware.processRequest(context.request.url.substring(path.length() + pattern.toString().length()), MiddlewarePosition.AFTER, context);
            }
        } catch (Exception e) {
            context.server.getErrorHandlers().on500(context, e);
        }
    }

    /**
     * Changes pattern for router
     * @param pattern new {@link Pattern}
     * @return self
     */
    public Router setPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * Adds new {@link Middleware} to router
     * @param middleware target middleware
     */
    public void add(Middleware middleware) {
        middlewares.add(middleware);
    }

    /**
     * Adds new {@link Page} to router
     * @param page target page
     */
    public void add(Page page) {
        pages.add(page);
    }

    /**
     * Adds new {@link Router} to router
     * @param router target router
     */
    public void add(Router router) {
        routers.add(router);
    }

    public List<Map.Entry<String, Page>> getPages() {
        ArrayList<Map.Entry<String, Page>> allPages = new ArrayList<>();
        for (Page page : pages) {
            allPages.add(new AbstractMap.SimpleEntry<>(pattern.pattern() + page.getRegex().pattern(), page));
        }
        for (Router router : routers) {
            for (Map.Entry<String, Page> page : router.getPages()) {
                allPages.add(new AbstractMap.SimpleEntry<>(pattern.pattern() + page.getKey(), page.getValue()));
            }
        }
        return allPages;
    }
}
