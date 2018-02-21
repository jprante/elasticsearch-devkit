package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.Numeric;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.numeric;

public class NumericArgumentProvider extends RandomizedArgumentProvider<Numeric> {
    private Numeric annotation;

    static String generateParam(Numeric annotation) {
        if (annotation.length() > 0) return numeric(annotation.length());
        return numeric(annotation.min(), annotation.max());
    }

    static Object[] generateParams(Numeric annotation) {
        return new Object[]{generateParam(annotation), annotation.name()};
    }

    @Override
    public void accept(Numeric annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation).map(NumericArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(annotation).map(NumericArgumentProvider::generateParam);
    }
}
