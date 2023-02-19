package ru.redguy.jrweb;

import sun.nio.ch.ThreadPool;

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
    private final ServerSocket socket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    public boolean closing = false;

    public ServerSocketThread(ServerSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!closing) {
            try {
                executorService.submit(new ClientSocketThread(socket.accept()));
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
