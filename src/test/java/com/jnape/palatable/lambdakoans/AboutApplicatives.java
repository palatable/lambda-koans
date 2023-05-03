package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.builtin.fn3.LiftA2;
import com.jnape.palatable.lambda.functions.builtin.fn4.LiftA3;
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.builtin.Lazy;
import com.jnape.palatable.lambda.traversable.LambdaIterable;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.Fn1.fn1;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Repeat.repeat;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functor.builtin.Lazy.lazy;
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

    @Test
    public void lazyApplicatives() {
        // Zipping LambdaIterables is lazy because LambdaIterables are lazy
        LambdaIterable<Integer> infiniteOnes = LambdaIterable.wrap(repeat(1));
        Fn1<Integer, Integer> inc = x -> x + 1;

        LambdaIterable<Fn1<? super Integer, ? extends Integer>> wrappedInc = LambdaIterable.wrap(asList(inc));
        LambdaIterable<Integer> zippedOnes = infiniteOnes.zip(wrappedInc);
        assertThat(take(3, zippedOnes.unwrap()), iterates(2, 2, 2));

        // We might lazily get a mapping function...
        AtomicInteger computed = new AtomicInteger(0);

        Fn1<Integer, Maybe<Fn1<? super Integer, ? extends String>>> expensiveWayToGetMaybeToString = fn1((Integer n) -> {
            for (int i = 0; i < n; i++) {
                computed.incrementAndGet();
            }
            return just(Object::toString);
        });

        Lazy<Maybe<Fn1<? super Integer, ? extends String>>> lazyGetToString = lazy(() ->
                expensiveWayToGetMaybeToString.apply(100_000_000));

        // ...then apply it with lazyZip
        Maybe<Integer> nothing = nothing();
        Lazy<Maybe<String>> lazyNothingToString = nothing.lazyZip(lazyGetToString);

        assertThat(lazyNothingToString.value(), equalTo(nothing()));
        assertThat(computed.get(), equalTo(0));

        // zip, however, we've eagerly generated a mapping function
        Maybe<String> nothingToString = nothing.zip(expensiveWayToGetMaybeToString.apply(100_000));
        assertThat(nothingToString, equalTo(nothing()));
        assertThat(computed.get(), equalTo(100_000));
    }

    @Test
    public void functionIsApplicative() {
        Fn1<String, Integer> strLen = String::length;
        Fn1<String, String> toUpper = String::toUpperCase;

        // Result of unary function calls are passed to mapping function as arguments
        Fn1<String, Tuple2<Integer, String>> lengthAndUppercase = LiftA2.liftA2(Tuple2::tuple, strLen, toUpper);
        assertThat(lengthAndUppercase.apply("hello world"), equalTo(tuple(11, "HELLO WORLD")));

        Fn1<Integer, Integer> mod3 = i -> i % 3;
        Fn1<Integer, Integer> div3 = i -> i / 3;

        Fn1<Integer, String> showDivision = LiftA2.liftA2((divided, remainder) -> String.format("%d * 3 + %d", divided, remainder), div3, mod3);
        assertThat(showDivision.apply(10), equalTo("3 * 3 + 1"));


        Fn1<String, Integer> findStart = s -> s.indexOf('j');
        Fn1<String, Integer> findEnd = s -> s.indexOf(' ');
        Fn3<String, Integer, Integer, String> cutString = String::substring;

        Fn1<String, String> transformAndCut = LiftA3.liftA3(cutString, toUpper, findStart, findEnd);
        assertThat(transformAndCut.apply("hellojava world"), equalTo("JAVA"));
    }
}