package ru.redguy.jrweb.utils.codes;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.StatusCode;

/**
 * Retry with status code.
 * @author RedGuy
 */
public class RetryWithStatus extends StatusCode {
    public RetryWithStatus(int status, String message, String msEchoRequest) {
        super(status, message);
        getHeaders().add(Headers.Response.MS_ECHO_REQUEST.instance(msEchoRequest));
    }

    public RetryWithStatus(int status, String message) {
        super(status, message);
    }
}
