package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.HeadersList;
import ru.redguy.jrweb.utils.StatusCode;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Response {
    public StatusCode statusCode = StatusCodes.OK;
    private HeadersList headers = new HeadersList();
    public BufferedWriter writer;
    public OutputStream outputStream;
    private boolean headersSent = false;
    public WebServer webServer;

    public Response(WebServer webServer, BufferedWriter writer, OutputStream outputStream) {
        this.webServer = webServer;
        this.writer = writer;
        this.outputStream = outputStream;
    }

    public boolean send(String str) {
        if (!headersSent) flushHeaders();
        try {
            if (webServer.getOptions().getCompressor() != null) {
                return send(str.getBytes(StandardCharsets.UTF_8));
            }
            if (webServer.getOptions().isEnableChunkedTransfer()) {
                writer.write(Integer.toHexString(str.length()));
                writer.write("\r\n");
            }
            writer.write(str);
            if (webServer.getOptions().isEnableChunkedTransfer()) {
                writer.write("\r\n");
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean send(byte[] bytes) {
        if (!headersSent) flushHeaders();
        try {
            if (webServer.getOptions().getCompressor() != null) {
                bytes = webServer.getOptions().getCompressor().compress(bytes);
            }
            if (webServer.getOptions().isEnableChunkedTransfer()) {
                writer.write(Integer.toHexString(bytes.length));
                writer.write("\r\n");
            }
            writer.flush();
            outputStream.write(bytes);
            outputStream.flush();
            if (webServer.getOptions().isEnableChunkedTransfer()) {
                writer.write("\r\n");
                writer.flush();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    protected void finish() {
        try {
            if (webServer.getOptions().isEnableChunkedTransfer()) {
                writer.write("0\r\n\r\n");
            }
            writer.flush();
        } catch (IOException e) {
            return;
        }
    }

    public void flushHeaders() {
        if (headersSent) return;
        generateTransferEncoding();
        try {
            writer.write("HTTP/2 ");
            writer.write(statusCode.generate());
            writer.write("\r\n");
            writer.write(headers.generate());
            writer.write("\r\n");
            writer.write("\r\n"); //indicating the end of the header section
            writer.flush();
            headersSent = true;
        } catch (IOException e) {
            return;
        }
    }

    private void generateTransferEncoding() {
        if (webServer.getOptions().isEnableChunkedTransfer()) {
            headers.add(Headers.Response.TRANSFER_ENCODING,"chunked");
        }
        if(webServer.getOptions().getCompressor() != null) {
            headers.add(Headers.Response.CONTENT_ENCODING,webServer.getOptions().getCompressor().getName());
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
