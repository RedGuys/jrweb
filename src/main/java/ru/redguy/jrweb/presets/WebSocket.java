package ru.redguy.jrweb.presets;

import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class WebSocket implements ContextRunner {

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

    private DataFrameFactory factory = new DataFrameFactory();

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
                factory.onNewDataFrame = () -> {
                    List<DataFrame> frames = factory.pollParsedDataFrame();
                    if (frames != null) {
                        List<byte[]> content = new LinkedList<>();
                        for (DataFrame frame : frames) {
                            //Parse content with using frame.getMask
                            if (frame.isFin()) {
                                content.add(frame.getContent());
                            }
                        }
                        System.out.println(new String(content.toArray(byte[]::new)));
                    }
                };

                while (true) {
                    int read = context.request.reader.read();
                    if (read == -1) continue;

                    //split int to two bytes
                    byte b1 = (byte) (read & 0xFF);
                    byte b2 = (byte) ((read >> 8) & 0xFF);

                    factory.addByte(b1);
                    factory.addByte(b2);
                }
            }
        }
    }
}
