package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.Alphanumeric;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.alphanumeric;

public class AlphanumericArgumentProvider extends RandomizedArgumentProvider<Alphanumeric> {
    private Alphanumeric annotation;

    static String generateParam(Alphanumeric annotation) {
        if (annotation.length() > 0) return alphanumeric(annotation.length());
        else return alphanumeric(annotation.min(), annotation.max());
    }

    static Object[] generateParams(Alphanumeric annotation) {
        return new Object[]{generateParam(annotation), annotation.name()};
    }

    @Override
    public void accept(Alphanumeric annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation).map(AlphanumericArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(annotation).map(AlphanumericArgumentProvider::generateParam);
    }
}
