package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.RandomLong;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.generateLong;

public class RandomLongArgumentProvider extends RandomizedArgumentProvider<RandomLong> {
    private RandomLong annotation;

    static long generateParam(RandomLong annotation) {
        return generateLong(annotation.min(), annotation.max());
    }

    static Object[] generateParams(RandomLong annotation) {
        String name = annotation.name();
        if (name.isEmpty()) name = "long from " + annotation.min() + " to " + annotation.max();
        return new Object[]{generateLong(annotation.min(), annotation.max()), name};
    }

    @Override
    public void accept(RandomLong annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation).map(RandomLongArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(annotation).map(RandomLongArgumentProvider::generateParam);
    }
}
