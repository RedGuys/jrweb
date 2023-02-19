package ru.redguy.jrweb.utils.codes;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.StatusCode;

/**
 * Status codes with retry-after header. 413, 429, 503.
 * @author RedGuy
 */
public class RetryAfterStatus extends StatusCode {
    public RetryAfterStatus(int status, String message, String retryAfter) {
        super(status, message);
        getHeaders().add(Headers.Response.RETRY_AFTER.instance(retryAfter));
    }

    public RetryAfterStatus(int status, String message) {
        super(status, message);
    }
}
