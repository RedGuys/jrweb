package ru.redguy.jrweb;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Cookie {
    private String name;
    private String value;
    private String path;
    private String expires;

    @Contract(pure = true)
    public Cookie(@NotNull String cookie) {
        String[] split = cookie.split("=");
        name = split[0];
        value = split[1];
    }

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Cookie(String name, String value, String path) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    protected void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append("=");
        stringBuilder.append(value);
        if(path != null) {
            stringBuilder.append("; Path=");
            stringBuilder.append(path);
        }
        if(expires != null) {
            stringBuilder.append("; Expires=");
            stringBuilder.append(expires);
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }
}
