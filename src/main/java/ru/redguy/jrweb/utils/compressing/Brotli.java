package ru.redguy.jrweb.utils.compressing;

import ru.redguy.jrweb.utils.optional.BrotliUtil;

import java.io.IOException;

public class Brotli implements Compressor {

    @Override
    public byte[] compress(byte[] data) throws IOException {
        //check if brotli is supported
        if(!BrotliUtil.isSupported()) {
            throw new IOException("Brotli is not supported");
        }
        return BrotliUtil.compress(data);
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        return BrotliUtil.decompress(compressedData);
    }

    @Override
    public String getName() {
        return "br";
    }
}
