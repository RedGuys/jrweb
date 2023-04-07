package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.Cookie;
import ru.redguy.jrweb.Request;

import java.util.HashMap;
import java.util.UUID;

public class SessionStorage {
    private final HashMap<String, Session> sessions = new HashMap<>();

    public Session get(@NotNull Context context) {
        if (context.cookies.hasCookie("jrsession")) {
            String sessionId = context.cookies.getCookie("jrsession");
            if (sessions.containsKey(sessionId)) {
                return sessions.get(sessionId);
            } else {
                Session session = new Session();
                sessions.put(sessionId, session);
                return session;
            }
        } else {
            Session session = new Session();
            String sessionId = generateSessionId();
            sessions.put(sessionId, session);
            context.cookies.addCookie(new Cookie("jrsession", sessionId));
            return session;
        }
    }

    private @NotNull String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
