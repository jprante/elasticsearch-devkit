package org.xbib.randomizedtesting.junit5.annotation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.xbib.randomizedtesting.junit5.provider.AlphanumericsArgumentProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ArgumentsSource(AlphanumericsArgumentProvider.class)
@ParameterizedTest
public @interface Alphanumerics {
    Alphanumeric[] value();
}
