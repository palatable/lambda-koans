package com.jnape.palatable.lambdakoans.builtin.fn2;

import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.Test;

import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

public class AboutMap {
    @Test
    public void mapsValuesWithinAType() {
        Fn1<Integer, Integer> increment = __();

        Iterable<Integer> list = asList(1, 2, 3, 4, 5);
        Iterable<Integer> incremented = map(increment, list);

        assertThat(incremented, iterates(2, 3, 4, 5, 6));
    }

    @Test
    public void mapsToADifferentType() {
        Fn1<Integer, Double> halve = __();

        Iterable<Integer> list = asList(1, 2, 3, 4, 5);
        Iterable<Double> halved = map(halve, list);

        assertThat(halved, iterates(0.5, 1.0, 1.5, 2.0, 2.5));
    }

    @Test
    public void useMapToConvertTypes() {
        Fn1<Boolean, String> serialize = b -> b ? "True" : "False";

        Iterable<Boolean> list = asList(true, false, false, true);
        Iterable<String> strings = __();

        assertThat(strings, iterates("True", "False", "False", "True"));
    }

    @Test
    public void mappingsCompose() {
        Fn1<Integer, Boolean> isEven = i -> i % 2 == 0;
        Fn1<Boolean, String> serialize = b -> b ? "Even" : "Odd";

        List<Integer> list = asList(1, 2, 3, 4, 5);
        Iterable<String> strings = __(); // Call map twice to map over both functions

        assertThat(strings, iterates("Odd", "Even", "Odd", "Even", "Odd"));
    }

    @Test
    public void predictMappings() {
        Fn1<String, Integer> stringLength = String::length;
        Fn1<Integer, Boolean> isEven = i -> i % 2 == 0;
        Fn1<Boolean, String> serialize = b -> b ? "Even" : "Odd";

        Iterable<String> strings = asList("first", "second", "third", "fourth", "fifth");
        Iterable<String> newStrings = map(serialize, map(isEven, map(stringLength, strings)));

        assertThat(newStrings, iterates(__(), __(), __(), __(), __()));
    }

}
