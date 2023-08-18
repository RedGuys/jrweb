package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.HeadersList;
import ru.redguy.jrweb.utils.StatusCode;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Response {
    /**
     * Status code who will be returned to client
     */
    public StatusCode statusCode = StatusCodes.OK;
    /**
     * {@link Context} of this {@link Response}
     */
    public final Context context;
    private final HeadersList headers = new HeadersList();
    private boolean headersSent = false;

    public Response(Context context) {
        this.context = context;
    }

    /**
     * Sends text data to client
     * @param str text data
     * @return true if sent is successful
     */
    public boolean send(String str) {
        if (!headersSent) flushHeaders();
        try {
            if (context.server.getOptions().getCompressor() != null) {
                return send(str.getBytes(StandardCharsets.UTF_8));
            }
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write(Integer.toHexString(str.length()));
                context.outputStream.write("\r\n");
            }
            context.outputStream.write(str);
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write("\r\n");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sends byte data to client
     * @param bytes byte data
     * @return true is sent is successful
     */
    public boolean send(byte[] bytes) {
        if (!headersSent) flushHeaders();
        try {
            if (context.server.getOptions().getCompressor() != null) {
                bytes = context.server.getOptions().getCompressor().compress(bytes);
            }
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write(Integer.toHexString(bytes.length));
                context.outputStream.write("\r\n");
            }
            context.outputStream.write(bytes);
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write("\r\n");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Finishes data transmission
     */
    protected void finish() {
        try {
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write("0\r\n\r\n");
            }
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Flushes headers and starts sending body to client, mostly called automatically
     */
    public void flushHeaders() {
        if (headersSent) return;
        generateTransferEncoding();
        try {
            context.outputStream.write(context.request.httpVersion + " ");
            context.outputStream.write(statusCode.generate());
            context.outputStream.write("\r\n");
            context.outputStream.write(headers.generate());
            context.outputStream.write("\r\n");
            context.outputStream.write("\r\n"); //indicating the end of the header section
            headersSent = true;
        } catch (Exception e) {
            return;
        }
    }

    private void generateTransferEncoding() {
        if (context.server.getOptions().isEnableChunkedTransfer()) {
            headers.add(Headers.Response.TRANSFER_ENCODING, "chunked");
        }
        if (context.server.getOptions().getCompressor() != null) {
            headers.add(Headers.Response.CONTENT_ENCODING, context.server.getOptions().getCompressor().getName());
        }
    }

    /**
     * Sets response status code, alias of direct accessing statusCode field for builder style writing
     * @param statusCode target status code
     * @return self
     */
    public Response setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * {@link HeadersList} of response, it can be edited before sending headers
     * @return current headers list
     */
    public HeadersList getHeaders() {
        return headers;
    }

    /**
     * Checks is headers sent
     * @return headers sent status
     */
    public boolean isHeadersSent() {
        return headersSent;
    }
}
