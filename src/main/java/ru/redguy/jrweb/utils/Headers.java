package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all headers.
 *
 * @author RedGuy
 */
public class Headers {

    protected static List<Header> headers = new ArrayList<>();

    /**
     * This class contains all request headers.
     */
    public static class Request {
        public static final Header HOST = new Header("Host");
        public static final Header USER_AGENT = new Header("User-Agent");
        public static final Header COOKIE = new Header("Cookie");
        public static final Header SEC_WEBSOCKET_KEY = new Header("Sec-WebSocket-Key");
        public static final Header AUTHORIZATION = new Header("Authorization");
        public static final Header CONTENT_TYPE = new Header("Content-Type");
    }

    /**
     * This class contains all response headers.
     */
    public static class Response {
        public static final Header LOCATION = new Header("Location").setAllowMultiple();
        public static final Header RETRY_AFTER = new Header("Retry-After");
        public static final Header MS_ECHO_REQUEST = new Header("MS-Echo-Request");
        public static final Header CONTENT_LENGTH = new Header("Content-Length");
        public static final Header SET_COOKIE = new Header("Set-Cookie");
        public static final Header SEC_WEBSOCKET_ACCEPT = new Header("Sec-WebSocket-Accept");
        public static final Header TRANSFER_ENCODING = new Header("Transfer-Encoding");
        public static final Header CONTENT_ENCODING = new Header("Content-Encoding");
        public static final Header SERVER = new Header("Server");
    }

    public static class Common {

        public static final Header CONNECTION = new Header("Connection");
        public static final Header UPGRADE = new Header("Upgrade");
    }

    public static @NotNull Header getHeader(String name) {
        for (Header header : headers) {
            if (header.getName().equals(name)) {
                return header;
            }
        }
        return new Header(name);
    }
}
