package ru.redguy.jrweb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server socket thread.
 * @author RedGuy
 */
public class ServerSocketThread extends Thread {
    private final WebServer webServer;
    private final ServerSocket socket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    public boolean closing = false;

    public ServerSocketThread(WebServer webServer, ServerSocket socket) {
        this.webServer = webServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!closing) {
            try {
                executorService.submit(new ClientSocketThread(webServer,socket.accept()));
            } catch (SocketException e) {
                break;
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void close() throws IOException {
        socket.close();
        executorService.shutdown();
    }
}
