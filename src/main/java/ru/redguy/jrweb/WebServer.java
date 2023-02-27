package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Web server class.
 * @author RedGuy
 */
public class WebServer {
    private ServerSocketThread socket;
    private final WebServerOptions options;
    private boolean started = false;
    private Router rootRouter;


    public WebServer() {
        this(new WebServerOptions());
    }

    public WebServer(WebServerOptions options) {
        this.options = options;
        this.rootRouter = new Router();
    }

    public WebServerOptions getOptions() {
        return options;
    }

    /**
     * Start server on selected port. If already started, returns false.
     * If port is 0, server will be started on random port.
     * @param port Port to start server on.
     * @return true if server started, false if already started.
     * @exception  IOException  if an I/O error occurs when opening the socket.
     * @exception  SecurityException
     * if a security manager exists and its {@code checkListen}
     * method doesn't allow the operation.
     * @exception  IllegalArgumentException if the port parameter is outside
     *             the specified range of valid port values, which is between
     *             0 and 65535, inclusive.
     */
    public boolean start(int port) throws IOException {
        if(started) return false;
        socket = new ServerSocketThread(this,new ServerSocket(port, options.getSocketBacklog()));
        socket.start();
        started = true;
        return true;
    }

    /**
     * Stops server. If server is not started, returns false.
     * @exception  IOException  if an I/O error occurs when closing the socket.
     * @throws SecurityException if a security manager exists and
     *         shutting down this ExecutorService may manipulate
     *         threads that the caller is not permitted to modify
     *         because it does not hold {@link
     *         java.lang.RuntimePermission}{@code ("modifyThread")},
     *         or the security manager's {@code checkAccess} method
     *         denies access.
     */
    public boolean stop() throws IOException {
        if(!started) return false;
        socket.close();
        return true;
    }

    protected void processRequest(Context context) {
        rootRouter.processRequest(context);
    }

    public Middleware addMiddleware(Middleware middleware) {
        rootRouter.add(middleware);
        return middleware;
    }

    public Middleware addMiddleware(ContextRunner runner) {
        Middleware middleware = new Middleware(runner);
        rootRouter.add(middleware);
        return middleware;
    }

    public Page addPage(Page page) {
        rootRouter.add(page);
        return page;
    }
}
