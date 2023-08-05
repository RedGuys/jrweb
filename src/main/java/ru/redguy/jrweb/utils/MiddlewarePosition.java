package ru.redguy.jrweb.utils;

public enum MiddlewarePosition {
    /**
     * Runs {@link Middleware} before page processing
     */
    BEFORE,
    /**
     * Runs {@link Middleware} before and after page processing
     */
    BOTH,
    /**
     * Runs {@link Middleware} after page processing
     */
    AFTER;
}
