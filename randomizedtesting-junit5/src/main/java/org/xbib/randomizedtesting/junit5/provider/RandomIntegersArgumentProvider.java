package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.RandomIntegers;

import java.util.Arrays;
import java.util.stream.Stream;

public class RandomIntegersArgumentProvider extends RandomizedArgumentProvider<RandomIntegers> {
    private RandomIntegers annotation;

    @Override
    public void accept(RandomIntegers annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Arrays.stream(annotation.value()).map(RandomIntegerArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Arrays.stream(annotation.value()).map(RandomIntegerArgumentProvider::generateParam);
    }
}
