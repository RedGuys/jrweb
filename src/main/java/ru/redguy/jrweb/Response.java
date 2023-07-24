package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.HeadersList;
import ru.redguy.jrweb.utils.StatusCode;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Response {
    public StatusCode statusCode = StatusCodes.OK;
    private final HeadersList headers = new HeadersList();
    private boolean headersSent = false;
    public final Context context;

    public Response(Context context) {
        this.context = context;
    }

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
        } catch (IOException e) {
            return false;
        }
    }

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
            context.outputStream.flush();
            context.outputStream.write(bytes);
            context.outputStream.flush();
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write("\r\n");
                context.outputStream.flush();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected void finish() {
        try {
            if (context.server.getOptions().isEnableChunkedTransfer()) {
                context.outputStream.write("0\r\n\r\n");
            }
            context.outputStream.flush();
        } catch (IOException e) {
            return;
        }
    }

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
            context.outputStream.flush();
            headersSent = true;
        } catch (IOException e) {
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

    public Response setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HeadersList getHeaders() {
        return headers;
    }

    public boolean isHeadersSent() {
        return headersSent;
    }
}
