package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.Headers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cookies {
    private Context context;
    private List<Cookie> cookies = new ArrayList<>();

    public Cookies(Context context) {
        this.context = context;
    }

    /**
     * Internal method for adding cookie. Use {@link #addCookie(Cookie)} instead.
     * @param cookie
     */
    protected void internalAddCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    /**
     * Internal method who adds {@link Headers.Response#SET_COOKIE} header. Use {@link #addCookie(Cookie)} instead.
     * @param cookie cookie to add
     */
    private void addCookieUpdateHeader(@NotNull Cookie cookie) {
        context.response.getHeaders().add(Headers.Response.SET_COOKIE, cookie.toString());
    }

    /**
     * Adds cookie to response and to cookies list.
     * @param cookie
     */
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
        addCookieUpdateHeader(cookie);
    }

    /**
     * Returns list of cookies.
     * @return list of cookies.
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * Sets list of cookies. Removes all cookies from response and adds new.
     * @param cookies list of cookies.
     */
    public void setCookies(@NotNull List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            cookie.setExpires("Thu, Jan 01 1970 00:00:00 UTC");
            addCookieUpdateHeader(cookie);
        }
        this.cookies = cookies;
        for (Cookie cookie : cookies) {
            addCookieUpdateHeader(cookie);
        }
    }

    /**
     * Returns cookie by name.
     * @param name name of cookie.
     * @return cookie or empty optional if cookie not found.
     */
    public Optional<String> getCookie(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Sets cookie by name. If cookie not found, creates new cookie.
     * @param name name of cookie.
     * @param value value of cookie.
     * @return created cookie.
     */
    public Cookie setCookie(String name, String value) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                cookie.setValue(value);
                addCookieUpdateHeader(cookie);
                return cookie;
            }
        }
        Cookie cookie = new Cookie(name, value);
        cookies.add(cookie);
        addCookieUpdateHeader(cookie);
        return cookie;
    }

    /**
     * Removes cookie by name.
     * @param name name of cookie.
     */
    public void removeCookie(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                cookies.remove(cookie);
                cookie.setExpires("Thu, Jan 01 1970 00:00:00 UTC");
                addCookieUpdateHeader(cookie);
                return;
            }
        }
    }

    /**
     * Checks if cookie exists.
     * @param name name of cookie.
     * @return true if cookie exists.
     */
    public boolean hasCookie(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Cookie cookie : cookies) {
            stringBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        return stringBuilder.toString();
    }
}
