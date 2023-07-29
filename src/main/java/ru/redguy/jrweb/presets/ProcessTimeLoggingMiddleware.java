package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.Middleware;
import ru.redguy.jrweb.utils.MiddlewarePosition;
import ru.redguy.jrweb.utils.SessionData;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

public class ProcessTimeLoggingMiddleware {

    public static HashMap<Context, Instant> sessions = new HashMap<>();

    /**
     * Middleware to record start time of request.
     */
    public static class Pre extends Middleware {
        @Override
        public void run(@NotNull Context context) throws IOException {
            if(context.session != null) {
                ProcessLoggingSessionData plsd = context.session.get(ProcessLoggingSessionData.class);
                plsd.start = Instant.now();
            } else {
                sessions.put(context, Instant.now());
            }
        }
    }

    /**
     * Middleware to record end time of request and print it.
     */
    public static class Post extends Middleware {

        public Post() {
            super();
            setPosition(MiddlewarePosition.AFTER);
        }

        @Override
        public void run(@NotNull Context context) throws IOException {
            if(context.session != null) {
                ProcessLoggingSessionData plsd = context.session.get(ProcessLoggingSessionData.class);
                System.out.println("Process time: " + context.request.url + " " + (Instant.now().toEpochMilli() - plsd.start.toEpochMilli()) + "ms");
            } else {
                System.out.println("Process time: " + context.request.url + " " + (Instant.now().toEpochMilli() - sessions.get(context).toEpochMilli()) + "ms");
            }
        }
    }

    public static class ProcessLoggingSessionData extends SessionData {
        public Instant start;
    }
}
