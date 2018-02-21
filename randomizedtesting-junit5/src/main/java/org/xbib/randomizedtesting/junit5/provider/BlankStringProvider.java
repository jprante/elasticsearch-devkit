package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.BlankString;

import java.util.stream.Stream;

import static org.xbib.randomizedtesting.RandomApi.nullOrBlank;

public class BlankStringProvider extends RandomizedArgumentProvider<BlankString> {
    private BlankString annotation;

    static String generateParam() {
        return nullOrBlank();
    }

    static Object[] generateParams(BlankString annotation) {
        String randomValue = generateParam();
        String name = annotation.name();
        if (name.isEmpty()) {
            if (randomValue == null) name = "null";
            else if (randomValue.isEmpty()) name = "empty string";
            else name = "string with spaces only";
        }
        return new Object[]{randomValue, name};
    }

    @Override
    public void accept(BlankString annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Stream.of(annotation)
                .map(BlankStringProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Stream.of(generateParam());
    }
}
