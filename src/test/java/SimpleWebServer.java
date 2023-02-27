import ru.redguy.jrweb.WebServer;
import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.Page;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.IOException;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer();
        server.start(80);

        server.addPage(new Page("/",(ctx) -> ctx.response.setStatusCode(StatusCodes.TEMPORARY_REDIRECT("https://google.com"))));
    }
}
