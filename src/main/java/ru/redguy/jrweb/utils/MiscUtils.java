package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;

import java.util.regex.Pattern;

public class MiscUtils {
    public static String formatPath(@NotNull Context context, @NotNull String path, @NotNull Pattern pattern) {
        String fPath = context.request.url.substring(path.length() + pattern.toString().length());
        if (fPath.startsWith("/") || fPath.startsWith("\\")) fPath = fPath.substring(1);
        return fPath;
    }
}
