package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

public abstract class AuthorizationMiddleware extends Middleware {

    public static class AuthorizationData {
        public String type;
        public String payload;

        public AuthorizationData(String type, String payload) {
            this.type = type;
            this.payload = payload;
        }
    }

    public AuthorizationMiddleware(Method method) {
        super(method);
    }

    public AuthorizationData getAuthorizationData(@NotNull Context ctx) {
        HeaderValue headerValue = ctx.request.headers.getFirst(Headers.Request.AUTHORIZATION);
        if (headerValue == null) {
            return null;
        }
        String value = headerValue.getValue();
        if (value == null) {
            return null;
        }
        String[] split = value.split(" ");
        if (split.length != 2) {
            return null;
        }
        return new AuthorizationData(split[0], split[1]);
    }
}
