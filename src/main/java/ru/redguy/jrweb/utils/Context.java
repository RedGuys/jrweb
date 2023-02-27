package ru.redguy.jrweb.utils;

public class Context {
    public Request request;
    public Response response;

    public Context(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    boolean cancelled = false;
    boolean processed = false;
}
