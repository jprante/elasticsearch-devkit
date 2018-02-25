package org.elasticsearch.analysis.common;

import org.elasticsearch.Version;
import org.elasticsearch.common.logging.DeprecationLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class LegacyDelimitedPayloadTokenFilterFactory extends DelimitedPayloadTokenFilterFactory {
    private static final DeprecationLogger DEPRECATION_LOGGER =
        new DeprecationLogger(Loggers.getLogger(LegacyDelimitedPayloadTokenFilterFactory.class));

    LegacyDelimitedPayloadTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, env, name, settings);
        if (indexSettings.getIndexVersionCreated().onOrAfter(Version.V_6_2_0)) {
            DEPRECATION_LOGGER.deprecated("Deprecated [delimited_payload_filter] used, replaced by [delimited_payload]");
        }
    }
}
