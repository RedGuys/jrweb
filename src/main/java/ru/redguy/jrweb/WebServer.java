package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.annotations.Param;
import ru.redguy.jrweb.utils.*;
import ru.redguy.jrweb.utils.bodyparsers.JsonBody;
import ru.redguy.jrweb.utils.compressing.Brotli;
import ru.redguy.jrweb.utils.optional.BrotliUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.List;
import java.util.Map;

/**
 * Web server class.
 *
 * @author RedGuy
 */
public class WebServer {
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
     *
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
        AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open();
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
     *
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
     *
     * @param middleware {@link Middleware} to add.
     * @return added {@link Middleware}.
     */
    public Middleware addMiddleware(Middleware middleware) {
        rootRouter.add(middleware);
        return middleware;
    }

    /**
     * Adds {@link Page} to root {@link Router}.
     *
     * @param page {@link Page} to add.
     * @return added {@link Page}.
     */
    public Page addPage(Page page) {
        rootRouter.add(page);
        return page;
    }

    /**
     * Adds sub-{@link Router} to root {@link Router}.
     *
     * @param router {@link Router} to add.
     * @return added {@link Router}.
     */
    public Router addRouter(Router router) {
        rootRouter.add(router);
        return router;
    }

    /**
     * Creates new {@link Router} from object and adds it to root {@link Router}. Object must have @{@link Router} annotation. Methods must have @{@link Page} or @{@link Middleware} annotation.
     *
     * @param object object to create {@link Router} from.
     * @return created {@link Router}.
     */
    public Router addRouter(@NotNull Object object) {
        ru.redguy.jrweb.annotations.Router routerAnnotation = object.getClass().getAnnotation(ru.redguy.jrweb.annotations.Router.class);
        if (routerAnnotation == null) throw new IllegalArgumentException("Object must have @Router annotation");
        Router router = new Router(routerAnnotation.value());
        rootRouter.add(router);

        for (java.lang.reflect.Method method : object.getClass().getDeclaredMethods()) {
            if (method.getParameterCount() < 1) continue;
            if (!method.getParameterTypes()[0].isAssignableFrom(Context.class)) continue;
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
                Method httpMethod = Methods.getMethod(pageAnnotation.method());
                Page page = new Page(httpMethod, pageAnnotation.value()) {

                    @Override
                    public void processRequest(String path, @NotNull Context context) throws Exception {
                        if (!this.getRegex().matcher(path).matches() || (this.getMethod() != null && !this.getMethod().equals(context.request.method))) {
                            return;
                        }
                        if (method.getParameterCount() > 1) {
                            if (this.getMethod().hasBody()) {
                                if (context.request.body instanceof JsonBody) {
                                    JsonBody body = (JsonBody) context.request.body;
                                    for (int i = 1; i < method.getParameterCount(); i++) {
                                        Parameter parameter = method.getParameters()[i];
                                        if (!parameter.isAnnotationPresent(Param.class)) return;
                                        Param param = parameter.getAnnotation(Param.class);
                                        if (!body.has(param.value()) && param.required()) {
                                            return;
                                        }
                                    }
                                }
                            } else {
                                top:
                                for (int i = 1; i < method.getParameterCount(); i++) {
                                    Parameter parameter = method.getParameters()[i];
                                    if (!parameter.isAnnotationPresent(Param.class)) return;
                                    Param param = parameter.getAnnotation(Param.class);
                                    if (!context.request.query.containsKey(param.value()) && param.required()) {
                                        return;
                                    }
                                    Class<?> type = method.getParameterTypes()[i];
                                    if (type.isAssignableFrom(String.class)) {
                                        continue;
                                    }
                                    try {
                                        type.getConstructor(String.class);
                                        continue;
                                    } catch (NoSuchMethodException | SecurityException ignored) {
                                    }
                                    try {
                                        for (java.lang.reflect.Method method : type.getMethods()) {
                                            if (method.getReturnType().isAssignableFrom(type) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class) && Modifier.isStatic(method.getModifiers())) {
                                                continue top;
                                            }
                                        }
                                    } catch (SecurityException ignored) {
                                    }
                                    if(param.required())
                                        return;
                                }
                            }
                        }
                        run(context);
                        context.processed = true;
                    }

                    @Override
                    public void run(Context context) throws Exception {
                        Object result = null;
                        if (method.getParameterCount() == 1) {
                            result = method.invoke(object, context);
                        } else {
                            Object[] args = new Object[method.getParameterCount()];
                            args[0] = context;
                            if (httpMethod.hasBody()) {
                                if (context.request.body instanceof JsonBody) {
                                    JsonBody body = (JsonBody) context.request.body;
                                    for (int i = 1; i < method.getParameterCount(); i++) {
                                        args[i] = body.get(method.getParameters()[i].getAnnotation(Param.class).value(), method.getParameterTypes()[i]);
                                    }
                                }
                            } else {
                                top:
                                for (int i = 1; i < method.getParameterTypes().length; i++) {
                                    Class<?> type = method.getParameterTypes()[i];
                                    String name = method.getParameters()[i].getAnnotation(Param.class).value();
                                    if (type.isAssignableFrom(String.class)) {
                                        args[i] = context.request.query.get(name);
                                        continue;
                                    } else {
                                        try {
                                            Constructor<?> constructor = type.getConstructor(String.class);
                                            args[i] = constructor.newInstance(context.request.query.get(name));
                                            continue;
                                        } catch (NoSuchMethodException | IllegalAccessException |
                                                 InstantiationException | InvocationTargetException ignored) {
                                        }
                                        try {
                                            for (java.lang.reflect.Method m : type.getMethods()) {
                                                if (m.getReturnType().isAssignableFrom(type) && m.getParameterCount() == 1 && m.getParameterTypes()[0].isAssignableFrom(String.class) && Modifier.isStatic(m.getModifiers())) {
                                                    args[i] = m.invoke(null, context.request.query.get(name));
                                                    continue top;
                                                }
                                            }
                                        } catch (IllegalAccessException | InvocationTargetException ignored) {
                                        }
                                    }
                                    args[i] = null;
                                }
                            }
                            result = method.invoke(object, args);
                        }
                        if(result != null) {
                            context.response.send(result);
                        }
                    }
                };
                router.add(page);
            }
        }

        return router;
    }

    /**
     * Collects all pages from root {@link Router}.
     *
     * @return
     */
    public List<Map.Entry<String, Page>> getPages() {
        return rootRouter.getPages();
    }

    /**
     * Sets {@link ErrorHandlers}, that will be called on error.
     *
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
