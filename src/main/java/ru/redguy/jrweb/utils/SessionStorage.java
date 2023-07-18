package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.Cookie;
import ru.redguy.jrweb.WebServer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionStorage {
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final WebServer webServer;

    public SessionStorage(@NotNull WebServer webServer) {
        this.webServer = webServer;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            sessions.entrySet().removeIf(entry -> entry.getValue().deleteAt.isBefore(Instant.now()));
        }, webServer.getOptions().getSessionCheckInterval(), webServer.getOptions().getSessionCheckInterval(), TimeUnit.SECONDS);
    }

    public Session get(@NotNull Context context) {
        if (webServer.getOptions().isRemoveExpiredSessionsOnAccess()) {
            sessions.entrySet().removeIf(entry -> entry.getValue().deleteAt!=null&&entry.getValue().deleteAt.isBefore(Instant.now()));
        }
        if (context.cookies.hasCookie("jrsession")) {
            String sessionId = context.cookies.getCookie("jrsession");
            if (sessions.containsKey(sessionId)) {
                return sessions.get(sessionId);
            } else {
                Session session = new Session(webServer.getOptions().getSessionTTL() == -1?null:Instant.now().plus(webServer.getOptions().getSessionTTL(), ChronoUnit.SECONDS));
                sessions.put(sessionId, session);
                return session;
            }
        } else {
            Session session = new Session(webServer.getOptions().getSessionTTL() == -1?null:Instant.now().plus(webServer.getOptions().getSessionTTL(), ChronoUnit.SECONDS));
            String sessionId = generateSessionId();
            sessions.put(sessionId, session);
            context.cookies.addCookie(new Cookie("jrsession", sessionId, "/").setMaxAge(86400L));
            return session;
        }
    }

    private @NotNull String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
