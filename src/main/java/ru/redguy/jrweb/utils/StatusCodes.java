package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.redguy.jrweb.utils.codes.*;

/**
 * Class with all standard and popular status codes.
 */
public class StatusCodes {
    public static final StatusCode CONTINUE = new StatusCode(100, "Continue");

    @Contract("_, _ -> new")
    public static @NotNull UpgradeStatus SWITCHING_PROTOCOLS(String upgradeTo, String connection) {
        return new UpgradeStatus(101, "Switching Protocols", upgradeTo, connection);
    }

    public static final StatusCode PROCESSING = new StatusCode(102, "Processing");
    public static final StatusCode EARLY_HINTS = new StatusCode(103, "Early Hints");

    public static final StatusCode OK = new StatusCode(200, "OK");

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus CREATED(String location) {
        return new RelocatedStatus(201, "Created", location);
    }

    public static final StatusCode ACCEPTED = new StatusCode(202, "Accepted");
    public static final StatusCode NON_AUTHORITATIVE_INFORMATION = new StatusCode(203, "Non-Authoritative Information");
    public static final StatusCode NO_CONTENT = new StatusCode(204, "No Content");
    public static final StatusCode RESET_CONTENT = new StatusCode(205, "Reset Content");
    public static final StatusCode PARTIAL_CONTENT = new StatusCode(206, "Partial Content");
    public static final StatusCode MULTI_STATUS = new StatusCode(207, "Multi-Status");
    public static final StatusCode ALREADY_REPORTED = new StatusCode(208, "Already Reported");
    public static final StatusCode IM_USED = new StatusCode(226, "IM Used");

    public static final StatusCode MULTIPLE_CHOICES = new StatusCode(300, "Multiple Choices");

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus MOVED_PERMANENTLY(String location) {
        return new RelocatedStatus(301, "Moved Permanently", location);
    }

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus FOUND(String location) {
        return new RelocatedStatus(302, "Found", location);
    }

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus SEE_OTHER(String location) {
        return new RelocatedStatus(303, "See Other", location);
    }

    public static final StatusCode NOT_MODIFIED = new StatusCode(304, "Not Modified");

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus USE_PROXY(String location) {
        return new RelocatedStatus(305, "Use Proxy", location);
    }

    @Reserved
    public static final StatusCode SWITCH_PROXY = new StatusCode(306, "Switch Proxy");

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus TEMPORARY_REDIRECT(String location) {
        return new RelocatedStatus(307, "Temporary Redirect", location);
    }

    @Contract("_ -> new")
    public static @NotNull RelocatedStatus PERMANENT_REDIRECT(String location) {
        return new RelocatedStatus(308, "Permanent Redirect", location);
    }

    public static final StatusCode BAD_REQUEST = new StatusCode(400, "Bad Request");
    public static final StatusCode UNAUTHORIZED = new StatusCode(401, "Unauthorized");
    public static final StatusCode PAYMENT_REQUIRED = new StatusCode(402, "Payment Required");
    public static final StatusCode FORBIDDEN = new StatusCode(403, "Forbidden");
    public static final StatusCode NOT_FOUND = new StatusCode(404, "Not Found");
    public static final StatusCode METHOD_NOT_ALLOWED = new StatusCode(405, "Method Not Allowed");
    public static final StatusCode NOT_ACCEPTABLE = new StatusCode(406, "Not Acceptable");
    public static final StatusCode PROXY_AUTHENTICATION_REQUIRED = new StatusCode(407, "Proxy Authentication Required");
    public static final StatusCode REQUEST_TIMEOUT = new StatusCode(408, "Request Timeout");
    public static final StatusCode CONFLICT = new StatusCode(409, "Conflict");
    public static final StatusCode GONE = new StatusCode(410, "Gone");
    public static final StatusCode LENGTH_REQUIRED = new StatusCode(411, "Length Required");
    public static final StatusCode PRECONDITION_FAILED = new StatusCode(412, "Precondition Failed");

    @Contract("_ -> new")
    public static @NotNull RetryAfterStatus PAYLOAD_TOO_LARGE(String retryAfter) {
        return new RetryAfterStatus(413, "Payload Too Large", retryAfter);
    }

    public static @NotNull RetryAfterStatus PAYLOAD_TOO_LARGE() {
        return new RetryAfterStatus(413, "Payload Too Large");
    }

    public static final StatusCode URI_TOO_LONG = new StatusCode(414, "URI Too Long");
    public static final StatusCode UNSUPPORTED_MEDIA_TYPE = new StatusCode(415, "Unsupported Media Type");
    public static final StatusCode RANGE_NOT_SATISFIABLE = new StatusCode(416, "Range Not Satisfiable");
    public static final StatusCode EXPECTATION_FAILED = new StatusCode(417, "Expectation Failed");
    public static final StatusCode I_AM_A_TEAPOT = new StatusCode(418, "I'm a teapot");
    @NotInRFC
    public static final StatusCode AUTHENTICATION_TIMEOUT = new StatusCode(419, "Authentication Timeout");
    public static final StatusCode MISDIRECTED_REQUEST = new StatusCode(421, "Misdirected Request");
    public static final StatusCode UNPROCESSABLE_ENTITY = new StatusCode(422, "Unprocessable Entity");
    public static final StatusCode LOCKED = new StatusCode(423, "Locked");
    public static final StatusCode FAILED_DEPENDENCY = new StatusCode(424, "Failed Dependency");
    public static final StatusCode TOO_EARLY = new StatusCode(425, "Too Early");

    @Contract("_, _ -> new")
    public static @NotNull UpgradeStatus UPGRADE_REQUIRED(String upgradeTo, String connection) {
        return new UpgradeStatus(426, "Upgrade Required", upgradeTo, connection);
    }

    public static final StatusCode PRECONDITION_REQUIRED = new StatusCode(428, "Precondition Required");

    @Contract("_ -> new")
    public static @NotNull RetryAfterStatus TOO_MANY_REQUESTS(String retryAfter) {
        return new RetryAfterStatus(429, "Too Many Requests", retryAfter);
    }

    public static @NotNull RetryAfterStatus TOO_MANY_REQUESTS() {
        return new RetryAfterStatus(429, "Too Many Requests");
    }

    public static final StatusCode REQUEST_HEADER_FIELDS_TOO_LARGE = new StatusCode(431, "Request Header Fields Too Large");

    @Contract("_ -> new")
    public static @NotNull RetryWithStatus RETRY_WITH(String retryWith) {
        return new RetryWithStatus(449, "Retry With", retryWith);
    }

    public static @NotNull RetryWithStatus RETRY_WITH() {
        return new RetryWithStatus(449, "Retry With");
    }

    public static final StatusCode UNAVAILABLE_FOR_LEGAL_REASONS = new StatusCode(451, "Unavailable For Legal Reasons");
    public static final StatusCode CLIENT_CLOSED_REQUEST = new StatusCode(499, "Client Closed Request");

    public static final StatusCode INTERNAL_SERVER_ERROR = new StatusCode(500, "Internal Server Error");
    public static final StatusCode NOT_IMPLEMENTED = new StatusCode(501, "Not Implemented");
    public static final StatusCode BAD_GATEWAY = new StatusCode(502, "Bad Gateway");

    @Contract("_ -> new")
    public static @NotNull RetryAfterStatus SERVICE_UNAVAILABLE(String retryAfter) {
        return new RetryAfterStatus(503, "Service Unavailable", retryAfter);
    }

    public static @NotNull RetryAfterStatus SERVICE_UNAVAILABLE() {
        return new RetryAfterStatus(503, "Service Unavailable");
    }

    public static final StatusCode GATEWAY_TIMEOUT = new StatusCode(504, "Gateway Timeout");
    public static final StatusCode HTTP_VERSION_NOT_SUPPORTED = new StatusCode(505, "HTTP Version Not Supported");
    public static final StatusCode VARIANT_ALSO_NEGOTIATES = new StatusCode(506, "Variant Also Negotiates");
    public static final StatusCode INSUFFICIENT_STORAGE = new StatusCode(507, "Insufficient Storage");
    public static final StatusCode LOOP_DETECTED = new StatusCode(508, "Loop Detected");
    @NotInRFC
    public static final StatusCode BANDWIDTH_LIMIT_EXCEEDED = new StatusCode(509, "Bandwidth Limit Exceeded");
    public static final StatusCode NOT_EXTENDED = new StatusCode(510, "Not Extended");
    public static final StatusCode NETWORK_AUTHENTICATION_REQUIRED = new StatusCode(511, "Network Authentication Required");
    public static final StatusCode UNKNOWN_ERROR = new StatusCode(520, "Unknown Error");
    public static final StatusCode WEB_SERVER_IS_DOWN = new StatusCode(521, "Web Server Is Down");
    public static final StatusCode CONNECTION_TIMED_OUT = new StatusCode(522, "Connection Timed Out");
    public static final StatusCode ORIGIN_IS_UNREACHABLE = new StatusCode(523, "Origin Is Unreachable");
    public static final StatusCode A_TIMEOUT_OCCURRED = new StatusCode(524, "A Timeout Occurred");
    public static final StatusCode SSL_HANDSHAKE_FAILED = new StatusCode(525, "SSL Handshake Failed");
    public static final StatusCode INVALID_SSL_CERTIFICATE = new StatusCode(526, "Invalid SSL Certificate");
}
