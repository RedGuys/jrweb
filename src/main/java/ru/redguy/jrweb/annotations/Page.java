package ru.redguy.jrweb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Page {
    /**
     * Path to page.
     */
    String value();

    /**
     * HTTP method of page.
     */
    String method() default "GET";
}
