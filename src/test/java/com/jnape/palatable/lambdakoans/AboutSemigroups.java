package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.semigroup.Semigroup;
import org.junit.Test;

import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class AboutSemigroups {

    @Test
    public void semigroupsCombineValues() {
        // A semigroup is a binary associative operation over a set.
        // This means it takes the form of Fn2<A, A, A> and is associative (more on this later)

        Semigroup<Integer> addition = Integer::sum;  // Where A is Integer
        assertThat(addition.apply(1, 2), equalTo(__));

        Semigroup<String> concatenation = (a, b) -> a + b;
        assertThat(concatenation.apply("Hello ", "World"), equalTo(__));

        Semigroup<Integer> multiplication = (a, b) -> a * b; // Semigroups are not necessarily unique over their type
        assertThat(multiplication.apply(3, 5), equalTo(__));
    }

    @Test
    public void associativity() {
        // Semigroups are associative
        Semigroup<String> stringSG = (x, y) -> __("Choose an associative operation over x and y");

        String a = "Hello";
        String b = " ";
        String c = "World";

        assertEquals(stringSG.apply(a, stringSG.apply(b, c)),
                     stringSG.apply(stringSG.apply(a, b), c));

        // Let's practice with Integers

        Integer d = 1;
        Integer e = 2;
        Integer f = 3;

        Semigroup<Integer> integerSG = (x, y) -> __("Choose an associative operation over Integers");
        assertEquals(integerSG.apply(d, integerSG.apply(e, f)),
                     integerSG.apply(integerSG.apply(d, e), f));

        // Convince yourself subtraction isn't associative
        Fn2<Integer, Integer, Integer> subtraction = (x, y) -> x - y;

        Integer g = __("Choose your own value to demonstrate");
        Integer h = __("Choose your own value to demonstrate");
        Integer i = __("Choose your own value to demonstrate");
        assertNotEquals(subtraction.apply(g, subtraction.apply(h, i)),
                        subtraction.apply(subtraction.apply(g, h), i));
    }

    @Test
    public void leftFolding() {
        Semigroup<String> stringConcatenation = (a, b) -> a + b;

        // You can also fold with semigroups
        assertThat(stringConcatenation.foldLeft("Hello", asList(" ", "World", "!")), equalTo(__));
        assertThat(((Semigroup<Integer>) Integer::sum).foldLeft(1, asList(2, 3, 4, 5)), equalTo(__));
        assertThat(((Semigroup<Integer>) (a, b) -> a * b).foldLeft(1, asList(2, 3, 4, 5)), equalTo(__));
    }

    @Test
    public void rightFolding() {
        assertThat(((Semigroup<String>) (a, b) -> a + b).foldRight("Hello", asList(" ", "World", "!")).value(),
                   equalTo(__));
        assertThat(((Semigroup<Integer>) Integer::sum).foldRight(1, asList(2, 3, 4, 5)).value(), equalTo(__));
        assertThat(((Semigroup<Integer>) (a, b) -> a * b).foldRight(1, asList(2, 3, 4, 5)).value(), equalTo(__));

    }

    @Test
    public void flipping() {
        Semigroup<String> concatenation = (x, y) -> x + y;
        Semigroup<String> flip          = concatenation.flip();
        assertThat(flip.apply("a", "b"), equalTo(__));
        assertThat(flip.foldLeft("!", asList("World", " ", "Hello")), equalTo(__));
    }
}
