package ru.redguy.jrweb.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class Response {

    public StatusCode statusCode = StatusCodes.OK;
    private HeadersList headers = new HeadersList();
    public BufferedWriter writer;
    private boolean headersSent = false;

    public Response(BufferedWriter writer) {
        this.writer = writer;
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

    public void flushHeaders() {
        if (headersSent) return;
        try {
            writer.write("HTTP/2 ");
            writer.write(statusCode.generate());
            writer.newLine();
            writer.write(headers.generate());
            writer.newLine();
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
