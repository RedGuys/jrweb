import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Cookie;
import ru.redguy.jrweb.WebServer;
import ru.redguy.jrweb.WebServerOptions;
import ru.redguy.jrweb.presets.FileRouter;
import ru.redguy.jrweb.presets.ResourcesRouter;
import ru.redguy.jrweb.presets.WebSocket;
import ru.redguy.jrweb.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer(new WebServerOptions()/*.enableChunkedTransfer()*/.enableBrotliCompression());
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
            InputStream is = SimpleWebServer.class.getResourceAsStream("rick.webp");
            byte[] bytes = readAllBytes(is);
            ctx.response.getHeaders().add(Headers.Response.CONTENT_LENGTH, String.valueOf(bytes.length));
            ctx.response.send(bytes);
        }));

        server.addRouter(new FileRouter("/src", Paths.get("src")));

        server.addRouter(new RouterClass());

        server.addRouter(new ResourcesRouter("/resources", "/"));

        server.addPage(new Page("/headers",(ctx) -> {
            ctx.response.send("<html><body>Headers:<br>");
            ctx.request.headers.forEach((e) -> {
                ctx.response.send(e.generate() + ": " + e.getValue() + "<br>");
            });
            ctx.response.send("</body></html>");
        }));

        server.addPage(new Page("/cookies",(ctx) -> {
            ctx.cookies.setCookie("test", UUID.randomUUID().toString());

            for (Cookie cookie : ctx.cookies.getCookies()) {
                ctx.response.send(cookie.getName() + " - " + cookie.getValue() + "<br>");
            }
        }));

        server.addPage(new Page("/ws", new WebSocket() {
            @Override
            public void onMessage(Context ctx, DataFrame frame) {
                try {
                    send(ctx, frame.getPayloadText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    public static byte @NotNull [] readAllBytes(@NotNull InputStream inputStream) throws IOException {
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
