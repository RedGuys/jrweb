package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.Nullable;

/**
 * Header value class.
 * @author RedGuy
 */
public class HeaderValue {
    private final Header header;
    private String value = null;

    /**
     * Header value constructor with value.
     * @param header {@link Header}.
     * @param value Value.
     */
    public HeaderValue(Header header, @Nullable String value) {
        this.header = header;
        this.value = value;
    }

    /**
     * Header value constructor without value. Value will be null.
     * @param header {@link Header}.
     */
    public HeaderValue(Header header) {
        this.header = header;
    }

    /**
     * Returns {@link Header}.
     * @return {@link Header}.
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Returns value of this header.
     * @return Value, may be null;
     */
    @Nullable
    public String getValue() {
        return value;
    }

    /**
     * Sets value of this header.
     * @param value Value.
     * @return self.
     */
    public HeaderValue setValue(@Nullable String value) {
        this.value = value;
        return this;
    }

    /**
     * Generates header value string.
     * @return Header value string.
     */
    public String generate() {
        return header.getName() + ": " + value;
    }
}
