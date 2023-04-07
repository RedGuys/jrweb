package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.Router;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourcesRouter extends Router {

    protected String contentRoot;

    public ResourcesRouter(String pattern, String contentRoot) {
        super(pattern);
        this.contentRoot = contentRoot;
    }

    @Override
    public void processRequest(@NotNull String path, @NotNull Context context) {
        if (!this.pattern.matcher(context.request.url.substring(path.length())).find() && !Objects.equals(pattern.toString(), ""))
            return;

        String fPath = context.request.url.substring(path.length() + pattern.toString().length());
        if (fPath.startsWith("/") || fPath.startsWith("\\")) fPath = fPath.substring(1);

        InputStream is = getClass().getClassLoader().getResourceAsStream(fPath);
        if (is != null) {
            context.response.setStatusCode(StatusCodes.OK);
            try {
                byte[] bytes = readAllBytes(is);
                context.response.getHeaders().add(Headers.Response.CONTENT_LENGTH, String.valueOf(bytes.length));
                context.response.send(bytes);
            } catch (SecurityException e) {
                context.response.setStatusCode(StatusCodes.FORBIDDEN);
                context.response.send("Forbidden");
            } catch (IOException e) {
                context.response.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                context.response.getHeaders().remove(Headers.Response.CONTENT_LENGTH);
                context.response.send("Internal Server Error");
            }
        } else {
            context.response.setStatusCode(StatusCodes.NOT_FOUND);
            context.response.send("Not Found");
        }
        context.processed = true;
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
