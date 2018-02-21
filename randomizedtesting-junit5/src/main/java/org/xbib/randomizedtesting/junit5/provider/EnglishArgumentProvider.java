package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.English;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.english;

public class EnglishArgumentProvider extends RandomizedArgumentProvider<English> {
    private English annotation;

    static String generateParam(English annotation) {
        if (annotation.length() > 0) return english(annotation.length());
        return english(annotation.min(), annotation.max());
    }

    static Object[] generateParams(English annotation) {
        return new Object[]{generateParam(annotation), annotation.name()};
    }

    @Override
    public void accept(English annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation).map(EnglishArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(annotation).map(EnglishArgumentProvider::generateParam);
    }
}
