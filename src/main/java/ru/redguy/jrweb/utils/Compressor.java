package ru.redguy.jrweb.utils;

import java.io.IOException;

public interface Compressor {
    byte[] compress(byte[] data) throws IOException;
    byte[] decompress(byte[] compressedData) throws IOException;
    String getName();
}
