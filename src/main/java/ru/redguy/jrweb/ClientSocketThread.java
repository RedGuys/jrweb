package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;

/**
 * Client socket thread.
 * @author RedGuy
 */
public class ClientSocketThread implements Runnable {

    private final WebServer webServer;
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public ClientSocketThread(WebServer webServer, @NotNull Socket socket) throws IOException {
        this.webServer = webServer;
        this.socket = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            Context context = new Context(new Request(bufferedReader), new Response(webServer, bufferedWriter, socket.getOutputStream()));
            context.request.stream = socket.getInputStream();
            context.request.parseRequest(context, webServer);
            webServer.processRequest(context);
            if(!context.response.isHeadersSent())
                context.response.flushHeaders();
            bufferedWriter.flush();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
