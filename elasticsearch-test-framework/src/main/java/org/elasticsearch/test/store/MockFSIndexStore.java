package org.elasticsearch.test.store;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.logging.ServerLoggers;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Setting.Property;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.shard.IndexEventListener;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.index.shard.IndexShardState;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.index.shard.ShardPath;
import org.elasticsearch.index.store.DirectoryService;
import org.elasticsearch.index.store.IndexStore;
import org.elasticsearch.plugins.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class MockFSIndexStore extends IndexStore {

    public static final Setting<Boolean> INDEX_CHECK_INDEX_ON_CLOSE_SETTING =
        Setting.boolSetting("index.store.mock.check_index_on_close", true, Property.IndexScope, Property.NodeScope);

    public static class TestPlugin extends Plugin {
        @Override
        public Settings additionalSettings() {
            return Settings.builder().put(IndexModule.INDEX_STORE_TYPE_SETTING.getKey(), "mock").build();
        }

        @Override
        public List<Setting<?>> getSettings() {
            return Arrays.asList(INDEX_CHECK_INDEX_ON_CLOSE_SETTING,
            MockFSDirectoryService.CRASH_INDEX_SETTING,
            MockFSDirectoryService.RANDOM_IO_EXCEPTION_RATE_SETTING,
            MockFSDirectoryService.RANDOM_PREVENT_DOUBLE_WRITE_SETTING,
            MockFSDirectoryService.RANDOM_NO_DELETE_OPEN_FILE_SETTING,
            MockFSDirectoryService.RANDOM_IO_EXCEPTION_RATE_ON_OPEN_SETTING);
        }

        @Override
        public void onIndexModule(IndexModule indexModule) {
            Settings indexSettings = indexModule.getSettings();
            if ("mock".equals(indexSettings.get(IndexModule.INDEX_STORE_TYPE_SETTING.getKey()))) {
                if (INDEX_CHECK_INDEX_ON_CLOSE_SETTING.get(indexSettings)) {
                    indexModule.addIndexEventListener(new Listener());
                }
                indexModule.addIndexStore("mock", MockFSIndexStore::new);
            }
        }
    }

    MockFSIndexStore(IndexSettings indexSettings) {
        super(indexSettings);
    }

    public DirectoryService newDirectoryService(ShardPath path) {
        return new MockFSDirectoryService(indexSettings, this, path);
    }

    private static final EnumSet<IndexShardState> validCheckIndexStates = EnumSet.of(
            IndexShardState.STARTED, IndexShardState.RELOCATED, IndexShardState.POST_RECOVERY
    );
    private static final class Listener implements IndexEventListener {

        private final Map<IndexShard, Boolean> shardSet = Collections.synchronizedMap(new IdentityHashMap<>());
        @Override
        public void afterIndexShardClosed(ShardId shardId, @Nullable IndexShard indexShard, Settings indexSettings) {
            if (indexShard != null) {
                Boolean remove = shardSet.remove(indexShard);
                if (remove == Boolean.TRUE) {
                    Logger logger = ServerLoggers.getLogger(getClass(), indexShard.indexSettings().getSettings(),
                            indexShard.shardId());
                    MockFSDirectoryService.checkIndex(logger, indexShard.store(), indexShard.shardId());
                }
            }
        }

        @Override
        public void indexShardStateChanged(IndexShard indexShard, @Nullable IndexShardState previousState,
                                           IndexShardState currentState, @Nullable String reason) {
            if (currentState == IndexShardState.CLOSED && validCheckIndexStates.contains(previousState)) {
               shardSet.put(indexShard, Boolean.TRUE);
            }

        }
    }

}
