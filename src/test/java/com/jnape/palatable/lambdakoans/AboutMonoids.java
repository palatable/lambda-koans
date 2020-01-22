package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.monoid.Monoid;
import org.junit.Test;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.monoid.Monoid.monoid;
import static com.jnape.palatable.lambda.monoid.builtin.Concat.concat;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @see AboutSemigroups
 */
public class AboutMonoids {

    @Test
    public void monoidsCombineValues() {
        Monoid<Integer> integerMonoid = monoid(Integer::sum, 0);
        assertThat(integerMonoid.apply(1, integerMonoid.identity()), equalTo(__()));
        assertThat(integerMonoid.apply(1, 2), equalTo(__()));

        Monoid<String> stringMonoid = monoid(String::concat, "");
        assertThat(stringMonoid.apply("Hello, ", "World"), equalTo(__()));
    }

    @Test
    public void identityIsIdentityForBothSides() {
        Monoid<Integer> addition = monoid(Integer::sum, 0);
        Integer         freebie  = __();

        assertThat(addition.apply(freebie, addition.identity()), equalTo(freebie));
        assertThat(addition.apply(addition.identity(), freebie), equalTo(freebie));

        Iterable<Integer>         newMonoidIdentity = emptyList();
        Monoid<Iterable<Integer>> newMonoid         = monoid(concat(), newMonoidIdentity);
        Iterable<Integer>         param             = asList(1, 2, 3);

        assertThat(newMonoid.apply(param, newMonoid.identity()), contains(1, 2, 3));
        assertThat(newMonoid.apply(newMonoid.identity(), param), contains(1, 2, 3));

    }

    @Test
    public void monoidsCanReduce() {
        Monoid<Integer> integerMonoid = monoid(Integer::sum, 0);
        assertThat(integerMonoid.foldLeft(1, asList(1, 2, 3)), equalTo(__()));
        assertThat(integerMonoid.foldLeft(integerMonoid.identity(), asList(1, 2, 3)), equalTo(__()));
        assertThat(integerMonoid.reduceLeft(asList(1, 2, 3)), equalTo(__()));

        Monoid<String> stringMonoid = monoid((x, y) -> x + y, "");
        assertThat(stringMonoid.reduceRight(asList("foo", "bar", "baz")), equalTo(__()));
    }

    @Test
    public void computationOfIdentityValueCanBeDeferred() {
        Monoid<Integer> integerMonoid = Monoid.<Integer>monoid((x, y) -> x * y, () -> 1);
        assertThat(integerMonoid.identity(), equalTo(__()));
    }

    @Test
    public void youCanMapWhileYouFold() {
        assertThat(monoid(Integer::sum, 0).foldMap(String::length, asList("Hello", "World", "!")),
                   equalTo(__()));
        assertThat(monoid((a, b) -> a + b, "").foldMap(id(), asList("Hello", "World", "!")),
                   equalTo(__()));
    }
}
