package com.jnape.palatable.lambdakoans;

public final class Koans {

    public static Object __ = new Object() {
        @Override
        public String toString() {
            return "__";
        }
    };

    public static <A> A __(String message) {
        throw new AssertionError(message);
    }

    public static <A> A __() {
        return __("Fill me in");
    }
}
