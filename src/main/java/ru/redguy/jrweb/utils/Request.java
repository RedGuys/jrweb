package ru.redguy.jrweb.utils;

import java.io.BufferedReader;

public class Request {
    private BufferedReader reader;

    public String url = "/";

    public Request(BufferedReader reader) {
        this.reader = reader;
        parseRequest();
    }

    private void parseRequest() {

    }
}
