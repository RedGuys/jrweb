package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Methods {
    public static final Method GET = new Method("GET");
    public static final Method POST = new Method("POST");
    public static final Method PUT = new Method("PUT");
    public static final Method DELETE = new Method("DELETE");
    public static final Method HEAD = new Method("HEAD");
    public static final Method OPTIONS = new Method("OPTIONS");
    public static final Method TRACE = new Method("TRACE");
    public static final Method CONNECT = new Method("CONNECT");
    public static final Method PATCH = new Method("PATCH");

    @Contract(pure = true)
    public static Method getMethod(@NotNull String name) {
        switch (name) {
            case "GET": return GET;
            case "POST": return POST;
            case "PUT": return PUT;
            case "DELETE": return DELETE;
            case "HEAD": return HEAD;
            case "OPTIONS": return OPTIONS;
            case "TRACE": return TRACE;
            case "CONNECT": return CONNECT;
            case "PATCH": return PATCH;
            default: return new Method(name);
        }
    }
}
