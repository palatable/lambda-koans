package com.jnape.palatable.lambdakoans;

import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.functions.Fn0;

import static com.jnape.palatable.lambda.monoid.builtin.And.and;
import static java.util.Arrays.asList;

public final class Koans {

    @SuppressWarnings("serial")
    static final class StudyRequest extends AssertionError {
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static void learn(String message, Fn0<Boolean>... assertions) {
        if (!and().foldMap(assertion -> Try.trying(assertion)
                               .catching(StudyRequest.class, mr -> false)
                               .orThrow(),
                           asList(assertions)))
            throw new AssertionError("Reflect on <" + message + ">");
    }

    public static Object __ = new Object() {
        @Override
        public String toString() {
            return "__";
        }
    };

    public static <A> A __() {
        throw new StudyRequest();
    }

    public static <A, B> B __(A a) {
        throw new StudyRequest();
    }

    public static <A, B, C> C __(A a, B b) {
        throw new StudyRequest();
    }

    public static <A, B, C, D> D __(A a, B b, C c) {
        throw new StudyRequest();
    }
}
