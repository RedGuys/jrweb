package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibUtil {
    /**
     * Compresses original data byte array
     * @param data input bytes
     * @return compressed byte array
     * @throws IOException on unexpected error while closing {@link ByteArrayOutputStream}
     */
    public static byte @NotNull [] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * Decompresses compressed data to original byte array
     * @param compressedData compressed byte array
     * @return original bytes array
     * @throws IOException on unexpected error while closing {@link ByteArrayOutputStream}
     */
    public static byte @NotNull [] decompress(byte[] compressedData) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        outputStream.close();
        return outputStream.toByteArray();
    }
}
