package org.xbib.randomizedtesting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.xbib.randomizedtesting.RandomElements.from;
import static org.xbib.randomizedtesting.RandomApi.blankOr;
import static org.xbib.randomizedtesting.RandomApi.nullOr;
import static org.xbib.randomizedtesting.RandomApi.nullOrBlank;
import static org.xbib.randomizedtesting.RandomApi.nullOrEmpty;
import static org.xbib.randomizedtesting.RandomApi.sample;
import static org.xbib.randomizedtesting.RandomApi.sampleMultiple;
import static org.xbib.randomizedtesting.RandomValue.length;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Random Elements")
public class RandomElementsTest {
    @Test
    void canSampleTheOnlyElementOfList() {
        assertEquals("ABC", from("ABC").sample());
    }

    @Test
    void canSampleOneElementFromList() {
        List<String> list = RandomValue.fromZeroTo(10).alphanumerics();
        assertThat(list, hasItem(from(list).sample()));
        assertThat(list, hasItem(sample(list)));
    }

    @Test
    void canSampleOneElementFromList_andAdditionalVarargs() {
        assertThat("element", equalTo(from(emptyList(), "element").sample()));
        assertThat("element", equalTo(sample(emptyList(), "element")));
    }

    @Test
    void sampleDoesNotReturnDuplicates_ifWithoutReplacement() {
        List<String> population = length(500).alphanumerics(10);
        List<String> sample = from(population).sample(5);

        assertEquals(sample.size(), new HashSet<>(sample).size());
        assertEquals(sampleMultiple(5, population).size(), new HashSet<>(sample).size());
    }

    @Test
    void samplingSets_returnsSets() {
        HashSet<String> toSampleFrom = new HashSet<>();
        toSampleFrom.add("a");
        assertThat(sampleMultiple(toSampleFrom), instanceOf(Set.class));
    }

    @Test
    void canSampleOneElementFromArray() {
        assertThat(from("element1", "element2").sample(2), containsInAnyOrder("element1", "element2"));
        assertThat(sampleMultiple(2, "element1", "element2"), containsInAnyOrder("element1", "element2"));
    }

    @Test
    void mustThrowIfSampleIsLargerThanPopulation() {
        assertThrows(IllegalArgumentException.class, () -> from("el", "el2").sample(3));
        assertThrows(IllegalArgumentException.class, () -> sampleMultiple(3, "el", "el2"));
    }

    @Test
    void throwsIfCollectionToSampleFromIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> from().sample(3));
        assertThrows(IllegalArgumentException.class, () -> sampleMultiple(0));
        assertThrows(IllegalArgumentException.class, RandomApi::sample);
    }

    @Test
    void canSampleMultipleElementsFromList() {
        List<String> population = RandomValue.fromZeroTo(10).alphanumerics(5, 10);
        List<String> sample = from(population).sample(5);
        assertEquals(5, sample.size());
        assertThat(population, hasItems(sample.toArray(new String[sample.size()])));
    }

    @Test
    void samplesDuplicateElements_ifSampleSizeLargerThanPopulation_andSamplingIsWithReplacement() {
        List<String> population = RandomValue.fromZeroTo(10).alphanumerics(2);
        List<String> sample = from(population).sampleWithReplacement(5);
        assertEquals(5, sample.size());
        assertThat(population, hasItems(sample.toArray(new String[sample.size()])));
    }

    @Test
    void nullOrObj_returnsNull_sometimes() {
        for (int i = 0; i < 100; i++) {
            if (nullOr("str") == null) {
                return;
            }
        }
        fail("nullOr() had to return null at least once, but it didn't");
    }

    @Test
    void nullOrObj_returnsObj_sometimes() {
        for (int i = 0; i < 100; i++) {
            if ("str".equals(nullOr("str"))) {
                return;
            }
        }
        fail("nullOr() had to return Obj at least once, but it didn't");
    }

    @Test
    void nullOrObj_doesNotReturnAnything_butNullOrObj() {
        String result = nullOr("str");
        assertTrue("str".equals(result) || result == null);
    }

    @Test
    void nullOrEmpty_doesNotReturnAnything_butNullOrEmptyString() {
        String result = nullOrEmpty();
        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void nullOrEmpty_returnsEmpty_sometimes() {
        for (int i = 0; i < 100; i++) {
            if ("".equals(nullOrEmpty())) {
                return;
            }
        }
        fail("nullOrEmpty() had to return Empty String at least once, but it didn't");
    }

    @Test
    void nullOrEmpty_returnsNull_sometimes() {
        for (int i = 0; i < 100; i++) {
            if (nullOrEmpty() == null) {
                return;
            }
        }
        fail("nullOrEmpty() had to return null at least once, but it didn't");
    }

    @Test
    void nullOrBlank_doesNotReturnAnything_butNullOrBlankString() {
        String result = nullOrBlank();
        assertTrue(result == null || result.trim().isEmpty());
    }

    @Test
    void nullOrBlank_returnsEmpty_sometimes() {
        for (int i = 0; i < 100; i++) {
            if ("".equals(nullOrBlank())) {
                return;
            }
        }
        fail("nullOrBlank() had to return Empty String at least once, but it didn't");
    }

    @Test
    void nullOrBlank_returnsNull_sometimes() {
        for (int i = 0; i < 100; i++) {
            if (nullOrBlank() == null) {
                return;
            }
        }
        fail("nullOrBlank() had to return null at least once, but it didn't");
    }

    @Test
    void nullOrBlank_returnsWhitespaces_sometimes() {
        for (int i = 0; i < 100; i++) {
            String nullOrBlank = nullOrBlank();
            if (nullOrBlank != null && nullOrBlank.contains(" ")) {
                return;
            }
        }
        fail("nullOrBlank() had to return whitespaces at least once, but it didn't");
    }

    @Test
    void blankOr_doesNotReturnAnything_butBlankOrSpecifiedString() {
        String result = blankOr("str");
        assertTrue(result == null || result.trim().isEmpty() || result.equals("str"));
    }

    @Test
    void blankOr_returnsEmpty_sometimes() {
        for (int i = 0; i < 100; i++) {
            if ("".equals(blankOr("str"))) {
                return;
            }
        }
        fail("blankOr() had to return Empty String at least once, but it didn't");
    }

    @Test
    void blankOr_returnsNull_sometimes() {
        for (int i = 0; i < 100; i++) {
            if (blankOr("str") == null) {
                return;
            }
        }
        fail("blankOr() had to return null at least once, but it didn't");
    }

    @Test
    void blankOr_returnsWhitespaces_sometimes() {
        for (int i = 0; i < 100; i++) {
            String nullOrBlank = blankOr("str");
            if (nullOrBlank != null && nullOrBlank.contains(" ")) {
                return;
            }
        }
        fail("blankOr() had to return whitespaces at least once, but it didn't");
    }

    @Test
    void blankOr_returnsSpecifiedString_sometimes() {
        for (int i = 0; i < 100; i++) {
            if ("str".equals(blankOr("str"))) {
                return;
            }
        }
        fail("blankOr() had to return specified string at least once, but it didn't");
    }
}
