package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.AsynchronousSocketReader;
import ru.redguy.jrweb.utils.DataOutputStream;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Client socket thread.
 * @author RedGuy
 */
public class ClientSocketThread implements Runnable {

    private final WebServer webServer;
    private final AsynchronousSocketChannel socket;
    private final AsynchronousSocketReader bufferedReader;
    private final DataOutputStream outputStream;

    public ClientSocketThread(WebServer webServer, @NotNull SocketChannel socket) throws IOException {
        this.webServer = webServer;
        this.socket = socket;
        bufferedReader = new AsynchronousSocketReader(socket);
        outputStream = new DataOutputStream(socket);
    }

    @Override
    public void run() {
        try {
            Context context = new Context(webServer, socket, bufferedReader, outputStream);
            context.request.parseRequest();
            webServer.processRequest(context);
            if(!context.response.isHeadersSent())
                context.response.flushHeaders();
            context.outputStream.waitLock();
            //socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
