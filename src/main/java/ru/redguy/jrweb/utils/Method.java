package ru.redguy.jrweb.utils;

public class Method {
    private final String name;
    private final boolean hasBody;

    public Method(String name, boolean hasBody) {
        this.name = name;
        this.hasBody = hasBody;
    }

    public String getName() {
        return name;
    }

    public boolean hasBody() {
        return hasBody;
    }
}
