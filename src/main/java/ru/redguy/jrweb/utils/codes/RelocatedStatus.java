package ru.redguy.jrweb.utils.codes;

import ru.redguy.jrweb.utils.Headers;
import ru.redguy.jrweb.utils.StatusCode;

/**
 * Status codes with location header. 201, 301, 302, 303, 305, 307, 308.
 * @author RedGuy
 */
public class RelocatedStatus extends StatusCode {
    public RelocatedStatus(int status, String message, String location) {
        super(status, message);
        getHeaders().add(Headers.Response.LOCATION.instance(location));
    }
}
