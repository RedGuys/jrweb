package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;

public class BearerAuthorizationMiddleware extends AuthorizationMiddleware {

    private final AuthorizationChecker checker;

    /**
     * Bearer authorization middleware.
     *
     * @param checker checker realization.
     */
    public BearerAuthorizationMiddleware(AuthorizationChecker checker) {
        this(null, checker);
    }

    /**
     * Bearer authorization middleware.
     *
     * @param method  http method to check.
     * @param checker checker realization.
     */
    public BearerAuthorizationMiddleware(Method method, AuthorizationChecker checker) {
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
        if (!authorizationData.type.equals("Bearer")) {
            restrict(ctx);
            return;
        }
        if (!checker.check(authorizationData.payload)) {
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
         * @param token token to check.
         * @return true if credentials are valid.
         */
        boolean check(String token);
    }
}
