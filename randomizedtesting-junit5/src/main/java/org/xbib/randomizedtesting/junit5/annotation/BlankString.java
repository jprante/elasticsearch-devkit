package org.xbib.randomizedtesting.junit5.annotation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.xbib.randomizedtesting.junit5.provider.BlankStringProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Passes either {@code null} or empty string or string with spaces to JUnit5 tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@ArgumentsSource(BlankStringProvider.class)
@ParameterizedTest
public @interface BlankString {
    /**
     * Is passed to the test case if 2nd parameter is available in the test method. If left blank it's going to be
     * filled with the description of what's passed - null or empty string or string with spaces.
     */
    String name() default "";
}
