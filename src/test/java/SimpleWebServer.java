import ru.redguy.jrweb.WebServer;

import java.io.IOException;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer();
        server.start(80);
    }
}
