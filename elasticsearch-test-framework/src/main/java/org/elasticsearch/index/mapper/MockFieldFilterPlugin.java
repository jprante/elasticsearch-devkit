package org.elasticsearch.index.mapper;

import org.elasticsearch.plugins.MapperPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.function.Function;
import java.util.function.Predicate;

public class MockFieldFilterPlugin extends Plugin implements MapperPlugin {

    @Override
    public Function<String, Predicate<String>> getFieldFilter() {
        //this filter doesn't filter any field out, but it's used to exercise the code path executed when the filter is not no-op
        return index -> field -> true;
    }
}
