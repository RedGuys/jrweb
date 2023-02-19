package ru.redguy.jrweb.utils;

/**
 * Header description realization.
 * @author RedGuy
 */
public class Header {
    private final String name;
    private boolean allowMultiple = false;

    /**
     * Header description constructor.
     * @param name Header name which will be used in encoding/decoding headers.
     */
    public Header(String name) {
        this.name = name;
    }

    /**
     * Allows multiple headers with same name.
     * @return self.
     */
    public Header setAllowMultiple() {
        this.allowMultiple = true;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    /**
     * Creates {@link HeaderValue} instance of this header.
     * @param value Header value.
     * @return {@link HeaderValue} instance.
     */
    public HeaderValue instance(String value) {
        return new HeaderValue(this, value);
    }
}
