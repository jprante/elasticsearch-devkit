package org.xbib.randomizedtesting;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.xbib.randomizedtesting.RandomElements.from;
import static org.xbib.randomizedtesting.RandomValue.RANDOM;
import static org.xbib.randomizedtesting.RandomValue.SPECIAL_SYMBOLS;
import static org.xbib.randomizedtesting.RandomValue.between;
import static org.xbib.randomizedtesting.RandomValue.length;

/**
 * If you need a more flexible way of generating the values,
 * including different string modifiers like prefixes and
 * suffixes, use {@link RandomValue}.
 */
public class RandomApi {

    private RandomApi() {
    }

    /**
     * @param max inclusive, must be greater than or equal to 0
     * @return integer between 0 (inclusive) to {@code max}
     */
    public static int generateInteger(int max) {
        return between(0, max).integer();
    }

    /**
     * @param min inclusive, must be less than or equal to {@code max}
     * @param max inclusive, must be greater than or equal to {@code min}
     * @return integer from {@code min} to {@code max}
     */
    public static int generateInteger(int min, int max) {
        return between(min, max).integer();
    }

    public static int generateInteger() {
        return between(Integer.MIN_VALUE, Integer.MAX_VALUE).integer();
    }

    /**
     * @return value between 0 (inclusive) and {@link Integer#MAX_VALUE} (inclusive)
     */
    public static int generatePositiveInteger() {
        return between(0, Integer.MAX_VALUE).integer();
    }

    /**
     * @return value between {@link Long#MIN_VALUE} (inclusive) and {@link Long#MAX_VALUE} (inclusive)
     */
    public static long generateLong() {
        return between(Long.MIN_VALUE, Long.MAX_VALUE).Long();
    }

    public static double generateDouble() {
        return nextUniform(RANDOM, Long.MIN_VALUE, Long.MAX_VALUE, true);
    }

    /**
     * Creates double that's {@code >=} 0.
     *
     * @return double that's {@code >=} 0
     */
    public static double generatePositiveDouble() {
        return generateDouble(Long.MAX_VALUE);
    }

    public static double generateNegativeDouble() {
        return generateDouble(Long.MIN_VALUE, 0);
    }

    public static double generateDouble(double max) {
        return nextUniform(RANDOM, 0, max, true);
    }

    /**
     * Returns double that's greater than what was specified.
     *
     * @param from lower boundary
     * @return double that's greater than what was specified
     */
    public static double greaterDouble(double from) {//todo: not released!
        return generateDouble(from + Double.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Returns random double.
     *
     * @param min min boundary (inclusive), cannot be equal to {@code max}
     * @param max max boundary (exclusive), cannot be equal to {@code min}
     * @return uniformly distributed random double between 2 boundaries
     */
    public static double generateDouble(double min, double max) {
        return nextUniform(RANDOM, min, max, true);
    }

    /**
     * Long between 0 to {@link Long#MAX_VALUE} inclusive.
     *
     * @return between 0 to {@link Long#MAX_VALUE} inclusive.
     */
    public static long generatePositiveLong() {
        return generateLong(0, Long.MAX_VALUE);
    }

    /**
     * Long between {@link Long#MIN_VALUE} to -1
     *
     * @return between {@link Long#MIN_VALUE} to -1
     */
    public static long generateNegativeLong() {
        return generateLong(Long.MIN_VALUE, -1);
    }

    /**
     * @param min inclusive
     * @param max inclusive
     * @return a long from min boundary to max
     */
    public static long generateLong(long min, long max) {
        return between(min, max).Long();
    }

    public static String alphanumeric(int exactLength) {
        return length(exactLength).alphanumeric();
    }

    public static String alphanumeric(int min, int max) {
        return between(min, max).alphanumeric();
    }

    public static String numeric(int exactLength) {
        return length(exactLength).numeric();
    }

    public static String numeric(int min, int max) {
        return between(min, max).numeric();
    }

    public static String english(int exactLength) {
        return length(exactLength).english();
    }

    public static String english(int min, int max) {
        return between(min, max).english();
    }

    /**
     * Generates unicode string of variable length that includes characters from different languages, weird symbols
     * and <a href="https://docs.oracle.com/javase/tutorial/i18n/text/supplementaryChars.html">Supplementary Characters</a>
     * that are comprised of multiple chars.
     *
     * @param exactLength length of the string to be returned
     * @return unicode characters including different languages and weird symbols
     */
    public static String unicode(int exactLength) {
        return length(exactLength).unicode();
    }

    /**
     * Generates unicode string of variable length that includes characters from different languages, weird symbols
     * and <a href="https://docs.oracle.com/javase/tutorial/i18n/text/supplementaryChars.html">Supplementary Characters</a>
     * that are comprised of multiple chars.
     *
     * @param min min boundary of the string length
     * @param max max boundary of the string length
     * @return unicode characters including different languages and weird symbols
     */
    public static String unicode(int min, int max) {
        return between(min, max).unicode();
    }

    /**
     * @see RandomString#unicodeWithoutBoundarySpaces()
     */
    public static String unicodeWithoutBoundarySpaces(int exactLength) {
        return length(exactLength).unicodeWithoutBoundarySpaces();
    }

    /**
     * @see RandomString#unicodeWithoutBoundarySpaces()
     */
    public static String unicodeWithoutBoundarySpaces(int min, int max) {
        return between(min, max).unicodeWithoutBoundarySpaces();
    }

    public static String specialSymbols() {
        return SPECIAL_SYMBOLS;
    }

    public static String specialSymbols(int exactLength) {
        return length(exactLength).specialSymbols();
    }

    public static String specialSymbols(int min, int max) {
        return between(min, max).specialSymbols();
    }

    /**
     * Returns an array of random booleans (true/false).
     *
     * @param n size of the resulting array
     * @return an array of random booleans
     */
    public static boolean[] bools(int n) {
        boolean[] result = new boolean[n];
        for (int i = 0; i < n; i++) {
            result[i] = bool();
        }
        return result;
    }

    public static boolean bool() {
        return RANDOM.nextBoolean();
    }

    /**
     * Returns true with the specified probability.
     *
     * @param probabilityOfTrue the probability that true is to be returned, must be between 0 and 1
     * @return true with the specified probability. Always returns true if 1 is passed and always false if 0 is passed.
     */
    public static boolean weighedTrue(double probabilityOfTrue) {
        if (probabilityOfTrue == 0.0) {
            return false;
        }
        if (probabilityOfTrue == 1.0) {
            return true;
        }
        return probabilityOfTrue >= RANDOM.nextDouble();
    }

    /**
     * Besides returning TRUE or FALSE it can also return {@code null}.
     *
     * @return TRUE, FALSE or {@code null}
     */
    public static Boolean nullableBool() {
        return from(Boolean.TRUE, Boolean.FALSE, null).sample();
    }

    /**
     * Returns random element from the collection.
     *
     * @param toSampleFrom retrieve random element from
     * @return a random element from the collection
     * @see RandomElements
     */
    public static <T> T sample(Collection<T> toSampleFrom) {
        return from(toSampleFrom).sample();
    }

    /**
     * Returns random element from the collection.
     *
     * @param toSampleFrom the population of the elements you'd like to get a random value from
     * @return a random element from the collection
     * @see RandomElements
     */
    @SafeVarargs
    public static <T> T sample(T... toSampleFrom) {
        return from(toSampleFrom).sample();
    }

    /**
     * Returns either null or the specified object.
     *
     * @param obj object to return in 50% of cases
     * @param <T> type of the specified object
     * @return null or the specified object with the 50/50 odds
     */
    public static <T> T nullOr(T obj) {
        return sample(obj, null);
    }

    /**
     * Returns either null or empty string with the 50/50 odds.
     *
     * @return either null or empty string
     */
    public static String nullOrEmpty() {
        return sample("", null);
    }

    /**
     * Returns either null or empty string or string with only spaces each with equal chance.
     *
     * @return either null or empty string or string with whitespaces only
     */
    public static String nullOrBlank() {
        return sample("", between(1, 100).string(' '), null);
    }

    /**
     * Returns either the specified string or null or empty string or string with only spaces each with equal chance.
     *
     * @return either the specified string or null or empty string or string with whitespaces only
     */
    public static String blankOr(String string) {
        return sample(nullOrBlank(), string);
    }

    /**
     * Returns a random element from the collection. Is used in case you have a collection and then couple of other
     * elements you want to sample from too, but you don't want to create a collection that includes all of them
     * combined.
     *
     * @param elements the main collection to sample from
     * @param others   other elements you'd like to include into population to sample from
     * @return a random element from all the listed elements/other elements
     * @see RandomElements
     */
    @SafeVarargs
    public static <T> T sample(Collection<T> elements, T... others) {
        return from(elements, others).sample();
    }

    /**
     * Returns multiple random elements from the specified collection.
     *
     * @param toSampleFrom the population of the elements you'd like to get a random value from
     * @return 0 or more elements of the specified collection, elements don't repeat
     */
    public static <T> List<T> sampleMultiple(Collection<T> toSampleFrom) {
        return sampleMultiple(generateInteger(toSampleFrom.size()), toSampleFrom);
    }

    /**
     * Returns multiple random elements from the specified collection.
     *
     * @param toSampleFrom the population of the elements you'd like to get a random value from
     * @return a random element from the collection
     */
    public static <T> Set<T> sampleMultiple(Set<T> toSampleFrom) {
        return new HashSet<T>(sampleMultiple((Collection<T>) toSampleFrom));
    }

    /**
     * Returns multiple random elements from the specified collection.
     *
     * @param toSampleFrom the population of the elements you'd like to get a random value from
     * @param nToReturn    number of elements to be returned, must be smaller than the collection size
     * @return list of size {@code nToReturn} - contains random elements from the specified collection
     */
    public static <T> List<T> sampleMultiple(int nToReturn, Collection<T> toSampleFrom) {
        return from(toSampleFrom).sample(nToReturn);
    }

    /**
     * Returns random element from the collection.
     *
     * @param toSampleFrom the population of the elements you'd like to get a random value from
     * @param nToReturn    number of elements to be returned, must be smaller than the collection size
     * @return list of size {@code nToReturn} - contains random elements from the specified array
     */
    @SafeVarargs
    public static <T> List<T> sampleMultiple(int nToReturn, T... toSampleFrom) {
        return from(toSampleFrom).sample(nToReturn);
    }

    /**
     * Invokes one and only one of the specified functions. This is an API for Java8 Lambdas.
     *
     * @param functions functions to choose from for invocation
     */
    public static <T, R> void applyOneOf(T t, Function<T, R>... functions) {
        sample(functions).apply(t);
    }

    /**
     * Invokes one and only one of the specified functions. This is an API for Java8 Lambdas.
     *
     * @param functions functions to choose from for invocation
     */
    public static void applyOneOf(Runnable... functions) {
        sample(functions).run();
    }

    /**
     * May invoke 0, 1 or more functions from the specified list.
     *
     * @param functions functions to choose from for invocation
     */
    public static <T, R> void applyNoneOrMore(T t, Function<T, R>... functions) {
        List<Function<T, R>> toCall = sampleMultiple(generateInteger(functions.length), functions);
        for (Function<T, R> function : toCall) {
            function.apply(t);
        }
    }

    /**
     * May invoke 0, 1 or more functions from the specified list.
     *
     * @param functions functions to choose from for invocation
     */
    public static void applyNoneOrMore(Runnable... functions) {
        List<Runnable> toCall = sampleMultiple(generateInteger(functions.length), functions);
        for (Runnable function : toCall) {
            function.run();
        }
    }

    /**
     * Invokes one or more of the specified functions.
     *
     * @param functions functions to choose from for invocation
     */
    public static <T, R> void applyOneOrMore(T t, Function<T, R>... functions) {
        List<Function<T, R>> toCall = sampleMultiple(generateInteger(1, functions.length), functions);
        for (Function<T, R> function : toCall) {
            function.apply(t);
        }
    }

    /**
     * Invokes one or more of the specified functions.
     *
     * @param functions functions to choose from for invocation
     */
    public static void applyOneOrMore(Runnable... functions) {
        List<Runnable> toCall = sampleMultiple(generateInteger(1, functions.length), functions);
        for (Runnable function : toCall) {
            function.run();
        }
    }

    public static double nextUniform(ThreadedRandom random, double lower, double upper, boolean lowerInclusive) throws IllegalArgumentException {
        if (lower >= upper) {
            throw new IllegalArgumentException("lower bound " + lower + " must be strictly less than upper bound " + upper);
        }
        if (Double.isInfinite(lower)) {
            throw new IllegalArgumentException("interval bounds must be finite " + lower);
        }
        if (Double.isInfinite(upper)) {
            throw new IllegalArgumentException("interval bounds must be finite " + upper);
        }
        if (Double.isNaN(lower) || Double.isNaN(upper)) {
            throw new IllegalArgumentException("Not-a-number was specified");
        }
        // ensure nextDouble() isn't 0.0
        double u = nextDouble(random);
        while (!lowerInclusive && u <= 0.0) {
            u = nextDouble(random);
        }
        return u * upper + (1.0 - u) * lower;
    }

    private static double nextDouble(ThreadedRandom random) {
        final long high = ((long) random.next(26)) << 26;
        final int low = random.next(26);
        return (high | low) * 0x1.0p-52d;
    }
}
