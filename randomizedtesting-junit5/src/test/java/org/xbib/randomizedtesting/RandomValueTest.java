package org.xbib.randomizedtesting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

import static org.xbib.randomizedtesting.ContainsNonAlphanumericsMatcher.containsNonAlphanumerics;
import static org.xbib.randomizedtesting.ContainsOneOfMatcher.containsOneOf;
import static org.xbib.randomizedtesting.ContainsOnlyMatcher.containsOnly;
import static org.xbib.randomizedtesting.RandomApi.generateDouble;
import static org.xbib.randomizedtesting.RandomApi.generateLong;
import static org.xbib.randomizedtesting.RandomApi.bools;
import static org.xbib.randomizedtesting.RandomApi.english;
import static org.xbib.randomizedtesting.RandomApi.greaterDouble;
import static org.xbib.randomizedtesting.RandomApi.generateInteger;
import static org.xbib.randomizedtesting.RandomApi.generateNegativeDouble;
import static org.xbib.randomizedtesting.RandomApi.generateNegativeLong;
import static org.xbib.randomizedtesting.RandomApi.nullableBool;
import static org.xbib.randomizedtesting.RandomApi.numeric;
import static org.xbib.randomizedtesting.RandomApi.generatePositiveDouble;
import static org.xbib.randomizedtesting.RandomApi.generatePositiveInteger;
import static org.xbib.randomizedtesting.RandomApi.generatePositiveLong;
import static org.xbib.randomizedtesting.RandomApi.specialSymbols;
import static org.xbib.randomizedtesting.RandomApi.unicode;
import static org.xbib.randomizedtesting.RandomApi.unicodeWithoutBoundarySpaces;
import static org.xbib.randomizedtesting.RandomApi.weighedTrue;
import static org.xbib.randomizedtesting.RandomValue.between;
import static org.xbib.randomizedtesting.RandomValue.length;
import static org.xbib.randomizedtesting.RandomValue.fromZeroTo;
import static org.xbib.randomizedtesting.RandomValue.random;
import static org.xbib.randomizedtesting.RandomValue.randomAlphabetic;
import static org.xbib.randomizedtesting.RandomValue.randomAlphanumeric;
import static org.xbib.randomizedtesting.RandomValue.randomAscii;
import static org.xbib.randomizedtesting.RandomValue.randomNumeric;
import static org.xbib.randomizedtesting.StringModifier.Impls.multipleOf;
import static org.xbib.randomizedtesting.StringModifier.Impls.occasional;
import static org.xbib.randomizedtesting.StringModifier.Impls.oneOf;
import static org.xbib.randomizedtesting.StringModifier.Impls.prefix;
import static org.xbib.randomizedtesting.StringModifier.Impls.spaceLeft;
import static org.xbib.randomizedtesting.StringModifier.Impls.spaceRight;
import static org.xbib.randomizedtesting.StringModifier.Impls.spaces;
import static org.xbib.randomizedtesting.StringModifier.Impls.spacesLeft;
import static org.xbib.randomizedtesting.StringModifier.Impls.spacesRight;
import static org.xbib.randomizedtesting.StringModifier.Impls.specialSymbol;
import static org.xbib.randomizedtesting.StringModifier.Impls.suffix;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RandomValueTest {

    private static final long
            LESS_THAN_INT_MIN = ((long) Integer.MIN_VALUE) - 1,
            GREATER_THAN_MAX_INT = ((long) Integer.MAX_VALUE) + 1;

    private static class Assert {
        static void eachStringBetweenBoundaries(int min, int max, String... methods) {
            for (String method : methods) {
                RandomValue value = between(min, max);
                String generated = invokeAndGetString(value, method);
                String msg = "Method [" + method + "] returned: " + generated;
                assertThat(msg, generated.length(), lessThanOrEqualTo(max));
                assertThat(msg, generated.length(), greaterThanOrEqualTo(min));
            }
        }

        static void eachStringGeneratedByShortApiBetweenBoundaries(int min, int max, String... methods) {
            for (String method : methods) {
                String generated = invokeShortApiAndGetString(method, min, max);
                String msg = "Static method [" + method + "] returned: " + generated;
                assertThat(msg, generated.length(), lessThanOrEqualTo(max));
                assertThat(msg, generated.length(), greaterThanOrEqualTo(min));
            }
        }

        private static void eachStringUpToMax(int max, String... methods) {
            for (String method : methods) {
                RandomValue value = RandomValue.fromZeroTo(max);
                String generated = invokeAndGetString(value, method);

                String msg = "Method [" + method + "] returned: " + generated;
                assertThat(msg, generated.length(), lessThanOrEqualTo(max));
                assertThat(msg, generated.length(), greaterThanOrEqualTo(0));
            }
        }

        static void eachStringExactlyOfRequiredLength(int exactLength, String... methods) {
            for (String method : methods) {
                RandomValue value = length(exactLength);
                String generated = invokeAndGetString(value, method);

                String msg = "Static method [" + method + "] returned: " + generated;
                assertThat(msg, generated.length(), lessThanOrEqualTo(exactLength));
                assertThat(msg, generated.length(), greaterThanOrEqualTo(0));
            }
        }

        static void eachStringGeneratedByShortApiExactlyOfRequiredLength(int exactLength, String... methods) {
            for (String method : methods) {
                String generated = invokeShortApiAndGetString(method, exactLength);
                String msg = "Static method [" + method + "] returned: " + generated;
                assertThat(msg, generated.length(), lessThanOrEqualTo(exactLength));
                assertThat(msg, generated.length(), greaterThanOrEqualTo(0));
            }
        }

        private static String invokeAndGetString(RandomValue value, String method) {
            try {
                Method toInvoke = RandomValue.class.getDeclaredMethod(method);
                return (String) toInvoke.invoke(value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        private static String invokeShortApiAndGetString(String method, Object... params) {
            Class<?>[] paramClasses = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                if (params[i].getClass() == Integer.class) paramClasses[i] = int.class;
                else throw new IllegalArgumentException("Wrong type of the parameter found - " +
                        "no such randomized methods that can accept" + params[i].getClass());
            }
            try {
                Method toInvoke = RandomApi.class.getDeclaredMethod(method, paramClasses);
                return (String) toInvoke.invoke(null, params);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Nested
    @DisplayName("Integer Generator")
    class Integers {

        @Test
        void returnsPositiveIntegers() {
            assertThat(RandomValue.fromZeroTo(Integer.MAX_VALUE).integer(), greaterThan(0));
            assertThat(generatePositiveInteger(), greaterThan(0));
        }

        @Test
        void returnsNegativeIntegers() {
            assertThat(between(Integer.MIN_VALUE, -1).integer(), lessThan(0));
            assertThat(RandomApi.generateInteger(Integer.MIN_VALUE, -1), lessThan(0));
        }

        @Test
        void returnsSameNumberIfBoundariesEqual() {
            int boundary = new Random().nextInt();
            assertEquals(boundary, between(boundary, boundary).integer());
            assertEquals(boundary, RandomApi.generateInteger(boundary, boundary));
        }

        @Test
        void returnsAnyInteger() {
            generateInteger();
        }

        @Test
        void throwsIfMaxBoundaryGreaterThanInteger() {
            assertThrows(NumberOutOfBoundaryException.class, () -> RandomValue.fromZeroTo(GREATER_THAN_MAX_INT).integer());
        }

        @Test
        void throwsIfMinBoundaryLessThanInteger() {
            assertThrows(NumberOutOfBoundaryException.class, () -> between(LESS_THAN_INT_MIN, LESS_THAN_INT_MIN).integer());
        }
    }

    @Nested
    @DisplayName("Long Generator")
    class Longs {

        @Test
        void returnsPositiveLong() {
            assertThat(RandomValue.fromZeroTo(Long.MAX_VALUE).Long(), greaterThan(0L));
            assertThat(generatePositiveLong(), greaterThan(0L));
        }

        @Test
        void returnsNegativeLongs() {
            assertThat(generateLong(Long.MIN_VALUE, -1), lessThan(0L));
            assertThat(generateNegativeLong(), lessThan(0L));
        }

        @Test
        void returnsSameNumberIfBoundariesEqual() {
            int boundary = new Random().nextInt();
            assertEquals(boundary, between(boundary, boundary).Long());
            assertEquals(boundary, generateLong(boundary, boundary));
        }

        @Test
        void returnsLongs_thatAreGreaterThan0_sometimes() {
            for (int i = 0; i < 50; i++) if (RandomApi.generateLong() > 0) return;
            fail("Random Long should've returned a positive number at least once");
        }

        @Test
        void returnsLong_thatAreLessThan0_sometimes() {
            for (int i = 0; i < 50; i++) if (RandomApi.generateLong() < 0) return;
            fail("Random Long should've returned a negative number at least once");
        }
    }

    @Nested
    @DisplayName("Double Generator")
    class Doubles {
        @Test
        void returnsDouble_betweenBoundaries() {
            double aDouble = generateDouble(-100, 100);
            assertThat(aDouble, greaterThan(-100.));
            assertThat(aDouble, lessThan(100.));
        }

        @Test
        void returnsPositiveDoubles() {
            assertThat(RandomApi.generateDouble(Double.MAX_VALUE), greaterThan(0.0));
            assertThat(generatePositiveDouble(), greaterThan(0.0));
        }

        @Test
        void returnsNegativeDoubles() {
            assertThat(generateDouble(Long.MIN_VALUE, 0.0), lessThan(0.0));
            assertThat(generateNegativeDouble(), lessThan(0.0));
        }

        @Test
        void returnsDouble_thatAreGreaterThan0_sometimes() {
            for (int i = 0; i < 50; i++) if (RandomApi.generateDouble() > 0) return;
            fail("Random Double should've returned a positive number at least once");
        }

        @Test
        void returnsDouble_thatAreLessThan0_sometimes() {
            for (int i = 0; i < 50; i++) if (RandomApi.generateDouble() < 0) return;
            fail("Random Double should've returned a negative number at least once");
        }

        @Test
        void returnsDoubleGreaterThanTheSpecifiedOne() {
            double original = Long.MAX_VALUE - 10000;
            assertThat(original, lessThan(greaterDouble(original)));
        }

        @Test
        void throwsIfBoundariesEqual() {
            long boundary = RandomApi.generateLong();
            assertThrows(IllegalArgumentException.class, () -> generateDouble(boundary, boundary));
        }

        @Test
        void throwsIfLowerIsGreaterThanUpper() {
            assertThrows(IllegalArgumentException.class, () -> generateDouble(1, 0));
        }
    }

    @Nested
    @DisplayName("String Generator")
    class Strings {
        String[] allMethods = {"alphanumeric", "numeric", "unicode", "english", "specialSymbols"};

        @Test
        void returnsStringUpToMaxBoundary() {
            Assert.eachStringUpToMax(RandomApi.generateInteger(1000), allMethods);
        }

        @Test
        void returnsStringBetweenBoundaries() {
            int min = RandomApi.generateInteger(1000), max = min + RandomApi.generateInteger(1000);
            Assert.eachStringBetweenBoundaries(min, max, allMethods);
            Assert.eachStringGeneratedByShortApiBetweenBoundaries(min, max, allMethods);
        }

        @Test
        void returnsStringWithExactLength() {
            Assert.eachStringExactlyOfRequiredLength(RandomApi.generateInteger(5000), allMethods);
            Assert.eachStringGeneratedByShortApiExactlyOfRequiredLength(RandomApi.generateInteger(5000), allMethods);
        }

        @Test
        void returnsEmptyStringIfLengthIsSetTo0() {
            Assert.eachStringExactlyOfRequiredLength(0, allMethods);
            Assert.eachStringGeneratedByShortApiExactlyOfRequiredLength(0, allMethods);
        }

        @Test
        void returnsNumbersIfNumeric() {
            assertThat(length(1000).numeric(), containsString("1"));
            assertThat(numeric(1000), containsString("1"));
        }

        @Test
        void doesNotReturnsNumbersIfEnglishRequested() {
            assertThat(length(100).english(), not(containsString("1")));
            assertThat(english(100), not(containsString("1")));
        }

        @Test
        void doesNotReturnWeirdUnicodesIfEnglishRequested() {
            assertThat(length(100).english(), not(containsNonAlphanumerics()));
            assertThat(english(100), not(containsNonAlphanumerics()));
        }

        @Test
        void createsUnicodeStringThatContainsNonAlphanumerics() {
            assertThat(length(1000).unicode(), containsNonAlphanumerics());
            assertThat(unicode(100, 1000), containsNonAlphanumerics());
        }

        @Test
        void createsStringWithSpecialSymbols() {
            assertThat(length(1000).specialSymbols(), containsString(","));
            assertThat(specialSymbols(1000), containsString(","));
        }

        @Test
        void createsStringFromGivenVocabulary() {
            assertThat(length(100).string('A', 'b', ' '), containsOnly('A', 'b', ' '));
            assertThat(length(100).string("AB "), containsOnly('A', 'B', ' '));
        }

        @Test
        void throwsIfVocabularyIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> length(1000).string());
            assertThrows(IllegalArgumentException.class, () -> length(10).string(""));
        }

        @Test
        void addsSpecialSymbolsViaStringModifiers() {
            assertThat(length(100).with(specialSymbol()).english(), containsOneOf(specialSymbols().toCharArray()));
        }

        @Test
        void doesNotModifyLengthIfAddsSpecialSymbol() {
            assertThat(length(100).with(specialSymbol()).english().length(), equalTo(100));
        }

        @Test
        void doesNotModifyLengthIfAddsMultipleChars() {
            assertThat(length(100).with(multipleOf("!!_#")).english().length(), equalTo(100));
        }

        @Test
        void addsSymbolsOccasionally() {
            bools(10);
            List<String> alphanumerics = length(5).with(occasional("!#")).alphanumerics(500);
            assertThat(alphanumerics, hasItems(containsString("!")));
            assertThat(alphanumerics, hasItems(containsString("#")));
            assertThat(alphanumerics, hasItems(not(containsString("!"))));
        }

        @Test
        void addsOneOfPassedSymbols() {
            assertThat(length(100).with(oneOf(",")).english(), containsString(","));
        }

        @Test
        void addsSpacesViaStringModifier() {
            assertThat(length(100).with(spaces()).numeric(), containsString(" "));
        }

        @Test
        void addsPrefixAtTheBeginning() {
            assertThat(length(10).with(prefix("BLAH")).numeric(), startsWith("BLAH"));
        }

        @Test
        void addsPrefixAtTheBeginningInBatchMode() {
            assertThat(length(10).with(prefix("BLAH")).alphanumerics(), everyItem(startsWith("BLAH")));
        }

        @Test
        void addsSuffixAtTheEnd() {
            assertThat(length(10).with(suffix("BLAH")).numeric(), endsWith("BLAH"));
        }

        @Test
        void addsSuffixAtTheEndInBatchMode() {
            assertThat(length(10).with(suffix("BLAH")).alphanumerics(), everyItem(endsWith("BLAH")));
        }

        @Test
        void addsSpacesAtTheBeginning() {
            assertThat(length(10).with(spaceLeft()).numeric(), startsWith(" "));
            assertThat(length(10).with(spacesLeft(2)).numeric(), startsWith("  "));
            assertThat(length(10).with(spacesLeft(2)).numerics(), everyItem(startsWith("  ")));
        }

        @Test
        void addsSpacesAtTheEnd() {
            assertThat(length(10).with(spaceRight()).english(), endsWith(" "));
            assertThat(length(10).with(spacesRight(2)).alphanumeric(), endsWith("  "));
            assertThat(length(10).with(spacesRight(2)).alphanumerics(), everyItem(endsWith("  ")));
        }

        @Test
        void doesNotDamageRestOfStringIfSpacesAddedAtTheEnd() {
            assertThat(length(10).with(spacesRight(2)).alphanumeric().substring(0, 8), not(containsString(" ")));
        }

        @Test
        void returnsNoSpacesAtBorders_ifUnicodeWithSuchReqsRequested() {
            String original = unicodeWithoutBoundarySpaces(2);
            assertFalse(Character.isWhitespace(original.codePointAt(0)));
            assertFalse(Character.isWhitespace(original.codePointAt(1)));

            original = unicodeWithoutBoundarySpaces(2, 5);
            assertFalse(Character.isWhitespace(original.codePointAt(0)));
            assertFalse(Character.isWhitespace(original.codePointAt(original.length() - 1)));
        }

        @Test
        void trimDoesNotChange_unicodeWithoutSpacesStrings() {
            String original = unicodeWithoutBoundarySpaces(2);
            assertEquals(original, original.trim());
        }

        @Test
        void createsMultipleStringsInBatchMode() {
            List<String> alphanumerics = RandomValue.fromZeroTo(10).alphanumerics(5);
            assertThat(alphanumerics.size(), equalTo(5));
        }

        @Test
        void createsUpTo100StringsInBatchMode() {
            List<String> alphanumerics = RandomValue.fromZeroTo(10).alphanumerics();
            assertThat(alphanumerics.size(), greaterThanOrEqualTo(1));
            assertThat(alphanumerics.size(), lessThanOrEqualTo(100));
        }

        @Test
        void appliesMultipleModifiersInBatchMode() {
            List<String> result = between(5, 15).with(prefix("blah"), spaceRight(), spaceLeft()).alphanumerics();
            assertThat(result, everyItem(allOf(startsWith(" lah"), endsWith(" "))));
        }

        @Test
        void throwsIfMinBoundaryIsNegative() {
            assertThrows(NumberOutOfBoundaryException.class, () -> between(-1, 10).alphanumeric());
        }
    }

    @Nested
    @DisplayName("Date Generator")
    class Dates {

        @Test
        void returnsDateUpToMaxBoundary() {
            assertThat(fromZeroTo(ZonedDateTime.now()).zonedDateTime(ZoneId.of("GMT")), lessThanOrEqualTo(ZonedDateTime.now()));
        }

        @Test
        void startsDatesFrom1970_ifUpToUsed() {
            assertThat(fromZeroTo(ZonedDateTime.now()).zonedDateTime(),
                    greaterThanOrEqualTo(ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("GMT"))));
        }

        @Test
        void createsDatesBetweenBoundaries() {
            ZonedDateTime left = ZonedDateTime.now().minusSeconds(1);
            ZonedDateTime right = ZonedDateTime.now();
            assertThat(between(left, right).zonedDateTime(), greaterThanOrEqualTo(left));
            assertThat(between(left, right).zonedDateTime(), lessThanOrEqualTo(right));
        }
    }

    @Nested
    @DisplayName("Boolean Generator")
    class Booleans {

        @Test
        void canReturnTrueAndFalse() {
            boolean[] bools = bools(500);
            assertArrayContains(bools, true);
            assertArrayContains(bools, false);
        }

        @Test
        void canReturnNulls() {
            for (int i = 0; i < 1000; i++) if (nullableBool() == null) return;
            fail("Nullable Boolean should've returned null at least once");
        }

        @Test
        void canReturnTrue() {
            for (int i = 0; i < 1000; i++) if (nullableBool() == Boolean.TRUE) return;
            fail("Nullable Boolean should've returned True at least once");
        }

        @Test
        void canReturnFalse() {
            for (int i = 0; i < 1000; i++) if (nullableBool() == Boolean.FALSE) return;
            fail("Nullable Boolean should've returned False at least once");
        }

        @Test
        void alwaysReturnsTrueIfProbabilityOfTrueIs1() {
            assertTrue(weighedTrue(1));
        }

        @Test
        void alwaysReturnsFalseIfProbabilityOfTrueIs0() {
            assertFalse(weighedTrue(0));
        }

        @Test
        void weighedTrueCanReturnsTrue_sometimes() {
            for (int i = 0; i < 50; i++) if (weighedTrue(.5)) return;
            fail("weighedTrue() had to return True at least once, but it didn't");
        }

        @Test
        void weighedTrueCanReturnsFalse_sometimes() {
            for (int i = 0; i < 50; i++) if (!weighedTrue(.5)) return;
            fail("weighedTrue() had to return False at least once, but it didn't");
        }

        private void assertArrayContains(boolean[] array, boolean element) {
            for (boolean anArray : array) {
                if (anArray == element) return;
            }
            fail("Couldn't find element [" + element + "] in the array");
        }
    }


    @Test
    public void testRandomStringUtils() {
        String r1 = random(50);
        assertEquals(50, r1.length(), "random(50) length");
        String r2 = random(50);
        assertEquals(50, r2.length(), "random(50) length");
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        r1 = randomAscii(50);
        assertEquals(50, r1.length(), "randomAscii(50) length");
        for (int i = 0; i < r1.length(); i++) {
            assertTrue(r1.charAt(i) >= 32 && r1.charAt(i) <= 127, "char between 32 and 127");
        }
        r2 = randomAscii(50);
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        r1 = randomAlphabetic(50);
        assertEquals(50, r1.length(), "randomAlphabetic(50)");
        for (int i = 0; i < r1.length(); i++) {
            assertTrue(Character.isLetter(r1.charAt(i)) && !Character.isDigit(r1.charAt(i)), "r1 contains alphabetic");
        }
        r2 = randomAlphabetic(50);
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        r1 = randomAlphanumeric(50);
        assertEquals(50, r1.length(), "randomAlphanumeric(50)");
        for (int i = 0; i < r1.length(); i++) {
            assertTrue(Character.isLetterOrDigit(r1.charAt(i)), "r1 contains alphanumeric");
        }
        r2 = randomAlphabetic(50);
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        r1 = randomNumeric(50);
        assertEquals(50, r1.length(), "randomNumeric(50)");
        for (int i = 0; i < r1.length(); i++) {
            assertTrue(Character.isDigit(r1.charAt(i)) && !Character.isLetter(r1.charAt(i)), "r1 contains numeric");
        }
        r2 = randomNumeric(50);
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        String set = "abcdefg";
        r1 = random(50, set);
        assertEquals(50, r1.length(), "random(50, \"abcdefg\")");
        for (int i = 0; i < r1.length(); i++) {
            assertTrue(set.indexOf(r1.charAt(i)) > -1, "random char in set");
        }
        r2 = random(50, set);
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        r1 = random(50, (String) null);
        assertEquals(50, r1.length(), "random(50) length");
        r2 = random(50, (String) null);
        assertEquals(50, r2.length(), "random(50) length");
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        set = "stuvwxyz";
        r1 = random(50, set.toCharArray());
        assertEquals(50, r1.length(), "random(50, \"stuvwxyz\")");
        for (int i = 0; i < r1.length(); i++) {
            assertTrue(set.indexOf(r1.charAt(i)) > -1, "random char in set");
        }
        r2 = random(50, set);
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        r1 = random(50, (char[]) null);
        assertEquals(50, r1.length(), "random(50) length");
        r2 = random(50, (char[]) null);
        assertEquals(50, r2.length(), "random(50) length");
        assertTrue(!r1.equals(r2), "!r1.equals(r2)");

        final long seed = System.currentTimeMillis();
        r1 = random(50, 0, 0, true, true, null, new Random(seed));
        r2 = random(50, 0, 0, true, true, null, new Random(seed));
        assertEquals(r1, r2, "r1.equals(r2)");

        r1 = random(0);
        assertEquals("", r1, "random(0).equals(\"\")");
    }

    @Test
    public void testLANG805() {
        final long seed = System.currentTimeMillis();
        assertEquals("aaa", random(3, 0, 0, false, false, new char[]{'a'}, new Random(seed)));
    }

    @Test
    public void testLANG807() {
        try {
            random(3, 5, 5, false, false);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException ex) { // distinguish from Random#nextInt message
            final String msg = ex.getMessage();
            assertTrue(msg.contains("start"), "Message (" + msg + ") must contain 'start'");
            assertTrue(msg.contains("end"), "Message (" + msg + ") must contain 'end'");
        }
    }

    @Test
    public void testExceptions() {
        final char[] DUMMY = new char[]{'a'}; // valid char array
        try {
            random(-1);
            fail("");
        } catch (final IllegalArgumentException ex) {
            //
        }
        try {
            random(-1, true, true);
            fail("");
        } catch (final IllegalArgumentException ex) {
            //
        }
        try {
            random(-1, DUMMY);
            fail("");
        } catch (final IllegalArgumentException ex) {
            //
        }
        try {
            random(1, new char[0]); // must not provide empty array => IAE
            fail("");
        } catch (final IllegalArgumentException ex) {
        }
        try {
            random(-1, "");
            fail("");
        } catch (final IllegalArgumentException ex) {
        }
        try {
            random(-1, (String) null);
            fail("");
        } catch (final IllegalArgumentException ex) {
        }
        try {
            random(-1, 'a', 'z', false, false);
            fail("");
        } catch (final IllegalArgumentException ex) {
        }
        try {
            random(-1, 'a', 'z', false, false, DUMMY);
            fail("");
        } catch (final IllegalArgumentException ex) {
        }
        try {
            random(-1, 'a', 'z', false, false, DUMMY, new Random());
            fail("");
        } catch (final IllegalArgumentException ex) {
        }
    }

    /**
     * Make sure boundary alphanumeric characters are generated by randomAlphaNumeric
     * This test will fail randomly with probability = 6 * (61/62)**1000 ~ 5.2E-7
     */
    @Test
    public void testRandomAlphaNumeric() {
        final char[] testChars = {'a', 'z', 'A', 'Z', '0', '9'};
        final boolean[] found = {false, false, false, false, false, false};
        for (int i = 0; i < 100; i++) {
            final String randString = randomAlphanumeric(10);
            for (int j = 0; j < testChars.length; j++) {
                if (randString.indexOf(testChars[j]) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < testChars.length; i++) {
            if (!found[i]) {
                fail("alphanumeric character not generated in 1000 attempts: "
                        + testChars[i] + " -- repeated failures indicate a problem ");
            }
        }
    }

    /**
     * Make sure '0' and '9' are generated by randomNumeric
     * This test will fail randomly with probability = 2 * (9/10)**1000 ~ 3.5E-46
     */
    @Test
    public void testRandomNumeric() {
        final char[] testChars = {'0', '9'};
        final boolean[] found = {false, false};
        for (int i = 0; i < 100; i++) {
            final String randString = randomNumeric(10);
            for (int j = 0; j < testChars.length; j++) {
                if (randString.indexOf(testChars[j]) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < testChars.length; i++) {
            if (!found[i]) {
                fail("digit not generated in 1000 attempts: "
                        + testChars[i] + " -- repeated failures indicate a problem ");
            }
        }
    }

    /**
     * Make sure boundary alpha characters are generated by randomAlphabetic
     * This test will fail randomly with probability = 4 * (51/52)**1000 ~ 1.58E-8
     */
    @Test
    public void testRandomAlphabetic() {
        final char[] testChars = {'a', 'z', 'A', 'Z'};
        final boolean[] found = {false, false, false, false};
        for (int i = 0; i < 100; i++) {
            final String randString = randomAlphabetic(10);
            for (int j = 0; j < testChars.length; j++) {
                if (randString.indexOf(testChars[j]) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < testChars.length; i++) {
            if (!found[i]) {
                fail("alphanumeric character not generated in 1000 attempts: "
                        + testChars[i] + " -- repeated failures indicate a problem ");
            }
        }
    }

    /**
     * Make sure 32 and 127 are generated by randomNumeric
     * This test will fail randomly with probability = 2*(95/96)**1000 ~ 5.7E-5
     */
    @Test
    public void testRandomAscii() {
        final char[] testChars = {(char) 32, (char) 126};
        final boolean[] found = {false, false};
        for (int i = 0; i < 100; i++) {
            final String randString = randomAscii(10);
            for (int j = 0; j < testChars.length; j++) {
                if (randString.indexOf(testChars[j]) > 0) {
                    found[j] = true;
                }
            }
        }
        for (int i = 0; i < testChars.length; i++) {
            if (!found[i]) {
                fail("ascii character not generated in 1000 attempts: "
                        + (int) testChars[i] +
                        " -- repeated failures indicate a problem");
            }
        }
    }

    /**
     * Test homogeneity of random strings generated --
     * i.e., test that characters show up with expected frequencies
     * in generated strings.  Will fail randomly about 1 in 1000 times.
     * Repeated failures indicate a problem.
     */
    @Test
    public void testCommonsLang3RandomStringUtilsHomog() {
        final String set = "abc";
        final char[] chars = set.toCharArray();
        String gen;
        final int[] counts = {0, 0, 0};
        final int[] expected = {200, 200, 200};
        for (int i = 0; i < 100; i++) {
            gen = random(6, chars);
            for (int j = 0; j < 6; j++) {
                switch (gen.charAt(j)) {
                    case 'a': {
                        counts[0]++;
                        break;
                    }
                    case 'b': {
                        counts[1]++;
                        break;
                    }
                    case 'c': {
                        counts[2]++;
                        break;
                    }
                    default: {
                        fail("generated character not in set");
                    }
                }
            }
        }
        // Perform chi-square test with df = 3-1 = 2, testing at .001 level
        assertTrue(chiSquare(expected, counts) < 13.82, "test homogeneity -- will fail about 1 in 1000 times");
    }

    /**
     * Computes Chi-Square statistic given observed and expected counts
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     */
    private double chiSquare(final int[] expected, final int[] observed) {
        double sumSq = 0.0d;
        double dev;
        for (int i = 0; i < observed.length; i++) {
            dev = observed[i] - expected[i];
            sumSq += dev * dev / expected[i];
        }
        return sumSq;
    }

    /**
     * Checks if the string got by {@link RandomValue#random(int)}
     * can be converted to UTF-8 and back without loss.
     */
    @Test
    public void testLang100() {
        final int size = 5000;
        final Charset charset = Charset.forName("UTF-8");
        final String orig = random(size);
        final byte[] bytes = orig.getBytes(charset);
        final String copy = new String(bytes, charset);

        // for a verbose compare:
        for (int i = 0; i < orig.length() && i < copy.length(); i++) {
            final char o = orig.charAt(i);
            final char c = copy.charAt(i);
            assertEquals(o, c, "differs at " + i + "(" + Integer.toHexString(Character.valueOf(o).hashCode()) + "," +
                    Integer.toHexString(Character.valueOf(c).hashCode()) + ")");
        }
        // compare length also
        assertEquals(orig.length(), copy.length());
        // just to be complete
        assertEquals(orig, copy);
    }
}