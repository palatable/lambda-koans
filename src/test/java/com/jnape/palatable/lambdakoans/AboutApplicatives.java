package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.builtin.fn3.LiftA2;
import com.jnape.palatable.lambda.functions.builtin.fn4.LiftA3;
import com.jnape.palatable.lambda.functor.builtin.Lazy;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.traversable.LambdaIterable;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
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
import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

public class AboutApplicatives {

    @Test
    public void applicativesZipEmbeddedValuesWithEmbeddedFunctions() {
        Fn1<Integer, Integer> inc = x -> x + 1;

        assertThat(just(9).zip(just(inc)), equalTo(just(10)));
        assertThat(just(15).zip(just(inc)), equalTo(just(__)));
        assertThat(just(7).zip(nothing()), equalTo(__));
        // Explicit types help the compiler
        assertThat(Maybe.<Integer>nothing().zip(just(inc)), equalTo(__));

        Fn1<? super String, ? extends Integer> getStringLength = String::length;
        assertThat(right("Hello").zip(right(getStringLength)), equalTo(__));
        assertThat(right("World!").zip(right(getStringLength)), equalTo(__));
        assertThat(Either.<String, String>left("whoops").zip(right(getStringLength)), equalTo(left(__)));

        // Moving on to LambdaIterables, where "zipping" is more obvious
        LambdaIterable<Integer> oneThroughThree = LambdaIterable.wrap(asList(1, 2, 3));
        LambdaIterable<Fn1<? super Integer, ? extends Integer>> wrappedInc = LambdaIterable.wrap(asList(inc));
        assertThat(oneThroughThree.zip(wrappedInc).unwrap(), iterates(2, 3, 4));

        Fn1<Integer, Integer> dec = x -> x - 1;
        LambdaIterable<Fn1<? super Integer, ? extends Integer>> twoFunctions = LambdaIterable.wrap(asList(inc, dec));
        assertThat(oneThroughThree.zip(twoFunctions).unwrap(), iterates(2, 0, 3, 1, 4, 2));

        Fn1<Integer, Integer> times3 = x -> x * 3;
        LambdaIterable<Fn1<? super Integer, ? extends Integer>> allFunctions = LambdaIterable.wrap(asList(inc, dec, times3));
        assertThat(oneThroughThree.zip(allFunctions).unwrap(), iterates(__()));
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
        assertThat(showDivision.apply(10), equalTo(__));


        Fn1<String, Integer> findStart = s -> s.indexOf('j');
        Fn1<String, Integer> findEnd = s -> s.indexOf(' ');
        Fn3<String, Integer, Integer, String> cutString = String::substring;

        Fn1<String, String> transformAndCut = LiftA3.liftA3(cutString, toUpper, findStart, findEnd);
        assertThat(transformAndCut.apply("hellojava world"), equalTo(__));
    }

    @Test
    public void lazyApplicatives() {
        // Zipping LambdaIterables is lazy because LambdaIterables are lazy
        LambdaIterable<Integer> infiniteOnes = LambdaIterable.wrap(repeat(1));
        Fn1<Integer, Integer> inc = x -> x + 1;

        LambdaIterable<Fn1<? super Integer, ? extends Integer>> wrappedInc = LambdaIterable.wrap(asList(inc));
        LambdaIterable<Integer> zippedOnes = infiniteOnes.zip(wrappedInc);
        assertThat(take(3, zippedOnes.unwrap()), iterates(__()));

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

        // ...then apply it with lazyZip.
        Maybe<Integer> nothing = nothing();
        // Note: unlike LambdaIterables, the Maybe inside is not itself lazy
        Lazy<Maybe<String>> lazyNothingToString = nothing.lazyZip(lazyGetToString);

        assertThat(lazyNothingToString.value(), equalTo(__));
        assertThat(computed.get(), equalTo(__));

        // zip, however, eagerly generates a mapping function
        Maybe<String> nothingToString = nothing.zip(expensiveWayToGetMaybeToString.apply(100_000));
        assertThat(nothingToString, equalTo(__));
        assertThat(computed.get(), equalTo(__));
    }

    @Test(timeout = 6500)
    public void applicativeRepresentsParallelism() throws ExecutionException, InterruptedException {
        IO<Integer> foo = IO.io(() -> {
            Thread.sleep(2_000);
            return 14;
        });

        IO<Integer> bar = IO.io(() -> {
            Thread.sleep(2_000);
            return 28;
        });

        IO<Integer> applicativeInIo = LiftA2.liftA2(Integer::sum, foo, bar);

        long singleThreadStart = System.currentTimeMillis();

        applicativeInIo
                .flatMap(result -> IO.io(() -> assertThat(result, equalTo(__))))
                .unsafePerformIO();

        System.out.printf("Single thread execution took %d seconds%n", (System.currentTimeMillis() - singleThreadStart) / 1000);

        // If we have multiple threads available, function evaluation is done in parallel
        long multipleThreadStart = System.currentTimeMillis();

        applicativeInIo
                .flatMap(result -> IO.io(() -> assertThat(result, equalTo(__))))
                // How many threads should we use?
                .unsafePerformAsyncIO(Executors.newFixedThreadPool(__()))
                .get();

        System.out.printf("Multiple thread execution took %d seconds%n", (System.currentTimeMillis() - multipleThreadStart) / 1000);
    }
}