package ru.redguy.jrweb.utils;

import java.io.IOException;

@FunctionalInterface
public interface ContextRunner {
    public void run(Context context) throws IOException;
}
