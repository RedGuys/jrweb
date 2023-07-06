package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.Session;
import ru.redguy.jrweb.utils.StatusCodes;

public class Context {
    public Request request;
    public Response response;
    public Cookies cookies = new Cookies(this);
    public Session session;

    public Context(Request request, Response response) {
        this.request = request;
        this.response = response;
        this.response.setContext(this);
    }

    public boolean cancelled = false;
    public boolean processed = false;

    public void redirect(@NotNull String url) {
        response.setStatusCode(StatusCodes.FOUND(url));
        processed = true;
    }
}
