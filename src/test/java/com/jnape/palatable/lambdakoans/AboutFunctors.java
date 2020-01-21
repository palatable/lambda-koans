package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.traversable.LambdaIterable;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Repeat.repeat;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

public class AboutFunctors {

    @Test
    public void functorsCanMapTheirParameter() {
        String shrug     = "¯\\_(ツ)_/¯";
        String theAnswer = "The answer to life, the universe, and everything";
        Fn1<Functor<Integer, ?>, Functor<String, ?>> play = fInt -> {
            Fn1<Integer, String> describeInt = value -> value == 42 ? theAnswer : shrug;
            return __("fInt.???(describeInt)");
        };

        // Maybe is a functor
        assertThat(play.apply(just(1)), equalTo(just(shrug)));
        assertThat(play.apply(just(42)), equalTo(just(theAnswer)));
        assertThat(play.apply(nothing()), equalTo(nothing()));

        // Either is also a functor
        assertThat(play.apply(right(1)), equalTo(right(shrug)));
        assertThat(play.apply(right(42)), equalTo(right(theAnswer)));
        assertThat(play.apply(left(":(")), equalTo(left(":(")));
    }

    @Test
    public void lambdaIterableCombinesIterableWithFunctor() {
        Fn1<Integer, Integer> inc = x -> x + 1;
        assertThat(LambdaIterable.wrap(asList(1, 2, 3)) // LambdaIterable wraps Iterable in a Functor instance
                       .fmap(__())
                       .unwrap(), // then we unwrap it again to get back the fmap'ed Iterable
                   iterates(__(), __(), __()));

        // empty Iterables can also be mapped...
        assertThat(LambdaIterable.<Integer>empty()
                       .fmap(inc)
                       .unwrap(),
                   iterates());

        // even infinite Iterables can be fmap'ed thanks to laziness...
        Iterable<Integer> infiniteValues = LambdaIterable.wrap(repeat(1))
            .fmap(inc)
            .unwrap();

        assertThat(take(3, infiniteValues), // ...just remember to constrain it before iteration!
                   iterates(__(), __(), __()));
    }

    @Test
    public void fn1sAreAlsoFunctorsOverTheirOutputType() {
        Fn1<String, Integer>  length = String::length;
        Fn1<Integer, Integer> inc    = x -> x + 1;

        Fn1<String, Integer> lengthThenInc = length.fmap(inc); // fmap for Fn1 is just left-to-right composition!
        assertThat(lengthThenInc.apply("13 characters"), __());
    }
}
