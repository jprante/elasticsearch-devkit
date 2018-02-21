package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.Unicodes;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnicodesArgumentProvider extends RandomizedArgumentProvider<Unicodes> {
    private Unicodes annotation;

    @Override
    public void accept(Unicodes annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Arrays.stream(annotation.value()).map(UnicodeArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Arrays.stream(annotation.value()).map(UnicodeArgumentProvider::generateParam);
    }
}
