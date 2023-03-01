package ru.redguy.jrweb.utils;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {
    private BufferedReader reader;
    public Method method = Methods.GET;

    public String url = "/";

    public Request(BufferedReader reader) throws IOException {
        this.reader = reader;
        parseRequest();
    }

    private void parseRequest() throws IOException {
        String line = reader.readLine();
        method = Methods.getMethod(line.split(" ")[0]);
        url = line.split(" ")[1];
    }
}
