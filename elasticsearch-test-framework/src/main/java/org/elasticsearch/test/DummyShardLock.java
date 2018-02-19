package org.elasticsearch.test;

import org.elasticsearch.env.ShardLock;
import org.elasticsearch.index.shard.ShardId;

/*
 * A ShardLock that does nothing... for tests only
 */
public class DummyShardLock extends ShardLock {

    public DummyShardLock(ShardId id) {
        super(id);
    }

    @Override
    protected void closeInternal() {
    }
}
