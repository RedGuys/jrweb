package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.*;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {
    private BufferedReader reader;
    public Method method = Methods.GET;
    public String url = "/";
    public HeadersList headers = new HeadersList();

    public Request(BufferedReader reader) throws IOException {
        this.reader = reader;
    }

    protected void parseRequest(Context context) throws IOException {
        String line = reader.readLine();
        method = Methods.getMethod(line.split(" ")[0]);
        url = line.split(" ")[1];

        while (!(line = reader.readLine()).equals("")) {
            headers.add(line);
        }

        if (headers.has(Headers.Request.COOKIE)) {
            HeaderValue headerValue = headers.get(Headers.Request.COOKIE)[0]; //Only one cookie header is allowed by RFC6265
            String[] split = headerValue.getValue().split(";");
            for (String s : split) {
                context.cookies.internalAddCookie(new Cookie(s));
            }
        }
    }
}
