package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functor.builtin.Lazy;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.functions.builtin.fn2.LTE.lte;
import static com.jnape.palatable.lambda.functions.builtin.fn3.Times.times;
import static com.jnape.palatable.lambda.functor.builtin.Lazy.lazy;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AboutLazy {

    @Test
    public void lazyValuesAreNotComputedUntilForced() {
        AtomicInteger computed = new AtomicInteger(0);

        Lazy<Integer> lazyInt = lazy(() -> {
            computed.incrementAndGet();
            return 42;
        });

        assertThat(computed.get(), equalTo(__));

        assertThat(lazyInt.value(), equalTo(__));
        assertThat(computed.get(), equalTo(__));

        assertThat(lazyInt.value(), equalTo(__));
        assertThat(computed.get(), equalTo(__));
    }

    @Test
    public void regularValuesCanBeLiftedIntoLazy() {
        assertThat(lazy(1).value(), equalTo(__));
    }

    @Test
    public void lazyIsStackSafe() {
        Lazy<Integer> hugeLazyInt = times(100_000, lazyInt -> lazyInt.fmap(x -> x + 1), lazy(0));
        assertThat(hugeLazyInt.value(), equalTo(__));
    }

    @Test
    public void traversingLazyValuesForcesThem() {
        AtomicBoolean forced = new AtomicBoolean(false);
        Maybe<Lazy<Integer>> traversed = lazy(() -> {
            forced.set(true);
            return 1;
        }).traverse(x -> just(x).filter(lte(1)), Maybe::just);
        assertThat(forced.get(), equalTo(__));
        assertThat(traversed, equalTo(__));
    }
}
