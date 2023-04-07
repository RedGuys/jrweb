package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.util.Base64;

public class BasicAuthorizationMiddleware extends Middleware {

    private final AuthorizationChecker checker;

    public BasicAuthorizationMiddleware(AuthorizationChecker checker) {
        this(null, checker);
    }

    public BasicAuthorizationMiddleware(Method method, AuthorizationChecker checker) {
        super(method);
        this.checker = checker;
    }

    @Override
    public void run(Context ctx) throws IOException {
        HeaderValue headerValue = ctx.request.headers.getFirst(Headers.Request.AUTHORIZATION);
        if (headerValue == null) {
            restrict(ctx);
            return;
        }
        String value = headerValue.getValue();
        if (value == null) {
            restrict(ctx);
            return;
        }
        String[] split = value.split(" ");
        if (split.length != 2) {
            restrict(ctx);
            return;
        }
        if (!split[0].equals("Basic")) {
            restrict(ctx);
            return;
        }
        String[] credentials = new String(Base64.getDecoder().decode(split[1])).split(":");
        if (credentials.length != 2) {
            restrict(ctx);
            return;
        }
        if (!checker.check(credentials[0], credentials[1])) {
            restrict(ctx);
        }
    }

    private void restrict(@NotNull Context ctx) {
        ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
        ctx.cancelled = true;
        ctx.response.send("Unauthorized");
    }

    public interface AuthorizationChecker {
        boolean check(String username, String password);
    }
}
