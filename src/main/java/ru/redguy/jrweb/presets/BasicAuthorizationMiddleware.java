package ru.redguy.jrweb.presets;

import ru.redguy.jrweb.utils.*;

import java.util.Base64;

public class BasicAuthorizationMiddleware extends Middleware {
    public BasicAuthorizationMiddleware(AuthorizationChecker checker) {
        this(null, checker);
    }

    public BasicAuthorizationMiddleware(Method method, AuthorizationChecker checker) {
        super(method, (ctx) -> {
            HeaderValue headerValue = ctx.request.headers.getFirst(Headers.Request.AUTHORIZATION);
            if (headerValue == null) {
                ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
                ctx.cancelled = true;
                ctx.response.send("Unauthorized");
                return;
            }
            String value = headerValue.getValue();
            if (value == null) {
                ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
                ctx.cancelled = true;
                ctx.response.send("Unauthorized");
                return;
            }
            String[] split = value.split(" ");
            if (split.length != 2) {
                ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
                ctx.cancelled = true;
                ctx.response.send("Unauthorized");
                return;
            }
            if (!split[0].equals("Basic")) {
                ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
                ctx.cancelled = true;
                ctx.response.send("Unauthorized");
                return;
            }
            String[] credentials = new String(Base64.getDecoder().decode(split[1])).split(":");
            if (credentials.length != 2) {
                ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
                ctx.cancelled = true;
                ctx.response.send("Unauthorized");
                return;
            }
            if (!checker.check(credentials[0], credentials[1])) {
                ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
                ctx.cancelled = true;
                ctx.response.send("Unauthorized");
            }
        });
    }

    public interface AuthorizationChecker {
        boolean check(String username, String password);
    }
}
