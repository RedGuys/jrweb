package ru.redguy.jrweb.presets;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Websocket implementation.
 */
public abstract class WebSocket extends Page {
    public WebSocket(String regex) {
        super(regex);
    }

    public WebSocket(Method method, String regex) {
        super(method, regex);
    }

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
        if (context.request.headers.has(Headers.Common.CONNECTION) && context.request.headers.getFirst(Headers.Common.CONNECTION).getValue().equalsIgnoreCase("upgrade")) {
            if (context.request.headers.has(Headers.Common.UPGRADE) && context.request.headers.getFirst(Headers.Common.UPGRADE).getValue().equalsIgnoreCase("websocket")) {
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

                onOpen(context);

                while (true) {
                    if(context.socket.isClosed()) {
                        onClose(context);
                        return;
                    }
                    DataFrame frame = null;
                    try {
                        frame = DataFrame.parseDataFrame(context.socket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(frame == null) {
                        continue;
                    }
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

    /**
     * Sends string data to client
     * @param context connection context
     * @param text data to send
     * @throws IOException throws if connection broken
     */
    public static void send(@NotNull Context context, @NotNull String text) throws IOException {
        context.outputStream.write(createHeaderBytes(text.getBytes(StandardCharsets.UTF_8).length));
        context.outputStream.write(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates header bytes
     * @param payloadLength length of payload
     * @return byte array of header
     */
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

    /**
     * Callback for new messages from client
     * @param ctx connection context
     * @param frame received dataframe
     */
    public abstract void onMessage(Context ctx, DataFrame frame);

    /**
     * Callback for closing connection
     * @param ctx connection context
     */
    public void onClose(Context ctx) {
        // Do nothing
    }

    /**
     * Callback for opening connection
     * @param ctx connection context
     */
    public void onOpen(Context ctx) {
        // Do nothing
    }
}
