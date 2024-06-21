package ru.redguy.jrweb.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * List of headers.
 * @author RedGuy
 */
public class HeadersList {
    private final List<HeaderValue> headers = new ArrayList<>();

    /**
     * Adds header, if {@link Header#isAllowMultiple()} false - replaces old header on replaceExisting
     * @param headerValue {@link Header} with value
     * @param replaceExisting if header allows only one instance, replaces existing on true
     */
    public void add(@NotNull HeaderValue headerValue, boolean replaceExisting) {
        if(!headerValue.getHeader().isAllowMultiple()) {
            for (HeaderValue value : headers) {
                if (value.getHeader().getName().equalsIgnoreCase(headerValue.getHeader().getName())) {
                    if(replaceExisting) {
                        headers.remove(value);
                    } else {
                        return;
                    }
                }
            }
        }
        headers.add(headerValue);
    }

    /**
     * Adds header with replacing exiting singleton header
     * @param headerValue {@link Header} with value
     */
    public void add(HeaderValue headerValue) {
        add(headerValue, true);
    }

    /**
     * Parses header from string
     * @param raw raw header content
     */
    public void add(@NotNull String raw) {
        String[] split = raw.split(":");
        add(new HeaderValue(Headers.getHeader(split[0].trim()), split[1].trim()), false);
    }

    /**
     * Adds header.
     * @param header target header
     * @param value header value
     * @param replaceExisting replace exiting singleton header
     */
    public void add(Header header, String value, boolean replaceExisting) {
        add(new HeaderValue(header, value), replaceExisting);
    }

    /**
     * Adds header with replacing old singleton header.
     * @param header target header
     * @param value header value
     */
    public void add(Header header, String value) {
        add(new HeaderValue(header, value), true);
    }

    /**
     * Removes all instances of {@link Header}
     * @param header header to remove
     */
    public void remove(Header header) {
        headers.removeIf(value -> value.getHeader().getName().equalsIgnoreCase(header.getName()));
    }

    /**
     * Removes provided header with value
     * @param headerValue {@link Header} with value
     */
    public void remove(HeaderValue headerValue) {
        headers.remove(headerValue);
    }

    /**
     * Removes provided header with value
     * @param header {@link Header}
     * @param value header value
     */
    public void remove(Header header, String value) {
        headers.removeIf(headerValue -> headerValue.getHeader().getName().equalsIgnoreCase(header.getName()) && Objects.equals(headerValue.getValue(),value));
    }

    /**
     * Gets {@link HeaderValue}-s
     * @param header target {@link Header}
     * @return array of {@link HeaderValue}
     */
    public HeaderValue[] get(Header header) {
        return headers.stream().filter(hv -> hv.getHeader().getName().equalsIgnoreCase(header.getName().toLowerCase())).toArray(HeaderValue[]::new);
    }

    /**
     * Gets first found {@link HeaderValue}
     * @param header target {@link Header}
     * @return {@link HeaderValue}
     */
    public HeaderValue getFirst(Header header) {
        return headers.stream().filter(hv -> hv.getHeader().getName().equalsIgnoreCase(header.getName().toLowerCase())).findFirst().orElse(null);
    }

    /**
     * Iterator with {@link HeaderValue}'s
     * @return an iterator over the elements in headers in proper sequence
     */
    public Iterator<HeaderValue> iterator() {
        return headers.iterator();
    }

    /**
     * Performs the given action for each element of the Iterable until all elements have been processed or the action throws an exception. Unless otherwise specified by the implementing class, actions are performed in the order of iteration (if an iteration order is specified). Exceptions thrown by the action are relayed to the caller.
     * @param headerConsumer The action to be performed for each element
     */
    public void forEach(Consumer<HeaderValue> headerConsumer) {
        headers.forEach(headerConsumer);
    }

    /**
     * Generates string of HTTP headers
     * @return HTTP head headers
     */
    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((hv) -> stringBuilder.append(hv.generate()).append("\r\n"));
        return stringBuilder.toString().trim();
    }

    /**
     * Checks that this list contains target {@link Header}
     * @param header target {@link Header}
     * @return true if header found
     */
    public boolean has(Header header) {
        return headers.stream().anyMatch(hv -> hv.getHeader().getName().equalsIgnoreCase(header.getName().toLowerCase()));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (HeaderValue header : headers) {
            stringBuilder.append(header.getHeader().getName()).append("=").append(header.getValue()).append(",\n");
        }
        stringBuilder.setLength(Math.max(stringBuilder.length() - 1, 1));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
