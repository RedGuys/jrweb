import ru.redguy.jrweb.WebServer;
import ru.redguy.jrweb.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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

        Router router = server.addRouter(new Router("/route"));
        router.add(new Page("/test",(ctx) -> {
            ctx.response.setStatusCode(StatusCodes.OK);
            ctx.response.send("<html><body>Route test</body></html>");
        }));

        server.addPage(new Page(Methods.GET, "/file", (ctx) -> {
            ctx.response.setStatusCode(StatusCodes.OK);
            ctx.response.getHeaders().add(Headers.Response.RETRY_AFTER, "10");
            InputStream is = SimpleWebServer.class.getResourceAsStream("rick.webp");
            byte[] bytes = readAllBytes(is);
            ctx.response.send(bytes);
        }));

        server.addRouter(new RouterClass());
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
