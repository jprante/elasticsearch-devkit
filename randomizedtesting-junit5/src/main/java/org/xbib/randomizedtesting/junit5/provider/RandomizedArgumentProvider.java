package org.xbib.randomizedtesting.junit5.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.xbib.randomizedtesting.junit5.Utils;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

abstract class RandomizedArgumentProvider<T extends Annotation> implements ArgumentsProvider, AnnotationConsumer<T> {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Utils.setCurrentSeedIfNotSetYet(context);
        if (Utils.passCaseNameToTestMethod(context)) {
            return getValueWithDescription().map(Arguments::of);
        }
        return getValue().map(Arguments::of);
    }

    abstract Stream<Object[]> getValueWithDescription();

    abstract Stream<Object> getValue();
}
