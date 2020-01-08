package com.jnape.palatable.lambdakoans.maybe;

import com.jnape.palatable.lambda.adt.Maybe;
import org.junit.Test;

import static com.jnape.palatable.lambdakoans.Koans.__;
import static com.jnape.palatable.lambdakoans.Koans.learn;

public class MaybeKoans {

    @Test
    public void maybeIsTheOnlyNullCheckInYourCodebase() {
        learn("Maybe.maybe is the only null check you need",
              () -> __(null).equals(Maybe.nothing()),
              () -> __(1).equals(Maybe.just(1)));

        learn("Maybe.orElse chooses the value wrapped in Maybe.just, or the given value if nothing",
              () -> Maybe.just(1).orElse(-1).equals(__()),
              () -> Maybe.nothing().orElse(-1).equals(__()));
    }
}
