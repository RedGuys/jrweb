package ru.redguy.jrweb.utils.compressing;

import ru.redguy.jrweb.utils.ZlibUtil;

import java.io.IOException;

public class Deflate implements Compressor {
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return ZlibUtil.compress(data);
    }

    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        return ZlibUtil.decompress(compressedData);
    }

    @Override
    public String getName() {
        return "deflate";
    }
}
