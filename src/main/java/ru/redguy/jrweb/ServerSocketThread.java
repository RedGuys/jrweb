package ru.redguy.jrweb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * Server socket thread.
 * @author RedGuy
 */
public class ServerSocketThread extends Thread {
    private final WebServer webServer;
    private final AsynchronousServerSocketChannel socket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    public boolean closing = false;

    public ServerSocketThread(WebServer webServer, AsynchronousServerSocketChannel socket) {
        this.webServer = webServer;
        this.socket = socket;
        this.setName("ServerSocketThread");
    }

    /**
     * Accepts new connections and creates new ClientSocketThread for each connection.
     */
    @Override
    public void run() {
        while (!closing) {
            try {
                AsynchronousSocketChannel a = socket.accept().get();
                ClientSocketThread clientSocketThread = new ClientSocketThread(webServer, a);
                executorService.submit(clientSocketThread);
            } catch (SocketException | ExecutionException ignored) {
            } catch (IOException e) {
                System.out.println(e);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void close() throws IOException {
        socket.close();
        executorService.shutdown();
    }
}
