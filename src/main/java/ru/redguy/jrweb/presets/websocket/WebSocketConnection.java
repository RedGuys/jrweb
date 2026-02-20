package ru.redguy.jrweb.presets.websocket;

import org.jetbrains.annotations.Nullable;
import ru.redguy.jrweb.Context;

import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketConnection {
    private final WebSocket server;
    private final Context context;
    protected volatile boolean opened;
    private final LinkedBlockingQueue<DataFrame> dataFrames;
    private static final int DEFAULT_QUEUE_SIZE = 1000;

    public WebSocketConnection(WebSocket server, Context context) {
        this(server, context, DEFAULT_QUEUE_SIZE);
    }

    public WebSocketConnection(WebSocket server, Context context, int queueSize) {
        this.server = server;
        this.context = context;
        this.dataFrames = new LinkedBlockingQueue<>(queueSize);
    }

    public @Nullable DataFrame read() {
        return dataFrames.poll();
    }

    public DataFrame awaitRead() throws InterruptedException {
        return dataFrames.take();
    }

    public void write(String data) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connection is closed");
        }
        WebSocket.send(context, data);
    }

    public void write(byte[] data) throws Exception {
        if (!isOpen()) {
            throw new IllegalStateException("Connection is closed");
        }
        WebSocket.send(context, data);
    }

    public void close() throws Exception {
        close(1000);
    }

    public void close(int statusCode) throws Exception {
        close(statusCode, "");
    }

    public void close(int statusCode, String reason) throws Exception {
        opened = false;
        WebSocket.close(context, statusCode, reason);
    }

    public WebSocket getServer() {
        return server;
    }

    public boolean isOpen() {
        return opened && context.socket.isOpen();
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public LinkedBlockingQueue<DataFrame> getDataFrames() {
        return dataFrames;
    }

    protected void cleanup() {
        dataFrames.clear();
        opened = false;
    }
}
