package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.traversable.LambdaIterable;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Repeat.repeat;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Sequence.sequence;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambdakoans.Koans.__;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static testsupport.matchers.IOMatcher.yieldsValue;
import static testsupport.matchers.IterableMatcher.iterates;
import static testsupport.matchers.LeftMatcher.isLeftThat;
import static testsupport.matchers.RightMatcher.isRightThat;

public class AboutTraversables {

    @Test
    public void traversingAMaybeWithAnActionThatReturnsAnEither() {
        // first, let's just gain some intuition
        Fn1<Integer, Boolean> isEven = x -> x % 2 == 0;
        Fn1<Integer, Either<String, Boolean>> isEvenWithNegativesDisallowed =
            x -> x >= 0
                ? right(isEven.apply(x))
                : left("no negatives allowed");

        assertThat(isEvenWithNegativesDisallowed.apply(0), equalTo(__));
        assertThat(isEvenWithNegativesDisallowed.apply(1), equalTo(__));
        assertThat(isEvenWithNegativesDisallowed.apply(-1), equalTo(__));

        // what if instead of an Integer, we had a Maybe<Integer>?
        assertThat(just(0).fmap(isEvenWithNegativesDisallowed), equalTo(__));
        assertThat(just(1).fmap(isEvenWithNegativesDisallowed), equalTo(__));
        assertThat(just(-1).fmap(isEvenWithNegativesDisallowed), equalTo(__));
        assertThat(Maybe.<Integer>nothing().fmap(isEvenWithNegativesDisallowed), equalTo(__));

        // What if we wanted the Either back without yet dealing with the Maybe?
        // What are some options for our types?
        //
        // Maybe<Either<String, Boolean>> - Nope, can't get the Either without dealing with the Maybe
        // Either<String, Boolean> - Closer, but we can't guarantee a String or Boolean without dealing with the Maybe
        // Either<String, Maybe<Boolean>> - Better! Know immediately if it's left or right, deal with Maybe later.

        // First, we need to know how to lift a nothing Maybe<Integer> into an Either<String, Maybe<Integer>>:
        Fn1<Maybe<Boolean>, Either<String, Maybe<Boolean>>> liftNothing = Either::right;

        // Now we can get Either<String, Maybe<Boolean>> via Maybe's Traversable instance
        Either<String, Maybe<Boolean>> traverseJust0 = just(0).traverse(isEvenWithNegativesDisallowed, liftNothing);

        // Now, pause! Try to do this without printing traverseJust0; *reason* through the implications of the types.
        // What makes value Either left vs. right?
        // What makes the Maybe just vs. nothing?
        // What makes the Boolean true vs.false?
        //
        // Now, given what you know about the Either, Maybe, and Boolean, what *must* the value be?
        assertThat(traverseJust0, equalTo(__));

        Either<String, Maybe<Boolean>> traverseJust1 = just(1).traverse(isEvenWithNegativesDisallowed, liftNothing);
        // Same exercise, this time starting with just(1)...
        assertThat(traverseJust1, equalTo(__));

        Either<String, Maybe<Boolean>> traverseJustNeg1 = just(-1).traverse(isEvenWithNegativesDisallowed, liftNothing);
        // Once more for good measure, this time starting with just(-1)...
        assertThat(traverseJustNeg1, equalTo(__));

        Either<String, Maybe<Boolean>> traverseNothing = Maybe.<Integer>nothing()
            .traverse(isEvenWithNegativesDisallowed, liftNothing);
        // And finally, the nothing case...
        assertThat(traverseNothing, equalTo(__));

        // Voila! Now, if only this worked for more than just Maybe and Either...(proceed to next koan)
    }

    @Test
    public void traversingAnyTraversableWithAnActionThatReturnsAnEither() {
        // Our old, familiar friend
        Fn1<Integer, Either<String, Boolean>> isEvenWithNegativesDisallowed = x -> x >= 0
            ? right(x % 2 == 0)
            : left("no negatives allowed");

        // We already know this works for Maybe...what about tuples?
        Fn1<Tuple2<Character, Boolean>, Either<String, Tuple2<Character, Boolean>>> wrapTupleWithEither = Either::right;

        Either<String, Tuple2<Character, Boolean>> traverse0SideOfTuple =
            tuple('a', 0).traverse(isEvenWithNegativesDisallowed, wrapTupleWithEither);
        assertThat(traverse0SideOfTuple, equalTo(__));

        Either<String, Tuple2<Character, Boolean>> traverse1SideOfTuple =
            tuple('b', 1).traverse(isEvenWithNegativesDisallowed, wrapTupleWithEither);
        assertThat(traverse1SideOfTuple, equalTo(__));

        Either<String, Tuple2<Character, Boolean>> traverseNeg1SideOfTuple =
            tuple('c', -1).traverse(isEvenWithNegativesDisallowed, wrapTupleWithEither);
        assertThat(traverseNeg1SideOfTuple, equalTo(__));

        // What about Either itself?
        Fn1<Either<Byte, Boolean>, Either<String, Either<Byte, Boolean>>> wrapEitherWithEither = Either::right;

        Either<String, Either<Byte, Boolean>> traverseRight0 = Either.<Byte, Integer>right(0)
            .traverse(isEvenWithNegativesDisallowed, wrapEitherWithEither);
        assertThat(traverseRight0, equalTo(__));

        Either<String, Either<Byte, Boolean>> traverseRight1 = Either.<Byte, Integer>right(1)
            .traverse(isEvenWithNegativesDisallowed, wrapEitherWithEither);
        assertThat(traverseRight1, equalTo(__));

        Either<String, Either<Byte, Boolean>> traverseRightNeg1 = Either.<Byte, Integer>right(-1)
            .traverse(isEvenWithNegativesDisallowed, wrapEitherWithEither);
        assertThat(traverseRightNeg1, equalTo(__));

        Either<String, Either<Byte, Boolean>> traverseLeftNeg7 = Either.<Byte, Integer>left((byte) -7)
            .traverse(isEvenWithNegativesDisallowed, wrapEitherWithEither);
        assertThat(traverseLeftNeg7, equalTo(__));

        // So we can traverse any Traversable into an Either; great, but that seems limited....(proceed to next koan)
    }

    @Test
    public void traversingAnyTraversableWithAnActionThatReturnsAnyApplicative() {
        // Our old friend with a new twist
        Fn1<Integer, Maybe<Boolean>> isEvenWithNegativesDisallowed = x -> x >= 0 ? just(x % 2 == 0) : nothing();

        Fn1<Tuple2<Character, Boolean>, Maybe<Tuple2<Character, Boolean>>> wrapTupleWithMaybe = Maybe::just;

        Maybe<Tuple2<Character, Boolean>> traverse0SideOfTuple =
            tuple('a', 0).traverse(isEvenWithNegativesDisallowed, wrapTupleWithMaybe);
        assertThat(traverse0SideOfTuple, equalTo(__));

        Fn1<Either<Byte, Boolean>, Maybe<Either<Byte, Boolean>>> wrapEitherWithMaybe = Maybe::just;

        Maybe<Either<Byte, Boolean>> traverseRight0 = Either.<Byte, Integer>right(0)
            .traverse(isEvenWithNegativesDisallowed, wrapEitherWithMaybe);
        assertThat(traverseRight0, equalTo(__));
    }

    @Test
    public void sometimesOurTraversableAlreadyWrapsAnApplicativeAndWeJustWantToInvertTheTypes() {
        // all we care about for the outer type is that it's Traversable (even if it's also Functor, etc.)
        Maybe<
            // all we care about for the inner type is that it's Applicative (even if it's also Traversable, etc.)
            Either<String, Integer>
            > maybeEitherStringInt = just(right(1));

        // What if our traversing function is just the identity function?
        // Maybe<A> is traversable over A...
        // Either<L, R> is applicative over R...
        Either<String, Maybe<Integer>> eitherStringMaybeInt = maybeEitherStringInt.traverse(id(), Either::right); // !!!
        assertThat(eitherStringMaybeInt, equalTo(__));

        // what sorcery is this!? Let's do it again.
        Tuple2<Character, Maybe<Integer>> charAndMaybeInt = tuple('a', just(1));
        Maybe<Tuple2<Character, Integer>> maybeCharAndInt = charAndMaybeInt.traverse(id(), Maybe::just);
        assertThat(maybeCharAndInt, equalTo(__));

        // Wanting to flip a Traversable<Applicative<A>> -> Applicative<Traversable<A>> is such a common scenario,
        // it has a special shortcut
        Either<String, Maybe<Integer>> alsoEitherStringMaybeInt = sequence(maybeEitherStringInt, Either::right);
        assertThat(alsoEitherStringMaybeInt, equalTo(__));

        Maybe<Tuple2<Character, Integer>> alsoMaybeCharAndInt = sequence(charAndMaybeInt, Maybe::just);
        assertThat(alsoMaybeCharAndInt, equalTo(__));

        // So far we've been traversing 0 or 1 values; if only we could traverse more....(proceed to next koan)
    }

    @Test
    public void lambdaIterableIsAlsoTraversable() {
        Fn1<Integer, Either<String, Boolean>> isEvenWithNegativesDisallowed = x -> x >= 0
            ? right(x % 2 == 0)
            : left("no negatives allowed");

        LambdaIterable<Integer> oneTwoThree = LambdaIterable.wrap(asList(1, 2, 3));
        Either<String, LambdaIterable<Boolean>> errorOrBooleanResults =
            oneTwoThree.traverse(isEvenWithNegativesDisallowed, Either::right);
        assertThat(errorOrBooleanResults.fmap(LambdaIterable::unwrap),
                   isRightThat(iterates(/* ??? */)));

        LambdaIterable<Integer> negatives = LambdaIterable.wrap(asList(-1, -2, -3));
        Either<String, LambdaIterable<Boolean>> errorOrEvenResults =
            negatives.traverse(isEvenWithNegativesDisallowed, Either::right);
        assertThat(errorOrEvenResults.fmap(LambdaIterable::unwrap),
                   isLeftThat(equalTo(__())));

        // once we're a left, we can never again zip to a right, so we might as well short-circuit...
        LambdaIterable<Integer> infiniteNegatives = LambdaIterable.wrap(repeat(-1));
        Either<String, LambdaIterable<Boolean>> firstErrorOrInfiniteBooleans =
            infiniteNegatives.traverse(isEvenWithNegativesDisallowed, Either::right);
        assertThat(firstErrorOrInfiniteBooleans.fmap(LambdaIterable::unwrap),
                   isLeftThat(equalTo(__())));
    }

    @Test
    public void traversingIntoAnIOIsAPrevalentPattern() {
        List<String> greetings = new LinkedList<>();

        Fn1<String, IO<Integer>> sayHelloAndReturnNameLength = name -> io(() -> {
            greetings.add(format("Hello, %s!", name));
            return name.length();
        });

        IO<Maybe<Integer>> traverseJustAliceBrady = just("Alice Brady").traverse(sayHelloAndReturnNameLength, IO::io);
        assertThat(traverseJustAliceBrady,
                   yieldsValue(equalTo(__)));

        IO<Maybe<Integer>> traverseNothing = Maybe.<String>nothing().traverse(sayHelloAndReturnNameLength, IO::io);
        assertThat(traverseNothing,
                   yieldsValue(equalTo(__)));

        assertThat(greetings,
                   iterates(/* ??? */));
    }
}
