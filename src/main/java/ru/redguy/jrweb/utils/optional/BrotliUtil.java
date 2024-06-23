package ru.redguy.jrweb.utils.optional;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BrotliUtil {
    public static byte @NotNull [] compress(byte[] data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BrotliOutputStream brotliOutputStream = new BrotliOutputStream(outputStream);
        brotliOutputStream.write(data);
        brotliOutputStream.flush();
        brotliOutputStream.close();
        return outputStream.toByteArray();
    }

    public static byte @NotNull [] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
        BrotliInputStream brotliInputStream = new BrotliInputStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = brotliInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        brotliInputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

    public static boolean isSupported() {
        try {
            Class.forName("com.aayushatharva.brotli4j.decoder.BrotliInputStream");
            Class.forName("com.aayushatharva.brotli4j.encoder.BrotliOutputStream");
            Class.forName("com.aayushatharva.brotli4j.Brotli4jLoader");

            Brotli4jLoader.ensureAvailability();
            return true;
        } catch (ClassNotFoundException | UnsatisfiedLinkError e) {
            return false;
        }
    }
}
