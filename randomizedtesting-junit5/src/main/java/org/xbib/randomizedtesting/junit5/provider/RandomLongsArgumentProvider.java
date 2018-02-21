package org.xbib.randomizedtesting.junit5.provider;

import org.xbib.randomizedtesting.junit5.annotation.RandomLongs;

import java.util.Arrays;
import java.util.stream.Stream;

public class RandomLongsArgumentProvider extends RandomizedArgumentProvider<RandomLongs> {
    private RandomLongs annotation;

    @Override
    public void accept(RandomLongs annotation) {
        this.annotation = annotation;
    }

    @Override
    Stream<Object[]> getValueWithDescription() {
        return Arrays.stream(annotation.value()).map(RandomLongArgumentProvider::generateParams);
    }

    @Override
    Stream<Object> getValue() {
        return Arrays.stream(annotation.value()).map(RandomLongArgumentProvider::generateParam);
    }
}
