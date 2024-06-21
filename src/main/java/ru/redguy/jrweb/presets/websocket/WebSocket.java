package ru.redguy.jrweb.presets.websocket;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;

/**
 * Websocket implementation.
 */
public class WebSocket extends Page {
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

    private HashMap<Context, WebSocketConnection> connections = new HashMap<>();

    @Override
    public void run(Context context) throws Exception {
        if (context.request.headers.has(Headers.Common.CONNECTION) && context.request.headers.getFirst(Headers.Common.CONNECTION).getValue().equalsIgnoreCase("upgrade") && context.request.headers.has(Headers.Common.UPGRADE) && context.request.headers.getFirst(Headers.Common.UPGRADE).getValue().equalsIgnoreCase("websocket")) {
            context.response.setStatusCode(StatusCodes.SWITCHING_PROTOCOLS("websocket", "Upgrade"));
            String key = context.request.headers.getFirst(Headers.Request.SEC_WEBSOCKET_KEY).getValue().trim();
            key = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            key = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA1").digest(key.getBytes("UTF-8")));
            context.response.getHeaders().add(Headers.Response.SEC_WEBSOCKET_ACCEPT, key);
            context.response.flushHeaders();

            onOpen(context);

            while (true) {
                if (!context.socket.isOpen()) {
                    onClose(context);
                    return;
                }
                DataFrame frame = null;
                try {
                    frame = DataFrame.parseDataFrame(context.reader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (frame == null) {
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
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Sends string data to client
     *
     * @param context connection context
     * @param text    data to send
     * @throws Exception throws if connection broken
     */
    public static void send(@NotNull Context context, @NotNull String text) throws Exception {
        context.outputStream.write(createHeaderBytes(text.getBytes(StandardCharsets.UTF_8).length));
        context.outputStream.write(text.getBytes(StandardCharsets.UTF_8));
    }

    public static void close(@NotNull Context context) throws Exception {
        close(context, 1000);
    }

    public static void close(@NotNull Context context, int statusCode) throws Exception {
        close(context, statusCode, "");
    }

    public static void close(@NotNull Context context, int statusCode, String reason) throws Exception {
        try {
            context.outputStream.write(createClosePacket(statusCode, reason));
        } finally {
            context.socket.close();
        }
    }

    public static byte @NotNull [] createClosePacket(int statusCode, String reason) {
        // Convert the status code to two bytes (big-endian)
        byte[] statusCodeBytes = new byte[2];
        statusCodeBytes[0] = (byte) ((statusCode >> 8) & 0xFF);
        statusCodeBytes[1] = (byte) (statusCode & 0xFF);

        byte[] reasonBytes = new byte[0];
        if (reason != null && !reason.isEmpty()) {
            reasonBytes = reason.getBytes(StandardCharsets.UTF_8);
        }

        // Calculate the total length of the payload
        int payloadLength = 2 + reasonBytes.length;

        // Prepare the first byte indicating that this is a control frame with FIN and opcode 0x08
        byte firstByte = (byte) 0x88;

        // Construct the frame bytes
        byte[] frameBytes = new byte[payloadLength + 2]; // Add 2 bytes for the length
        frameBytes[0] = firstByte;
        frameBytes[1] = (byte) payloadLength;
        System.arraycopy(statusCodeBytes, 0, frameBytes, 2, 2); // Copy the status code bytes
        System.arraycopy(reasonBytes, 0, frameBytes, 4, reasonBytes.length); // Copy the reason bytes

        return frameBytes;
    }

    /**
     * Creates header bytes
     *
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
     *
     * @param ctx   connection context
     * @param frame received dataframe
     */
    public void onMessage(Context ctx, DataFrame frame) {
        try {
            if (connections.containsKey(ctx)) connections.get(ctx).dataFrames.put(frame);
        } catch (InterruptedException e) {
            System.out.println("Cannot save dataframe");
        }
    }

    /**
     * Callback for closing connection
     *
     * @param ctx connection context
     */
    public void onClose(Context ctx) {
        if (connections.containsKey(ctx)) connections.get(ctx).opened = false;
        connections.remove(ctx);
    }

    /**
     * Callback for opening connection
     *
     * @param ctx connection context
     */
    public void onOpen(Context ctx) {
        WebSocketConnection connection = new WebSocketConnection(this, ctx);
        connection.opened = true;
        connections.put(ctx, connection);
        onOpen(connection);
    }

    /**
     * Callback for connection version
     *
     * @param connection WebSocket connection to client
     */
    public void onOpen(WebSocketConnection connection) {
        //Do nothing
    }
}
