package ru.redguy.jrweb.utils;

/**
 * This class contains all headers.
 * @author RedGuy
 */
public class Headers {
    /**
     * This class contains all request headers.
     */
    public static class Request {

    }

    /**
     * This class contains all response headers.
     */
    public static class Response {
        public static final Header UPGRADE = new Header("Upgrade");
        public static final Header CONNECTION = new Header("Connection");
        public static final Header LOCATION = new Header("Location").setAllowMultiple();
        public static final Header RETRY_AFTER = new Header("Retry-After");
        public static final Header MS_ECHO_REQUEST = new Header("MS-Echo-Request");
        public static final Header CONTENT_LENGTH = new Header("Content-Length");
    }
}
