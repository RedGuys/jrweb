import ru.redguy.jrweb.WebServer;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer();
        server.start(80);

        server.addPage(new Page("/",(ctx) -> {
            ctx.response.setStatusCode(StatusCodes.OK);
            ctx.response.send("<html><body>Hello World!<br><a href=\"/redirect\">Redirect</a></body></html>");
        }));

        server.addPage(new Page("/redirect",(ctx) -> {
            ctx.response.setStatusCode(StatusCodes.MOVED_PERMANENTLY("https://google.com"));
        }));

        server.addPage(new Page(Methods.POST,"/post-only",(ctx) -> {
            ctx.response.setStatusCode(StatusCodes.OK);
            ctx.response.send("<html><body>POST ONLY</body></html>");
        }));
    }
}
