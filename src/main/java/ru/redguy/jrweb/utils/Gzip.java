package ru.redguy.jrweb.utils;

import java.io.IOException;

public class Gzip implements Compressor{

    @Override
    public byte[] compress(byte[] data) throws IOException {
        return GzipUtil.compress(data);
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        return GzipUtil.decompress(compressedData);
    }

    @Override
    public String getName() {
        return "gzip";
    }
}
