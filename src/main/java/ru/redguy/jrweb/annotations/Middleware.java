package ru.redguy.jrweb.annotations;

import ru.redguy.jrweb.utils.MiddlewarePosition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface Middleware {
    /**
     * Path to middleware.
     */
    String value();
    /**
     * HTTP method of middleware.
     */
    String method() default "GET";
    /**
     * Execute middleware before or after {@link Router}s and {@link Page}s.
     */
    MiddlewarePosition position() default MiddlewarePosition.BEFORE;
}
