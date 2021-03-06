package org.elasticsearch.test.test;

import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.TestCluster;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

/**
 * This test ensures that the cluster initializion for TEST scope is not influencing
 * the tests random sequence due to initializtion using the same random instance.
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST)
public class TestScopeClusterIT extends ESIntegTestCase {
    private static int ITER = 0;
    private static long[] SEQUENCE = new long[100];
    private static Long CLUSTER_SEED = null;

    public void testReproducible() throws IOException {
        if (ITER++ == 0) {
            CLUSTER_SEED = cluster().seed();
            for (int i = 0; i < SEQUENCE.length; i++) {
                SEQUENCE[i] = randomLong();
            }
        } else {
            assertEquals(CLUSTER_SEED, Long.valueOf(cluster().seed()));
            for (long aSEQUENCE : SEQUENCE) {
                assertThat(aSEQUENCE, equalTo(randomLong()));
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
