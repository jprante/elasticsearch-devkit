package org.elasticsearch.test.test;

import com.carrotsearch.randomizedtesting.annotations.Repeat;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.TestCluster;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

/**
 * This test ensures that the cluster initializion for suite scope is not influencing
 * the tests random sequence due to initializtion using the same random instance.
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
public class SuiteScopeClusterIT extends ESIntegTestCase {
    private static int ITER = 0;
    private static long[] SEQUENCE = new long[100];
    private static Long CLUSTER_SEED = null;

    @Test
    @SuppressForbidden(reason = "repeat is a feature here")
    @Repeat(iterations = 10, useConstantSeed = true)
    public void testReproducible() throws IOException {
        if (ITER++ == 0) {
            CLUSTER_SEED = cluster().seed();
            for (int i = 0; i < SEQUENCE.length; i++) {
                SEQUENCE[i] = randomLong();
            }
        } else {
            assertEquals(CLUSTER_SEED, Long.valueOf(cluster().seed()));
            for (int i = 0; i < SEQUENCE.length; i++) {
                assertThat(SEQUENCE[i], equalTo(randomLong()));
            }
        }
    }

    @Override
    protected TestCluster buildTestCluster(Scope scope, long seed) throws IOException {
        // produce some randomness
        int iters = between(1, 100);
        for (int i = 0; i < iters; i++) {
            randomLong();
        }
        return super.buildTestCluster(scope, seed);
    }
}