package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.util.Base64;

public class BasicAuthorizationMiddleware extends AuthorizationMiddleware {

    private final AuthorizationChecker checker;

    /**
     * Basic authorization middleware.
     *
     * @param checker checker realization.
     */
    public BasicAuthorizationMiddleware(AuthorizationChecker checker) {
        this(null, checker);
    }

    /**
     * Basic authorization middleware.
     *
     * @param method  http method to check.
     * @param checker checker realization.
     */
    public BasicAuthorizationMiddleware(Method method, AuthorizationChecker checker) {
        super(method);
        this.checker = checker;
    }

    @Override
    public void run(Context ctx) throws IOException {
        AuthorizationData authorizationData = getAuthorizationData(ctx);
        if (authorizationData == null) {
            restrict(ctx);
            return;
        }
        if (!authorizationData.type.equals("Basic")) {
            restrict(ctx);
            return;
        }
        String[] credentials = new String(Base64.getDecoder().decode(authorizationData.payload)).split(":");
        if (credentials.length != 2) {
            restrict(ctx);
            return;
        }
        if (!checker.check(credentials[0], credentials[1])) {
            restrict(ctx);
        }
    }

    /**
     * Restrict access.
     */
    private void restrict(@NotNull Context ctx) {
        ctx.response.setStatusCode(StatusCodes.UNAUTHORIZED);
        ctx.cancelled = true;
        ctx.response.send("Unauthorized");
    }

    /**
     * Authorization checker interface.
     */
    public interface AuthorizationChecker {
        /**
         * Check credentials.
         *
         * @param username username.
         * @param password password.
         * @return true if credentials are valid.
         */
        boolean check(String username, String password);
    }
}
