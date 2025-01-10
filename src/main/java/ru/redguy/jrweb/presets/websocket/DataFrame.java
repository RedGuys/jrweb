package ru.redguy.jrweb.presets.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.redguy.jrweb.utils.AsynchronousSocketReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class DataFrame {

    public enum PacketType {
        CONTINUATION,
        TEXT,
        BINARY,
        CLOSE,
        PING,
        PONG
    }

    private static final int MASK_BIT = 0x80;
    private static final int PAYLOAD_LENGTH_16_BIT = 0x7E;
    private static final int PAYLOAD_LENGTH_64_BIT = 0x7F;

    private final byte[] payload;
    private final PacketType type;

    public static @Nullable DataFrame parseDataFrame(@NotNull AsynchronousSocketReader input) throws Exception {
        byte[] header;
        try {
            header = input.asyncReadBytes(2).get().array();
        } catch (ExecutionException e) {
            return null;
        }

        PacketType type;
        int opcode = header[0] & 0x0F;
        switch (opcode) {
            case 0x00:
                type = PacketType.CONTINUATION;
                break;
            case 0x01:
                type = PacketType.TEXT;
                break;
            case 0x02:
                type = PacketType.BINARY;
                break;
            case 0x08:
                type = PacketType.CLOSE;
                break;
            case 0x09:
                type = PacketType.PING;
                break;
            case 0x0A:
                type = PacketType.PONG;
                break;
            default:
                throw new IOException("Unexpected opcode: " + opcode);
        }

        int payloadLength = header[1] & 0x7F;
        boolean masked = (header[1] & MASK_BIT) != 0;
        if (payloadLength == PAYLOAD_LENGTH_16_BIT) {
            ByteBuffer length = input.readBytes(2);
            payloadLength = length.getShort() & 0xFFFF;
        } else if (payloadLength == PAYLOAD_LENGTH_64_BIT) {
            ByteBuffer length = input.readBytes(2);
            payloadLength = (int) length.getLong();
        }

        ByteBuffer mask = null;
        if (masked) {
            mask = input.readBytes(4);
        }

        ByteBuffer payload = input.readBytes(payloadLength);

        // mask
        byte[] bytePayload = payload.array();
        byte[] byteMask = mask != null ? mask.array() : new byte[4];
        if (masked) {
            for (int i = 0; i < bytePayload.length; i++) {
                bytePayload[i] = (byte) (bytePayload[i] ^ byteMask[i % 4]);
            }
        }

        return new DataFrame(type, bytePayload);
    }

    private DataFrame(PacketType type, byte[] payload) {
        this.type = type;
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
