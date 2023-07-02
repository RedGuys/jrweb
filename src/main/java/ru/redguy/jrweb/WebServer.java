package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.*;
import ru.redguy.jrweb.utils.bodyparsers.BodyParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

/**
 * Web server class.
 *
 * @author RedGuy
 */
public class WebServer {

    static {
        BodyParser.init();
    }

    private ServerSocketThread socket;
    private final WebServerOptions options;
    private boolean started = false;
    private Router rootRouter;
    private SessionStorage sessionStorage;
    private ErrorHandlers errorHandlers = new ErrorHandlers();


    public WebServer() {
        this(new WebServerOptions());
    }

    public WebServer(WebServerOptions options) {
        this.options = options;
        this.rootRouter = new Router();
        if (this.options.isEnableSessionStorage()) {
            this.sessionStorage = new SessionStorage(this);
        }
    }

    public WebServerOptions getOptions() {
        return options;
    }

    /**
     * Start server on selected port. If already started, returns false.
     * If port is 0, server will be started on random port.
     *
     * @param port Port to start server on.
     * @return true if server started, false if already started.
     * @throws IOException              if an I/O error occurs when opening the socket.
     * @throws SecurityException        if a security manager exists and its {@code checkListen}
     *                                  method doesn't allow the operation.
     * @throws IllegalArgumentException if the port parameter is outside
     *                                  the specified range of valid port values, which is between
     *                                  0 and 65535, inclusive.
     */
    public boolean start(int port) throws IOException {
        if (started) return false;
        socket = new ServerSocketThread(this, new ServerSocket(port, options.getSocketBacklog()));
        socket.start();
        checkOptions();
        started = true;
        return true;
    }

    private void checkOptions() {
        if (getOptions().getCompressor() != null && getOptions().isEnableChunkedTransfer()) {
            System.out.println("WARNING: Chunked transfer and compression enabled. This may cause problems.");
        }
        if (getOptions().getCompressor() instanceof Brotli && !BrotliUtil.isSupported()) {
            System.out.println("WARNING: Brotli compression enabled, but brotli library not found. Data will not be sent to clients.");
        }
    }

    /**
     * Stops server. If server is not started, returns false.
     *
     * @throws IOException       if an I/O error occurs when closing the socket.
     * @throws SecurityException if a security manager exists and
     *                           shutting down this ExecutorService may manipulate
     *                           threads that the caller is not permitted to modify
     *                           because it does not hold {@link
     *                           java.lang.RuntimePermission}{@code ("modifyThread")},
     *                           or the security manager's {@code checkAccess} method
     *                           denies access.
     */
    public boolean stop() throws IOException {
        if (!started) return false;
        socket.close();
        return true;
    }

    protected void processRequest(@NotNull Context context) {
        context.response.getHeaders().add(Headers.Response.SERVER, "JRWeb");
        rootRouter.processRequest("", context);

        if (!context.processed) {
            errorHandlers.on404(context);
        } else {
            context.response.finish();
        }
    }

    public Middleware addMiddleware(Middleware middleware) {
        rootRouter.add(middleware);
        return middleware;
    }

    public Page addPage(Page page) {
        rootRouter.add(page);
        return page;
    }

    public Router addRouter(Router router) {
        rootRouter.add(router);
        return router;
    }

    public Router addRouter(@NotNull Object object) {
        ru.redguy.jrweb.annotations.Router routerAnnotation = object.getClass().getAnnotation(ru.redguy.jrweb.annotations.Router.class);
        if (routerAnnotation == null) throw new IllegalArgumentException("Object must have @Router annotation");
        Router router = new Router(routerAnnotation.value());
        rootRouter.add(router);

        for (java.lang.reflect.Method method : object.getClass().getDeclaredMethods()) {
            if (method.getParameterCount() != 1) continue;
            if (method.getParameterTypes()[0].isAssignableFrom(Context.class)) {
                if (method.isAnnotationPresent(ru.redguy.jrweb.annotations.Middleware.class)) {
                    ru.redguy.jrweb.annotations.Middleware middlewareAnnotation = method.getAnnotation(ru.redguy.jrweb.annotations.Middleware.class);
                    if (middlewareAnnotation == null) continue;
                    Middleware middleware = new Middleware(Methods.getMethod(middlewareAnnotation.method())) {
                        @Override
                        public void run(Context context) throws IOException {
                            try {
                                method.invoke(object, context);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    router.add(middleware);
                }
                if (method.isAnnotationPresent(ru.redguy.jrweb.annotations.Page.class)) {
                    ru.redguy.jrweb.annotations.Page pageAnnotation = method.getAnnotation(ru.redguy.jrweb.annotations.Page.class);
                    if (pageAnnotation == null) continue;
                    Page page = new Page(Methods.getMethod(pageAnnotation.method()), pageAnnotation.value()) {
                        @Override
                        public void run(Context context) throws IOException {
                            try {
                                method.invoke(object, context);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    router.add(page);
                }
            }
        }

        return router;
    }

    public void setErrorHandlers(ErrorHandlers errorHandlers) {
        this.errorHandlers = errorHandlers;
    }

    public ErrorHandlers getErrorHandlers() {
        return errorHandlers;
    }

    protected SessionStorage getSessionStorage() {
        return sessionStorage;
    }
}
