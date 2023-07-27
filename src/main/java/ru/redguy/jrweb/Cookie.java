package ru.redguy.jrweb;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Cookie {
    private String name;
    private String value;
    private String path;
    private String expires;
    private Long maxAge;

    /**
     * Create cookie from string.
     * @param cookie string with cookie.
     */
    @Contract(pure = true)
    public Cookie(@NotNull String cookie) {
        String[] split = cookie.split("=");
        name = split[0].trim();
        value = split[1];
    }

    /**
     * Create cookie with name and value.
     * @param name name of cookie.
     * @param value value of cookie.
     */
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Create cookie with name, value and path.
     * @param name name of cookie.
     * @param value value of cookie.
     * @param path path of cookie.
     */
    public Cookie(String name, String value, String path) {
        this.name = name;
        this.value = value;
        this.path = path;
    }

    /**
     * @return name of cookie.
     */
    public String getName() {
        return name;
    }

    /**
     * @return value of cookie.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set value of cookie.
     * @param value value of cookie.
     */
    protected void setValue(String value) {
        this.value = value;
    }

    /**
     * @return path of cookie.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set path of cookie.
     * @param path path of cookie.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return expires of cookie.
     */
    public String getExpires() {
        return expires;
    }

    /**
     * Set expires of cookie. Format: "Thu, Jan 01 1970 00:00:00 UTC"
     * @param expires expires of cookie.
     */
    public void setExpires(String expires) {
        this.expires = expires;
    }

    /**
     * @return max age of cookie.
     */
    public Long getMaxAge() {
        return maxAge;
    }

    /**
     * Set max age of cookie. In seconds.
     * @param maxAge max age of cookie.
     */
    public Cookie setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    /**
     * @return string with cookie. Format: "name=value; Path=path; Expires=expires; Max-Age=maxAge;"
     */
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
        if(maxAge != null) {
            stringBuilder.append("; Max-Age=");
            stringBuilder.append(maxAge);
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }
}
