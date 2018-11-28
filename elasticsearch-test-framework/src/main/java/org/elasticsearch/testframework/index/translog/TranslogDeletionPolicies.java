package org.elasticsearch.testframework.index.translog;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.translog.TranslogDeletionPolicy;

public class TranslogDeletionPolicies {

    public static TranslogDeletionPolicy createTranslogDeletionPolicy() {
        return new TranslogDeletionPolicy(
                IndexSettings.INDEX_TRANSLOG_RETENTION_SIZE_SETTING.getDefault(Settings.EMPTY).getBytes(),
                IndexSettings.INDEX_TRANSLOG_RETENTION_AGE_SETTING.getDefault(Settings.EMPTY).getMillis()
        );
    }

    public static TranslogDeletionPolicy createTranslogDeletionPolicy(IndexSettings indexSettings) {
        return new TranslogDeletionPolicy(indexSettings.getTranslogRetentionSize().getBytes(),
                indexSettings.getTranslogRetentionAge().getMillis());
    }

}
