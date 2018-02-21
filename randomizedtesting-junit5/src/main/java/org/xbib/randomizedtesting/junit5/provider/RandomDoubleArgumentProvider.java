package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.RandomDouble;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.generateDouble;

public class RandomDoubleArgumentProvider extends RandomizedArgumentProvider<RandomDouble> {
    private RandomDouble annotation;

    static Double generateParam(RandomDouble annotation) {
        return generateDouble(annotation.min(), annotation.max());
    }

    static Object[] generateParams(RandomDouble annotation) {
        String name = annotation.name();
        if (name.isEmpty()) name = "double from " + annotation.min() + " to " + annotation.max();
        return new Object[]{generateDouble(annotation.min(), annotation.max()), name};
    }

    @Override
    public void accept(RandomDouble annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation).map(RandomDoubleArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(annotation).map(RandomDoubleArgumentProvider::generateParam);
    }
}
