package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.AsynchronousSocketReader;
import ru.redguy.jrweb.utils.DataOutputStream;
import ru.redguy.jrweb.utils.Session;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public class Context {
    public final WebServer server;
    public final AsynchronousSocketChannel socket;
    public final AsynchronousSocketReader reader;
    public final DataOutputStream outputStream;
    public final Request request;
    public final Response response;
    public final Cookies cookies = new Cookies(this);
    public Session session;

    public Context(WebServer server, AsynchronousSocketChannel socket, AsynchronousSocketReader reader, DataOutputStream outputStream) throws IOException {
        this.server = server;
        this.socket = socket;
        this.reader = reader;
        this.outputStream = outputStream;

        this.request = new Request(this);
        this.response = new Response(this);
    }

    public boolean cancelled = false;
    public boolean processed = false;

    /**
     * Redirect to url with 302 status code.
     * @param url url to redirect.
     */
    public void redirect(@NotNull String url) {
        response.setStatusCode(StatusCodes.FOUND(url));
        processed = true;
    }
}
