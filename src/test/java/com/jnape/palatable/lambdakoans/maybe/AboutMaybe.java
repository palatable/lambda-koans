package com.jnape.palatable.lambdakoans.maybe;

import com.jnape.palatable.lambda.adt.Maybe;
import org.junit.Test;

import java.util.Optional;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.fromEither;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.maybe;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AboutMaybe {

    @Test
    public void maybeMethodIsTheOnlyNullCheckYouNeed() {
        assertThat(maybe(null), equalTo(__));
        assertThat(maybe(1), equalTo(__));
    }

    @Test
    public void orElseChoosesTheValueWrappedInJustOrTheGivenValueIfNothing() {
        assertThat(just(1).orElse(-1), equalTo(__));
        assertThat(Maybe.<Integer>nothing().orElse(-1), equalTo(__));
    }

    @Test
    public void orElseGetIsLikeOrElseButWithDeferredComputationInNothingCase() {
        assertThat(just(1).orElseGet(() -> -1), equalTo(__));
        assertThat(Maybe.<Integer>nothing().orElseGet(() -> -1), equalTo(__));
        assertThat(just(1).orElseGet(() -> { throw new IllegalStateException(); }), equalTo(__));
    }

    @Test
    public void orElseThrowWillThrowIfThereIsNoValue() {
        assertThat(just(1).orElseThrow(IllegalStateException::new), equalTo(__));

        try {
            Maybe.<Integer>nothing().orElseThrow(() -> new IllegalStateException("expected"));
            fail("Expected exception to have been thrown, but wasn't");
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), equalTo(__));
        }
    }

    @Test
    public void filterOnlyRetainsJustValuesThatSatisfyPredicate() {
        assertThat(just(1).filter(x -> x > 0), equalTo(__));
        assertThat(just(1).filter(x -> x > 1), equalTo(__));
        assertThat(Maybe.<Integer>nothing().filter(x -> true), equalTo(__));
    }

    @Test
    public void toOptionalConvertsMaybeToJavaOptional() {
        assertThat(just(1).toOptional(), equalTo(__));
        assertThat(Maybe.<Integer>nothing().toOptional(), equalTo(__));
    }

    @Test
    public void fromOptionalConvertsJavaOptionalToMaybe() {
        assertThat(Maybe.fromOptional(Optional.of(1)), equalTo(__));
        assertThat(Maybe.fromOptional(Optional.<Integer>empty()), equalTo(__));
    }

    @Test
    public void toEitherConvertsMaybeToEitherGivenLeftSideValue() {
        assertThat(just(1).toEither(() -> "no value"), equalTo(right(__)));
        assertThat(Maybe.<Integer>nothing().toEither(() -> "no value"), equalTo(left(__)));
    }

    @Test
    public void fromEitherConvertsEitherToMaybeByForgettingTheLeftValue() {
        assertThat(Maybe.fromEither(right(1)), equalTo(__));
        assertThat(Maybe.<Integer>fromEither(left("no value")), equalTo(__));

        assertThat(Maybe.fromOptional(Optional.of(1)), equalTo(__));
        assertThat(Maybe.fromOptional(Optional.<Integer>empty()), equalTo(__));
    }
}
