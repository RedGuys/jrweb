package ru.redguy.jrweb.presets.websocket;

import org.jetbrains.annotations.Nullable;
import ru.redguy.jrweb.Context;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketConnection {
    private final WebSocket server;
    private final Context context;
    protected boolean opened;
    protected LinkedBlockingQueue<DataFrame> dataFrames = new LinkedBlockingQueue<>();

    public WebSocketConnection(WebSocket server, Context context) {
        this.server = server;
        this.context = context;
    }

    public @Nullable DataFrame read() {
        return dataFrames.poll();
    }

    public DataFrame awaitRead() throws InterruptedException {
        return dataFrames.take();
    }

    public void write(String data) throws Exception {
        WebSocket.send(context, data);
    }

    public void close() throws Exception {
        WebSocket.close(context);
    }

    public void close(int statusCode) throws Exception {
        WebSocket.close(context, statusCode);
    }

    public void close(int statusCode, String reason) throws Exception {
        WebSocket.close(context, statusCode, reason);
    }

    public WebSocket getServer() {
        return server;
    }
}
