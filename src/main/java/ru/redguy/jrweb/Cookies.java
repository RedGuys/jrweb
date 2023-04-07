package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.Headers;

import java.util.ArrayList;
import java.util.List;

public class Cookies {

    private Context context;
    private List<Cookie> cookies = new ArrayList<>();

    public Cookies(Context context) {
        this.context = context;
    }

    protected void internalAddCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    private void addCookieUpdateHeader(@NotNull Cookie cookie) {
        context.response.getHeaders().add(Headers.Response.SET_COOKIE, cookie.toString());
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
        addCookieUpdateHeader(cookie);
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

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

    public String getCookie(String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

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
