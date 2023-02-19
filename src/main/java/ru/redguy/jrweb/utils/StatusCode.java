package ru.redguy.jrweb.utils;

/**
 * Base status code.
 * @author RedGuy
 */
public class StatusCode {
    private final int status;
    private final String message;
    private final HeadersList headers;

    /**
     * Default status code constructor without custom headers.
     * @param status Status code.
     * @param message Status message.
     */
    public StatusCode(int status, String message) {
        this.status = status;
        this.message = message;
        this.headers = new HeadersList();
    }

    /**
     * Status code constructor with custom headers.
     * @param status Status code.
     * @param message Status message.
     * @param headers Custom headers.
     */
    public StatusCode(int status, String message, HeadersList headers) {
        this.status = status;
        this.message = message;
        this.headers = headers;
    }

    /**
     * Get status code.
     * @return int status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get status message.
     * @return String status message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get headers.
     * @return HeadersList headers.
     */
    public HeadersList getHeaders() {
        return headers;
    }

    /**
     * Generate status string with headers.
     * @return String status string with headers.
     */
    public String generate() {
        return (status + " " + message + "\n" + headers.generate()).trim();
    }
}
