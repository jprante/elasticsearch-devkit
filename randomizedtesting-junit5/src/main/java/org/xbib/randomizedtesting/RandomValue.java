package org.xbib.randomizedtesting;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class RandomValue implements RandomString {

    /**
     * Random object used by random method. This has to be not local
     * to the random method so as to not return the same value in the
     * same millisecond.
     */
    static final ThreadedRandom RANDOM = new ThreadedRandom();

    public static String SPECIAL_SYMBOLS = "!@#$%^&*()_+{}[]'\"|:?><~`§\\,/;.";

    private final List<StringModifier> modifiers = new CopyOnWriteArrayList<StringModifier>();

    private final Long min;

    private final Long max;

    private RandomValue(Long min, Long max) {
        if (max < min) {
            throw new IllegalArgumentException("Min [" + min + "] cannot be larger than max [" + max + "]");
        }
        this.min = min;
        this.max = max;
    }

    public static RandomValue between(long from, long to) {
        return new RandomValue(from, to);
    }

    public static RandomValue fromZeroTo(long to) {
        return new RandomValue(0L, to);
    }

    public static RandomValue fromOneTo(long to) {
        return new RandomValue(1L, to);
    }

    public static RandomValue fromZeroTo(ZonedDateTime to) {
        return new RandomValue(0L, to.toInstant().toEpochMilli());
    }

    public static RandomValue between(ZonedDateTime from, ZonedDateTime to) {
        return new RandomValue(from.toInstant().toEpochMilli(), to.toInstant().toEpochMilli());
    }

    public static RandomValue length(long length) {
        return new RandomValue(length, length);
    }

    /**
     * See {@link RandomString#unicodeWithoutBoundarySpaces()}.
     */
    private static boolean isWhitespace(int codePoint) {
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint) || codePoint < ' ';
    }

    /**
     * Generates a uniformly distributed random integer between {@code lower}
     * and {@code upper} (endpoints included).
     * The generated integer will be random, but not cryptographically secure. </p>
     *
     * @return a random integer greater than or equal to {@code lower}
     * and less than or equal to {@code upper}
     * @throws NumberOutOfBoundaryException if previously specified boundaries are greater/smaller than integer
     *                                      boundaries
     */
    private static int nextInt(Random random, int upper, int lower) {
        int max = (upper - lower) + 1;
        if (max <= 0) {
            // The range is too wide to fit in a positive int (larger
            // than 2^31); as it covers more than half the integer range,
            // we use a simple rejection method.
            while (true) {
                int r = random.nextInt();
                if (r >= lower && r <= upper) return r;
            }
        } else {
            // We can shift the range and directly generate a positive int.
            return lower + random.nextInt(max);
        }
    }

    /**
     * Generates a uniformly distributed random long integer between {@code lower}
     * and {@code upper} (endpoints included).
     *
     * @param lower lower bound for generated long integer
     * @param upper upper bound for generated long integer
     * @return a random long integer greater than or equal to {@code lower}
     * and less than or equal to {@code upper}
     */
    private static long nextLong(Random random, final long lower, final long upper) {
        if (lower == upper) return lower;
        if (lower > upper) throw new IllegalArgumentException();
        final long max = (upper - lower) + 1;
        if (max <= 0) {
            // the range is too wide to fit in a positive long (larger than 2^63); as it covers
            // more than half the long range, we use directly a simple rejection method
            while (true) {
                final long r = random.nextLong();
                if (r >= lower && r <= upper) {
                    return r;
                }
            }
        } else if (max < Integer.MAX_VALUE) {
            // we can shift the range and generate directly a positive int
            return lower + random.nextInt((int) max);
        } else {
            // we can shift the range and generate directly a positive long
            return lower + nextLong(random, max);
        }
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.
     *
     * @param rng random generator to use
     * @param n   the bound on the random number to be returned.  Must be
     *            positive.
     * @return a pseudorandom, uniformly distributed {@code long}
     * value between 0 (inclusive) and n (exclusive).
     * @throws IllegalArgumentException if n is not positive.
     */
    private static long nextLong(final Random rng, final long n) throws IllegalArgumentException {
        if (n > 0) {
            final byte[] byteArray = new byte[8];
            long bits;
            long val;
            do {
                rng.nextBytes(byteArray);
                bits = 0;
                for (final byte b : byteArray) {
                    bits = (bits << 8) | (((long) b) & 0xffL);
                }
                bits &= 0x7fffffffffffffffL;
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }
        throw new IllegalStateException("Not strictly positive" + n);
    }

    public RandomValue with(StringModifier... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
        return this;
    }

    @Override
    public String alphanumeric() {
        throwIfLowerBoundaryIsNegative();
        return applyStringModifiers(randomAlphanumeric(integer()));
    }

    @Override
    public List<String> alphanumerics() {
        return alphanumerics(between(1, 100).integer());
    }

    @Override
    public List<String> alphanumerics(int nOfElements) {
        throwIfLowerBoundaryIsNegative();
        List<String> result = new ArrayList<String>(nOfElements);
        for (int i = 0; i < nOfElements; i++) {
            result.add(randomAlphanumeric(integer()));
        }
        return applyStringModifiers(result);
    }

    @Override
    public List<String> alphanumerics(int minNOfElements, int maxNOfElements) {
        throwIfLowerBoundaryIsNegative();
        int n = between(minNOfElements, maxNOfElements).integer();
        return alphanumerics(n);
    }

    @Override
    public String numeric() {
        throwIfLowerBoundaryIsNegative();
        return applyStringModifiers(randomNumeric(integer()));
    }

    @Override
    public List<String> numerics() {
        return numerics(between(1, 100).integer());
    }

    @Override
    public List<String> numerics(int nOfElements) {
        throwIfLowerBoundaryIsNegative();
        List<String> result = new ArrayList<String>(nOfElements);
        for (int i = 0; i < nOfElements; i++) {
            result.add(randomNumeric(integer()));
        }
        return applyStringModifiers(result);
    }

    @Override
    public String english() {
        throwIfLowerBoundaryIsNegative();
        return applyStringModifiers(randomAlphabetic(integer()));
    }

    @Override
    public String specialSymbols() {
        throwIfLowerBoundaryIsNegative();
        return applyStringModifiers(random(integer(), SPECIAL_SYMBOLS.toCharArray()));
    }

    @Override
    public String unicode() {
        throwIfLowerBoundaryIsNegative();
        return applyStringModifiers(random(integer()));
    }

    @Override
    public String unicodeWithoutBoundarySpaces() {
        String s = applyStringModifiers(random(integer()));
        int lastIndex = s.length() - 1;
        if (isWhitespace(s.charAt(0)) || isWhitespace(s.charAt(lastIndex))) {
            char[] characters = s.toCharArray();
            if (Character.isWhitespace(characters[0])) characters[0] = length(1).alphanumeric().charAt(0);
            if (Character.isWhitespace(characters[lastIndex]))
                characters[lastIndex] = length(1).alphanumeric().charAt(0);
            s = String.valueOf(characters);
        }
        return s;
    }

    @Override
    public String string(char... vocabulary) {
        if (vocabulary == null || vocabulary.length == 0)
            throw new IllegalArgumentException("You cannot generate string from an empty vocabulary. Either pass " +
                    "the symbols the string is to be generated from, or use methods like alphanumerics(), unicode()," +
                    " etc.");
        throwIfLowerBoundaryIsNegative();
        return applyStringModifiers(random(integer(), vocabulary));
    }

    @Override
    public String string(String vocabulary) {
        return string(vocabulary.toCharArray());
    }

    private String applyStringModifiers(String value) {
        String result = value;
        for (StringModifier modifier : modifiers) {
            result = modifier.modify(result);
        }
        return result;
    }

    private List<String> applyStringModifiers(List<String> value) {
        List<String> result = value;
        for (StringModifier modifier : modifiers) {
            result = modifier.modify(result);
        }
        return result;
    }

    private void throwIfLowerBoundaryIsNegative() {
        if (min < 0) throw new NumberOutOfBoundaryException("String length cannot be less than 0:" + min);
    }

    /**
     * Generates a uniformly distributed random integer between {@code lower}
     * and {@code upper} (endpoints included).
     * The generated integer will be random, but not cryptographically secure.
     *
     * @return a random integer greater than or equal to {@code lower}
     * and less than or equal to {@code upper}
     * @throws NumberOutOfBoundaryException if previously specified boundaries are greater/smaller than integer
     *                                      boundaries
     */
    public int integer() {
        return nextInt(RANDOM, maxInt(), minInt());
    }

    /**
     * Returns any Long from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE}.
     *
     * @return long from {@link Long#MIN_VALUE} to {@link Long#MAX_VALUE}
     */
    public long Long() {
        return nextLong(RANDOM, min, max);
    }

    public ZonedDateTime zonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long()), ZoneId.systemDefault());
    }

    public ZonedDateTime zonedDateTime(ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long()), zoneId);
    }

    private int maxInt() {
        if (max > Integer.MAX_VALUE) {
            throw new NumberOutOfBoundaryException("The number was expected to be integer, but it's too large:" + max);
        }
        return max.intValue();
    }

    private int minInt() {
        if (min < Integer.MIN_VALUE) {
            throw new NumberOutOfBoundaryException("The number was expected to be integer, but it's too small:" + min);
        }
        return min.intValue();
    }

    /*
     * Operations for random {@code String}s.
     * Currently <em>private high surrogate</em> characters are ignored.
     * These are Unicode characters that fall between the values 56192 (db80)
     * and 56319 (dbff) as we don't know how to handle them.
     * High and low surrogates are correctly dealt with - that is if a
     * high surrogate is randomly chosen, 55296 (d800) to 56191 (db7f)
     * then it is followed by a low surrogate. If a low surrogate is chosen,
     * 56320 (dc00) to 57343 (dfff) then it is placed after a randomly
     * chosen high surrogate.
     */

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of all characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String random(final int count) {
        return random(count, false, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of characters whose
     * ASCII value is between {@code 32} and {@code 126} (inclusive).</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAscii(final int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of alphabetic
     * characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphabetic(final int count) {
        return random(count, true, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphanumeric(final int count) {
        return random(count, true, true);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of numeric
     * characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomNumeric(final int count) {
        return random(count, false, true);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters as indicated by the arguments.</p>
     *
     * @param count   the length of random string to create
     * @param letters if {@code true}, generated string may include
     *                alphabetic characters
     * @param numbers if {@code true}, generated string may include
     *                numeric characters
     * @return the random string
     */
    public static String random(final int count, final boolean letters, final boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p>
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters as indicated by the arguments.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters if {@code true}, generated string may include
     *                alphabetic characters
     * @param numbers if {@code true}, generated string may include
     *                numeric characters
     * @return the random string
     */
    public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    /**
     * <p>Creates a random string based on a variety of options, using
     * default source of randomness.</p>
     * <p>
     * <p>This method has exactly the same semantics as
     * {@link #random(int, int, int, boolean, boolean, char[], Random)}, but
     * instead of using an externally supplied source of randomness, it uses
     * the internal static {@link Random} instance.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters only allow letters?
     * @param numbers only allow numbers?
     * @param chars   the set of chars to choose randoms from.
     *                If {@code null}, then it will use the set of all chars.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *                                        {@code (end - start) + 1} characters in the set array.
     */
    public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers, final char... chars) {
        return random(count, start, end, letters, numbers, chars, RANDOM);
    }

    /**
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     * <p>
     * <p>If start and end are both {@code 0}, start and end are set
     * to {@code ' '} and {@code 'z'}, the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * {@code false}, in which case, start and end are set to
     * {@code 0} and {@code Integer.MAX_VALUE}.
     * <p>
     * <p>If set is not {@code null}, characters between start and
     * end are chosen.</p>
     * <p>
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single
     * {@link Random} instance with a fixed seed and using it for each call,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters only allow letters?
     * @param numbers only allow numbers?
     * @param chars   the set of chars to choose randoms from, must not be empty.
     *                If {@code null}, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *                                        {@code (end - start) + 1} characters in the set array.
     * @throws IllegalArgumentException       if {@code count} &lt; 0 or the provided chars array is empty.
     */
    public static String random(int count, int start, int end, final boolean letters, final boolean numbers,
                                final char[] chars, final Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else {
                if (!letters && !numbers) {
                    end = Integer.MAX_VALUE;
                } else {
                    end = 'z' + 1;
                    start = ' ';
                }
            }
        } else {
            if (end <= start) {
                throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
            }
        }

        final char[] buffer = new char[count];
        final int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (letters && Character.isLetter(ch)
                    || numbers && Character.isDigit(ch)
                    || !letters && !numbers) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

    /**
     * Creates a random string whose length is the number of characters
     * specified. Characters will be chosen from the set of characters
     * specified by the string, must not be empty.
     * If null, the set of all characters is used.
     *
     * @param count the length of random string to create
     * @param chars the String containing the set of characters to use,
     *              may be null, but must not be empty
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0 or the string is empty.
     */
    public static String random(final int count, final String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, chars.toCharArray());
    }

    /**
     * Creates a random string whose length is the number of characters
     * specified.
     * Characters will be chosen from the set of characters specified.
     *
     * @param count the length of random string to create
     * @param chars the character array containing the set of characters to use,
     *              may be null
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0.
     */
    public static String random(final int count, final char... chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, 0, chars.length, false, false, chars, RANDOM);
    }

}
