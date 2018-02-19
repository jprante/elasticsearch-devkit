package org.elasticsearch.index.translog;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;

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
