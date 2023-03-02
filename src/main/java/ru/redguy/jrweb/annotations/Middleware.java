package ru.redguy.jrweb.annotations;

import ru.redguy.jrweb.utils.MiddlewarePosition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface Middleware {
    String value();
    String method() default "GET";
    MiddlewarePosition position() default MiddlewarePosition.BEFORE;
}
