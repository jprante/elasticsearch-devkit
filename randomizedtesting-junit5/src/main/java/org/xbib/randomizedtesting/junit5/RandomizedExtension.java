package org.xbib.randomizedtesting.junit5;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * Logs the seeds of the failed methods so that it's possible to re-run tests with the same data generated.
 * Unfortunately right now
 * <a href="https://github.com/junit-team/junit5/issues/618">JUnit5 doesn't have means to know which test failed</a>
 * and which passed, so currently the seed is printed for each test method that threw exception.
 */
public class RandomizedExtension implements BeforeTestExecutionCallback, BeforeAllCallback, TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        context.getTestMethod().ifPresent((m) -> Utils.logCurrentSeeds(context));
        throw throwable;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent((m) -> Utils.setCurrentSeedIfNotSetYet(context));
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        context.getTestClass().ifPresent((m) -> Utils.setCurrentSeedIfNotSetYet(context));
    }
}
