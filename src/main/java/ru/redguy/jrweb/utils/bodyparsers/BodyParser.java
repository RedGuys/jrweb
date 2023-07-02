package ru.redguy.jrweb.utils.bodyparsers;

import ru.redguy.jrweb.Context;

import java.util.HashMap;

public abstract class BodyParser {

    public static HashMap<String, BodyParser> bodyParsers = new HashMap<>();

    public static void init() {
        new URLEncodedBodyParser();
    }

    public BodyParser(String type) {
        bodyParsers.put(type, this);
    }

    public abstract void parse(Context context);
}
