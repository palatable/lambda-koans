package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.traversable.LambdaIterable;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

public class AboutApplicatives {
    @Test
    public void applicativesZipEmbeddedValuesWithEmbeddedFunctions() {
        Fn1<Integer, Integer> inc = x -> x + 1;

        assertThat(just(9).zip(just(inc)), equalTo(just(10)));
        assertThat(just(15).zip(just(inc)), equalTo(just(16)));
        // Explicit types on the nothing help the compiler
        assertThat(Maybe.<Integer>nothing().zip(just(inc)), equalTo(nothing()));

        Fn1<? super String, ? extends Integer> getStringLength = String::length;
        assertThat(right("Hello").zip(right(getStringLength)), equalTo(right(5)));
        assertThat(right("World!").zip(right(getStringLength)), equalTo(right(6)));
        // Explicit types on the nothing help the compiler
        assertThat(Either.<String, String>left("whoops").zip(right(getStringLength)), equalTo(left("whoops")));

        // Moving on to LambdaIterables, where "zipping" is more obvious
        LambdaIterable<Integer> oneThroughThree = LambdaIterable.wrap(asList(1, 2, 3));
        Applicative<Fn1<? super Integer, ? extends Integer>, LambdaIterable<?>> wrappedInc = LambdaIterable.wrap(asList(inc));
        assertThat(oneThroughThree.zip(wrappedInc).unwrap(), iterates(2, 3, 4));

        Fn1<Integer, Integer> dec = x -> x - 1;
        LambdaIterable<Fn1<? super Integer, ? extends Integer>> twoFunctions = LambdaIterable.wrap(asList(inc, dec));
        assertThat(oneThroughThree.zip(twoFunctions).unwrap(), iterates(2, 0, 3, 1, 4, 2));

        Fn1<Integer, Integer> times3 = x -> x * 3;
        LambdaIterable<Fn1<? super Integer, ? extends Integer>> allFunctions = LambdaIterable.wrap(asList(inc, dec, times3));
        assertThat(oneThroughThree.zip(allFunctions).unwrap(), iterates(2, 0, 3, 3, 1, 6, 4, 2, 9));
    }
}