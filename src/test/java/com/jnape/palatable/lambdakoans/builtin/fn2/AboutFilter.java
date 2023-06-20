package com.jnape.palatable.lambdakoans.builtin.fn2;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambdakoans.Koans;
import org.junit.Test;

import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Filter.filter;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class AboutFilter {
    @Test
    public void filterDoesWhatForEmptyList() {
        Fn1<String, Boolean> nonEmptyString = s -> !s.isEmpty();

        Iterable<String> strings = emptyList();
        Iterable<String> filteredStrings = filter(nonEmptyString, strings);

        assertThat(filteredStrings, contains(Koans.<String>__("Replace with whatever strings you expect to be in filteredStrings")));
    }


    @Test
    public void filteredElementsStayIn() {
        Fn1<String, Boolean> nonEmptyString = s -> !s.isEmpty();

        Iterable<String> strings = asList("", "Hello", "", "World", "!");
        Iterable<String> filteredStrings = filter(nonEmptyString, strings);

        assertThat(filteredStrings, contains(Koans.<String>__("Replace with whatever strings you expect to be in filteredStrings")));
    }

    @Test
    public void removesLongStrings() {
        Fn1<String, Boolean> isShortString = s -> s.length() <= 5;

        Iterable<String> strings = asList("Hello", "World!", "Functional", "Code");
        Iterable<String> filteredStrings = __();

        assertThat(filteredStrings, contains("Hello", "Code"));
    }

    @Test
    public void filtersWithAlwaysTrue() {
        Fn1<Integer, Boolean> alwaysTrue = s -> true; // Same as constantly(true)

        Iterable<Integer> ints = asList(1, 2, 3, 4, 5);
        Iterable<Integer> filteredInts = filter(alwaysTrue, ints);

        assertThat(filteredInts, contains(Koans.<Integer>__("Replace with whatever strings you expect to be in filteredInts")));
    }

    @Test
    public void filtersWithAlwaysFalse() {
        Fn1<Integer, Boolean> alwaysFalse = s -> false; // Same as constantly(false)

        Iterable<Integer> ints = asList(1, 2, 3, 4, 5);
        Iterable<Integer> filteredInts = filter(alwaysFalse, ints);

        assertThat(filteredInts, contains(Koans.<Integer>__("Replace with whatever strings you expect to be in filteredInts")));
    }

    @Test
    public void removeOdds() {
        Fn1<Integer, Boolean> fn = __();

        Iterable<Integer> ints = asList(1, 2, 3, 4, 5, 6);
        Iterable<Integer> filteredInts = filter(fn, ints);

        assertThat(filteredInts, contains(2, 4, 6));
    }

    @Test
    public void keepEvenStringsThatArentEmpty() {
        Fn1<String, Boolean> isEven = __("Keep Even Strings");
        Fn1<String, Boolean> isNotEmpty = __("Remove Empty Strings");

        List<String> strings = asList("", "a", "ab", "abc", "abcd", "abcde", "abcdef");
        Iterable<String> filteredInts = filter(isNotEmpty, filter(isEven, strings));

        assertThat(filteredInts, contains("ab", "abcd", "abcdef"));
    }

    // Complete AboutMap first
    @Test
    public void combineMapAndFilter() {
        Fn1<Integer, Integer> halve = i -> i / 2;
        Fn1<Integer, Boolean> isEven = i -> i % 2 == 0;

        Iterable<Integer> ints = asList(1, 6, 9, 14, 11, 32);
        Iterable<Integer> halvedEvens = __("Use Filter and Map with the above functions to return the even numbers divided by 2");

        assertThat(halvedEvens, contains(3, 7, 16));
    }

}
