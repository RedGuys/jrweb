package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileRouter extends Router {

    protected Path contentRoot;

    /**
     * File router.
     *
     * @param pattern     path pattern.
     * @param contentRoot root directory.
     */
    public FileRouter(String pattern, Path contentRoot) {
        super(pattern);
        this.contentRoot = contentRoot;
    }

    @Override
    public void processRequest(@NotNull String path, @NotNull Context context) {
        if (!this.pattern.matcher(context.request.url.substring(path.length())).find() && !Objects.equals(pattern.toString(), ""))
            return;

        String fPath = MiscUtils.formatPath(context, path, pattern);
        File filePath = contentRoot.resolve(fPath).toFile();
        if (!filePath.exists()) {
            context.response.setStatusCode(StatusCodes.NOT_FOUND);
            context.response.send("Not Found!");
            context.processed = true;
            return;
        }

        if (filePath.isDirectory()) {
            context.response.setStatusCode(StatusCodes.OK);
            context.response.send(generateFolderIndex(context, filePath));
        } else {
            sendFile(context, filePath);
        }

        context.processed = true;
    }

    public String generateFolderIndex(Context context, @NotNull File directory) {
        return Arrays.stream(Objects.requireNonNull(directory.list())).map(s -> "<a href=\"" + context.request.url + "/" + s + "\">" + s + "</a><br>").collect(Collectors.joining("\n"));
    }

    public void sendFile(Context context, @NotNull File file) {
        if (file.canRead()) {
            context.response.setStatusCode(StatusCodes.OK);
            context.response.getHeaders().add(Headers.Common.CONTENT_LENGTH, String.valueOf(file.length()));
            try {
                context.response.send(Files.readAllBytes(file.toPath()));
            } catch (SecurityException e) {
                context.response.setStatusCode(StatusCodes.FORBIDDEN);
                context.response.send("Forbidden");
            } catch (IOException e) {
                context.response.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                context.response.getHeaders().remove(Headers.Common.CONTENT_LENGTH);
                context.response.send("Internal Server Error");
            }
        } else {
            context.response.setStatusCode(StatusCodes.FORBIDDEN);
            context.response.send("Forbidden");
        }
    }
}
