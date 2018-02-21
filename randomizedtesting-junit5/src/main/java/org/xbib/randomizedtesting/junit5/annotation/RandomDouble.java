package org.xbib.randomizedtesting.junit5.annotation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.xbib.randomizedtesting.junit5.provider.RandomDoubleArgumentProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can pass a random double number to a JUnit5 test.
 * If multiple of these annotations are specified, the test will be run multiple times each time with a different value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(RandomDoubles.class)
@ArgumentsSource(RandomDoubleArgumentProvider.class)
@ParameterizedTest
public @interface RandomDouble {
    /**
     * Min value of the generated int.
     */
    double min() default Long.MIN_VALUE;

    /**
     * Max value of the generated int.
     */
    double max() default Long.MAX_VALUE;

    /**
     * Name of the test case, useful when you have multiple annotations and you want to give title to each of the
     * generated string. Can be obtained in the test if there is a second param of type String. Ignored if the 2nd
     * param is not present in the test method.
     * <p>
     * Defaults to "double from [min] to [max]".
     */
    String name() default "";
}
