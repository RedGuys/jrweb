package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.util.HashMap;

public class Request {
    public Context context;
    public String httpVersion = "HTTP/2";
    public Method method = Methods.GET;
    public String url = "/";
    public HashMap<String, String> query = new HashMap<>();
    public HeadersList headers = new HeadersList();
    public HashMap<String, Object> params = new HashMap<>();

    public Request(Context context) {
        this.context = context;
    }

    protected void parseRequest() throws IOException {
        String line = context.reader.readLine();
        method = Methods.getMethod(line.split(" ")[0]);
        url = line.split(" ")[1];
        httpVersion = line.split(" ")[2];
        if (url.contains("?")) {
            String[] split = url.split("\\?");
            url = split[0];
            String[] query = split[1].split("&");
            for (String s : query) {
                String[] keyValue = s.split("=");
                if (keyValue.length == 2)
                    this.query.put(keyValue[0], keyValue[1]);
                else
                    this.query.put(keyValue[0], "");
            }
        }

        while (!(line = context.reader.readLine()).equals("")) {
            headers.add(line);
        }

        if (headers.has(Headers.Request.COOKIE)) {
            HeaderValue headerValue = headers.get(Headers.Request.COOKIE)[0]; //Only one cookie header is allowed by RFC6265
            String[] split = headerValue.getValue().split(";");
            for (String s : split) {
                context.cookies.internalAddCookie(new Cookie(s));
            }
        }

        if (context.server.getOptions().isEnableSessionStorage())
            context.session = context.server.getSessionStorage().get(context);
    }
}
