import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.Cookie;
import ru.redguy.jrweb.WebServer;
import ru.redguy.jrweb.WebServerOptions;
import ru.redguy.jrweb.presets.*;
import ru.redguy.jrweb.presets.websocket.DataFrame;
import ru.redguy.jrweb.presets.websocket.WebSocket;
import ru.redguy.jrweb.presets.websocket.WebSocketConnection;
import ru.redguy.jrweb.utils.*;
import ru.redguy.jrweb.utils.bodyparsers.BytesBody;
import ru.redguy.jrweb.utils.bodyparsers.URLEncodedBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        WebServer server = new WebServer(new WebServerOptions().showExceptions().enableSessionStorage()/*.enableChunkedTransfer()/*.enableGzipCompression()*/);
        server.start(80);

        server.addMiddleware(new ProcessTimeLoggingMiddleware.Pre());
        server.addMiddleware(new ProcessTimeLoggingMiddleware.Post());

        server.addPage(new Page("/") {
            @Override
            public void run(Context context) {
                context.response.setStatusCode(StatusCodes.OK);
                context.response.send("<html><body>Hello World!<br><a href=\"/redirect\">Redirect</a></body></html>");
            }
        });

        server.addPage(new Page("/redirect") {
            @Override
            public void run(Context context) throws IOException {
                context.response.setStatusCode(StatusCodes.MOVED_PERMANENTLY("https://google.com"));
            }
        });

        server.addPage(new Page(Methods.POST, "/post-only") {
            @Override
            public void run(Context context) throws IOException {
                context.response.setStatusCode(StatusCodes.OK);
                context.response.send("<html><body>POST ONLY</body></html>");
            }
        });

        Router router = server.addRouter(new Router("/route"));
        router.add(new Page("/test") {
            @Override
            public void run(Context context) throws IOException {
                context.response.setStatusCode(StatusCodes.OK);
                context.response.send("<html><body>Route test</body></html>");
            }
        });

        server.addPage(new Page(Methods.GET, "/file") {
            @Override
            public void run(Context context) throws IOException {
                context.response.setStatusCode(StatusCodes.OK);
                InputStream is = SimpleWebServer.class.getResourceAsStream("rick.webp");
                byte[] bytes = readAllBytes(is);
                context.response.getHeaders().add(Headers.Common.CONTENT_LENGTH, String.valueOf(bytes.length));
                context.response.getHeaders().add(Headers.Common.CONTENT_TYPE, "image/webp");
                context.response.send(bytes);
            }
        });

        server.addRouter(new FileRouter("/src", Paths.get("src")));

        server.addRouter(new RouterClass());

        server.addRouter(new ResourcesRouter("/resources", "/"));

        server.addPage(new Page("/headers") {
            @Override
            public void run(Context context) throws IOException {
                context.response.send("<html><body>Headers:<br>");
                context.request.headers.forEach((e) -> {
                    context.response.send(e.generate() + ": " + e.getValue() + "<br>");
                });
                context.response.send("</body></html>");
            }
        });

        server.addPage(new Page("/cookies") {
            @Override
            public void run(Context context) throws IOException {
                context.cookies.setCookie("test", UUID.randomUUID().toString());

                for (Cookie cookie : context.cookies.getCookies()) {
                    context.response.send(cookie.getName() + " - " + cookie.getValue() + "<br>");
                }
            }
        });

        server.addPage(new WebSocket("/ws") {
            @Override
            public void onMessage(Context ctx, DataFrame frame) {
                try {
                    send(ctx, frame.getPayloadText());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        server.addMiddleware(new BasicAuthorizationMiddleware((username, password) -> username.equals("admin") && password.equals("admin"))
                .setRegex("/password"));
        server.addPage(new Page("/password") {
            @Override
            public void run(Context context) throws IOException {
                context.response.send("<html><body>Authorized</body></html>");
            }
        });

        server.addPage(new Page("/count") {
            @Override
            public void run(Context context) throws IOException {
                context.response.send(String.valueOf(context.session.get(Counter.class).increment()));
            }
        });

        server.addPage(new Page("/error") {
            @Override
            public void run(Context context) throws IOException {
                throw new RuntimeException("Test error");
            }
        });

        server.addPage(new WebSocket("/web") {
            @Override
            public void onOpen(WebSocketConnection connection) {
                new Thread(() -> {
                    try {
                        DataFrame read = connection.awaitRead();
                        connection.write(read.getPayloadText());
                        connection.close(1000, "Only one packet");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        });

        server.addPage(new Page(Methods.POST,"/body") {
            @Override
            public void run(Context context) throws Exception {
                if(context.request.body instanceof BytesBody) {
                    if(context.request.headers.has(Headers.Common.CONTENT_TYPE)) {
                        context.response.getHeaders().add(Headers.Common.CONTENT_TYPE, context.request.headers.getFirst(Headers.Common.CONTENT_TYPE).getValue());
                    }
                    BytesBody body = (BytesBody) context.request.body;
                    context.response.send(body.bytes);
                } else if(context.request.body instanceof URLEncodedBody) {
                    URLEncodedBody body = (URLEncodedBody) context.request.body;
                    context.response.send(body.parameters.keySet().toString());
                }
            }
        });
    }

    public static class Counter extends SessionData {
        private int count = 0;

        public int increment() {
            return ++count;
        }
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
