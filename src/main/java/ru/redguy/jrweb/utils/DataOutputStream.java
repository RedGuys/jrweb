package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataOutputStream {
    private final OutputStream stream;

    public DataOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public void write(@NotNull String value) throws IOException {
        stream.write(value.getBytes(StandardCharsets.UTF_8));
    }

    public void write(@NotNull String value, Charset charset) throws IOException {
        stream.write(value.getBytes(charset));
    }

    public void write(byte[] bytes) throws IOException {
        stream.write(bytes);
    }

    public void flush() throws IOException {
        stream.flush();
    }
}
