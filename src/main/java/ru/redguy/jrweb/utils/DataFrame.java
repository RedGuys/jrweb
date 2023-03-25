package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DataFrame {

    public enum PacketType {
        TEXT,
        BINARY,
        CLOSE
    }

    private static final int MASK_BIT = 0x80;
    private static final int PAYLOAD_LENGTH_16_BIT = 0x7E;
    private static final int PAYLOAD_LENGTH_64_BIT = 0x7F;

    private final byte[] payload;
    private final PacketType type;

    public DataFrame(@NotNull InputStream input) throws IOException {
        byte[] header = new byte[2];
        input.read(header);

        int opcode = header[0] & 0x0F;
        switch (opcode) {
            case 0x01:
                type = PacketType.TEXT;
                break;
            case 0x02:
                type = PacketType.BINARY;
                break;
            case 0x08:
                type = PacketType.CLOSE;
                break;
            default:
                throw new IOException("Unexpected opcode: " + opcode);
        }

        int payloadLength = header[1] & 0x7F;
        boolean masked = (header[1] & MASK_BIT) != 0;
        if (payloadLength == PAYLOAD_LENGTH_16_BIT) {
            byte[] lengthBytes = new byte[2];
            input.read(lengthBytes);
            payloadLength = ByteBuffer.wrap(lengthBytes).getShort() & 0xFFFF;
        } else if (payloadLength == PAYLOAD_LENGTH_64_BIT) {
            byte[] lengthBytes = new byte[8];
            input.read(lengthBytes);
            payloadLength = (int) ByteBuffer.wrap(lengthBytes).getLong();
        }

        byte[] mask = null;
        if (masked) {
            mask = new byte[4];
            input.read(mask);
        }

        byte[] payload = new byte[payloadLength];
        input.read(payload);

        if (masked) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (payload[i] ^ mask[i % 4]);
            }
        }

        this.payload = payload;
    }

    public String getPayloadText() {
        if (type == PacketType.TEXT) {
            return new String(payload, StandardCharsets.UTF_8);
        } else return null;
    }

    public byte[] getPayloadBytes() {
        if (type == PacketType.BINARY || type == PacketType.TEXT)
            return payload;
        else return null;
    }

    public PacketType getType() {
        return type;
    }
}
