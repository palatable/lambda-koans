package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static testsupport.matchers.LeftMatcher.isLeftThat;
import static testsupport.matchers.RightMatcher.isRightThat;

public class AboutEither {

    @Test
    public void eitherCanTypeSafelyHoldAValueOfTwoDifferentTypes() {
        Either<String, Integer> leftString = left(__());
        Either<String, Integer> rightInt   = right(__());

        assertThat(leftString, isLeftThat(equalTo(__())));
        assertThat(rightInt, isRightThat(equalTo(__())));
    }

    @Test
    public void filterSendsRightValuesThatFailPredicateToTheLeft() {
        Fn1<Integer, Boolean> isEven = x -> x % 2 == 0;

        Either<String, Integer> filteredRight2 = Either.<String, Integer>right(2)
            .filter(isEven, x -> "odd: " + x);
        assertThat(filteredRight2, equalTo(__));

        Either<String, Integer> filteredRight1 = Either.<String, Integer>right(1)
            .filter(isEven, x -> "odd: " + x);
        assertThat(filteredRight1, equalTo(__));

        Either<String, Integer> filteredLeft = Either.<String, Integer>left("Already left")
            .filter(isEven, x -> "odd: " + x);
        assertThat(filteredLeft, equalTo(__));

        Either<String, Integer> filterCanAlsoIgnoreTheRightValueInTheLeftSupplier = Either.<String, Integer>right(1)
            .filter(isEven, () -> "odd: ??? (I didn't ask)");
        assertThat(filterCanAlsoIgnoreTheRightValueInTheLeftSupplier, equalTo(__));
    }

    @Test
    public void recoverGuaranteesRightValueByMappingLeftToRight() {
        Either<String, Integer> right1        = right(1);
        Integer                 recoverRight1 = right1.recover(String::length);
        assertThat(recoverRight1, equalTo(__));

        Either<String, Integer> leftFoo        = left("foo");
        Integer                 recoverLeftFoo = leftFoo.recover(String::length);
        assertThat(recoverLeftFoo, equalTo(__));
    }

    @Test
    public void forfeitGuaranteesLeftValueByMappingRightToLeft() {
        Either<String, Integer> right1        = right(1);
        String                  forfeitRight1 = right1.forfeit(Object::toString);
        assertThat(forfeitRight1, equalTo(__));

        Either<String, Integer> leftFoo        = left("foo");
        String                  forfeitLeftFoo = leftFoo.forfeit(Object::toString);
        assertThat(forfeitLeftFoo, equalTo(__));
    }

    @Test
    public void orGuaranteesRightValueByRequiringDefaultRight() {
        Either<String, Integer> right1   = right(1);
        Integer                 orRight1 = right1.or(-1);
        assertThat(orRight1, equalTo(__));

        Either<String, Integer> leftFoo   = left("foo");
        Integer                 orLeftFoo = leftFoo.or(-1);
        assertThat(orLeftFoo, equalTo(__));
    }

    @Test
    public void orThrowReturnsRightOrThrowsUsingLeftToCraftException() {
        Either<String, Integer> right1        = right(1);
        Integer                 orThrowRight1 = right1.orThrow(IllegalStateException::new);
        assertThat(orThrowRight1, equalTo(__));

        try {
            Either<String, Integer> leftFoo = left("foo");
            leftFoo.orThrow(IllegalStateException::new);
            fail("Expected an exception to have been thrown.");
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), equalTo(__));
        }
    }

    @Test
    public void invertFlipsTheLeftAndRightSide() {
        Either<String, Integer> right1         = right(1);
        Either<Integer, String> invertedRight1 = right1.invert();
        assertThat(invertedRight1, equalTo(__));

        Either<String, Integer> leftFoo         = left("foo");
        Either<Integer, String> invertedLeftFoo = leftFoo.invert();
        assertThat(invertedLeftFoo, equalTo(__));
    }

    @Test
    public void mergeCollapsesMultipleTypeCompatibleEithersIntoASingleEither() {
        Fn2<String, String, String>    mergeLefts  = (str1, str2) -> str1 + str2;
        Fn2<Integer, Integer, Integer> mergeRights = Integer::sum;

        Either<String, Integer> right1  = right(1);
        Either<String, Integer> right2  = right(2);
        Either<String, Integer> leftFoo = left("foo");

        Either<String, Integer> mergedRight1AndRight2 = right1.merge(mergeLefts, mergeRights, right2);
        assertThat(mergedRight1AndRight2, equalTo(__));

        Either<String, Integer> mergedRight1AndLeftFoo = right1.merge(mergeLefts, mergeRights, leftFoo);
        assertThat(mergedRight1AndLeftFoo, equalTo(__));

        Either<String, Integer> mergeAllThree = right1.merge(mergeLefts, mergeRights, right2, leftFoo);
        assertThat(mergeAllThree, equalTo(__));
    }

    @Test
    public void tryingExecutesAPotentiallyNonTotalFn0AndShuntsAnyThrownThrowableToTheLeft() {
        Either<Throwable, Integer> tryingSuccess = Either.trying(() -> 1);
        assertThat(tryingSuccess, equalTo(__));

        IllegalStateException      exception    = new IllegalStateException("kaboom");
        Either<Throwable, Integer> tryingThrown = Either.trying(() -> { throw exception; });
        assertThat(tryingThrown, equalTo(__));

        Either<String, Integer> tryingCanAlsoMapThrowableImmediately = Either.trying(() -> { throw exception; },
                                                                                     Throwable::getMessage);
        assertThat(tryingCanAlsoMapThrowableImmediately, equalTo(__));
    }

    @Test
    public void fromMaybeConvertsMaybeToEither() {
        Fn0<String> inCaseItWasNothing = () -> "it was nothing";

        Maybe<Integer>          just1     = just(1);
        Either<String, Integer> fromJust1 = Either.fromMaybe(just1, inCaseItWasNothing);
        assertThat(fromJust1, equalTo(__));

        Maybe<Integer>          nothing     = nothing();
        Either<String, Integer> fromNothing = Either.fromMaybe(nothing, inCaseItWasNothing);
        assertThat(fromNothing, equalTo(__));
    }

    @Test
    public void toMaybeConvertsEitherToMaybe() {
        Either<String, Integer> right1        = right(1);
        Maybe<Integer>          right1ToMaybe = right1.toMaybe();
        assertThat(right1ToMaybe, equalTo(__));

        Either<String, Integer> leftMissing        = left("missing");
        Maybe<Integer>          leftMissingToMaybe = leftMissing.toMaybe();
        assertThat(leftMissingToMaybe, equalTo(__));
    }
}
