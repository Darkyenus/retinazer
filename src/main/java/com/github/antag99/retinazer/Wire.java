package com.github.antag99.retinazer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that makes fields eligible for wiring. This can be applied to a single field.
 * If a wired field is not handled by any {@link WireResolver}, an exception is thrown.
 * Attempting to wire a static field results in an exception.
 *
 * @see WireResolver
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Wire {
}
