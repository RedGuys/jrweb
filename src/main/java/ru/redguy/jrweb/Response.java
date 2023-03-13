package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.HeadersList;
import ru.redguy.jrweb.utils.StatusCode;
import ru.redguy.jrweb.utils.StatusCodes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

public class Response {
    public StatusCode statusCode = StatusCodes.OK;
    private HeadersList headers = new HeadersList();
    public BufferedWriter writer;
    public OutputStream outputStream;
    private boolean headersSent = false;

    public Response(BufferedWriter writer, OutputStream outputStream) {
        this.writer = writer;
        this.outputStream = outputStream;
    }

    public boolean send(String str) {
        if (!headersSent) flushHeaders();
        try {
            writer.write(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean send(byte[] bytes) {
        if (!headersSent) flushHeaders();
        try {
            writer.flush();
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void flushHeaders() {
        if (headersSent) return;
        try {
            writer.write("HTTP/2 ");
            writer.write(statusCode.generate());
            writer.newLine();
            writer.write(headers.generate());
            writer.newLine();
            writer.newLine(); //indicating the end of the header section
            headersSent = true;
        } catch (IOException e) {
            return;
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
