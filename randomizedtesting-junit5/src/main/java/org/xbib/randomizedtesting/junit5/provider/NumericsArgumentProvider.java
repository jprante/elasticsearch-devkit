package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.Numerics;

import java.util.Arrays;
import java.util.stream.Stream;

public class NumericsArgumentProvider extends RandomizedArgumentProvider<Numerics> {
    private Numerics annotation;

    @Override
    public void accept(Numerics annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Arrays.stream(annotation.value()).map(NumericArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Arrays.stream(annotation.value()).map(NumericArgumentProvider::generateParam);
    }

}
