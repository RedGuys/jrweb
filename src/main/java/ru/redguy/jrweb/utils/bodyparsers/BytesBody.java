package ru.redguy.jrweb.utils.bodyparsers;

import ru.redguy.jrweb.Context;
import ru.redguy.jrweb.Body;
import ru.redguy.jrweb.utils.Headers;

import java.io.IOException;

public class BytesBody extends Body {

    public byte[] bytes;

    public BytesBody(Context context) {
        super(context);
    }

    @Override
    protected void parse() {
        if (context.request.headers.has(Headers.Common.CONTENT_LENGTH)) {
            try {
                bytes = context.reader.readBytes(Integer.parseInt(context.request.headers.getFirst(Headers.Common.CONTENT_LENGTH).getValue())).array();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                bytes = context.reader.readAllBytes().array();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
