package ru.redguy.jrweb;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.*;
import java.net.Socket;

/**
 * Client socket thread.
 * @author RedGuy
 */
public class ClientSocketThread implements Runnable {

    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public ClientSocketThread(@NotNull Socket socket) throws IOException {
        this.socket = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while (bufferedReader.ready()) {
                System.out.println(bufferedReader.readLine());
            }
            bufferedWriter.write("HTTP/1.1");
            bufferedWriter.newLine();
            bufferedWriter.write(StatusCodes.OK.generate());
            bufferedWriter.newLine();
            bufferedWriter.write("lyl");
            bufferedWriter.flush();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
