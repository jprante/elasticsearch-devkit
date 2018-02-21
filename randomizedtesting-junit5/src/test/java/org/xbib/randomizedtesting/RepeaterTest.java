package org.xbib.randomizedtesting;

import org.junit.jupiter.api.Test;

import static org.xbib.randomizedtesting.RandomString.Type.ENGLISH;
import static org.xbib.randomizedtesting.RandomString.Type.NUMERIC;
import static org.xbib.randomizedtesting.RandomValue.length;
import static org.xbib.randomizedtesting.Repeater.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepeaterTest {

    @Test
    void stringsAreRepeated() {
        assertEquals("", repeat("1-").times(0));
        assertEquals("1-1-1", repeat("1-").times(3));
        assertEquals("1-1-1", repeat("1").string("-").times(3));
        assertEquals("1-1-1-", repeat("1").string("-").includeLastSymbol().times(3));
        assertEquals("1-1-", repeat("1").string("-").removeLastSymbols(2).times(3));
    }

    @Test
    void stringsAndRandomsAreRepeated() {
        assertThat(repeat(length(2), NUMERIC).string("-").times(3), matchesPattern("\\d{2}-\\d{2}-\\d{2}"));
        assertThat(repeat(length(2), ENGLISH).string(" ").times(1, 2), matchesPattern("[a-zA-Z]{2}|[a-zA-Z]{2} [a-zA-Z]{2}"));
        assertThat(repeat(length(2), ENGLISH).string(" ").includeLastSymbol().times(2), matchesPattern("[a-zA-Z]{2} [a-zA-Z]{2} "));
    }

    @Test
    void throwsIfRepeatingNegativeNumberOfTimes() {
        assertThrows(IllegalArgumentException.class, () -> repeat("").times(-1));
        assertThrows(IllegalArgumentException.class, () -> repeat("").times(-1, 5));
    }
}