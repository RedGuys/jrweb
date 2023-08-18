import java.io.*;
import java.net.*;

public class NetCat {
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Netcat <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(host, port);

            // Create threads for handling input and output concurrently
            Thread inputThread = createInputThread(socket);
            Thread outputThread = createOutputThread(socket);

            // Start the threads
            inputThread.start();
            outputThread.start();

            // Wait for both threads to finish
            inputThread.join();
            outputThread.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Thread createInputThread(Socket socket) {
        return new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Thread createOutputThread(Socket socket) {
        return new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
