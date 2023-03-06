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

    public void add(@NotNull HeaderValue headerValue, boolean replaceExisting) {
        if(!headerValue.getHeader().isAllowMultiple()) {
            for (HeaderValue value : headers) {
                if (value.getHeader().getName().equals(headerValue.getHeader().getName())) {
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

    public void add(HeaderValue headerValue) {
        add(headerValue, true);
    }

    public void add(Header header, String value, boolean replaceExisting) {
        add(new HeaderValue(header, value), replaceExisting);
    }

    public void add(Header header, String value) {
        add(new HeaderValue(header, value), true);
    }

    public void remove(Header header) {
        headers.removeIf(value -> value.getHeader().getName().equals(header.getName()));
    }

    public void remove(HeaderValue headerValue) {
        headers.remove(headerValue);
    }

    public void remove(Header header, String value) {
        headers.removeIf(headerValue -> headerValue.getHeader().getName().equals(header.getName()) && Objects.equals(headerValue.getValue(),value));
    }

    public HeaderValue[] get(Header header) {
        return headers.stream().filter(hv -> Objects.equals(hv.getHeader().getName(), header.getName())).toArray(HeaderValue[]::new);
    }

    public Iterator<HeaderValue> iterator() {
        return headers.iterator();
    }

    public void forEach(Consumer<HeaderValue> headerConsumer) {
        headers.forEach(headerConsumer);
    }

    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((hv) -> stringBuilder.append(hv.generate()).append("\n"));
        return stringBuilder.toString().trim();
    }
}
