package ru.redguy.jrweb;

import ru.redguy.jrweb.utils.*;

import java.io.IOException;
import java.util.HashMap;

public class Request {
     /**
     * Context of request.
     */
    public Context context;
    /**
     * HTTP version. Be aware of editing this field, some clients may break.
     */
    public String httpVersion = "HTTP/2";
    /**
     * Method of request.
     */
    public Method method = Methods.GET;
    /**
     * URL of request.
     */
    public String url = "/";
    /**
     * Url query data of request.
     */
    public HashMap<String, String> query = new HashMap<>();
    /**
     * Headers of request.
     */
    public HeadersList headers = new HeadersList();
    /**
     * Params of request. Automatically parsed from body depending on {@link Headers.Common#CONTENT_TYPE}
     */
    public HashMap<String, Object> params = new HashMap<>();

    public Request(Context context) {
        this.context = context;
    }

    /**
     * Parses request. Internal method.
     * @throws IOException if an I/O error occurs.
     */
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
