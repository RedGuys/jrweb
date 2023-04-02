package ru.redguy.jrweb.utils;

import ru.redguy.jrweb.Cookies;
import ru.redguy.jrweb.Request;
import ru.redguy.jrweb.Response;

public class Context {
    public Request request;
    public Response response;
    public Cookies cookies = new Cookies(this);

    public Context(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public boolean cancelled = false;
    public boolean processed = false;
}
