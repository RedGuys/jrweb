package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.Session;

public class Context {
    public Request request;
    public Response response;
    public Cookies cookies = new Cookies(this);
    public Session session;

    public Context(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public boolean cancelled = false;
    public boolean processed = false;
}
