package org.elasticsearch.testframework.junit.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation used to set a custom log level for a specific test method.
 *
 * It supports multiple logger:level comma separated key value pairs
 * Use the _root keyword to set the root logger level
 * e.g. @TestLogging("_root:DEBUG,org.elasticsearch.cluster.metadata:TRACE")
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PACKAGE, TYPE, METHOD})
public @interface TestLogging {
    String value();
}
