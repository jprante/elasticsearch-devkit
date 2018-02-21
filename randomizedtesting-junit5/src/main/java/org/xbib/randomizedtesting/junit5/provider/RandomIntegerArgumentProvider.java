package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.RandomApi;
import org.xbib.randomizedtesting.junit5.annotation.RandomInteger;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.generateInteger;

public class RandomIntegerArgumentProvider extends RandomizedArgumentProvider<RandomInteger> {
    private RandomInteger annotation;

    static int generateParam(RandomInteger annotation) {
        return RandomApi.generateInteger(annotation.min(), annotation.max());
    }

    static Object[] generateParams(RandomInteger annotation) {
        String name = annotation.name();
        if (name.isEmpty()) name = "int from " + annotation.min() + " to " + annotation.max();
        return new Object[]{RandomApi.generateInteger(annotation.min(), annotation.max()), name};
    }

    @Override
    public void accept(RandomInteger annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation).map(RandomIntegerArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(annotation).map(RandomIntegerArgumentProvider::generateParam);
    }
}
