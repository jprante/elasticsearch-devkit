package org.xbib.randomizedtesting;

import java.util.Random;

public class ThreadedRandom extends Random {

    private static final long multiplier = 0x5DEECE66DL;

    private static final long addend = 0xBL;

    private static final long mask = (1L << 48) - 1;

    private static final ThreadLocal<Long> SEEDS = ThreadLocal.withInitial(System::nanoTime);

    public static void overrideSeed(long seed) {
        SEEDS.set(seed);
    }

    public static long getCurrentSeed() {
        return SEEDS.get();
    }

    @Override
    public int next(int bits) {
        long oldseed = getCurrentSeed(), nextseed;
        do {
            nextseed = (oldseed * multiplier + addend) & mask;
        } while (oldseed == nextseed);
        super.setSeed(nextseed);
        overrideSeed(nextseed);
        return (int) (nextseed >>> (48 - bits));
    }
}
