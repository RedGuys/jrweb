package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.*;
import ru.redguy.jrweb.utils.bodyparsers.BodyParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.ServerSocketChannel;

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

    /**
     * Constructs a new WebServer with default options.
     */
    public WebServer() {
        this(new WebServerOptions());
    }

    /**
     * Constructs a new WebServer with selected options.
     * @param options instance of WebServerOptions.
     */
    public WebServer(WebServerOptions options) {
        this.options = options;
        this.rootRouter = new Router();
        if (this.options.isEnableSessionStorage()) {
            this.sessionStorage = new SessionStorage(this);
        }
    }

    /**
     * @return current server options.
     */
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
    public boolean start(int port) throws IOException, SecurityException, IllegalArgumentException {
        if (started) return false;
        checkOptions();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(port), options.getSocketBacklog());
        socket = new ServerSocketThread(this, channel);
        socket.start();
        started = true;
        return true;
    }

    /**
     * Internal check of options.
     */
    private void checkOptions() {
        if (getOptions().getCompressor() != null && getOptions().isEnableChunkedTransfer()) {
            System.out.println("WARNING: Chunked transfer and compression enabled. This may cause problems.");
        }
        if (getOptions().getCompressor() instanceof Brotli && !BrotliUtil.isSupported()) {
            System.out.println("WARNING: Brotli compression enabled, but brotli library not found. Falling back to gzip.");
            getOptions().enableGzipCompression();
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
    public boolean stop() throws IOException, SecurityException {
        if (!started) return false;
        socket.close();
        return true;
    }

    /**
     * Processes request and sends response. If request is not processed, sends 404 error.
     * @param context context of request.
     */
    protected void processRequest(@NotNull Context context) {
        context.response.getHeaders().add(Headers.Response.SERVER, "JRWeb");
        rootRouter.processRequest("", context);

        if (!context.processed) {
            errorHandlers.on404(context);
        } else {
            context.response.finish();
        }
    }

    /**
     * Adds {@link Middleware} to root {@link Router}.
     * @param middleware {@link Middleware} to add.
     * @return added {@link Middleware}.
     */
    public Middleware addMiddleware(Middleware middleware) {
        rootRouter.add(middleware);
        return middleware;
    }

    /**
     * Adds {@link Page} to root {@link Router}.
     * @param page {@link Page} to add.
     * @return added {@link Page}.
     */
    public Page addPage(Page page) {
        rootRouter.add(page);
        return page;
    }

    /**
     * Adds sub-{@link Router} to root {@link Router}.
     * @param router {@link Router} to add.
     * @return added {@link Router}.
     */
    public Router addRouter(Router router) {
        rootRouter.add(router);
        return router;
    }

    /**
     * Creates new {@link Router} from object and adds it to root {@link Router}. Object must have @{@link Router} annotation. Methods must have @{@link Page} or @{@link Middleware} annotation.
     * @param object object to create {@link Router} from.
     * @return created {@link Router}.
     */
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

    /**
     * Sets {@link ErrorHandlers}, that will be called on error.
     * @param errorHandlers error handlers.
     */
    public void setErrorHandlers(ErrorHandlers errorHandlers) {
        this.errorHandlers = errorHandlers;
    }

    /**
     * @return current {@link ErrorHandlers}.
     */
    public ErrorHandlers getErrorHandlers() {
        return errorHandlers;
    }

    /**
     * @return current {@link SessionStorage}.
     */
    protected SessionStorage getSessionStorage() {
        return sessionStorage;
    }
}
