package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public abstract class WebSocket implements ContextRunner {

    //  0                   1                   2                   3
    //      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    //     +-+-+-+-+-------+-+-------------+-------------------------------+
    //     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
    //     |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
    //     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
    //     | |1|2|3|       |K|             |                               |
    //     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
    //     |     Extended payload length continued, if payload len == 127  |
    //     + - - - - - - - - - - - - - - - +-------------------------------+
    //     |                               |Masking-key, if MASK set to 1  |
    //     +-------------------------------+-------------------------------+
    //     | Masking-key (continued)       |          Payload Data         |
    //     +-------------------------------- - - - - - - - - - - - - - - - +
    //     :                     Payload Data continued ...                :
    //     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
    //     |                     Payload Data continued ...                |
    //     +---------------------------------------------------------------+

    // pesdec

    @Override
    public void run(Context context) throws IOException {
        if (context.request.headers.has(Headers.Common.CONNECTION) && context.request.headers.getFirst(Headers.Common.CONNECTION).getValue().equals("Upgrade")) {
            if (context.request.headers.has(Headers.Common.UPGRADE) && context.request.headers.getFirst(Headers.Common.UPGRADE).getValue().equals("websocket")) {
                context.response.setStatusCode(StatusCodes.SWITCHING_PROTOCOLS("websocket", "Upgrade"));
                String key = context.request.headers.getFirst(Headers.Request.SEC_WEBSOCKET_KEY).getValue().trim();
                key = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                try {
                    key = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest(key.getBytes("UTF-8")));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                context.response.getHeaders().add(Headers.Response.SEC_WEBSOCKET_ACCEPT, key);
                context.response.flushHeaders();
                context.response.send("\r\n");

                while (true) {
                    DataFrame frame = new DataFrame(context.request.stream); //This is not good, but who cares :3
                    switch (frame.getType()) {
                        case CLOSE:
                            onClose(context);
                            return;
                        case TEXT:
                        case BINARY:
                            onMessage(context, frame);
                            break;
                    }
                }
            }
        }
    }

    public void send(@NotNull Context context, @NotNull String text) throws IOException {
        context.response.outputStream.write(createHeaderBytes(text.getBytes().length));
        context.response.outputStream.write(text.getBytes());
    }

    @Contract(pure = true)
    private static byte @NotNull [] createHeaderBytes(int payloadLength) {
        byte[] headerBytes;
        int payloadLengthSize;
        if (payloadLength < 126) {
            headerBytes = new byte[2];
            payloadLengthSize = 0;
        } else if (payloadLength < 65536) {
            headerBytes = new byte[4];
            payloadLengthSize = 2;
        } else {
            headerBytes = new byte[10];
            payloadLengthSize = 8;
        }

        headerBytes[0] = (byte) (0x80 | 0x1); // FIN bit and Opcode
        if (payloadLength < 126) {
            headerBytes[1] = (byte) (payloadLength);
        } else if (payloadLength < 65536) {
            headerBytes[1] = (byte) (126);
            for (int i = 0; i < payloadLengthSize; i++) {
                headerBytes[2 + i] = (byte) ((payloadLength >> ((payloadLengthSize - 1 - i) * 8)) & 0xff);
            }
        } else {
            headerBytes[1] = (byte) (127);
            for (int i = 0; i < payloadLengthSize; i++) {
                headerBytes[2 + i] = (byte) ((payloadLength >> ((payloadLengthSize - 1 - i) * 8)) & 0xff);
            }
        }

        return headerBytes;
    }

    public abstract void onMessage(Context ctx, DataFrame frame);

    public void onClose(Context ctx) {
        // Do nothing
    }
}
