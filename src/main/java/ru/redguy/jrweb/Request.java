package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class Request {
    public BufferedReader reader;
    public InputStream stream;
    public Socket socket;
    public String httpVersion = "HTTP/2";
    public Method method = Methods.GET;
    public String url = "/";
    public HashMap<String, String> query = new HashMap<>();
    public HeadersList headers = new HeadersList();
    public HashMap<String, Object> params = new HashMap<>();

    public Request(BufferedReader reader, Socket socket) throws IOException {
        this.reader = reader;
        this.socket = socket;
    }

    protected void parseRequest(Context context, WebServer webServer) throws IOException {
        String line = reader.readLine();
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

        if (webServer.getOptions().isEnableSessionStorage())
            context.session = webServer.getSessionStorage().get(context);
    }
}
