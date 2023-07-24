package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.DataOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Client socket thread.
 * @author RedGuy
 */
public class ClientSocketThread implements Runnable {

    private final WebServer webServer;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final DataOutputStream outputStream;

    public ClientSocketThread(WebServer webServer, @NotNull Socket socket) throws IOException {
        this.webServer = webServer;
        this.socket = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            Context context = new Context(webServer, socket, bufferedReader, outputStream);
            context.request.parseRequest();
            webServer.processRequest(context);
            if(!context.response.isHeadersSent())
                context.response.flushHeaders();
            outputStream.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
