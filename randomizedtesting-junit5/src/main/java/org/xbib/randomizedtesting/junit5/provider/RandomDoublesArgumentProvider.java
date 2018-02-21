package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.RandomDoubles;

import java.util.Arrays;
import java.util.stream.Stream;

public class RandomDoublesArgumentProvider extends RandomizedArgumentProvider<RandomDoubles> {
    private RandomDoubles annotation;

    @Override
    public void accept(RandomDoubles annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Arrays.stream(annotation.value()).map(RandomDoubleArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Arrays.stream(annotation.value()).map(RandomDoubleArgumentProvider::generateParam);
    }
}
