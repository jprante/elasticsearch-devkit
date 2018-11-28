package org.elasticsearch.testframework.env;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

/**
 * Provides a convenience method for tests to construct an Environment when the config path does not matter.
 * This is in the test framework to force people who construct an Environment in production code to think
 * about what the config path needs to be set to.
 */
public class TestEnvironment {

    private TestEnvironment() {
    }

    public static Environment newEnvironment(Settings settings) {
        return new Environment(settings, null);
    }
}
